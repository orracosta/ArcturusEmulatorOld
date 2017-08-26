package com.eu.habbo.messages.outgoing.gamecenter.basejump;

import com.eu.habbo.Emulator;
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

        this.response.appendInt(3);
        this.response.appendString("1344031458870");
        this.response.appendString("http://localhost/game/BaseJump.swf");
        this.response.appendString("best");
        this.response.appendString("showAll");
        this.response.appendInt(60);
        this.response.appendInt(10);
        this.response.appendInt(0);
        this.response.appendInt(6);
        this.response.appendString("assetUrl");
        this.response.appendString("http://localhost/gamecenter/gamecenter_basejump/BasicAssets.swf");
        this.response.appendString("habboHost");
        this.response.appendString("localhost");
        this.response.appendString("accessToken");
        this.response.appendString(Emulator.getConfig().getValue("username") + "\t" + Emulator.version + "\t" + this.client.getHabbo().getHabboInfo().getId() + "\t" + this.client.getHabbo().getHabboInfo().getUsername() + "\t" + this.client.getHabbo().getHabboInfo().getLook() + "\t" + this.client.getHabbo().getHabboInfo().getCredits());
        this.response.appendString("gameServerHost");
        this.response.appendString("127.0.0.1");
        this.response.appendString("gameServerPort");
        this.response.appendString("3002");
        this.response.appendString("socketPolicyPort");
        this.response.appendString("3000");
//        this.response.appendString("accessToken");
//        this.response.appendString(this.client.getHabbo().getHabboInfo().getUsername() + "-" + this.client.getHabbo().getHabboInfo().getLook());
//        this.response.appendString("gameServerHost");
//        this.response.appendString("localhost");
//        this.response.appendString("gameServerPort");
//        this.response.appendString("3002");
//        this.response.appendString("socketPolicyPort");
//        this.response.appendString("3003");
        return this.response;
    }
}