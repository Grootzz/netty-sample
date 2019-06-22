package com.sample.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author noodle
 * @date 2019/6/22
 */
public class MyChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 保存channel对象
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 处理接收消息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取向服务器发送消息的客户端channel
        Channel channel = ctx.channel();

        channelGroup.forEach(ch -> {
            // 如果不是本channel
            if (ch != channel) {
                ch.writeAndFlush("【" + channel.remoteAddress() + "】" + " 发送的消息: " + msg + "\n");
            } else {
                ch.writeAndFlush("【自己】 " + msg + "\n");
            }
        });
    }


    // 客户端与服务端建立连接后，handler添加到Context时被调用
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 获取连接（channel）
        Channel channel = ctx.channel();
        // 调用已添加到group中的channel的writeAndFlush(), 达到广播的目的
        channelGroup.writeAndFlush("【客户端】- " + channel.remoteAddress() + " 加入\n");
        // 将本channel添加到group
        channelGroup.add(channel);
    }

    // 客户端与服务端失去连接
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("【服务器】- " + channel.remoteAddress() + " 离开\n");
        channelGroup.remove(channel);// 实际上，不用remove也可以，netty自动完成remove
    }

    // 连接处于活动状态
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + " 上线");
    }

    // 连接处于失活状态
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress() + "下线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}
