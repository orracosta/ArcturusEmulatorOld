package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BotErrorComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import com.eu.habbo.messages.outgoing.inventory.AddBotComposer;
import com.eu.habbo.messages.outgoing.inventory.RemoveBotComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUsersComposer;
import com.eu.habbo.plugin.events.bots.BotPickUpEvent;
import com.eu.habbo.plugin.events.bots.BotPlacedEvent;
import gnu.trove.map.hash.THashMap;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;

public class BotManager
{
    //Configuration. Loaded from database & updated accordingly.
    public static int MINIMUM_CHAT_SPEED = 7;

    final private static THashMap<String, Class<? extends Bot>> botDefenitions = new THashMap<String, Class<? extends Bot>>();

    /**
     * Loads up the BotManager. Do NOT initialise this class yourself.
     *
     * Extend from the Bot class and implement 'public static void initialise()' to load data as you see fit.
     */
    public BotManager()
    {
        long millis = System.currentTimeMillis();

        botDefenitions.put("generic", Bot.class);
        botDefenitions.put("bartender", ButlerBot.class);
        botDefenitions.put("visitor_log", VisitorBot.class);

        this.reload();

        Emulator.getLogging().logStart("Bot Manager -> Loaded! ("+(System.currentTimeMillis() - millis)+" MS)");
    }

