package com.habboproject.server.game.commands.notifications.types;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Notification {
    private String trigger;
    private String text;
    private NotificationType type;
    private int minRank;
    private int coolDown;

    public Notification(ResultSet data) throws SQLException {
        this.trigger = data.getString("name");
        this.text = data.getString("text");
        this.type = NotificationType.valueOf(data.getString("type").toUpperCase());
        this.minRank = data.getInt("min_rank");
        this.coolDown = data.getInt("cooldown");
    }

    public void execute(Player player) {
        if ((player.getNotifCooldown() + coolDown) >= Comet.getTime()) {
            return;
        }

        switch (this.type) {
            case GLOBAL:
                NetworkManager.getInstance().getSessions().broadcast(new AdvancedAlertMessageComposer(this.text + "\n\n-" + player.getData().getUsername()));
                break;

            case LOCAL:
                player.getSession().send(new AdvancedAlertMessageComposer(this.text));
                break;
        }

        player.setNotifCooldown((int) Comet.getTime());
    }

    public String getTrigger() {
        return trigger;
    }

    public String getText() {
        return text;
    }

    public NotificationType getType() {
        return type;
    }

    public int getMinRank() {
        return minRank;
    }

    public int getCoolDown() {
        return coolDown;
    }
}
