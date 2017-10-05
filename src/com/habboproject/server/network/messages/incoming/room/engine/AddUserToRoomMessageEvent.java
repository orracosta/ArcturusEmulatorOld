package com.habboproject.server.network.messages.incoming.room.engine;

import com.habboproject.server.api.game.players.data.components.bots.PlayerBot;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.polls.PollManager;
import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.bots.NewbieAI;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerEnterRoom;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.tiles.RoomTileState;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.group.GroupBadgesMessageComposer;
import com.habboproject.server.network.messages.outgoing.misc.LinkEventMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.*;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomEntryInfoMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.FloorItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.WallItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.FloodFilterMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.pets.horse.HorseFigureMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.polls.InitializePollMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.polls.QuickPollMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.polls.QuickPollResultsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.settings.RoomVisualizationSettingsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;

import java.util.HashMap;
import java.util.Map;

public class AddUserToRoomMessageEvent implements Event {
    public void handle(Session client, MessageEvent msg) {
        PlayerEntity avatar = client.getPlayer().getEntity();

        if (avatar == null) {
            return;
        }

        Room room = avatar.getRoom();

        if (room == null) {
            return;
        }

        if (!room.getProcess().isActive()) {
            room.getProcess().start();
        }

        if (!room.getItemProcess().isActive()) {
            room.getItemProcess().start();
        }

        if (client.getPlayer().getRoomFloodTime() >= 1) {
            client.sendQueue(new FloodFilterMessageComposer(client.getPlayer().getRoomFloodTime()));
        }

        Map<Integer, String> groupsInRoom = new HashMap<>();

        for (PlayerEntity playerEntity : room.getEntities().getPlayerEntities()) {
            if (playerEntity.getPlayer() != null && playerEntity.getPlayer().getData() != null) {
                if (playerEntity.getPlayer().getData().getFavouriteGroup() != 0) {
                    GroupData groupData = GroupManager.getInstance().getData(playerEntity.getPlayer().getData().getFavouriteGroup());

                    if (groupData == null)
                        continue;

                    groupsInRoom.put(playerEntity.getPlayer().getData().getFavouriteGroup(), groupData.getBadge());
                }
            }
        }

        client.sendQueue(new GroupBadgesMessageComposer(groupsInRoom));
        client.sendQueue(new RoomEntryInfoMessageComposer(room.getId(), room.getData().getOwnerId() == client.getPlayer().getId() || client.getPlayer().getPermissions().getRank().roomFullControl()));
        client.sendQueue(new AvatarsMessageComposer(room));

        if (room.getEntities().getAllEntities().size() > 0)
            client.sendQueue(new AvatarUpdateMessageComposer(room.getEntities().getAllEntities().values()));

        for (RoomEntity av : room.getEntities().getAllEntities().values()) {
            if (av.getCurrentEffect() != null) {
                client.sendQueue(new ApplyEffectMessageComposer(av.getId(), av.getCurrentEffect().getEffectId()));
            }

            if (av.getDanceId() != 0) {
                client.sendQueue(new DanceMessageComposer(av.getId(), av.getDanceId()));
            }

            if (av.getHandItem() != 0) {
                client.sendQueue(new HandItemMessageComposer(av.getId(), av.getHandItem()));
            }

            if (av.isIdle()) {
                client.sendQueue(new IdleStatusMessageComposer(av.getId(), true));
            }

            if (av.getAI() != null) {
                if (av instanceof PetEntity && ((PetEntity) av).getData().getTypeId() == 15) {
                    client.send(new HorseFigureMessageComposer(((PetEntity) av)));
                }

                av.getAI().onPlayerEnter(client.getPlayer().getEntity());
            }
        }

        client.sendQueue(new RoomVisualizationSettingsMessageComposer(room.getData().getHideWalls(), room.getData().getWallThickness(), room.getData().getFloorThickness()));

        client.getPlayer().getMessenger().sendStatus(true, true);

        client.sendQueue(new FloorItemsMessageComposer(room));
        client.sendQueue(new WallItemsMessageComposer(room));

        WiredTriggerEnterRoom.executeTriggers(client.getPlayer().getEntity());

        if (PollManager.getInstance().roomHasPoll(room.getId())) {
            Poll poll = PollManager.getInstance().getPollByRoomId(room.getId());

            if (!poll.getPlayersAnswered().contains(client.getPlayer().getId())) {
                client.send(new InitializePollMessageComposer(poll.getPollId(), poll.getPollTitle(), poll.getThanksMessage()));
            }
        }

        if (room.getQuestion() != null) {
            client.send(new QuickPollMessageComposer(room.getQuestion()));

            if (room.getYesVotes().contains(client.getPlayer().getId()) || room.getNoVotes().contains(client.getPlayer().getId())) {
                client.send(new QuickPollResultsMessageComposer(room.getYesVotes().size(), room.getNoVotes().size()));
            }
        }

        /*if (client.getPlayer().getData().getNewbieStep().equals("1") && room.getData().getOwnerId() == client.getPlayer().getId()) {
            Position startPosition = new Position(room.getModel().getDoorX(), room.getModel().getDoorY(), room.getModel().getDoorZ());

            double height = room.getMapping().getTile(startPosition.getX(), startPosition.getY()).getWalkHeight();

            RoomTile tile = room.getMapping().getTile(startPosition.getX(), startPosition.getY());

            if (tile != null) {
                InventoryBot bot = new InventoryBot(1, 0, "", "Frank",
                        "hr-3194-38-36.hd-180-1.ch-220-1408.lg-285-73.sh-906-90.ha-3129-73.fa-1206-73.cc-3039-73",
                        "M", "", "newbie");

                BotEntity botEntity = null;
                if ((botEntity = room.getBots().getBotByName("Frank")) == null || botEntity.getBotId() != 1) { // fix para o "dual frank"
                    botEntity = room.getBots().addBot(bot, startPosition.getX(), startPosition.getY(), height);
                    room.getEntities().broadcastMessage(new AvatarsMessageComposer(botEntity));

                    botEntity.getAI().onAddedToRoom();

                    botEntity.getData().setMode("relaxed");

                    ((NewbieAI) botEntity.getAI()).setNewbieEntity(client.getPlayer().getEntity());
                    botEntity.getAI().onPlayerEnter(client.getPlayer().getEntity());
                }
            }
        }*/

        client.flush();
        avatar.markNeedsUpdate();
    }
}
