package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.threading.runnables.ChannelReadHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

@ChannelHandler.Sharable
public class GameMessageHandler extends ChannelInboundHandlerAdapter
{
    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        if (!Emulator.getGameServer().getGameClientManager().addClient(ctx))
        {
            ctx.close();
            return;
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
    {
        try
        {
            Emulator.getThreading().run(new ChannelReadHandler(ctx, msg));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        if (cause instanceof Exception)
        {
            if (!(cause instanceof IOException))
            {
                cause.printStackTrace(Logging.getErrorsRuntimeWriter());
            }
        }

        ctx.close();
    }
}