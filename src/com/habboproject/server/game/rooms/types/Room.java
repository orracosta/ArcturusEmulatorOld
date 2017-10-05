package com.habboproject.server.game.rooms.types;

import com.habboproject.server.api.game.rooms.IRoom;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.RoomQueue;
import com.habboproject.server.game.rooms.RoomSpectator;
import com.habboproject.server.game.rooms.models.CustomModel;
import com.habboproject.server.game.rooms.models.RoomModel;
import com.habboproject.server.game.rooms.models.types.DynamicRoomModel;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreMostWinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreTeamFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerAtGivenTime;
import com.habboproject.server.game.rooms.types.components.*;
import com.habboproject.server.game.rooms.types.mapping.RoomMapping;
import com.habboproject.server.network.messages.outgoing.room.polls.QuickPollMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.polls.QuickPollResultsMessageComposer;
import com.habboproject.server.utilities.JsonFactory;
import com.habboproject.server.utilities.attributes.Attributable;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Room implements Attributable, IRoom {
    public static final boolean useCycleForItems = false;
    public static final boolean useCycleForEntities = false;

    public final Logger log;

    private final RoomData data;
    private RoomModel model;
    private RoomMapping mapping;

    private ProcessComponent process;
    private RightsComponent rights;
    private ItemsComponent items;
    private ItemProcessComponent itemProcess;
    private TradeComponent trade;
    private RoomBotComponent bots;
    private PetComponent pets;
    private GameComponent game;
    private EntityComponent entities;
    private Group group;

    private Map<String, Object> attributes;
    private Set<Integer> ratings;

    private String question;
    private Set<Integer> yesVotes;
    private Set<Integer> noVotes;

    private boolean isDisposed = false;
    private int idleTicks = 0;

    private final AtomicInteger wiredTimer = new AtomicInteger(0);

    public Room(RoomData data) {
        this.data = data;
        this.log = Logger.getLogger("Room \"" + this.getData().getName() + "\"");
    }

    public Room load() {
        this.model = RoomManager.getInstance().getModel(this.getData().getModel());

        if (this.getData().getHeightmap() != null) {
            DynamicRoomModel dynamicRoomModel;

            if (this.getData().getHeightmap().startsWith("{")) {
                CustomModel mapData = JsonFactory.getInstance().fromJson(this.getData().getHeightmap(), CustomModel.class);

                dynamicRoomModel = DynamicRoomModel.create("dynamic_heightmap", mapData.getModelData(), mapData.getDoorX(), mapData.getDoorY(), mapData.getDoorZ(), mapData.getDoorRotation(), mapData.getWallHeight());
            } else {
                dynamicRoomModel = DynamicRoomModel.create("dynamic_heightmap", this.getData().getHeightmap(), this.getModel().getDoorX(), this.getModel().getDoorY(), this.getModel().getDoorZ(), this.getModel().getDoorRotation(), -1);
            }

            if (dynamicRoomModel != null) {
                this.model = dynamicRoomModel;
            }
        }

        this.attributes = new HashMap<>();
        this.ratings = new HashSet<>();

        this.mapping = new RoomMapping(this);
        this.itemProcess = new ItemProcessComponent(this);
        this.process = new ProcessComponent(this);
        this.rights = new RightsComponent(this);
        this.items = new ItemsComponent(this);

        this.mapping.init();

        this.trade = new TradeComponent(this);
        this.game = new GameComponent(this);
        this.entities = new EntityComponent(this);
        this.bots = new RoomBotComponent(this);
        this.pets = new PetComponent(this);

        // Cache the group.
        this.group = GroupManager.getInstance().get(this.getData().getGroupId());

        this.setAttribute("loadTime", System.currentTimeMillis());

        if(this.data.getType() == RoomType.PUBLIC) {
            RoomQueue.getInstance().addQueue(this.getId(), 0);
        }

        this.log.debug("Room loaded");

        return this;
    }

    public boolean isIdle() {
        if (this.idleTicks < 600 && this.getEntities().realPlayerCount() > 0) {
            this.idleTicks = 0;
        } else {
            if (this.idleTicks >= 600) {
                return true;
            } else {
                this.idleTicks += 10;
            }
        }

        return false;
    }

    private boolean forcedUnload = false;

    public void setIdleNow() {
        this.idleTicks = 600;
        this.forcedUnload = true;
    }

    public synchronized void dispose() {
        if (this.isDisposed) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        this.items.commit();

        this.isDisposed = true;

        this.process.stop();
        this.itemProcess.stop();
        this.game.stop();

        this.game.dispose();
        this.entities.dispose();
        this.items.dispose();
        this.bots.dispose();
        this.mapping.dispose();

        if(this.data.getType() == RoomType.PUBLIC) {
            RoomQueue.getInstance().removeQueue(this.getId());
            RoomSpectator.getInstance().removeSpectatorsFromRoom(this.getId());
        }

        if(this.forcedUnload) {
            RoomManager.getInstance().removeData(this.getId());
        }

        if (this.yesVotes != null) {
            this.yesVotes.clear();
        }

        if (this.noVotes != null) {
            this.noVotes.clear();
        }

        long timeTaken = System.currentTimeMillis() - currentTime;

        if(timeTaken >= 250) {
            this.log.warn("Room [" + this.getData().getId() + "][" + this.getData().getName() + "] took " + timeTaken + "ms to dispose");
        }

        this.log.debug("Room has been disposed");
    }

    public void tick() {
        WiredTriggerAtGivenTime.executeTriggers(this, this.wiredTimer.incrementAndGet());

        if (this.rights != null) {
            this.rights.tick();
        }

        if (this.mapping != null) {
            this.mapping.tick();
        }
    }

    public void startQuestion(String question) {
        this.question = question;
        this.yesVotes = Sets.newConcurrentHashSet();
        this.noVotes = Sets.newConcurrentHashSet();

        this.getEntities().broadcastMessage(new QuickPollMessageComposer(question));
    }

    public void endQuestion() {
        this.question = null;

        this.getEntities().broadcastMessage(new QuickPollResultsMessageComposer(this.yesVotes.size(), this.noVotes.size()));

        if (this.yesVotes != null) {
            this.yesVotes.clear();
        }

        if (this.noVotes != null) {
            this.noVotes.clear();
        }
    }

    public int getWiredTimer() {
        return this.wiredTimer.get();
    }

    public void resetWiredTimer() {
        this.wiredTimer.set(0);
    }

    public RoomPromotion getPromotion() {
        return RoomManager.getInstance().getRoomPromotions().get(this.getId());
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        if (this.attributes.containsKey(attributeKey)) {
            this.removeAttribute(attributeKey);
        }

        this.attributes.put(attributeKey, attributeValue);
    }

    @Override
    public Object getAttribute(String attributeKey) {
        return this.attributes.get(attributeKey);
    }

    @Override
    public boolean hasAttribute(String attributeKey) {
        return this.attributes.containsKey(attributeKey);
    }

    @Override
    public void removeAttribute(String attributeKey) {
        this.attributes.remove(attributeKey);
    }

    public int getId() {
        return this.data.getId();
    }

    public RoomData getData() {
        return this.data;
    }

    public RoomModel getModel() {
        return this.model;
    }

    public ProcessComponent getProcess() {
        return this.process;
    }

    public ItemProcessComponent getItemProcess() {
        return this.itemProcess;
    }

    public ItemsComponent getItems() {
        return this.items;
    }

    public TradeComponent getTrade() {
        return this.trade;
    }

    public RightsComponent getRights() {
        return this.rights;
    }

    public RoomBotComponent getBots() {
        return this.bots;
    }

    public PetComponent getPets() {
        return this.pets;
    }

    public GameComponent getGame() {
        return this.game;
    }

    public EntityComponent getEntities() {
        return this.entities;
    }

    public RoomMapping getMapping() {
        return this.mapping;
    }

    public Group getGroup() {
        if(this.group == null || this.group.getData() == null) return null;

        return this.group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    public boolean hasRoomMute() {
        return this.attributes.containsKey("room_muted") && (boolean) this.attributes.get("room_muted");
    }

    public void setRoomMute(boolean mute) {
        if (this.attributes.containsKey("room_muted")) {
            this.attributes.replace("room_muted", mute);
        } else {
            this.attributes.put("room_muted", mute);
        }
    }

    public Set<Integer> getRatings() {
        return ratings;
    }

    public Set<Integer> getNoVotes() {
        return noVotes;
    }

    public Set<Integer> getYesVotes() {
        return yesVotes;
    }

    public String getQuestion() {
        return question;
    }
}
