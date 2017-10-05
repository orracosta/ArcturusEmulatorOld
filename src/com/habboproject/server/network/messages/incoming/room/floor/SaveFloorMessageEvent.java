package com.habboproject.server.network.messages.incoming.room.floor;

import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.models.CustomModel;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.utilities.ModelUtils;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.collect.Lists;

import java.util.List;


public class SaveFloorMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        String model = msg.readString();

        int doorX = msg.readInt();
        int doorY = msg.readInt();
        int doorRotation = msg.readInt();
        int wallThickness = msg.readInt();
        int floorThickness = msg.readInt();
        int wallHeight = msg.readInt();

        if (client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null) {
            return;
        }

        Room room = client.getPlayer().getEntity().getRoom();
        if (room.getData().getOwnerId() != client.getPlayer().getId() && !client.getPlayer().getPermissions().getRank().roomFullControl()) {
            return;
        }

        int roomId = room.getId();

        List<Player> players = Lists.newArrayList();
        for (PlayerEntity entity : room.getEntities().getPlayerEntities()) {
            players.add(entity.getPlayer());
        }
        
        model = model.replace((char) 10, (char) 0);
        String[] modelData = model.split(String.valueOf((char) 13));

        int sizeY = modelData.length;
        int sizeX = modelData[0].length();

        if (sizeX < 2 || sizeY < 2 || (CometSettings.floorEditorMaxX != 0 && sizeX > CometSettings.floorEditorMaxX) || (CometSettings.floorEditorMaxY != 0 && sizeY > CometSettings.floorEditorMaxY) || (CometSettings.floorEditorMaxTotal != 0 && CometSettings.floorEditorMaxTotal < sizeX * sizeY)) {
        	client.send(new AdvancedAlertMessageComposer("Invalid Model", Locale.get("command.floor.size")));
            return;
        }

        boolean hasTiles = false;
        boolean validDoor = false;

        try {
            int y = 0;
            while (y < sizeY) {
                String modelLine = modelData[y];
                int x = 0;
                while (x < sizeX) {
                    if (x < modelLine.length() + 1 && !Character.toString(modelLine.charAt(x)).equals("x")) {
                        if (x == doorX && y == doorY) {
                            validDoor = true;
                        }

                        hasTiles = true;
                    }

                    ++x;
                }

                ++y;
            }
        } catch (Exception e) {
            client.send(new AdvancedAlertMessageComposer("Invalid Model", "There seems to be a problem parsing this floor plan, please either try again or contact an admin!"));
        }

        if (!hasTiles || !validDoor) {
            client.send(new AdvancedAlertMessageComposer("Invalid Model", Locale.get("command.floor.invalid")));
            return;
        }

        room.getData().setThicknessWall(wallThickness);
        room.getData().setThicknessFloor(floorThickness);

        int doorZ = ModelUtils.getHeight(modelData[doorY].charAt(doorX));

        CustomModel floorMapData = new CustomModel(doorX, doorY, doorZ, doorRotation, model.trim(), wallHeight == 0 ? room.getModel().getWallHeight() : wallHeight);

        room.getData().setHeightmap(JsonFactory.getInstance().toJson(floorMapData));

        room.getData().save();

        room.getItems().commit();

        ThreadManager.getInstance().execute(new CometThread(){
            @Override
            public void run() {
                RoomManager.getInstance().forceUnload(roomId);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }

                for (Player player : players) {
                    if (player == null || player.getSession() == null)
                        continue;

                    player.getSession().send(new RoomForwardMessageComposer(roomId));
                }
            }
        });
    }
}
