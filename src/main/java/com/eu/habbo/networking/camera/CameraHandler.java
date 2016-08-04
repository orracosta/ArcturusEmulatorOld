package com.eu.habbo.networking.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.threading.runnables.CameraClientAutoReconnect;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CameraHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        ByteBuf message = (ByteBuf) msg;
        ((ByteBuf) msg).readerIndex(0);
        int length = message.readInt();

        ByteBuf b = Unpooled.wrappedBuffer(message.readBytes(length));

        short header = b.readShort();

        CameraPacketHandler.instance().handle(ctx.channel(), header, b);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Emulator.getThreading().run(new CameraClientAutoReconnect());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
    }

}