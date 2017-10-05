package com.habboproject.server.network.clients;

import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.misc.PingMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.network.sessions.SessionManager;
import com.habboproject.server.protocol.messages.MessageEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

import java.io.IOException;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MessageEvent> {
    private static Logger log = Logger.getLogger(ClientHandler.class.getName());

    private static ClientHandler clientHandlerInstance;

    public static ClientHandler getInstance() {
        if (clientHandlerInstance == null)
            clientHandlerInstance = new ClientHandler();

        return clientHandlerInstance;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!NetworkManager.getInstance().getSessions().add(ctx)) {
            ctx.channel().disconnect();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            Session session = ctx.attr(SessionManager.SESSION_ATTR).get();
            session.onDisconnect();
        } catch (Exception e) {
//            e.printStackTrace();
        }

        NetworkManager.getInstance().getSessions().remove(ctx);

        ctx.attr(SessionManager.SESSION_ATTR).remove();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (NetworkManager.IDLE_TIMER_ENABLED) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;
                if (e.state() == IdleState.READER_IDLE) {
                    ctx.close();
                } else if (e.state() == IdleState.WRITER_IDLE) {
                    ctx.writeAndFlush(new PingMessageComposer());
                }
            }
        }

        if (evt instanceof ChannelInputShutdownEvent) {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel().isActive()) {
            ctx.close();
        }

        if (cause instanceof IOException) return;

        log.error("Exception caught in ClientHandler", cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, MessageEvent event) throws Exception {
        try {
            Session session = channelHandlerContext.attr(SessionManager.SESSION_ATTR).get();

            if (session != null) {
                session.handleMessageEvent(event);
            }
        } catch (Exception e) {
            log.error("Error while receiving message", e);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }
}