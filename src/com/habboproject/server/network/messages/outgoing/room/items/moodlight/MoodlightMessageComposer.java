package com.habboproject.server.network.messages.outgoing.room.items.moodlight;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.items.data.MoodlightPresetData;
import com.habboproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class MoodlightMessageComposer extends MessageComposer {
    private final MoodlightWallItem moodlightWallItem;

    public MoodlightMessageComposer(final MoodlightWallItem moodlightWallItem) {
        this.moodlightWallItem = moodlightWallItem;
    }

    @Override
    public short getId() {
        return Composers.MoodlightConfigMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.moodlightWallItem.getMoodlightData().getPresets().size());
        msg.writeInt(this.moodlightWallItem.getMoodlightData().getActivePreset());

        int id = 1;

        for (MoodlightPresetData data : this.moodlightWallItem.getMoodlightData().getPresets()) {
            msg.writeInt(id++);
            msg.writeInt(data.backgroundOnly ? 2 : 1);
            msg.writeString(data.colour);
            msg.writeInt(data.intensity);
        }
    }
}
