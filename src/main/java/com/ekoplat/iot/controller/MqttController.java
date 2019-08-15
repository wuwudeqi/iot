package com.ekoplat.iot.controller;

import com.ekoplat.iot.config.MqttReceiveConfig;
import com.ekoplat.iot.mqtt.MqttGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 16:11 2019-07-23
 **/
@RestController
@RequestMapping("/mqtt")
@Slf4j
public class MqttController {

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private MqttReceiveConfig mqttReceiveConfig;

    @RequestMapping("send")
    public String sendMqtt(String topic, String  msg){
        mqttGateway.sendToMqtt(topic,msg);
        log.info("【mqtt发送消息】success, topic:{} msg:{}",topic,msg);
        return "OK";
    }

    @GetMapping("addTopic")
    public String addTopic(String[] topicArr) {
        mqttReceiveConfig.addListenTopic(topicArr);
        return "ok";
    }
}