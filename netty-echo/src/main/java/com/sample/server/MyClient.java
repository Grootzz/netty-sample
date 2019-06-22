package com.sample.server;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MyClient {
    public static void main(String[] args) throws InterruptedException {
        // 接收服务端的时间循环
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            // 创建客户端引导
            Bootstrap bootstrap = new Bootstrap();
            // 配置客户端
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class) // 指定监听的io
                    .handler(new MyClientInitializer()); // 处理请求
            // 使用 tcp 连接
            ChannelFuture channelFuture = bootstrap.connect("localhost", 3333).sync();
            channelFuture.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
