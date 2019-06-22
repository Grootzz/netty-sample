package com.sample.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty服务端
 *
 * @author noodle
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        // 接收客户端连接, 除此之外，不做其他处理
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 对接收到的连接做处理
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 服务端， 相当于NIO中的ServerChannel
            // sub-class which allows easy bootstrap of ServerChannel
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer()); // 子处理器

            ChannelFuture channelFuture = serverBootstrap.bind(3333).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
