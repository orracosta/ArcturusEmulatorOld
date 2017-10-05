package com.habboproject.server.game.rooms.objects.items.types.floor.football;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;

import java.util.Arrays;


public class FootballGateFloorItem extends RoomItemFloor {
    public FootballGateFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (this.getExtraData().equals("0")) {
            this.setExtraData("hr-828-31.ch-255-82.sh-3089-64.hd-180-10.lg-3058-64;hr-828-31.ch-255-82.sh-3089-64.hd-180-10.lg-3058-64");
            this.saveData();
        }
    }

    public void onEntityStepOn(RoomEntity entity) {
        if (!(entity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity playerEntity = (PlayerEntity)entity;

        String newFigure = "";
        for (final String playerFigurePart : Arrays.asList(playerEntity.getFigure().split("[.]"))) {
            if (!playerFigurePart.startsWith("ch") && !playerFigurePart.startsWith("lg")) {
                newFigure = String.valueOf(newFigure) + playerFigurePart + ".";
            }
        }

        String newFigureParts = this.getFigure(playerEntity.getGender());
        for (final String newFigurePart : Arrays.asList(newFigureParts.split("[.]"))) {
            if (newFigurePart.startsWith("hd")) {
                newFigureParts = newFigureParts.replace(newFigurePart, "");
            }
        }

        if (newFigureParts.equals("")) {
            return;
        }

        playerEntity.getPlayer().getData().setFigure(String.valueOf(newFigure) + newFigureParts);
        playerEntity.getPlayer().poof();

        playerEntity.getPlayer().getData().save();
    }

    public void setFigure(String gender, String figure) {
        switch (gender.toUpperCase()) {
            case "F": {
                this.setExtraData(String.valueOf(this.getFigure("M")) + ";" + figure);
                break;
            }

            case "M": {
                this.setExtraData(String.valueOf(figure) + ";" + this.getFigure("F"));
                break;
            }

            default:
                break;
        }
    }

    public String getFigure(final String gender) {
        if (!this.getExtraData().contains(";")) {
            return "hr-828-31.ch-255-82.sh-3089-64.hd-180-10.lg-3058-64";
        }

        String[] figureData = this.getExtraData().split(";");

        String figure;
        if (gender.toUpperCase().equals("M")) {
            figure = figureData[0];
        } else if (figureData.length != 2) {
            figure = "";
        } else {
            figure = figureData[1];
        }

        return figure.isEmpty() ? "hr-828-31.ch-255-82.sh-3089-64.hd-180-10.lg-3058-64" : figure;
    }
}
