package com.habboproject.server.network.messages.outgoing.user.details;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.types.PlayerSettings;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class PlayerSettingsMessageComposer extends MessageComposer {
    private final PlayerSettings playerSettings;

    public PlayerSettingsMessageComposer(final PlayerSettings playerSettings) {
        this.playerSettings = playerSettings;
    }

    @Override
    public short getId() {
        return Composers.SoundSettingsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.playerSettings.getVolumes().getSystemVolume());
        msg.writeInt(this.playerSettings.getVolumes().getFurniVolume());
        msg.writeInt(this.playerSettings.getVolumes().getTraxVolume());
        msg.writeBoolean(this.playerSettings.isUseOldChat()); // old chat enabled?
        msg.writeBoolean(this.playerSettings.isIgnoreInvites()); // ignore room invites
        msg.writeBoolean(false); //disable_room_camera_follow_checkbox
        msg.writeInt(0); //??
        msg.writeInt(0); //??
    }
}
