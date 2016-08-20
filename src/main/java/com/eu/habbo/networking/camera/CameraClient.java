package com.eu.habbo.networking.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.networking.camera.messages.outgoing.CameraLoginComposer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CameraClient
{
    public static final String host = "arcturus.wf";
    public static final int port = 1232;
    public static ChannelFuture channelFuture;
    public static Channel channel;
    public static boolean isLoggedIn = false;
    public static boolean attemptReconnect = true;

    private Bootstrap bootstrap = new Bootstrap();

    public CameraClient()
    {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast(new CameraDecoder());
                ch.pipeline().addLast(new CameraHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_RCVBUF, 2000000);
        bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2000000));
        bootstrap.option(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
    }

    public void connect()
    {
        channelFuture = this.bootstrap.connect(host, port);

        while (!channelFuture.isDone())
        {
        }

        if (channelFuture.isSuccess())
        {
            channel = channelFuture.channel();
            System.out.println("[" + Logging.ANSI_GREEN + "CAMERA" + Logging.ANSI_RESET + "] Connected to the Camera Server. Attempting to login...");
            sendMessage(new CameraLoginComposer());
        }
        else
        {
            System.out.println("[" + Logging.ANSI_RED + "CAMERA" + Logging.ANSI_RESET + "] Failed to connect to the Camera Server. Server unreachable.");
        }
    }

    public void disconnect()
    {
        if (channelFuture != null)
        {
            channelFuture.channel().close();
            channelFuture = null;
        }

        channel = null;
        isLoggedIn = false;
    }

    public void sendMessage(CameraOutgoingMessage outgoingMessage)
    {
        try
        {
            if (isLoggedIn || outgoingMessage instanceof CameraLoginComposer)
            {
                outgoingMessage.compose(channel);
                channel.write(outgoingMessage.get().copy(), channel.voidPromise());
                channel.flush();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}