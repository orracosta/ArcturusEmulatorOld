package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.MessagesForYouComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.StaffAlertWithLinkComposer;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;

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

    public HabboInventory getHabboInventory()
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
        //this.habboInfo.setIpLogin(((InetSocketAddress)this.client.getChannel().remoteAddress()).getAddress().getHostAddress());
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
                Room room =  Emulator.getGameEnvironment().getRoomManager().getRoom(this.getHabboInfo().getRoomQueueId());

                if (room != null)
                {
                    room.removeFromQueue(this);
                }
            }

            Emulator.getGameEnvironment().getGuideManager().userLogsOut(this);
            this.needsUpdate(true);
            this.run();
            this.isOnline(false);
            this.getHabboInventory().dispose();
            this.messenger.connectionChanged(this, false, false);
            this.messenger.dispose();
            this.disconnected = true;
            this.habboInfo.setLastOnline(Emulator.getIntUnixTimestamp());
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

        this.getHabboInfo().addCredits(credits);
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

        this.getHabboInfo().addPixels(pixels);
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

        this.getHabboInfo().addCurrencyAmount(type, points);
        this.client.sendResponse(new UserPointsComposer(this.client.getHabbo().getHabboInfo().getCurrencyAmount(type), points, type));
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
}
