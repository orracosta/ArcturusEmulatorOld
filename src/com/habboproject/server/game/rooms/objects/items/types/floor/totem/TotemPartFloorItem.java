package com.habboproject.server.game.rooms.objects.items.types.floor.totem;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.user.inventory.EffectsInventoryMessageComposer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public abstract class TotemPartFloorItem extends RoomItemFloor {
    public TotemPartFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (!StringUtils.isNumeric(this.getExtraData())) {
            this.setExtraData("0");
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (entity == null || !(entity instanceof PlayerEntity) || isWiredTrigger) {
            return false;
        }

        PlayerEntity pEntity = (PlayerEntity)entity;
        if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
            return false;
        }

        this.toggleInteract(true);
        this.sendUpdate();
        this.saveData();

        if (this.isComplete()) {
            int effectId = 0;

            int planetData = 0;
            int headData = 0;
            int bodyData = 0;

            double atTop = 0.0;
            double inMiddle = 0.0;
            double inFloor = 0.0;

            boolean isHead = this instanceof TotemHeadFloorItem;
            boolean isBody = this instanceof TotemBodyFloorItem;
            boolean isPlanet = this instanceof TotemPlanetFloorItem;

            if (isBody) {
                bodyData = Integer.parseInt(this.getExtraData());
                inFloor = this.getPosition().getZ();
            }

            if (isHead) {
                headData = Integer.parseInt(this.getExtraData());
                inMiddle = this.getPosition().getZ();
            }

            if (isPlanet) {
                planetData = Integer.parseInt(this.getExtraData());
                atTop = this.getPosition().getZ();
            }

            for (RoomItemFloor floorItem : this.getItemsOnStack()) {
                if (floorItem instanceof TotemHeadFloorItem && !isHead) {
                    headData = Integer.parseInt(floorItem.getExtraData());
                    inMiddle = floorItem.getPosition().getZ();
                }

                if (floorItem instanceof TotemBodyFloorItem && !isBody) {
                    bodyData = Integer.parseInt(floorItem.getExtraData());
                    inFloor = floorItem.getPosition().getZ();
                }

                if (!(floorItem instanceof TotemPlanetFloorItem) || isPlanet)
                    continue;

                planetData = Integer.parseInt(floorItem.getExtraData());
                atTop = floorItem.getPosition().getZ();
            }

            if (atTop > inMiddle && inMiddle > inFloor && (effectId = this.getEffect(bodyData, headData, planetData)) != 0) {
                DateTime date = new DateTime();
                if (!((PlayerEntity)entity).getPlayer().getStats().getLastTotemEffect().isEmpty()) {
                    String dateStr = ((PlayerEntity)entity).getPlayer().getStats().getLastTotemEffect();

                    int lastDay = Integer.parseInt(dateStr.split("[/]")[0]);
                    int lastMonth = Integer.parseInt(dateStr.split("[/]")[1]);
                    int lastYear = Integer.parseInt(dateStr.split("[/]")[2]);

                    if (date.getDayOfMonth() == lastDay && date.getMonthOfYear() == lastMonth && lastYear == date.getYear()) {
                        return false;
                    }
                }

                ((PlayerEntity)entity).applyEffect(new PlayerEffect(effectId, 0));

                if (!((PlayerEntity)entity).getPlayer().getEffectComponent().hasEffect(effectId)) {
                    ((PlayerEntity)entity).getPlayer().getEffectComponent().addEffect(effectId, 3600, true);
                    ((PlayerEntity)entity).getPlayer().getSession().send(new EffectsInventoryMessageComposer(((PlayerEntity)entity).getPlayer().getEffectComponent().getEffects().values()));
                }

                ((PlayerEntity)entity).getPlayer().getStats().setLastTotemEffect(String.valueOf(date.getDayOfMonth()) + "/" + date.getMonthOfYear() + "/" + date.getYear());
                ((PlayerEntity)entity).getPlayer().getStats().save();
            }
        }

        return true;
    }

    @Override
    public void onItemAddedToStack(RoomItemFloor floorItem) {
        if (floorItem instanceof TotemHeadFloorItem && this instanceof TotemBodyFloorItem) {
            if (!StringUtils.isNumeric(this.getExtraData())) {
                this.setExtraData("0");
            }

            floorItem.setExtraData("0");
            floorItem.sendUpdate();
            floorItem.saveData();
        }
    }

    protected boolean isComplete() {
        boolean hasHead = this instanceof TotemHeadFloorItem;
        boolean hasBody = this instanceof TotemBodyFloorItem;
        boolean hasPlanet = this instanceof TotemPlanetFloorItem;

        for (RoomItemFloor floorItem : this.getItemsOnStack()) {
            if (floorItem instanceof TotemHeadFloorItem) {
                hasHead = true;
            }
            if (floorItem instanceof TotemBodyFloorItem) {
                hasBody = true;
            }
            if (!(floorItem instanceof TotemPlanetFloorItem)) continue;
            hasPlanet = true;
        }

        if (hasHead && hasBody && hasPlanet) {
            return true;
        }

        return false;
    }

    @Override
    public void onPositionChanged(Position newPosition) {
        this.setExtraData("0");
        this.sendUpdate();
        this.saveData();
    }

    public int getEffect(int bodyPart, int headPart, int planetHead) {
        if (bodyPart == 2 && headPart == 5 && planetHead == 2) {
            return 23;
        }

        if (bodyPart == 7 && headPart == 10 && planetHead == 0) {
            return 24;
        }

        if (bodyPart == 9 && headPart == 12 && planetHead == 1) {
            return 25;
        }

        if (bodyPart == 10 && headPart == 9 && planetHead == 2) {
            return 26;
        }

        return 0;
    }
}
