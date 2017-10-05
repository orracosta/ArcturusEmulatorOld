package com.habboproject.server.game.rooms.objects.items.types.floor.boutique;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Arrays;


public class MannequinFloorItem extends RoomItemFloor {
    private String name = "New Mannequin";
    private String figure = "ch-210-62.lg-270-62";
    private String gender = "m";

    public MannequinFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (!this.getExtraData().isEmpty()) {
            String[] splitData = this.getExtraData().split(";#;");
            if (splitData.length != 3) return;

            this.name = splitData[0];
            this.figure = splitData[1];
            this.gender = splitData[2];

            String[] figureParts = this.figure.split("\\.");
            String finalFigure = "";

            for (String figurePart : figureParts) {
                if (!figurePart.contains("hr") && !figurePart.contains("hd") && !figurePart.contains("he") && !figurePart.contains("ha")) {
                    finalFigure += figurePart + ".";
                }
            }

            this.figure = finalFigure.substring(0, finalFigure.length() - 1);
        }
    }

    @Override
    public void compose(IComposer msg, boolean isNew) {
        msg.writeInt(0);
        msg.writeInt(1);
        msg.writeInt(3);

        msg.writeString("GENDER");
        msg.writeString(this.getGender());
        msg.writeString("FIGURE");
        msg.writeString(this.getFigure());
        msg.writeString("OUTFIT_NAME");
        msg.writeString(this.getName());

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(this.ownerId);

        if (isNew) {
            msg.writeString(this.getRoom().getData().getOwner());
        }
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        if (isWiredTrigger || !(entity instanceof PlayerEntity))
            return isWiredTrigger;

        PlayerEntity playerEntity = (PlayerEntity) entity;

        if (this.name == null || this.gender == null || this.figure == null) return false;

        if (!this.gender.equals(playerEntity.getGender())) return false;

        String newFigure = "";

        for (String playerFigurePart : Arrays.asList(playerEntity.getFigure().split("\\."))) {
            if (!playerFigurePart.startsWith("ch") && !playerFigurePart.startsWith("lg"))
                newFigure += playerFigurePart + ".";
        }

        String newFigureParts = "";

        switch (playerEntity.getGender().toUpperCase()) {
            case "M":
                if (this.figure.equals("")) return false;
                newFigureParts = this.figure;
                break;

            case "F":
                if (this.figure.equals("")) return false;
                newFigureParts = this.figure;
                break;
        }

        for (String newFigurePart : Arrays.asList(newFigureParts.split("\\."))) {
            if (newFigurePart.startsWith("hd"))
                newFigureParts = newFigureParts.replace(newFigurePart, "");
        }

        if (newFigureParts.equals("")) return false;

        final String figure = newFigure + newFigureParts;

        if (figure.length() > 512)
            return false;

        playerEntity.getPlayer().getData().setFigure(figure);
        playerEntity.getPlayer().getData().setGender(this.gender);

        playerEntity.getPlayer().getData().save();
        playerEntity.getPlayer().poof();

        return true;
    }

    @Override
    public String getDataObject() {
        return this.name + ";#;" + this.figure + ";#;" + this.gender;
    }

    public String getName() {
        return name;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setName(String name) {
        this.name = name;
    }
}

