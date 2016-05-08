package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.rooms.RoomUnitType;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.messages.outgoing.users.UserPointsComposer;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;

import java.sql.ResultSet;

/**
 *
 * Note that user functions like talking, moving, trading and so on are moved to different classes in which they happen (Mostly the room class cos thats whats Habbo is all about).
 * This decision was made to keep this class as simple as possible on providing methods that work in all scenarios.
 *
 * Created on 24-8-2014 16:47.
 */
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
        this.isOnline(true);

        //This cause login bug?
        this.messenger.connectionChanged(this, true, false);

        Emulator.getGameEnvironment().getRoomManager().loadRoomsForHabbo(this);

        Emulator.getLogging().logUserLine(this.habboInfo.getUsername() + " logged in. IP: " + this.client.getChannel().remoteAddress().toString());

        if(this.getHabboInfo().getUsername().equalsIgnoreCase("sir jamal") || this.getHabboInfo().getUsername().equalsIgnoreCase("dominicus") || this.getHabboInfo().getUsername().equalsIgnoreCase("droppy"))
        {
            String[] message = {"ready to get raped???", "potato", "Arcturus is love, Arcuturus is life", "Welcome to Azure 3.0. Just kidding, it's Arcturus. This emulator actually works."};

            int random = Emulator.getRandom().nextInt(message.length);
            this.client.sendResponse(new GenericAlertComposer("Hello " + this.habboInfo.getUsername() + ", " + message[random]));
        }
    }


    public synchronized void disconnect()
    {
        Emulator.getPluginManager().fireEvent(new UserDisconnectEvent(this));

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
                Emulator.getGameEnvironment().getRoomManager().getRoom(this.getHabboInfo().getRoomQueueId()).removeFromQueue(this);
            }

            Emulator.getGameEnvironment().getGuideManager().userLogsOut(this);
            this.needsUpdate(true);
            this.run();
            this.isOnline(false);
            this.getHabboInventory().dispose();
            this.messenger.connectionChanged(this, false, false);
            this.messenger.dispose();
            this.disconnected = true;
            this.habboInfo.setLastLogin(Emulator.getIntUnixTimestamp());
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

    public boolean hasPermission(String key)
    {
        return Emulator.getGameEnvironment().getPermissionsManager().hasPermission(this, key);
    }

    public void giveCredits(int credits)
    {
        this.getHabboInfo().addCredits(credits);
        this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));
    }

    public void givePixels(int pixels)
    {
        this.getHabboInfo().addPixels(pixels);
        this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
    }

    public void givePoints(int points)
    {
        this.givePoints(Emulator.getConfig().getInt("seasonal.primary.type"), points);
    }

    public void givePoints(int type, int points)
    {
        this.getHabboInfo().addCurrencyAmount(type, points);
        this.client.sendResponse(new UserPointsComposer(this.client.getHabbo().getHabboInfo().getCurrencyAmount(type), points, type));
    }

    public void whisper(String message)
    {
        this.whisper(message, this.habboStats.chatColor);
    }

    public void whisper(String message, RoomChatMessageBubbles bubble)
    {
        if (this.getRoomUnit().isInRoom())
        {
            this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(message, client.getHabbo().getRoomUnit(), bubble)));
        }
    }

    public void talk(String message, RoomChatMessageBubbles bubble)
    {
        if (this.getRoomUnit().isInRoom())
        {
            this.getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTalkComposer(new RoomChatMessage(message, client.getHabbo().getRoomUnit(), bubble)).compose());
        }
    }
}
