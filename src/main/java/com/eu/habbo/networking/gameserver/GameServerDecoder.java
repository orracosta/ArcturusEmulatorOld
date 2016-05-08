package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.ClientMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created on 12-7-2015 17:07.
 */
public class GameServerDecoder extends ByteArrayDecoder
{
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception
    {
        int handledObjects = 0;
        while (msg.readableBytes() > 0) {
            try {
                final byte testXmlLength = msg.readByte();
                if (testXmlLength == 60) {
                    msg.resetReaderIndex();
                    byte[] array = new byte[msg.readableBytes()];
                    msg.getBytes(0, array);
                    out.add(array);
                    return;
                }

                msg.readerIndex(0);
                msg.writerIndex(msg.capacity());
                final int messageLength = msg.readInt();
                final short messageId = msg.readShort();
                final byte[] content = new byte[messageLength - 2];
                msg.readBytes(content);

                Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(messageId, Unpooled.wrappedBuffer(content)));
                msg.writerIndex(messageLength + 4);

                ++handledObjects;
            } catch (final Exception e) {
                e.printStackTrace();
                Emulator.getLogging().logErrorLine(msg.toString(Charset.defaultCharset()));
            }
        }

        for(Object o : out)
        {
            if(o instanceof byte[])
            {
                System.out.println(new String((byte[]) o));
            }
            else
            {
                System.out.println(o.toString());
            }
        }
        /*byte[] array = new byte[msg.readableBytes()];
        msg.getBytes(0, array);
        out.add(array);*/
    }
}
