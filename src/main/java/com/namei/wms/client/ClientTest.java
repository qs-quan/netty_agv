package com.namei.wms.client;

/**
 * ${DESCRIPTION}
 *
 * @author 14684
 * @create 2020-12-07 17:16
 */
public class ClientTest {

    public static void main(String[] args) {
        new NettyClient().start("172.16.2.229", 1001);
    }
}
