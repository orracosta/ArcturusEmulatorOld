package com.habboproject.server.game.rooms.objects.items.types.floor.others;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.gifts.GiftData;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.habboproject.server.utilities.JsonFactory;


public class GiftFloorItem extends RoomItemFloor {
    private GiftData giftData;
    private boolean isOpened = false;

    public GiftFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) throws Exception {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        this.giftData = JsonFactory.getInstance().fromJson(data.split("GIFT::##")[1], GiftData.class);

        if(!CatalogManager.getInstance().getGiftBoxesNew().contains(giftData.getSpriteId()) && !CatalogManager.getInstance().getGiftBoxesOld().contains(giftData.getSpriteId())) {
            throw new Exception("some sad fucker used an exploit, bye bye gift.");
        }
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        GiftData giftData = this.getGiftData();
        PlayerAvatar purchaser = PlayerManager.getInstance().getAvatarByPlayerId(giftData.getSenderId(), PlayerAvatar.USERNAME_FIGURE);

        msg.writeInt(giftData.getWrappingPaper() * 1000 + giftData.getDecorationType());
        msg.writeInt(1);

        msg.writeInt(6);
        msg.writeString("EXTRA_PARAM");
        msg.writeString("");
        msg.writeString("MESSAGE");
        msg.writeString(giftData.getMessage());
        msg.writeString("PURCHASER_NAME");
        msg.writeString(purchaser.getUsername());
        msg.writeString("PURCHASER_FIGURE");
        msg.writeString(purchaser.getFigure());
        msg.writeString("PRODUCT_CODE");
        msg.writeString("");
        msg.writeString("state");
        msg.writeString(this.isOpened() ? "1" : "0");
    }

    @Override
    public boolean onInteract(RoomEntity entity, int state, boolean isWiredTrigger) {
        this.isOpened = true;

        this.sendUpdate();
        this.getRoom().getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(this.getVirtualId(), 1200));
//        this.getRoom().getEntities().broadcastMessage(new SendFloorItemMessageComposer(this));

        this.isOpened = false;
        return true;
    }

    public GiftData getGiftData() {
        return giftData;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
