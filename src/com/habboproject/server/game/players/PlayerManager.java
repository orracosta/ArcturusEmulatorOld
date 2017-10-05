package com.habboproject.server.game.players;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.game.players.login.PlayerLoginRequest;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PlayerManager implements Initializable {
    private static PlayerManager playerManagerInstance;
    private static Logger log = Logger.getLogger(PlayerManager.class.getName());

    private Map<Integer, Integer> playerIdToSessionId;
    private Map<String, Integer> playerUsernameToPlayerId;

    private Map<String, List<Integer>> ipAddressToPlayerIds;

    private CacheManager cacheManager;

    private Cache playerAvatarCache;
    private Cache playerDataCache;
    private ExecutorService playerLoginService;

    public PlayerManager() {

    }

    @Override
    public void initialize() {
        this.playerIdToSessionId = new ConcurrentHashMap<>();
        this.playerUsernameToPlayerId = new ConcurrentHashMap<>();
        this.ipAddressToPlayerIds = new ConcurrentHashMap<>();

        this.playerLoginService = Executors.newFixedThreadPool(2);// TODO: configure this.

        // Configure player cache
        if ((boolean) Comet.getServer().getConfig().getOrDefault("comet.cache.players.enabled", true)) {
            log.info("Initializing Player cache");

            final int oneDay = 24 * 60 * 60;
            this.playerAvatarCache = new Cache("playerAvatarCache", 75000, false, false, oneDay, oneDay);
            this.playerDataCache = new Cache("playerDataCache", 15000, false, false, oneDay, oneDay);

            this.cacheManager = CacheManager.newInstance("./config/ehcache.xml");

            this.cacheManager.addCache(this.playerAvatarCache);
            this.cacheManager.addCache(this.playerDataCache);
        } else {
            log.info("Player data cache is disabled.");
        }

        log.info("Resetting player online status");
        PlayerDao.resetOnlineStatus();

        log.info("PlayerManager initialized");
    }

    public static PlayerManager getInstance() {
        if (playerManagerInstance == null)
            playerManagerInstance = new PlayerManager();

        return playerManagerInstance;
    }

    public void submitLoginRequest(Session client, String ticket) {
        this.playerLoginService.submit(new PlayerLoginRequest(client, ticket));
    }

    public PlayerAvatar getAvatarByPlayerId(int playerId, byte mode) {
        if (this.isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null && session.getPlayer() != null && session.getPlayer().getData() != null) {
                return session.getPlayer().getData();
            }
        }

        if (this.playerDataCache != null) {
            Element cachedElement = this.playerDataCache.get(playerId);

            if (cachedElement != null && cachedElement.getObjectValue() != null) {
                return (PlayerData) cachedElement.getObjectValue();
            }
        }

        if (this.playerAvatarCache != null) {
            Element cachedElement = this.playerAvatarCache.get(playerId);

            if (cachedElement != null && cachedElement.getObjectValue() != null) {
                final PlayerAvatar playerAvatar = ((PlayerAvatar) cachedElement.getObjectValue());

                if (playerAvatar.getMotto() == null && mode == PlayerAvatar.USERNAME_FIGURE_MOTTO) {
                    playerAvatar.setMotto(PlayerDao.getMottoByPlayerId(playerId));
                }

                return playerAvatar;
            }
        }

        PlayerAvatar playerAvatar = PlayerDao.getAvatarById(playerId, mode);

        if (playerAvatar != null && this.playerAvatarCache != null) {
            this.playerAvatarCache.put(new Element(playerId, playerAvatar));
        }

        return playerAvatar;
    }

    public PlayerData getDataByPlayerId(int playerId) {
        if (this.isOnline(playerId)) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null && session.getPlayer() != null && session.getPlayer().getData() != null) {
                return session.getPlayer().getData();
            }
        }

        if (this.playerDataCache != null) {
            Element cachedElement = this.playerDataCache.get(playerId);

            if (cachedElement != null && cachedElement.getObjectValue() != null) {
                return (PlayerData) cachedElement.getObjectValue();
            }
        }

        PlayerData playerData = PlayerDao.getDataById(playerId);

        if (playerData != null && this.playerDataCache != null) {
            this.playerDataCache.put(new Element(playerId, playerData));
        }

        return playerData;
    }

    public int getPlayerCountByIpAddress(String ipAddress) {
        if (this.ipAddressToPlayerIds.containsKey(ipAddress)) {
            return this.ipAddressToPlayerIds.get(ipAddress).size();
        }

        return 0;
    }

    public void put(int playerId, int sessionId, String username, String ipAddress) {
        if (this.playerIdToSessionId.containsKey(playerId)) {
            this.playerIdToSessionId.remove(playerId);
        }

        if (this.playerUsernameToPlayerId.containsKey(username.toLowerCase())) {
            this.playerUsernameToPlayerId.remove(username.toLowerCase());
        }

        if (!this.ipAddressToPlayerIds.containsKey(ipAddress)) {
            this.ipAddressToPlayerIds.put(ipAddress, Lists.newArrayList(playerId));
        } else {
            this.ipAddressToPlayerIds.get(ipAddress).add(playerId);
        }

        this.playerIdToSessionId.put(playerId, sessionId);
        this.playerUsernameToPlayerId.put(username.toLowerCase(), playerId);
    }

    public void remove(int playerId, String username, int sessionId, String ipAddress) {
        if (this.getSessionIdByPlayerId(playerId) != sessionId) {
            return;
        }

        if (this.ipAddressToPlayerIds.containsKey(ipAddress)) {
            List<Integer> playerIds = this.ipAddressToPlayerIds.get(ipAddress);

            if (!playerIds.isEmpty()) {
                playerIds.remove((Integer) playerId);
            }

            if (playerIds.isEmpty()) {
                this.ipAddressToPlayerIds.remove(ipAddress);
            }
        }

        this.playerIdToSessionId.remove(playerId);
        this.playerUsernameToPlayerId.remove(username.toLowerCase());
    }

    public int getPlayerIdByUsername(String username) {
        if (this.playerUsernameToPlayerId.containsKey(username.toLowerCase())) {
            return this.playerUsernameToPlayerId.get(username.toLowerCase());
        }

        return -1;
    }

    public int getSessionIdByPlayerId(int playerId) {
        if (this.playerIdToSessionId.containsKey(playerId)) {
            return this.playerIdToSessionId.get(playerId);
        }

        return -1;
    }

    public List<Integer> getPlayerIdsByIpAddress(String ipAddress) {
        return new ArrayList<>(this.ipAddressToPlayerIds.get(ipAddress));
    }

    public boolean isOnline(int playerId) {
        return this.playerIdToSessionId.containsKey(playerId);
    }

    public boolean isOnline(String username) {
        return this.playerUsernameToPlayerId.containsKey(username.toLowerCase());
    }

    public int size() {
        return this.playerIdToSessionId.size();
    }
}
