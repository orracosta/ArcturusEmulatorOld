package com.habboproject.server.network.messages.incoming.user.details;

import com.habboproject.server.game.players.components.types.settings.VolumeData;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.player.PlayerDao;
import com.habboproject.server.utilities.JsonFactory;


public class UpdateAudioSettingsMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null) {
            return;
        }

        int systemVolume = msg.readInt();
        int furniVolume = msg.readInt();
        int traxVolume = msg.readInt();

        if (client.getPlayer().getSettings().getVolumes().getSystemVolume() == systemVolume
                && client.getPlayer().getSettings().getVolumes().getFurniVolume() == furniVolume
                && client.getPlayer().getSettings().getVolumes().getTraxVolume() == traxVolume) {
            return;
        }

        PlayerDao.saveVolume(JsonFactory.getInstance().toJson(new VolumeData(systemVolume, furniVolume, traxVolume)), client.getPlayer().getId());
    }
}
