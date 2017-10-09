package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.types.RoomBan;
import com.habboproject.server.game.rooms.types.components.types.RoomMute;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.navigator.NavigatorDao;
import com.habboproject.server.storage.queries.rooms.RightsDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public class RightsComponent {
    private Room room;

    private List<Integer> rights;
    private List<RoomBan> bannedPlayers;
    private List<RoomMute> mutedPlayers;

    public RightsComponent(Room room) {
        this.room = room;

        try {
            this.rights = RightsDao.getRightsByRoomId(room.getId());
        } catch (Exception e) {
            this.rights = new CopyOnWriteArrayList<>();
            this.room.log.error("Error while loading room rights", e);
        }

        this.bannedPlayers = new CopyOnWriteArrayList<>();
        this.mutedPlayers = new CopyOnWriteArrayList<>();
    }

    public void dispose() {
        this.rights.clear();
        this.bannedPlayers.clear();
    }

    public boolean hasRights(int playerId) {
        Group group = this.getRoom().getGroup();

        if (group != null && group.getData() != null && group.getMembershipComponent() != null && group.getMembershipComponent().getMembers() != null) {
            if (group.getData().canMembersDecorate() && group.getMembershipComponent().getMembers().containsKey(playerId)) {
                return true;
            }

            if (group.getMembershipComponent().getAdministrators().contains(playerId)) {
                return true;
            }
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);
        if (session != null) {
            //session.send(new NotificationMessageComposer("alert", "PERMCHECK - "+this.room.getId()));
            if (session.getPlayer().getPermissions().getRank().modTool()) {
                return true;
            }

            if (session.getPlayer().getPermissions().getRank().roomFullControl()) {
                return true;
            }

            if(session.getPlayer().getPermissions().getRank().roomFullAcessPublic() && this.room.getData().getOwnerId() == 0)
            {
                //session.send(new NotificationMessageComposer("alert", "PERM1"));
                return true;
            }
        }

        return this.room.getData().getOwnerId() == playerId || this.rights.contains(playerId);
    }

    public void removeRights(int playerId) {
        if (this.rights.contains(playerId)) {
            this.rights.remove(rights.indexOf(playerId));
            RightsDao.delete(playerId, room.getId());
        }
    }

    public void addRights(int playerId) {
        this.rights.add(playerId);
        RightsDao.add(playerId, this.room.getId());
    }

    public void addBan(int playerId, String playerName, int length) {
        this.bannedPlayers.add(new RoomBan(playerId, playerName, length != -1 ? length * 2 : -1));
    }

    public void addMute(int playerId, int minutes) {
        this.mutedPlayers.add(new RoomMute(playerId, (minutes * 60) * 2));
    }

    public void updateMute(int playerId, int minutes) {
        for (RoomMute mute : this.mutedPlayers.stream().filter(x -> x != null && x.getPlayerId() == playerId).collect(Collectors.toList())) {
            mute.setTicksLeft(minutes);
        }
    }

    public boolean hasBan(int userId) {
        for (RoomBan ban : this.bannedPlayers) {
            if (ban.getPlayerId() == userId) {
                return true;
            }
        }

        return false;
    }

    public void removeBan(int playerId) {
        int indexToRemove = -1;

        for (RoomBan ban : this.bannedPlayers) {
            if (ban.getPlayerId() == playerId) {
                indexToRemove = this.bannedPlayers.indexOf(ban);
            }
        }

        if (indexToRemove != -1) {
            this.bannedPlayers.remove(indexToRemove);
        }
    }

    public boolean hasMute(int playerId) {
        for (RoomMute mute : this.mutedPlayers) {
            if (mute.getPlayerId() == playerId) {
                return true;
            }
        }

        return false;
    }

    public int getMuteTime(int playerId) {
        for (RoomMute mute : this.mutedPlayers) {
            if (mute.getPlayerId() == playerId) {
                return (mute.getTicksLeft() / 2);
            }
        }

        return 0;
    }

    public void tick() {
        List<RoomBan> bansToRemove = new ArrayList<>();
        List<RoomMute> mutesToRemove = new ArrayList<>();

        for (RoomBan ban : this.bannedPlayers) {
            if (ban.getTicksLeft() <= 0 && !ban.isPermanent()) {
                bansToRemove.add(ban);
            }

            ban.decreaseTicks();
        }

        for (RoomMute mute : this.mutedPlayers) {
            if (mute.getTicksLeft() <= 0) {
                mutesToRemove.add(mute);
            }

            mute.decreaseTicks();
        }


        for (RoomBan ban : bansToRemove) {
            this.bannedPlayers.remove(ban);
        }

        for (RoomMute mute : mutesToRemove) {
            this.mutedPlayers.remove(mute);
        }

        bansToRemove.clear();
        mutesToRemove.clear();
    }

    public List<RoomBan> getBannedPlayers() {
        return this.bannedPlayers;
    }

    public List<Integer> getAll() {
        return this.rights;
    }

    public Room getRoom() {
        return this.room;
    }
}
