package com.eu.habbo.networking.gameserver;

import com.eu.habbo.habbohotel.gameclients.GameClientManager;
import com.eu.habbo.messages.PacketManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

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

        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(10);

        this.serverBootstrap = new ServerBootstrap();

        this.host = host;
        this.port = port;
    }

    public void initialise()
    {
        this.serverBootstrap.group(bossGroup, workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast("bytesDecoder", new GameByteDecoder());
                ch.pipeline().addLast(new GameMessageHandler());
            }
        });
        this.serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        this.serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        this.serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 5120);
        this.serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(5120));
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator());
    }

    public void connect()
    {
        this.serverBootstrap.bind(this.host, this.port);
    }

    public void stop()
    {
        this.workerGroup.shutdownGracefully();
        this.bossGroup.shutdownGracefully();
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
