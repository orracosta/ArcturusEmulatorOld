package com.habboproject.server.boot;

import com.habboproject.server.boot.utils.gui.CometGui;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Configuration;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.GameCycle;
import com.habboproject.server.game.achievements.AchievementManager;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.effects.EffectManager;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.landing.LandingManager;
import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.pets.PetManager;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.polls.PollManager;
import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.bundles.RoomBundleManager;
import com.habboproject.server.game.utilities.validator.PlayerFigureValidator;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.modules.ModuleManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.websocket.WebSocketManager;
import com.habboproject.server.storage.StorageManager;
import com.habboproject.server.storage.queue.types.ItemStorageQueue;
import com.habboproject.server.storage.queue.types.PlayerDataStorageQueue;
import com.habboproject.server.threads.ThreadManager;
import org.apache.log4j.Logger;

import java.util.Map;


public class CometServer {
    private final Logger log = Logger.getLogger(CometServer.class.getName());

    public static final String CLIENT_VERSION = "PRODUCTION-201607262204-86871104";

    /**
     * Comet's configuration
     */
    private Configuration config;

    public CometServer(Map<String, String> overridenConfig) {
        this.config = new Configuration("./config/comet.properties");

        if (overridenConfig != null) {
            this.config.override(overridenConfig);
        }
    }

    /**
     * Initialize Comet Server
     */
    public void init() {
        ModuleManager.getInstance().initialize();
        //APIManager.getInstance().initialize();
        WebSocketManager.getInstance().initialize();
        PlayerFigureValidator.loadFigureData();

        ThreadManager.getInstance().initialize();
        StorageManager.getInstance().initialize();
        LogManager.getInstance().initialize();

        // Initialize the server texts settings
        CometSettings.initialize();
        Locale.initialize();

        // Initialize the game managers
        PermissionsManager.getInstance().initialize();
        RoomBundleManager.getInstance().initialize();
        ItemManager.getInstance().initialize();
        CatalogManager.getInstance().initialize();
        RoomManager.getInstance().initialize();
        NavigatorManager.getInstance().initialize();
        CommandManager.getInstance().initialize();
        BanManager.getInstance().initialize();
        ModerationManager.getInstance().initialize();
        PetManager.getInstance().initialize();
        LandingManager.getInstance().initialize();
        GroupManager.getInstance().initialize();
        PlayerManager.getInstance().initialize();
        QuestManager.getInstance().initialize();
        AchievementManager.getInstance().initialize();
        PollManager.getInstance().initialize();
        MarketplaceManager.getInstance().initialize();
        EffectManager.getInstance().initialize();

        PlayerDataStorageQueue.getInstance().initialize();
        ItemStorageQueue.getInstance().initialize();

        String ipAddress = this.getConfig().get("comet.network.host"),
                port = this.getConfig().get("comet.network.port");

        NetworkManager.getInstance().initialize(ipAddress, port);
        GameCycle.getInstance().initialize();

        if(Comet.showGui) {
            CometGui gui = new CometGui();
            gui.setVisible(true);
        }

        if (Comet.isDebugging) {
            log.debug("Comet Server is debugging");
        }
    }

    /**
     * Get the Comet configuration
     *
     * @return Comet configuration
     */
    public Configuration getConfig() {
        return this.config;
    }

    public Logger getLogger() {
        return log;
    }
}
