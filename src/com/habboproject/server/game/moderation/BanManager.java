package com.habboproject.server.game.moderation;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.moderation.types.Ban;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.storage.queries.moderation.BanDao;
import com.habboproject.server.utilities.Initializable;
import com.corundumstudio.socketio.misc.ConcurrentHashSet;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class BanManager implements Initializable {
    public static BanManager banManagerInstance;

    private Map<String, Ban> bans;
    private Set<Integer> mutedPlayers;

    Logger log = Logger.getLogger(BanManager.class.getName());

    public BanManager() {

    }

    @Override
    public void initialize() {
        this.mutedPlayers = new ConcurrentHashSet<>();

        loadBans();
        log.info("BanManager initialized");
    }

    public static BanManager getInstance() {
        if (banManagerInstance == null)
            banManagerInstance = new BanManager();

        return banManagerInstance;
    }

    public void loadBans() {
        if (this.bans != null)
            this.bans.clear();

        try {
            this.bans = BanDao.getActiveBans();
            log.info("Loaded " + this.bans.size() + " bans");
        } catch (Exception e) {
            log.error("Error while loading bans", e);
        }
    }

    public void tick() {
        List<Ban> bansToRemove = Lists.newArrayList();

        for (Ban ban : this.bans.values()) {
            if (ban.getExpire() != 0 && Comet.getTime() >= ban.getExpire()) {
                bansToRemove.add(ban);
            }
        }

        if (bansToRemove.size() != 0) {
            for (Ban ban : bansToRemove) {
                this.bans.remove(ban.getData());
            }
        }

        bansToRemove.clear();
    }

    public void banPlayer(BanType type, String data, int length, long expire, String reason, int bannerId) {
        int banId = BanDao.createBan(type, length, expire, data, bannerId, reason);
        this.add(new Ban(banId, data, length == 0 ? length : expire, type, reason));
    }

    private void add(Ban ban) {
        this.bans.put(ban.getData(), ban);
    }

    public boolean hasBan(String data, BanType type) {
        if (this.bans.containsKey(data)) {
            Ban ban = this.bans.get(data);

            if (ban != null && ban.getType() == type) {
                if (ban.getExpire() != 0 && Comet.getTime() >= ban.getExpire()) {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    public Ban get(String data) {
        return this.bans.get(data);
    }

    public boolean isMuted(int playerId) {
        return this.mutedPlayers.contains(playerId);
    }

    public void mute(int playerId) {
        this.mutedPlayers.add(playerId);
    }

    public void unmute(int playerId) {
        this.mutedPlayers.remove(playerId);
    }
}
