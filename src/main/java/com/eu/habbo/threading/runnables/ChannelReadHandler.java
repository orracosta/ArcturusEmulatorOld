package com.eu.habbo.threading.runnables;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class ChannelReadHandler implements Runnable
{
    private final ChannelHandlerContext ctx;
    private final Object msg;

    public ChannelReadHandler(ChannelHandlerContext ctx, Object msg)
    {
        this.ctx = ctx;
        this.msg = msg;
    }

    public void run()
    {
        ByteBuf m = (ByteBuf) msg;
        int length = m.readInt();
        short header = m.readShort();
        ByteBuf body = Unpooled.wrappedBuffer(m.readBytes(m.readableBytes()));
        Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(header, body));
        body.release();
        m.release();
    }
}