package com.ekoplat.iot.server.handler;

/**
 * @author wuwudeqi
 * @date 14:22 2019-07-23
 * @description netty服务器
 **/


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ekoplat.iot.server.common.model.RequestCmd;
import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.Common;
import com.ekoplat.iot.util.SpringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<Object> {


    private GatewayAndLockService gatewayAndLockService = SpringUtil.getBean(GatewayAndLockService.class);

    /**
     * 接受到数据
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof String) {
            String jsonMsg = (String) msg;
            log.info("【服务器收到】msg: {}, ip: {}", jsonMsg, Common.getIP(ctx.channel().remoteAddress().toString()));
            //测试gm3模块
            JSONObject json = JSON.parseObject(jsonMsg);
            String gm3 = json.getString("Gm3-Test");
            if (gm3 != null && !"".equals(gm3)) {
                ctx.channel().writeAndFlush("{\"Gm3-Test\":\"ok\"}");
                log.info("【gm3测试正常】已发送ok");
            }
            ctx.fireChannelRead(msg);
        }else if (msg instanceof RequestCmd) {

            RequestCmd requestCmd = (RequestCmd) msg;
            log.info("【服务器收到】requestCmd:{}, ip: {}", msg.toString(), Common.getIP(ctx.channel().remoteAddress().toString()));
            ctx.fireChannelRead(requestCmd);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String ip = Common.getIP(ctx.channel().remoteAddress().toString());
        log.info("【设备建立连接】ip:{}",ip);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String ip = Common.getIP(ctx.channel().remoteAddress().toString());
        //将ip为key的map移除
        ChannelMap.removeChannelByName(ip);
        //更新数据库
        gatewayAndLockService.changeGwStatus("", ip, 0);
        log.info("【设备失去连接】ip:{}",ip);
    }

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        String ip = Common.getIP(ctx.channel().remoteAddress().toString());
//        super.userEventTriggered(ctx, evt);
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state().equals(IdleState.READER_IDLE)) {
//                log.error("【设备离线】60s没有心跳，{}离线",ip);
//            }
//        }
//    }
}

