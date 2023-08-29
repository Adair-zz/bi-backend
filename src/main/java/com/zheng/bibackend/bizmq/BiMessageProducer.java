package com.zheng.bibackend.bizmq;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;



/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/10/2023 - 14:43
 */
@Slf4j
@Component
public class BiMessageProducer {
  
  @Resource
  private RabbitTemplate rabbitTemplate;
  
  /**
   * MQ send message.
   * @param message
   */
  public void sendMessage(String message) {
    log.info("bi_queue send message");
    rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
  }
}
