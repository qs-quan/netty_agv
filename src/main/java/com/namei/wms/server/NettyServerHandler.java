package com.namei.wms.server;

import com.namei.wms.client.NettyClientHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/*
说明
1. 我们自定义一个Handler 需要继续netty 规定好的某个HandlerAdapter(规范)
2. 这时我们自定义一个Handler , 才能称为一个handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    NettyClientHandler clientHandler = new NettyClientHandler();

    //读取数据实际(这里我们可以读取客户端发送的消息)
    /*
    1. ChannelHandlerContext ctx:上下文对象, 含有 管道pipeline , 通道channel, 地址
    2. Object msg: 就是客户端发送的数据 默认Object
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        //比如这里我们有一个非常耗时长的业务-> 异步执行 -> 提交该channel 对应的
        // Channel channel = ctx.channel();

        //将 msg 转成一个 ByteBuf
        //ByteBuf 是 Netty 提供的，不是 NIO 的 ByteBuffer.
        System.err.println("server reveived");
        clientHandler.channelRead(ctx, msg);
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        // writeAndFlush 是 write + flush
        // 将数据写入到缓存，并刷新
        // 一般讲，我们对这个发送的数据进行编码
//        ctx.writeAndFlush(Unpooled.copiedBuffer("STXWMS   @METRO @12@U@TS@00512@ @ @002@050@000000000000@1200@STABLE" +
//                "@0000@0000@09902@02501@1ETX", CharsetUtil.ISO_8859_1));


    }

    //处理异常, 一般是需要关闭通道

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
