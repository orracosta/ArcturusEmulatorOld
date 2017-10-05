package com.habboproject.server.game.players.components;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.components.types.messenger.MessengerFriend;
import com.habboproject.server.game.players.components.types.messenger.MessengerSearchResult;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.players.types.PlayerComponent;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.messages.outgoing.messenger.MessengerSearchResultsMessageComposer;
import com.habboproject.server.network.messages.outgoing.messenger.UpdateFriendStateMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.messenger.MessengerDao;
import com.habboproject.server.storage.queries.player.messenger.MessengerSearchDao;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


public class MessengerComponent implements PlayerComponent {
    private Player player;

    private Map<Integer, MessengerFriend> friends;

    private List<Integer> requests;

    public MessengerComponent(Player player) {
        this.player = player;

        try {
            this.friends = MessengerDao.getFriendsByPlayerId(player.getId());
        } catch (Exception e) {
            Logger.getLogger(MessengerComponent.class.getName()).error("Error while loading messenger friends", e);
        }
    }

    public void dispose() {
        this.sendStatus(false);
        this.sendStatus(false, false);

        this.requests.clear();
        this.friends.clear();
        this.requests = null;
        this.friends = null;
        this.player = null;
    }

    public MessageComposer search(String query) {
        List<MessengerSearchResult> currentFriends = Lists.newArrayList();
        List<MessengerSearchResult> otherPeople = Lists.newArrayList();

        try {
            for (MessengerSearchResult searchResult : MessengerSearchDao.performSearch(query)) {
                if (this.getFriendById(searchResult.getId()) != null) {
                    currentFriends.add(searchResult);
                } else {
                    otherPeople.add(searchResult);
                }
            }
        } catch (Exception e) {
            player.getSession().getLogger().error("Error while searching for players", e);
        }

        return new MessengerSearchResultsMessageComposer(currentFriends, otherPeople);
    }

    public void addRequest(int playerId) {
        this.getRequests().add(playerId);
    }

    public void addFriend(MessengerFriend friend) {
        this.getFriends().put(friend.getUserId(), friend);

        this.getPlayer().getAchievements().progressAchievement(AchievementType.FRIENDS_LIST, 1);
    }

    public void removeFriend(int userId) {
        if (!this.friends.containsKey(userId)) {
            return;
        }

        this.friends.remove(userId);

        MessengerDao.deleteFriendship(this.player.getId(), userId);
        this.player.getSession().send(new UpdateFriendStateMessageComposer(-1, userId));
    }

    public Integer getRequestBySender(int sender) {
        for (Integer request : requests) {
            if (request == sender) {
                return request;
            }
        }

        return null;
    }

    public void broadcast(MessageComposer msg) {
        for (MessengerFriend friend : this.getFriends().values()) {
            if (!friend.isOnline() || friend.getUserId() == this.getPlayer().getId()) {
                continue;
            }

            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(friend.getUserId());

            if (session != null)
                session.send(msg);
        }
    }

    public void broadcast(List<Integer> friends, MessageComposer msg) {
        for (int friendId : friends) {
            if (friendId == this.player.getId() || !this.friends.containsKey(friendId) || !this.friends.get(friendId).isOnline()) {
                continue;
            }

            MessengerFriend friend = this.friends.get(friendId);

            if (!friend.isOnline() || friend.getUserId() == this.getPlayer().getId()) {
                continue;
            }

            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(friend.getUserId());

            if (session != null && session.getPlayer() != null)
                session.send(msg);
        }
    }

    public boolean hasRequestFrom(int playerId) {
        if (this.requests == null) return false;

        for (Integer messengerRequest : this.requests) {
            if (messengerRequest == playerId)
                return true;
        }

        return false;
    }

    public List<PlayerAvatar> getRequestAvatars() {
        List<PlayerAvatar> avatars = Lists.newArrayList();

        if (this.requests == null) {
            this.requests = MessengerDao.getRequestsByPlayerId(player.getId());
        }

        for (int playerId : this.requests) {
            PlayerAvatar playerAvatar = PlayerManager.getInstance().getAvatarByPlayerId(playerId, PlayerAvatar.USERNAME_FIGURE);

            if (playerAvatar != null) {
                avatars.add(playerAvatar);
            }
        }

        return avatars;
    }

    public void clearRequests() {
        this.requests.clear();
    }

    public void sendOffline(int friend, boolean online, boolean inRoom) {
        this.getPlayer().getSession().send(new UpdateFriendStateMessageComposer(PlayerManager.getInstance().getAvatarByPlayerId(friend, PlayerAvatar.USERNAME_FIGURE_MOTTO), online, inRoom));
    }

    public void sendStatus(boolean online) {
        String image = Comet.getServer().getConfig().get("comet.notification.avatar.prefix");

        if (this.getPlayer() == null || this.getPlayer().getSettings() == null) {
            return;
        }

        if (this.getPlayer().getSettings().getHideOnline()) {
            return;
        }

        this.broadcast(new NotificationMessageComposer(image.replace("{0}", this.getPlayer().getData().getUsername()),
                "Seu amigo " + this.getPlayer().getData().getUsername() + " ficou " + (online ? "online" : "offline")));
    }

    public void sendStatus(boolean online, boolean inRoom) {
        if (this.getPlayer() == null || this.getPlayer().getSettings() == null) {
            return;
        }

        if (this.getPlayer().getSettings().getHideOnline()) {
            return;
        }

        this.broadcast(new UpdateFriendStateMessageComposer(this.getPlayer().getData(), online, inRoom));
    }

    public MessengerFriend getFriendById(int id) {
        return this.getFriends().get(id);
    }

    public Map<Integer, MessengerFriend> getFriends() {
        return this.friends;
    }

    public List<Integer> getRequests() {
        return this.requests;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void removeRequest(Integer request) {
        this.requests.remove(request);
    }

}
