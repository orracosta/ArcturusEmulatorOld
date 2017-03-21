package com.eu.habbo.messages.outgoing.gamcenter.basejump;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class BaseJumpLoadGameComposer extends MessageComposer
{
    private final GameClient client;

    public BaseJumpLoadGameComposer(GameClient client)
    {
        this.client = client;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.BaseJumpLoadGameComposer);

        this.response.appendInt32(3);
        this.response.appendString("1344031458870");
        this.response.appendString("http://images.habbo.com/basejump/693/BaseJump.swf");
        this.response.appendString("best");
        this.response.appendString("showAll");
        this.response.appendInt32(60);
        this.response.appendInt32(10);
        this.response.appendInt32(0);
        this.response.appendInt32(4);
        this.response.appendString("accessToken");
        this.response.appendString(this.client.getHabbo().getHabboInfo().getUsername() + "-" + this.client.getHabbo().getHabboInfo().getLook());
        this.response.appendString("gameServerHost");
        this.response.appendString("localhost");
        this.response.appendString("gameServerPort");
        this.response.appendString("3002");
        this.response.appendString("socketPolicyPort");
        this.response.appendString("30843");
        return this.response;
    }
}