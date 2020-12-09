package com.namei.wms.config;

import com.namei.wms.client.NettyClient;
import com.namei.wms.server.NettyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ${DESCRIPTION}
 *
 * @author 14684
 * @create 2020-12-07 14:20
 */
@Configuration
public class Config {

    @Bean
    public NettyServer nettyServer() {
        return new NettyServer();
    }

    @Bean
    public NettyClient nettyClient() {
        return new NettyClient();
    }

}
