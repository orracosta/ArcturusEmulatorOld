package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModToolUserInfoComposer extends MessageComposer
{
    private ResultSet set;

    public ModToolUserInfoComposer(ResultSet set)
    {
        this.set = set;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolUserInfoComposer);
        try
        {
            this.response.appendInt(set.getInt("user_id"));
            this.response.appendString(set.getString("username"));
            this.response.appendString(set.getString("look"));
            this.response.appendInt(set.getInt("account_created"));
            this.response.appendInt(set.getInt("online") == 1 ? 0 : set.getInt("last_online"));
            this.response.appendBoolean(set.getInt("online") == 1);
            this.response.appendInt(set.getInt("cfh_send"));
            this.response.appendInt(set.getInt("cfh_abusive"));
            this.response.appendInt(set.getInt("cfh_warnings"));
            this.response.appendInt(set.getInt("cfh_bans"));
            this.response.appendInt(Emulator.getIntUnixTimestamp() - set.getInt("account_created"));
            this.response.appendString("");
            this.response.appendString("");
            this.response.appendInt(0);
            this.response.appendInt(0);
            this.response.appendString(set.getString("mail"));
            this.response.appendString("");
            return this.response;
        }
        catch (SQLException e)
        {
			Emulator.getLogging().logSQLException(e);
        }

        return null;
    }
}
