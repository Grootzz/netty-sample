package com.sample.lifecycle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author noodle
 * @date 2019/6/25 19:03
 */
public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());

            ChannelFuture future = bootstrap.connect("127.0.0.1", 3333).sync();
            future.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
