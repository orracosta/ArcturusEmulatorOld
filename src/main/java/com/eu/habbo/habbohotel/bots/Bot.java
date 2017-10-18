package com.eu.habbo.habbohotel.bots;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.*;
import com.eu.habbo.plugin.events.bots.BotChatEvent;
import com.eu.habbo.plugin.events.bots.BotShoutEvent;
import com.eu.habbo.plugin.events.bots.BotTalkEvent;
import com.eu.habbo.plugin.events.bots.BotWhisperEvent;
import com.eu.habbo.threading.runnables.BotFollowHabbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Bot implements Runnable
{
    /**
     * Bot id.
     */
    private int id;

    /**
     * Bot name.
     */
    private String name;

    /**
     * Bot motto.
     */
    private String motto;

    /**
     * Bot look.
     */
    private String figure;

    /**
     * Bot gender.
     */
    private HabboGender gender;

    /**
     * Bot owner id.
     */
    private int ownerId;

    /**
     * Bot owner name.
     */
    private String ownerName;

    /**
     * Current room the bot is in.
     */
    private Room room;

    /**
     * Current roomUnit that is linked to this bot.
     */
    private RoomUnit roomUnit;

    /**
     * Should auto talk.
     */
    private boolean chatAuto;

    /**
     * Should talk random.
     */
    private boolean chatRandom;

    /**
     * Delay between each sentence.
     */
    private short chatDelay;

    /**
     * Next timestamp the bot can talk.
     */
    private int chatTimeOut;

    /**
     * All chatlines the bot has.
     */
    private final ArrayList<String> chatLines;

    /**
     * Last chatline the bot spoke.
     */
    private short lastChatIndex;

    /**
     * Type of this bot. Used to define custom behaviour.
     * See API docs on how to use this.
     */
    private String type;

    /**
     * Enable effect id.
     */
    private int effect;

    /**
     * Wether the bot has to be saved to the database.
     */
    private boolean needsUpdate;

    /**
     * Which roomunit is this bot following.
     */
    private int followingHabboId;

    public Bot(int id, String name, String motto, String figure, HabboGender gender, int ownerId, String ownerName)
    {
        this.id = id;
        this.name = name;
        this.motto = motto;
        this.figure = figure;
        this.gender = gender;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.chatAuto = false;
        this.chatRandom = false;
        this.chatDelay = 1000;
        this.chatLines = new ArrayList<String>();
        this.type = "generic_bot";
        this.room = null;
    }

    public Bot(ResultSet set) throws SQLException
    {
        this.id             = set.getInt("id");
        this.name           = set.getString("name");
        this.motto          = set.getString("motto");
        this.figure         = set.getString("figure");
        this.gender         = HabboGender.valueOf(set.getString("gender"));
        this.ownerId        = set.getInt("user_id");
        this.ownerName      = set.getString("owner_name");
        this.chatAuto       = set.getString("chat_auto").equals("1");
        this.chatRandom     = set.getString("chat_random").equals("1");
        this.chatDelay      = set.getShort("chat_delay");
        this.chatLines      = new ArrayList<String>(Arrays.asList(set.getString("chat_lines").split("\r")));
        this.type           = set.getString("type");
        this.effect         = set.getInt("effect");
        this.room           = null;
        this.roomUnit       = null;
        this.chatTimeOut    = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.needsUpdate    = false;
        this.lastChatIndex  = 0;
    }

    public Bot(Bot bot)
    {
        this.name           = bot.getName();
        this.motto          = bot.getMotto();
        this.figure         = bot.getFigure();
        this.gender         = bot.getGender();
        this.ownerId        = bot.getOwnerId();
        this.ownerName      = bot.getOwnerName();
        this.chatAuto       = true;
        this.chatRandom     = false;
        this.chatDelay      = 10;
        this.chatTimeOut    = Emulator.getIntUnixTimestamp() + this.chatDelay;
        this.chatLines      = new ArrayList<String>(Arrays.asList(new String[] {"Default Message :D"}));
        this.type           = bot.getType();
        this.effect         = bot.getEffect();
        this.room           = null;
        this.roomUnit       = null;
        this.lastChatIndex  = 0;

        this.needsUpdate = false;
    }

    /**
     * Sets wheter the bot has to be updated in the database.
     * @param needsUpdate Should the bot be updated in the database.
     */
    public void needsUpdate(boolean needsUpdate)
    {
        this.needsUpdate = needsUpdate;
    }

    /**
     * @return Wether the bot has to be updated in the database.
     */
    public boolean needsUpdate()
    {
        return this.needsUpdate;
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE bots SET name = ?, motto = ?, figure = ?, gender = ?, user_id = ?, room_id = ?, x = ?, y = ?, z = ?, rot = ?, dance = ?, freeroam = ?, chat_lines = ?, chat_auto = ?, chat_random = ?, chat_delay = ? WHERE id = ?"))
            {
                statement.setString(1, this.name);
                statement.setString(2, this.motto);
                statement.setString(3, this.figure);
                statement.setString(4, this.gender.toString());
                statement.setInt(5, this.ownerId);
                statement.setInt(6, this.room == null ? 0 : this.room.getId());
                statement.setInt(7, this.roomUnit == null ? 0 : this.roomUnit.getX());
                statement.setInt(8, this.roomUnit == null ? 0 : this.roomUnit.getY());
                statement.setDouble(9, this.roomUnit == null ? 0 : this.roomUnit.getZ());
                statement.setInt(10, this.roomUnit == null ? 0 : this.roomUnit.getBodyRotation().getValue());
                statement.setInt(11, this.roomUnit == null ? 0 : this.roomUnit.getDanceType().getType());
                statement.setString(12, this.roomUnit == null ? "0" : this.roomUnit.canWalk() ? "1" : "0");
                String text = "";
                for(String s : this.chatLines)
                {
                    text += s + "\r";
                }
                statement.setString(13, text);
                statement.setString(14, this.chatAuto ? "1" : "0");
                statement.setString(15, this.chatRandom ? "1" : "0");
                statement.setInt(16, this.chatDelay);
                statement.setInt(17, this.id);
                statement.execute();
                this.needsUpdate = false;
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    /**
     * Cycles through the bot checking if the bot should perform a certain action:
     *
     * -> Look for a new tile to walk to.
     * -> Speak
     *
     * Override to implement custom behaviour. Make sure to call super.cycle() first.
     */
    public void cycle(boolean canWalk)
    {
        if(this.roomUnit != null)
        {
            if(canWalk && this.getRoomUnit().canWalk())
            {
                if (!this.roomUnit.isWalking())
                {
                    if (this.roomUnit.getWalkTimeOut() < Emulator.getIntUnixTimestamp() && this.followingHabboId == 0)
                    {
                        this.roomUnit.setGoalLocation(this.room.getRandomWalkableTile());
                        int timeOut = Emulator.getRandom().nextInt(20) * 2;
                        this.roomUnit.setWalkTimeOut((timeOut < 10 ? 5 : timeOut) + Emulator.getIntUnixTimestamp());
                    }
                } else
                {
                    for (RoomTile t : this.room.getLayout().getTilesAround(this.room.getLayout().getTile(this.getRoomUnit().getX(), this.getRoomUnit().getY())))
                    {
                        WiredHandler.handle(WiredTriggerType.BOT_REACHED_STF, this.roomUnit, this.room, room.getItemsAt(t).toArray());
                    }
                }
            }

            if(!this.chatLines.isEmpty() && this.chatTimeOut <= Emulator.getIntUnixTimestamp() && this.chatAuto)
            {
                if(this.room != null)
                {
                    this.lastChatIndex = (this.chatRandom ? (short)Emulator.getRandom().nextInt(this.chatLines.size()) : (this.lastChatIndex == (this.chatLines.size() - 1) ? 0 : this.lastChatIndex++));
                    this.talk(this.chatLines.get(this.lastChatIndex)
                            .replace("%owner%", this.room.getOwnerName())
                            .replace("%item_count%", this.room.itemCount() + "")
                            .replace("%name%", this.name)
                            .replace("%roomname%", this.room.getName())
                            .replace("%user_count%", this.room.getUserCount() + ""));
                    this.chatTimeOut = Emulator.getIntUnixTimestamp() + this.chatDelay;
                }
            }
        }
    }

    /**
     * Have the bot say something in the room it currently is in.
     * @param message The message the bot has to say.
     */
    public void talk(String message)
    {
        if(this.room != null)
        {
            BotChatEvent event = new BotTalkEvent(this, message);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.room.botChat(new RoomUserTalkComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)).compose());
        }
    }

    /**
     * Have the bot shout something in the room it currently is in.
     * @param message The message the bot has to shout.
     */
    public void shout(String message)
    {
        if(this.room != null)
        {
            BotChatEvent event = new BotShoutEvent(this, message);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            this.room.botChat(new RoomUserShoutComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)).compose());
        }
    }

    /**
     * Have the bot whisper something to a habbo.
     * @param message The message the bot has to whisper.
     * @param habbo The Habbo it should whisper to.
     */
    public void whisper(String message, Habbo habbo)
    {
        if(this.room != null && habbo != null)
        {
            BotWhisperEvent event = new BotWhisperEvent(this, message, habbo);
            if(Emulator.getPluginManager().fireEvent(event).isCancelled())
                return;

            event.target.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(event.message, this.roomUnit, RoomChatMessageBubbles.BOT)));
        }
    }

    /**
     * This event is triggered whenever a Habbo places a bot.
     * @param habbo The Habbo who placed this bot.
     * @param room The room this bot was placed in.
     */
    public void onPlace(Habbo habbo, Room room)
    {

    }

    /**
     * This event is triggered whenever a Habbo picks a bot.
     * @param habbo The Habbo who picked up this bot.
     * @param room The Room this bot was placed in.
     */
    public void onPickUp(Habbo habbo, Room room)
    {

    }

    /**
     * This event is triggered whenever a Habbo talks or shouts in a room.
     * @param message The message that has been said.
     */
    public void onUserSay(final RoomChatMessage message)
    {

    }

    /**
     * @return The id of the bot.
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * Sets the id of the bot.
     * @param id The new id of the bot.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * @return The name of the bot
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of the bot.
     * @param name The new name of the bot.
     */
    public void setName(String name)
    {
        this.name        = name;
        this.needsUpdate = true;

        //if(this.room != null)
            //this.room.sendComposer(new ChangeNameUpdatedComposer(this.getRoomUnit(), this.getName()).compose());
    }

    /**
     * @return The motto of the bot.
     */
    public String getMotto()
    {
        return this.motto;
    }

    /**
     * Sets a new motto for the bot.
     * @param motto The new motto for the bot.
     */
    public void setMotto(String motto)
    {
        this.motto       = motto;
        this.needsUpdate = true;
    }

    /**
     * @return The figure of the bot.
     */
    public String getFigure()
    {
        return this.figure;
    }

    /**
     * Sets a new figure of the bot and updates it in the room.
     * @param figure The new figure of the bot.
     */
    public void setFigure(String figure)
    {
        this.figure      = figure;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    /**
     * @return The gender of the bot.
     */
    public HabboGender getGender()
    {
        return this.gender;
    }

    /**
     * Sets a new Gender of the bot and updates it in the room.
     * @param gender The new gender of the bot.
     */
    public void setGender(HabboGender gender)
    {
        this.gender      = gender;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    /**
     * @return The owner id of the bot.
     */
    public int getOwnerId()
    {
        return this.ownerId;
    }

    /**
     * @param ownerId The new owner id of the bot.
     */
    public void setOwnerId(int ownerId)
    {
        this.ownerId     = ownerId;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    /**
     * @return The owner name of the bot.
     */
    public String getOwnerName()
    {
        return this.ownerName;
    }

    /**
     * @param ownerName The new owner name of the bot.
     */
    public void setOwnerName(String ownerName)
    {
        this.ownerName   = ownerName;
        this.needsUpdate = true;

        if(this.room != null)
            this.room.sendComposer(new RoomUsersComposer(this).compose());
    }

    /**
     * @return The room this bot is in. Returns NULL when in inventory.
     */
    public Room getRoom()
    {
        return this.room;
    }

    /**
     * @param room The room this bot is in.
     */
    public void setRoom(Room room)
    {
        this.room = room;
    }

    /**
     * @return The RoomUnit of the bot.
     */
    public RoomUnit getRoomUnit()
    {
        return this.roomUnit;
    }

    /**
     * @param roomUnit The RoomUnit of the bot.
     */
    public void setRoomUnit(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
    }

    /**
     * @return Wether the bot has auto chat enabled.
     */
    public boolean isChatAuto()
    {
        return this.chatAuto;
    }

    /**
     * @param chatAuto Sets wheter the bot has auto chat enabled.
     */
    public void setChatAuto(boolean chatAuto)
    {
        this.chatAuto    = chatAuto;
        this.needsUpdate = true;
    }

    /**
     * @return Wheter the chatter is randomly selected.
     */
    public boolean isChatRandom()
    {
        return this.chatRandom;
    }

    /**
     * @param chatRandom Sets wheter the chatter is randomly selected.
     */
    public void setChatRandom(boolean chatRandom)
    {
        this.chatRandom  = chatRandom;
        this.needsUpdate = true;
    }

    /**
     * @return The minimum interval between two messages spoken by the bot.
     */
    public int getChatDelay()
    {
        return this.chatDelay;
    }

    /**
     * @param chatDelay Sets the minimum interval between two messages spoken by the bot.
     */
    public void setChatDelay(short chatDelay)
    {
        this.chatDelay   = chatDelay;
        this.needsUpdate = true;
    }

    /**
     * Removes all chatlines from the bot.
     */
    public void clearChat()
    {
        synchronized (this.chatLines)
        {
            this.chatLines.clear();
            this.needsUpdate = true;
        }
    }

    /**
     * @return The bot type.
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * @return The current enable effect.
     */
    public int getEffect()
    {
        return this.effect;
    }

    /**
     * Sets the effect for a bot and also updates it to the room.
     * @param effect The effect to give to the bot.
     */
    public void setEffect(int effect)
    {
        this.effect      = effect;
        this.needsUpdate = true;

        if (this.roomUnit != null)
        {
            this.roomUnit.setEffectId(this.effect);

            if (this.room != null)
            {
                this.room.sendComposer(new RoomUserEffectComposer(this.roomUnit).compose());
            }
        }
    }

    /**
     * Adds new chatlines to the bot. Does not erase existing chatlines.
     * @param chatLines The chatlines to add.
     */
    public void addChatLines(ArrayList<String> chatLines)
    {
        synchronized (this.chatLines)
        {
            this.chatLines.addAll(chatLines);
            this.needsUpdate = true;
        }
    }

    /**
     * Adds a new chatline to the bot. Does not erase existing chatlines.
     * @param chatLine The chatline to add.
     */
    public void addChatLine(String chatLine)
    {
        synchronized (this.chatLines)
        {
            this.chatLines.add(chatLine);
            this.needsUpdate = true;
        }
    }

    /**
     * @return The chatlines this bot can speak.
     */
    public ArrayList<String> getChatLines()
    {
        return this.chatLines;
    }

    /**
     * @return The HabboInfo.id of the Habbo it is following.
     */
    public int getFollowingHabboId()
    {
        return this.followingHabboId;
    }

    /**
     * Starts following a specific Habbo. Action may get interrupted when:
     * <li>The bot is triggered with WiredEffectBotFollowHabbo.</li>
     * <li>The bot is triggered with WiredEffectBotGiveHandItem.</li>
     * <li>The bots in the room are frozen</li>
     *
     * @param habbo The Habbo to follow.
     */
    public void startFollowingHabbo(Habbo habbo)
    {
        this.followingHabboId = habbo.getHabboInfo().getId();

        Emulator.getThreading().run(new BotFollowHabbo(this, habbo, habbo.getHabboInfo().getCurrentRoom()));
    }

    public void stopFollowingHabbo()
    {
        this.followingHabboId = 0;
    }

    /**
     * Used to load in custom data.
     * Implement this method whenever you want data to be loaded from the database
     * upon the loading of the BotManager. This is to guarantee database integrity.
     * This method must be implemented. Failing to do so will result into a runtime error.
     */
    public static void initialise()
    {

    }

    /**
     * Called upon Emulator shutdown.
     */
    public static void dispose()
    {

    }
}
