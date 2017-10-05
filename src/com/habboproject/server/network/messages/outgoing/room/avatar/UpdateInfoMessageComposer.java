package com.habboproject.server.network.messages.outgoing.room.avatar;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class UpdateInfoMessageComposer extends MessageComposer {

    private final int playerId;
    private final String figure;
    private final String gender;
    private final String motto;
    private final int achievementPoints;

    public UpdateInfoMessageComposer(final int playerId, final String figure, final String gender, final String motto, final int achievementPoints) {
        this.playerId = playerId;
        this.figure = figure;
        this.gender = gender;
        this.motto = motto;
        this.achievementPoints = achievementPoints;
    }

    public UpdateInfoMessageComposer(RoomEntity entity) {
        this(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
    }

    public UpdateInfoMessageComposer(int id, RoomEntity entity) {
        this(id, entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
    }

    @Override
    public short getId() {
        return Composers.UserChangeMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);
        msg.writeString(figure);
        msg.writeString(gender.toLowerCase());
        msg.writeString(motto);
        msg.writeInt(achievementPoints);
    }

//    public static Composer compose(RoomEntity entity) {
//        return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//    }
//
//    public static Composer compose(boolean isMe, RoomEntity entity) {
//        if (!isMe) {
//            return compose(entity.getId(), entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        } else {
//            return compose(-1, entity.getFigure(), entity.getGender(), entity.getMotto(), (entity instanceof PlayerEntity) ? ((PlayerEntity) entity).getPlayer().getData().getAchievementPoints() : 0);
//        }
//    }
}
