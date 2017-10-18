package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.RoomUserPetComposer;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.messenger.MessengerBuddy;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterComparator;
import com.eu.habbo.habbohotel.navigation.NavigatorFilterField;
import com.eu.habbo.habbohotel.navigation.NavigatorManager;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.PetData;
import com.eu.habbo.habbohotel.pets.PetTasks;
import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.polls.PollManager;
import com.eu.habbo.habbohotel.users.*;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.incoming.users.UserNuxEvent;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericErrorMessagesComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.polls.PollStartComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollAnswersComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.SimplePollStartComposer;
import com.eu.habbo.messages.outgoing.rooms.*;
import com.eu.habbo.messages.outgoing.rooms.items.RoomFloorItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.items.RoomWallItemsComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetComposer;
import com.eu.habbo.messages.outgoing.rooms.promotions.RoomPromotionMessageComposer;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.messages.outgoing.users.MutedWhisperComposer;
import com.eu.habbo.plugin.events.navigator.NavigatorRoomCreatedEvent;
import com.eu.habbo.plugin.events.rooms.RoomUncachedEvent;
import com.eu.habbo.plugin.events.users.UserEnterRoomEvent;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager
{
    //Configuration. Loaded from database & updated accordingly.
    public static int MAXIMUM_ROOMS_USER = 25;
    public static int MAXIMUM_ROOMS_VIP = 35;
    public static int HOME_ROOM_ID = 0;

    private final THashMap<Integer, RoomCategory> roomCategories;
    private final List<String> mapNames;
    private final ConcurrentHashMap<Integer, Room> activeRooms;

    public RoomManager()
    {
        long millis = System.currentTimeMillis();
        this.roomCategories = new THashMap<Integer, RoomCategory>();
        this.mapNames = new ArrayList<String>();
        this.activeRooms = new ConcurrentHashMap<Integer, Room>();
        this.loadRoomCategories();
        this.loadRoomModels();

        Emulator.getLogging().logStart("Room Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void loadRoomModels()
    {
        this.mapNames.clear();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM room_models"))
        {
            while(set.next())
            {
                this.mapNames.add(set.getString("name"));
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public CustomRoomLayout loadCustomLayout(Room room)
    {
        RoomLayout layout = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_models_custom WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, room.getId());
            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    layout = new CustomRoomLayout(set, room);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return (CustomRoomLayout) layout;
    }

    private void loadRoomCategories()
    {
        this.roomCategories.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM navigator_flatcats"))
        {
            while(set.next())
            {
                this.roomCategories.put(set.getInt("id"), new RoomCategory(set));
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void loadPublicRooms()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE is_public = ? OR is_staff_picked = ? ORDER BY id DESC"))
        {
            statement.setString(1, "1");
            statement.setString(2, "1");
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    Room room = new Room(set);
                    room.preventUncaching = true;
                    this.activeRooms.put(set.getInt("id"), room);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private static final int page = 0;
    public THashMap<Integer, List<Room>> findRooms(NavigatorFilterField filterField, String value, int category)
    {
        THashMap<Integer, List<Room>> rooms = new THashMap<Integer, List<Room>>();
        String query = filterField.databaseQuery + " AND rooms.state != 'invisible' " + (category >= 0 ? "AND rooms.category = '" + category + "'" : "") + "  ORDER BY rooms.users, rooms.id DESC LIMIT " + (page * NavigatorManager.MAXIMUM_RESULTS_PER_PAGE)  + "" + ((page * NavigatorManager.MAXIMUM_RESULTS_PER_PAGE) + NavigatorManager.MAXIMUM_RESULTS_PER_PAGE);
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, (filterField.comparator == NavigatorFilterComparator.EQUALS ? value : "%" + value + "%"));
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    Room room = this.activeRooms.get(set.getInt("id"));

                    if (room == null)
                    {
                        room = new Room(set);
                        this.activeRooms.put(set.getInt("id"), room);
                    }

                    if (!rooms.containsKey(set.getInt("category")))
                    {
                        rooms.put(set.getInt("category"), new ArrayList<Room>());
                    }

                    rooms.get(set.getInt("category")).add(room);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public RoomCategory getCategory(int id)
    {
        for (RoomCategory category : this.roomCategories.values())
        {
            if (category.getId() == id)
                return category;
        }

        return null;
    }

    public RoomCategory getCategory(String name)
    {
        for (RoomCategory category : this.roomCategories.values())
        {
            if (category.getCaption().equalsIgnoreCase(name))
            {
                return category;
            }
        }

        return null;
    }

    public RoomCategory getCategoryBySafeCaption(String safeCaption)
    {
        for (RoomCategory category : this.roomCategories.values())
        {
            if (category.getCaptionSave().equalsIgnoreCase(safeCaption))
            {
                return category;
            }
        }

        return null;
    }

    public List<RoomCategory> roomCategoriesForHabbo(Habbo habbo)
    {
        List<RoomCategory> categories = new ArrayList<RoomCategory>();
        for(RoomCategory category : this.roomCategories.values())
        {
            if(category.getMinRank() <= habbo.getHabboInfo().getRank().getId())
                categories.add(category);
        }

        Collections.sort(categories);

        return categories;
    }

    public boolean hasCategory(int categoryId, Habbo habbo)
    {
        for(RoomCategory category : this.roomCategories.values())
        {
            if(category.getId() == categoryId)
            {
                if(category.getMinRank() <= habbo.getHabboInfo().getRank().getId())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public THashMap<Integer, RoomCategory> getRoomCategories()
    {
        return this.roomCategories;
    }

    public List<Room> getRoomsByScore()
    {
        List<Room> rooms = new ArrayList<Room>();
        rooms.addAll(this.activeRooms.values());
        Collections.sort(rooms, Room.SORT_SCORE);

        return rooms;
    }

    public List<Room> getActiveRooms(int categoryId)
    {
        List<Room> rooms = new ArrayList<Room>();
        for (Room room : this.activeRooms.values())
        {
            if (categoryId == room.getCategory() || categoryId == -1)
                rooms.add(room);
        }
        Collections.sort(rooms);
        return rooms;
    }

    //TODO Move to HabboInfo class.
    public List<Room> getRoomsForHabbo(Habbo habbo)
    {
        List<Room> rooms = new ArrayList<Room>();
        for(Room room : this.activeRooms.values())
        {
            if(room.getOwnerId() == habbo.getHabboInfo().getId())
                rooms.add(room);
        }
        Collections.sort(rooms, Room.SORT_ID);
        return rooms;
    }

    public List<Room> getRoomsForHabbo(String username)
    {
        Habbo h = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);
        if(h != null)
        {
            return getRoomsForHabbo(h);
        }

        List<Room> rooms = new ArrayList<Room>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE owner_name = ? ORDER BY id DESC LIMIT 25"))
        {
            statement.setString(1, username);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    rooms.add(this.loadRoom(set.getInt("id")));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public Room loadRoom(int id)
    {
        Room room = null;

        if(this.activeRooms.containsKey(id))
        {
            room = this.activeRooms.get(id);

            if (room.isPreLoaded() && !room.isLoaded())
            {
                room.loadData();
            }

            return room;
        }

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, id);

            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    room = new Room(set);
                    room.loadData();
                }
            }

            if(room != null)
            {
                this.activeRooms.put(room.getId(), room);
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return room;
    }

    /**
     * Creates and preloads a room.
     * @param ownerId
     * @param ownerName
     * @param name
     * @param description
     * @param modelName
     * @param usersMax
     * @param categoryId
     * @return
     */
    public Room createRoom(int ownerId, String ownerName, String name, String description, String modelName, int usersMax, int categoryId)
    {
        Room room = null;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO rooms (owner_id, owner_name, name, description, model, users_max, category) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setInt(1, ownerId);
            statement.setString(2, ownerName);
            statement.setString(3, name);
            statement.setString(4, description);
            statement.setString(5, modelName);
            statement.setInt(6, usersMax);
            statement.setInt(7, categoryId);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                    room = this.loadRoom(set.getInt(1));
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return room;
    }

    /**
     * Creates a room for the given Habbo and also preloads the room.
     * @param habbo
     * @param name
     * @param description
     * @param modelName
     * @param usersMax
     * @param categoryId
     * @return
     */
    public Room createRoomForHabbo(Habbo habbo, String name, String description, String modelName, int usersMax, int categoryId)
    {
        Room room = this.createRoom(habbo.getHabboInfo().getId(), habbo.getHabboInfo().getUsername(), name, description, modelName, usersMax, categoryId);

        Emulator.getPluginManager().fireEvent(new NavigatorRoomCreatedEvent(habbo, room));

        return room;
    }

    public void loadRoomsForHabbo(Habbo habbo)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms WHERE owner_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (!this.activeRooms.containsKey(set.getInt("id")))
                        this.activeRooms.put(set.getInt("id"), new Room(set));
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void unloadRoomsForHabbo(Habbo habbo)
    {
        List<Room> roomsToDispose = new ArrayList<Room>();
        for(Room room : this.activeRooms.values())
        {
            if(!room.isPublicRoom() && !room.isStaffPromotedRoom() && room.getOwnerId() == habbo.getHabboInfo().getId() && room.getUserCount() == 0)
            {
                roomsToDispose.add(room);
            }
        }

        for(Room room : roomsToDispose)
        {
            if (Emulator.getPluginManager().fireEvent(new RoomUncachedEvent(room)).isCancelled())
                continue;

            this.activeRooms.remove(room.getId());
            room.dispose();
        }
    }

    public void clearInactiveRooms()
    {
        THashSet<Room> roomsToDispose = new THashSet<Room>();
        for(Room room : this.activeRooms.values())
        {
            if(!room.isPublicRoom() && !room.isStaffPromotedRoom() && !Emulator.getGameServer().getGameClientManager().containsHabbo(room.getOwnerId()) && room.isPreLoaded())
            {
                roomsToDispose.add(room);
            }
        }

        for(Room room : roomsToDispose)
        {
            if(room.getUserCount() == 0)
                this.activeRooms.remove(room.getId());
            room.dispose();
        }
    }

    public boolean layoutExists(String name)
    {
        return this.mapNames.contains(name);
    }

    public RoomLayout loadLayout(String name, Room room)
    {
        RoomLayout layout = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM room_models WHERE name LIKE ? LIMIT 1"))
        {
            statement.setString(1, name);
            try (ResultSet set = statement.executeQuery())
            {
                if (set.next())
                {
                    layout = new RoomLayout(set, room);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return layout;
    }

    public void unloadRoom(Room room)
    {
        room.dispose();
    }

    public void voteForRoom(Habbo habbo, Room room)
    {
        if(habbo.getHabboInfo().getCurrentRoom() != null && room != null && habbo.getHabboInfo().getCurrentRoom() == room)
        {
            if(this.hasVotedForRoom(habbo, room))
                return;

            room.setScore(room.getScore() + 1);
            room.setNeedsUpdate(true);
            habbo.getHabboStats().votedRooms.push(room.getId());
            for(Habbo h : room.getHabbos())
            {
                h.getClient().sendResponse(new RoomScoreComposer(room.getScore(), !this.hasVotedForRoom(h, room)));
            }

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_votes VALUES (?, ?)"))
            {
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, room.getId());
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    boolean hasVotedForRoom(Habbo habbo, Room room)
    {
        if(room.getOwnerId() == habbo.getHabboInfo().getId())
            return true;

        for(int i : habbo.getHabboStats().votedRooms.toArray())
        {
            if(i == room.getId())
                return true;
        }

        return false;
    }

    public Room getRoom(int roomId)
    {
        return this.activeRooms.get(roomId);
    }

    public ArrayList<Room> getActiveRooms()
    {
        return new ArrayList<Room>(this.activeRooms.values());
    }

    public int loadedRoomsCount()
    {
        return this.activeRooms.size();
    }

    public void enterRoom(Habbo habbo, int roomId, String password)
    {
        this.enterRoom(habbo, roomId, password, false, null);
    }

    public void enterRoom(Habbo habbo, int roomId, String password, boolean overrideChecks)
    {
        this.enterRoom(habbo, roomId, password, overrideChecks, null);
    }

    public void enterRoom(Habbo habbo, int roomId, String password, boolean overrideChecks, RoomTile doorLocation)
    {
        Room room = loadRoom(roomId);

        if(room == null)
            return;

        if (habbo.getHabboInfo().getLoadingRoom() != 0 && room.getId() != habbo.getHabboInfo().getLoadingRoom())
        {
            habbo.getClient().sendResponse(new HotelViewComposer());
            habbo.getHabboInfo().setLoadingRoom(0);
            return;
        }

        if(Emulator.getPluginManager().fireEvent(new UserEnterRoomEvent(habbo, room)).isCancelled())
        {
            if(habbo.getHabboInfo().getCurrentRoom() == null)
            {
                habbo.getClient().sendResponse(new HotelViewComposer());
                habbo.getHabboInfo().setLoadingRoom(0);
                return;
            }
        }

        if (room.isBanned(habbo) && !habbo.hasPermission("acc_anyroomowner") && !habbo.hasPermission("acc_enteranyroom"))
        {
            habbo.getClient().sendResponse(new RoomEnterErrorComposer(RoomEnterErrorComposer.ROOM_ERROR_BANNED));
            return;
        }

        if (habbo.getHabboInfo().getRoomQueueId() != roomId)
        {
            Room queRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

            if (queRoom != null)
            {
                queRoom.removeFromQueue(habbo);
            }
        }

        if(overrideChecks ||
           room.isOwner(habbo) ||
           room.getState() == RoomState.OPEN ||
           room.getState() == RoomState.INVISIBLE ||
           habbo.hasPermission("acc_anyroomowner") ||
           habbo.hasPermission("acc_enteranyroom") ||
           room.hasRights(habbo) ||
           (room.hasGuild() && room.guildRightLevel(habbo) > 2))
        {
            this.openRoom(habbo, room, doorLocation);
        }
        else if(room.getState() == RoomState.LOCKED)
        {
            boolean rightsFound = false;

            synchronized (room.roomUnitLock)
            {
                for (Habbo current : room.getHabbos())
                {
                    if (room.hasRights(current) || current.getHabboInfo().getId() == room.getOwnerId() || (room.hasGuild() && room.guildRightLevel(current) >= 2))
                    {
                        current.getClient().sendResponse(new DoorbellAddUserComposer(habbo.getHabboInfo().getUsername()));
                        rightsFound = true;
                    }
                }
            }

            if(!rightsFound)
            {
                habbo.getClient().sendResponse(new RoomAccessDeniedComposer(""));
                habbo.getClient().sendResponse(new HotelViewComposer());
                habbo.getHabboInfo().setLoadingRoom(0);
                return;
            }

            habbo.getHabboInfo().setRoomQueueId(roomId);
            habbo.getClient().sendResponse(new DoorbellAddUserComposer(""));
            room.addToQueue(habbo);
        }
        else if(room.getState() == RoomState.PASSWORD)
        {
            if(room.getPassword().equalsIgnoreCase(password))
                this.openRoom(habbo, room, doorLocation);
            else
            {
                habbo.getClient().sendResponse(new GenericErrorMessagesComposer(-100002));
                habbo.getClient().sendResponse(new HotelViewComposer());
                habbo.getHabboInfo().setLoadingRoom(0);
            }
        }
    }

    void openRoom(Habbo habbo, Room room, RoomTile doorLocation)
    {
        if (room == null)
            return;

        if (Emulator.getConfig().getBoolean("hotel.room.enter.logs"))
        {
            this.logEnter(habbo, room);
        }

        if (habbo.getHabboInfo().getRoomQueueId() > 0)
        {
            Room r = Emulator.getGameEnvironment().getRoomManager().getRoom(habbo.getHabboInfo().getRoomQueueId());

            if (r != null)
            {
                r.removeFromQueue(habbo);
            }
        }
        habbo.getHabboInfo().setRoomQueueId(0);
        habbo.getClient().sendResponse(new HideDoorbellComposer(""));

        if (habbo.getRoomUnit() == null)
            habbo.setRoomUnit(new RoomUnit());

        if (habbo.getRoomUnit().getCurrentLocation() == null)
        {
            habbo.getRoomUnit().setLocation(room.getLayout().getDoorTile());
        }
        habbo.getRoomUnit().setRoomUnitType(RoomUnitType.USER);
        if(room.isBanned(habbo))
        {
            habbo.getClient().sendResponse(new RoomEnterErrorComposer(RoomEnterErrorComposer.ROOM_ERROR_BANNED));
            return;
        }

        if (room.getUserCount() >= room.getUsersMax() && !habbo.hasPermission("acc_fullrooms") && !room.hasRights(habbo))
        {
            habbo.getClient().sendResponse(new RoomEnterErrorComposer(RoomEnterErrorComposer.ROOM_ERROR_GUESTROOM_FULL));
            return;
        }

        habbo.getRoomUnit().getStatus().clear();
        habbo.getRoomUnit().cmdTeleport = false;

        habbo.getClient().sendResponse(new RoomOpenComposer());

        habbo.getRoomUnit().setInRoom(true);
        if (habbo.getHabboInfo().getCurrentRoom() != room && habbo.getHabboInfo().getCurrentRoom() != null)
        {
            habbo.getHabboInfo().getCurrentRoom().removeHabbo(habbo);
        } else if (!habbo.getHabboStats().blockFollowing && habbo.getHabboInfo().getCurrentRoom() == null)
        {
            habbo.getMessenger().connectionChanged(habbo, true, true);
        }

        if (habbo.getHabboInfo().getLoadingRoom() != 0)
        {
            Room oldRoom = Emulator.getGameEnvironment().getRoomManager().getRoom(habbo.getHabboInfo().getLoadingRoom());
            if (oldRoom != null)
            {
                oldRoom.removeFromQueue(habbo);
            }
        }

        habbo.getHabboInfo().setLoadingRoom(room.getId());

        if (habbo.getRoomUnit().isTeleporting)
        {
            habbo.getRoomUnit().setLocation(doorLocation);
        }

        habbo.getClient().sendResponse(new RoomModelComposer(room));

        if (!room.getWallPaint().equals("0.0"))
            habbo.getClient().sendResponse(new RoomPaintComposer("wallpaper", room.getWallPaint()));

        if (!room.getFloorPaint().equals("0.0"))
            habbo.getClient().sendResponse(new RoomPaintComposer("floor", room.getFloorPaint()));

        habbo.getClient().sendResponse(new RoomPaintComposer("landscape", room.getBackgroundPaint()));

        room.refreshRightsForHabbo(habbo);

        habbo.getClient().sendResponse(new RoomScoreComposer(room.getScore(), !this.hasVotedForRoom(habbo, room)));

        if (room.isPromoted())
        {
            habbo.getClient().sendResponse(new RoomPromotionMessageComposer(room, room.getPromotion()));
        }
        else
        {
            habbo.getClient().sendResponse(new RoomPromotionMessageComposer(null, null));
        }

        if(room.getOwnerId() != habbo.getHabboInfo().getId())
        {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomEntry"));
        }
    }

    public void enterRoom(final Habbo habbo, final Room room)
    {
        if (habbo.getHabboInfo().getLoadingRoom() != room.getId())
        {
            if (habbo.getHabboInfo().getLoadingRoom() != 0)
            {
                habbo.getClient().sendResponse(new HotelViewComposer());
            }
            return;
        }

        habbo.getHabboInfo().setLoadingRoom(0);
        habbo.getHabboInfo().setCurrentRoom(room);
        habbo.getRoomUnit().setPathFinderRoom(room);
        habbo.getRoomUnit().setHandItem(0);

        habbo.getRoomUnit().setRightsLevel(RoomRightLevels.NONE);
        room.refreshRightsForHabbo(habbo);
        if (habbo.getRoomUnit().isKicked && !habbo.getRoomUnit().canWalk())
        {
            habbo.getRoomUnit().setCanWalk(true);
        }
        habbo.getRoomUnit().isKicked = false;

        if (!habbo.getRoomUnit().isTeleporting)
        {
            habbo.getRoomUnit().setLocation(room.getLayout().getTile(room.getLayout().getDoorX(), room.getLayout().getDoorY()));
            RoomTile doorTile = room.getLayout().getTile(room.getLayout().getDoorX(), room.getLayout().getDoorY());

            if (doorTile != null)
            {
                habbo.getRoomUnit().setZ(doorTile.getStackHeight());
            }

            habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
            habbo.getRoomUnit().setHeadRotation(RoomUserRotation.values()[room.getLayout().getDoorDirection()]);
        }
        habbo.getRoomUnit().setPathFinderRoom(room);
        habbo.getRoomUnit().resetIdleTimer();

        room.addHabbo(habbo);
        if (!room.getCurrentHabbos().isEmpty())
        {
            // ServerMessage m = new RoomUsersComposer(habbo).compose().appendResponse(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());

            room.sendComposer(new RoomUsersComposer(habbo).compose());
            room.sendComposer(new RoomUserStatusComposer(habbo.getRoomUnit()).compose());


            synchronized (room.roomUnitLock)
            {
                habbo.getClient().sendResponse(new RoomUsersComposer(room.getHabbos()));
                habbo.getClient().sendResponse(new RoomUserStatusComposer(room.getHabbos()));
            }

            if (habbo.getHabboStats().guild != 0)
            {
                Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(habbo.getHabboStats().guild);

                if (guild != null)
                {
                    room.sendComposer(new RoomUsersAddGuildBadgeComposer(guild).compose());
                }
            }

            int effect = habbo.getInventory().getEffectsComponent().activatedEffect ;

            if (effect == 0)
            {
                effect = habbo.getHabboInfo().getRank().getRoomEffect();
            }

            if (effect > 0)
            {
                habbo.getRoomUnit().setEffectId(effect);
            }

            room.sendComposer(new RoomUserEffectComposer(habbo.getRoomUnit()).compose());
        }


        habbo.getClient().sendResponse(new RoomUsersComposer(room.getCurrentBots().valueCollection(), true));
        if (!room.getCurrentBots().isEmpty())
        {
            TIntObjectIterator<Bot> botIterator = room.getCurrentBots().iterator();
            for (int i = room.getCurrentBots().size(); i-- > 0; )
            {
                try
                {
                    botIterator.advance();
                }
                catch (NoSuchElementException e)
                {
                    break;
                }
                Bot bot = botIterator.value();
                if (!bot.getRoomUnit().getDanceType().equals(DanceType.NONE))
                {
                    habbo.getClient().sendResponse(new RoomUserDanceComposer(bot.getRoomUnit()));
                }

                habbo.getClient().sendResponse(new RoomUserStatusComposer(bot.getRoomUnit()));
            }
        }

        habbo.getClient().sendResponse(new RoomPaneComposer(room, room.isOwner(habbo)));

        habbo.getClient().sendResponse(new RoomThicknessComposer(room));

        habbo.getClient().sendResponse(new RoomDataComposer(room, habbo.getClient().getHabbo(), false, true));

        habbo.getClient().sendResponse(new RoomWallItemsComposer(room));

        {
            final THashSet<HabboItem> floorItems = new THashSet<HabboItem>();

            room.getFloorItems().forEach(new TObjectProcedure<HabboItem>()
            {
                @Override
                public boolean execute(HabboItem object)
                {
                    floorItems.add(object);
                    if (floorItems.size() == 250)
                    {
                        habbo.getClient().sendResponse(new RoomFloorItemsComposer(room.getFurniOwnerNames(), floorItems));
                        floorItems.clear();
                    }

                    return true;
                }
            });

            habbo.getClient().sendResponse(new RoomFloorItemsComposer(room.getFurniOwnerNames(), floorItems));
            floorItems.clear();
        }

        if (!room.getCurrentPets().isEmpty())
        {
            habbo.getClient().sendResponse(new RoomPetComposer(room.getCurrentPets()));
            for (AbstractPet pet : room.getCurrentPets().valueCollection())
            {
                habbo.getClient().sendResponse(new RoomUserStatusComposer(pet.getRoomUnit()));
            }
        }

        if (!habbo.getHabboStats().allowTalk())
        {
            habbo.getHabboStats().mutedBubbleTracker = true;
            int remainingMuteTime = habbo.getHabboStats().remainingMuteTime();
            habbo.getClient().sendResponse(new FloodCounterComposer(remainingMuteTime));
            habbo.getClient().sendResponse(new MutedWhisperComposer(remainingMuteTime));
            room.sendComposer(new RoomUserIgnoredComposer(habbo, RoomUserIgnoredComposer.MUTED).compose());
        }
        else if (habbo.getHabboStats().mutedBubbleTracker)
        {
            habbo.getHabboStats().mutedBubbleTracker = false;
        }

        THashMap<Integer, String> guildBadges = new THashMap<Integer, String>();
        for (Habbo roomHabbo : room.getHabbos())
        {
            {
                if (roomHabbo.getRoomUnit().getDanceType().getType() > 0)
                {
                    habbo.getClient().sendResponse(new RoomUserDanceComposer(roomHabbo.getRoomUnit()));
                }

                if (roomHabbo.getRoomUnit().getHandItem() > 0)
                {
                    habbo.getClient().sendResponse(new RoomUserHandItemComposer(roomHabbo.getRoomUnit()));
                }

                if (roomHabbo.getRoomUnit().getEffectId() > 0)
                {
                    habbo.getClient().sendResponse(new RoomUserEffectComposer(roomHabbo.getRoomUnit()));
                }

                if (roomHabbo.getRoomUnit().isIdle())
                {
                    habbo.getClient().sendResponse(new RoomUnitIdleComposer(roomHabbo.getRoomUnit()));
                }

                if (roomHabbo.getHabboStats().ignoredUsers.contains(habbo.getHabboInfo().getId()))
                {
                    roomHabbo.getClient().sendResponse(new RoomUserIgnoredComposer(habbo, RoomUserIgnoredComposer.IGNORED));
                }

                if (!roomHabbo.getHabboStats().allowTalk())
                {
                    habbo.getClient().sendResponse(new RoomUserIgnoredComposer(roomHabbo, RoomUserIgnoredComposer.MUTED));
                }
                else if (habbo.getHabboStats().ignoredUsers.contains(roomHabbo.getHabboInfo().getId()))
                {
                    habbo.getClient().sendResponse(new RoomUserIgnoredComposer(roomHabbo, RoomUserIgnoredComposer.IGNORED));
                }

                if (roomHabbo.getHabboStats().guild != 0 && !guildBadges.containsKey(roomHabbo.getHabboStats().guild))
                {
                    Guild guild = Emulator.getGameEnvironment().getGuildManager().getGuild(roomHabbo.getHabboStats().guild);

                    if (guild != null)
                    {
                        guildBadges.put(roomHabbo.getHabboStats().guild, guild.getBadge());
                    }
                }

                if (roomHabbo.getRoomUnit().getRoomUnitType().equals(RoomUnitType.PET))
                {
                    try
                    {
                        habbo.getClient().sendResponse(new RoomUserRemoveComposer(roomHabbo.getRoomUnit()));
                        habbo.getClient().sendResponse(new RoomUserPetComposer(((PetData)roomHabbo.getHabboStats().cache.get("pet_type")).getType(), (Integer)roomHabbo.getHabboStats().cache.get("pet_race"), (String)roomHabbo.getHabboStats().cache.get("pet_color"), roomHabbo));
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }

        habbo.getClient().sendResponse(new RoomUsersGuildBadgesComposer(guildBadges));

        if (room.hasRights(habbo) || (room.hasGuild() && room.guildRightLevel(habbo) >= 2))
        {
            if (!room.getHabboQueue().isEmpty())
            {
                for (Habbo waiting : room.getHabboQueue().valueCollection())
                {
                    habbo.getClient().sendResponse(new DoorbellAddUserComposer(waiting.getHabboInfo().getUsername()));
                }
            }
        }

        if (room.getPollId() > 0)
        {
            if (!PollManager.donePoll(habbo.getClient().getHabbo(), room.getPollId()))
            {
                Poll poll = Emulator.getGameEnvironment().getPollManager().getPoll(room.getPollId());

                if (poll != null)
                {
                    habbo.getClient().sendResponse(new PollStartComposer(poll));
                }
            }
        }

        if (room.hasActiveWordQuiz())
        {
            habbo.getClient().sendResponse(new SimplePollStartComposer((Emulator.getIntUnixTimestamp() - room.wordQuizEnd) * 1000, room.wordQuiz));

            if (room.hasVotedInWordQuiz(habbo))
            {
                habbo.getClient().sendResponse(new SimplePollAnswersComposer(habbo.getHabboInfo().getId(), room.noVotes, room.yesVotes));
            }
        }

        WiredHandler.handle(WiredTriggerType.ENTER_ROOM, habbo.getRoomUnit(), room, null);
        room.habboEntered(habbo);

        if (!habbo.getHabboStats().nux && room.isOwner(habbo))
        {
            UserNuxEvent.handle(habbo);
        }
    }

    void logEnter(Habbo habbo, Room room)
    {
        habbo.getHabboStats().roomEnterTimestamp = Emulator.getIntUnixTimestamp();
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_enter_log (room_id, user_id, timestamp) VALUES(?, ?, ?)"))
        {
            statement.setInt(1, room.getId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, (int)(habbo.getHabboStats().roomEnterTimestamp));
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void leaveRoom(Habbo habbo, Room room)
    {
        if(habbo.getHabboInfo().getCurrentRoom() != null && habbo.getHabboInfo().getCurrentRoom() == room)
        {
            habbo.getRoomUnit().setPathFinderRoom(null);
            if (habbo.getHabboInfo().getRiding() != null)
            {
                if (habbo.getHabboInfo().getRiding().getRoomUnit() != null)
                {
                    habbo.getHabboInfo().getRiding().getRoomUnit().setGoalLocation(habbo.getHabboInfo().getRiding().getRoomUnit().getCurrentLocation());
                }
                habbo.getHabboInfo().getRiding().setTask(PetTasks.FREE);
                habbo.getHabboInfo().getRiding().setRider(null);
                habbo.getHabboInfo().setRiding(null);
            }

            if (!room.isOwner(habbo))
            {
                room.pickupPetsForHabbo(habbo);
            }
            this.logExit(habbo);
            room.removeHabbo(habbo);
            room.sendComposer(new RoomUserRemoveComposer(habbo.getRoomUnit()).compose());
            habbo.getClient().sendResponse(new HotelViewComposer());
            habbo.getHabboInfo().setCurrentRoom(null);
            habbo.getRoomUnit().isKicked = false;

            if (room.getOwnerId() != habbo.getHabboInfo().getId())
            {
                AchievementManager.progressAchievement(room.getOwnerId(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("RoomDecoHosting"), (int)Math.floor((Emulator.getIntUnixTimestamp() - habbo.getHabboStats().roomEnterTimestamp) / 60000));
            }
        }
    }
    public void logExit(Habbo habbo)
    {
        if(habbo.getRoomUnit().getCacheable().containsKey("control"))
        {
            Habbo control = (Habbo)habbo.getRoomUnit().getCacheable().remove("control");
            control.getRoomUnit().getCacheable().remove("controller");
        }

        Room room = habbo.getHabboInfo().getCurrentRoom();
        if(room != null)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE room_enter_log SET exit_timestamp = ? WHERE user_id = ? AND room_id = ? ORDER BY timestamp DESC LIMIT 1"))
            {
                statement.setInt(1, Emulator.getIntUnixTimestamp());
                statement.setInt(2, habbo.getHabboInfo().getId());
                statement.setInt(3, room.getId());
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public Set<String> getTags()
    {
        Map<String, Integer> tagCount = new HashMap<String, Integer>();

        for(Room room : this.activeRooms.values())
        {
            for(String s : room.getTags().split(";"))
            {
                int i = 0;
                if(tagCount.get(s) != null)
                    i++;

                tagCount.put(s, i++);
            }
        }
        return new TreeMap<String, Integer>(tagCount).keySet();
    }

    public ArrayList<Room> getPublicRooms()
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for(Room room : this.activeRooms.values())
        {
            if(room.isPublicRoom() || room.isStaffPromotedRoom() || this.roomCategories.get(room.getCategory()).isPublic())
            {
                rooms.add(room);
            }
        }

        return rooms;
    }

    public ArrayList<Room> getPopularRooms(int count)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for (Room room : this.activeRooms.values())
        {
            if (!room.isPublicRoom() && room.getUserCount() > 0)
            {
                rooms.add(room);
            }
        }

        if (rooms.isEmpty())
        {
            return rooms;
        }

        Collections.sort(rooms);

        return new ArrayList<Room>(rooms.subList(0, (rooms.size() < count ? rooms.size() : count)));
    }

    public ArrayList<Room> getPopularRooms(int count, int category)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for (Room room : this.activeRooms.values())
        {
            if (!room.isPublicRoom() && room.getCategory() == category)
            {
                rooms.add(room);
            }
        }

        if (rooms.isEmpty())
        {
            return rooms;
        }

        Collections.sort(rooms);

        return new ArrayList<Room>(rooms.subList(0, (rooms.size() < count ? rooms.size() : count)));
    }

    public Map<Integer, List<Room>> getPopularRoomsByCategory(int count)
    {
        Map<Integer, List<Room>> rooms = new HashMap<Integer, List<Room>>();

        for (Room room : this.activeRooms.values())
        {
            if (!room.isPublicRoom())
            {
                if (!rooms.containsKey(room.getCategory()))
                {
                    rooms.put(room.getCategory(), new ArrayList<Room>());
                }

                rooms.get(room.getCategory()).add(room);
            }
        }

        Map<Integer, List<Room>> result = new HashMap<Integer, List<Room>>();

        for (Map.Entry<Integer, List<Room>> set : rooms.entrySet())
        {
            if (set.getValue().isEmpty())
                continue;

            Collections.sort(set.getValue());

            result.put(set.getKey(), new ArrayList<Room>(set.getValue().subList(0, (set.getValue().size() < count ? set.getValue().size() : count))));
        }

        return result;
    }

    public ArrayList<Room> getRoomsWithName(String name)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for (Room room : this.activeRooms.values())
        {
            if (room.getName().toLowerCase().contains(name.toLowerCase()))
            {
                rooms.add(room);
            }
        }

        if(rooms.size() < 25)
        {
            rooms.addAll(getOfflineRoomsWithName(name));
        }

        Collections.sort(rooms);

        return rooms;
    }

    private ArrayList<Room> getOfflineRoomsWithName(String name)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, rooms.* FROM rooms INNER JOIN users ON owner_id = users.id WHERE name LIKE ? ORDER BY id DESC LIMIT 25"))
        {
            statement.setString(1, "%"+name+"%");
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                        continue;

                    Room r = new Room(set);
                    rooms.add(r);
                    this.activeRooms.put(r.getId(), r);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public ArrayList<Room> getRoomsWithTag(String tag)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for (Room room : this.activeRooms.values())
        {
            for (String s : room.getTags().split(";"))
            {
                if (s.toLowerCase().equals(tag.toLowerCase()))
                {
                    rooms.add(room);
                    break;
                }
            }
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getGroupRoomsWithName(String name)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for (Room room : this.activeRooms.values())
        {
            if (room.getGuildId() == 0)
                continue;

            if (room.getName().toLowerCase().contains(name.toLowerCase()))
                rooms.add(room);
        }

        if(rooms.size() < 25)
        {
            rooms.addAll(this.getOfflineGroupRoomsWithName(name));
        }

        Collections.sort(rooms);

        return rooms;
    }

    private ArrayList<Room> getOfflineGroupRoomsWithName(String name)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT users.username AS owner_name, rooms.* FROM rooms INNER JOIN users ON rooms.owner_id = users.id WHERE name LIKE ? AND guild_id != 0 ORDER BY id DESC LIMIT 25"))
        {
            statement.setString(1, "%"+name+"%");
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                        continue;

                    Room r = new Room(set);
                    rooms.add(r);

                    this.activeRooms.put(r.getId(), r);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public ArrayList<Room> getRoomsFriendsNow(Habbo habbo)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for(MessengerBuddy buddy : habbo.getMessenger().getFriends().values())
        {
            if(buddy.getOnline() == 0)
                continue;

            Habbo friend = Emulator.getGameEnvironment().getHabboManager().getHabbo(buddy.getId());

            if(friend == null || friend.getHabboInfo().getCurrentRoom() == null)
                continue;

            rooms.add(friend.getHabboInfo().getCurrentRoom());
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getRoomsFriendsOwn(Habbo habbo)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        for(MessengerBuddy buddy : habbo.getMessenger().getFriends().values())
        {
            if(buddy.getOnline() == 0)
                continue;

            Habbo friend = Emulator.getGameEnvironment().getHabboManager().getHabbo(buddy.getId());

            if(friend == null)
                continue;

            rooms.addAll(this.getRoomsForHabbo(friend));
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getRoomsVisited(Habbo habbo, boolean includeSelf, int limit)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM room_enter_log INNER JOIN rooms ON room_enter_log.room_id = rooms.id WHERE user_id = ? AND timestamp >= ? AND rooms.owner_id != ? GROUP BY rooms.id ORDER BY timestamp DESC LIMIT " + limit))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, Emulator.getIntUnixTimestamp() - 259200);
            statement.setInt(3, (includeSelf ? 0 : habbo.getHabboInfo().getId()));
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    Room room = this.activeRooms.get(set.getInt("id"));

                    if (room == null)
                    {
                        room = new Room(set);

                        this.activeRooms.put(room.getId(), room);
                    }

                    rooms.add(room);
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        Collections.sort(rooms);

        return rooms;
    }

    public ArrayList<Room> getRoomsFavourite(Habbo habbo)
    {
        final ArrayList<Room> rooms = new ArrayList<Room>();

        habbo.getHabboStats().getFavoriteRooms().forEach(new TIntProcedure()
        {
            @Override
            public boolean execute(int value)
            {
                Room room = loadRoom(value);

                if (room != null)
                {
                    rooms.add(room);
                }
                return true;
            }
        });

        return rooms;
    }

    public List<Room> getGroupRooms(Habbo habbo, int limit)
    {
        final ArrayList<Room> rooms = new ArrayList<Room>();

        for (Guild guild : Emulator.getGameEnvironment().getGuildManager().getGuilds(habbo.getHabboInfo().getId()))
        {
            if (guild.getOwnerId() != habbo.getHabboInfo().getId())
            {
                Room room = this.loadRoom(guild.getRoomId());

                if (room != null)
                {
                    rooms.add(room);
                }
            }
        }

        Collections.sort(rooms);

        return rooms.subList(0, (rooms.size() > limit ? limit : rooms.size()));
    }

    public ArrayList<Room> getRoomsWithRights(Habbo habbo)
    {
        ArrayList<Room> rooms = new ArrayList<Room>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT rooms.* FROM rooms INNER JOIN room_rights ON room_rights.room_id = rooms.id WHERE room_rights.user_id = ? ORDER BY rooms.id DESC LIMIT 30"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                    {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    }
                    else
                    {
                        rooms.add(new Room(set));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public ArrayList<Room> getRoomsWithAdminRights(Habbo habbo)
    {
        ArrayList<Room> rooms = new ArrayList<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM rooms INNER JOIN guilds_members ON guilds_members.guild_id = rooms.guild_id WHERE guilds_members.user_id = ? AND level_id = 0"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    if (this.activeRooms.containsKey(set.getInt("id")))
                    {
                        rooms.add(this.activeRooms.get(set.getInt("id")));
                    }
                    else
                    {
                        rooms.add(new Room(set));
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return rooms;
    }

    public ArrayList<Room> getRoomsInGroup(Habbo habbo)
    {
        return new ArrayList<Room>();
    }

    public ArrayList<Room> getRoomsPromoted()
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : this.getActiveRooms())
        {
            if(room.isPromoted())
            {
                r.add(room);
            }
        }

        return r;
    }

    public List<Room> filterRoomsByOwner(List<Room> rooms, String filter)
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : rooms)
        {
            if(room.getOwnerName().equalsIgnoreCase(filter))
                r.add(room);
        }

        return r;
    }

    public List<Room> filterRoomsByName(List<Room> rooms, String filter)
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : rooms)
        {
            if(room.getName().toLowerCase().contains(filter.toLowerCase()))
                r.add(room);
        }

        return r;
    }

    public List<Room> filterRoomsByNameAndDescription(List<Room> rooms, String filter)
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : rooms)
        {
            if(room.getName().toLowerCase().contains(filter.toLowerCase()) || room.getDescription().toLowerCase().contains(filter.toLowerCase()))
                r.add(room);
        }

        return r;
    }

    public List<Room> filterRoomsByTag(List<Room> rooms, String filter)
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : rooms)
        {
            if(room.getTags().split(";").length == 0)
                continue;

            for(String s : room.getTags().split(";"))
            {
                if(s.equalsIgnoreCase(filter))
                    r.add(room);
            }
        }

        return r;
    }

    public List<Room> filterRoomsByGroup(List<Room> rooms, String filter)
    {
        ArrayList<Room> r = new ArrayList<Room>();

        for(Room room : rooms)
        {
            if(room.getGuildId() == 0)
                continue;

            if(Emulator.getGameEnvironment().getGuildManager().getGuild(room.getGuildId()).getName().toLowerCase().contains(filter.toLowerCase()))
                r.add(room);
        }

        return r;
    }

    public synchronized void dispose()
    {
        for (Room room : this.activeRooms.values())
        {
            room.dispose();
        }

        this.activeRooms.clear();

        Emulator.getLogging().logShutdownLine("Room Manager -> Disposed!");
    }

    public CustomRoomLayout insertCustomLayout(Room room, String map, int doorX, int doorY, int doorDirection)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO room_models_custom (id, name, door_x, door_y, door_dir, heightmap) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE door_x = ?, door_y = ?, door_dir = ?, heightmap = ?"))
        {
            statement.setInt(1, room.getId());
            statement.setString(2, "custom_" + room.getId());
            statement.setInt(3, doorX);
            statement.setInt(4, doorY);
            statement.setInt(5, doorDirection);
            statement.setString(6, map);
            statement.setInt(7, doorX);
            statement.setInt(8, doorY);
            statement.setInt(9, doorDirection);
            statement.setString(10, map);
            statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
            e.printStackTrace();
        }

        return this.loadCustomLayout(room);
    }

    public void banUserFromRoom(Habbo rights, int userId, int roomId, RoomBanTypes length)
    {
        Room room = this.getRoom(roomId);

        if (room == null)
            return;

        if (rights != null && !room.hasRights(rights))
            return;

        String name = "";

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);
        if (habbo != null)
        {
            if (habbo.hasPermission("acc_unkickable"))
            {
                return;
            }

            name = habbo.getHabboInfo().getUsername();
        }
        else
        {
            HabboInfo info = HabboManager.getOfflineHabboInfo(userId);

            if (info != null)
            {
                if (info.getRank().hasPermission("acc_unkickable", false))
                {
                    return;
                }
                name = info.getUsername();
            }
        }

        if (name.isEmpty())
        {
            return;
        }

        RoomBan roomBan = new RoomBan(roomId, userId, name, Emulator.getIntUnixTimestamp() + length.duration);
        roomBan.insert();

        room.addRoomBan(roomBan);

        if (habbo != null)
        {
            if (habbo.getHabboInfo().getCurrentRoom() == room)
            {
                room.removeHabbo(habbo);
                habbo.getClient().sendResponse(new RoomEnterErrorComposer(RoomEnterErrorComposer.ROOM_ERROR_BANNED));
            }
        }
    }

    public enum RoomBanTypes
    {
        RWUAM_BAN_USER_HOUR(60 * 60),
        RWUAM_BAN_USER_DAY(24 * 60 * 60),
        RWUAM_BAN_USER_PERM(10 * 365 * 24 * 60 * 60);

        public int duration;

        RoomBanTypes(int duration)
        {
            this.duration = duration;
        }
    }
}