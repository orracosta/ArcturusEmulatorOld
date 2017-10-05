package com.habboproject.server.game.rooms.objects.entities.types.ai.bots;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.AbstractBotAI;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.outgoing.room.avatar.DanceMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.utilities.RandomInteger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MinionAI extends AbstractBotAI {
    private final static Map<String, Consumer<BotEntity>> speechCommands = new HashMap<String, Consumer<BotEntity>>() {{
        put("minions leave", (entity) -> entity.leaveRoom(false, false, false));

        put("minions dance", (entity) -> {
            int danceId = RandomInteger.getRandom(1, 4);

            entity.setDanceId(danceId);
            entity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(entity.getId(), danceId));
        });

        put("minions stop dancing", (entity) -> {
            entity.setDanceId(0);
            entity.getRoom().getEntities().broadcastMessage(new DanceMessageComposer(entity.getId(), 0));
        });

        put("minions sit", (entity) -> {
            entity.addStatus(RoomEntityStatus.SIT, "0.5");
            entity.markNeedsUpdate();

            entity.getData().setMode("relaxed");
        });

        put("minions stand", (entity) -> {
            entity.removeStatus(RoomEntityStatus.SIT);
            entity.markNeedsUpdate();

            entity.getData().setMode("default");
        });

        put("minions follow", (entity) -> {
            PlayerEntity playerEntity = entity.getRoom().getEntities().getEntityByPlayerId(entity.getData().getOwnerId());

            if(playerEntity != null) {
                playerEntity.getFollowingEntities().add(entity);
            }
        });
    }};

    public MinionAI(RoomEntity entity) {
        super(entity);
    }

    @Override
    public boolean onTalk(PlayerEntity entity, String message) {
        if (message.startsWith(":")) return false;

        if (entity.getPlayerId() != ((BotEntity) this.getEntity()).getData().getOwnerId()) {
            return false;
        }

        if (speechCommands.containsKey(message.toLowerCase())) {
            speechCommands.get(message.toLowerCase()).accept(((BotEntity) this.getEntity()));
        } else {
            this.getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(this.getEntity().getId(), message, ChatEmotion.NONE, 2));
        }

        return false;
    }

    @Override
    public boolean onPlayerLeave(PlayerEntity playerEntity) {
        if (playerEntity.getPlayerId() == ((BotEntity) this.getEntity()).getData().getOwnerId()) {
            this.getEntity().leaveRoom(false, false, false);
        }

        return false;
    }
}
