package com.habboproject.server.game.rooms.objects.items;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.types.DefaultFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.AdjustableHeightFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.MagicStackFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.boutique.MannequinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballGateFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.jukebox.SoundMachineFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.WiredFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.items.UpdateFloorItemMessageComposer;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.storage.queue.types.ItemStorageQueue;
import com.habboproject.server.utilities.attributes.Collidable;
import com.habboproject.server.utilities.comparators.PositionComporator;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;

public abstract class RoomItemFloor extends RoomItem implements Collidable {
    private String extraData;

    private ItemDefinition itemDefinition;
    private RoomEntity collidedEntity;
    private boolean hasQueuedSave;

    private int lastDirection;

    public RoomItemFloor(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, new Position(x, y, z), room);

        this.itemId = itemId;
        this.ownerId = this.getRoom().getGroup() == null ? this.getRoom().getData().getOwnerId() : owner;
        this.ownerName = this.getRoom().getGroup() == null ? this.getRoom().getData().getOwner() : PlayerDao.getUsernameByPlayerId(this.ownerId);
        this.groupId = groupId;
        this.rotation = rotation;
        this.extraData = data;
        this.lastDirection = 0;
        this.needsUpdate = false;
    }

    public void serialize(IComposer msg, boolean isNew) {
        msg.writeInt(this.getVirtualId());
        msg.writeInt(this.getDefinition().getSpriteId());
        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeInt(this.getRotation());

        msg.writeString(this instanceof MagicStackFloorItem ? ((MagicStackFloorItem)this).getMagicHeight() : this.getPosition().getZ());
        msg.writeString(this instanceof AdjustableHeightFloorItem ? ((AdjustableHeightFloorItem)this).getCurrentHeight() : this.getDefinition().getHeight());

        this.compose(msg, isNew);

        if (!(this instanceof MannequinFloorItem)) {
            msg.writeInt(-1);
            msg.writeInt(!(this instanceof DefaultFloorItem) && !(this instanceof SoundMachineFloorItem) ? 1 : 0);
            msg.writeInt(this.ownerId);

            if (isNew) {
                msg.writeString(this.ownerName);
            }
        }
    }

    @Override
    public void serialize(IComposer msg) {
        this.serialize(msg, false);
    }

    public ItemDefinition getDefinition() {
        if (this.itemDefinition == null) {
            this.itemDefinition = ItemManager.getInstance().getDefinition(this.getItemId());
        }

        return this.itemDefinition;
    }

    public double getTotalHeight() {
        double startHeight = this.getPosition().getZ();

        if (this instanceof AdjustableHeightFloorItem) {
            return startHeight + ((AdjustableHeightFloorItem)this).getCurrentHeight();
        }

        if (this instanceof MagicStackFloorItem) {
            return ((MagicStackFloorItem)this).getMagicHeight();
        }

        return startHeight + this.getDefinition().getHeight();
    }

    public void compose(IComposer msg, boolean isNew) {
        if (this.getDefinition().isAdFurni()) {
            msg.writeInt(0);
            msg.writeInt(1);
            if (!this.extraData.equals("") && this.extraData.contains(String.valueOf('\t'))) {
                String[] adsData = this.extraData.split(String.valueOf('\t'));
                int count = adsData.length;
                msg.writeInt(count / 2);
                int i = 0;
                while (i <= count - 1) {
                    msg.writeString(adsData[i]);
                    ++i;
                }
            } else {
                msg.writeInt(0);
            }
        } else if (this.getDefinition().getItemName().contains("yttv")) {
            msg.writeInt(0);
            msg.writeInt(1);
            msg.writeInt(1);
            msg.writeString("THUMBNAIL_URL");
            msg.writeString("/deliver/" + (this.hasAttribute("video") ? this.getAttribute("video") : "pv-MvYijLjs"));
        } else if (this.getLimitedEditionItemData() != null) {
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(true);
            msg.writeBoolean(false);
            msg.writeString(this.getExtraData());
            msg.writeInt(this.getLimitedEditionItemData().getLimitedRare());
            msg.writeInt(this.getLimitedEditionItemData().getLimitedRareTotal());
        } else {
            msg.writeInt(1);
            msg.writeInt(0);
            msg.writeString(this instanceof FootballGateFloorItem ? "" : (this instanceof WiredFloorItem ? "0" : (this instanceof SoundMachineFloorItem ? (((SoundMachineFloorItem)this).getState() ? "1" : "0") : this.getExtraData())));
        }
    }

    public void onItemAddedToStack(RoomItemFloor floorItem) {
        // override me
    }

    public void onEntityPreStepOn(RoomEntity entity) {
        // override me
    }

    public void onEntityStepOn(RoomEntity entity) {
        // override me
    }

    public void onEntityStepOff(RoomEntity entity) {
        // override me
    }

    public void onEntityPostStepOn(RoomEntity entity) {
        // override me
    }

    public void onPositionChanged(Position newPosition) {
        // override me
    }

    public boolean isMovementCancelled(RoomEntity entity) {
        return false;
    }

    @Override
    public boolean toggleInteract(boolean state) {
        if (!state) {
            if (!(this instanceof WiredFloorItem))
                this.setExtraData("0");

            return true;
        }

        if (!StringUtils.isNumeric(this.getExtraData())) {
            return true;
        }

        if (this.getDefinition().getInteractionCycleCount() > 1) {
            if (this.getExtraData().isEmpty() || this.getExtraData().equals(" ")) {
                this.setExtraData("0");
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
    public void save() {
        RoomItemDao.saveItem(this);
    }

    @Override
    public void saveData() {
        RoomItemDao.saveData(this.getId(), this.getDataObject());
    }

    @Override
    public void sendUpdate() {
        Room r = this.getRoom();

        if (r != null) {
            r.getEntities().broadcastMessage(new UpdateFloorItemMessageComposer(this));
        }
    }

    public List<RoomItemFloor> getItemsOnStack() {
        List<RoomItemFloor> floorItems = Lists.newArrayList();

        List<AffectedTile> affectedTiles = AffectedTile.getAffectedTilesAt(
                this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation());

        floorItems.addAll(this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY()));

        for (AffectedTile tile : affectedTiles) {
            for (RoomItemFloor floorItem : this.getRoom().getItems().getItemsOnSquare(tile.x, tile.y)) {
                if (!floorItems.contains(floorItem)) floorItems.add(floorItem);
            }
        }

        return floorItems;
    }

    public List<RoomEntity> getEntitiesOnItem() {
        List<RoomEntity> entities = Lists.newArrayList();

        entities.addAll(this.getRoom().getEntities().getEntitiesAt(this.getPosition()));

        for (AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
            List<RoomEntity> entitiesOnTile = this.getRoom().getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

            entities.addAll(entitiesOnTile);
        }

        return entities;
    }

    public Position getPartnerTile() {
        if (this.getDefinition().getLength() != 2) return null;

        for (AffectedTile affTile : AffectedTile.getAffectedBothTilesAt(this.getDefinition().getLength(), this.getDefinition().getWidth(), this.getPosition().getX(), this.getPosition().getY(), this.getRotation())) {
            if (affTile.x == this.getPosition().getX() && affTile.y == this.getPosition().getY()) continue;

            return new Position(affTile.x, affTile.y);
        }

        return null;
    }

    public PlayerEntity nearestPlayerEntity() {
        PositionComporator positionComporator = new PositionComporator(this);

        List<PlayerEntity> nearestEntities = this.getRoom().getEntities().getPlayerEntities();

        Collections.sort(nearestEntities, positionComporator);

        for (PlayerEntity playerEntity : nearestEntities) {
            if (playerEntity.getTile().isReachable(this)) {
                return playerEntity;
            }
        }

        return null;
    }

    public RoomEntity getCollision() {
        return this.collidedEntity;
    }

    public void setCollision(RoomEntity entity) {
        this.collidedEntity = entity;
    }

    public void nullifyCollision() {
        this.collidedEntity = null;
    }

    public double getOverrideHeight() {
        return -1d;
    }

    public String getDataObject() {
        return this.extraData;
    }

    public String getExtraData() {
        return this.extraData;
    }

    public void setRotation(int rot) {
        this.rotation = rot;
    }

    public void setExtraData(String data) {
        this.extraData = data;

        if (!this.needsUpdate) {
            this.needsUpdate = true;
        }
    }

    public boolean hasQueuedSave() {
        return hasQueuedSave;
    }

    public void setHasQueuedSave(boolean hasQueuedSave) {
        this.hasQueuedSave = hasQueuedSave;
    }

    public int getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }
}
