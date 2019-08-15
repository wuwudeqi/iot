package com.ekoplat.iot.config;


import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import javax.servlet.MultipartConfigElement;


/**
 * mqtt send config.
 *
 * @author wuwudeqi
 * @version v1.0.0
 * @date 16:08 2019-07-23
 **/
@Configuration
@IntegrationComponentScan
public class MqttSenderConfig {

    @Value("${mqtt.server.username}")
    private String username;

    @Value("${mqtt.server.password}")
    private String password;

    @Value("${mqtt.server.address}")
    private String hostUrl;

    @Value("${mqtt.server.clientid}")
    private String clientId;

    @Value("${mqtt.server.default.topic}")
    private String defaultTopic;

    @Bean
    public MqttConnectOptions getMqttPubConnectOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
        mqttConnectOptions.setKeepAliveInterval(20);
        return mqttConnectOptions;
    }
    @Bean
    public MqttPahoClientFactory mqttPubClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttPubConnectOptions());
        return factory;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttPubOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =  new MqttPahoMessageHandler(clientId, mqttPubClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(defaultTopic);
        return messageHandler;
    }
    @Bean
    public MessageChannel mqttPubOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置文件大小限制 ,超出设置页面会抛出异常信息，
        // 这样在文件上传的地方就需要进行异常信息的处理了;
        factory.setMaxFileSize("256KB"); // KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("512KB");
        // Sets the directory location where files will be stored.
        // factory.setLocation("路径地址");
        return factory.createMultipartConfig();
    }

}