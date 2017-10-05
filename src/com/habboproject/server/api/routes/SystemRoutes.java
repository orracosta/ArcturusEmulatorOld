package com.habboproject.server.api.routes;

import com.habboproject.server.api.networking.sessions.BaseSession;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.landing.LandingManager;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogPublishMessageComposer;
import com.habboproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class SystemRoutes {
    public static Object reload(Request req, Response res) {
        Map<String, Object> result = new HashMap<>();
        res.type("application/json");

        String type = req.params("type");

        if (type == null) {
            result.put("error", "Invalid type");
            return result;
        }

        switch (type) {
            case "bans":
                BanManager.getInstance().loadBans();
                break;

            case "catalog":
                CatalogManager.getInstance().loadItemsAndPages();
                CatalogManager.getInstance().loadGiftBoxes();

                NetworkManager.getInstance().getSessions().broadcast(new CatalogPublishMessageComposer(true));
                break;

            case "navigator":
                NavigatorManager.getInstance().loadPublicRooms();
                break;

            case "permissions":
                PermissionsManager.getInstance().loadRankPermissions();
                PermissionsManager.getInstance().loadPerks();
                PermissionsManager.getInstance().loadCommands();
                break;

            case "config":
                CometSettings.initialize();
                break;

            case "news":
                LandingManager.getInstance().loadArticles();
                break;

            case "items":
                ItemManager.getInstance().loadItemDefinitions();
                break;

            case "filter":
                RoomManager.getInstance().getFilter().loadFilter();
                break;

            case "locale":
                Locale.reload();
                CommandManager.getInstance().reloadAllCommands();
                break;

            case "modpresets":
                ModerationManager.getInstance().loadPresets();

                for (BaseSession session : NetworkManager.getInstance().getSessions().getByPlayerPermission("mod_tool")) {
                    session.send(new ModToolMessageComposer());
                }

                break;
        }

        result.put("success", true);
        return result;
    }

}
