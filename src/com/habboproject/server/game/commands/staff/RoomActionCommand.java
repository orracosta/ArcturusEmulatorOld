package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.components.types.inventory.InventoryBot;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.List;


public class RoomActionCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            return;
        }

        final String action = params[0];

        switch (action) {
            case "effect":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                int effectId = Integer.parseInt(params[1]);

                for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.applyEffect(new PlayerEffect(effectId, 0));
                }
                break;

            case "say":
                String msg = this.merge(params, 1);

                for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.getRoom().getEntities().broadcastMessage(new TalkMessageComposer(playerEntity.getId(), msg, RoomManager.getInstance().getEmotions().getEmotion(msg), 0));
                }
                break;

            case "dance":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                int danceId = Integer.parseInt(params[1]);

                for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.setDanceId(danceId);
                    playerEntity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(playerEntity.getId(), danceId));
                }
                break;

            case "sign":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.addStatus(RoomEntityStatus.SIGN, String.valueOf(params[1]));

                    playerEntity.markDisplayingSign();
                    playerEntity.markNeedsUpdate();
                }
                break;

            case "bots":
                if (!StringUtils.isNumeric(params[1])) {
                    return;
                }

                int count = Integer.parseInt(params[1]);
                final Position entityPosition = client.getPlayer().getEntity().getPosition();

                if (count > 1000) {
                    count = 1000;
                } else if (count < 0) {
                    count = 1;
                }

                List<RoomEntity> addedEntities = Lists.newArrayList();

                for (int i = 0; i < count; i++) {
                    final BotEntity botEntity = client.getPlayer().getEntity().getRoom().getBots().addBot(new InventoryBot(0 - (i + 1), client.getPlayer().getId(), client.getPlayer().getData().getUsername(), client.getPlayer().getData().getUsername() + "Minion" + i, client.getPlayer().getData().getFigure(), client.getPlayer().getData().getGender(), client.getPlayer().getData().getMotto(), "mimic"), entityPosition.getX(), entityPosition.getY(), entityPosition.getZ());

                    if(botEntity != null) {
                        addedEntities.add(botEntity);
                    }
                }

                client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(addedEntities));
                break;

            case "handitem":
                int handItem = Integer.parseInt(params[1]);

                for (PlayerEntity playerEntity : client.getPlayer().getEntity().getRoom().getEntities().getPlayerEntities()) {
                    playerEntity.carryItem(handItem, false);
                }

                break;
        }
    }

    @Override
    public String getPermission() {
        return "roomaction_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.roomaction.description");
    }
}
