package com.habboproject.server.game.commands.staff.banning;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.types.BanType;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.sessions.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class IpBanCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length < 2) {
            return;
        }

        String username = params[0];
        int length = StringUtils.isNumeric(params[1]) ? Integer.parseInt(params[1]) : 0;

        Session user = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        if (user == null) {
            // TODO: Use the "player_access" table to allow you to IP ban a user that's not online
            return;
        }

        if (user == client || !user.getPlayer().getPermissions().getRank().bannable()) {
            return;
        }

        long expire = Comet.getTime() + (length * 3600);

        String ipAddress = user.getIpAddress();

        if (BanManager.getInstance().hasBan(ipAddress, BanType.IP)) {
            sendNotif("IP: " + ipAddress + " is already banned.", client);
            return;
        }

        BanManager.getInstance().banPlayer(BanType.IP, user.getIpAddress(), length, expire, params.length > 2 ? this.merge(params, 2) : "", client.getPlayer().getId());

        sendNotif("User has been IP banned (IP: " + ipAddress + ")", client);

        List<Integer> playerIds = PlayerManager.getInstance().getPlayerIdsByIpAddress(ipAddress);

        for(int playerId : playerIds) {
            Session player = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if(player != null) {
                player.disconnect("banned");
            }
        }

        playerIds.clear();
    }

    @Override
    public String getPermission() {
        return "ipban_command";
    }

    @Override
    public String getDescription() {
        return "command.ipban.description";
    }
}
