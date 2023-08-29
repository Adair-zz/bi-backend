package com.zheng.bibackend.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zheng.bibackend.annotation.AuthCheck;
import com.zheng.bibackend.bizmq.BiMessageProducer;
import com.zheng.bibackend.common.BaseResponse;
import com.zheng.bibackend.common.DeleteRequest;
import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.common.ResultUtils;
import com.zheng.bibackend.constant.CommonConstant;
import com.zheng.bibackend.constant.UserConstant;
import com.zheng.bibackend.exception.BusinessException;
import com.zheng.bibackend.exception.ThrowUtils;
import com.zheng.bibackend.manager.OpenAiManager;
import com.zheng.bibackend.manager.RedisLimiterManager;
import com.zheng.bibackend.model.dto.chart.*;
import com.zheng.bibackend.model.entity.Chart;
import com.zheng.bibackend.model.entity.User;
import com.zheng.bibackend.model.vo.BiResponse;
import com.zheng.bibackend.service.ChartService;
import com.zheng.bibackend.service.UserService;
import com.zheng.bibackend.utils.ExcelUtils;
import com.zheng.bibackend.utils.SqlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Chart API.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;
    
    @Resource
    private OpenAiManager openAiManager;
    
    @Resource
    private RedisLimiterManager redisLimiterManager;
    
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    
    @Resource
    private BiMessageProducer biMessageProducer;

    /**
     * Create a chart.
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * Delete Chart.
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();

        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);

        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * Update chart (Only for admin).
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();

        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * Get Chart by ID.
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * Get chart list by page.
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long currentPage = chartQueryRequest.getCurrentPage();
        long size = chartQueryRequest.getPageSize();
        // restrict crawlers
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(currentPage, size), getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * User's chart list By page.
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long currentPage = chartQueryRequest.getCurrentPage();
        long size = chartQueryRequest.getPageSize();
        // Restrict crawlers
        ThrowUtils.throwIf(size > 30, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(currentPage, size), getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * Edit user info.
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // only user itSelf and admin
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }
    
    /**x
     * Get chart query wrapper.
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();
        
        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.ASCENDING_ORDER),
            sortField);
        return queryWrapper;
    }
    
    /**
     * Data upload Async MQ.
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq")
    public BaseResponse<BiResponse> genChartByAiAsyncMQ(@RequestPart("file") MultipartFile multipartFile,
                                                        GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "Goal is empty");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "Name is too long");
        
        // check file size
        final long ONE_MB = 1 * 1024 * 1024;
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "File size is large than 1MB");
        
        // check filename extension
        final List<String> acceptedFileTypeList = Arrays.asList("xlsx", "csv", "xls");
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!acceptedFileTypeList.contains(suffix), ErrorCode.PARAMS_ERROR, "File Type is not accpted");
        
        User loginUser = userService.getLoginUser(request);
        
        // rate limiting
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean isSave = chartService.save(chart);
        ThrowUtils.throwIf(!isSave, ErrorCode.SYSTEM_ERROR, "Fail to save chart");
        
        long newChartId = chart.getId();
        // MQ
        biMessageProducer.sendMessage(String.valueOf(newChartId));
        
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return ResultUtils.success(biResponse);
    }
    
    /**
     * Data upload Async.
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen/async")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile,
                                             GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "Goal is empty");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "Name is too long");
    
        // check file size
        final long ONE_MB = 1 * 1024 * 1024;
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "File size is large than 1MB");
        
        // check filename extension
        final List<String> acceptedFileTypeList = Arrays.asList("xlsx", "csv", "xls");
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!acceptedFileTypeList.contains(suffix), ErrorCode.PARAMS_ERROR, "File Type is not accepted");
    
        User loginUser = userService.getLoginUser(request);
        
        // rate limiting
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        
        String csvData = ExcelUtils.excelToCsv(multipartFile);
    
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean isSave = chartService.save(chart);
        ThrowUtils.throwIf(!isSave, ErrorCode.SYSTEM_ERROR, "Fail to save chart");
        
        CompletableFuture.runAsync(() -> {
            // set chart status to "running"
            Chart updatedChart = new Chart();
            updatedChart.setId(chart.getId());
            updatedChart.setStatus("running");
            boolean isStatusUpdated = chartService.updateById(updatedChart);
            if (!isStatusUpdated) {
                handleChartUpdateError(chart.getId(), "Fail to update chart type to running");
            }
            
//            // user input for ai
//            Map<String, String> dataMap = new HashMap<>();
//            dataMap.put("model", "gpt-3.5-turbo");
//            String userGoal = goal;
//            if (StringUtils.isNotBlank(chartType)) {
//                userGoal += "Please use" + chartType;
//            }
//            dataMap.put("message", userGoal);
//            dataMap.put("Raw Data:", csvData);
//            String resultByAi = openAiApi.genResultByOpenAi(JSONUtil.toJsonStr(dataMap));
    
    
            // Suppose we have got the result from ai
            String resultByAi = "{\n" +
                "  \"xAxis\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"data\": [\"08/01/2023\", \"08/02/2023\", \"08/03/2023\"]\n" +
                "  },\n" +
                "  \"yAxis\": {\n" +
                "    \"type\": \"value\"\n" +
                "  },\n" +
                "  \"series\": [\n" +
                "    {\n" +
                "      \"type\": \"bar\",\n" +
                "      \"data\": [10, 20, 30]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n" +
                "=====\n" +
                "Based on the data provided, we can observe a clear trend in the user numbers over the specified dates. The bar chart illustrates the growth in user numbers, with each bar representing a date.\n" +
                "\n" +
                "On August 1st, 2023, there were 10 users. This number increased to 20 users on August 2nd, 2023, and further rose to 30 users on August 3rd, 2023. The consistent increase in user numbers suggests a positive growth trend during this period.\n" +
                "\n" +
                "This data can be valuable for assessing the performance and popularity of the platform during these dates. It's recommended to continue monitoring user numbers and identifying factors that contribute to such growth. Further analysis could involve investigating any external events or marketing efforts that might have influenced these increases in user engagement.";
    
            String[] strSplits = resultByAi.split("=====");
            if (strSplits.length < 2) {
                handleChartUpdateError(chart.getId(), "Fail to generate AI chart");
            }
    
            String genChart = strSplits[0];
            String genResult = strSplits[1];
            
            // set chart status to "success"
            Chart updatedChartResult = new Chart();
            updatedChartResult.setId(chart.getId());
            updatedChartResult.setStatus("success");
            updatedChartResult.setGenChart(genChart);
            updatedChartResult.setGenResult(genResult);
            boolean isResultUpdated = chartService.updateById(updatedChartResult);
            if (!isResultUpdated) {
                handleChartUpdateError(chart.getId(), "Fail to update chart status to success");
            }
        }, threadPoolExecutor);
        
        
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }
    
    private void handleChartUpdateError(long chartId, String errorMessage) {
        Chart updateChart = new Chart();
        updateChart.setId(chartId);
        updateChart.setStatus("failed");
        updateChart.setExecMessage(errorMessage);
        boolean isChartUpdated = chartService.updateById(updateChart);
        if (!isChartUpdated) {
            log.error("Fail to update chart status: " + chartId + ", " + errorMessage);
        }
    }
    
    /**
     * Data upload.
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/gen")
    public BaseResponse<BiResponse> genChartByAi(@RequestPart("file") MultipartFile multipartFile,
                                                      GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "Goal is empty");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "Name is too long");
        
        // check file size
        final long ONE_MB = 1 * 1024 * 1024;
        long size = multipartFile.getSize();
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "File size is large than 1MB");
        
        // check filename extension
        final List<String> acceptedFileTypeList = Arrays.asList("xlsx", "csv", "xls");
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!acceptedFileTypeList.contains(suffix), ErrorCode.PARAMS_ERROR, "File Type is not accepted");
        
        User loginUser = userService.getLoginUser(request);
        
        // rate limiting
        redisLimiterManager.doRateLimit("genChartByAi_" + loginUser.getId());
        
        String csvData = ExcelUtils.excelToCsv(multipartFile);

//            // user input for AI
//            Map<String, String> dataMap = new HashMap<>();
//            dataMap.put("model", "gpt-3.5-turbo");
//            String userGoal = goal;
//            if (StringUtils.isNotBlank(chartType)) {
//                userGoal += "Please use" + chartType;
//            }
//            dataMap.put("message", userGoal);
//            dataMap.put("Raw Data:", csvData);
//            String resultByAi = openAiApi.genResultByOpenAi(JSONUtil.toJsonStr(dataMap));
    
    
        // Suppose we have got the result from ai
        String resultByAi = "{\n" +
            "  \"xAxis\": {\n" +
            "    \"type\": \"category\",\n" +
            "    \"data\": [\"08/01/2023\", \"08/02/2023\", \"08/03/2023\"]\n" +
            "  },\n" +
            "  \"yAxis\": {\n" +
            "    \"type\": \"value\"\n" +
            "  },\n" +
            "  \"series\": [\n" +
            "    {\n" +
            "      \"type\": \"bar\",\n" +
            "      \"data\": [10, 20, 30]\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "=====\n" +
            "Based on the data provided, we can observe a clear trend in the user numbers over the specified dates. The bar chart illustrates the growth in user numbers, with each bar representing a date.\n" +
            "\n" +
            "On August 1st, 2023, there were 10 users. This number increased to 20 users on August 2nd, 2023, and further rose to 30 users on August 3rd, 2023. The consistent increase in user numbers suggests a positive growth trend during this period.\n" +
            "\n" +
            "This data can be valuable for assessing the performance and popularity of the platform during these dates. It's recommended to continue monitoring user numbers and identifying factors that contribute to such growth. Further analysis could involve investigating any external events or marketing efforts that might have influenced these increases in user engagement.";
    
        String[] strSplits = resultByAi.split("=====");
        if (strSplits.length < 2) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    
        String genChart = strSplits[0];
        String genResult = strSplits[1];
    
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setUserId(loginUser.getId());
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setStatus("success");
        boolean isSave = chartService.save(chart);
        ThrowUtils.throwIf(!isSave, ErrorCode.SYSTEM_ERROR, "Fail to save chart");
        
        BiResponse biResponse = new BiResponse(genChart, genResult, chart.getId());
        return ResultUtils.success(biResponse);
    }
}
