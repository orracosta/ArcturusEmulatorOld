package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.VisitorBot;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.items.ICycleable;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagField;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.pets.*;
import com.eu.habbo.habbohotel.users.*;
import com.eu.habbo.habbohotel.wired.*;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorMessagesComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.inventory.*;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollAnswerComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollStartComposer;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.messages.outgoing.rooms.items.*;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.MutedWhisperComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurniturePickedUpEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRolledEvent;
import com.eu.habbo.plugin.events.rooms.RoomLoadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUnloadingEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserRightsTakenEvent;
import com.eu.habbo.plugin.events.users.UserRolledEvent;
import com.eu.habbo.threading.runnables.YouAreAPirate;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;
import io.netty.util.internal.ConcurrentSet;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Room implements Comparable<Room>, ISerialize, Runnable
{
    //Configuration. Loaded from database & updated accordingly.
    public static boolean HABBO_CHAT_DELAY = false;
    public static int MAXIMUM_BOTS = 10;
    public static int MAXIMUM_PETS = 10;
    public static int HAND_ITEM_TIME = 10;
    public static int IDLE_CYCLES = 240;
    public static int IDLE_CYCLES_KICK = 480;
    public static String PREFIX_FORMAT = "[<font color=\"%color%\">%prefix%</font>] ";

    private static final TIntObjectHashMap<RoomMoodlightData> defaultMoodData = new TIntObjectHashMap<RoomMoodlightData>();

    static
    {
        for(int i = 1; i <= 3; i++)
        {
            RoomMoodlightData data = RoomMoodlightData.fromString("");
            data.setId(i);
            defaultMoodData.put(i, data);
        }
    }
    private int id;
    private int ownerId;
    private String ownerName;
    private String name;
    private String description;
    private RoomLayout layout;
    private boolean overrideModel;
    private String layoutName;
    private String password;
    private RoomState state;
    private int usersMax;
    private volatile int score;
    private volatile int category;

    private String floorPaint;
    private String wallPaint;
    private String backgroundPaint;

    private int wallSize;
    private int wallHeight;
    private int floorSize;

    private int guild;

    private String tags;
    private volatile boolean publicRoom;
    private volatile boolean staffPromotedRoom;
    private volatile boolean allowPets;
    private volatile boolean allowPetsEat;
    private volatile boolean allowWalkthrough;
    private volatile boolean allowBotsWalk;
    private volatile boolean allowEffects;
    private volatile boolean hideWall;
    private volatile int chatMode;
    private volatile int chatWeight;
    private volatile int chatSpeed;
    private volatile int chatDistance;
    private volatile int chatProtection;
    private volatile int muteOption;
    private volatile int kickOption;
    private volatile int banOption;
    private volatile int pollId;
    private volatile boolean promoted;
    private volatile int tradeMode;
    private volatile boolean moveDiagonally;
    private volatile boolean jukeboxActive;

    private final ConcurrentHashMap<Integer, Habbo> currentHabbos = new ConcurrentHashMap<>(3);
    private final TIntObjectMap<Habbo>  habboQueue = TCollections.synchronizedMap(new TIntObjectHashMap<Habbo>(0));
    private final TIntObjectMap<Bot>  currentBots = TCollections.synchronizedMap(new TIntObjectHashMap<Bot>(0));
    private final TIntObjectMap<AbstractPet>  currentPets = TCollections.synchronizedMap(new TIntObjectHashMap<AbstractPet>(0));
    private final THashSet<RoomTrade> activeTrades;
    private final TIntArrayList rights;
    private final TIntArrayList traxItems;
    private final TIntIntHashMap mutedHabbos;
    private final TIntObjectHashMap<RoomBan> bannedHabbos;
    private final ConcurrentSet<Game> games;
    private final TIntObjectMap<String> furniOwnerNames;
    private final TIntIntMap furniOwnerCount;
    private final TIntObjectMap<RoomMoodlightData> moodlightData;
    private final THashSet<String> wordFilterWords;
    private final TIntObjectMap<HabboItem> roomItems;
    private final THashMap<WiredHighscoreScoreType, THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>>> wiredHighscoreData;
    private RoomPromotion promotion;

    private volatile boolean needsUpdate;
    private volatile boolean loaded;
    private volatile boolean preLoaded;
    private volatile int idleCycles;
    private volatile int unitCounter;
    private volatile int rollerSpeed;
    private volatile long rollerCycle = System.currentTimeMillis();
    private volatile int lastTimerReset = Emulator.getIntUnixTimestamp();
    private volatile boolean muted;

    private RoomSpecialTypes roomSpecialTypes;

    private final Object loadLock = new Object();
    public final Object roomUnitLock = new Object();

    //Use appropriately. Could potentially cause memory leaks when used incorrectly.
    public volatile boolean preventUnloading = false;
    public volatile boolean preventUncaching = false;
    public THashMap<Integer, TIntArrayList> waterTiles;
    public final ConcurrentHashMap<RoomTile, THashSet<HabboItem>> tileCache = new ConcurrentHashMap<RoomTile, THashSet<HabboItem>>();
    public ConcurrentSet<ServerMessage> scheduledComposers = new ConcurrentSet<ServerMessage>();
    public ConcurrentSet<Runnable> scheduledTasks = new ConcurrentSet<Runnable>();
    public String wordQuiz = "";
    public int noVotes = 0;
    public int yesVotes = 0;
    public int wordQuizEnd = 0;
    public final List<Integer> userVotes;
    public ScheduledFuture roomCycleTask;
    private TraxManager traxManager;

    public Room(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.ownerId = set.getInt("owner_id");
        this.ownerName = set.getString("owner_name");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.password = set.getString("password");
        this.state = RoomState.valueOf(set.getString("state").toUpperCase());
        this.usersMax = set.getInt("users_max");
        this.score = set.getInt("score");
        this.category = set.getInt("category");
        this.floorPaint = set.getString("paper_floor");
        this.wallPaint = set.getString("paper_wall");
        this.backgroundPaint = set.getString("paper_landscape");
        this.wallSize = set.getInt("thickness_wall");
        this.wallHeight = set.getInt("wall_height");
        this.floorSize = set.getInt("thickness_floor");
        this.tags = set.getString("tags");
        this.publicRoom = set.getBoolean("is_public");
        this.staffPromotedRoom = set.getBoolean("is_staff_picked");
        this.allowPets = set.getBoolean("allow_other_pets");
        this.allowPetsEat = set.getBoolean("allow_other_pets_eat");
        this.allowWalkthrough = set.getBoolean("allow_walkthrough");
        this.hideWall = set.getBoolean("allow_hidewall");
        this.chatMode = set.getInt("chat_mode");
        this.chatWeight = set.getInt("chat_weight");
        this.chatSpeed = set.getInt("chat_speed");
        this.chatDistance = set.getInt("chat_hearing_distance");
        this.chatProtection = set.getInt("chat_protection");
        this.muteOption = set.getInt("who_can_mute");
        this.kickOption = set.getInt("who_can_kick");
        this.banOption = set.getInt("who_can_ban");
        this.pollId = set.getInt("poll_id");
        this.guild = set.getInt("guild_id");
        this.rollerSpeed = set.getInt("roller_speed");
        this.overrideModel = set.getString("override_model").equals("1");
        this.layoutName = set.getString("model");
        this.promoted = set.getString("promoted").equals("1");
        this.jukeboxActive = set.getString("jukebox_active").equals("1");

        this.bannedHabbos = new TIntObjectHashMap<RoomBan>();
        this.traxItems = new TIntArrayList();

//        for (String itemString : set.getString("trax").split(";"))
//        {
//            try
//            {
//                this.traxItems.add(Integer.valueOf(itemString));
//            }
//            catch (Exception e)
//            {
//                Emulator.getLogging().logErrorLine(e);
//            }
//        }
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_promotions WHERE room_id = ? AND end_timestamp > ? LIMIT 1"))
        {
            if(this.promoted)
            {
                statement.setInt(1, this.id);
                statement.setInt(2, Emulator.getIntUnixTimestamp());

                try (ResultSet promotionSet = statement.executeQuery())
                {
                    this.promoted = false;
                    if (promotionSet.next())
                    {
                        this.promoted = true;
                        this.promotion = new RoomPromotion(this, promotionSet);
                    }
                }
            }

            this.loadBans(connection);
        }
        this.tradeMode = set.getInt("trade_mode");
        this.moveDiagonally = set.getString("move_diagonally").equals("1");

        this.preLoaded = true;
        this.allowBotsWalk = true;
        this.allowEffects = true;
        this.furniOwnerNames = TCollections.synchronizedMap(new TIntObjectHashMap<String>(0));
        this.furniOwnerCount = TCollections.synchronizedMap(new TIntIntHashMap(0));
        this.roomItems = TCollections.synchronizedMap(new TIntObjectHashMap<HabboItem>(0));
        this.wordFilterWords = new THashSet<String>(0);
        this.moodlightData = new TIntObjectHashMap<RoomMoodlightData>(defaultMoodData);

        for(String s : set.getString("moodlight_data").split(";"))
        {
            RoomMoodlightData data = RoomMoodlightData.fromString(s);
            this.moodlightData.put(data.getId(), data);
        }

        this.mutedHabbos = new TIntIntHashMap();
        this.games = new ConcurrentSet<Game>();

        this.activeTrades = new THashSet<RoomTrade>(0);
        this.rights = new TIntArrayList();
        this.wiredHighscoreData = new THashMap<WiredHighscoreScoreType, THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>>>();
        this.userVotes = new ArrayList<>();
    }

    public synchronized void loadData()
    {
        synchronized (this.loadLock)
        {
            if (!this.preLoaded || this.loaded)
                return;

            this.preLoaded = false;

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
            {
                synchronized ( this.roomUnitLock)
                {
                    this.unitCounter = 0;
                    this.currentHabbos.clear();
                    this.currentPets.clear();
                    this.currentBots.clear();
                }

                this.roomSpecialTypes = new RoomSpecialTypes();

                try
                {
                    this.loadLayout();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadRights(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadItems(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadHeightmap();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadBots(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadPets(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadWordFilter(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadWiredData(connection);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                this.idleCycles = 0;
                this.loaded = true;

                this.roomCycleTask = Emulator.getThreading().getService().scheduleAtFixedRate(this, 500, 500, TimeUnit.MILLISECONDS);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }

            this.traxManager = new TraxManager(this);

            if (this.jukeboxActive)
            {
                this.traxManager.play(0);
                for (HabboItem item : this.roomSpecialTypes.getItemsOfType(InteractionJukeBox.class))
                {
                    item.setExtradata("1");
                    this.updateItem(item);
                }
            }
        }

        Emulator.getPluginManager().fireEvent(new RoomLoadedEvent(this));
    }

    private synchronized void loadLayout()
    {
        if (this.layout == null)
        {
            if (this.overrideModel)
            {
                this.layout = Emulator.getGameEnvironment().getRoomManager().loadCustomLayout(this);
            }
            else
            {
                this.layout = Emulator.getGameEnvironment().getRoomManager().loadLayout(this.layoutName, this);
            }
        }
    }

    private synchronized void loadHeightmap()
    {
        if (this.layout != null)
        {
            for (short x = 0; x < this.layout.getMapSizeX(); x++)
            {
                for (short y = 0; y < this.layout.getMapSizeY(); y++)
                {
                    RoomTile tile = this.layout.getTile(x, y);
                    if (tile != null)
                    {
                        this.updateTile(tile);
                    }
                }
            }
        }
        else
        {
            Emulator.getLogging().logErrorLine("Unknown Room Layout for Room (ID: " + this.id + ")");
        }
    }

    private synchronized void loadItems(Connection connection) throws SQLException
    {
        this.roomItems.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.addHabboItem(Emulator.getGameEnvironment().getItemManager().loadHabboItem(set));
                }
            }
        }
    }

    private synchronized void loadWiredData(Connection connection) throws SQLException
    {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id, wired_data FROM items WHERE room_id = ? AND wired_data<>?"))
        {
            statement.setInt(1, this.id);
            statement.setString(2, "");

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    try
                    {
                        HabboItem item = this.getHabboItem(set.getInt("id"));

                        if (item != null && item instanceof InteractionWired)
                        {
                            ((InteractionWired) item).loadWiredData(set, this);
                        }
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    private synchronized void loadBots(Connection connection) throws SQLException
    {
        this.currentBots.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON bots.user_id = users.id WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    Bot b = Emulator.getGameEnvironment().getBotManager().loadBot(set);

                    if (b != null)
                    {
                        b.setRoom(this);
                        b.setRoomUnit(new RoomUnit());
                        b.getRoomUnit().setPathFinderRoom(this);
                        b.getRoomUnit().setLocation(this.layout.getTile((short) set.getInt("x"), (short) set.getInt("y")));
                        if (b.getRoomUnit().getCurrentLocation() == null)
                        {
                            b.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                            b.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
                        }
                        else
                        {
                            b.getRoomUnit().setZ(set.getDouble("z"));
                            b.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
                        }
                        b.getRoomUnit().setRoomUnitType(RoomUnitType.BOT);
                        b.getRoomUnit().setDanceType(DanceType.values()[set.getInt("dance")]);
                        b.getRoomUnit().setCanWalk(set.getBoolean("freeroam"));
                        b.getRoomUnit().setInRoom(true);
                        this.addBot(b);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private synchronized void loadPets(Connection connection) throws SQLException
    {
        this.currentPets.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username as pet_owner_name, users_pets.* FROM users_pets INNER JOIN users ON users_pets.user_id = users.id WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    try
                    {
                        AbstractPet pet = PetManager.loadPet(set);
                        pet.setRoom(this);
                        pet.setRoomUnit(new RoomUnit());
                        pet.getRoomUnit().setPathFinderRoom(this);
                        pet.getRoomUnit().setLocation(this.layout.getTile((short) set.getInt("x"), (short) set.getInt("y")));
                        if (pet.getRoomUnit().getCurrentLocation() == null)
                        {
                            pet.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                            pet.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
                        }
                        else
                        {
                            pet.getRoomUnit().setZ(set.getDouble("z"));
                            pet.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
                        }
                        pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
                        pet.getRoomUnit().setCanWalk(true);
                        this.addPet(pet);

                        this.getFurniOwnerNames().put(pet.getUserId(), set.getString("pet_owner_name"));
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
    }

    private synchronized void loadWordFilter(Connection connection) throws SQLException
    {
        this.wordFilterWords.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_wordfilter WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.wordFilterWords.add(set.getString("word"));
                }
            }
        }
    }

    public void updateTile(RoomTile tile)
    {
        if (tile != null)
        {
            tile.setStackHeight(this.getStackHeight(tile.x, tile.y, false));
            tile.setWalkable(this.canWalkAt(tile));
        }
    }

    public void updateTiles(THashSet<RoomTile> tiles)
    {
        for (RoomTile tile : tiles)
        {
            this.tileCache.remove(tile);
            tile.setStackHeight(this.getStackHeight(tile.x, tile.y, false));
            tile.setWalkable(this.canWalkAt(tile));
        }

        this.sendComposer(new UpdateStackHeightComposer(tiles).compose());
    }

    public boolean tileWalkable(RoomTile t)
    {
        return this.tileWalkable(t.x, t.y);
    }

    public boolean tileWalkable(short x, short y)
    {
        boolean walkable = this.layout.tileWalkable(x, y);

        if (walkable)
        {
            if (this.hasHabbosAt(x, y) && !this.allowWalkthrough)
            {
                walkable = false;
            }
        }
        return walkable; //&& (!this.allowWalkthrough && !this.hasHabbosAt(x, y)));
        //if(this.layout.tileWalkable(x, y))
//        {
//            if(!this.allowWalkthrough)
//            {
//                if (hasHabbosAt(x, y))
//                    return false;
//
//                if (hasBotsAt(x, y))
//                    return false;
//            }
//
//            THashSet<HabboItem> items = getItemsAt(x, y);
//            if(items.isEmpty())
//                return true;
//
//            boolean walkAble = true;
//            for(HabboItem item : items)
//            {
//                if(!item.getBaseItem().allowWalk())
//                    walkAble = false;
//
//                if(item.getBaseItem().allowSit())
//                    walkAble = false;
//
//                if(item instanceof InteractionGate)
//                {
//                    walkAble = item.isWalkable();
//                }
//                else if(item instanceof InteractionGameGate)
//                {
//                    walkAble = item.isWalkable();
//                }
//                else if(item instanceof InteractionFreezeBlock)
//                {
//                    walkAble = item.isWalkable();
//                }
//            }
//
//            return walkAble;
//            HabboItem item = this.getTopItemAt(x, y);
//
//            return item == null || item.isWalkable();
//        }
//        return false;
    }

    public void pickUpItem(HabboItem item, Habbo picker)
    {
        if(item == null)
            return;

        if(Emulator.getPluginManager().isRegistered(FurniturePickedUpEvent.class, true))
        {
            Event furniturePickedUpEvent = new FurniturePickedUpEvent(item, picker);
            Emulator.getPluginManager().fireEvent(furniturePickedUpEvent);

            if(furniturePickedUpEvent.isCancelled())
                return;
        }

        this.removeHabboItem(item.getId());
        item.onPickUp(this);
        item.setRoomId(0);
        item.needsUpdate(true);

        if (item.getBaseItem().getType() == FurnitureType.FLOOR)
        {
            this.sendComposer(new RemoveFloorItemComposer(item).compose());

            THashSet<RoomTile> updatedTiles = new THashSet<RoomTile>();
            Rectangle rectangle = RoomLayout.getRectangle(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

            for (short x = (short)rectangle.x; x < rectangle.x + rectangle.getWidth(); x++)
            {
                for (short y = (short)rectangle.y; y < rectangle.y + rectangle.getHeight(); y++)
                {
                    double stackHeight = this.getStackHeight(x, y, false);
                    RoomTile tile = this.layout.getTile(x, y);

                    if (tile != null)
                    {
                        tile.setStackHeight(stackHeight);
                        updatedTiles.add(tile);
                    }
                }
            }
            this.sendComposer(new UpdateStackHeightComposer(updatedTiles).compose());
            this.updateTiles(updatedTiles);
            for (RoomTile tile : updatedTiles)
            {
                this.updateHabbosAt(tile.x, tile.y);
            }
        }
        else if (item.getBaseItem().getType() == FurnitureType.WALL)
        {
            this.sendComposer(new RemoveWallItemComposer(item).compose());
        }

        Habbo habbo = (picker != null && picker.getHabboInfo().getId() == item.getId() ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId()));
        if (habbo != null) {
            habbo.getInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
        Emulator.getThreading().run(item);
    }

    public void updateHabbosAt(Rectangle rectangle)
    {
        for(short i = (short) rectangle.x; i < rectangle.x + rectangle.width; i++)
        {
            for(short j = (short) rectangle.y; j < rectangle.y + rectangle.height; j++)
            {
                this.updateHabbosAt(i, j);
            }
        }
    }

    public void updateHabbo(Habbo habbo)
    {
        HabboItem item = this.getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

        if((item == null && !habbo.getRoomUnit().cmdSit) || (item != null && !item.getBaseItem().allowSit()))
            habbo.getRoomUnit().getStatus().remove("sit");


        if(item != null)
        {
            if(item.getBaseItem().allowSit())
            {
                habbo.getRoomUnit().setZ(item.getZ());
            }
            else
            {
                habbo.getRoomUnit().setZ(item.getZ() + item.getBaseItem().getHeight());
            }
        }

        this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
    }

    public void updateHabbosAt(short x, short y)
    {
        THashSet<Habbo> habbos = this.getHabbosAt(x, y);

        HabboItem item = this.getTopItemAt(x, y);

        THashSet<RoomUnit> roomUnits = new THashSet<RoomUnit>();
        for (Habbo habbo : habbos)
        {
            if ((item == null && !habbo.getRoomUnit().cmdSit) || (item != null && !item.getBaseItem().allowSit()))
                habbo.getRoomUnit().getStatus().remove("sit");

            if ((item == null && !habbo.getRoomUnit().cmdLay) || (item != null && !item.getBaseItem().allowLay()))
                habbo.getRoomUnit().getStatus().remove("lay");

            if(item != null)
            {
                if(item.getBaseItem().allowSit())
                {
                    habbo.getRoomUnit().setZ(item.getZ());
                }
                else
                {
                    habbo.getRoomUnit().setZ(item.getZ() + item.getBaseItem().getHeight());

                    if (item.getBaseItem().allowLay())
                    {
                        habbo.getRoomUnit().getStatus().put("lay", (item.getZ() + item.getBaseItem().getHeight()) + "");
                    }
                }
            }
            else
            {
                habbo.getRoomUnit().setZ(habbo.getRoomUnit().getCurrentLocation().getStackHeight());
            }
            roomUnits.add(habbo.getRoomUnit());
        }

        this.sendComposer(new RoomUserStatusComposer(roomUnits, true).compose());
    }

    public void pickupPetsForHabbo(Habbo habbo)
    {
        THashSet<AbstractPet> pets = new THashSet<AbstractPet>();

        synchronized (this.currentPets)
        {
            for(AbstractPet pet : this.currentPets.valueCollection())
            {
                if(pet.getUserId() == habbo.getHabboInfo().getId())
                {
                    pets.add((Pet) pet);
                }
            }
        }

        for(AbstractPet pet : pets)
        {
            if(pet instanceof Pet)
            {
                pet.setRoom(null);
                pet.needsUpdate = true;

                habbo.
                        getInventory().
                        getPetsComponent().
                        addPet((Pet) pet);

                this.sendComposer(new RoomUserRemoveComposer(pet.getRoomUnit()).compose());
                habbo.getClient().sendResponse(new AddPetComposer(pet));
            }
            this.currentPets.remove(pet.getId());
        }

    }

    public void startTrade(Habbo userOne, Habbo userTwo)
    {
        RoomTrade trade = new RoomTrade(userOne, userTwo, this);
        synchronized (this.activeTrades)
        {
            this.activeTrades.add(trade);
        }
    }

    public void stopTrade(RoomTrade trade)
    {
        synchronized (this.activeTrades)
        {
            this.activeTrades.remove(trade);
        }
    }

    public RoomTrade getActiveTradeForHabbo(Habbo user)
    {
        synchronized (this.activeTrades)
        {
            for (RoomTrade trade : this.activeTrades)
            {
                for (RoomTradeUser habbo : trade.getRoomTradeUsers())
                {
                    if (habbo.getHabbo() == user)
                        return trade;
                }
            }
        }
        return null;
    }

    public synchronized void dispose()
    {
        synchronized (this.loadLock)
        {
            if (this.preventUnloading)
                return;

            if (Emulator.getPluginManager().fireEvent(new RoomUnloadingEvent(this)).isCancelled())
                return;

            if (this.loaded)
            {
                if (!this.traxManager.disposed())
                {
                    this.traxManager.dispose();
                }
                try
                {
                    this.roomCycleTask.cancel(false);
                    this.scheduledTasks.clear();
                    this.scheduledComposers.clear();
                    this.loaded = false;

                    this.tileCache.clear();

                    synchronized (this.mutedHabbos)
                    {
                        this.mutedHabbos.clear();
                    }

                    for (Game game : this.games)
                    {
                        game.stop();
                    }
                    this.games.clear();

                    synchronized (this.roomItems)
                    {
                        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

                        for (int i = this.roomItems.size(); i-- > 0; )
                        {
                            try
                            {
                                iterator.advance();

                                if (iterator.value().needsUpdate())
                                    iterator.value().run();
                            } catch (NoSuchElementException e)
                            {
                                break;
                            }
                        }
                    }

                    if (this.roomSpecialTypes != null)
                    {
                        this.roomSpecialTypes.dispose();
                    }

                    synchronized (this.roomItems)
                    {
                        this.roomItems.clear();
                    }

                    synchronized (this.habboQueue)
                    {
                        this.habboQueue.clear();
                    }

                    //   SAFE


                    for (Habbo habbo : this.currentHabbos.values())
                    {
                        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
                    }

                    this.sendComposer(new HotelViewComposer().compose());

                    this.currentHabbos.clear();


                    TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();

                    for (int i = this.currentBots.size(); i-- > 0; )
                    {
                        try
                        {
                            botIterator.advance();
                            botIterator.value().needsUpdate(true);
                            Emulator.getThreading().run(botIterator.value());
                        } catch (NoSuchElementException e)
                        {
                            Emulator.getLogging().logErrorLine(e);
                            break;
                        }
                    }

                    TIntObjectIterator<AbstractPet> petIterator = this.currentPets.iterator();
                    for (int i = this.currentPets.size(); i-- > 0; )
                    {
                        try
                        {
                            petIterator.advance();
                            if (petIterator.value() instanceof Pet)
                            {
                                petIterator.value().needsUpdate = true;
                                Emulator.getThreading().run(petIterator.value());
                            }
                        } catch (NoSuchElementException e)
                        {
                            Emulator.getLogging().logErrorLine(e);
                            break;
                        }
                    }

                    this.currentBots.clear();
                    this.currentPets.clear();
                } catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }
            }

            this.wordQuiz = "";
            this.yesVotes = 0;
            this.noVotes = 0;
            this.updateDatabaseUserCount();
            this.preLoaded = true;
            this.layout = null;
        }

        Emulator.getPluginManager().fireEvent(new RoomUnloadedEvent(this));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Room o)
    {
        if (o.getUserCount() != this.getUserCount())
        {
            return  o.getCurrentHabbos().size() - this.getCurrentHabbos().size();
        }

        return this.id - o.id ;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.id);
        message.appendString(this.name);
        if(this.isPublicRoom())
        {
            message.appendInt(0);
            message.appendString("");
        }
        else
        {
            message.appendInt(this.ownerId);
            message.appendString(this.ownerName);
        }
        message.appendInt(this.state.getState());
        message.appendInt(this.getUserCount());
        message.appendInt(this.usersMax);
        message.appendString(this.description);
        message.appendInt(0);
        message.appendInt(this.score);
        message.appendInt(0);
        message.appendInt(this.category);
        message.appendInt(this.tags.split(";").length);
        for(String s : this.tags.split(";"))
        {
            message.appendString(s);
        }

        /*if(g != null)
        {
            message.appendInt(58);
            message.appendInt(g.getId());
            message.appendString(g.getName());
            message.appendString(g.getBadge());
        }
        else
        {
            message.appendInt(56);
        }*/

        int base = 0;

        if(this.getGuildId() > 0)
        {
            base = base | 2;
        }

        if (this.isPromoted())
        {
            base = base | 4;
        }

        if(!this.isPublicRoom())
        {
            base = base | 8;
        }

        message.appendInt(base);


        //message.appendString("a.png"); //Camera image.

        if(this.getGuildId() > 0)
        {
            Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(this.getGuildId());
            if (g != null)
            {
                message.appendInt(g.getId());
                message.appendString(g.getName());
                message.appendString(g.getBadge());
            }
            else
            {
                message.appendInt(0);
                message.appendString("");
                message.appendString("");
            }
        }

        if(this.promoted)
        {
            message.appendString(this.promotion.getTitle());
            message.appendString(this.promotion.getDescription());
            message.appendInt((this.promotion.getEndTimestamp() - Emulator.getIntUnixTimestamp()) / 60);
        }

    }

    public static final Comparator SORT_SCORE = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {

            if (!(o1 instanceof Room && o2 instanceof Room))
                return 0;

            return ((Room) o2).getScore() - ((Room) o1).getScore();
        }
    };

    public static final Comparator SORT_ID = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {

            if (!(o1 instanceof Room && o2 instanceof Room))
                return 0;

            return ((Room) o2).getId() - ((Room) o1).getId();
        }
    };

    @Override
    public void run()
    {
        long millis = System.currentTimeMillis();

        synchronized (this.loadLock)
        {
            if (this.loaded)
            {
                try
                {
                    Emulator.getThreading().run(
                            new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    cycle();
                                }
                            });
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }
            }
        }

        this.save();
    }

    public void save()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET name = ?, description = ?, password = ?, state = ?, users_max = ?, category = ?, score = ?, paper_floor = ?, paper_wall = ?, paper_landscape = ?, thickness_wall = ?, wall_height = ?, thickness_floor = ?, moodlight_data = ?, tags = ?, allow_other_pets = ?, allow_other_pets_eat = ?, allow_walkthrough = ?, allow_hidewall = ?, chat_mode = ?, chat_weight = ?, chat_speed = ?, chat_hearing_distance = ?, chat_protection =?, who_can_mute = ?, who_can_kick = ?, who_can_ban = ?, poll_id = ?, guild_id = ?, roller_speed = ?, override_model = ?, is_staff_picked = ?, promoted = ?, trade_mode = ?, move_diagonally = ?, owner_id = ?, owner_name = ?, jukebox_active = ? WHERE id = ?"))
            {
                statement.setString(1, this.name);
                statement.setString(2, this.description);
                statement.setString(3, this.password);
                statement.setString(4, this.state.name().toLowerCase());
                statement.setInt(5, this.usersMax);
                statement.setInt(6, this.category);
                statement.setInt(7, this.score);
                statement.setString(8, this.floorPaint);
                statement.setString(9, this.wallPaint);
                statement.setString(10, this.backgroundPaint);
                statement.setInt(11, this.wallSize);
                statement.setInt(12, this.wallHeight);
                statement.setInt(13, this.floorSize);
                String moodLightData = "";

                int id = 1;
                for(RoomMoodlightData data : this.moodlightData.valueCollection())
                {
                    data.setId(id);
                    moodLightData += data.toString() + ";";
                    id++;
                }

                statement.setString(14, moodLightData);
                statement.setString(15, this.tags);
                statement.setString(16, this.allowPets ? "1" : "0");
                statement.setString(17, this.allowPetsEat ? "1" : "0");
                statement.setString(18, this.allowWalkthrough ? "1" : "0");
                statement.setString(19, this.hideWall ? "1" : "0");
                statement.setInt(20, this.chatMode);
                statement.setInt(21, this.chatWeight);
                statement.setInt(22, this.chatSpeed);
                statement.setInt(23, this.chatDistance);
                statement.setInt(24, this.chatProtection);
                statement.setInt(25, this.muteOption);
                statement.setInt(26, this.kickOption);
                statement.setInt(27, this.banOption);
                statement.setInt(28, this.pollId);
                statement.setInt(29, this.guild);
                statement.setInt(30, this.rollerSpeed);
                statement.setString(31, this.overrideModel ? "1" : "0");
                statement.setString(32, this.staffPromotedRoom ? "1" : "0");
                statement.setString(33, this.promoted ? "1" : "0");
                statement.setInt(34, this.tradeMode);
                statement.setString(35, this.moveDiagonally ? "1" : "0");
                statement.setInt(36, this.ownerId);
                statement.setString(37, this.ownerName);
                statement.setString(38, this.jukeboxActive ? "1" : "0");
                statement.setInt(39, this.id);
                statement.executeUpdate();
                this.needsUpdate = false;
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    private void updateDatabaseUserCount()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE rooms SET users = ? WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, this.currentHabbos.size());
            statement.setInt(2, this.id);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private void cycle()
    {
        final boolean[] foundRightHolder = {false};

        this.tileCache.clear();

        boolean loaded = false;
        synchronized (this.loadLock)
        {
            loaded = this.loaded;
        }
        if(loaded)
        {
            if (!this.scheduledTasks.isEmpty())
            {
                ConcurrentSet<Runnable> tasks = this.scheduledTasks;
                this.scheduledTasks = new ConcurrentSet<>();

                for (Runnable runnable : tasks)
                {
                    Emulator.getThreading().run(runnable);
                }
            }

            if (!this.currentHabbos.isEmpty())
            {
                this.idleCycles = 0;

                THashSet<RoomUnit> updatedUnit = new THashSet<RoomUnit>();
                ArrayList<Habbo> toKick = new ArrayList<Habbo>();

                final Room room = this;

                final long millis = System.currentTimeMillis();
                if(this.rollerSpeed != -1 && this.rollerCycle >= this.rollerSpeed)
                {
                    this.rollerCycle = 0;

                    THashSet<MessageComposer> messages = new THashSet<MessageComposer>();

                    this.roomSpecialTypes.getRollers().forEachValue(new TObjectProcedure<InteractionRoller>()
                    {
                        @Override
                        public boolean execute(InteractionRoller roller)
                        {
                            //if(Double.compare(roller.getZ(), this.layout.getHeightAtSquare(roller.getX(), roller.getY())) != 0)
                            // {
                            //    continue;
                            //}

                            HabboItem newRoller = null;

                            THashSet<Habbo> habbosOnRoller = getHabbosAt(roller.getX(), roller.getY());
                            THashSet<HabboItem> itemsOnRoller = getItemsAt(roller.getX(), roller.getY(), roller.getZ() + roller.getBaseItem().getHeight());

                            itemsOnRoller.remove(roller);

                            if (habbosOnRoller.isEmpty())
                            {
                                if (itemsOnRoller.isEmpty())
                                    return true;
                            }

                            RoomTile roomTile = layout.getTileInFront(layout.getTile(roller.getX(), roller.getY()), roller.getRotation());

                            if (roomTile == null)
                                return true;

                            if(!layout.tileExists(roomTile.x, roomTile.y))
                                return true;

                            if (roomTile.state == RoomTileState.BLOCKED)
                                return true;

                            if (!getHabbosAt(roomTile.x, roomTile.y).isEmpty())
                                return true;

                            THashSet<HabboItem> itemsNewTile = getItemsAt(roomTile.x, roomTile.y);
                            itemsNewTile.removeAll(itemsOnRoller);
                            List<HabboItem> toRemove = new ArrayList<HabboItem>();
                            for (HabboItem item : itemsOnRoller)
                            {
                                if (item.getX() != roller.getX() || item.getY() != roller.getY())
                                {
                                    toRemove.add(item);
                                }
                            }
                            itemsOnRoller.removeAll(toRemove);
                            HabboItem topItem = getTopItemAt(roomTile.x, roomTile.y);

                            boolean allowUsers = true;
                            boolean allowFurniture = true;
                            boolean stackContainsRoller = false;

                            for (HabboItem item : itemsNewTile)
                            {
                                if (!(item.getBaseItem().allowWalk() || item.getBaseItem().allowSit()) && !(item instanceof InteractionGate && item.getExtradata().equals("1")))
                                {
                                    allowUsers = false;
                                }
                                if (item instanceof InteractionRoller)
                                {
                                    newRoller = item;
                                    stackContainsRoller = true;

                                    if (itemsNewTile.size() > 1 && item != topItem)
                                    {
                                        allowUsers = false;
                                        allowFurniture = false;
                                        continue;
                                    }

                                    break;
                                }
                                else
                                {
                                    allowFurniture = false;
                                }
                            }

                            double zOffset = 0;
                            if(newRoller == null)
                            {
                                zOffset = -roller.getBaseItem().getHeight();
                            }

                            if (allowFurniture || (!allowFurniture && !stackContainsRoller))
                            {
                                /**
                                 * Redneck way to prevent checking ifregistered each time.
                                 */
                                Event furnitureRolledEvent = null;

                                if (Emulator.getPluginManager().isRegistered(FurnitureRolledEvent.class, true))
                                {
                                    furnitureRolledEvent = new FurnitureRolledEvent(null, null, null);
                                }

                                //if (newRoller == null || topItem == newRoller)
                                {
                                    for (HabboItem item : itemsOnRoller)
                                    {
                                        //if (item.getX() == roller.getX() && item.getY() == roller.getY())
                                        {
                                            if (furnitureRolledEvent != null)
                                            {
                                                furnitureRolledEvent = new FurnitureRolledEvent(item, roller, roomTile);
                                                Emulator.getPluginManager().fireEvent(furnitureRolledEvent);

                                                if (furnitureRolledEvent.isCancelled())
                                                    continue;
                                            }

                                            if (item != roller)
                                                messages.add(new FloorItemOnRollerComposer(item, roller, roomTile, zOffset, room));
                                        }
                                    }
                                }
                            }

                            if (allowUsers)
                            {
                                Event roomUserRolledEvent = null;

                                if(Emulator.getPluginManager().isRegistered(UserRolledEvent.class, true))
                                {
                                    roomUserRolledEvent = new UserRolledEvent(null, null, null);
                                }

                                for (Habbo habbo : habbosOnRoller)
                                {
                                    if (stackContainsRoller && !allowFurniture && !(topItem != null && topItem.isWalkable()))
                                        continue;

                                    if (!habbo.getRoomUnit().getStatus().containsKey("mv"))
                                    {
                                        RoomTile tile = roomTile.copy();
                                        tile.setStackHeight(habbo.getRoomUnit().getZ() + zOffset);
                                        if(roomUserRolledEvent != null)
                                        {
                                            roomUserRolledEvent = new UserRolledEvent(habbo, roller, tile);
                                            Emulator.getPluginManager().fireEvent(roomUserRolledEvent);

                                            if(roomUserRolledEvent.isCancelled())
                                                continue;
                                        }

                                        messages.add(new RoomUnitOnRollerComposer(habbo.getRoomUnit(), roller, tile, room));
                                    }

                                    if(habbo.getRoomUnit().getStatus().containsKey("sit"))
                                        habbo.getRoomUnit().sitUpdate = true;
                                }
                            }

                            return true;
                        }
                    });

                    for (MessageComposer message : messages)
                    {
                        this.sendComposer(message.compose());
                    }

                    for(HabboItem pyramid : this.roomSpecialTypes.getItemsOfType(InteractionPyramid.class))
                    {
                        if(pyramid instanceof InteractionPyramid)
                        {
                            int currentTime = Emulator.getIntUnixTimestamp();

                            if(((InteractionPyramid) pyramid).getNextChange() < currentTime)
                            {
                                ((InteractionPyramid) pyramid).change(this);
                            }
                        }
                    }
                }
                else
                {
                    this.rollerCycle++;
                }

                for (Habbo habbo : this.currentHabbos.values())
                {
                    if (!foundRightHolder[0])
                    {
                        foundRightHolder[0] = habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE;
                    }

                    if (habbo.getRoomUnit().getHandItem() > 0 && millis - habbo.getRoomUnit().getHandItemTimestamp() > (Room.HAND_ITEM_TIME * 1000))
                    {
                        giveHandItem(habbo, 0);
                    }

                    if (Emulator.getConfig().getBoolean("hotel.rooms.auto.idle"))
                    {
                        if (!habbo.getRoomUnit().isIdle())
                        {
                            habbo.getRoomUnit().increaseIdleTimer();

                            if (habbo.getRoomUnit().isIdle())
                            {
                                sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
                            }
                        }
                        else
                        {
                            habbo.getRoomUnit().increaseIdleTimer();

                            if (!isOwner(habbo) && habbo.getRoomUnit().getIdleTimer() >= Room.IDLE_CYCLES_KICK)
                            {
                                UserExitRoomEvent event = new UserExitRoomEvent(habbo, UserExitRoomEvent.UserExitRoomReason.KICKED_IDLE);
                                Emulator.getPluginManager().fireEvent(event);

                                if (!event.isCancelled())
                                {
                                    toKick.add(habbo);
                                }
                            }
                        }
                    }

                    if (habbo.getHabboStats().mutedBubbleTracker && habbo.getHabboStats().allowTalk())
                    {
                        habbo.getHabboStats().mutedBubbleTracker = false;
                        this.sendComposer(new RoomUserIgnoredComposer(habbo, RoomUserIgnoredComposer.UNIGNORED).compose());
                    }
                    if (!habbo.hasPermission("acc_chat_no_flood") && habbo.getHabboStats().chatCounter > 0)
                    {
                        //if (habbo.getRoomUnit().talkTimeOut == 0 || currentTimestamp - habbo.getRoomUnit().talkTimeOut < 0)
                        {
                            habbo.getHabboStats().chatCounter--;

                            if (habbo.getHabboStats().chatCounter > 3 && !this.hasRights(habbo))
                            {
                                if (chatProtection == 0)
                                {
                                    floodMuteHabbo(habbo, 30);
                                }
                                else if (chatProtection == 1 && habbo.getHabboStats().chatCounter > 4)
                                {
                                    floodMuteHabbo(habbo, 30);
                                }
                                else if (chatProtection == 2 && habbo.getHabboStats().chatCounter > 5)
                                {
                                    floodMuteHabbo(habbo, 30);
                                }
                            }
                        }
                    }
                    else
                    {
                        habbo.getHabboStats().chatCounter = 0;
                    }

                    if (habbo.getRoomUnit().getStatus().containsKey("sign"))
                    {
                        sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
                        habbo.getRoomUnit().getStatus().remove("sign");
                    }

                    if (habbo.getRoomUnit().isWalking() && habbo.getRoomUnit().getPath() != null && !habbo.getRoomUnit().getPath().isEmpty())
                    {
                        if (!habbo.getRoomUnit().cycle(room))
                        {
                            updatedUnit.add(habbo.getRoomUnit());
                            continue;
                        }
                    }
                    else
                    {
                        if (habbo.getRoomUnit().getStatus().containsKey("mv") && !habbo.getRoomUnit().animateWalk)
                        {
                            habbo.getRoomUnit().getStatus().remove("mv");

                            updatedUnit.add(habbo.getRoomUnit());
                        }

                        if (!habbo.getRoomUnit().isWalking() && !habbo.getRoomUnit().cmdSit)
                        {
                            HabboItem topItem = getLowestChair(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

                            if (topItem == null || !topItem.getBaseItem().allowSit())
                            {
                                if (habbo.getRoomUnit().getStatus().containsKey("sit"))
                                {
                                    habbo.getRoomUnit().getStatus().remove("sit");
                                    updatedUnit.add(habbo.getRoomUnit());
                                }
                            }
                            else
                            {
                                if (!habbo.getRoomUnit().getStatus().containsKey("sit") || habbo.getRoomUnit().sitUpdate)
                                {
                                    dance(habbo, DanceType.NONE);
                                    if (topItem instanceof InteractionMultiHeight)
                                    {
                                        habbo.getRoomUnit().getStatus().put("sit", Item.getCurrentHeight(topItem) * 1.0D + "");
                                    }
                                    else
                                    {
                                        habbo.getRoomUnit().getStatus().put("sit", topItem.getBaseItem().getHeight() * 1.0D + "");
                                    }
                                    habbo.getRoomUnit().setZ(topItem.getZ());
                                    habbo.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                                    updatedUnit.add(habbo.getRoomUnit());
                                    habbo.getRoomUnit().sitUpdate = false;
                                }
                            }
                        }
                    }

                    if (!habbo.getRoomUnit().isWalking() && !habbo.getRoomUnit().cmdLay)
                    {
                        HabboItem topItem = getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

                        if (topItem == null || !topItem.getBaseItem().allowLay())
                        {
                            if (habbo.getRoomUnit().getStatus().containsKey("lay"))
                            {
                                habbo.getRoomUnit().getStatus().remove("lay");
                                updatedUnit.add(habbo.getRoomUnit());
                            }
                        }
                        else
                        {
                            if (!habbo.getRoomUnit().getStatus().containsKey("lay"))
                            {
                                if (topItem instanceof InteractionMultiHeight)
                                {
                                    habbo.getRoomUnit().getStatus().put("lay", Item.getCurrentHeight(topItem) * 1.0D + "");
                                }
                                else
                                {
                                    habbo.getRoomUnit().getStatus().put("lay", topItem.getBaseItem().getHeight() * 1.0D + "");
                                }
                                habbo.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);

                                if (topItem.getRotation() == 0 || topItem.getRotation() == 4)
                                {
                                    habbo.getRoomUnit().setLocation(layout.getTile(habbo.getRoomUnit().getX(), topItem.getY()));
                                    //habbo.getRoomUnit().setOldY(topItem.getY());
                                }
                                else
                                {
                                    habbo.getRoomUnit().setLocation(layout.getTile(topItem.getX(), habbo.getRoomUnit().getY()));
                                    //habbo.getRoomUnit().setOldX(topItem.getX());
                                }
                                updatedUnit.add(habbo.getRoomUnit());
                            }
                        }
                    }
                }

                if(!toKick.isEmpty())
                {
                    for(Habbo habbo : toKick)
                    {
                        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
                    }
                }

                if (!this.currentBots.isEmpty())
                {
                    TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
                    for (int i = this.currentBots.size(); i-- > 0; )
                    {
                        try
                        {
                            final Bot bot;
                            try
                            {
                                botIterator.advance();
                                bot = botIterator.value();
                            }
                            catch (Exception e)
                            {
                                break;
                            }

                            if (!this.allowBotsWalk && bot.getRoomUnit().isWalking())
                            {
                                bot.getRoomUnit().stopWalking();
                                updatedUnit.add(bot.getRoomUnit());
                                continue;
                            }

                            botIterator.value().cycle(this.allowBotsWalk);

                            if (!bot.getRoomUnit().isWalking())
                            {
                                if (bot.getRoomUnit().getStatus().containsKey("mv"))
                                {
                                    bot.getRoomUnit().getStatus().remove("mv");

                                    updatedUnit.add(bot.getRoomUnit());
                                }

                                if (!bot.getRoomUnit().isWalking() && !bot.getRoomUnit().cmdSit)
                                {
                                    HabboItem topItem = this.getLowestChair(bot.getRoomUnit().getX(), bot.getRoomUnit().getY());

                                    if (topItem == null || !topItem.getBaseItem().allowSit())
                                    {
                                        if (bot.getRoomUnit().getStatus().containsKey("sit"))
                                        {
                                            bot.getRoomUnit().getStatus().remove("sit");
                                            updatedUnit.add(bot.getRoomUnit());
                                        }
                                    }
                                    else
                                    {
                                        if (!bot.getRoomUnit().getStatus().containsKey("sit"))
                                        {
                                            if (topItem instanceof InteractionMultiHeight)
                                            {
                                                bot.getRoomUnit().getStatus().put("sit", Item.getCurrentHeight(topItem) * 1.0D + "");
                                            } else
                                            {
                                                bot.getRoomUnit().getStatus().put("sit", topItem.getBaseItem().getHeight() * 1.0D + "");
                                            }
                                            bot.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                                            updatedUnit.add(bot.getRoomUnit());
                                        }
                                    }
                                }
                            }

                            if (!bot.getRoomUnit().isWalking() && !bot.getRoomUnit().cmdLay)
                            {
                                HabboItem topItem = this.getTopItemAt(bot.getRoomUnit().getX(), bot.getRoomUnit().getY());

                                if (topItem == null || !topItem.getBaseItem().allowLay())
                                {
                                    if (bot.getRoomUnit().getStatus().containsKey("lay"))
                                    {
                                        bot.getRoomUnit().getStatus().remove("lay");
                                        updatedUnit.add(bot.getRoomUnit());
                                    }
                                } else
                                {
                                    if (!bot.getRoomUnit().getStatus().containsKey("lay"))
                                    {
                                        if (topItem instanceof InteractionMultiHeight)
                                        {
                                            bot.getRoomUnit().getStatus().put("lay", Item.getCurrentHeight(topItem) * 1.0D + "");
                                        } else
                                        {
                                            bot.getRoomUnit().getStatus().put("lay", topItem.getBaseItem().getHeight() * 1.0D + "");
                                        }
                                        bot.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                                        bot.getRoomUnit().setLocation(this.layout.getTile(topItem.getX(), topItem.getY()));
                                        updatedUnit.add(bot.getRoomUnit());
                                    }
                                }
                            }

                            if (!botIterator.value().getRoomUnit().cycle(this))
                                updatedUnit.add(botIterator.value().getRoomUnit());
                        }
                        catch (NoSuchElementException e)
                        {
                            Emulator.getLogging().logErrorLine(e);
                            break;
                        }
                    }
                }

                if (!this.currentPets.isEmpty())
                {
                    if (this.allowBotsWalk)
                    {
                        TIntObjectIterator<AbstractPet> petIterator = this.currentPets.iterator();
                        for (int i = this.currentPets.size(); i-- > 0; )
                        {
                            try
                            {
                                petIterator.advance();
                            }
                            catch (NoSuchElementException e)
                            {
                                Emulator.getLogging().logErrorLine(e);
                                break;
                            }

                            AbstractPet pet = petIterator.value();
                            if (pet instanceof Pet)
                            {
//                                    if (pet.getRoomUnit().getStatus().containsKey("mv") && pet.getRoomUnit().isAtGoal())
//                                    {
//                                        System.out.println("Clearing Pet Walking Animation...");
//                                        pet.getRoomUnit().getStatus().remove("mv");
//                                        updatedUnit.add(pet.getRoomUnit());
//                                    }

                                if (!pet.getRoomUnit().isWalking() && pet.getRoomUnit().getStatus().containsKey("mv"))
                                {
                                    pet.getRoomUnit().getStatus().remove("mv");
                                    updatedUnit.add(pet.getRoomUnit());
                                    continue;
                                }

                                ((Pet) pet).cycle();

                                if (((Pet) pet).packetUpdate)
                                {
                                    updatedUnit.add(pet.getRoomUnit());
                                    ((Pet) pet).packetUpdate = false;
                                }

                                if (((Pet) pet).getTask() == PetTasks.RIDE)
                                {
                                    if (pet instanceof HorsePet)
                                    {
                                        HorsePet horse = ((HorsePet) pet);
                                        if (horse.getRider() != null)
                                        {
                                            if (!horse.getRider().getRoomUnit().getCurrentLocation().equals(horse.getRoomUnit().getCurrentLocation()))
                                            {
                                                horse.getRoomUnit().setGoalLocation(horse.getRider().getRoomUnit().getCurrentLocation());
                                            }
                                        }
                                    }
                                }

//                                    if (pet.getRoomUnit().isAtGoal())
//                                    {
//                                        if (!(pet.getRoomUnit().getStatus().isEmpty() || (pet.getRoomUnit().getStatus().size() >= 1 && (pet.getRoomUnit().getStatus().containsKey("lay") || pet.getRoomUnit().getStatus().containsKey("gst")))) || ((Pet) pet).packetUpdate)
//                                        {
//                                            updatedUnit.add(pet.getRoomUnit());
//                                            ((Pet) pet).packetUpdate = false;
//                                        }
//                                    }

                                if (pet.getRoomUnit().isWalking() && pet.getRoomUnit().getPath().size() == 1 && pet.getRoomUnit().getStatus().containsKey("gst"))
                                {
                                    pet.getRoomUnit().getStatus().remove("gst");
                                    updatedUnit.add(pet.getRoomUnit());
                                }

                                if (!pet.getRoomUnit().cycle(this))
                                {
                                    updatedUnit.add(pet.getRoomUnit());
                                }
                            }
                        }
                    }
                }


                if(!updatedUnit.isEmpty())
                {
                    this.sendComposer(new RoomUserStatusComposer(updatedUnit, true).compose());
                }

                for (ICycleable task : this.roomSpecialTypes.getCycleTasks())
                {
                    task.cycle(this);
                }

                this.traxManager.cycle();
            }
            else
            {

                if(this.idleCycles < 60)
                    this.idleCycles++;
                else
                    this.dispose();
            }
        }

        synchronized (this.habboQueue)
        {
            if (!this.habboQueue.isEmpty() && !foundRightHolder[0])
            {
                this.habboQueue.forEachEntry(new TIntObjectProcedure<Habbo>()
                {
                    @Override
                    public boolean execute(int a, Habbo b)
                    {
                        if (b.isOnline())
                        {
                            if (b.getHabboInfo().getRoomQueueId() == getId())
                            {
                                b.getClient().sendResponse(new RoomAccessDeniedComposer(""));
                            }
                        }

                        return true;
                    }
                });

                this.habboQueue.clear();
            }
        }

        if (!this.scheduledComposers.isEmpty())
        {
            for (ServerMessage message : scheduledComposers)
            {
                this.sendComposer(message);
            }

            this.scheduledComposers.clear();
        }
    }

    public int getId()
    {
        return this.id;
    }

    public int getOwnerId()
    {
        return this.ownerId;
    }

    public void setOwnerId(int ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerName()
    {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public RoomLayout getLayout()
    {
        return this.layout;
    }

    public void setLayout(RoomLayout layout)
    {
        this.layout = layout;
    }

    public boolean hasCustomLayout()
    {
        return this.overrideModel;
    }

    public void setHasCustomLayout(boolean overrideModel)
    {
        this.overrideModel = overrideModel;
    }

    public String getPassword()
    {
        return this.password;
    }

    public RoomState getState()
    {
        return this.state;
    }

    public int getUsersMax()
    {
        return this.usersMax;
    }

    public int getScore()
    {
        return this.score;
    }

    public int getCategory()
    {
        return this.category;
    }

    public String getFloorPaint()
    {
        return this.floorPaint;
    }

    public String getWallPaint()
    {
        return this.wallPaint;
    }

    public String getBackgroundPaint()
    {
        return this.backgroundPaint;
    }

    public int getWallSize()
    {
        return this.wallSize;
    }

    public int getWallHeight()
    {
        return this.wallHeight;
    }

    public void setWallHeight(int wallHeight)
    {
        this.wallHeight = wallHeight;
    }

    public int getFloorSize()
    {
        return this.floorSize;
    }

    public String getTags()
    {
        return this.tags;
    }

    public int getTradeMode()
    {
        return this.tradeMode;
    }

    public boolean moveDiagonally()
    {
        return this.moveDiagonally;
    }

    public void moveDiagonally(boolean moveDiagonally)
    {
        this.moveDiagonally = moveDiagonally;
        this.layout.moveDiagonally(this.moveDiagonally);
        this.needsUpdate = true;
    }

    public int getGuildId()
    {
        return this.guild;
    }

    public boolean hasGuild()
    {
        return this.guild != 0;
    }

    public void setGuild(int guild)
    {
        this.guild = guild;
    }

    public String getGuildName()
    {
        if (this.hasGuild())
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);

            if (guild != null)
            {
                return guild.getName();
            }
        }

        return "";
    }

    public boolean isPublicRoom()
    {
        return this.publicRoom;
    }

    public boolean isStaffPromotedRoom()
    {
        return this.staffPromotedRoom;
    }

    public void setStaffPromotedRoom(boolean staffPromotedRoom)
    {
        this.staffPromotedRoom = staffPromotedRoom;
    }

    public boolean isAllowPets()
    {
        return this.allowPets;
    }

    public boolean isAllowPetsEat()
    {
        return this.allowPetsEat;
    }

    public boolean isAllowWalkthrough()
    {
        return this.allowWalkthrough;
    }

    public boolean isAllowBotsWalk()
    {
        return this.allowBotsWalk;
    }

    public boolean isAllowEffects()
    {
        return this.allowEffects;
    }

    public void setAllowEffects(boolean allowEffects)
    {
        this.allowEffects = allowEffects;
    }

    public boolean isHideWall()
    {
        return this.hideWall;
    }

    public Color getBackgroundTonerColor()
    {
        Color color = new Color(0, 0, 0);
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i > 0; i--)
        {
            try
            {
                iterator.advance();
                HabboItem object = iterator.value();

                if (object instanceof InteractionBackgroundToner)
                {
                    String[] extraData = object.getExtradata().split(":");

                    if (extraData.length == 4)
                    {
                        if (extraData[0].equalsIgnoreCase("1"))
                        {
                            return Color.getHSBColor(Integer.valueOf(extraData[1]), Integer.valueOf(extraData[2]), Integer.valueOf(extraData[3]));
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
        }

        return color;
    }

    public int getChatMode()
    {
        return this.chatMode;
    }

    public int getChatWeight()
    {
        return this.chatWeight;
    }

    public int getChatSpeed()
    {
        return this.chatSpeed;
    }

    public int getChatDistance()
    {
        return this.chatDistance;
    }

    public void setName(String name)
    {
        this.name = name;

        if (this.name.length() > 50)
        {
            this.name = this.name.substring(0, 50);
        }

        if (this.hasGuild())
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);

            if (guild != null)
            {
                guild.setRoomName(name);
            }
        }
    }

    public void setDescription(String description)
    {
        this.description = description;

        if (this.description.length() > 250)
        {
            this.description = this.description.substring(0, 250);
        }
    }

    public void setPassword(String password)
    {
        this.password = password;

        if (this.password.length() > 20)
        {
            this.password = this.password.substring(0, 20);
        }
    }

    public void setState(RoomState state)
    {
        this.state = state;
    }

    public void setUsersMax(int usersMax)
    {
        this.usersMax = usersMax;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public void setCategory(int category)
    {
        this.category = category;
    }

    public void setFloorPaint(String floorPaint)
    {
        this.floorPaint = floorPaint;
    }

    public void setWallPaint(String wallPaint)
    {
        this.wallPaint = wallPaint;
    }

    public void setBackgroundPaint(String backgroundPaint)
    {
        this.backgroundPaint = backgroundPaint;
    }

    public void setWallSize(int wallSize)
    {
        this.wallSize = wallSize;
    }

    public void setFloorSize(int floorSize)
    {
        this.floorSize = floorSize;
    }

    public void setTags(String tags)
    {
        this.tags = tags;
    }

    public void setTradeMode(int tradeMode)
    {
        this.tradeMode = tradeMode;
    }

    public void setAllowPets(boolean allowPets)
    {
        this.allowPets = allowPets;
    }

    public void setAllowPetsEat(boolean allowPetsEat)
    {
        this.allowPetsEat = allowPetsEat;
    }

    public void setAllowWalkthrough(boolean allowWalkthrough)
    {
        this.allowWalkthrough = allowWalkthrough;
    }

    public void setAllowBotsWalk(boolean allowBotsWalk)
    {
        this.allowBotsWalk = allowBotsWalk;
    }

    public void setHideWall(boolean hideWall)
    {
        this.hideWall = hideWall;
    }

    public void setChatMode(int chatMode)
    {
        this.chatMode = chatMode;
    }

    public void setChatWeight(int chatWeight)
    {
        this.chatWeight = chatWeight;
    }

    public void setChatSpeed(int chatSpeed)
    {
        this.chatSpeed = chatSpeed;
    }

    public void setChatDistance(int chatDistance)
    {
        this.chatDistance = chatDistance;
    }

    public int getChatProtection()
    {
        return this.chatProtection;
    }

    public void setChatProtection(int chatProtection)
    {
        this.chatProtection = chatProtection;
    }

    public int getMuteOption()
    {
        return this.muteOption;
    }

    public void setMuteOption(int muteOption)
    {
        this.muteOption = muteOption;
    }

    public int getKickOption()
    {
        return this.kickOption;
    }

    public void setKickOption(int kickOption)
    {
        this.kickOption = kickOption;
    }

    public int getBanOption()
    {
        return this.banOption;
    }

    public void setBanOption(int banOption)
    {
        this.banOption = banOption;
    }

    public int getPollId()
    {
        return this.pollId;
    }

    public int getRollerSpeed()
    {
        return this.rollerSpeed;
    }

    public String[] filterAnything()
    {
        return new String[]{ getOwnerName(), getGuildName(), getDescription(), getPromotionDesc()};
    }

    public boolean isPromoted()
    {
        this.promoted = this.promotion != null && this.promotion.getEndTimestamp() > Emulator.getIntUnixTimestamp();
        this.needsUpdate = true;

        return this.promoted;
    }

    public RoomPromotion getPromotion()
    {
        return this.promotion;
    }

    public String getPromotionDesc()
    {
        if (this.promotion != null)
        {
            return this.promotion.getDescription();
        }

        return "";
    }

    public void createPromotion(String title, String description)
    {
        this.promoted = true;

        if(this.promotion == null)
        {
            this.promotion = new RoomPromotion(this, title, description, Emulator.getIntUnixTimestamp() + (120 * 60));
        }
        else
        {
            this.promotion.setTitle(title);
            this.promotion.setDescription(description);
            this.promotion.setEndTimestamp(Emulator.getIntUnixTimestamp() + (120 * 60));
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_promotions (room_id, title, description, end_timestamp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = ?, description = ?, end_timestamp = ?"))
        {
            statement.setInt(1, this.id);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setInt(4, this.promotion.getEndTimestamp());
            statement.setString(5, this.promotion.getTitle());
            statement.setString(6, this.promotion.getDescription());
            statement.setInt(7, this.promotion.getEndTimestamp());
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.needsUpdate = true;
    }

    public void setRollerSpeed(int rollerSpeed)
    {
        this.rollerSpeed = rollerSpeed;
        this.rollerCycle = 0;
        this.needsUpdate = true;
    }

    public void setPollId(int pollId)
    {
        this.pollId = pollId;
    }

    public boolean addGame(Game game)
    {
        synchronized (this.games)
        {
            return this.games.add(game);
        }
    }

    public boolean deleteGame(Game game)
    {
        game.stop();
        synchronized (this.games)
        {
            return this.games.remove(game);
        }
    }

    public Game getGame(Class<? extends Game> gameType)
    {
        synchronized (this.games)
        {
            for(Game game : this.games)
            {
                if(gameType.isInstance(game))
                {
                    return game;
                }
            }
        }

        return null;
    }

    public int getUserCount()
    {
		return this.currentHabbos.size();
    }

    public ConcurrentHashMap<Integer, Habbo> getCurrentHabbos()
    {
        return this.currentHabbos;
    }

    public Collection<Habbo> getHabbos()
    {
        return this.currentHabbos.values();
    }

    public TIntObjectMap<Habbo> getHabboQueue()
    {
        return this.habboQueue;
    }

    public TIntObjectMap<String> getFurniOwnerNames()
    {
        return this.furniOwnerNames;
    }

    public String getFurniOwnerName(int userId)
    {
        return this.furniOwnerNames.get(userId);
    }

    public TIntIntMap getFurniOwnerCount()
    {
        return this.furniOwnerCount;
    }

    public TIntObjectMap<RoomMoodlightData> getMoodlightData()
    {
        return this.moodlightData;
    }

    public int getLastTimerReset()
    {
        return this.lastTimerReset;
    }

    public void setLastTimerReset(int lastTimerReset)
    {
        this.lastTimerReset = lastTimerReset;
    }

    public void addToQueue(Habbo habbo)
    {
        synchronized (this.habboQueue)
        {
            this.habboQueue.put(habbo.getHabboInfo().getId(), habbo);
        }
    }

    public boolean removeFromQueue(Habbo habbo)
    {
        try
        {
            this.sendComposer(new HideDoorbellComposer(habbo.getHabboInfo().getUsername()).compose());

            synchronized (this.habboQueue)
            {
                return this.habboQueue.remove(habbo.getHabboInfo().getId()) != null;
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return true;
    }

    public TIntObjectMap<Bot> getCurrentBots()
    {
        return this.currentBots;
    }

    public TIntObjectMap<AbstractPet> getCurrentPets()
    {
        return this.currentPets;
    }

    public THashSet<String> getWordFilterWords()
    {
        return this.wordFilterWords;
    }

    public RoomSpecialTypes getRoomSpecialTypes()
    {
        return this.roomSpecialTypes;
    }

    public boolean isPreLoaded()
    {
        return this.preLoaded;
    }

    public boolean isLoaded()
    {
        return this.loaded;
    }

    public void setNeedsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }

    public TIntArrayList getRights()
    {
        return rights;
    }

    public boolean isMuted()
    {
        return this.muted;
    }

    public void setMuted(boolean muted)
    {
        this.muted = muted;
    }

    public TraxManager getTraxManager()
    {
        return this.traxManager;
    }

    public void addHabboItem(HabboItem item)
    {
        if(item == null)
            return;

        try
        {
            this.roomItems.put(item.getId(), item);
        }
        catch (Exception e)
        {

        }

        synchronized (this.furniOwnerCount)
        {
            this.furniOwnerCount.put(item.getUserId(), this.furniOwnerCount.get(item.getUserId()) + 1);
        }

        synchronized (this.furniOwnerNames)
        {
            if (!this.furniOwnerNames.containsKey(item.getUserId()))
            {
                HabboInfo habbo = HabboManager.getOfflineHabboInfo(item.getUserId());

                if (habbo != null)
                {
                    this.furniOwnerNames.put(item.getUserId(), habbo.getUsername());
                }
                else
                {
                    Emulator.getLogging().logDebugLine("Failed to find username for item (ID:" + item.getId() + ", UserID: " + item.getUserId() + ")" );
                }
            }
        }

        //TODO: Move this list
        synchronized (this.roomSpecialTypes)
        {
            if (item instanceof ICycleable)
            {
                this.roomSpecialTypes.addCycleTask((ICycleable)item);
            }

            if (item instanceof InteractionWiredTrigger)
            {
                this.roomSpecialTypes.addTrigger((InteractionWiredTrigger) item);
            } else if (item instanceof InteractionWiredEffect)
            {
                this.roomSpecialTypes.addEffect((InteractionWiredEffect) item);
            } else if (item instanceof InteractionWiredCondition)
            {
                this.roomSpecialTypes.addCondition((InteractionWiredCondition) item);
            } else if (item instanceof InteractionWiredExtra)
            {
                this.roomSpecialTypes.addExtra((InteractionWiredExtra) item);
            } else if (item instanceof InteractionBattleBanzaiTeleporter)
            {
                this.roomSpecialTypes.addBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
            } else if (item instanceof InteractionRoller)
            {
                this.roomSpecialTypes.addRoller((InteractionRoller) item);
            } else if (item instanceof InteractionGameScoreboard)
            {
                this.roomSpecialTypes.addGameScoreboard((InteractionGameScoreboard) item);
            } else if (item instanceof InteractionGameGate)
            {
                this.roomSpecialTypes.addGameGate((InteractionGameGate) item);
            } else if (item instanceof InteractionGameTimer)
            {
                this.roomSpecialTypes.addGameTimer((InteractionGameTimer) item);
            } else if (item instanceof InteractionFreezeExitTile)
            {
                this.roomSpecialTypes.addFreezeExitTile((InteractionFreezeExitTile) item);
            } else if (item instanceof InteractionNest)
            {
                this.roomSpecialTypes.addNest((InteractionNest) item);
            } else if (item instanceof InteractionPetDrink)
            {
                this.roomSpecialTypes.addPetDrink((InteractionPetDrink) item);
            } else if (item instanceof InteractionPetFood)
            {
                this.roomSpecialTypes.addPetFood((InteractionPetFood) item);
            } else if (item instanceof InteractionMoodLight)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionPyramid)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMusicDisc)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionBattleBanzaiSphere)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTalkingFurniture)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWater)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionWaterItem)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionMuteArea)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagPole)
            {
                this.roomSpecialTypes.addUndefined(item);
            } else if (item instanceof InteractionTagField)
            {
                this.roomSpecialTypes.addUndefined(item);
            }else if (item instanceof InteractionJukeBox)
            {
                this.roomSpecialTypes.addUndefined(item);
            }else if (item instanceof InteractionPetBreedingNest)
            {
                this.roomSpecialTypes.addUndefined(item);
            }
            else if (item instanceof InteractionBlackHole)
            {
                this.roomSpecialTypes.addUndefined(item);
            }
            else if (item instanceof InteractionWiredHighscore)
            {
                this.roomSpecialTypes.addUndefined(item);
            }
            else if (item instanceof InteractionStickyPole)
            {
                this.roomSpecialTypes.addUndefined(item);
            }
        }
    }

    public HabboItem getHabboItem(int id)
    {
        HabboItem item;
        synchronized (this.roomItems)
        {
            item = this.roomItems.get(id);
        }

        if(item == null)
            item = this.roomSpecialTypes.getBanzaiTeleporter(id);

        if(item == null)
            item = this.roomSpecialTypes.getTrigger(id);

        if(item == null)
            item = this.roomSpecialTypes.getEffect(id);

        if(item == null)
            item = this.roomSpecialTypes.getCondition(id);

        if(item == null)
            item = this.roomSpecialTypes.getGameGate(id);

        if(item == null)
            item = this.roomSpecialTypes.getGameScorebord(id);

        if(item == null)
            item = this.roomSpecialTypes.getGameTimer(id);

        if(item == null)
            item = this.roomSpecialTypes.getFreezeExitTiles().get(id);

        if(item == null)
            item = this.roomSpecialTypes.getRoller(id);

        if(item == null)
            item = this.roomSpecialTypes.getNest(id);

        if(item == null)
            item = this.roomSpecialTypes.getPetDrink(id);

        if(item == null)
            item = this.roomSpecialTypes.getPetFood(id);

        return item;
    }

    void removeHabboItem(int id)
    {
        this.removeHabboItem(this.getHabboItem(id));
    }

    /**
     * Removes an {@see HabboItem} from the list of room items. Does not visually remove it from the room.
     * @param item The item to remove.
     */
    public void removeHabboItem(HabboItem item)
    {
        if (item != null)
        {

            HabboItem i;
            synchronized (this.roomItems)
            {
                i = this.roomItems.remove(item.getId());
            }

            if (i != null)
            {
                synchronized (this.furniOwnerCount)
                {
                    synchronized (this.furniOwnerNames)
                    {
                        int count = this.furniOwnerCount.get(i.getUserId());

                        if (count > 1)
                            this.furniOwnerCount.put(i.getUserId(), count - 1);
                        else
                        {
                            this.furniOwnerCount.remove(i.getUserId());
                            this.furniOwnerNames.remove(i.getUserId());
                        }
                    }
                }

                if (item instanceof ICycleable)
                {
                    this.roomSpecialTypes.removeCycleTask((ICycleable) item);
                }

                if (item instanceof InteractionBattleBanzaiTeleporter)
                {
                    this.roomSpecialTypes.removeBanzaiTeleporter((InteractionBattleBanzaiTeleporter) item);
                }
                else if (item instanceof InteractionWiredTrigger)
                {
                    this.roomSpecialTypes.removeTrigger((InteractionWiredTrigger) item);
                }
                else if (item instanceof InteractionWiredEffect)
                {
                    this.roomSpecialTypes.removeEffect((InteractionWiredEffect) item);
                }
                else if (item instanceof InteractionWiredCondition)
                {
                    this.roomSpecialTypes.removeCondition((InteractionWiredCondition) item);
                }
                else if (item instanceof InteractionWiredExtra)
                {
                    this.roomSpecialTypes.removeExtra((InteractionWiredExtra) item);
                }
                else if (item instanceof InteractionRoller)
                {
                    this.roomSpecialTypes.removeRoller((InteractionRoller) item);
                }
                else if (item instanceof InteractionGameScoreboard)
                {
                    this.roomSpecialTypes.removeScoreboard((InteractionGameScoreboard) item);
                }
                else if (item instanceof InteractionGameGate)
                {
                    this.roomSpecialTypes.removeGameGate((InteractionGameGate) item);
                }
                else if (item instanceof InteractionGameTimer)
                {
                    this.roomSpecialTypes.removeGameTimer((InteractionGameTimer) item);
                }
                else if (item instanceof InteractionFreezeExitTile)
                {
                    this.roomSpecialTypes.removeFreezeExitTile((InteractionFreezeExitTile) item);
                }
                else if (item instanceof InteractionNest)
                {
                    this.roomSpecialTypes.removeNest((InteractionNest) item);
                }
                else if (item instanceof InteractionPetDrink)
                {
                    this.roomSpecialTypes.removePetDrink((InteractionPetDrink) item);
                }
                else if (item instanceof InteractionPetFood)
                {
                    this.roomSpecialTypes.removePetFood((InteractionPetFood) item);
                }
                else if (item instanceof InteractionMoodLight)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionPyramid)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionMusicDisc)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionBattleBanzaiSphere)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionTalkingFurniture)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionWaterItem)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionWater)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionMuteArea)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionTagPole)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionTagField)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionJukeBox)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionPetBreedingNest)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionBlackHole)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionWiredHighscore)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
                else if (item instanceof InteractionStickyPole)
                {
                    this.roomSpecialTypes.removeUndefined(item);
                }
            }
        }
    }

    public THashSet<HabboItem> getFloorItems()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            try
            {
                iterator.advance();
            }
            catch (Exception e)
            {
                break;
            }

            if (iterator.value().getBaseItem().getType() == FurnitureType.FLOOR)
                items.add(iterator.value());

        }

        /*items.addAll(this.roomSpecialTypes.getBanzaiTeleporters());
        items.addAll(this.roomSpecialTypes.getNests());
        items.addAll(this.roomSpecialTypes.getPetDrinks());
        items.addAll(this.roomSpecialTypes.getPetFoods());
        items.addAll(this.roomSpecialTypes.getRollers());
        items.addAll(this.roomSpecialTypes.getTriggers());
        items.addAll(this.roomSpecialTypes.getEffects());
        items.addAll(this.roomSpecialTypes.getConditions());*/

            return items;

    }

    public THashSet<HabboItem> getWallItems()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            try
            {
                iterator.advance();
            }
            catch (Exception e)
            {
                break;
            }

            if (iterator.value().getBaseItem().getType() == FurnitureType.WALL)
                items.add(iterator.value());
        }

        return items;

    }

    public void addHabbo(Habbo habbo)
    {
		synchronized (this.roomUnitLock)
		{
			habbo.getRoomUnit().setId(this.unitCounter);
			this.currentHabbos.put(habbo.getHabboInfo().getId(), habbo);
			this.unitCounter++;
			this.updateDatabaseUserCount();
		}
    }

    public void kickHabbo(Habbo habbo, boolean alert)
    {
        if(alert)
        {
            habbo.getClient().sendResponse(new GenericErrorMessagesComposer(GenericErrorMessagesComposer.KICKED_OUT_OF_THE_ROOM));
        }

        habbo.getRoomUnit().isKicked = true;
        habbo.getRoomUnit().setGoalLocation(this.layout.getDoorTile());

        if (habbo.getRoomUnit().getPath() == null || habbo.getRoomUnit().getPath().size() <= 1 || this.isPublicRoom())
        {
            habbo.getRoomUnit().setCanWalk(true);
            Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);
        }
    }

    public void removeHabbo(Habbo habbo)
    {
        HabboItem item = getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

        if (item != null)
        {
            try
            {
                item.onWalkOff(habbo.getRoomUnit(), this, new Object[]{});
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }

        synchronized (this.roomUnitLock)
        {
            this.currentHabbos.remove(habbo.getHabboInfo().getId());
        }

        if(habbo.getHabboInfo().getCurrentGame() != null)
        {
            if (this.getGame(habbo.getHabboInfo().getCurrentGame()) != null)
            {
                this.getGame(habbo.getHabboInfo().getCurrentGame()).removeHabbo(habbo);
            }
        }

        RoomTrade trade = this.getActiveTradeForHabbo(habbo);

        if(trade != null)
        {
            trade.stopTrade(habbo);
        }

        this.updateDatabaseUserCount();
    }

    public void addBot(Bot bot)
    {
        synchronized (this.roomUnitLock)
        {
            bot.getRoomUnit().setId(this.unitCounter);
            this.currentBots.put(bot.getId(), bot);
            this.unitCounter++;
        }
    }

    public void addPet(AbstractPet pet)
    {
        synchronized (this.roomUnitLock)
        {
            pet.getRoomUnit().setId(this.unitCounter);
            this.currentPets.put(pet.getId(), pet);
            this.unitCounter++;

            Habbo habbo = this.getHabbo(pet.getUserId());
            if (habbo != null)
            {
                this.furniOwnerNames.put(pet.getUserId(), this.getHabbo(pet.getUserId()).getHabboInfo().getUsername());
            }
        }
    }

    public Bot getBot(int botId)
    {
        return this.currentBots.get(botId);
    }

    public List<Bot> getBots(String name)
    {
        List<Bot> bots = new ArrayList<Bot>();

        synchronized (this.currentBots)
        {
            TIntObjectIterator<Bot> iterator = this.currentBots.iterator();

            for(int i = this.currentBots.size(); i-- > 0;)
            {
                try
                {
                    iterator.advance();
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }

                if (iterator.value().getName().equalsIgnoreCase(name))
                    bots.add(iterator.value());
            }
        }

        return bots;
    }

    public boolean hasBotsAt(final int x, final int y)
    {
        final boolean[] result = {false};

        synchronized (this.currentBots)
        {
            this.currentBots.forEachValue(new TObjectProcedure<Bot>()
            {
                @Override
                public boolean execute(Bot object)
                {
                    if (object.getRoomUnit().getX() == x && object.getRoomUnit().getY() == y)
                    {
                        result[0] = true;
                        return false;
                    }

                    return true;
                }
            });
        }

        return result[0];
    }

    public AbstractPet getPet(int petId)
    {
        return this.currentPets.get(petId);
    }

    public AbstractPet getPet(RoomUnit roomUnit)
    {
        TIntObjectIterator<AbstractPet> petIterator = this.currentPets.iterator();

        for(int i = this.currentPets.size(); i-- > 0;)
        {
            try
            {
                petIterator.advance();
            }
            catch (NoSuchElementException e)
            {
                Emulator.getLogging().logErrorLine(e);
                break;
            }

            if(petIterator.value().getRoomUnit() == roomUnit)
                return petIterator.value();
        }

        return null;
    }

    public boolean removeBot(Bot bot)
    {
        synchronized (this.currentBots)
        {
            if (this.currentBots.containsKey(bot.getId()))
            {
                this.currentBots.remove(bot.getId());
                bot.getRoomUnit().setInRoom(false);
                bot.setRoom(null);
                this.sendComposer(new RoomUserRemoveComposer(bot.getRoomUnit()).compose());
                bot.setRoomUnit(null);
                return true;
            }
        }

        return false;
    }

    public void placePet(AbstractPet pet, short x, short y, double z, int rot)
    {
        synchronized (this.currentPets)
        {
            RoomTile tile = this.layout.getTile(x, y);

            if (tile == null)
            {
                tile = this.layout.getDoorTile();
            }

            pet.setRoomUnit(new RoomUnit());
            pet.setRoom(this);
            pet.getRoomUnit().setLocation(tile);
            pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
            pet.getRoomUnit().setCanWalk(true);
            pet.getRoomUnit().setPathFinderRoom(this);
            if (pet.getRoomUnit().getCurrentLocation() == null)
            {
                pet.getRoomUnit().setLocation(this.getLayout().getDoorTile());
                pet.getRoomUnit().setRotation(RoomUserRotation.fromValue(this.getLayout().getDoorDirection()));
            }

            pet.needsUpdate = true;
            this.furniOwnerNames.put(pet.getUserId(), this.getHabbo(pet.getUserId()).getHabboInfo().getUsername());
            this.addPet(pet);
            this.sendComposer(new RoomPetComposer(pet).compose());
        }
    }

    public AbstractPet removePet(int petId)
    {
        return this.currentPets.remove(petId);
    }

    public boolean hasHabbosAt(int x, int y)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (habbo.getRoomUnit().getX() == x && habbo.getRoomUnit().getY() == y)
                return true;
        }
        return false;
    }

    public boolean hasPetsAt(int x, int y)
    {
        synchronized (this.currentPets)
        {
            TIntObjectIterator<AbstractPet> petIterator = this.currentPets.iterator();

            for (int i = this.currentPets.size(); i-- > 0; )
            {
                try
                {
                    petIterator.advance();
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }

                if (petIterator.value().getRoomUnit().getX() == x && petIterator.value().getRoomUnit().getY() == y)
                    return true;
            }
        }

        return false;
    }

    public THashSet<Habbo> getHabbosAt(short x, short y)
    {
        return getHabbosAt(this.layout.getTile(x, y));
    }

    public THashSet<Habbo> getHabbosAt(RoomTile tile)
    {
        THashSet<Habbo> habbos = new THashSet<Habbo>();

        for (Habbo habbo : this.getHabbos())
        {
            if (habbo.getRoomUnit().getCurrentLocation().equals(tile))
                habbos.add(habbo);
        }

        return habbos;
    }

    public THashSet<Habbo> getHabbosOnItem(HabboItem item)
    {
        THashSet<Habbo> habbos = new THashSet<Habbo>();
        for(short x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x++)
        {
            for(short y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y++)
            {
                habbos.addAll(getHabbosAt(x, y));
            }
        }

        return habbos;
    }

    public void teleportHabboToItem(Habbo habbo, HabboItem item)
    {
        this.teleportHabboToLocation(habbo, item.getX(), item.getY(), item.getZ() + item.getBaseItem().getHeight());
    }

    public void teleportHabboToLocation(Habbo habbo, short x, short y)
    {
        this.teleportHabboToLocation(habbo, x, y, 0.0);
    }

    void teleportHabboToLocation(Habbo habbo, short x, short y, double z)
    {
        if (this.loaded)
        {
            RoomTile tile = this.layout.getTile(x, y);
            habbo.getRoomUnit().setLocation(tile);
            habbo.getRoomUnit().setGoalLocation(tile);
            habbo.getRoomUnit().setZ(z);
            this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
        }
    }

    public void muteHabbo(Habbo habbo, int minutes)
    {
        synchronized (this.mutedHabbos)
        {
            this.mutedHabbos.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + (minutes * 60));
        }
    }

    public boolean isMuted(Habbo habbo)
    {
        if (this.isOwner(habbo) || this.hasRights(habbo))
            return false;

        if(this.mutedHabbos.containsKey(habbo.getHabboInfo().getId()))
        {
            boolean time = this.mutedHabbos.get(habbo.getHabboInfo().getId()) > Emulator.getIntUnixTimestamp();

            if (!time)
            {
                this.mutedHabbos.remove(habbo.getHabboInfo().getId());
            }

            return time;
        }

        return false;
    }

    public void habboEntered(Habbo habbo)
    {
        habbo.getRoomUnit().animateWalk = false;

        synchronized (this.currentBots)
        {
            if(habbo.getHabboInfo().getId() != this.getOwnerId())
                return;

            TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();

            for (int i = this.currentBots.size(); i-- > 0; )
            {
                try
                {
                    botIterator.advance();

                    if(botIterator.value() instanceof VisitorBot)
                    {
                        ((VisitorBot)botIterator.value()).onUserEnter(habbo);
                        break;
                    }
                }
                catch (Exception e)
                {
                    break;
                }
            }
        }
    }

    public void floodMuteHabbo(Habbo habbo, int timeOut)
    {
        habbo.getHabboStats().mutedCount++;
        timeOut += (timeOut * (int)Math.ceil(Math.pow(habbo.getHabboStats().mutedCount, 2)));
        habbo.getHabboStats().chatCounter = 0;
        habbo.mute(timeOut);
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType)
    {
        this.talk(habbo, roomChatMessage, chatType, false);
    }

    public void talk(final Habbo habbo, final RoomChatMessage roomChatMessage, RoomChatType chatType, boolean ignoreWired)
    {
        if (!habbo.getHabboStats().allowTalk())
            return;

        habbo.getHabboStats().chatCounter += 2;

        if (habbo.getHabboInfo().getCurrentRoom() != this)
            return;

        long millis = System.currentTimeMillis();
        if (HABBO_CHAT_DELAY)
        {
            if (millis - habbo.getHabboStats().lastChat < 750)
            {
                return;
            }
        }
        habbo.getHabboStats().lastChat = millis;
        if(roomChatMessage != null && roomChatMessage.getMessage().equalsIgnoreCase("i am a pirate"))
        {
            Emulator.getThreading().run(new YouAreAPirate(habbo, this));
            return;
        }

        UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.TALKED, false);
        Emulator.getPluginManager().fireEvent(event);

        if (!event.isCancelled())
        {
            if (!event.idle)
            {
                this.unIdle(habbo);
            }
        }

        this.sendComposer(new RoomUserTypingComposer(habbo.getRoomUnit(), false).compose());

        if(roomChatMessage == null || roomChatMessage.getMessage() == null || roomChatMessage.getMessage().equals(""))
            return;

        for(HabboItem area : this.getRoomSpecialTypes().getItemsOfType(InteractionMuteArea.class))
        {
            if(((InteractionMuteArea)area).inSquare(habbo.getRoomUnit().getCurrentLocation()))
            {
                return;
            }
        }

        if(!this.wordFilterWords.isEmpty())
        {
            if (!habbo.hasPermission("acc_chat_no_filter"))
            {
                for(String string : this.wordFilterWords)
                {
                    roomChatMessage.setMessage(roomChatMessage.getMessage().replace(string, "bobba"));
                }
            }
        }

        if(!habbo.hasPermission("acc_nomute"))
        {
            if(this.isMuted() && !this.hasRights(habbo))
            {
                return;
            }

            if (this.isMuted(habbo))
            {
                habbo.getClient().sendResponse(new MutedWhisperComposer(this.mutedHabbos.get(habbo.getHabboInfo().getId()) - Emulator.getIntUnixTimestamp()));
                return;
            }
        }

        if (chatType != RoomChatType.WHISPER)
        {
            if (CommandHandler.handleCommand(habbo.getClient(), roomChatMessage.getUnfilteredMessage()))
            {
                WiredHandler.handle(WiredTriggerType.SAY_COMMAND, habbo.getRoomUnit(), habbo.getHabboInfo().getCurrentRoom(), new Object[]{roomChatMessage.getMessage()});
                roomChatMessage.isCommand = true;
                return;
            }

            if(!ignoreWired)
            {
                if (WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, habbo.getRoomUnit(), habbo.getHabboInfo().getCurrentRoom(), new Object[]{roomChatMessage.getMessage()}))
                {
                    habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(roomChatMessage.getMessage(), habbo, habbo, roomChatMessage.getBubble())));
                    return;
                }
            }
        }

        ServerMessage prefixMessage = roomChatMessage.getHabbo().getHabboInfo().getRank().hasPrefix() ? new RoomUserNameChangedComposer(habbo, true).compose() : null;
        ServerMessage clearPrefixMessage = prefixMessage != null ? new RoomUserNameChangedComposer(habbo).compose() : null;

        if(chatType == RoomChatType.WHISPER)
        {
            if (roomChatMessage.getTargetHabbo() == null)
            {
                return;
            }

            final ServerMessage message = new RoomUserWhisperComposer(roomChatMessage).compose();
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("To " + staffChatMessage.getTargetHabbo().getHabboInfo().getUsername() + ": " + staffChatMessage.getMessage());
            final ServerMessage staffMessage = new RoomUserWhisperComposer(staffChatMessage).compose();

            for (Habbo h : this.getHabbos())
            {
                if (h == roomChatMessage.getTargetHabbo() || h == habbo)
                {
                    if (!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                    {
                        if (prefixMessage != null)
                        {
                            h.getClient().sendResponse(prefixMessage);
                        }
                        h.getClient().sendResponse(message);

                        if (clearPrefixMessage != null)
                        {
                            h.getClient().sendResponse(clearPrefixMessage);
                        }
                    }

                    continue;
                }
                if (h.hasPermission("acc_see_whispers"))
                {
                    h.getClient().sendResponse(staffMessage);
                }

                continue;
            }
        }
        else if (chatType == RoomChatType.TALK)
        {
            ServerMessage message = new RoomUserTalkComposer(roomChatMessage).compose();
            boolean noChatLimit = habbo.hasPermission("acc_chat_no_limit");

            for (Habbo h : this.getHabbos())
            {
                if (h.getRoomUnit().getCurrentLocation().distance(habbo.getRoomUnit().getCurrentLocation()) <= this.chatDistance ||
                        h.hasPermission("acc_chat_no_limit") ||
                        h.equals(habbo) ||
                        this.hasRights(h) ||
                        noChatLimit)
                {
                    if (!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                    {
                        if (prefixMessage != null)
                        {
                            h.getClient().sendResponse(prefixMessage);
                        }
                        h.getClient().sendResponse(message);
                        if (clearPrefixMessage != null)
                        {
                            h.getClient().sendResponse(clearPrefixMessage);
                        }
                    }
                }
            }
        }
        else if(chatType == RoomChatType.SHOUT)
        {
            ServerMessage message = new RoomUserShoutComposer(roomChatMessage).compose();

            for (Habbo h : this.getHabbos())
            {
                if (!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                {
                    if (prefixMessage != null){ h.getClient().sendResponse(prefixMessage); }
                    h.getClient().sendResponse(message);
                    if (clearPrefixMessage != null){ h.getClient().sendResponse(clearPrefixMessage); }
                }
            }
        }

        if(chatType == RoomChatType.TALK || chatType == RoomChatType.SHOUT)
        {
            synchronized (this.currentBots)
            {
                TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();

                for (int i = this.currentBots.size(); i-- > 0; )
                {
                    try
                    {
                        botIterator.advance();
                        Bot bot = botIterator.value();
                        bot.onUserSay(roomChatMessage);

                    }
                    catch (NoSuchElementException e)
                    {
                        Emulator.getLogging().logErrorLine(e);
                        break;
                    }
                }
            }

            if(roomChatMessage.getBubble()!= RoomChatMessageBubbles.PARROT &&
                    roomChatMessage.getBubble() != RoomChatMessageBubbles.FORTUNE_TELLER)
            {
                synchronized (this.roomSpecialTypes)
                {
                    THashSet<HabboItem> items = this.roomSpecialTypes.getItemsOfType(InteractionTalkingFurniture.class);

                    for (HabboItem item : items)
                    {
                        if (this.layout.getTile(item.getX(), item.getY()).distance(habbo.getRoomUnit().getCurrentLocation()) <= Emulator.getConfig().getInt("furniture.talking.range"))
                        {
                            int count = Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.count", 0);

                            if (count > 0)
                            {
                                int randomValue = Emulator.getRandom().nextInt(count + 1);

                                RoomChatMessage itemMessage = new RoomChatMessage(Emulator.getTexts().getValue(item.getBaseItem().getName() + ".message." + randomValue, item.getBaseItem().getName() + ".message." + randomValue + " not found!"), habbo, RoomChatMessageBubbles.getBubble(Emulator.getConfig().getInt(item.getBaseItem().getName() + ".message.bubble")));

                                this.talk(habbo, itemMessage, chatType);

                                try
                                {
                                    item.onClick(habbo.getClient(), this, new Object[0]);
                                }
                                catch (Exception e)
                                {
                                    Emulator.getLogging().logErrorLine(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public THashSet<RoomTile> getLockedTiles()
    {
        THashSet<RoomTile> lockedTiles = new THashSet<RoomTile>();

        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            HabboItem item;
            try
            {
                iterator.advance();
                item = iterator.value();
            }
            catch (Exception e)
            {
                break;
            }

            if (item.getBaseItem().getType() != FurnitureType.FLOOR)
                continue;

            boolean found = false;
            for(RoomTile tile : lockedTiles)
            {
                if(tile.x == item.getX() &&
                   tile.y == item.getY())
                {
                    found = true;
                    break;
                }
            }

            if(!found)
            {
                if(item.getRotation() == 0 || item.getRotation() == 4)
                {
                    for(short y = 0; y < item.getBaseItem().getLength(); y++)
                    {
                        for(short x = 0; x < item.getBaseItem().getWidth(); x++)
                        {
                            RoomTile tile = this.layout.getTile((short) (item.getX() + x), (short) (item.getY() + y));

                            if (tile != null)
                            {
                                lockedTiles.add(tile);
                            }
                        }
                    }
                }
                else
                {
                    for(short y = 0; y < item.getBaseItem().getWidth(); y++)
                    {
                        for(short x = 0; x < item.getBaseItem().getLength(); x++)
                        {
                            RoomTile tile = this.layout.getTile((short) (item.getX() + x), (short) (item.getY() + y));

                            if (tile != null)
                            {
                                lockedTiles.add(tile);
                            }
                        }
                    }
                }
            }
        }

        return lockedTiles;
    }

    @Deprecated
    public THashSet<HabboItem> getItemsAt(int x, int y)
    {
        RoomTile tile = this.getLayout().getTile((short)x, (short)y);

        if (tile != null)
        {
            return this.getItemsAt(tile);
        }

        return new THashSet<HabboItem>(0);
    }
    public THashSet<HabboItem> getItemsAt(RoomTile tile)
    {
        if (this.loaded)
        {
            if (this.tileCache.containsKey(tile))
            {
                return this.tileCache.get(tile);
            }
        }

        THashSet<HabboItem> items = new THashSet<HabboItem>(0);

        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            HabboItem item;
            try
            {
                iterator.advance();
                item = iterator.value();
            }
            catch (Exception e)
            {
                break;
            }

            if (item.getBaseItem().getType() != FurnitureType.FLOOR)
                continue;

            if (item.getX() == tile.x && item.getY() == tile.y)
            {
                items.add(item);
            }
            else
            {
                if (item.getBaseItem().getWidth() <= 1 && item.getBaseItem().getLength() <= 1)
                {
                    continue;
                }

                THashSet<RoomTile> tiles = this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                for (RoomTile t : tiles)
                {
                    if ((t.x == tile.x) && (t.y == tile.y) && (!items.contains(item)))
                    {
                        items.add(item);
                    }
                }
            }
        }

        if (this.loaded)
        {
            this.tileCache.put(tile, items);
        }

        return items;
    }

    public THashSet<HabboItem> getItemsAt(int x, int y, double minZ)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.getItemsAt(x, y))
        {
            if(item.getZ() < minZ)
                continue;

            if(item.getX() == x && item.getY() == y && item.getZ() >= minZ)
            {
                items.add(item);
            }
            else
            {
                if(item.getBaseItem().getWidth() <= 1 && item.getBaseItem().getLength() <= 1)
                {
                    continue;
                }

                THashSet<RoomTile> tiles = this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                for (RoomTile tile : tiles)
                {
                    if ((tile.x == x) && (tile.y == y) && (!items.contains(item))) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    public THashSet<HabboItem> getItemsAt(Class<? extends HabboItem> type, int x, int y)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.getFloorItems())
        {
            if(item.getClass().equals(type))
            {
                if(item.getX() == x && item.getY() == y)
                {
                    items.add(item);
                }
                else
                {
                    if(item.getBaseItem().getWidth() <= 1 && item.getBaseItem().getLength() <= 1)
                    {
                        continue;
                    }

                    THashSet<RoomTile> tiles = this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                    for (RoomTile tile : tiles){
                        if ((tile.x == x) && (tile.y == y) && (!items.contains(item))) {
                            items.add(item);
                        }
                    }
                }
            }
        }

        return items;
    }

    public HabboItem getTopItemAt(int x, int y)
    {
        return this.getTopItemAt(x, y, null);
    }

    public HabboItem getTopItemAt(int x, int y, HabboItem exclude)
    {
        HabboItem item = null;

        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            HabboItem habboItem;
            try
            {
                iterator.advance();
                habboItem = iterator.value();
            }
            catch (Exception e)
            {
                break;
            }

            if (habboItem.getBaseItem().getType() != FurnitureType.FLOOR)
                continue;

            if (exclude != null)
            {
                if (exclude == habboItem)
                    continue;
            }

            if (habboItem.getX() == x && habboItem.getY() == y)
            {
                if (item == null || (habboItem.getZ() + Item.getCurrentHeight(habboItem)) > (item.getZ() + Item.getCurrentHeight(item)))
                {
                    item = habboItem;
                }
            } else
            {
                if (habboItem.getBaseItem().getWidth() <= 1 && habboItem.getBaseItem().getLength() <= 1)
                {
                    continue;
                }

                THashSet<RoomTile> tiles = this.layout.getTilesAt(
                        this.layout.getTile(habboItem.getX(), habboItem.getY()),
                        habboItem.getBaseItem().getWidth(),
                        habboItem.getBaseItem().getLength(),
                        habboItem.getRotation());

                for (RoomTile tile : tiles)
                {
                    if (((tile.x == x) && (tile.y == y)))
                    {
                        if (item == null || item.getZ() < habboItem.getZ())
                            item = habboItem;
                    }
                }
            }
        }

        return item;
    }

    public double getTopHeightAt(int x, int y)
    {
        HabboItem item = getTopItemAt(x, y);

        if(item != null)
            return (item.getZ() + item.getBaseItem().getHeight());
        else
            return this.layout.getHeightAtSquare(x, y);
    }

    @Deprecated
    public HabboItem getLowestChair(int x, int y)
    {
        RoomTile tile = this.layout.getTile((short)x, (short)y);

        if (tile != null)
        {
            return getLowestChair(tile);
        }

        return null;
    }

    public HabboItem getLowestChair(RoomTile tile)
    {
        HabboItem lowestChair = null;

        THashSet<HabboItem> items = this.getItemsAt(tile);
        if (items != null && !items.isEmpty())
        {
            for (HabboItem item : items)
            {
                if (item.getBaseItem().allowSit())
                {
                    if (lowestChair == null || item.getZ() < lowestChair.getZ())
                    {
                        lowestChair = item;
                    }
                }

                if (lowestChair != null)
                {
                    if (item.getZ() > lowestChair.getZ() && item.getZ() - lowestChair.getZ() < 1.5)
                    {
                        lowestChair = null;
                    }
                }
            }
        }

        return lowestChair;
    }

    public HabboItem getStackHelper(int x, int y)
    {
        RoomTile tile = this.getLayout().getTile((short)x, (short)y);

        if (tile != null)
        {
            THashSet<HabboItem> items = this.getItemsAt(tile);

            for (HabboItem item : items)
            {
                if (item instanceof InteractionStackHelper)
                {
                    return item;
                }
            }
        }

        return null;
    }

    public double getStackHeight(int x, int y, boolean calculateHeightmap)
    {
        if(x < 0 || y < 0)
            return 0.0;

        double height = this.layout.getHeightAtSquare(x, y);
        boolean canStack = true;
        boolean stackHelper = false;
        THashSet<HabboItem> items = this.getItemsAt(x, y);

        if (items != null)
        {
            for (HabboItem item : items)
            {
                if (item instanceof InteractionStackHelper)
                {
                    stackHelper = true;
                    height = item.getExtradata().isEmpty() ? Double.valueOf("0.0") : (Double.valueOf(item.getExtradata()) / 100);
                    canStack = true;
                }
            }

            if (!stackHelper)
            {
                for (HabboItem item : items)
                {
                    if (item.getBaseItem().allowSit())
                    {
                        canStack = false;
                        height = -1.0D;
                        break;
                    }

                    if (!item.getBaseItem().allowStack())
                    {
                        canStack = false;
                        height = -1.0D;
                        break;
                    }

                    double itemHeight = (item.getBaseItem().allowSit() ? 0.0D : item.getBaseItem().getHeight()) + item.getZ();

                    if (item instanceof InteractionMultiHeight)
                    {
                        if (item.getExtradata().length() == 0)
                        {
                            item.setExtradata("0");
                        }
                        itemHeight += Item.getCurrentHeight(item);
                    }

                    if (itemHeight > height)
                    {
                        height = itemHeight;
                    }
                }
            }
        }

        if(calculateHeightmap)
        {
            return (canStack ? height * 256.0D : Short.MAX_VALUE);
        }

        return height;
    }

    public double getStackHeight(int x, int y, boolean calculateHeightmap, HabboItem exclude)
    {
        if(x < 0 || y < 0)
            return 0.0;

        double height = this.layout.getHeightAtSquare(x, y);
        boolean canStack = true;

        THashSet<HabboItem> items = this.getItemsAt(x, y);
        if (items != null && !items.isEmpty())
        {
            for (HabboItem item : items)
            {
                if (item == exclude)
                    continue;

                if (item.getBaseItem().allowSit())
                {
                    canStack = false;
                    height = 0.0D;
                    break;
                }

                if (!item.getBaseItem().allowStack())
                {
                    canStack = false;
                    break;
                }

                double itemHeight = (item.getBaseItem().allowSit() ? 0.0D : item.getBaseItem().getHeight()) + item.getZ();

                if (item instanceof InteractionMultiHeight)
                {
                    if (item.getExtradata().length() == 0)
                    {
                        item.setExtradata("0");
                    }
                    itemHeight += Item.getCurrentHeight(item);
                }

                if (itemHeight > height)
                {
                    height = itemHeight;
                }
            }
        }

        if(calculateHeightmap)
        {
            return (canStack ? height * 256.0D : 65535.0D);
        }

        return height;
    }

    public boolean hasObjectTypeAt(Class<?> type, int x, int y)
    {
        THashSet<HabboItem> items = this.getItemsAt(x, y);

        for(HabboItem item : items)
        {
            if(item.getClass() == type)
            {
                return true;
            }
        }

        return false;
    }

    public boolean canSitOrLayAt(int x, int y)
    {
        if(hasHabbosAt(x, y))
            return false;

        THashSet<HabboItem> items = getItemsAt(x, y);

        return canSitAt(items) || canLayAt(items);
    }
    public boolean canSitAt(int x, int y)
    {
        if(hasHabbosAt(x, y))
            return false;

        return this.canSitAt(this.getItemsAt(x, y));
    }

    boolean canWalkAt(RoomTile roomTile)
    {
        if (roomTile == null)
        {
            return false;
        }

        if (roomTile.state == RoomTileState.BLOCKED)
            return false;

        HabboItem topItem = null;
        boolean canWalk = true;
        THashSet<HabboItem> items = this.getItemsAt(roomTile);
        if (items != null)
        {
            for (HabboItem item : items)
            {
                if (topItem == null)
                {
                    topItem = item;
                }

                if (item.getZ() > topItem.getZ())
                {
                    topItem = item;
                    canWalk = topItem.isWalkable() || topItem.getBaseItem().allowWalk();
                }
                else if (item.getZ() == topItem.getZ() && canWalk)
                {
                    if ((!topItem.isWalkable() && !topItem.getBaseItem().allowWalk()) || (!item.getBaseItem().allowWalk() && !item.isWalkable()))
                    {
                        canWalk = false;
                    }
                }
            }
        }

        return canWalk;
    }

    boolean canSitAt(THashSet<HabboItem> items)
    {
        if (items == null)
            return false;

        HabboItem topItem = null;
        HabboItem lowestSitItem = null;
        boolean canSit = false;
        boolean canSitUnder = false;

        for(HabboItem item : items)
        {
            if((lowestSitItem == null || lowestSitItem.getZ() > item.getZ()) && item.getBaseItem().allowSit())
            {
                lowestSitItem = item;
            }

            if(lowestSitItem != null)
            {
                if(item.getZ() > lowestSitItem.getZ())
                {
                    if(item.getZ() - lowestSitItem.getZ() > 0.8)
                        canSitUnder = true;
                    else
                        canSitUnder = false;
                }
            }

            if(topItem == null || item.getZ() > topItem.getZ())
            {
                topItem = item;
                canSit = false;
            }
        }

        if(topItem == null)
            return false;

        if(lowestSitItem == null)
            return false;

        if(topItem == lowestSitItem)
            return true;

        return topItem.getZ() <= lowestSitItem.getZ() || (canSitUnder);
    }

    public boolean canLayAt(int x, int y)
    {
        return this.canLayAt(this.getItemsAt(x, y));
    }

    boolean canLayAt(THashSet<HabboItem> items)
    {
        if (items == null || items.isEmpty())
            return true;

        HabboItem topItem = null;

        for(HabboItem item : items)
        {
            if((topItem == null || item.getZ() > topItem.getZ()))
            {
                topItem = item;
            }
        }

        return (topItem == null || topItem.getBaseItem().allowLay());
    }

    public RoomTile getRandomWalkableTile()
    {
        for (int i = 0; i < 10; i++)
        {
            RoomTile tile = this.layout.getTile((short) (Math.random() * this.layout.getMapSizeX()), (short) (Math.random() * this.layout.getMapSizeY()));
            if (tile.isWalkable() || this.canSitAt(tile.x, tile.y))
            {
                return tile;
            }
        }

        return null;
    }

    public Habbo getHabbo(String username)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (habbo.getHabboInfo().getUsername().equalsIgnoreCase(username))
                return habbo;
        }
        return null;
    }

    public Habbo getHabbo(RoomUnit roomUnit)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (habbo.getRoomUnit() == roomUnit)
                return habbo;
        }
        return null;
    }

    public Habbo getHabbo(int userId)
    {
        return this.currentHabbos.get(userId);
    }

    public Habbo getHabboByRoomUnitId(int roomUnitId)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if(habbo.getRoomUnit().getId() == roomUnitId)
                return habbo;
        }

        return null;
    }

    public void sendComposer(ServerMessage message)
    {
        for (Habbo habbo : this.getHabbos())
        {
            habbo.getClient().sendResponse(message);
        }
    }

    public void sendComposerToHabbosWithRights(ServerMessage message)
    {
        for (Habbo habbo : this.getHabbos())

        {
            if (hasRights(habbo))
            {
                habbo.getClient().sendResponse(message);
            }
        }
    }

    public void petChat(ServerMessage message)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (!habbo.getHabboStats().ignorePets)
                habbo.getClient().sendResponse(message);
        }
    }

    public void botChat(ServerMessage message)
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (!habbo.getHabboStats().ignoreBots)
                habbo.getClient().sendResponse(message);
        }
    }

    private void loadRights(Connection connection)
    {
        this.rights.clear();
        try (PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM room_rights WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.rights.add(set.getInt("user_id"));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private void loadBans(Connection connection)
    {
        this.bannedHabbos.clear();

        try (PreparedStatement statement = connection.prepareStatement("SELECT users.username, users.id, room_bans.* FROM room_bans INNER JOIN users ON room_bans.user_id = users.id WHERE ends > ? AND room_bans.room_id = ?"))
        {
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            statement.setInt(2, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (this.bannedHabbos.containsKey(set.getInt("user_id")))
                        continue;

                    this.bannedHabbos.put(set.getInt("user_id"), new RoomBan(set));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    //TODO: Return Enum
    public int guildRightLevel(Habbo habbo)
    {
        if(this.guild > 0 && habbo.getHabboStats().hasGuild(this.guild))
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);

            if(Emulator.getGameEnvironment().getGuildManager().getOnlyAdmins(guild).get(habbo.getHabboInfo().getId()) != null)
                return 3;

            if(guild.getRights() == 0)
            {
                return 2;
            }
        }

        return 0;
    }

    public boolean isOwner(Habbo habbo)
    {
        return habbo.getHabboInfo().getId() == this.ownerId || habbo.hasPermission("acc_anyroomowner");
    }

    public boolean hasRights(Habbo habbo)
    {
        return isOwner(habbo) || this.rights.contains(habbo.getHabboInfo().getId()) || (habbo.getRoomUnit().getRightsLevel() != RoomRightLevels.NONE && this.currentHabbos.containsKey(habbo.getHabboInfo().getId()));
    }

    public void giveRights(Habbo habbo)
    {
        if(habbo != null)
        {
            this.giveRights(habbo.getHabboInfo().getId());
        }
    }

    public void giveRights(int userId)
    {
        if (this.rights.contains(userId))
            return;

        if(this.rights.add(userId))
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_rights VALUES (?, ?)"))
            {
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        Habbo habbo = this.getHabbo(userId);

        if(habbo != null)
        {
            this.refreshRightsForHabbo(habbo);

            this.sendComposer(new RoomAddRightsListComposer(this, habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername()).compose());
        }
        else
        {
            Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(this.ownerId);

            if(owner != null)
            {
                MessengerBuddy buddy = owner.getMessenger().getFriend(userId);

                if(buddy != null)
                {
                    this.sendComposer(new RoomAddRightsListComposer(this, userId, buddy.getUsername()).compose());
                }
            }
        }
    }

    public void removeRights(int userId)
    {
        Habbo habbo = this.getHabbo(userId);

        if(Emulator.getPluginManager().fireEvent(new UserRightsTakenEvent(this.getHabbo(this.getOwnerId()), userId, habbo)).isCancelled())
            return;

        this.sendComposer(new RoomRemoveRightsListComposer(this, userId).compose());

        if(this.rights.remove(userId))
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ? AND user_id = ?"))
            {
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        if(habbo != null)
        {
            this.refreshRightsForHabbo(habbo);
        }
    }

    public void removeAllRights()
    {
        this.rights.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_rights WHERE room_id = ?"))
        {
            statement.setInt(1, this.id);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.refreshRightsInRoom();
    }

    void refreshRightsInRoom()
    {
        Room room = this;
        for (Habbo habbo : this.getHabbos())
        {
            if(habbo.getHabboInfo().getCurrentRoom() == room)
            {
                refreshRightsForHabbo(habbo);
            }
        }
    }

    public void refreshRightsForHabbo(Habbo habbo)
    {
        HabboItem item;
        RoomRightLevels flatCtrl = RoomRightLevels.NONE;
        if (!habbo.getHabboStats().canRentSpace())
        {
            item = this.getHabboItem(habbo.getHabboStats().getRentedItemId());

            if (item != null)
            {
                flatCtrl = RoomRightLevels.GUILD_ADMIN;
                return;
            }
        }

        if (habbo.hasPermission("acc_anyroomowner"))
        {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        }
        else if (this.isOwner(habbo))
        {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            flatCtrl = RoomRightLevels.MODERATOR;
        }
        else if (this.hasRights(habbo) && !this.hasGuild())
        {
            flatCtrl = RoomRightLevels.RIGHTS;
        }
        else if (this.hasGuild())
        {
            int level = this.guildRightLevel(habbo);

            if(level == 3)
            {
                flatCtrl = RoomRightLevels.GUILD_ADMIN;
            }
            else if(level == 2)
            {
                flatCtrl = RoomRightLevels.GUILD_RIGHTS;
            }
        }


        habbo.getClient().sendResponse(new RoomRightsComposer(flatCtrl));
        habbo.getRoomUnit().getStatus().put("flatctrl", flatCtrl.level + "");
        habbo.getRoomUnit().setRightsLevel(flatCtrl);

        if (flatCtrl.equals(RoomRightLevels.MODERATOR))
        {
            habbo.getClient().sendResponse(new RoomRightsListComposer(this));
        }
    }

    public THashMap<Integer, String> getUsersWithRights()
    {
        THashMap<Integer, String> rightsMap = new THashMap<Integer, String>();

        if(!this.rights.isEmpty())
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS username, users.id as user_id FROM room_rights INNER JOIN users ON room_rights.user_id = users.id WHERE room_id = ?"))
            {
                statement.setInt(1, this.id);
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        rightsMap.put(set.getInt("user_id"), set.getString("username"));
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        return rightsMap;
    }

    public void unbanHabbo(int userId)
    {
        RoomBan ban = this.bannedHabbos.remove(userId);

        if(ban != null)
        {
            ban.delete();
        }

        this.sendComposer(new RoomUserUnbannedComposer(this, userId).compose());
    }

    public boolean isBanned(Habbo habbo)
    {
        RoomBan ban = this.bannedHabbos.get(habbo.getHabboInfo().getId());

        boolean banned = ban != null && ban.endTimestamp > Emulator.getIntUnixTimestamp() && !habbo.hasPermission("acc_anyroomowner") && !habbo.hasPermission("acc_enteranyroom");

        if (!banned && ban != null)
        {
            this.unbanHabbo(habbo.getHabboInfo().getId());
        }

        return banned;
    }

    public TIntObjectHashMap<RoomBan> getBannedHabbos()
    {
        return this.bannedHabbos;
    }

    public void addRoomBan(RoomBan roomBan)
    {
        this.bannedHabbos.put(roomBan.userId, roomBan);
    }

    public void makeSit(Habbo habbo)
    {
        if (habbo.getRoomUnit().getStatus().containsKey("sit"))
        {
            return;
        }


        this.dance(habbo, DanceType.NONE);
        habbo.getRoomUnit().cmdSit = true;
        habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
        habbo.getRoomUnit().getStatus().put("sit", 0.5 + "");
        this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
    }

    public void giveEffect(Habbo habbo, int effectId)
    {
        if (this.currentHabbos.containsKey(habbo.getHabboInfo().getId()))
        {
            this.giveEffect(habbo.getRoomUnit(), effectId);
        }
    }

    public void giveEffect(RoomUnit roomUnit, int effectId)
    {
        if (this.allowEffects)
        {
            roomUnit.setEffectId(effectId);
            this.sendComposer(new RoomUserEffectComposer(roomUnit).compose());
        }
    }

    public void giveHandItem(Habbo habbo, int handItem)
    {
        habbo.getRoomUnit().setHandItem(handItem);
        this.sendComposer(new RoomUserHandItemComposer(habbo.getRoomUnit()).compose());
    }

    public void updateItem(HabboItem item)
    {
        if (this.isLoaded())
        {
            if (item != null && item.getRoomId() == this.id)
            {
                if (item.getBaseItem() != null)
                {
                    if (item.getBaseItem().getType() == FurnitureType.FLOOR)
                    {
                        this.sendComposer(new FloorItemUpdateComposer(item).compose());
                        this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
                    }
                    else if (item.getBaseItem().getType() == FurnitureType.WALL)
                    {
                        this.sendComposer(new WallItemUpdateComposer(item).compose());
                    }
                }
            }
        }
    }

    public void updateItemState(HabboItem item)
    {
        this.sendComposer(new ItemStateComposer(item).compose());

        if (item.getBaseItem().getType() == FurnitureType.FLOOR)
        {
            this.updateTiles(this.getLayout().getTilesAt(this.layout.getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation()));
        }
    }

    public int getUserFurniCount(int userId)
    {
        return this.furniOwnerCount.get(userId);

//        int count = 0;
//
//        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();
//
//        for(int i = this.roomItems.size(); i-- > 0;)
//        {
//            try
//            {
//                iterator.advance();
//
//                if(iterator.value().getUserId() == userId)
//                    count++;
//            }
//            catch (NoSuchElementException e)
//            {
//                break;
//            }
//        }
//
//        return count;
    }

    public void ejectUserFurni(int userId)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

        for (int i = this.roomItems.size(); i-- > 0; )
        {
            try
            {
                iterator.advance();
            }
            catch (Exception e)
            {
                break;
            }

            if (iterator.value().getUserId() == userId)
            {
                items.add(iterator.value());
                iterator.value().setRoomId(0);
            }
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

        if (habbo != null)
        {
            habbo.getInventory().getItemsComponent().addItems(items);
            habbo.getClient().sendResponse(new AddHabboItemComposer(items));
        }

        for (HabboItem i : items)
        {
            this.pickUpItem(i, null);
        }
    }

    public void ejectUserItem(HabboItem item)
    {
        this.pickUpItem(item, null);
    }

    /**
     * Ejects all furniture from the room not belonging to the room owner.
     */
    public void ejectAll()
    {
        this.ejectAll(null);
    }

    /**
     * @param habbo The Habbo to exclude to eject its items.
     */
    public void ejectAll(Habbo habbo)
    {
        THashMap<Integer, THashSet<HabboItem>> userItemsMap = new THashMap<Integer, THashSet<HabboItem>>();

        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();
                }
                catch (Exception e)
                {
                    break;
                }

                if (iterator.value().getUserId() != this.ownerId)
                    continue;

                if (habbo != null && iterator.value().getUserId() == habbo.getHabboInfo().getId())
                    continue;

                if (userItemsMap.get(iterator.value().getUserId()) == null)
                {
                    userItemsMap.put(iterator.value().getUserId(), new THashSet<HabboItem>());
                }

                userItemsMap.get(iterator.value().getUserId()).add(iterator.value());
            }
        }

        for (Map.Entry<Integer, THashSet<HabboItem>> entrySet : userItemsMap.entrySet())
        {
            for (HabboItem i : entrySet.getValue())
            {
                this.pickUpItem(i, null);
            }

            Habbo user = Emulator.getGameEnvironment().getHabboManager().getHabbo(entrySet.getKey());

            if (user != null)
            {
                user.getInventory().getItemsComponent().addItems(entrySet.getValue());
                user.getClient().sendResponse(new AddHabboItemComposer(entrySet.getValue()));
            }
        }
    }

    public void refreshGuild(Guild guild)
    {
        if(guild.getRoomId() == this.id)
        {
            THashMap<Integer, GuildMember> admins = Emulator.getGameEnvironment().getGuildManager().getOnlyAdmins(guild);

            for (Habbo habbo : this.getHabbos())
            {
                GuildMember member = admins.get(habbo.getHabboInfo().getId());
                habbo.getClient().sendResponse(new GuildInfoComposer(guild, habbo.getClient(), false, member));
            }
        }

        this.refreshGuildRightsInRoom();
    }

    public void refreshGuildColors(Guild guild)
    {
        if(guild.getRoomId() == this.id)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();
                }
                catch (Exception e)
                {
                    break;
                }

                HabboItem habboItem = iterator.value();

                if (habboItem != null && habboItem instanceof InteractionGuildFurni)
                {
                    if (((InteractionGuildFurni) habboItem).getGuildId() == guild.getId())
                        this.updateItem(habboItem);
                }
            }
        }
    }

    public void refreshGuildRightsInRoom()
    {
        for (Habbo habbo : this.getHabbos())
        {
            if (habbo.getHabboInfo().getCurrentRoom() == this)
            {
                if (habbo.getHabboInfo().getId() != this.ownerId)
                {
                    if (!(habbo.hasPermission("acc_anyroomowner") || habbo.hasPermission("acc_moverotate")))
                        refreshRightsForHabbo(habbo);
                }
            }
        }
    }

    public void idle(Habbo habbo)
    {
        habbo.getRoomUnit().setIdle();
        this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
    }

    public void unIdle(Habbo habbo)
    {
        habbo.getRoomUnit().resetIdleTimer();
        this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
    }

    public void dance(Habbo habbo, DanceType danceType)
    {
        habbo.getRoomUnit().setDanceType(danceType);
        this.sendComposer(new RoomUserDanceComposer(habbo.getRoomUnit()).compose());
    }

    public void addToWordFilter(String word)
    {
        synchronized (this.wordFilterWords)
        {
            if (this.wordFilterWords.contains(word))
                return;

            this.wordFilterWords.add(word);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_wordfilter VALUES (?, ?)"))
            {
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void removeFromWordFilter(String word)
    {
        synchronized (this.wordFilterWords)
        {
            this.wordFilterWords.remove(word);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM room_wordfilter WHERE room_id = ? AND word = ?"))
            {
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public THashSet<WiredHighscoreData> getWiredHighscoreData(WiredHighscoreScoreType scoreType, WiredHighscoreClearType clearType)
    {
        if(!this.wiredHighscoreData.containsKey(scoreType))
        {
            this.loadWiredHighscoreData(scoreType, clearType);
        }

        return this.wiredHighscoreData.get(scoreType).get(clearType);
    }

    public void loadWiredHighscoreData(WiredHighscoreScoreType scoreType, WiredHighscoreClearType clearType)
    {
        this.wiredHighscoreData.clear();
        THashSet<WiredHighscoreData> wiredData = new THashSet<WiredHighscoreData>();

        try
        {
            String query = "SELECT " +
                    "SUM(score + team_score) as total_score, " +
                    "COUNT(*) as wins, " +
                    "users.username, " +
                    "room_game_scores.*, " +
                    "GROUP_CONCAT(users.username) as usernames " +
                    "FROM room_game_scores " +
                    "INNER JOIN users ON room_game_scores.user_id = users.id " +
                    "WHERE room_id = ? AND game_start_timestamp >= ? ";

            int timestamp = 0;
            if(clearType != WiredHighscoreClearType.ALLTIME)
            {
                if(clearType == WiredHighscoreClearType.MONTHLY)
                {
                    timestamp = Emulator.getIntUnixTimestamp() - (31 * 24 * 60 * 60);
                }
                else if(clearType == WiredHighscoreClearType.WEEKLY)
                {
                    timestamp = Emulator.getIntUnixTimestamp() - (7 * 24 * 60 * 60);
                }
                else if(clearType == WiredHighscoreClearType.DAILY)
                {
                    timestamp = Emulator.getIntUnixTimestamp() - (24 * 60 * 60);
                }
            }

            /*
            SELECT SUM(score + team_score) as total_score, COUNT(*) as wins, users.username, score, GROUP_CONCAT(users.username) as usernames FROM room_game_scores INNER JOIN users ON room_game_scores.user_id = users.id WHERE room_id = 0 GROUP BY game_start_timestamp, game_name, team_id ORDER BY total_score ASC, wins DESC LIMIT 10
             */
            if(scoreType == WiredHighscoreScoreType.CLASSIC)
            {
                query += "GROUP BY game_start_timestamp, user_id, team_id ORDER BY total_score DESC";
            }
            else if(scoreType == WiredHighscoreScoreType.MOSTWIN)
            {
                query += "GROUP BY game_start_timestamp, game_name, team_id ORDER BY wins DESC, total_score ASC";
            }
            else if(scoreType == WiredHighscoreScoreType.PERTEAM)
            {
                query += "GROUP BY game_start_timestamp, team_id ORDER BY team_score DESC";
            }

            query += " LIMIT " + Emulator.getConfig().getValue("wired.highscores.displaycount");

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query))
            {
                statement.setInt(1, this.id);
                statement.setInt(2, timestamp);

                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        wiredData.add(new WiredHighscoreData(set.getString("usernames").split(","), set.getInt("score"), set.getInt("team_score"), set.getInt("total_score")));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>> dataMap = new THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>>();
        dataMap.put(clearType, wiredData);
        this.wiredHighscoreData.put(scoreType, dataMap);
    }

    public void handleWordQuiz(Habbo habbo, String answer)
    {
        synchronized (this.userVotes)
        {
            if (!this.wordQuiz.isEmpty() && !this.hasVotedInWordQuiz(habbo))
            {
                answer = answer.replace(":", "");

                if (answer.equals("0"))
                {
                    this.noVotes++;
                }
                else if (answer.equals("1"))
                {
                    this.yesVotes++;
                }

                this.sendComposer(new SimplePollAnswerComposer(habbo.getHabboInfo().getId(), answer, this.noVotes, this.yesVotes).compose());
                this.userVotes.add(habbo.getHabboInfo().getId());
            }
        }
    }

    public void startWordQuiz(String question, int duration)
    {
        if (!hasActiveWordQuiz())
        {
            this.wordQuiz = question;
            this.noVotes = 0;
            this.yesVotes = 0;
            this.userVotes.clear();
            this.wordQuizEnd = Emulator.getIntUnixTimestamp() + (duration / 1000);
            this.sendComposer(new SimplePollStartComposer(duration, question).compose());
        }
    }

    public boolean hasActiveWordQuiz()
    {
        return Emulator.getIntUnixTimestamp() < this.wordQuizEnd;
    }

    public boolean hasVotedInWordQuiz(Habbo habbo)
    {
        return this.userVotes.contains(habbo.getHabboInfo().getId());
    }

    public void alert(String message)
    {
        this.sendComposer(new GenericAlertComposer(message).compose());
    }

    public int itemCount()
    {
        return this.roomItems.size();
    }

    public void setJukeBoxActive(boolean jukeBoxActive)
    {
        this.jukeboxActive = jukeBoxActive;
        this.needsUpdate = true;
    }
}
