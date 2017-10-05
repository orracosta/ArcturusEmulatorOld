package com.habboproject.server.game.rooms;

import com.habboproject.server.api.game.rooms.settings.RoomAccessType;
import com.habboproject.server.api.game.rooms.settings.RoomTradeState;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.filter.WordFilter;
import com.habboproject.server.game.rooms.models.CustomModel;
import com.habboproject.server.game.rooms.models.types.StaticRoomModel;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.game.rooms.types.RoomPromotion;
import com.habboproject.server.game.rooms.types.RoomType;
import com.habboproject.server.game.rooms.types.misc.ChatEmotionsManager;
import com.habboproject.server.network.messages.outgoing.room.events.RoomPromotionMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.rooms.RoomDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.apache.solr.util.ConcurrentLRUCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RoomManager implements Initializable {
    private static RoomManager roomManagerInstance;

    public static final Logger log = Logger.getLogger(RoomManager.class.getName());

    public static final int LRU_MAX_ENTRIES = Integer.parseInt(Comet.getServer().getConfig().getProperty("comet.game.rooms.data.max"));
    public static final int LRU_MAX_LOWER_WATERMARK = Integer.parseInt(Comet.getServer().getConfig().getProperty("comet.game.rooms.data.lowerWatermark"));

    private ConcurrentLRUCache<Integer, RoomData> roomDataInstances;

    private Map<Integer, Room> loadedRoomInstances;
    private Map<Integer, Room> unloadingRoomInstances;

    private Map<Integer, RoomPromotion> roomPromotions;

    private Map<String, StaticRoomModel> models;
    private WordFilter filterManager;

    private RoomCycle globalCycle;
    private ChatEmotionsManager emotions;

    private ExecutorService executorService;

    public RoomManager() {

    }

    @Override
    public void initialize() {
        this.roomDataInstances = new ConcurrentLRUCache<>(LRU_MAX_ENTRIES, LRU_MAX_LOWER_WATERMARK);

        this.loadedRoomInstances = new ConcurrentHashMap<>();
        this.unloadingRoomInstances = new ConcurrentHashMap<>();
        this.roomPromotions = new ConcurrentHashMap<>();

        this.emotions = new ChatEmotionsManager();
        this.filterManager = new WordFilter();

        this.globalCycle = new RoomCycle();

        this.loadPromotedRooms();
        this.loadModels();

        this.globalCycle.start();
        this.executorService = Executors.newFixedThreadPool(4);

        log.info("RoomManager initialized");
    }

    public static RoomManager getInstance() {
        if (roomManagerInstance == null)
            roomManagerInstance = new RoomManager();

        return roomManagerInstance;
    }

    public void loadPromotedRooms() {
        RoomDao.deleteExpiredRoomPromotions();
        RoomDao.getActivePromotions(this.roomPromotions);

        log.info("Loaded " + this.getRoomPromotions().size() + " room promotions");
    }

    public void initializeRoom(Session initializer, int roomId, String password) {
        this.executorService.submit(() -> {
            if (initializer != null && initializer.getPlayer() != null) {
                initializer.getPlayer().loadRoom(roomId, password);
            }
        });
    }

    public void loadModels() {
        if (this.models != null && this.getModels().size() != 0) {
            this.getModels().clear();
        }

        this.models = RoomDao.getModels();

        log.info("Loaded " + this.getModels().size() + " room models");
    }

    public StaticRoomModel getModel(String id) {
        if (this.models.containsKey(id))
            return this.models.get(id);

        log.debug("Couldn't find model: " + id);

        return null;
    }

    public Room get(int id) {
        if (id == 0) return null;

        if (this.getRoomInstances().containsKey(id)) {
            return this.getRoomInstances().get(id);
        }

        if (this.getRoomInstances().containsKey(id)) {
            return this.getRoomInstances().get(id);
        }

        RoomData data = this.getRoomData(id);

        if (data == null) {
            return null;
        }

        Room room = new Room(data).load();

        if (room == null) return null;

        this.loadedRoomInstances.put(id, room);

        if (data.getType() == RoomType.PUBLIC) {
            RoomQueue.getInstance().addQueue(room.getId(), 0);
        }

        this.finalizeRoomLoad(room);

        return room;
    }

    private void finalizeRoomLoad(Room room) {
        if (room == null) {
            return;
        }

        room.getItems().onLoaded();
    }

    public RoomData getRoomData(int id) {
        if (this.getRoomDataInstances().getMap().containsKey(id)) {
            return this.getRoomDataInstances().get(id).setLastReferenced(Comet.getTime());
        }

        RoomData roomData = RoomDao.getRoomDataById(id);

        if (roomData != null) {
            this.getRoomDataInstances().put(id, roomData);
        }

        return roomData;
    }

    public void unloadIdleRooms() {
        for (Room room : this.unloadingRoomInstances.values()) {
            this.executorService.submit(room::dispose);
        }

        this.unloadingRoomInstances.clear();

        List<Room> idleRooms = new ArrayList<>();

        for (Room room : this.loadedRoomInstances.values()) {
            if (room.isIdle()) {
                idleRooms.add(room);
            }
        }

        for (Room room : idleRooms) {
            this.loadedRoomInstances.remove(room.getId());
            this.unloadingRoomInstances.put(room.getId(), room);
        }
    }

    public void forceUnload(int id) {
        if (this.loadedRoomInstances.containsKey(id)) {
            this.loadedRoomInstances.remove(id).dispose();
        }
    }

    public void removeData(int roomId) {
        if (this.getRoomDataInstances().get(roomId) == null) {
            return;
        }

        this.getRoomDataInstances().remove(roomId);
    }

    public void loadRoomsForUser(Player player) {
        player.getRooms().clear();

        Map<Integer, RoomData> rooms = RoomDao.getRoomsByPlayerId(player.getId());

        for (Map.Entry<Integer, RoomData> roomEntry : rooms.entrySet()) {
            player.getRooms().add(roomEntry.getKey());

            if (!this.getRoomDataInstances().getMap().containsKey(roomEntry.getKey())) {
                this.getRoomDataInstances().put(roomEntry.getKey(), roomEntry.getValue());
            }
        }
    }

    //TODO: check this...
    public List<RoomData> getRoomsByQuery(String query) {
        List<RoomData> rooms = Lists.newArrayList();

        List<RoomData> roomSearchResults = RoomDao.getRoomsByQuery(query);

        for (RoomData data : roomSearchResults) {
            if (!this.getRoomDataInstances().getMap().containsKey(data.getId())) {
                this.getRoomDataInstances().put(data.getId(), data);
            }

            rooms.add(data);
        }

        if (rooms.size() == 0 && !query.toLowerCase().startsWith("owner:")) {
            return this.getRoomsByQuery("owner:" + query);
        }

        return rooms;
    }

    public int getRandomLoadedRoom() {
        List<RoomData> rooms = Lists.newArrayList();
        for (Room room : this.getRoomInstances().values()) {
            if (room.getData().getAccess() != RoomAccessType.OPEN || room.getEntities().playerCount() <= 1 || room.getEntities().playerCount() >= room.getData().getMaxUsers())
                continue;

            rooms.add(room.getData());
        }

        if (rooms.size() > 0) {
            return RoomUtil.getRandomElement(rooms).getId();
        } else {
            return 0;
        }
    }

    public boolean isActive(int id) {
        return this.getRoomInstances().containsKey(id);
    }

    public int createRoom(String name, String description, CustomModel model, int category, int maxVisitors, int tradeState, Session client, int wallTickness, int floorThickness, String decorations, boolean hideWalls) {
        int roomId = RoomDao.createRoom(name, model, description, category, maxVisitors, RoomTradeState.valueOf(tradeState), client.getPlayer().getId(), client.getPlayer().getData().getUsername(), wallTickness, floorThickness, decorations, hideWalls);

        this.loadRoomsForUser(client.getPlayer());

        return roomId;
    }


    public int createRoom(String name, String description, String model, int category, int maxVisitors, int tradeState, Session client) {
        int roomId = RoomDao.createRoom(name, model, description, category, maxVisitors, RoomTradeState.valueOf(tradeState), client.getPlayer().getId(), client.getPlayer().getData().getUsername());

        this.loadRoomsForUser(client.getPlayer());

        return roomId;
    }

    public List<RoomData> getRoomsByCategory(int category) {
        List<RoomData> rooms = new ArrayList<>();

        for (Room room : this.getRoomInstances().values()) {
            if (category != -1 && (room.getData().getCategory() == null || room.getData().getCategory().getId() != category)) {
                continue;
            }

            rooms.add(room.getData());
        }

        return rooms;
    }

    public void promoteRoom(int roomId, String name, String description) {
        if (this.roomPromotions.containsKey(roomId)) {
            RoomPromotion promo = this.roomPromotions.get(roomId);
            promo.setTimestampFinish(promo.getTimestampFinish() + (RoomPromotion.DEFAULT_PROMO_LENGTH * 60));

            RoomDao.updatePromotedRoom(promo);
        } else {
            RoomPromotion roomPromotion = new RoomPromotion(roomId, name, description);
            RoomDao.createPromotedRoom(roomPromotion);

            this.roomPromotions.put(roomId, roomPromotion);
        }

        if (this.get(roomId) != null) {
            Room room = this.get(roomId);

            if (room.getEntities() != null && room.getEntities().realPlayerCount() >= 1) {
                room.getEntities().broadcastMessage(new RoomPromotionMessageComposer(room.getData(), this.roomPromotions.get(roomId)));
            }
        }
    }

    public boolean hasPromotion(int roomId) {
        if (this.roomPromotions.containsKey(roomId) && !this.roomPromotions.get(roomId).isExpired()) {
            return true;
        }

        return false;
    }

    public final ChatEmotionsManager getEmotions() {
        return this.emotions;
    }

    public final Map<Integer, Room> getRoomInstances() {
        return this.loadedRoomInstances;
    }

    public final ConcurrentLRUCache<Integer, RoomData> getRoomDataInstances() {
        return this.roomDataInstances;
    }

    public final Map<String, StaticRoomModel> getModels() {
        return this.models;
    }

    public final RoomCycle getGlobalCycle() {
        return this.globalCycle;
    }

    public final WordFilter getFilter() {
        return filterManager;
    }

    public Map<Integer, RoomPromotion> getRoomPromotions() {
        return roomPromotions;
    }
}
