package com.habboproject.server.network;

import com.habboproject.server.network.clients.ClientHandler;
import com.habboproject.server.protocol.codec.MessageDecoder;
import com.habboproject.server.protocol.codec.MessageEncoder;
import com.habboproject.server.protocol.codec.XMLPolicyDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.TimeUnit;

public class NetworkChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final EventExecutorGroup executor;

    public NetworkChannelInitializer(EventExecutorGroup executorGroup) {
        this.executor = executorGroup;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.config().setTrafficClass(0x18);

        ch.pipeline()
                .addLast("xmlDecoder", new XMLPolicyDecoder())
                .addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast("messageDecoder", new MessageDecoder())
                .addLast("messageEncoder", new MessageEncoder());

        if (NetworkManager.IDLE_TIMER_ENABLED) {
            ch.pipeline().addLast("idleHandler",
                    new IdleStateHandler(
                            NetworkManager.IDLE_TIMER_READER_TIME,
                            NetworkManager.IDLE_TIMER_WRITER_TIME,
                            NetworkManager.IDLE_TIMER_ALL_TIME,
                            TimeUnit.SECONDS));
        }

        ch.pipeline().addLast(this.executor, "clientHandler", ClientHandler.getInstance());
    }
}