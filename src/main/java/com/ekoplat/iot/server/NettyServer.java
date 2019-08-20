package com.ekoplat.iot.server;

/**
 * @Description:
 * @Author: wuwudeqi
 * @Date: 14:20 2019-07-23
 **/

import com.ekoplat.iot.service.GatewayAndLockService;
import com.ekoplat.iot.util.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class NettyServer {

    public  static final EventLoopGroup bossGroup = new NioEventLoopGroup();
    public  static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public  static  Channel channel;

    /**
     * 启动服务
     */
    public ChannelFuture run(InetSocketAddress address) {

        ChannelFuture f = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            f = b.bind(address).syncUninterruptibly();
            channel = f.channel();
        } catch (Exception e) {
            log.error("Netty start error:", e);
        } finally {
            if (f != null && f.isSuccess()) {
                log.info("Netty server listening " + address.getHostName() + " on port " + address.getPort() + " and ready for connections...");
            } else {
                log.error("Netty server start up Error!");
            }
        }

        return f;
    }

    public static void destroy() {
        log.info("Shutdown Netty Server...");
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        log.info("Shutdown Netty Server Success!");
        GatewayAndLockService gatewayAndLockService = SpringUtil.getBean(GatewayAndLockService.class);
        gatewayAndLockService.changeAllGwStatus();
        log.info("所有设备数据库下线");
    }
}
