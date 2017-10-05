package com.habboproject.server.game.commands.notifications;

import com.habboproject.server.game.commands.notifications.types.Notification;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.storage.queries.system.NotificationCommandsDao;
import org.apache.log4j.Logger;

import java.util.Map;


public class NotificationManager {
    private Map<String, Notification> notifications;

    private Logger log = Logger.getLogger(NotificationManager.class.getName());

    public NotificationManager() {
        this.notifications = NotificationCommandsDao.getAll();

        log.info("Loaded " + notifications.size() + " notification commands");
    }

    public boolean isNotificationExecutor(String text, int rank) {
        return this.notifications.containsKey(text.substring(1)) && this.notifications.get(text.substring(1)).getMinRank() <= rank;
    }

    public void execute(Player player, String command) {
        Notification notification = this.notifications.get(command);

        if (notification == null)
            return;

        notification.execute(player);
    }
}
