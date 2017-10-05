package com.habboproject.server.network.messages.outgoing.gamecenter;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.messages.composers.MessageComposer;

/**
 * Created by brend on 01/03/2017.
 */
public class LoadGameMessageComposer extends MessageComposer {
    private final int gameId;
    private final String host;
    private final int port;
    private final String server;
    private final String token;

    public LoadGameMessageComposer(int gameId, String host, int port, String server, String token) {
        this.gameId = gameId;
        this.host = host;
        this.port = port;
        this.server = server;
        this.token = token;
    }

    @Override
    public short getId() {
        return 56;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(gameId);
        msg.writeString("" + Comet.getTime());
        msg.writeString(Comet.getServer().getConfig().get("comet.gamecenter.image.path") + "/gamecenter_basejump/BaseJump.swf");
        msg.writeString("best");
        msg.writeString("showAll");
        msg.writeInt(60);
        msg.writeInt(10);
        msg.writeInt(0);
        msg.writeInt(5);
        msg.writeString("assetUrl");
        msg.writeString(Comet.getServer().getConfig().get("comet.gamecenter.image.path") + "/gamecenter_basejump/BasicAssets.swf");
        msg.writeString("habboHost");
        msg.writeString(server);
        msg.writeString("accessToken");
        msg.writeString(token); // <-- Must have the same value as the "fastfood_token"
        msg.writeString("gameServerHost");
        msg.writeString(host);
        msg.writeString("gameServerPort");
        msg.writeString(port);
        msg.writeString("socketPolicyPort");
        msg.writeString("30843");
    }
}
