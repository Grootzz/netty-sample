package com.sample.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author noodle
 * @date 2019/6/22
 */
public class MyServerHandler extends ChannelInboundHandlerAdapter {

    // 用户事件触发
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            String eventType = null;

            switch (event.state()) {
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
            }
            System.out.println(ctx.channel().remoteAddress() + "超时事件: " + eventType);
            ctx.channel().close();
        }
    }
}