    /**
     * Reloads the bot manager
     * @return Returns true if the BotManager has been succesfully reloaded.
     */
    public boolean reload()
    {
        for(Map.Entry<String, Class<? extends Bot>> set : botDefenitions.entrySet())
        {
            try
            {
                Method m = set.getValue().getMethod("initialise");
                m.setAccessible(true);
                m.invoke(null);
            }
            catch (NoSuchMethodException e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute initialise method upon bot type '" + set.getKey() + "'. No Such Method!");
                return false;
            }
            catch (Exception e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute initialise method upon bot type '" + set.getKey() + "'. Error: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new Bot and inserts it into the database.
     * @param data A key-value set of details of the bot (name, motto, figure, gender)
     * @param type The type of the bot that must be initialised.
     * @return The initialised bot. Returns NULL upon Exception;
     */
    public Bot createBot(THashMap<String, String> data, String type)
    {
        Bot bot = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO bots (user_id, room_id, name, motto, figure, gender, type) VALUES (0, 0, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS))
        {
            statement.setString(1, data.get("name"));
            statement.setString(2, data.get("motto"));
            statement.setString(3, data.get("figure"));
            statement.setString(4, data.get("gender").toUpperCase());
            statement.setString(5, type);
            statement.execute();
            try (ResultSet set = statement.getGeneratedKeys())
            {
                if (set.next())
                {
                    try (PreparedStatement stmt = connection.prepareStatement("SELECT users.username AS owner_name, bots.* FROM bots LEFT JOIN users ON bots.user_id = users.id WHERE bots.id = ? LIMIT 1"))
                    {
                        stmt.setInt(1, set.getInt(1));
                        try (ResultSet resultSet = stmt.executeQuery())
                        {
                            if (resultSet.next())
                            {
                                bot = this.loadBot(resultSet);
                            }
                        }
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return bot;
    }

    /**
     * Places a bot at the given location in the given room.
     * @param bot The Bot that is being placed.
     * @param habbo The Habbo that owns the Bot.
     * @param room The Room this Bot is being placed in.
     * @param location The given location of the Bot.
     */
    public void placeBot(Bot bot, Habbo habbo, Room room, RoomTile location)
    {
        BotPlacedEvent event = new BotPlacedEvent(bot, location, habbo);
        Emulator.getPluginManager().fireEvent(event);

        if(event.isCancelled())
            return;

        if(room != null && bot != null && habbo != null)
        {
            if (room.getOwnerId() == habbo.getHabboInfo().getId() || habbo.hasPermission("acc_anyroomowner") || habbo.hasPermission("acc_placefurni"))
            {
                if (room.getCurrentBots().size() >= Room.MAXIMUM_BOTS && !habbo.hasPermission("acc_unlimited_bots"))
                {
                    habbo.getClient().sendResponse(new BotErrorComposer(BotErrorComposer.ROOM_ERROR_MAX_BOTS));
                    return;
                }

                if (!room.tileWalkable(location.x, location.y))
                    return;

                RoomUnit roomUnit = new RoomUnit();
                roomUnit.setRotation(RoomUserRotation.SOUTH);
                roomUnit.setLocation(location);
                roomUnit.setZ(room.getStackHeight(location.x, location.y, false));
                roomUnit.setPathFinderRoom(room);
                roomUnit.setRoomUnitType(RoomUnitType.BOT);
                roomUnit.setCanWalk(room.isAllowBotsWalk());
                bot.setRoomUnit(roomUnit);
                bot.setRoom(room);
                bot.needsUpdate(true);
                room.addBot(bot);
                Emulator.getThreading().run(bot);
                room.sendComposer(new RoomUsersComposer(bot).compose());
                habbo.getInventory().getBotsComponent().removeBot(bot);
                habbo.getClient().sendResponse(new RemoveBotComposer(bot));
                bot.onPlace(habbo, room);
                bot.cycle(false);
            }
            else
            {
                habbo.getClient().sendResponse(new BubbleAlertComposer(BubbleAlertKeys.FURNI_PLACE_EMENT_ERROR.key, "cant_set_not_owner"));
            }
        }
    }

    /**
     * Removes a bot from the room.
     * Note the owner is being set to the Habbo.
     * @param botId The id of the Bot that is being picked up.
     * @param habbo The Habbo who picks it.
     */
    public void pickUpBot(int botId, Habbo habbo)
    {
        if(habbo.getHabboInfo().getCurrentRoom() != null)
        {
            this.pickUpBot(habbo.getHabboInfo().getCurrentRoom().getBot(Math.abs(botId)), habbo);
        }
    }

    /**
     * Removes a bot from the room.
     * Note the owner is being set to the Habbo.
     * @param bot The Bot that is being picked up.
     * @param habbo The Habbo who picks it.
     */
    public void pickUpBot(Bot bot, Habbo habbo)
    {
        if(bot != null && habbo != null)
        {
            BotPickUpEvent pickedUpEvent = new BotPickUpEvent(bot, habbo);
            Emulator.getPluginManager().fireEvent(pickedUpEvent);

            if(pickedUpEvent.isCancelled())
                return;

            if (bot.getOwnerId() == habbo.getHabboInfo().getId() || habbo.hasPermission("acc_anyroomowner"))
            {
                if (!habbo.hasPermission("acc_unlimited_bots") && habbo.getInventory().getBotsComponent().getBots().size() >= 15)
                    return;

                bot.onPickUp(habbo, habbo.getHabboInfo().getCurrentRoom());
                habbo.getHabboInfo().getCurrentRoom().removeBot(bot);
                bot.stopFollowingHabbo();
                bot.setOwnerId(habbo.getHabboInfo().getId());
                bot.setOwnerName(habbo.getHabboInfo().getUsername());
                bot.needsUpdate(true);
                Emulator.getThreading().run(bot);

                habbo.getInventory().getBotsComponent().addBot(bot);
                habbo.getClient().sendResponse(new AddBotComposer(bot));
            }
        }
    }

    /**
     * Loads a bot from the given ResultSet.
     * @param set The set this bot must be initialised from.
     * @return The initialised bot. Returns NULL upon SQLException being thrown.
     */
    public Bot loadBot(ResultSet set)
    {
        try
        {
            String type = set.getString("type");
            Class<? extends Bot> botClazz = botDefenitions.get(type);

            if(botClazz != null)
                return botClazz.getDeclaredConstructor(ResultSet.class).newInstance(set);
            else
                Emulator.getLogging().logErrorLine("Unknown Bot Type: " + type);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return null;
    }

    /**
     * Deletes a bot from the database.
     * @param bot The bot to delete.
     * @return true if the bot has been deleted.
     */
    public boolean deleteBot(Bot bot)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM bots WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, bot.getId());
            return statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }

    /**
     * Add a new bot type in order to implement custom behaviour.
     *
     * Make sure to extend the Bot class and make the constructor match Bot(ResultSet)
     * @param type The name of the bot type.
     * @param botClazz The class that needs to be initialised.
     * @throws Exception If the bot type already exists.
     *                   If the bot class has no constructor matchin Bot(ResultSet)
     */
    public static void addBotDefinition(String type, Class<? extends Bot> botClazz) throws Exception
    {
        if(botClazz.getDeclaredConstructor(ResultSet.class) == null)
        {
            throw new Exception("Missing Bot(ResultSet) constructor!");
        }
        else
        {
            botClazz.getDeclaredConstructor(ResultSet.class).setAccessible(true);

            botDefenitions.put(type, botClazz);
        }
    }

    /**
     * Called upon Emulator shutdown.
     * Implement 'public static void dispose()' to pass on this event
     * to your custom bot class.
     */
    public void dispose()
    {
        for(Map.Entry<String, Class<? extends Bot>> set : botDefenitions.entrySet())
        {
            try
            {
                Method m = set.getValue().getMethod("dispose");
                m.setAccessible(true);
                m.invoke(null);
            }
            catch (NoSuchMethodException e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute dispose method upon bot type '" + set.getKey() + "'. No Such Method!");
            }
            catch (Exception e)
            {
                Emulator.getLogging().logStart("Bot Manager -> Failed to execute dispose method upon bot type '" + set.getKey() + "'. Error: " + e.getMessage());
            }
        }
    }
}
