package com.habboproject.server.network.sessions;

import com.habboproject.server.api.networking.messages.IMessageComposer;
import com.habboproject.server.api.networking.sessions.BaseSession;
import com.habboproject.server.api.networking.sessions.ISessionManager;
import com.habboproject.server.api.networking.sessions.SessionManagerAccessor;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.players.PlayerManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class SessionManager implements ISessionManager {
    public static final AttributeKey<Session> SESSION_ATTR = AttributeKey.valueOf("Session.attr");
    public static final AttributeKey<Integer> CHANNEL_ID_ATTR = AttributeKey.valueOf("ChannelId.attr");

    private final AtomicInteger idGenerator = new AtomicInteger();
    private final Map<Integer, BaseSession> sessions = new ConcurrentHashMap<>();

    private final List<String> bannedIps = new ArrayList<>();

    private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static boolean isLocked = false;

    public SessionManager() {
        SessionManagerAccessor.getInstance().setSessionManager(this);
    }

    public boolean add(ChannelHandlerContext channel) {
        /*String address = ((InetSocketAddress)channel.channel().remoteAddress()).getAddress().getHostAddress();

        if (bannedIps.contains(address)) {
            return false;
        }*/

        Session session = new Session(channel);

        this.channelGroup.add(channel.channel());
        channel.attr(CHANNEL_ID_ATTR).set(this.idGenerator.incrementAndGet());

        return (this.sessions.putIfAbsent(channel.attr(CHANNEL_ID_ATTR).get(), session) == null);
    }

    public boolean remove(ChannelHandlerContext channel) {
        if (this.sessions.containsKey(channel.attr(CHANNEL_ID_ATTR).get())) {
            this.channelGroup.remove(channel.channel());
            this.sessions.remove(channel.attr(CHANNEL_ID_ATTR).get());

            return true;
        }

        return false;
    }

    public boolean disconnectByPlayerId(int id) {
        if (PlayerManager.getInstance().getSessionIdByPlayerId(id) == -1) {
            return false;
        }

        int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(id);
        Session session = (Session) sessions.get(sessionId);

        if (session != null) {
            session.disconnect();
            return true;
        }

        return false;
    }

    public Session getByPlayerId(int id) {
        if (PlayerManager.getInstance().getSessionIdByPlayerId(id) != -1) {
            int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(id);

            return (Session) sessions.get(sessionId);
        }

        return null;
    }

    public List<BaseSession> getPlayersByRank(int rank) {
        return this.sessions.values().stream()
                .filter(x -> x != null && x.getPlayer() != null && x.getPlayer().getData().getRank() >= rank)
                .collect(Collectors.toList());
    }

    public Set<BaseSession> getByPlayerPermission(String permission) {
        Set<BaseSession> sessions = new HashSet<>();

        List<Integer> enabledRanks = PermissionsManager.getInstance().getRankByPermission(permission);
        for (BaseSession session : this.sessions.values()) {
            if (enabledRanks.contains(session.getPlayer().getData().getRank())) {
                sessions.add(session);
            }
        }

        return sessions;
    }

    public Session getByPlayerUsername(String username) {
        int playerId = PlayerManager.getInstance().getPlayerIdByUsername(username);

        if (playerId == -1)
            return null;

        int sessionId = PlayerManager.getInstance().getSessionIdByPlayerId(playerId);

        if (sessionId == -1)
            return null;

        if (this.sessions.containsKey(sessionId))
            return (Session) this.sessions.get(sessionId);

        return null;
    }

    public int getUsersOnlineCount() {
        return PlayerManager.getInstance().size();
    }

    public Map<Integer, BaseSession> getSessions() {
        return this.sessions;
    }

    public void broadcast(IMessageComposer msg) {
        this.getChannelGroup().writeAndFlush(msg);
    }

    public void broadcastEventAlert(IMessageComposer msgOne, IMessageComposer msgTwo) {
        for (BaseSession session : this.sessions.values()) {
            if (session.getPlayer() == null || session.getPlayer().getSettings() == null)
                continue;

            if (session.getPlayer().getSettings().enableEventNotif()) {
                session.send(msgOne);
                continue;
            }

            session.send(msgTwo);
        }
    }

    public void broadcastCatalogUpdate() {
        for (BaseSession session : this.sessions.values()) {
            if (session.getPlayer() == null || session.getPlayer().getSettings() == null)
                continue;

            if (!session.getPlayer().getSettings().catalogUpdateMessage()) {
                session.getPlayer().getSettings().setCatalogUpdateMessage(true);
                continue;
            }

        }
    }

    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void broadcastToModerators(IMessageComposer messageComposer) {
        for (BaseSession session : this.sessions.values()) {
            if (session.getPlayer() != null && session.getPlayer().getPermissions() != null && session.getPlayer().getPermissions().getRank().modTool()) {
                session.send(messageComposer);
            }
        }
    }
}