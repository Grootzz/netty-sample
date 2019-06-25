package com.sample.lifecycle;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;


/**
 * @author noodle
 * @date 2019/6/25 18:58
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ServerHandler());
    }
}
