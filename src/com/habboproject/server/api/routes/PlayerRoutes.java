package com.habboproject.server.api.routes;

import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.storage.queries.player.inventory.InventoryDao;
import org.apache.commons.lang.StringUtils;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;


public class PlayerRoutes {
    public static Object reloadPlayerData(Request request, Response response) {
        Map<String, Object> result = new HashMap<>();
        response.type("application/json");

        if (!StringUtils.isNumeric(request.params("id"))) {
            result.put("error", "Invalid ID");
            return result;
        }

        int playerId = Integer.parseInt(request.params("id"));

        if (!PlayerManager.getInstance().isOnline(playerId)) {
            result.put("error", "Player is not online");
            return result;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if (session == null) {
            result.put("error", "Unable to find the player's session");
            return result;
        }

        PlayerData newPlayerData = PlayerDao.getDataById(playerId);
        PlayerData currentPlayerData = session.getPlayer().getData();

        if(newPlayerData == null) {
            result.put("error", "Unable to find the player's new data!");
            return result;
        }

        final boolean sendCurrencies = (newPlayerData.getCredits() != currentPlayerData.getCredits()) ||
                (newPlayerData.getActivityPoints() != currentPlayerData.getActivityPoints()) ||
                (newPlayerData.getVipPoints() != currentPlayerData.getVipPoints());

        currentPlayerData.setRank(newPlayerData.getRank());
        currentPlayerData.setMotto(newPlayerData.getMotto());
        currentPlayerData.setFigure(newPlayerData.getFigure());
        currentPlayerData.setGender(newPlayerData.getGender());
        currentPlayerData.setEmail(newPlayerData.getEmail());

        currentPlayerData.setCredits(newPlayerData.getCredits());
        currentPlayerData.setVipPoints(newPlayerData.getVipPoints());
        currentPlayerData.setActivityPoints(newPlayerData.getActivityPoints());

        currentPlayerData.setAchievementPoints(newPlayerData.getAchievementPoints());
        currentPlayerData.setFavouriteGroup(newPlayerData.getFavouriteGroup());
        currentPlayerData.setVip(newPlayerData.isVip());

        if (sendCurrencies) {
            session.getPlayer().sendBalance();
        }

        session.getPlayer().poof();

        result.put("success", true);
        return result;
    }

    public static Object disconnect(Request req, Response res) {
        Map<String, Object> result = new HashMap<>();
        res.type("application/json");

        if (!StringUtils.isNumeric(req.params("id"))) {
            result.put("error", "Invalid ID");
            return result;
        }

        int playerId = Integer.parseInt(req.params("id"));

        if (!PlayerManager.getInstance().isOnline(playerId)) {
            result.put("error", "Player is not online");
            return result;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if (session == null) {
            result.put("error", "Unable to find the player's session");
            return result;
        }

        session.disconnect();

        result.put("success", true);
        return result;
    }

    public static Object alert(Request req, Response res) {
        Map<String, Object> result = new HashMap<>();
        res.type("application/json");

        if (!StringUtils.isNumeric(req.params("id"))) {
            result.put("error", "Invalid ID");
            return result;
        }

        int playerId = Integer.parseInt(req.params("id"));

        if (!PlayerManager.getInstance().isOnline(playerId)) {
            result.put("error", "Player is not online");
            return result;
        }

        Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

        if (session == null) {
            result.put("error", "Unable to find the player's session");
            return result;
        }

        String title = req.queryParams("title");

        if (title == null)
            title = "Notification";

        String alert = req.queryParams("message");

        if (alert != null) {
            session.send(new AdvancedAlertMessageComposer(title, alert));
        }

        result.put("success", true);
        return result;
    }

    public static Object giveBadge(Request req, Response res) {
        Map<String, Object> result = new HashMap<>();
        res.type("application/json");

        if (!StringUtils.isNumeric(req.params("id"))) {
            result.put("error", "Invalid ID");
            return result;
        }

        int playerId = Integer.parseInt(req.params("id"));
        String badgeId = req.params("badge");

        if (!PlayerManager.getInstance().isOnline(playerId)) {
            if (badgeId != null) {
                InventoryDao.addBadge(badgeId, playerId);
            }
        } else {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (badgeId != null && session != null) {
                if (!session.getPlayer().getInventory().hasBadge(badgeId))
                    session.getPlayer().getInventory().addBadge(badgeId, true);
            }
        }

        result.put("success", true);
        return result;
    }
}
