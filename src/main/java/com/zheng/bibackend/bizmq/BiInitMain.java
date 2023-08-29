package com.zheng.bibackend.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @Author: Zheng Zhang
 * @Description RabbitMQ initializes.
 * @Created 08/10/2023 - 14:52
 */
public class BiInitMain {
  
  public static void main(String[] args) {
    final String BI_EXCHANGE_NAME = BiMqConstant.BI_EXCHANGE_NAME;
    final String queueName = BiMqConstant.BI_QUEUE_NAME;
    try {
      ConnectionFactory connectionFactory = new ConnectionFactory();
      connectionFactory.setHost("localhost");
      
      Connection connection = connectionFactory.newConnection();
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(BI_EXCHANGE_NAME, "direct");
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
