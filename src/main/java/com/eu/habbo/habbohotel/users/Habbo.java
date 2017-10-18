package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.inventory.*;
import com.eu.habbo.messages.outgoing.rooms.FloodCounterComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserIgnoredComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.plugin.events.users.UserCreditsEvent;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import com.eu.habbo.plugin.events.users.UserPointsEvent;
import gnu.trove.set.hash.THashSet;

import java.net.InetSocketAddress;
import java.sql.ResultSet;

public class Habbo implements Runnable
{
    private GameClient client;

    private final HabboInfo habboInfo;
    private final HabboStats habboStats;
    private final Messenger messenger;
    private final HabboInventory habboInventory;
    private RoomUnit roomUnit;

    private volatile boolean update;
    private volatile boolean disconnected = false;
    private volatile boolean disconnecting = false;

    public boolean firstVisit = false;

    public Habbo(ResultSet set)
    {
        this.client = null;
        this.habboInfo = new HabboInfo(set);
        this.habboStats = HabboStats.load(this);
        this.habboInventory = new HabboInventory(this);

        this.messenger = new Messenger();
        this.messenger.loadFriends(this);
        this.messenger.loadFriendRequests(this);

        this.roomUnit = new RoomUnit();
        this.roomUnit.setRoomUnitType(RoomUnitType.USER);

        this.update = false;
    }

    public boolean isOnline()
    {
        return this.habboInfo.isOnline();
    }

    void isOnline(boolean value)
    {
        this.habboInfo.setOnline(value);
        this.update();
    }

    void update()
    {
        this.update = true;
        Emulator.getThreading().run(this);
    }

    void needsUpdate(boolean value)
    {
        this.update = value;
    }

    boolean needsUpdate()
    {
        return this.update;
    }

    public Messenger getMessenger()
    {
        return this.messenger;
    }

    public HabboInfo getHabboInfo()
    {
        return this.habboInfo;
    }

    public HabboStats getHabboStats()
    {
        return this.habboStats;
    }

    public HabboInventory getInventory()
    {
        return this.habboInventory;
    }

    public RoomUnit getRoomUnit()
    {
        return this.roomUnit;
    }

    public void setRoomUnit(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
    }

    public GameClient getClient()
    {
        return this.client;
    }

    public void setClient(GameClient client)
    {
        this.client = client;
    }

    /*
        Called upon succesfull SSO Login. NOT socket connection.
     */
    public void connect()
    {
        if (!Emulator.getConfig().getBoolean("networking.tcp.proxy"))
        {
            this.habboInfo.setIpLogin(((InetSocketAddress) this.client.getChannel().remoteAddress()).getAddress().getHostAddress());
        }

        this.isOnline(true);

        this.messenger.connectionChanged(this, true, false);

        Emulator.getGameEnvironment().getRoomManager().loadRoomsForHabbo(this);
        Emulator.getLogging().logUserLine(this.habboInfo.getUsername() + " logged in from IP " + this.habboInfo.getIpLogin());
    }


