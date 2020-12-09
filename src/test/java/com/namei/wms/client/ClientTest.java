package com.namei.wms.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ${DESCRIPTION}
 *
 * @author 14684
 * @create 2020-12-07 15:40
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClientTest {

    @Autowired
    NettyClient nettyClient;

    @Test
    public void test() {
        nettyClient.start("127.0.0.1", 12345);
    }

}
