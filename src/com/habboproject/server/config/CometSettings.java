package com.habboproject.server.config;

import com.habboproject.server.game.rooms.filter.FilterMode;
import com.habboproject.server.storage.queries.config.ConfigDao;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;


public class CometSettings {
    public static boolean motdEnabled = false;

    public static String motdMessage = "";
    public static String hotelName = "";
    public static String hotelUrl = "";
    public static String aboutImg = "";

    public static boolean onlineRewardEnabled = false;
    public static int onlineRewardCredits = 0;
    public static int onlineRewardDuckets = 0;
    public static int onlineRewardInterval = 15;
    public static int maxrankPurchaseLtd = 11;
    public static int maxrankDiamoundTimer = 11;

    public static int groupCost = 0;

    public static boolean aboutShowPlayersOnline = true;
    public static boolean aboutShowUptime = true;
    public static boolean aboutShowRoomsActive = true;

    public static int floorEditorMaxX = 0;
    public static int floorEditorMaxY = 0;
    public static int floorEditorMaxTotal = 0;

    public static int roomMaxPlayers = 150;
    public static boolean roomEncryptPasswords = false;
    public static int roomPasswordEncryptionRounds = 10;
    public static boolean roomCanPlaceItemOnEntity = false;
    public static int roomMaxBots = 15;
    public static int roomMaxPets = 15;
    public static int roomIdleMinutes = 20;

    public static FilterMode wordFilterMode = FilterMode.DEFAULT;

    public static boolean useDatabaseIp = false;
    public static boolean saveLogins = false;
    public static boolean playerInfiniteBalance = false;
    public static int playerGiftCooldown = 30;
    public static final Map<String, String> strictFilterCharacters = Maps.newHashMap();

    public static boolean playerFigureValidation = false;
    public static int playerChangeFigureCooldown = 5;
    public static int messengerMaxFriends = 1100;
    public static boolean messengerLogMessages = false;

    public static int roomWiredRewardMinimumRank = 4;

    public static boolean asyncCatalogPurchase = true;
    public static boolean storagePlayerQueueEnabled = false;
    public static boolean storageItemQueueEnabled = false;
    public static boolean adaptiveEntityProcessDelay = false;

    public static String defaultEventPointsColumn = "";
    public static int defaultEventPointsQuantity = 0;
    public static boolean enableEventWinnerReward = false;
    public static String eventWinnerRewardType = "credits";
    public static int eventWinnerRewardQuantity = 0;

    public static boolean enableAchievementProgressReward = false;

    public static int cameraPhotoFurnitureId = 0;
    public static String cameraPhotoUrl = "";

    public static boolean enableEventWinnerNotification = false;

    public static String defaultPromoPointsColumn = "";
    public static int defaultPromoPointsQuantity = 0;
    public static boolean enablePromoWinnerReward = false;
    public static String promoWinnerRewardType = "credits";
    public static int promoWinnerRewardQuantity = 0;

    public static String defaultMarketplaceCoin = "credits";
    public static String defaultMarketplaceType = "all";

    public static boolean onlineRewardDiamondsEnabled = false;
    public static int onlineRewardDiamondsInterval = 60;
    public static int onlineRewardDiamondsQuantity = 0;

    private static final Logger log = Logger.getLogger(CometSettings.class.getName());

    public static void initialize() {
        ConfigDao.getAll();

        log.info("Initialized configuration");
    }

    public static void reloadAll() {
        ConfigDao.getAll();

        log.info("Reloaded configuration");
    }

    public static void setMotd(String motd) {
        motdEnabled = true;
        motdMessage = motd;
    }
}
