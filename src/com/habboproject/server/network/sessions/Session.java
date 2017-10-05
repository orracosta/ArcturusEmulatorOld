package com.habboproject.server.network.sessions;

import com.habboproject.server.api.networking.messages.IMessageComposer;
import com.habboproject.server.api.networking.sessions.BaseSession;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.network.messages.outgoing.notification.LogoutMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarUpdateMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.protocol.security.exchange.DiffieHellman;
import com.habboproject.server.storage.queries.player.PlayerDao;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.UUID;


public class Session implements BaseSession {
    private Logger logger = Logger.getLogger("Session");
    public static int CLIENT_VERSION = 0;

    private final ChannelHandlerContext channel;
    private final SessionEventHandler eventHandler;

    private boolean isClone = false;
    private String uniqueId = "";

    private final UUID uuid = UUID.randomUUID();

    private Player player;

    private DiffieHellman diffieHellman;

    public Session(ChannelHandlerContext channel) {
        this.channel = channel;
        this.channel.attr(SessionManager.SESSION_ATTR).set(this);
        this.eventHandler = new SessionEventHandler(this);
    }

    public void setPlayer(Player player) {
        if (player == null || player.getData() == null) {
            return;
        }

        String username = player.getData().getUsername();

        this.logger = Logger.getLogger("[" + username + "][" + player.getId() + "]");
        this.player = player;

        int channelId = this.channel.attr(SessionManager.CHANNEL_ID_ATTR).get();

        PlayerManager.getInstance().put(player.getId(), channelId, username, this.getIpAddress());

        if(player.getPermissions().getRank().modTool()) {
            ModerationManager.getInstance().addModerator(player.getSession());
        }
    }

    public void onDisconnect() {
        if (!isClone && player != null && player.getData() != null)
            PlayerManager.getInstance().remove(player.getId(), player.getData().getUsername(), this.channel.attr(SessionManager.CHANNEL_ID_ATTR).get(), this.getIpAddress());

        this.eventHandler.dispose();

        if (this.player != null) {
            if(this.getPlayer().getPermissions().getRank().modTool()) {
                ModerationManager.getInstance().removeModerator(this);
            }

            this.getPlayer().dispose();
        }

        this.setPlayer(null);
    }

    public void disconnect(boolean isClone) {
        this.isClone = isClone;
        this.getChannel().disconnect();
    }

    public String getIpAddress() {
        String ipAddress = "0.0.0.0";

        if (!CometSettings.useDatabaseIp) {
            return ((InetSocketAddress) this.getChannel().channel().remoteAddress()).getAddress().getHostAddress();
        } else {
            if(this.getPlayer() != null) {
                ipAddress = PlayerDao.getIpAddress(this.getPlayer().getId());
            }
        }

        return ipAddress;
    }

    public void disconnect() {
        this.disconnect(false);
    }

    public void disconnect(String reason) {
        this.send(new LogoutMessageComposer(reason));
        this.disconnect();
    }

    public void handleMessageEvent(MessageEvent msg) {
        this.eventHandler.handle(msg);
    }

    public Session sendQueue(final IMessageComposer msg) {
        return this.send(msg, true);
    }

    public Session send(IMessageComposer msg) {
        return this.send(msg, false);
    }

    public Session send(IMessageComposer msg, boolean queue) {
        if (msg == null) {
            return this;
        }

        if (msg.getId() == 0) {
            logger.debug("Unknown header ID for message: " + msg.getClass().getSimpleName());
        }

        if (!(msg instanceof AvatarUpdateMessageComposer) && !(msg instanceof UpdateFloorItemMessageComposer))
            logger.debug("Sent message: " + msg.getClass().getSimpleName() + " / " + msg.getId());

        if (!queue) {
            this.channel.writeAndFlush(msg);
        } else {
            this.channel.write(msg);
        }
        return this;
    }

    public void flush() {
        this.channel.flush();
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ChannelHandlerContext getChannel() {
        return this.channel;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getSessionId() {
        return uuid;
    }

    public DiffieHellman getDiffieHellman() {
        if(this.diffieHellman == null) {
            this.diffieHellman = new DiffieHellman();
        }

        return diffieHellman;
    }
}
