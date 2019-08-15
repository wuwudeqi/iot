package com.ekoplat.iot.client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * netty client handler.
 *
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:31 2019-07-23
 **/
public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) {
        channelHandlerContext.channel().attr(AttributeKey.valueOf("Attribute_key")).set(msg);
        channelHandlerContext.close();
    }
}