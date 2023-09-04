package com.zheng.bibackend.bizmq;

import com.rabbitmq.client.Channel;

import com.zheng.bibackend.common.ErrorCode;
import com.zheng.bibackend.exception.BusinessException;
import com.zheng.bibackend.manager.OpenAiManager;
import com.zheng.bibackend.model.entity.Chart;
import com.zheng.bibackend.service.ChartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/10/2023 - 14:43
 */
@Slf4j
@Component
public class BiMessageConsumer {
  
  @Resource
  private ChartService chartService;
  
  @Resource
  private OpenAiManager openAiManager;
  
  @RabbitListener(queues = {BiMqConstant.BI_QUEUE_NAME}, ackMode = "MANUAL")
  public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
    if (StringUtils.isBlank(message)) {
      try {
        channel.basicNack(deliveryTag, false, false);
      } catch (IOException e) {
        e.printStackTrace();
      }
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Message is empty");
    }
    long chartId = Long.parseLong(message);
    Chart chart = chartService.getById(chartId);
    if (chart == null) {
      try {
        channel.basicNack(deliveryTag, false, false);
      } catch (IOException e) {
        e.printStackTrace();
      }
      throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Chart is Empty");
    }
    // set chart status to "running"
    Chart updatedChart = new Chart();
    updatedChart.setId(chart.getId());
    updatedChart.setStatus("running");
    boolean isStatusUpdated = chartService.updateById(updatedChart);
    if (!isStatusUpdated) {
      try {
        channel.basicNack(deliveryTag, false, false);
      } catch (IOException e) {
        e.printStackTrace();
      }
      handleChartUpdateError(chart.getId(), "Fail to update chart type to running");
    }
    
  
    String resultByAi = openAiManager.genResultByOpenAi(chart.getGoal(), chart.getChartType(), chart.getChartData());
    
    // Suppose we have got the result from ai
//    String resultByAi = "{\n" +
//        "  \"xAxis\": {\n" +
//        "    \"type\": \"category\",\n" +
//        "    \"data\": [\"08/01/2023\", \"08/02/2023\", \"08/03/2023\"]\n" +
//        "  },\n" +
//        "  \"yAxis\": {\n" +
//        "    \"type\": \"value\"\n" +
//        "  },\n" +
//        "  \"series\": [\n" +
//        "    {\n" +
//        "      \"type\": \"bar\",\n" +
//        "      \"data\": [10, 20, 30]\n" +
//        "    }\n" +
//        "  ]\n" +
//        "}\n" +
//        "=====\n" +
//        "Based on the data provided, we can observe a clear trend in the user numbers over the specified dates. The bar chart illustrates the growth in user numbers, with each bar representing a date.\n" +
//        "\n" +
//        "On August 1st, 2023, there were 10 users. This number increased to 20 users on August 2nd, 2023, and further rose to 30 users on August 3rd, 2023. The consistent increase in user numbers suggests a positive growth trend during this period.\n" +
//        "\n" +
//        "This data can be valuable for assessing the performance and popularity of the platform during these dates. It's recommended to continue monitoring user numbers and identifying factors that contribute to such growth. Further analysis could involve investigating any external events or marketing efforts that might have influenced these increases in user engagement.";
  
    String[] strSplits = resultByAi.split("=====");
    if (strSplits.length < 2) {
      try {
        channel.basicNack(deliveryTag, false, false);
      } catch (IOException e) {
        e.printStackTrace();
      }
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
    
    // Message Confirm
    try {
      channel.basicAck(deliveryTag, false);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
}
