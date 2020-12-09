package com.namei.wms;

import com.namei.wms.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

/**
 * ${DESCRIPTION}
 *
 * @author 14684
 * @create 2020-12-07 14:18
 */
@SpringBootApplication
public class NameiWmsApplication implements CommandLineRunner {

    @Autowired
    NettyServer nettyServer;


    public static void main(String[] args) {

        SpringApplication.run(NameiWmsApplication.class, args);
    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        /**
         * 使用异步注解方式启动netty服务端服务
         */
        nettyServer.start(12345);

    }


}
