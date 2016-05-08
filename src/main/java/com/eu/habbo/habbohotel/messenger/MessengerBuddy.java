package com.eu.habbo.habbohotel.messenger;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 25-8-2014 14:53.
 */
public class MessengerBuddy implements Runnable {

    private int id;
    private String username;
    private String gender;
    private int online;
    private String look;
    private String motto;
    private short relation;
    private boolean inRoom;
    private int userOne = 0;

    public MessengerBuddy(ResultSet set)
    {
        try
        {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.gender = set.getString("gender");
            this.online = set.getInt("online");
            this.motto = set.getString("motto");
            this.look = set.getString("look");
            this.relation = (short) set.getInt("relation");
            this.userOne = set.getInt("user_one_id");
            this.inRoom = false;
            if(this.online == 1)
            {
                Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(this.username);

                if(habbo != null)
                {
                    this.inRoom = habbo.getHabboInfo().getCurrentRoom() != null;
                }
            }
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public MessengerBuddy(ResultSet set, boolean value)
    {
        try
        {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.gender = set.getString("gender");
            this.online = set.getInt("online");
            this.motto = set.getString("motto");
            this.look = set.getString("look");
            this.relation = 0;
            this.userOne = 0;
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public MessengerBuddy(int id, String username, String look, Short relation, int userOne)
    {
        this.id = id;
        this.username = username;
        this.gender = "M";
        this.online = 0;
        this.motto = "";
        this.look = look;
        this.relation = relation;
        this.userOne = userOne;
    }

    public MessengerBuddy(Habbo habbo, int userOne)
    {
        this.id = habbo.getHabboInfo().getId();
        this.username = habbo.getHabboInfo().getUsername();
        this.gender = habbo.getHabboInfo().getGender().name().toUpperCase();
        this.online = habbo.getHabboInfo().isOnline() ? 1 : 0;
        this.motto = habbo.getHabboInfo().getMotto();
        this.look = habbo.getHabboInfo().getLook();
        this.relation = 0;
        this.userOne = userOne;
    }

    public void setRelation(int relation)
    {
        this.relation = (short)relation;
        Emulator.getThreading().run(this);
    }

    public int getId()
    {
        return this.id;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getGender()
    {
        return this.gender;
    }

    public int getOnline()
    {
        return this.online;
    }

    public String getLook()
    {
        return this.look;
    }

    public String getMotto()
    {
        return this.motto;
    }

    public short getRelation()
    {
        return this.relation;
    }

    public boolean inRoom()
    {
        return this.inRoom;
    }

    public void inRoom(boolean value)
    {
        this.inRoom = value;
    }

    public void setOnline(boolean value)
    {
        this.online = (value ? 1 : 0);
    }

    @Override
    public void run() {

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE messenger_friendships SET relation = ? WHERE user_one_id = ? AND user_two_id = ?");
            statement.setInt(1, this.relation);
            statement.setInt(2, this.userOne);
            statement.setInt(3, this.id);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}
