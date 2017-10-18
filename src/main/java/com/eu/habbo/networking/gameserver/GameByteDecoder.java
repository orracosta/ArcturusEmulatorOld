package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class GameByteDecoder extends ByteToMessageDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
    {
        in.markReaderIndex();

        //4 bytes length + 2 bytes header
        if (in.readableBytes() < 6)
        {
            in.resetReaderIndex();
            return;
        }

        int length = in.readInt();

        //if(length > 5120 && (length >> 24 != 60))
        //{
        //    ctx.close();
        //}

        if (length == 1014001516)
        {
            in.resetReaderIndex();
            //in.readBytes(in.readableBytes());

            ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer("<?xml version=\"1.0\"?>\n" +
                    "  <!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n" +
                    "  <cross-domain-policy>\n" +
                    "  <allow-access-from domain=\"*\" to-ports=\"1-31111\" />\n" +
                    "  </cross-domain-policy>" + (char) 0, CharsetUtil.UTF_8));

            f.channel().close();
            ctx.close();
            return;
        }

        if (in.readableBytes() < length || length < 0)
        {
            in.resetReaderIndex();
            return;
        }

        in.resetReaderIndex();
        ByteBuf read = in.readBytes(length + 4);
        out.add(read); // (4)
    }
}