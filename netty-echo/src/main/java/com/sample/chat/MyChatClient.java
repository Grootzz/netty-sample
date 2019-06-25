package com.sample.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * chat 客户端
 *
 * @author noodle
 * @date 2019/6/22
 */
public class MyChatClient {
    public static void main(String[] args) throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new MyChatClientInitializer());

            Channel channel = bootstrap.connect("127.0.0.1", 3333).sync().channel();

            // 从控制台读取消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                channel.writeAndFlush(reader.readLine() + "\r\n");
            }
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
