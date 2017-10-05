package com.habboproject.server.game.commands.staff.muting;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class UnmuteCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) {
            return;
        }

        int playerId = PlayerManager.getInstance().getPlayerIdByUsername(params[0]);

        if (playerId != -1) {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

            if (session != null) {
                session.send(new AdvancedAlertMessageComposer(Locale.get("command.unmute.unmuted")));

                if (BanManager.getInstance().isMuted(playerId)) {
                    BanManager.getInstance().unmute(playerId);
                } else {
                    PlayerEntity entity = session.getPlayer().getEntity();

                    if (entity != null && entity.isRoomMuted()) {
                        entity.setRoomMuted(false);
                    }
                }
            }
        }
    }

    @Override
    public String getPermission() {
        return "unmute_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.unmute.name");
    }
}
