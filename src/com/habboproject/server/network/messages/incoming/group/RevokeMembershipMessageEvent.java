package com.habboproject.server.network.messages.incoming.group;

import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupMembersMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreControllerMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;


public class
        RevokeMembershipMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int groupId = msg.readInt();
        int playerId = msg.readInt();

        Group group = GroupManager.getInstance().get(groupId);

        if (group == null)
            return;

        if (playerId == group.getData().getOwnerId())
            return;

        GroupMember groupMember = group.getMembershipComponent().getMembers().get(client.getPlayer().getId());

        if (groupMember == null) {
            return;
        }

        if (!groupMember.getAccessLevel().isAdmin() && playerId != client.getPlayer().getId())
            return;

        group.getMembershipComponent().removeMembership(playerId);

        List<RoomItem> itemsToRemove = Lists.newArrayList();


        if (RoomManager.getInstance().isActive(group.getData().getRoomId())) {
            final Room room = RoomManager.getInstance().get(group.getData().getRoomId());


            for (RoomItemFloor floorItem : room.getItems().getFloorItems().values()) {
                if (floorItem.getOwner() == playerId) {
                    itemsToRemove.add(floorItem);
                }
            }

            for (RoomItemWall wallItem : room.getItems().getWallItems().values()) {
                if (wallItem.getOwner() == playerId) {
                    itemsToRemove.add(wallItem);
                }
            }
        }

        if (playerId == client.getPlayer().getId()) {
            if (client.getPlayer().getData().getFavouriteGroup() == groupId) {
                client.getPlayer().getData().setFavouriteGroup(0);
                client.getPlayer().getData().save();
            }

            if (client.getPlayer().getGroups().contains(groupId)) {
                client.getPlayer().getGroups().remove(client.getPlayer().getGroups().indexOf(groupId));
                client.send(group.composeInformation(true, client.getPlayer().getId()));
            }

            if (client.getPlayer().getEntity() != null && client.getPlayer().getEntity().getRoom().getId() == group.getData().getRoomId()) {
                client.send(new YouAreControllerMessageComposer(0));

                client.getPlayer().getEntity().removeStatus(RoomEntityStatus.CONTROLLER);
                client.getPlayer().getEntity().addStatus(RoomEntityStatus.CONTROLLER, "0");
                client.getPlayer().getEntity().markNeedsUpdate();
            }

            this.ejectItems(itemsToRemove, client);
        } else {
            if (PlayerManager.getInstance().isOnline(playerId)) {
                Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

                if (session != null) {
                    if (session.getPlayer().getData().getFavouriteGroup() == groupId) {
                        session.getPlayer().getData().setFavouriteGroup(0);
                        session.getPlayer().getData().save();
                    }

                    if (session.getPlayer().getEntity() != null && session.getPlayer().getEntity().getRoom().getId() == group.getData().getRoomId()) {
                        session.send(new YouAreControllerMessageComposer(0));

                        session.getPlayer().getEntity().removeStatus(RoomEntityStatus.CONTROLLER);
                        session.getPlayer().getEntity().addStatus(RoomEntityStatus.CONTROLLER, "0");
                        session.getPlayer().getEntity().markNeedsUpdate();
                    }

                    if (session.getPlayer().getGroups().contains(groupId)) {
                        session.getPlayer().getGroups().remove(session.getPlayer().getGroups().indexOf(groupId));
                    }

                    this.ejectItems(itemsToRemove, session);
                } else {
                    this.ejectItems(itemsToRemove, null);
                }
            }

            client.send(new GroupMembersMessageComposer(group.getData(), 0, new ArrayList<>(group.getMembershipComponent().getMembersAsList()), 0, "", group.getMembershipComponent().getAdministrators().contains(client.getPlayer().getId())));
        }

        itemsToRemove.clear();
    }

    private void ejectItems(List<RoomItem> items, Session player) {
        for (RoomItem roomItem : items) {
            if (roomItem instanceof RoomItemFloor) {
                roomItem.getRoom().getItems().removeItem(((RoomItemFloor) roomItem), player);
            } else if (roomItem instanceof RoomItemWall) {
                roomItem.getRoom().getItems().removeItem(((RoomItemWall) roomItem), player, true);
            }
        }
    }
}
