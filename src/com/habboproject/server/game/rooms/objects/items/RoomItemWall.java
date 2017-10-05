package com.habboproject.server.game.rooms.objects.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateWallItemMessageComposer;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import org.apache.commons.lang.StringUtils;


public abstract class RoomItemWall extends RoomItem {
    private String wallPosition;
    private String extraData;

    private ItemDefinition itemDefinition;

    public RoomItemWall(long id, int itemId, Room room, int owner, String position, String data) {
        super(id, null, room);

        this.itemId = itemId;

        this.ownerId = owner;
        this.wallPosition = position;
        this.extraData = data;
    }

    @Override
    public void serialize(IComposer msg) {
        msg.writeString(this.getVirtualId());
        msg.writeInt(this.getDefinition().getSpriteId());
        msg.writeString(this.getWallPosition());

        msg.writeString(this.getExtraData());
        msg.writeInt(!this.getDefinition().getInteraction().equals("default") ? 1 : 0);
        msg.writeInt(-1);
        msg.writeInt(-1);

        //msg.writeInt(this.getRoom().getData().getOwnerId());
        msg.writeInt(1);
    }

    @Override
    public boolean toggleInteract(boolean state) {
        if (this.getDefinition().getInteractionCycleCount() > 1) {
            if (this.getExtraData().isEmpty() || this.getExtraData().equals(" ")) {
                this.setExtraData("0");
            }

            if(!StringUtils.isNumeric(this.getExtraData())) {
                return false;
            }

            int i = Integer.parseInt(this.getExtraData()) + 1;

            if (i > (this.getDefinition().getInteractionCycleCount() - 1)) { // take one because count starts at 0 (0, 1) = count(2)
                this.setExtraData("0");
            } else {
                this.setExtraData(i + "");
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void sendUpdate() {
        Room r = this.getRoom();

        if (r != null && r.getEntities() != null) {
            r.getEntities().broadcastMessage(new UpdateWallItemMessageComposer(this, this.ownerId, this.getRoom().getData().getOwner()));
        }
    }

    public void save() {
        this.saveData();
    }

    @Override
    public void saveData() {
        RoomItemDao.saveData(this.getId(), this.extraData);
    }

    @Override
    public ItemDefinition getDefinition() {
        if (this.itemDefinition == null) {
            this.itemDefinition = ItemManager.getInstance().getDefinition(this.getItemId());
        }

        return this.itemDefinition;
    }

    @Override
    public int getItemId() {
        return itemId;
    }

    @Override
    public int getOwner() {
        return ownerId;
    }

    public void setPosition(String position) {
        this.wallPosition = position;
    }

    public String getWallPosition() {
        return this.wallPosition;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String data) {
        this.extraData = data;
    }
}
