package com.ekoplat.iot.config;


import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * mqtt receive config.
 *
 * @author wuwudeqi
 * @version v1.0.0
 * @date 16:22 2019-07-23
 **/
@Configuration
@IntegrationComponentScan
@Slf4j
public class MqttReceiveConfig {

    @Value("${mqtt.client.username}")
    private String username;

    @Value("${mqtt.client.password}")
    private String password;

    @Value("${mqtt.client.address}")
    private String hostUrl;

    @Value("${mqtt.client.clientid}")
    private String clientId;

    @Value("${mqtt.client.default.topic}")
    private String defaultTopic;

    /**
     * 连接超时
     */
    @Value("${mqtt.client.connectionTimeoutTime}")
    private int connectionTimeoutTime;


    /**
     * //设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送心跳判断客户端是否在线，但这个方法并没有重连的机制
     */
    @Value("${mqtt.client.keepAliveTime}")
    private int keepAliveTime;

    private MqttPahoMessageDrivenChannelAdapter adapter;

    /**
     * 添加监听主题Topic
     */
    public void addListenTopic(String[] topicArr) {
        if (adapter == null) {
            adapter = new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound", mqttSubClientFactory());
        }
        for (String topic : topicArr) {
            adapter.addTopic(topic, 1);
        }
    }

    /**
     * 移除监听主题
     */
    public void removeListenTopic(String[] topicArr) {
        if (adapter == null) {
            adapter = new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound", mqttSubClientFactory());
        }
        for (String topic : topicArr) {
            adapter.removeTopic(topic);
        }
    }

    /**
     *
     * @return MqttConnectOptions
     */
    @Bean
    public MqttConnectOptions getMqttSubConnectOptions() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{hostUrl});
        mqttConnectOptions.setKeepAliveInterval(keepAliveTime);
        mqttConnectOptions.setConnectionTimeout(connectionTimeoutTime);
        return mqttConnectOptions;
    }

    @Bean
    public MqttPahoClientFactory mqttSubClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(getMqttSubConnectOptions());
        return factory;
    }

    /**
     * 接受消息通道
     */
    @Bean
    public MessageChannel mqttSubInputChannel() {
        return new DirectChannel();
    }

    /**
     * 配置client,监听的topic
     */
    @Bean
    public MessageProducer SubInBound() {
        adapter = new MqttPahoMessageDrivenChannelAdapter(clientId + "_inbound", mqttSubClientFactory());
        String[] topics = defaultTopic.split(",");
        addListenTopic(topics);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttSubInputChannel());
        return adapter;
    }

    /**
     * 接受消息的handler
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttSubInputChannel")
    public MessageHandler SubHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                String msg = message.getPayload().toString();
                log.info("【mqtt收到消息】topic:{}, msg:{}", topic, msg);
            }
        };
    }
}