    public synchronized void disconnect()
    {
        if (!Emulator.isShuttingDown)
        {
            Emulator.getPluginManager().fireEvent(new UserDisconnectEvent(this));
        }

        if(this.disconnected || this.disconnecting)
            return;

        this.disconnecting = true;

        try
        {
            if (this.getHabboInfo().getCurrentRoom() != null)
            {
                Emulator.getGameEnvironment().getRoomManager().leaveRoom(this, this.getHabboInfo().getCurrentRoom());
            }
            if (this.getHabboInfo().getRoomQueueId() > 0)
            {
                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getHabboInfo().getRoomQueueId());

                if (room != null)
                {
                    room.removeFromQueue(this);
                }
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        try
        {
            Emulator.getGameEnvironment().getGuideManager().userLogsOut(this);
            this.isOnline(false);
            this.needsUpdate(true);
            this.run();
            this.getInventory().dispose();
            this.messenger.connectionChanged(this, false, false);
            this.messenger.dispose();
            this.disconnected = true;
            AchievementManager.saveAchievements(this);

            this.habboStats.dispose();
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
            return;
        }
        finally
        {
            Emulator.getGameEnvironment().getRoomManager().unloadRoomsForHabbo(this);
            Emulator.getGameEnvironment().getHabboManager().removeHabbo(this);
        }
        Emulator.getLogging().logUserLine(this.habboInfo.getUsername() + " disconnected.");
        this.client = null;
    }
    @Override
    public void run()
    {
        if(this.needsUpdate())
        {
            this.habboInfo.run();
            this.needsUpdate(false);
        }
    }

    /**
     * Checks if the Habbo has the allowed permission.
     *
     * @param key The permission to check.
     * @return True if the Habbo has the permission.
     */
    public boolean hasPermission(String key)
    {
        return hasPermission(key, false);
    }

    /**
     * Checks if the Habbo has the allowed permission.
     *
     * @param key The permission to check.
     * @param hasRoomRights True if the Habbo is the room owner.
     * @return True if the Habbo has the permission.
     */
    public boolean hasPermission(String key, boolean hasRoomRights)
    {
        return Emulator.getGameEnvironment().getPermissionsManager().hasPermission(this, key, hasRoomRights);
    }

    /**
     * Gives credits to the Habbo and updates the credits balance in game.
     *
     * @param credits The amount of credits to give.
     */
    public void giveCredits(int credits)
    {
        if (credits == 0)
            return;

        UserCreditsEvent event = new UserCreditsEvent(this, credits);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled())
            return;

        this.getHabboInfo().addCredits(event.credits);
        this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));
    }

    /**
     * Gives pixels to the Habbo and updates the pixels balance in game.
     *
     * @param pixels The amount of pixels to give.
     */
    public void givePixels(int pixels)
    {
        if (pixels == 0)
            return;


        UserPointsEvent event = new UserPointsEvent(this, pixels, 0);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled())
            return;

        this.getHabboInfo().addPixels(event.points);
        this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
    }

    /**
     * Gives points to the Habbo and updates the points balance in game.
     *
     * @param points The amount of points to give.
     */
    public void givePoints(int points)
    {
        this.givePoints(Emulator.getConfig().getInt("seasonal.primary.type"), points);
    }

    /**
     * Gives points to the Habbo and updates the points balance in game.
     *
     * @param type The points type to give.
     * @param points The amount of points to give.
     */
    public void givePoints(int type, int points)
    {
        if (points == 0)
            return;

        UserPointsEvent event = new UserPointsEvent(this, points, type);
        if (Emulator.getPluginManager().fireEvent(event).isCancelled())
            return;

        this.getHabboInfo().addCurrencyAmount(event.type, event.points);
        this.client.sendResponse(new UserPointsComposer(this.client.getHabbo().getHabboInfo().getCurrencyAmount(type), event.points, event.type));
    }

    /**
     * Whispers a message to the Habbo.
     *
     * @param message The message to whisper.
     */
    public void whisper(String message)
    {
        this.whisper(message, this.habboStats.chatColor);
    }

    /**
     * Whispers a message to the Habbo.
     *
     * @param message The message to whisper.
     * @param bubble The chat bubble type to use.
     */
    public void whisper(String message, RoomChatMessageBubbles bubble)
    {
        if (this.getRoomUnit().isInRoom())
        {
            this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(message, client.getHabbo().getRoomUnit(), bubble)));
        }
    }

    /**
     * Makes the Habbo talk in the room.
     *
     * @param message The message to say.
     */
    public void talk(String message)
    {
        this.talk(message, this.habboStats.chatColor);
    }

    /**
     * Makes the Habbo talk in the room.
     *
     * @param message The message to say.
     * @param bubble The chat bubble type to use.
     */
    public void talk(String message, RoomChatMessageBubbles bubble)
    {
        if (this.getRoomUnit().isInRoom())
        {
            this.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTalkComposer(new RoomChatMessage(message, client.getHabbo().getRoomUnit(), bubble)).compose());
        }
    }

    /**
     * Makes the Habbo shout in the room.
     *
     * @param message The message to shout.
     */
    public void shout(String message)
    {
        this.shout(message, this.habboStats.chatColor);
    }

    /**
     * Makes the Habbo shout in the room.
     *
     * @param message The message to shout.
     * @param bubble The chat bubble type to use.
     */
    public void shout(String message, RoomChatMessageBubbles bubble)
    {
        if (this.getRoomUnit().isInRoom())
        {
            this.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTalkComposer(new RoomChatMessage(message, client.getHabbo().getRoomUnit(), bubble)).compose());
        }
    }

    /**
     * Sends an alert to the Habbo.
     *
     * @param message The message the alert contains.
     */
    public void alert(String message)
    {
        this.client.sendResponse(new GenericAlertComposer(message));
    }

    /**
     * Sends an old style alert to the Habbo.
     *
     * @param messages The messages the alert contains.
     */
    public void alert(String[] messages)
    {
        this.client.sendResponse(new MessagesForYouComposer(messages));
    }

    /**
     * Sends an alert with url to the Habbo.
     *
     * @param message The message the alert contains.
     * @param url The URL the alert contains.
     */
    public void alertWithUrl(String message, String url)
    {
        this.client.sendResponse(new StaffAlertWithLinkComposer(message, url));
    }

    /**
     * Forwards the Habbo to a room.
     *
     * @param id The id of the room to go to.
     */
    public void goToRoom(int id)
    {
        this.client.sendResponse(new ForwardToRoomComposer(id));
    }

    /**
     * Adds a new furniture to the inventory and also updates the client.
     * @param item The furniture to add.
     */
    public void addFurniture(HabboItem item)
    {
        this.habboInventory.getItemsComponent().addItem(item);
        this.client.sendResponse(new AddHabboItemComposer(item));
        this.client.sendResponse(new InventoryRefreshComposer());
    }

    /**
     * Adds new furniture to the inventory and also updates the client.
     * @param items The furniture to add.
     */
    public void addFurniture(THashSet<HabboItem> items)
    {
        this.habboInventory.getItemsComponent().addItems(items);
        this.client.sendResponse(new AddHabboItemComposer(items));
        this.client.sendResponse(new InventoryRefreshComposer());
    }

    /**
     * Removes a furniture from the inventory and also updates the client.
     * @param item The furniture to remove.
     */
    public void removeFurniture(HabboItem item)
    {
        this.habboInventory.getItemsComponent().removeHabboItem(item);
        this.client.sendResponse(new RemoveHabboItemComposer(item.getId()));
    }

    /**
     * Adds a bot to the HabboInventory.
     * @param bot The bot to add.
     */
    public void addBot(Bot bot)
    {
        this.habboInventory.getBotsComponent().addBot(bot);
        this.client.sendResponse(new AddBotComposer(bot));
    }

    /**
     * Removes a bot from the HabboInventory.
     * @param bot The bot to remove.
     */
    public void removeBot(Bot bot)
    {
        this.habboInventory.getBotsComponent().removeBot(bot);
        this.client.sendResponse(new RemoveBotComposer(bot));
    }

    /**
     * Deletes a bot from the database. Also picks it up from the room or removes it from the inventory.
     * @param bot The Bot to delete.
     */
    public void deleteBot(Bot bot)
    {
        this.removeBot(bot);
        bot.getRoom().removeBot(bot);
        Emulator.getGameEnvironment().getBotManager().deleteBot(bot);
    }

    /**
     * Adds a pet to the HabboInventory.
     * @param pet The Pet to add.
     */
    public void addPet(Pet pet)
    {
        this.habboInventory.getPetsComponent().addPet(pet);
        this.client.sendResponse(new AddPetComposer(pet));
    }

    /**
     * Removes a pet from the HabboInventory.
     * @param pet The Pet to remove.
     */
    public void removePet(Pet pet)
    {
        this.habboInventory.getPetsComponent().removePet(pet);
        this.client.sendResponse(new RemovePetComposer(pet));
    }

    /**
     * Creates a new badge and adds it to the HabboInventory.
     * @param code The badgecode to use.
     * @return true if succesfully added. False otherwise.
     */
    public boolean addBadge(String code)
    {
        if (this.habboInventory.getBadgesComponent().getBadge(code) == null)
        {
            HabboBadge badge = BadgesComponent.createBadge(code, this);
            this.habboInventory.getBadgesComponent().addBadge(badge);
            this.client.sendResponse(new AddUserBadgeComposer(badge));
            return true;
        }

        return false;
    }

    /**
     * Deletes a badge from the database and removes it from the inventory.
     * @param badge The Badge to delete.
     */
    public void deleteBadge(HabboBadge badge)
    {
        this.habboInventory.getBadgesComponent().removeBadge(badge);
        BadgesComponent.deleteBadge(this.getHabboInfo().getUsername(), badge);
        this.client.sendResponse(new InventoryBadgesComposer(this));
    }

    public void mute(int seconds)
    {
        if (!this.hasPermission("acc_no_mute"))
        {
            int remaining = this.habboStats.addMuteTime(seconds);
            this.client.sendResponse(new FloodCounterComposer(remaining));
            this.client.sendResponse(new MutedWhisperComposer(remaining));

            Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
            if (room != null)
            {
                room.sendComposer(new RoomUserIgnoredComposer(this, RoomUserIgnoredComposer.MUTED).compose());
            }
        }
    }

    public void unMute()
    {
        this.habboStats.unMute();
        this.client.sendResponse(new FloodCounterComposer(3));
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
        if (room != null)
        {
            room.sendComposer(new RoomUserIgnoredComposer(this, RoomUserIgnoredComposer.UNIGNORED).compose());
        }
    }
}
