package com.ekoplat.iot.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 16:11 2019-07-23
 **/
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {
    void sendToMqtt(String data,@Header(MqttHeaders.TOPIC) String topic);
}