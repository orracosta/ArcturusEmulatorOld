package com.habboproject.server.network.messages.incoming.room.settings;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.handshake.HomeRoomMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.BotInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.PetInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.bots.RoomBotDao;
import com.habboproject.server.storage.queries.pets.RoomPetDao;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.rooms.RoomDao;

import java.util.ArrayList;
import java.util.List;


public class DeleteRoomMessageEvent implements Event {

    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        PlayerEntity entity = client.getPlayer().getEntity();

        if (entity == null)
            return;

        Room room = entity.getRoom();

        if (room == null || (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl())) {
            return;
        }

        final int roomId = room.getId();

        if (GroupManager.getInstance().getGroupByRoomId(room.getId()) != null) {
            client.send(new AlertMessageComposer(Locale.getOrDefault("room.delete.error.group", "You cannot delete a room with a group, please delete the group first!")));
            return;
        }

        List<RoomItem> itemsToRemove = new ArrayList<>();
        itemsToRemove.addAll(room.getItems().getFloorItems().values());
        itemsToRemove.addAll(room.getItems().getWallItems().values());

        for (RoomItem item : itemsToRemove) {
            if (item instanceof RoomItemFloor) {
                room.getItems().removeItem((RoomItemFloor) item, client);
            } else if (item instanceof RoomItemWall) {
                room.getItems().removeItem((RoomItemWall) item, client, true);
            }
        }

        for (BotEntity bot : room.getEntities().getBotEntities()) {
            InventoryBot inventoryBot = new InventoryBot(bot.getBotId(), bot.getData().getOwnerId(), bot.getData().getOwnerName(), bot.getUsername(), bot.getFigure(), bot.getGender(), bot.getMotto(), bot.getData().getBotType());
            client.getPlayer().getBots().addBot(inventoryBot);

            RoomBotDao.setRoomId(0, inventoryBot.getId());
        }

        for (PetEntity pet : room.getEntities().getPetEntities()) {
            client.getPlayer().getPets().addPet(pet.getData());

            RoomPetDao.updatePet(0, 0, 0, pet.getData().getId());
        }

        RoomManager.getInstance().forceUnload(room.getId());
        RoomManager.getInstance().removeData(room.getId());

        if (PlayerManager.getInstance().isOnline(room.getData().getOwnerId())) {
            Session owner = NetworkManager.getInstance().getSessions().getByPlayerId(room.getData().getOwnerId());

            if (owner != null && owner.getPlayer() != null && owner.getPlayer().getRooms() != null) {
                if (owner.getPlayer().getRooms().contains(room.getId())) {
                    owner.getPlayer().getRooms().remove(owner.getPlayer().getRooms().indexOf(room.getId()));
                }
            }
        }

//        if (GroupManager.getInstance().getGroupByRoomId(room.getId()) != null) {
//            Group group = GroupManager.getInstance().getGroupByRoomId(room.getId());
//
//            for (Integer groupMemberId : group.getMembershipComponent().getMembers().keySet()) {
//                Session groupMemberSession = NetworkManager.getInstance().getSessions().getByPlayerId(groupMemberId);
//
//                if (groupMemberSession != null && groupMemberSession.getPlayer() != null) {
//                    groupMemberSession.getPlayer().getGroups().remove(new Integer(group.getId()));
//
//                    if (groupMemberSession.getPlayer().getData().getFavouriteGroup() == group.getId()) {
//                        groupMemberSession.getPlayer().getData().setFavouriteGroup(0);
//
//                        if (groupMemberSession.getPlayer().getEntity() != null) {
//                            groupMemberSession.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(client.getPlayer().getEntity().getId()));
//                            groupMemberSession.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(client.getPlayer().getEntity()));
//                        }
//                    }
//                }
//            }
//
//            GroupManager.getInstance().removeGroup(group.getId());
//        }

        if (client.getPlayer().getSettings().getHomeRoom() == roomId) {
            client.send(new HomeRoomMessageComposer(client.getPlayer().getSettings().getHomeRoom(), 0));
            client.getPlayer().getSettings().setHomeRoom(0);
        }

        PlayerDao.resetHomeRoom(roomId);

        client.getLogger().debug("Room deleted: " + room.getId() + " by " + client.getPlayer().getId() + " / " + client.getPlayer().getData().getUsername());
        RoomDao.deleteRoom(room.getId());

        client.send(new UpdateInventoryMessageComposer());
        client.send(new PetInventoryMessageComposer(client.getPlayer().getPets().getPets()));
        client.send(new BotInventoryMessageComposer(client.getPlayer().getBots().getBots()));

        itemsToRemove.clear();
    }
}
