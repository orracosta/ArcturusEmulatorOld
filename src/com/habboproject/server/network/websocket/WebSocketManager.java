package com.habboproject.server.network.websocket;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.websocket.clients.WebSocketClientHandler;
import com.habboproject.server.network.websocket.messages.WebSocketCommandHandler;
import com.habboproject.server.utilities.Initializable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

public class WebSocketManager implements Initializable {
    private static WebSocketManager instance;

    private final Logger log = Logger.getLogger(WebSocketManager.class.getName());

    private final String host;
    private final int port;

    private final String[] allowedAddress;

    private final WebSocketCommandHandler commandHandler;

    public WebSocketManager() {
        this.host = Comet.getServer().getConfig().get("comet.websocket.host");
        this.port = Integer.parseInt(Comet.getServer().getConfig().get("comet.websocket.port"));

        this.allowedAddress = Comet.getServer().getConfig().get("comet.websocket.allowedAddress").split(";");

        this.commandHandler = new WebSocketCommandHandler();
    }

    @Override
    public void initialize() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(2);

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new WebSocketClientHandler());
            }
        });

        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 2048);
        bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));
        bootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator());

        bootstrap.bind(new InetSocketAddress(this.host, this.port)).addListener(objectFuture -> {
                    if (!objectFuture.isSuccess()) {
                        Comet.exit("Failed to initialize sockets on address: " + this.host + ":" + this.port);
                    }
                }
        );
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }

        return instance;
    }

    public String[] getAllowedAddress() {
        return allowedAddress;
    }

    public WebSocketCommandHandler getCommandHandler() {
        return commandHandler;
    }
}
