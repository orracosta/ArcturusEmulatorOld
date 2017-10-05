package com.habboproject.server.game.commands.staff.cache;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.groups.GroupDao;
import org.apache.commons.lang.StringUtils;

public class ReloadGroupCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        if (!StringUtils.isNumeric(params[0])) {
            return;
        }

        final int groupId = Integer.parseInt(params[0]);

        final GroupData groupData = GroupManager.getInstance().getData(groupId);
        final GroupData newGroupData = GroupDao.getDataById(groupId);

        if (groupData.getRoomId() != newGroupData.getRoomId()) {
            if (RoomManager.getInstance().isActive(groupData.getRoomId())) {
                Room oldRoom = RoomManager.getInstance().get(groupData.getRoomId());

                if (oldRoom != null) {
                    oldRoom.setIdleNow();
                }
            }

            if (RoomManager.getInstance().isActive(newGroupData.getRoomId())) {
                Room newRoom = RoomManager.getInstance().get(newGroupData.getRoomId());

                if (newRoom != null) {
                    newRoom.setIdleNow();
                }
            }
        }

        groupData.setCanMembersDecorate(newGroupData.canMembersDecorate());
        groupData.setOwnerId(newGroupData.getOwnerId());
        groupData.setRoomId(newGroupData.getRoomId());
        groupData.setBadge(newGroupData.getBadge());
        groupData.setColourA(newGroupData.getColourA());
        groupData.setColourB(newGroupData.getColourB());
        groupData.setDescription(newGroupData.getDescription());
        groupData.setTitle(newGroupData.getTitle());
        groupData.setType(newGroupData.getType());

        client.send(new AlertMessageComposer(Locale.getOrDefault("command.reloadgroup.done", "Group data reloaded successfully!")));
    }

    @Override
    public String getPermission() {
        return "reloadgroup_command";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
