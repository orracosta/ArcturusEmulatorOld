package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;

public class GameMessageHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        if (!Emulator.getGameServer().getGameClientManager().addClient(ctx))
        {
            ctx.channel().disconnect();
            return;
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        Emulator.getGameServer().getGameClientManager().disposeClient(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
    {
        ByteBuf m = (ByteBuf) msg;
        int length = m.readInt();
        short header = m.readShort();
        ByteBuf body = Unpooled.wrappedBuffer(m.readBytes(m.readableBytes()));

        Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(header, body));

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Emulator.getGameServer().getGameClientManager().disposeClient(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        if (cause instanceof Exception)
        {
            if (!(cause instanceof IOException))
            {
                Emulator.getLogging().logErrorLine(cause);
            }
        }

        ctx.close();
    }
}