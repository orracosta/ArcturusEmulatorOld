package com.habboproject.server.api.routes;

import com.habboproject.server.api.rooms.RoomStats;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomRoutes {
    public static Object getAllActiveRooms(Request request, Response response) {
        response.type("application/json");
        Map<String, Object> result = new HashMap<>();

        List<RoomStats> activeRooms = new ArrayList<>();

        for (Room room : RoomManager.getInstance().getRoomInstances().values()) {
            activeRooms.add(new RoomStats(room));
        }

        result.put("response", activeRooms);
        return result;
    }

    public static Object roomAction(Request request, Response response) {
        Map<String, Object> result = new HashMap<>();

        int roomId = Integer.parseInt(request.params("id"));
        String action = request.params("action");

        if (!RoomManager.getInstance().getRoomInstances().containsKey(roomId)) {
            result.put("active", false);
            return result;
        }

        Room room = RoomManager.getInstance().get(roomId);

        result.put("id", roomId);

        switch (action) {
            default: {
                result.put("active", false);
                break;
            }

            case "alert": {
                String message = request.headers("message");
                if (message == null || message.isEmpty()) {
                    result.put("success", false);
                } else {
                    room.getEntities().broadcastMessage(new AdvancedAlertMessageComposer(message));
                    result.put("success", true);
                }
                break;
            }

            case "dispose": {
                String message = request.headers("message");

                if (message != null && !message.isEmpty()) {
                    room.getEntities().broadcastMessage(new AdvancedAlertMessageComposer(message));
                }

                room.setIdleNow();
                result.put("success", true);
                break;
            }

            case "data": {
                result.put("data", room.getData());
                break;
            }

            case "delete":
                result.put("message", "Feature not completed");
                break;
        }

        return result;
    }
}
