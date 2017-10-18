package com.eu.habbo.networking.gameserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.PacketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameServer
{
    private final PacketManager packetManager;
    private final GameClientManager gameClientManager;
    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final String host;
    private final int port;

    public GameServer(String host, int port) throws Exception
    {
        this.packetManager = new PacketManager();
        this.gameClientManager = new GameClientManager();

        this.bossGroup = new NioEventLoopGroup(Emulator.getConfig().getInt("io.bossgroup.threads"));
        this.workerGroup = new NioEventLoopGroup(Emulator.getConfig().getInt("io.workergroup.threads"));

        this.serverBootstrap = new ServerBootstrap();

        this.host = host;
        this.port = port;
    }

    public void initialise()
    {
        this.serverBootstrap.group(this.bossGroup, this.workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        final GameMessageHandler gameMessageHandler = new GameMessageHandler();
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast("logger", new LoggingHandler());
                ch.pipeline().addLast("bytesDecoder", new GameByteDecoder());
                ch.pipeline().addLast(gameMessageHandler);
            }
        });
        this.serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        this.serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        this.serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 5120);
        this.serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(5120));
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new UnpooledByteBufAllocator(false));
    }

    public void connect()
    {
        ChannelFuture channelFuture = this.serverBootstrap.bind(this.host, this.port);

        while (!channelFuture.isDone())
        {}

        if (!channelFuture.isSuccess())
        {
            Emulator.getLogging().logShutdownLine("Failed to connect to the host (" + this.host + ":" + this.port + ").");
            System.exit(0);
        }
        else
        {
            Emulator.getLogging().logStart("Started GameServer on " + this.host + ":" + this.port);
        }
    }

    public void stop()
    {
        Emulator.getLogging().logShutdownLine("Stopping GameServer...");
        try
        {
            this.workerGroup.shutdownGracefully().sync();
            this.bossGroup.shutdownGracefully().sync();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine("Exception during GameServer shutdown... HARD STOP");
        }
        Emulator.getLogging().logShutdownLine("GameServer Stopped!");
    }

    public PacketManager getPacketManager()
    {
        return packetManager;
    }

    public GameClientManager getGameClientManager()
    {
        return gameClientManager;
    }

    public ServerBootstrap getServerBootstrap()
    {
        return serverBootstrap;
    }

    public EventLoopGroup getBossGroup()
    {
        return bossGroup;
    }

    public EventLoopGroup getWorkerGroup()
    {
        return workerGroup;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }
}
