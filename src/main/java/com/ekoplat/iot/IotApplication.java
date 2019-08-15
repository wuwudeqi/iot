package com.ekoplat.iot;

import com.ekoplat.iot.server.NettyServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import java.net.InetSocketAddress;


@SpringBootApplication
@EnableCaching
public class IotApplication extends SpringBootServletInitializer implements CommandLineRunner  {

    @Value("${socket.port}")
    private int port;

    @Value("${socket.address}")
    private String url;


    @Autowired
    private NettyServer socketServer;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(IotApplication.class);
    }



    public static void main(String[] args) {
        SpringApplication.run(IotApplication.class, args);
    }



    @Override
    public void run(String... strings) {
        InetSocketAddress address = new InetSocketAddress(port);
        ChannelFuture future = socketServer.run(address);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                    socketServer.destroy();
            }
        });
        future.channel().closeFuture().syncUninterruptibly();
    }

}
