package com.eu.habbo.messages.outgoing.guilds.forums;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UnknownGuildForumComposer4 extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        //Possibly related to settings.
        this.response.init(Outgoing.UnknownGuildForumComposer4);
        //_SafeStr_3845._SafeStr_19178(k)
        {
            //_SafeStr_3844.fillFromMessage : _SafeStr_3845
            {
                this.response.appendInt32(0); //k._SafeStr_6864 = _arg_2._SafeStr_5878();   = guild_id
                this.response.appendString(""); //k._name = _arg_2.readString();            = name
                this.response.appendString(""); //k._SafeStr_5790 = _arg_2.readString();    = description
                this.response.appendString(""); //k._icon = _arg_2.readString();            = icon
                this.response.appendInt32(0); //k._SafeStr_11338 = _arg_2._SafeStr_5878(); (?)
                this.response.appendInt32(0); //k._SafeStr_19191 = _arg_2._SafeStr_5878();  = rating
                this.response.appendInt32(0); //k._SafeStr_11328 = _arg_2._SafeStr_5878();  = total_messages
                this.response.appendInt32(0); //k._SafeStr_19192 = _arg_2._SafeStr_5878();  = new_messages
                this.response.appendInt32(0); //k._SafeStr_19193 = _arg_2._SafeStr_5878(); (?)
                this.response.appendInt32(0); //k._SafeStr_19194 = _arg_2._SafeStr_5878();  = last_author_id
                this.response.appendString(""); //k._SafeStr_19195 = _arg_2.readString();   = last_author_name
                this.response.appendInt32(0); //k._SafeStr_19196 = _arg_2._SafeStr_5878();  = update_time
            }

            this.response.appendInt32(0); //_local_2._SafeStr_11123 = k._SafeStr_5878();
            this.response.appendInt32(0); //_local_2._SafeStr_11124 = k._SafeStr_5878();
            this.response.appendInt32(0); //_local_2._SafeStr_11125 = k._SafeStr_5878();
            this.response.appendInt32(0); //_local_2._SafeStr_11126 = k._SafeStr_5878();
            this.response.appendString(""); //_local_2._SafeStr_19197 = k.readString();
            this.response.appendString(""); //_local_2._SafeStr_19198 = k.readString();
            this.response.appendString(""); //_local_2._SafeStr_19199 = k.readString();
            this.response.appendString(""); //_local_2._SafeStr_19200 = k.readString();
            this.response.appendString(""); //_local_2._SafeStr_19201 = k.readString();
            this.response.appendBoolean(false); //;_local_2._SafeStr_19202 = k.readBoolean();
            this.response.appendBoolean(false); //_local_2._SafeStr_19203 = k.readBoolean();
        }
        return this.response;
    }
}