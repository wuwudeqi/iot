package com.ekoplat.iot.server;

/**
 * @Author wuwudeqi
 * @Date 14:22 2019-07-23
 * @description
 **/


import com.ekoplat.iot.server.common.codc.RequestDecoder;
import com.ekoplat.iot.server.common.codc.ResponseEncoder;
import com.ekoplat.iot.server.handler.HexHandler;
import com.ekoplat.iot.server.handler.JsonMsgHandler;
import com.ekoplat.iot.server.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {


//    String certPath = ServerChannelInitializer.class.getClassLoader().getResource("ssl/cert.crt").getPath();
//    String privatePath = ServerChannelInitializer.class.getClassLoader().getResource("ssl/private.pem").getPath();
//    // 证书
//    private File certificate = new File(certPath);
//    // 私钥
//    private File privateKey = new File(privatePath);
//    private final SslContext sslContext = SslContextBuilder.forServer(certificate, privateKey).build();
//
//    public ServerChannelInitializer() throws CertificateException, SSLException {
//    }


    @Override
    protected void initChannel(SocketChannel socketChannel) throws IOException {
        ChannelPipeline pipeline = socketChannel.pipeline();
        // SslHandler要放在最前面
//        SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
//        pipeline.addLast(sslHandler);
        pipeline.addLast("ping", new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(new RequestDecoder());
        pipeline.addLast(new ResponseEncoder());
        pipeline.addLast(new ServerHandler());
        pipeline.addLast(new JsonMsgHandler());
        pipeline.addLast(new HexHandler());
    }


}
