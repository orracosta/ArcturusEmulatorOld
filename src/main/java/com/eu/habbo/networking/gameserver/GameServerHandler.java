package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created on 3-4-2015 16:53.
 */
public class GameServerHandler extends SimpleChannelInboundHandler<byte[]>
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
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception
    {

        byte[] msg = (byte[])message;

        //System.out.print(bufferToString(((ByteBuf) msg)));
        if(Emulator.isReady)
        {
            //ByteBuf buffer = Unpooled.copiedBuffer(msg);

            try
            {
                if(msg.length < 6)
                {
                    Emulator.getLogging().logErrorLine("Short Packet: " + new String(msg));
                    return;
                }

                if (msg[0] == 60)
                {
                    String s = new String(msg);
                    if(s.startsWith("<policy-file-request/>"))
                    {
                        ChannelFuture f = ctx.writeAndFlush(Unpooled.copiedBuffer("<?xml version=\"1.0\"?>\n" +
                                "  <!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\n" +
                                "  <cross-domain-policy>\n" +
                                "  <allow-access-from domain=\"*\" to-ports=\"1-31111\" />\n" +
                                "  </cross-domain-policy>" + (char) 0, CharsetUtil.UTF_8));

                        if(!f.isSuccess())
                        {
                            Emulator.getLogging().logErrorLine(f.cause());
                        }

                        f.channel().close();

                        //ctx.channel().pipeline().addLast(new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
                    }
                }
                else
                {
                    System.out.println(new String(msg));
                    /*byte[] length = buffer.readInt()
                    int messageLength = ByteBuffer.wrap(length).asIntBuffer().get();

                    if (messageLength < 0 || buffer.readableBytes() < messageLength)
                    {
                        Emulator.getLogging().logErrorLine("Malformed Packet ("+messageLength+ "): " +  bufferToString(buffer));
                        buffer.readerIndex(0);
                        return;
                    }

                    ByteBuf messageBuffer = Unpooled.wrappedBuffer(buffer.readBytes(messageLength));

                    if(messageBuffer.readableBytes() < 2)
                    {
                        Emulator.getLogging().logErrorLine("Malformed Packet, CANT READ LENGTH: " +  bufferToString(buffer));
                        return;
                    }

                    Short HeaderId = messageBuffer.readShort();
                    Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(HeaderId, messageBuffer));
*/
                    /*
                    int location = 0;

                    ByteBuf body = Unpooled.buffer();
                    Short header = 0;
                    while(location < buffer.capacity())
                    {
                        int length = buffer.readInt();
                        if(length < 2 || length > 99999)// cant read header or packet is wrong?
                        location = buffer.capacity();
                        else
                        location = length;

                        if(header == 0)
                            header = buffer.readShort();

                        body.writeBytes(buffer.readBytes(buffer.readableBytes()));
                        Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(header, body));
                        //ReferenceCountUtil.release(body);
                        //ReferenceCountUtil.release(buffer);
                    }*/

                    /*
                    ByteBuf buffer = Unpooled.wrappedBuffer(msg);
                    int pos = 0;
                    int i = 0;
                    while (pos < msg.length)
                    {
                        try
                        {
                            int MessageLength = buffer.readInt(); //Index out of bounds.
                            pos += 4;
                            if (MessageLength < 2 || MessageLength > 1024)
                            {
                                System.out.println(MessageLength);
                                Emulator.getLogging().logErrorLine(new String(msg));
                                Emulator.getLogging().logErrorLine("NEW LINE " + i);
                                i++;
                                //Console.WriteLine("bad size packet!");
                                pos = msg.length;
                                continue;
                            }

                            Short header = buffer.readShort();
                            pos += 2;

                            byte[] Content = new byte[MessageLength - 2];

                            int num2 = 0;
                            while (num2 < Content.length && pos < msg.length)
                            {
                                Content[num2] = buffer.getByte(pos++);
                                num2++;
                            }


                            ByteBuf body = Unpooled.copiedBuffer(Content);
                            Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(header, body));

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            break;
                        }
                    }*/

                    /*
                    buffer.readerIndex(0);
                    int length = buffer.readInt();
                    if(length > 1024)
                    {
                        Emulator.getLogging().logDebugLine(bufferToString(buffer));
                        return;
                    }
                    buffer.capacity(1024);

                    Emulator.getLogging().logDebugLine("Length: " + length + ", bufflength: " + buffer.readableBytes() + ", cap: " + buffer.capacity());
                    //buffer.capacity(length + 4);
                    //buffer.writerIndex(1024);
                    //ByteBuf messageBuffer = Unpooled.wrappedBuffer(buffer.readBytes(buffer.readableBytes()));

                    short headerId = buffer.readShort();
                    Emulator.getGameServer().getPacketManager().handlePacket(ctx.attr(GameClientManager.CLIENT).get(), new ClientMessage(headerId, buffer));
*/


                }
            }
            finally
            {
                //ReferenceCountUtil.release(msg); // (2)
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        //Emulator.getLogging().logPacketError(cause);

        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception
    {
        Emulator.getGameServer().getGameClientManager().disposeClient(ctx.channel());
    }

    private String byteArrayToString(byte[] data)
    {
        String s = "";

        for(int i = 0; i < data.length; i++)
        {
            s += data[i] < 12 ? "{"+(int)data[i]+"}" : (char)data[i] + "";
        }

        return s;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception
    {
        System.out.println("Message?");
        System.out.println(new String(msg));
    }
}