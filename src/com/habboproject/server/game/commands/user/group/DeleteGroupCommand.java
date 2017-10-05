package com.habboproject.server.game.commands.user.group;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.google.common.collect.Lists;

import java.util.List;

public class DeleteGroupCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (client.getPlayer().getId() != client.getPlayer().getEntity().getRoom().getData().getOwnerId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("command.deletegroup.permission", "You don't have permission to delete this group!")));
            return;
        }

        if (!client.getPlayer().isDeletingGroup() || (Comet.getTime() - client.getPlayer().getDeletingGroupAttempt()) >= 30) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("command.deletegroup.confirm", "Are you sure you want to delete this group? All items in the room will be returned to the rightful owner and the group will be deleted forever.<br><br>Use the command :deletegroup again to confirm!")));

            client.getPlayer().setDeletingGroup(true);
            client.getPlayer().setDeletingGroupAttempt(Comet.getTime());
            return;
        }

        final Room room = client.getPlayer().getEntity().getRoom();

        if (GroupManager.getInstance().getGroupByRoomId(room.getId()) != null) {
            Group group = GroupManager.getInstance().getGroupByRoomId(room.getId());

            for (Integer groupMemberId : group.getMembershipComponent().getMembers().keySet()) {
                Session groupMemberSession = NetworkManager.getInstance().getSessions().getByPlayerId(groupMemberId);

                List<RoomItem> floorItemsOwnedByPlayer = Lists.newArrayList();

                if (groupMemberId != group.getData().getOwnerId()) {
                    for (RoomItemFloor floorItem : room.getItems().getFloorItems().values()) {
                        if (floorItem.getOwner() == groupMemberId) {
                            floorItemsOwnedByPlayer.add(floorItem);
                        }
                    }

                    for (RoomItemWall wallItem : room.getItems().getWallItems().values()) {
                        if (wallItem.getOwner() == groupMemberId) {
                            floorItemsOwnedByPlayer.add(wallItem);
                        }
                    }
                }

                if (groupMemberSession != null && groupMemberSession.getPlayer() != null) {
                    groupMemberSession.getPlayer().getGroups().remove(new Integer(group.getId()));

                    if (groupMemberSession.getPlayer().getData().getFavouriteGroup() == group.getId()) {
                        groupMemberSession.getPlayer().getData().setFavouriteGroup(0);
                    }

                    for (RoomItem roomItem : floorItemsOwnedByPlayer) {
                        if (roomItem instanceof RoomItemFloor)
                            room.getItems().removeItem(((RoomItemFloor) roomItem), groupMemberSession);
                        else if (roomItem instanceof RoomItemWall)
                            room.getItems().removeItem(((RoomItemWall) roomItem), groupMemberSession, true);
                    }
                } else {
                    for (RoomItem roomItem : floorItemsOwnedByPlayer) {
                        RoomItemDao.removeItemFromRoom(roomItem.getId(), groupMemberId);
                    }
                }

                floorItemsOwnedByPlayer.clear();
            }

            client.send(new AlertMessageComposer(Locale.getOrDefault("command.deletegroup.done", "The group was deleted successfully.")));
            GroupManager.getInstance().removeGroup(group.getId());

            room.setGroup(null);

            room.getData().setGroupId(0);
            room.getData().save();

            room.setIdleNow();
        } else {
            client.send(new AlertMessageComposer(Locale.getOrDefault("command.deletegroup.nogroup", "This room doesn't have a group to delete!")));
        }
    }

    @Override
    public String getPermission() {
        return "deletegroup_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.deletegroup.description");
    }
}
