package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.threading.runnables.UpdateModToolIssue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModToolIssue implements ISerialize
{
    public int id;
    public volatile ModToolTicketState state;
    public volatile ModToolTicketType type = ModToolTicketType.NORMAL;
    public int category;
    public int timestamp;
    public volatile int priority;
    public int reportedId;
    public String reportedUsername = "Uknown Reported Habbo";
    public int roomId;
    public int senderId;
    public String senderUsername = "Unknown Sender";
    public volatile int modId = -1;
    public volatile String modName = "";
    public String message = "Unknown Message";
    public ArrayList<ModToolChatLog> chatLogs = null;

    public ModToolIssue(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.state = ModToolTicketState.getState(set.getInt("state"));
        this.timestamp = set.getInt("timestamp");
        this.priority = set.getInt("score");
        this.senderId = set.getInt("sender_id");
        this.senderUsername = set.getString("sender_username");
        this.reportedId = set.getInt("reported_id");
        this.reportedUsername = set.getString("reported_username");
        this.message = set.getString("issue");
        this.modId = set.getInt("mod_id");
        this.modName = set.getString("mod_username");
        this.type = ModToolTicketType.values()[set.getInt("type") - 1];
        this.category = set.getInt("category");

        if(this.modId <= 0)
        {
            this.modName = "";
            this.state = ModToolTicketState.OPEN;
        }
    }

    public ModToolIssue(int senderId, String senderUserName, int reportedId, String reportedUsername, int reportedRoomId, String message, ModToolTicketType type)
    {
        this.state = ModToolTicketState.OPEN;
        this.timestamp = Emulator.getIntUnixTimestamp();
        this.priority = 0;
        this.senderId = senderId;
        this.senderUsername = senderUserName;
        this.reportedUsername = reportedUsername;
        this.reportedId = reportedId;
        this.roomId = reportedRoomId;
        this.message = message;
        this.type = type;
        this.category = 1;
    }

    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.id); //ID
        message.appendInt(this.state.getState()); //STATE
        message.appendInt(this.type.getType()); //TYPE
        message.appendInt(this.category); //CATEGORY ID
        message.appendInt(((Emulator.getIntUnixTimestamp() - this.timestamp))); //TIME IN MS AGO
        message.appendInt(this.priority); //PRIORITY
        message.appendInt(1); // != 0?
        message.appendInt(this.senderId); //Reporter user ID
        message.appendString(this.senderUsername); //Reporter user name.
        message.appendInt(this.reportedId); //Reported user ID.
        message.appendString(this.reportedUsername); //Reported user name.
        message.appendInt(this.modId); //MOD User ID?
        message.appendString(this.modName); //MOD User name?
        message.appendString(this.message);
        message.appendInt(0);

        if (this.chatLogs != null)
        {
            message.appendInt(this.chatLogs.size());
            for (ModToolChatLog chatLog : this.chatLogs)
            {
                message.appendString(chatLog.message);
                message.appendInt(0);
                message.appendInt(chatLog.message.length());
            }
        }
        else
        {
            message.appendInt(0);
        }
    }

    public void updateInDatabase()
    {
        Emulator.getThreading().run(new UpdateModToolIssue(this));
    }
}
