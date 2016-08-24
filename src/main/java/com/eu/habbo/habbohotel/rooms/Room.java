package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.bots.VisitorBot;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
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
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorMessagesComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildInfoComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.messages.outgoing.rooms.items.*;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.MutedWhisperComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.furniture.FurniturePickedUpEvent;
import com.eu.habbo.plugin.events.furniture.FurnitureRolledEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserRightsTakenEvent;
import com.eu.habbo.plugin.events.users.UserRolledEvent;
import com.eu.habbo.threading.runnables.LoadCustomHeightMap;
import com.eu.habbo.threading.runnables.YouAreAPirate;
import com.eu.habbo.util.pathfinding.GameMap;
import com.eu.habbo.util.pathfinding.Node;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.TCollections;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class Room implements Comparable<Room>, ISerialize, Runnable
{
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

    private final TIntObjectMap<Habbo> currentHabbos;
    private final TIntObjectMap<Habbo> habboQueue;
    private final TIntObjectMap<Bot> currentBots;
    private final TIntObjectMap<AbstractPet> currentPets;
    private final THashSet<RoomTrade> activeTrades;
    private final TIntArrayList rights;
    private final TIntIntHashMap mutedHabbos;
    private final TIntObjectHashMap<RoomBan> bannedHabbos;
    private final THashSet<Game> games;
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
    private volatile long lastRollerCycle = System.currentTimeMillis();
    private volatile int lastTimerReset = Emulator.getIntUnixTimestamp();
    private volatile boolean muted;

    private GameMap<Node> gameMap;

    private RoomSpecialTypes roomSpecialTypes;

    private final Object loadLock = new Object();

    //Use appropriately. Could potentially cause memory leaks when used incorrectly.
    public volatile boolean preventUnloading = false;
    public volatile boolean preventUncaching = false;
    public THashMap<Integer, TIntArrayList> waterTiles;

    public Room(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.ownerId = set.getInt("owner_id");
        this.ownerName = set.getString("owner_name");
        this.name = set.getString("name");
        this.description = set.getString("description");
        this.layout = Emulator.getGameEnvironment().getRoomManager().getLayout(set.getString("model"));
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
        this.overrideModel = set.getInt("override_model") == 1;
        if(this.overrideModel)
        {
            Emulator.getThreading().run(new LoadCustomHeightMap(this));
        }

        this.promoted = set.getInt("promoted") == 1;
        if(this.promoted)
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM room_promotions WHERE room_id = ? AND end_timestamp > ? LIMIT 1");
            statement.setInt(1, this.id);
            statement.setInt(2, Emulator.getIntUnixTimestamp());

            ResultSet promotionSet = statement.executeQuery();
            this.promoted = false;
            while(promotionSet.next())
            {
                this.promoted = true;
                this.promotion = new RoomPromotion(this, promotionSet);
            }

            promotionSet.close();
            statement.close();
            statement.getConnection().close();
        }

        this.tradeMode = set.getInt("trade_mode");

        this.preLoaded = true;
        this.allowBotsWalk = true;
        this.currentHabbos = TCollections.synchronizedMap(new TIntObjectHashMap<Habbo>());
        this.habboQueue = TCollections.synchronizedMap(new TIntObjectHashMap<Habbo>());
        this.currentBots = TCollections.synchronizedMap(new TIntObjectHashMap<Bot>());
        this.currentPets = TCollections.synchronizedMap(new TIntObjectHashMap<AbstractPet>());
        this.furniOwnerNames = TCollections.synchronizedMap(new TIntObjectHashMap<String>());
        this.furniOwnerCount = TCollections.synchronizedMap(new TIntIntHashMap());
        this.roomItems = TCollections.synchronizedMap(new TIntObjectHashMap<HabboItem>());
        this.wordFilterWords = new THashSet<String>();
        this.moodlightData = new TIntObjectHashMap<RoomMoodlightData>(defaultMoodData);

        for(String s : set.getString("moodlight_data").split(";"))
        {
            RoomMoodlightData data = RoomMoodlightData.fromString(s);
            this.moodlightData.put(data.getId(), data);
        }

        this.mutedHabbos = new TIntIntHashMap();
        this.bannedHabbos = new TIntObjectHashMap<RoomBan>();
        this.games = new THashSet<Game>();

        this.activeTrades = new THashSet<RoomTrade>();
        this.rights = new TIntArrayList();
        this.wiredHighscoreData = new THashMap<WiredHighscoreScoreType, THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>>>();

        try
        {
            this.loadBans();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public void loadData()
    {
        synchronized (this.loadLock)
        {
            if (!this.preLoaded || this.loaded)
                return;

            try
            {
                this.unitCounter = 0;
                this.currentHabbos.clear();
                this.currentPets.clear();
                this.currentBots.clear();

                this.roomSpecialTypes = new RoomSpecialTypes();

                try
                {
                    this.loadRights();
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
                    this.loadItems();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadBots();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadPets();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadWordFilter();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                try
                {
                    this.loadWiredData();
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }

                this.preLoaded = false;
                this.idleCycles = 0;
                this.loaded = true;

                Emulator.getThreading().run(this);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    private synchronized void loadHeightmap()
    {
        if (this.layout != null)
        {
            this.gameMap = new GameMap<Node>(this.layout.getMapSizeX(), this.layout.getMapSizeY());
            for (Node node : this.gameMap.getNodes())
            {
                this.gameMap.setWalkable(node.getX(), node.getY(), tileWalkable(node.getX(), node.getY()));
            }
        }
        else
        {
            Emulator.getLogging().logErrorLine("Unknown Room Layout for Room (ID: " + this.id + ")");
        }
    }

    private synchronized void loadItems() throws SQLException
    {
        this.roomItems.clear();

        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM items WHERE room_id = ?");
        statement.setInt(1, this.id);
        ResultSet set = statement.executeQuery();
        while(set.next())
        {
            this.addHabboItem(Emulator.getGameEnvironment().getItemManager().loadHabboItem(set));
        }
        set.close();
        statement.close();
        statement.getConnection().close();
    }

    private synchronized void loadWiredData() throws SQLException
    {
        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT id, wired_data FROM items WHERE room_id = ? AND wired_data<>?");
        statement.setInt(1, this.id);
        statement.setString(2, "");
        ResultSet set = statement.executeQuery();

        try
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
                }
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        set.close();
        statement.close();
        statement.getConnection().close();
    }

    private synchronized void loadBots() throws SQLException
    {
        this.currentBots.clear();

        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username AS owner_name, bots.* FROM bots INNER JOIN users ON bots.user_id = users.id WHERE room_id = ?");
        statement.setInt(1, this.id);
        ResultSet set = statement.executeQuery();

        while(set.next())
        {
            //Bot b = new Bot(set);
            Bot b = Emulator.getGameEnvironment().getBotManager().loadBot(set);

            if(b != null)
            {
                b.setRoom(this);
                b.setRoomUnit(new RoomUnit());
                b.getRoomUnit().getPathFinder().setRoom(this);
                b.getRoomUnit().setX(set.getInt("x"));
                b.getRoomUnit().setY(set.getInt("y"));
                b.getRoomUnit().setZ(set.getDouble("z"));
                b.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
                b.getRoomUnit().setGoalLocation(set.getInt("x"), set.getInt("y"));
                b.getRoomUnit().setRoomUnitType(RoomUnitType.BOT);
                b.getRoomUnit().setDanceType(DanceType.values()[set.getInt("dance")]);
                b.getRoomUnit().setCanWalk(set.getBoolean("freeroam"));
                b.getRoomUnit().setInRoom(true);
                this.addBot(b);
                b.getRoomUnit().setId(this.unitCounter);
            }
        }
        set.close();
        statement.close();
        statement.getConnection().close();
    }

    synchronized void loadPets() throws SQLException
    {
        this.currentPets.clear();

        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_pets WHERE room_id = ?");
        statement.setInt(1, this.id);
        ResultSet set = statement.executeQuery();
        AbstractPet pet;
        while(set.next())
        {
            if(set.getInt("type") == 15)
                pet = new HorsePet(set);
            else if(set.getInt("type") == 16)
                pet = new MonsterplantPet(set);
            else
                pet = new Pet(set);
            pet.setRoom(this);
            pet.setRoomUnit(new RoomUnit());
            pet.getRoomUnit().getPathFinder().setRoom(this);
            pet.getRoomUnit().setX(set.getInt("x"));
            pet.getRoomUnit().setY(set.getInt("y"));
            pet.getRoomUnit().setZ(set.getDouble("z"));
            pet.getRoomUnit().setRotation(RoomUserRotation.values()[set.getInt("rot")]);
            pet.getRoomUnit().setGoalLocation(set.getInt("x"), set.getInt("y"));
            pet.getRoomUnit().setRoomUnitType(RoomUnitType.PET);
            pet.getRoomUnit().setCanWalk(true);
            this.addPet(pet);
            pet.getRoomUnit().setId(this.unitCounter);
        }
        set.close();
        statement.close();
        statement.getConnection().close();
    }

    synchronized void loadWordFilter() throws SQLException
    {
        this.wordFilterWords.clear();

        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM room_wordfilter WHERE room_id = ?");
        statement.setInt(1, this.id);
        ResultSet set = statement.executeQuery();

        while(set.next())
        {
            this.wordFilterWords.add(set.getString("word"));
        }

        set.close();
        statement.close();
        statement.getConnection().close();
    }

    public void updateTile(Tile tile)
    {
        this.gameMap.setWalkable(tile.X, tile.Y, tileWalkable(tile.X, tile.Y));
    }

    public void updateTiles(THashSet<Tile> tiles)
    {
        for(Tile tile : tiles)
        {
            updateTile(tile);
        }
    }

    public boolean tileWalkable(Tile t)
    {
        return this.tileWalkable(t.x, t.y);
    }

    public boolean tileWalkable(int x, int y)
    {
        if(this.layout.tileWalkable(x, y))
        {
            if(!this.allowWalkthrough)
            {
                if (hasHabbosAt(x, y))
                    return false;

                if (hasBotsAt(x, y))
                    return false;
            }

            THashSet<HabboItem> items = getItemsAt(x, y);
            if(items.size() == 0)
                return true;

            boolean walkAble = true;
            for(HabboItem item : items)
            {
                if(!item.getBaseItem().allowWalk())
                    walkAble = false;

                if(item.getBaseItem().allowSit())
                    walkAble = false;

                if(item instanceof InteractionGate)
                {
                    walkAble = item.isWalkable();
                }
                else if(item instanceof InteractionGameGate)
                {
                    walkAble = item.isWalkable();
                }
                else if(item instanceof InteractionFreezeBlock)
                {
                    walkAble = item.isWalkable();
                }
            }
            return walkAble;
        }
        return false;
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

        if(item instanceof InteractionFreezeExitTile)
        {
            FreezeGame game = (FreezeGame)this.getGame(FreezeGame.class);

            if(this.getGame(FreezeGame.class) != null)
            {
                if(game.isRunning && game.exitTile == item)
                {
                    return;
                }
            }
        }

        this.removeHabboItem(item.getId());
        item.onPickUp(this);
        item.setRoomId(0);
        item.needsUpdate(true);

        if (item.getBaseItem().getType().toLowerCase().equals("s"))
        {
            this.sendComposer(new RemoveFloorItemComposer(item).compose());

            THashSet<Tile> updatedTiles = new THashSet<Tile>();
            Rectangle rectangle = PathFinder.getSquare(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

            for (int x = rectangle.x; x < rectangle.x + rectangle.getWidth(); x++)
            {
                for (int y = rectangle.y; y < rectangle.y + rectangle.getHeight(); y++)
                {
                    double stackHeight = this.getStackHeight(x, y, false);
                    updatedTiles.add(new Tile(x, y, stackHeight * 250.0D));

                    updateHabbosAt(x, y);
                }
            }
            this.sendComposer(new UpdateStackHeightComposer(updatedTiles).compose());
            this.updateTiles(updatedTiles);
        }
        else if (item.getBaseItem().getType().toLowerCase().equals("i"))
        {
            this.sendComposer(new RemoveWallItemComposer(item).compose());
        }

        Habbo habbo = (picker != null && picker.getHabboInfo().getId() == item.getId() ? picker : Emulator.getGameServer().getGameClientManager().getHabbo(item.getUserId()));
        if (habbo != null) {
            habbo.getHabboInventory().getItemsComponent().addItem(item);
            habbo.getClient().sendResponse(new InventoryRefreshComposer());
        }
        Emulator.getThreading().run(item);
    }

    public void updateHabbosAt(Rectangle rectangle)
    {
        for(int i = rectangle.x; i < rectangle.x + rectangle.width; i++)
        {
            for(int j = rectangle.y; j < rectangle.y + rectangle.height; j++)
            {
                this.updateHabbosAt(i, j);
            }
        }
    }

    public void updateHabbo(Habbo habbo)
    {
        HabboItem item = this.getTopItemAt(habbo.getRoomUnit().getLocation().X, habbo.getRoomUnit().getLocation().Y);

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

    public void updateHabbosAt(int x, int y)
    {
        THashSet<Habbo> habbos = this.getHabbosAt(x, y);

        HabboItem item = this.getTopItemAt(x, y);

        THashSet<RoomUnit> roomUnits = new THashSet<RoomUnit>();
        for (Habbo habbo : habbos)
        {
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
            roomUnits.add(habbo.getRoomUnit());
        }

        this.sendComposer(new RoomUserStatusComposer(roomUnits, true).compose());
    }

    public void pickupPetsForHabbo(Habbo habbo)
    {
        THashSet<Pet> pets = new THashSet<Pet>();

        synchronized (this.currentPets)
        {
            for(AbstractPet pet : this.currentPets.valueCollection())
            {
                if(pet.getUserId() == habbo.getHabboInfo().getId())
                {
                    if(pet instanceof Pet)
                    {
                        pet.setRoom(null);
                        pet.needsUpdate = true;
                        pets.add((Pet) pet);
                    }
                }
            }
        }

        for(Pet p : pets)
        {
            this.currentPets.remove(p.getId());
        }

        habbo.
                getHabboInventory().
                getPetsComponent().
                addPets(pets);

    }

    public void startTrade(Habbo userOne, Habbo userTwo)
    {
        synchronized (this.activeTrades)
        {
            RoomTrade trade = new RoomTrade(userOne, userTwo, this);
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
        if(this.preventUnloading)
            return;

        try
        {
            this.loaded = false;

            synchronized (this.mutedHabbos)
            {
                this.mutedHabbos.clear();
            }

            synchronized (this.games)
            {
                TObjectHashIterator<Game> gameIterator = this.games.iterator();

                for (int i = this.games.size(); i-- > 0; )
                {
                    gameIterator.next().stop();
                }

                this.games.clear();
            }

            synchronized (this.roomItems)
            {
                TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

                for (int i = this.roomItems.size(); i-- > 0; )
                {
                    try
                    {
                        iterator.advance();

                        if (iterator.value().needsUpdate())
                            Emulator.getThreading().run(iterator.value());
                    }
                    catch (NoSuchElementException e)
                    {
                        Emulator.getLogging().logErrorLine(e);
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

            Object[] habbosToDispose = this.currentHabbos.valueCollection().toArray();
            synchronized (this.currentHabbos)
            {
                for (Object h : habbosToDispose)
                {
                    Emulator.getGameEnvironment().getRoomManager().leaveRoom((Habbo) h, this);
                }
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
                }
                catch (NoSuchElementException e)
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
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
            }

            this.currentBots.clear();
            this.currentPets.clear();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        this.updateDatabaseUserCount();
        this.preLoaded = true;
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
    public void serialize(ServerMessage message) {
        message.appendInt32(this.id);
        message.appendString(this.name);
        if(this.isPublicRoom())
        {
            message.appendInt32(0);
            message.appendString("");
        }
        else
        {
            message.appendInt32(this.ownerId);
            message.appendString(this.ownerName);
        }
        message.appendInt32(this.state.getState());
        message.appendInt32(this.getUserCount());
        message.appendInt32(this.usersMax);
        message.appendString(this.description);
        message.appendInt32(0);
        message.appendInt32(this.score);
        message.appendInt32(0);
        message.appendInt32(this.category);
        message.appendInt32(this.tags.split(";").length);
        for(String s : this.tags.split(";"))
        {
            message.appendString(s);
        }

        /*if(g != null)
        {
            message.appendInt32(58);
            message.appendInt32(g.getId());
            message.appendString(g.getName());
            message.appendString(g.getBadge());
        }
        else
        {
            message.appendInt32(56);
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

        message.appendInt32(base);


        //message.appendString("a.png"); //Camera image.

        if(this.getGuildId() > 0)
        {
            Guild g = Emulator.getGameEnvironment().getGuildManager().getGuild(this.getGuildId());
            if (g != null)
            {
                message.appendInt32(g.getId());
                message.appendString(g.getName());
                message.appendString(g.getBadge());
            }
            else
            {
                message.appendInt32(0);
                message.appendString("");
                message.appendString("");
            }
        }

        if(this.promoted)
        {
            message.appendString(this.promotion.getTitle());
            message.appendString(this.promotion.getDescription());
            message.appendInt32((this.promotion.getEndTimestamp() - Emulator.getIntUnixTimestamp()) / 60);
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
        if(this.loaded)
        {
            Emulator.getThreading().run(this, 500);
            this.cycle();
        }

        this.save();
    }

    public void save()
    {
        if(this.needsUpdate)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE rooms SET name = ?, description = ?, password = ?, state = ?, users_max = ?, category = ?, score = ?, paper_floor = ?, paper_wall = ?, paper_landscape = ?, thickness_wall = ?, wall_height = ?, thickness_floor = ?, moodlight_data = ?, tags = ?, allow_other_pets = ?, allow_other_pets_eat = ?, allow_walkthrough = ?, allow_hidewall = ?, chat_mode = ?, chat_weight = ?, chat_speed = ?, chat_hearing_distance = ?, chat_protection =?, who_can_mute = ?, who_can_kick = ?, who_can_ban = ?, poll_id = ?, guild_id = ?, roller_speed = ?, override_model = ?, is_staff_picked = ?, promoted = ?, trade_mode = ? WHERE id = ?");
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
                statement.setInt(35, this.id);
                statement.execute();
                statement.close();
                statement.getConnection().close();
                this.needsUpdate = false;
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    private synchronized void updateDatabaseUserCount()
    {
        PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE rooms SET users = ? WHERE id = ? LIMIT 1");

        try
        {
            statement.setInt(1, this.currentHabbos.size());
            statement.setInt(2, this.id);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
    private synchronized void cycle()
    {
        int currentTimestamp = Emulator.getIntUnixTimestamp();

        boolean foundRightHolder = false;

        if(this.loaded)
        {
            if (!this.currentHabbos.isEmpty())
            {
                this.idleCycles = 0;

                THashSet<RoomUnit> updatedUnit = new THashSet<RoomUnit>();
                ArrayList<Habbo> toKick = new ArrayList<Habbo>();
                synchronized (this.currentHabbos)
                {
                    for (Habbo habbo : this.currentHabbos.valueCollection())
                    {
                        if (!foundRightHolder)
                        {
                            foundRightHolder = hasRights(habbo) || guildRightLevel(habbo) > 2;
                        }

                        if (Emulator.getConfig().getBoolean("hotel.rooms.auto.idle"))
                        {
                            if (!habbo.getRoomUnit().isIdle())
                            {
                                habbo.getRoomUnit().increaseIdleTimer();

                                if (habbo.getRoomUnit().isIdle())
                                {
                                    this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
                                }
                            }
                            else
                            {
                                habbo.getRoomUnit().increaseIdleTimer();

                                if (!this.isOwner(habbo) && habbo.getRoomUnit().getIdleTimer() >= Emulator.getConfig().getInt("hotel.roomuser.idle.cycles.kick", 480))
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

                        if (!habbo.hasPermission("acc_chat_no_flood") && habbo.getRoomUnit().talkCounter > 0)
                        {
                            //if (habbo.getRoomUnit().talkTimeOut == 0 || currentTimestamp - habbo.getRoomUnit().talkTimeOut < 0)
                            {
                                if (habbo.getRoomUnit().talkCounter > 2)
                                {
                                    if (this.chatProtection == 3)
                                    {
                                        this.floodMuteHabbo(habbo, 30);
                                    }
                                    else if (this.chatProtection == 2 && habbo.getRoomUnit().talkCounter > 4)
                                    {
                                        this.floodMuteHabbo(habbo, 30);
                                    }
                                    else if (this.chatProtection == 1 && habbo.getRoomUnit().talkCounter > 5)
                                    {
                                        this.floodMuteHabbo(habbo, 30);
                                    }
                                }
                                else
                                {
                                    habbo.getRoomUnit().talkCounter--;
                                }
                            }
                        }
                        else
                        {
                            habbo.getRoomUnit().talkCounter = 0;
                        }

                        if (habbo.getRoomUnit().getStatus().containsKey("sign"))
                        {
                            this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
                            habbo.getRoomUnit().getStatus().remove("sign");
                        }

                        if (habbo.getRoomUnit().isWalking() && habbo.getRoomUnit().getPathFinder().getPath() != null && !habbo.getRoomUnit().getPathFinder().getPath().isEmpty())
                        {
                            if (!habbo.getRoomUnit().cycle(this))
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
                                HabboItem topItem = this.getLowestChair(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

                                if (topItem == null || !topItem.getBaseItem().allowSit())
                                {
                                    if (habbo.getRoomUnit().getStatus().containsKey("sit"))
                                    {
                                        habbo.getRoomUnit().getStatus().remove("sit");
                                        updatedUnit.add(habbo.getRoomUnit());
                                    }
                                } else
                                {
                                    if (!habbo.getRoomUnit().getStatus().containsKey("sit"))
                                    {
                                        if (topItem instanceof InteractionMultiHeight)
                                        {
                                            habbo.getRoomUnit().getStatus().put("sit", Item.getCurrentHeight(topItem) * 1.0D + "");
                                        } else
                                        {
                                            habbo.getRoomUnit().getStatus().put("sit", topItem.getBaseItem().getHeight() * 1.0D + "");
                                        }
                                        habbo.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);
                                        updatedUnit.add(habbo.getRoomUnit());
                                    }
                                }
                            }
                        }

                        if (!habbo.getRoomUnit().isWalking() && !habbo.getRoomUnit().cmdLay)
                        {
                            HabboItem topItem = this.getTopItemAt(habbo.getRoomUnit().getX(), habbo.getRoomUnit().getY());

                            if (topItem == null || !topItem.getBaseItem().allowLay())
                            {
                                if (habbo.getRoomUnit().getStatus().containsKey("lay"))
                                {
                                    habbo.getRoomUnit().getStatus().remove("lay");
                                    updatedUnit.add(habbo.getRoomUnit());
                                }
                            } else
                            {
                                if (!habbo.getRoomUnit().getStatus().containsKey("lay"))
                                {
                                    if (topItem instanceof InteractionMultiHeight)
                                    {
                                        habbo.getRoomUnit().getStatus().put("lay", Item.getCurrentHeight(topItem) * 1.0D + "");
                                    } else
                                    {
                                        habbo.getRoomUnit().getStatus().put("lay", topItem.getBaseItem().getHeight() * 1.0D + "");
                                    }
                                    habbo.getRoomUnit().setRotation(RoomUserRotation.values()[topItem.getRotation()]);

                                    if (topItem.getRotation() == 0 || topItem.getRotation() == 4)
                                    {
                                        habbo.getRoomUnit().setY(topItem.getY());
                                        habbo.getRoomUnit().setOldY(topItem.getY());
                                    }
                                    else
                                    {
                                        habbo.getRoomUnit().setX(topItem.getX());
                                        habbo.getRoomUnit().setOldX(topItem.getX());
                                    }
                                    updatedUnit.add(habbo.getRoomUnit());
                                }
                            }
                        }

                        if (habbo.getRoomUnit().getHandItem() > 0 && habbo.getRoomUnit().getHandItemTimestamp() - System.currentTimeMillis() > 90000)
                        {
                            habbo.getRoomUnit().setHandItem(0);
                            this.sendComposer(new RoomUserHandItemComposer(habbo.getRoomUnit()).compose());
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

                synchronized (this.currentBots)
                {
                    if (!this.currentBots.isEmpty())
                    {
                        TIntObjectIterator<Bot> botIterator = this.currentBots.iterator();
                        for (int i = this.currentBots.size(); i-- > 0; )
                        {
                            try
                            {
                                botIterator.advance();
                                Bot bot = botIterator.value();
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
                                            bot.getRoomUnit().setX(topItem.getX());
                                            bot.getRoomUnit().setY(topItem.getY());
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
                }

                synchronized (this.currentPets)
                {
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
                                    if (pet instanceof MonsterplantPet)
                                        return;

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
                                        return;
                                    }

                                    if (((Pet) pet).cycle())
                                    {
                                        updatedUnit.add(pet.getRoomUnit());
                                    }

                                    if (((Pet) pet).getTask() == PetTask.RIDE)
                                    {
                                        if (pet instanceof HorsePet)
                                        {
                                            HorsePet horse = ((HorsePet) pet);
                                            if (horse.getRider() != null)
                                            {
                                                if (!horse.getRider().getRoomUnit().getLocation().equals(horse.getRoomUnit().getLocation()))
                                                {
                                                    horse.getRoomUnit().setGoalLocation(horse.getRider().getRoomUnit().getLocation());
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

                                    if (pet.getRoomUnit().isWalking() && pet.getRoomUnit().getPathFinder().getPath().size() == 1 && pet.getRoomUnit().getStatus().containsKey("gst"))
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
                }

                if(!updatedUnit.isEmpty())
                {
                    this.sendComposer(new RoomUserStatusComposer(updatedUnit, true).compose());
                }

                if(((System.currentTimeMillis() - this.lastRollerCycle) / 500)  > this.rollerSpeed)
                {
                    this.lastRollerCycle = System.currentTimeMillis();

                    THashSet<MessageComposer> messages = new THashSet<MessageComposer>();

                    for (InteractionRoller roller : this.roomSpecialTypes.getRollers())
                    {
                        if(Double.compare(roller.getZ(), this.layout.getHeightAtSquare(roller.getX(), roller.getY())) != 0)
                        {
                            continue;
                        }

                        HabboItem newRoller = null;

                        THashSet<Habbo> habbosOnRoller = this.getHabbosAt(roller.getX(), roller.getY());
                        THashSet<HabboItem> itemsOnRoller = this.getItemsAt(roller.getX(), roller.getY(), roller.getZ() + roller.getBaseItem().getHeight());

                        itemsOnRoller.remove(roller);

                        if (habbosOnRoller.isEmpty())
                        {
                            if (itemsOnRoller.isEmpty())
                                continue;
                        }

                        Tile t = PathFinder.getSquareInFront(roller.getX(), roller.getY(), roller.getRotation());

                        if(!this.layout.tileExists(t.X, t.Y))
                            continue;

                        if (this.layout.getSquareStates()[t.X][t.Y] == RoomTileState.BLOCKED)
                            continue;

                        if (!this.getHabbosAt(t.X, t.Y).isEmpty())
                            continue;

                        THashSet<HabboItem> itemsNewTile = this.getItemsAt(t.X, t.Y);

                        boolean allowUsers = true;

                        for (HabboItem item : itemsNewTile)
                        {
                            if (!item.getBaseItem().allowWalk() && !item.getBaseItem().allowSit())
                            {
                                allowUsers = false;
                            }
                            if (item instanceof InteractionRoller)
                            {
                                newRoller = item;

                                if (itemsNewTile.size() > 1)
                                    continue;

                                break;
                            }
                        }

                        double zOffset = 0;
                        if(newRoller == null)
                        {
                            zOffset = -roller.getBaseItem().getHeight();
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
                                Tile tile = t.copy();
                                tile.Z = habbo.getRoomUnit().getZ() + zOffset;
                                if (!habbo.getRoomUnit().getStatus().containsKey("mv"))
                                {

                                    if(roomUserRolledEvent != null)
                                    {
                                        roomUserRolledEvent = new UserRolledEvent(habbo, roller, tile);
                                        Emulator.getPluginManager().fireEvent(roomUserRolledEvent);

                                        if(roomUserRolledEvent.isCancelled())
                                            continue;
                                    }

                                    messages.add(new RoomUnitOnRollerComposer(habbo.getRoomUnit(), roller, tile, this));
                                }

                                if(habbo.getRoomUnit().getStatus().containsKey("sit"))
                                    habbo.getRoomUnit().sitUpdate = true;
                            }
                        }

                        /**
                         * Redneck way to prevent checking ifregistered each time.
                         */
                        Event furnitureRolledEvent = null;

                        if(Emulator.getPluginManager().isRegistered(FurnitureRolledEvent.class, true))
                        {
                            furnitureRolledEvent = new FurnitureRolledEvent(null, null, null);
                        }

                        for (HabboItem item : itemsOnRoller)
                        {
                            Tile tile = t.copy();
                            tile.Z = item.getZ() + zOffset;

                            if(furnitureRolledEvent != null)
                            {
                                furnitureRolledEvent = new FurnitureRolledEvent(item, roller, tile);
                                Emulator.getPluginManager().fireEvent(furnitureRolledEvent);

                                if(furnitureRolledEvent.isCancelled())
                                    continue;
                            }

                            if(item != roller)
                                messages.add(new FloorItemOnRollerComposer(item, roller, tile, this));
                        }
                    }

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
            }
            else
            {

                if(this.idleCycles < 60)
                    ++this.idleCycles;
                else
                    this.dispose();
            }
        }

        synchronized (this.habboQueue)
        {
            if (!this.habboQueue.isEmpty() && !foundRightHolder)
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
    }

    public int getId() {
        return this.id;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public RoomLayout getLayout() {
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

    public String getPassword() {
        return this.password;
    }

    public RoomState getState() {
        return this.state;
    }

    public int getUsersMax() {
        return this.usersMax;
    }

    public int getScore() {
        return this.score;
    }

    public int getCategory() {
        return this.category;
    }

    public String getFloorPaint() {
        return this.floorPaint;
    }

    public String getWallPaint() {
        return this.wallPaint;
    }

    public String getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public int getWallSize() {
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

    public int getFloorSize() {
        return this.floorSize;
    }

    public String getTags() {
        return this.tags;
    }

    public int getTradeMode()
    {
        return this.tradeMode;
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

    public boolean isPublicRoom() {
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

    public boolean isAllowPets() {
        return this.allowPets;
    }

    public boolean isAllowPetsEat() {
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

    public boolean isHideWall() {
        return this.hideWall;
    }

    public synchronized Color getBackgroundTonerColor()
    {
        final Color[] color = {new Color(0, 0, 0)};
        this.roomItems.forEachValue(new TObjectProcedure<HabboItem>()
        {
            @Override
            public boolean execute(HabboItem object)
            {
                if (object instanceof InteractionBackgroundToner)
                {
                    String[] extraData = object.getExtradata().split(":");

                    if (extraData.length == 4)
                    {
                        if (extraData[0].equalsIgnoreCase("1"))
                        {
                            color[0] = Color.getHSBColor(Integer.valueOf(extraData[1]), Integer.valueOf(extraData[2]), Integer.valueOf(extraData[3]));
                            return false;
                        }
                    }
                }

                return true;
            }
        });

        return color[0];
    }

    public int getChatMode() {
        return this.chatMode;
    }

    public int getChatWeight() {
        return this.chatWeight;
    }

    public int getChatSpeed() {
        return this.chatSpeed;
    }

    public int getChatDistance() {
        return this.chatDistance;
    }

    public void setName(String name)
    {
        this.name = name;

        if (this.hasGuild())
        {
            Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(this.guild);
            guild.setRoomName(name);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setState(RoomState state) {
        this.state = state;
    }

    public void setUsersMax(int usersMax) {
        this.usersMax = usersMax;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setFloorPaint(String floorPaint) {
        this.floorPaint = floorPaint;
    }

    public void setWallPaint(String wallPaint) {
        this.wallPaint = wallPaint;
    }

    public void setBackgroundPaint(String backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public void setWallSize(int wallSize) {
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

    public void setAllowPets(boolean allowPets) {
        this.allowPets = allowPets;
    }

    public void setAllowPetsEat(boolean allowPetsEat) {
        this.allowPetsEat = allowPetsEat;
    }

    public void setAllowWalkthrough(boolean allowWalkthrough) {
        this.allowWalkthrough = allowWalkthrough;
    }

    public void setAllowBotsWalk(boolean allowBotsWalk)
    {
        this.allowBotsWalk = allowBotsWalk;
    }

    public void setHideWall(boolean hideWall) {
        this.hideWall = hideWall;
    }

    public void setChatMode(int chatMode) {
        this.chatMode = chatMode;
    }

    public void setChatWeight(int chatWeight) {
        this.chatWeight = chatWeight;
    }

    public void setChatSpeed(int chatSpeed) {
        this.chatSpeed = chatSpeed;
    }

    public void setChatDistance(int chatDistance) {
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

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO room_promotions (room_id, title, description, end_timestamp) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE title = ?, description = ?, end_timestamp = ?");
            statement.setInt(1, this.id);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setInt(4, this.promotion.getEndTimestamp());
            statement.setString(5, this.promotion.getTitle());
            statement.setString(6, this.promotion.getDescription());
            statement.setInt(7, this.promotion.getEndTimestamp());
            statement.execute();
            statement.close();
            statement.getConnection().close();
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

    public boolean deleteGame(Class<? extends Game> gameType)
    {
        THashSet<Object> objects = new THashSet<Object>();

        synchronized (this.games)
        {
            for(Game game: this.games)
            {
                if(gameType.isInstance(game))
                {
                    objects.add(game);
                }
            }

            return this.games.removeAll(objects);
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
        synchronized (this.currentHabbos)
        {
            return this.currentHabbos.size();
        }
    }

    public TIntObjectMap<Habbo> getCurrentHabbos()
    {
        return this.currentHabbos;
    }

    public TIntObjectMap<Habbo> getHabboQueue()
    {
        return this.habboQueue;
    }

    public TIntObjectMap<String> getFurniOwnerNames()
    {
        return this.furniOwnerNames;
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
        this.sendComposer(new HideDoorbellComposer(habbo.getHabboInfo().getUsername()).compose());

        synchronized (this.habboQueue)
        {
            return this.habboQueue.remove(habbo.getHabboInfo().getId()) != null;
        }
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

    public int getUnitCounter() {
        return unitCounter;
    }

    public RoomSpecialTypes getRoomSpecialTypes()
    {
        return this.roomSpecialTypes;
    }

    public boolean isPreLoaded()
    {
        return this.preLoaded;
    }

    public void setPreLoaded(boolean value)
    {
        this.preLoaded = value;
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

    public void addHabboItem(HabboItem item)
    {
        if(item == null)
            return;

        synchronized (this.roomItems)
        {
            this.roomItems.put(item.getId(), item);
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

        synchronized (this.roomSpecialTypes)
        {
            if (item instanceof InteractionWiredTrigger)
            {
                this.roomSpecialTypes.addTrigger((InteractionWiredTrigger) item);
            } else if (item instanceof InteractionWiredEffect)
            {
                this.roomSpecialTypes.addEffect((InteractionWiredEffect) item);
            } else if (item instanceof InteractionWiredCondition)
            {
                this.roomSpecialTypes.addCondition((InteractionWiredCondition) item);
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
        HabboItem i;
        synchronized (this.roomItems)
        {
            i = this.roomItems.remove(item.getId());
        }

        if(i != null)
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
            else if(item instanceof InteractionMoodLight)
            {
                this.roomSpecialTypes.removeUndefined(item);
            }
            else if(item instanceof InteractionPyramid)
            {
                this.roomSpecialTypes.removeUndefined(item);
            }
            else if(item instanceof InteractionMusicDisc)
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
            else if (item instanceof InteractionWater)
            {
                this.roomSpecialTypes.removeUndefined(item);
            }
            else if (item instanceof InteractionWaterItem)
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
        }
    }

    public THashSet<HabboItem> getFloorItems()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    if (iterator.value().getBaseItem().getType().toLowerCase().equals("s"))
                        items.add(iterator.value());
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
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
    }

    public THashSet<HabboItem> getWallItems()
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    if (iterator.value().getBaseItem().getType().toLowerCase().equals("i"))
                        items.add(iterator.value());
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
            }

            return items;
        }
    }

    public void addHabbo(Habbo habbo)
    {
        this.currentHabbos.put(habbo.getHabboInfo().getId(), habbo);
        this.unitCounter++;
        this.updateDatabaseUserCount();
    }

    public void kickHabbo(Habbo habbo)
    {
        this.kickHabbo(habbo, false);
    }

    public void kickHabbo(Habbo habbo, boolean alert)
    {
        Emulator.getGameEnvironment().getRoomManager().leaveRoom(habbo, this);

        if(alert)
        {
            habbo.getClient().sendResponse(new GenericErrorMessagesComposer(GenericErrorMessagesComposer.KICKED_OUT_OF_THE_ROOM));
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

        synchronized (this.currentHabbos)
        {
            TIntObjectIterator<Habbo> iterator = this.currentHabbos.iterator();

            for(int i = this.currentHabbos.size(); i-- > 0;)
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

                if (iterator.value() == habbo)
                    iterator.remove();
            }
        }

        if(habbo.getHabboInfo().getCurrentGame() != null)
        {
            if (this.getGame(habbo.getHabboInfo().getCurrentGame()) != null)
            {
                this.getGame(habbo.getHabboInfo().getCurrentGame()).removeHabbo(habbo);
            }
        }

        this.updateDatabaseUserCount();
    }

    public void addBot(Bot bot)
    {
        this.currentBots.put(bot.getId(), bot);
        this.unitCounter++;
    }

    public void addPet(AbstractPet pet)
    {
        this.currentPets.put(pet.getId(), pet);
        this.unitCounter++;
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

    public Bot removeBot(int botId)
    {
        this.currentBots.get(botId).getRoomUnit().setInRoom(false);
        return this.currentBots.remove(botId);
    }

    public AbstractPet removePet(int petId)
    {
        return this.currentPets.remove(petId);
    }

    public boolean hasHabbosAt(int x, int y)
    {
        synchronized (this.currentHabbos)
        {
            TIntObjectIterator<Habbo> habboIterator = this.currentHabbos.iterator();

            for (int i = this.currentHabbos.size(); i-- > 0; )
            {
                try
                {
                    habboIterator.advance();
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }

                if (habboIterator.value().getRoomUnit().getX() == x && habboIterator.value().getRoomUnit().getY() == y)
                    return true;
            }
        }
        return false;
    }

    public THashSet<Habbo> getHabbosAt(Tile tile)
    {
        return this.getHabbosAt(tile.X, tile.Y);
    }

    public THashSet<Habbo> getHabbosAt(int x, int y)
    {
        THashSet<Habbo> habbos = new THashSet<Habbo>();

        synchronized (this.currentHabbos)
        {
            TIntObjectIterator<Habbo> habboIterator = this.currentHabbos.iterator();

            for (int i = this.currentHabbos.size(); i-- > 0; )
            {
                try
                {
                    habboIterator.advance();
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }

                if (habboIterator.value().getRoomUnit().getX() == x && habboIterator.value().getRoomUnit().getY() == y)
                    habbos.add(habboIterator.value());
            }
        }

        return habbos;
    }

    public THashSet<Habbo> getHabbosOnItem(HabboItem item)
    {
        THashSet<Habbo> habbos = new THashSet<Habbo>();
        for(int x = item.getX(); x < item.getX() + item.getBaseItem().getLength(); x++)
        {
            for(int y = item.getY(); y < item.getY() + item.getBaseItem().getWidth(); y++)
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

    public void teleportHabboToLocation(Habbo habbo, int x, int y)
    {
        this.teleportHabboToLocation(habbo, x, y, 0.0);
    }

    void teleportHabboToLocation(Habbo habbo, int x, int y, double z)
    {
        habbo.getRoomUnit().setGoalLocation(x, y);
        habbo.getRoomUnit().setLocation(x, y, z);
        this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
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
        habbo.getRoomUnit().talkCounter = 0;
        habbo.getRoomUnit().talkTimeOut = Emulator.getIntUnixTimestamp() + timeOut;
        habbo.getClient().sendResponse(new FloodCounterComposer(timeOut));
        habbo.getClient().sendResponse(new MutedWhisperComposer(timeOut));
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType)
    {
        this.talk(habbo, roomChatMessage, chatType, false);
    }

    public void talk(Habbo habbo, RoomChatMessage roomChatMessage, RoomChatType chatType, boolean ignoreWired)
    {
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
            if(((InteractionMuteArea)area).inSquare(habbo.getRoomUnit().getLocation().getLocation()))
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

        if (chatType != RoomChatType.WHISPER)
        {
            if (CommandHandler.handleCommand(habbo.getClient(), roomChatMessage.getMessage()))
            {
                roomChatMessage.isCommand = true;
                return;
            }

            if(!ignoreWired)
            {
                if (WiredHandler.handle(WiredTriggerType.SAY_SOMETHING, habbo.getRoomUnit(), habbo.getHabboInfo().getCurrentRoom(), new Object[]{roomChatMessage.getMessage()}))
                {
                    return;
                }
            }
        }

        if(!habbo.hasPermission("acc_nomute"))
        {
            if(this.isMuted() && !this.isOwner(habbo))
            {
                return;
            }

            if (this.isMuted(habbo))
            {
                habbo.getClient().sendResponse(new MutedWhisperComposer(this.mutedHabbos.get(habbo.getHabboInfo().getId()) - Emulator.getIntUnixTimestamp()));
                return;
            }
        }

        if (habbo.getRoomUnit().talkTimeOut > 0 && (Emulator.getIntUnixTimestamp() - habbo.getRoomUnit().talkTimeOut < 0))
            return;

        habbo.getRoomUnit().talkCounter++;

        if(chatType == RoomChatType.WHISPER)
        {
            ServerMessage message = new RoomUserWhisperComposer(roomChatMessage).compose();
            RoomChatMessage staffChatMessage = new RoomChatMessage(roomChatMessage);
            staffChatMessage.setMessage("To " + staffChatMessage.getTargetHabbo().getHabboInfo().getUsername() + ": " + staffChatMessage.getMessage());
            ServerMessage staffMessage = new RoomUserWhisperComposer(staffChatMessage).compose();
            for(Habbo h : this.getCurrentHabbos().valueCollection())
            {
                if(h == roomChatMessage.getTargetHabbo() || h == habbo)
                {
                    if(!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                        h.getClient().sendResponse(message);

                    continue;
                }
                if(h.hasPermission("acc_see_whispers"))
                {
                    h.getClient().sendResponse(staffMessage);
                }
            }
        }
        else if (chatType == RoomChatType.TALK)
        {
            ServerMessage message = new RoomUserTalkComposer(roomChatMessage).compose();

            synchronized (this.currentHabbos)
            {
                boolean noChatLimit =  habbo.hasPermission("acc_chat_no_limit");

                for (Habbo h : this.getCurrentHabbos().valueCollection())
                {
                    if (h.getRoomUnit().getLocation().distance(habbo.getRoomUnit().getLocation()) <= this.chatDistance ||
                        h.hasPermission("acc_chat_no_limit") ||
                        h.equals(habbo) ||
                        this.hasRights(h) ||
                        noChatLimit)
                    {
                        if (!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                            h.getClient().sendResponse(message);
                    }
                }
            }
        }
        else if(chatType == RoomChatType.SHOUT)
        {
            ServerMessage message = new RoomUserShoutComposer(roomChatMessage).compose();

            synchronized (this.currentHabbos)
            {
                for (Habbo h : this.getCurrentHabbos().valueCollection())
                {
                    if (!h.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                        h.getClient().sendResponse(message);
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
                        Tile t = new Tile(item.getX(), item.getY(), item.getZ());

                        if (t.distance(habbo.getRoomUnit().getLocation()) <= Emulator.getConfig().getInt("furniture.talking.range"))
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

    public GameMap<Node> getGameMap()
    {
        for(Node node : this.gameMap.getNodes())
        {
            this.gameMap.setWalkable(node.getX(), node.getY(), this.tileWalkable(node.getX(), node.getY()));
        }
        return this.gameMap;
    }

    public boolean hasItemsAt(int x, int y)
    {
        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    if(iterator.value().getX() == x &&
                       iterator.value().getY() == y)
                    {
                        return true;
                    }
                }
                catch (NoSuchElementException e)
                {
                    return false;
                }
            }
        }

        return false;
    }

    public THashSet<Tile> getLockedTiles()
    {
        THashSet<Tile> lockedTiles = new THashSet<Tile>();

        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    boolean found = false;
                    for(Tile tile : lockedTiles)
                    {
                        if(tile.x == iterator.value().getX() &&
                           tile.y == iterator.value().getY())
                        {
                            found = true;
                            break;
                        }
                    }

                    if(!found)
                    {
                        if(iterator.value().getRotation() == 0 || iterator.value().getRotation() == 4)
                        {
                            for(int y = 0; y < iterator.value().getBaseItem().getLength(); y++)
                            {
                                for(int x = 0; x < iterator.value().getBaseItem().getWidth(); x++)
                                {
                                    lockedTiles.add(new Tile(iterator.value().getX() + x, iterator.value().getY() + y, 0));
                                }
                            }
                        }
                        else
                        {
                            for(int y = 0; y < iterator.value().getBaseItem().getWidth(); y++)
                            {
                                for(int x = 0; x < iterator.value().getBaseItem().getLength(); x++)
                                {
                                    lockedTiles.add(new Tile(iterator.value().getX() + x, iterator.value().getY() + y, 0));
                                }
                            }
                        }
                    }
                }
                catch (NoSuchElementException e)
                {
                }
            }
        }

        return lockedTiles;
    }

    public THashSet<HabboItem> getItemsAt(int x, int y)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    HabboItem item = iterator.value();

                    if (item.getX() == x && item.getY() == y)
                    {
                        items.add(item);
                    }
                    else
                    {
                        if (item.getBaseItem().getWidth() <= 1 && item.getBaseItem().getLength() <= 1)
                        {
                            continue;
                        }

                        THashSet<Tile> tiles = PathFinder.getTilesAt(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                        for (Tile tile : tiles)
                        {
                            if ((tile.X == x) && (tile.Y == y) && (!items.contains(item)))
                            {
                                items.add(item);
                            }
                        }
                    }
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
            }
        }
        return items;
    }

    public THashSet<HabboItem> getItemsAt(int x, int y, double minZ)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.getFloorItems())
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

                THashSet<Tile> tiles = PathFinder.getTilesAt(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                for (Tile tile : tiles){
                    if ((tile.X == x) && (tile.Y == y) && (!items.contains(item))) {
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

                    THashSet<Tile> tiles = PathFinder.getTilesAt(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());
                    for (Tile tile : tiles){
                        if ((tile.X == x) && (tile.Y == y) && (!items.contains(item))) {
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

        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    HabboItem habboItem = iterator.value();

                    if (!habboItem.getBaseItem().getType().toLowerCase().equals("s"))
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

                        THashSet<Tile> tiles = PathFinder.getTilesAt(habboItem.getX(), habboItem.getY(), habboItem.getBaseItem().getWidth(), habboItem.getBaseItem().getLength(), habboItem.getRotation());
                        for (Tile tile : tiles)
                        {
                            if (((tile.X == x) && (tile.Y == y)))
                            {
                                if (item == null || item.getZ() < habboItem.getZ())
                                    item = habboItem;
                            }
                        }
                    }
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
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

    public HabboItem getLowestChair(int x, int y)
    {
        HabboItem lowestChair = null;

        for(HabboItem item : this.getItemsAt(x, y))
        {
            if(item.getBaseItem().allowSit())
            {
                if(lowestChair == null || item.getZ() < lowestChair.getZ())
                {
                    lowestChair = item;
                }
            }

            if(lowestChair != null)
            {
                if(item.getZ() > lowestChair.getZ() && item.getZ() - lowestChair.getZ() < 1.5)
                {
                    lowestChair = null;
                }
            }
        }

        return lowestChair;
    }

    public HabboItem getStackHelper(int x, int y)
    {
        THashSet<HabboItem> items = this.getItemsAt(x, y);

        for(HabboItem item : items)
        {
            if(item instanceof InteractionStackHelper)
            {
                return item;
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

        for(HabboItem item : this.getItemsAt(x, y))
        {
            if(item instanceof InteractionStackHelper)
            {
                height = item.getExtradata().isEmpty() ? Double.valueOf("0.0") : (Double.valueOf(item.getExtradata()) / 100);
                canStack = true;
                break;
            }
            if(item.getBaseItem().allowSit())
            {
                canStack = false;
                height = 0.0D;
                break;
            }

            if(!item.getBaseItem().allowStack())
            {
                canStack = false;
                break;
            }

            double itemHeight = (item.getBaseItem().allowSit() ? 0.0D : item.getBaseItem().getHeight()) + item.getZ();

            if(item instanceof InteractionMultiHeight)
            {
                if(item.getExtradata().length() == 0)
                {
                    item.setExtradata("0");
                }
                itemHeight += Item.getCurrentHeight(item);
            }

            if(itemHeight > height)
            {
                height = itemHeight;
            }
        }

        if(calculateHeightmap)
        {
            return (canStack ? height * 256.0D : Short.MAX_VALUE);
        }

        return height;
    }

    public double getStackHeight(int x, int y, boolean calculateHeightmap, HabboItem i)
    {
        if(x < 0 || y < 0)
            return 0.0;

        double height = this.layout.getHeightAtSquare(x, y);
        boolean canStack = true;

        for(HabboItem item : this.getItemsAt(x, y))
        {
            if(item == i)
                continue;

            if(item.getBaseItem().allowSit())
            {
                canStack = false;
                height = 0.0D;
                break;
            }

            if(!item.getBaseItem().allowStack())
            {
                canStack = false;
                break;
            }

            double itemHeight = (item.getBaseItem().allowSit() ? 0.0D : item.getBaseItem().getHeight()) + item.getZ();

            if(item instanceof InteractionMultiHeight)
            {
                if(item.getExtradata().length() == 0)
                {
                    item.setExtradata("0");
                }
                itemHeight += Item.getCurrentHeight(item);
            }

            if(itemHeight > height)
            {
                height = itemHeight;
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

    boolean canSitAt(THashSet<HabboItem> items)
    {
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
            //else if(item.getZ() == topItem.getZ())
           // {
             //   canSit = topItem.getBaseItem().allowSit();
            //}
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

    public Tile getRandomWalkableTile()
    {
        while(true)
        {
            Tile tile = new Tile((int) (Math.random() * this.layout.getMapSizeX()), (int) (Math.random() * this.layout.getMapSizeY()), 0);

            if (this.tileWalkable(tile.X, tile.Y) || this.canSitAt(tile.X, tile.Y))
            {
                return tile;
            }
        }
    }

    public Habbo getHabbo(String username)
    {
        synchronized (this.currentHabbos)
        {
            for (Habbo habbo : this.currentHabbos.valueCollection())
            {
                if (habbo.getHabboInfo().getUsername().equals(username))
                    return habbo;
            }
        }
        return null;
    }

    public Habbo getHabbo(RoomUnit roomUnit)
    {
        synchronized (this.currentHabbos)
        {
            for (Habbo habbo : this.currentHabbos.valueCollection())
            {
                if (habbo.getRoomUnit() == roomUnit)
                    return habbo;
            }
        }
        return null;
    }

    public Habbo getHabbo(int userId)
    {
        return this.currentHabbos.get(userId);
    }

    public Habbo getHabboByRoomUnitId(int roomUnitId)
    {
        for (Habbo habbo : this.currentHabbos.valueCollection())
        {
            if(habbo.getRoomUnit().getId() == roomUnitId)
                return habbo;
        }

        return null;
    }

    //public void sendComposer(MessageComposer packet)
    //{
    //    for(Habbo habbo : this.currentHabbos)
    //    {
   //         habbo.getClient().sendResponse(packet);
    //    }
   // }

    public void sendComposer(ServerMessage message)
    {
        synchronized (this.currentHabbos)
        {
            TIntObjectIterator<Habbo> iterator = this.currentHabbos.iterator();

            for (int i = this.currentHabbos.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();
                    iterator.value().getClient().sendResponse(message);
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
            }
        }
    }

    void loadRights()
    {
        this.rights.clear();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT user_id FROM room_rights WHERE room_id = ?");
            statement.setInt(1, this.id);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                this.rights.add(set.getInt("user_id"));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    void loadBans()
    {
        this.bannedHabbos.clear();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username, users.id, room_bans.* FROM room_bans INNER JOIN users ON room_bans.user_id = users.id WHERE ends > ?");
            statement.setInt(1, Emulator.getIntUnixTimestamp());
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                if(this.bannedHabbos.containsKey(set.getInt("user_id")))
                    continue;

                this.bannedHabbos.put(set.getInt("user_id"), new RoomBan(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

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
        return isOwner(habbo) || this.rights.contains(habbo.getHabboInfo().getId());
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
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO room_rights VALUES (?, ?)");
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
                statement.close();
                statement.getConnection().close();
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
        this.sendComposer(new RoomRemoveRightsListComposer(this, userId).compose());

        Habbo habbo = this.getHabbo(userId);

        if(Emulator.getPluginManager().fireEvent(new UserRightsTakenEvent(this.getHabbo(this.getOwnerId()), userId, habbo)).isCancelled())
            return;

        if(this.rights.remove(userId))
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM room_rights WHERE room_id = ? AND user_id = ?");
                statement.setInt(1, this.id);
                statement.setInt(2, userId);
                statement.execute();
                statement.close();
                statement.getConnection().close();
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

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM room_rights WHERE room_id = ?");
            statement.setInt(1, this.id);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.refreshRightsInRoom();
    }

    void refreshRightsInRoom()
    {
        for (Habbo habbo : this.currentHabbos.valueCollection())
        {
            if(habbo.getHabboInfo().getCurrentRoom() != this)
                continue;

            this.refreshRightsForHabbo(habbo);
        }
    }

    public void refreshRightsForHabbo(Habbo habbo)
    {
        HabboItem item;
        if (!habbo.getHabboStats().canRentSpace())
        {
            item = this.getHabboItem(habbo.getHabboStats().getRentedItemId());

            if (item != null)
            {
                habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.GUILD_ADMIN));
                habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.GUILD_ADMIN.level + "");
                return;
            }
        }

        if (habbo.hasPermission("acc_anyroomowner"))
        {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.MODERATOR));
            habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.MODERATOR.level + "");
            habbo.getClient().sendResponse(new RoomRightsListComposer(this));
        }
        else if (this.isOwner(habbo))
        {
            habbo.getClient().sendResponse(new RoomOwnerComposer());
            habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.MODERATOR));
            habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.MODERATOR.level + "");
            habbo.getClient().sendResponse(new RoomRightsListComposer(this));
        }
        else if (this.hasRights(habbo) && !this.hasGuild())
        {
            habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.RIGHTS));
            habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.RIGHTS.level + "");
        }
        else if (this.hasGuild())
        {
            int level = this.guildRightLevel(habbo);

            if(level == 3)
            {
                habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.GUILD_ADMIN));
                habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.GUILD_ADMIN.level + "");
            }
            else if(level == 2)
            {
                habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.GUILD_RIGHTS));
                habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.GUILD_RIGHTS.level + "");
            }
            else
            {
                habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.NONE));
                habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.NONE.level + "");
            }
        }
        else
        {
            habbo.getClient().sendResponse(new RoomRightsComposer(RoomRightLevels.NONE));
            habbo.getRoomUnit().getStatus().put("flatctrl", RoomRightLevels.NONE.level + "");
        }
    }

    public THashMap<Integer, String> getUsersWithRights()
    {
        THashMap<Integer, String> rightsMap = new THashMap<Integer, String>();

        if(!this.rights.isEmpty())
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT users.username AS username, users.id as user_id FROM room_rights INNER JOIN users ON room_rights.user_id = users.id WHERE room_id = ?");
                statement.setInt(1, this.id);
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    rightsMap.put(set.getInt("user_id"), set.getString("username"));
                }

                set.close();
                statement.close();
                statement.getConnection().close();
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
        habbo.getRoomUnit().cmdSit = true;
        habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[habbo.getRoomUnit().getBodyRotation().getValue() - habbo.getRoomUnit().getBodyRotation().getValue() % 2]);
        habbo.getRoomUnit().getStatus().put("sit", habbo.getRoomUnit().getZ() + 0.5 + "");
        this.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());
    }

    public void giveEffect(Habbo habbo, int effectId)
    {
        this.giveEffect(habbo.getRoomUnit(), effectId);
    }

    public void giveEffect(RoomUnit roomUnit, int effectId)
    {
        roomUnit.setEffectId(effectId);
        this.sendComposer(new RoomUserEffectComposer(roomUnit).compose());
    }

    public void updateItem(HabboItem item)
    {
        if(item.getRoomId() == this.id)
        {
            if(item.getBaseItem() != null)
            {
                if(item.getBaseItem().getType().equalsIgnoreCase("s"))
                    this.sendComposer(new FloorItemUpdateComposer(item).compose());
                else if(item.getBaseItem().getType().equalsIgnoreCase("i"))
                    this.sendComposer(new WallItemUpdateComposer(item).compose());
            }
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

        synchronized (this.roomItems)
        {
            TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

            for (int i = this.roomItems.size(); i-- > 0; )
            {
                try
                {
                    iterator.advance();

                    if (iterator.value().getUserId() == userId)
                    {
                        items.add(iterator.value());
                        iterator.value().setRoomId(0);
                    }
                }
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
            }

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null)
            {
                habbo.getHabboInventory().getItemsComponent().addItems(items);
                habbo.getClient().sendResponse(new AddHabboItemComposer(items));
            }

            for (HabboItem i : items)
            {
                this.pickUpItem(i, null);
            }
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
                catch (NoSuchElementException e)
                {
                    Emulator.getLogging().logErrorLine(e);
                    break;
                }
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
                user.getHabboInventory().getItemsComponent().addItems(entrySet.getValue());
                user.getClient().sendResponse(new AddHabboItemComposer(entrySet.getValue()));
            }
        }
    }

    public void refreshGuild(Guild guild)
    {
        if(guild.getRoomId() == this.id)
        {
            THashMap<Integer, GuildMember> admins = Emulator.getGameEnvironment().getGuildManager().getOnlyAdmins(guild);

            for(Habbo habbo : this.currentHabbos.valueCollection())
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
            synchronized (this.roomItems)
            {
                TIntObjectIterator<HabboItem> iterator = this.roomItems.iterator();

                for (int i = this.roomItems.size(); i-- > 0; )
                {
                    try
                    {
                        iterator.advance();

                        HabboItem habboItem = iterator.value();

                        if (habboItem != null && habboItem instanceof InteractionGuildFurni)
                        {
                            if (((InteractionGuildFurni) habboItem).getGuildId() == guild.getId())
                                this.updateItem(habboItem);
                        }
                    }
                    catch (NoSuchElementException e)
                    {
                        Emulator.getLogging().logErrorLine(e);
                        break;
                    }
                }
            }
        }
    }

    public void refreshGuildRightsInRoom()
    {
        synchronized (this.currentHabbos)
        {
            for (Habbo habbo : this.currentHabbos.valueCollection())
            {
                if (habbo.getHabboInfo().getCurrentRoom() != this)
                    continue;

                if (habbo.getHabboInfo().getId() == this.ownerId)
                    return;

                if (habbo.hasPermission("acc_anyroomowner") || habbo.hasPermission("acc_moverotate"))
                    return;

                this.refreshRightsForHabbo(habbo);
            }
        }
    }

    public void unIdle(Habbo habbo)
    {
        habbo.getRoomUnit().resetIdleTimer();

        this.sendComposer(new RoomUnitIdleComposer(habbo.getRoomUnit()).compose());
    }

    public void addToWordFilter(String word)
    {
        synchronized (this.wordFilterWords)
        {
            if (this.wordFilterWords.contains(word))
                return;

            this.wordFilterWords.add(word);

            PreparedStatement statement = null;
            try
            {
                statement = Emulator.getDatabase().prepare("INSERT INTO room_wordfilter VALUES (?, ?)");
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            finally
            {
                if (statement != null)
                {
                    try
                    {
                        statement.close();
                        statement.getConnection().close();
                    } catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
    }

    public void removeFromWordFilter(String word)
    {
        synchronized (this.wordFilterWords)
        {
            this.wordFilterWords.remove(word);

            PreparedStatement statement = null;
            try
            {
                statement = Emulator.getDatabase().prepare("DELETE FROM room_wordfilter WHERE room_id = ? AND word = ?");
                statement.setInt(1, this.getId());
                statement.setString(2, word);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            finally
            {
                if (statement != null)
                {
                    try
                    {
                        statement.close();
                        statement.getConnection().close();
                    } catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
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

    private void loadWiredHighscoreData(WiredHighscoreScoreType scoreType, WiredHighscoreClearType clearType)
    {
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

            PreparedStatement statement = Emulator.getDatabase().prepare(query);
            statement.setInt(1, this.id);
            statement.setInt(2, timestamp);

            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                wiredData.add(new WiredHighscoreData(set.getString("usernames").split(","), set.getInt("score"), set.getInt("team_score"), set.getInt("total_score")));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>> dataMap = new THashMap<WiredHighscoreClearType, THashSet<WiredHighscoreData>>();
        dataMap.put(clearType, wiredData);
        this.wiredHighscoreData.put(scoreType, dataMap);
    }
}
