package com.habboproject.server.game.players.login.queue;

import com.habboproject.server.threads.CometThread;
import org.apache.log4j.Logger;

import java.util.ArrayDeque;


public class PlayerLoginQueue implements CometThread {
    private final int MAX_QUEUE_SIZE = 1000;
    private final ArrayDeque<PlayerLoginQueueEntry> queue = new ArrayDeque<>();

    private Logger log = Logger.getLogger(PlayerLoginQueue.class.getName());

    @Override
    public void run() {
        if (this.queue.isEmpty()) {
            return;
        }
        PlayerLoginQueueEntry entry = this.queue.pop();
        this.processQueueItem(entry);
    }

    private void processQueueItem(PlayerLoginQueueEntry entry) {
//        Session client = entry.getClient();
//
//        int id = entry.getPlayerId();
//        String sso = entry.getSsoTicket();
//
//        Player player = PlayerDao.getPlayer(sso);
//
//        if (player == null) {
//            client.disconnect();
//            return;
//        }
//
//        Session cloneSession = NetworkManager.getInstance().getSessions().getByPlayerId(player.getId());
//
//        if (cloneSession != null) {
//            cloneSession.disconnect();
//        }
//
//        if (BanManager.getInstance().hasBan(Integer.toString(player.getId())) || BanManager.getInstance().hasBan(entry.getClient().getIpAddress())) {
//            CometManager.getLogger().warn("Banned player: " + player.getId() + " tried logging in");
//
//            client.disconnect();
//            return;
//        }
//
//        player.setSession(client);
//        client.setPlayer(player);
//
//        RoomManager.getInstance().loadRoomsForUser(player);
//
//        //client.getLogger().info(client.getPlayer().getData().getUsername() + " logged in");
//
//        PlayerDao.updatePlayerStatus(player, true, true);
//
//        client.send(new AuthenticationOKMessageComposer());
//        client.getPlayer().sendBalance();
//        client.send(new FuserightsMessageComposer(client.getPlayer().getSubscription().exists(), client.getPlayer().getData().getRank()));
//        client.send(new MotdNotificationMessageComposer());
//
//        if (player.getSettings().getHomeRoom() > 0) {
//            client.send(new HomeRoomMessageComposer(player.getSettings().getHomeRoom()));
//        }
//
//        if (client.getPlayer().getPermissions().getRank().modTool()) {
//            client.send(new ModToolMessageComposer());
//        }
//
//        client.send(new RoomCategoriesMessageComposer(NavigatorManager.getInstance().getCategories(), client.getPlayer().getData().getRank()));
    }

    public boolean queue(PlayerLoginQueueEntry entry) {
        if (this.queue.size() >= MAX_QUEUE_SIZE) {
            log.warn("PlayerLoginQueue size reached max size of " + MAX_QUEUE_SIZE);
            return false;
        }

        return this.queue.add(entry);
    }
}
