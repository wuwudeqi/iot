package com.ekoplat.iot.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 *netty client.
 *
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:31 2019-07-23
 **/
@Slf4j
public class NettyClient {


    private static Bootstrap b;
    private static ChannelFuture f;
    private static final EventLoopGroup WORK_GROUP = new NioEventLoopGroup();

    private static void init () {
        try {
            log.info("init...");
            b = new Bootstrap();
            b.group(WORK_GROUP);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    // 解码编码
                    socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object startAndWrite(InetSocketAddress address, Object send) throws InterruptedException {
        init();
        f = b.connect(address).sync();
        // 传数据给服务端
        f.channel().writeAndFlush(send);
        f.channel().closeFuture().sync();
        return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(10101);
        String message = "hello";
        try {
            Object result = NettyClient.startAndWrite(address, message);
            log.info("....result:" + result);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            f.channel().close();
            WORK_GROUP.shutdownGracefully();
            log.info("Closed client!");
        }
    }
}

