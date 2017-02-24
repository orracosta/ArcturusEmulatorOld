package com.eu.habbo.messages.outgoing.gamcenter;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class GameCenterGameListComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.GameCenterGameListComposer);
        this.response.appendInt32(1);//Count

        this.response.appendInt32(3);
        this.response.appendString("basejump");
        this.response.appendString("68bbd2"); //Background Color
        this.response.appendString(""); //Text color
        this.response.appendString("http://localhost/game/dcr/c_images/gamecenter_basejump/");
        this.response.appendString("");

        return this.response;
    }
}