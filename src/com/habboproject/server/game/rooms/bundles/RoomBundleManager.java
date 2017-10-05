package com.habboproject.server.game.rooms.bundles;

import com.habboproject.server.game.rooms.bundles.types.RoomBundle;
import com.habboproject.server.storage.queries.rooms.BundleDao;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomBundleManager {
    private static RoomBundleManager roomBundleManager;
    private static Logger log = Logger.getLogger(RoomBundleManager.class.getName());

    private Map<String, RoomBundle> bundles;

    public RoomBundleManager() {
        this.bundles = new ConcurrentHashMap<>();
    }

    public void initialize() {
        if (this.bundles.size() != 0) {
            this.bundles.clear();
        }

        BundleDao.loadActiveBundles(this.bundles);

        log.info("Loaded " + this.bundles.size() + " active room bundles");
        log.info("RoomBundleManager initialized");
    }

    public void addBundle(RoomBundle bundle) {
        if (this.bundles.containsKey(bundle.getAlias())) {
            this.bundles.replace(bundle.getAlias(), bundle);
        } else {
            this.bundles.put(bundle.getAlias(), bundle);
        }
    }

    public RoomBundle getBundle(String alias) {
        return this.bundles.get(alias);
    }

    public static RoomBundleManager getInstance() {
        if (roomBundleManager == null) {
            roomBundleManager = new RoomBundleManager();
        }

        return roomBundleManager;
    }
}
