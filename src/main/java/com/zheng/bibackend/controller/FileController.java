package com.zheng.bibackend.controller;

import cn.hutool.core.io.FileUtil;

import com.zheng.bibackend.common.BaseResponse;
import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.common.ResultUtils;
import com.zheng.bibackend.constant.FileConstant;
import com.zheng.bibackend.exception.BusinessException;
import com.zheng.bibackend.model.dto.file.UploadFileRequest;
import com.zheng.bibackend.model.entity.User;
import com.zheng.bibackend.model.enums.FileUploadBizEnum;
import com.zheng.bibackend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;

/**
 * File API.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;



    /**
     * File upload.
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);

        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // upload file
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            // return address
            return ResultUtils.success(FileConstant.COS_HOST + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // delete temp file
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * Validate file.
     *
     * @param multipartFile
     * @param fileUploadBizEnum business type
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // file size
        long fileSize = multipartFile.getSize();
        // file extension
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "File size can't be larger than 1M");
            }
            if (!Arrays.asList("xlsx", "xls").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "file type is wrong");
            }
        }
    }
}
