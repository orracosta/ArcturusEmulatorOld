package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 13-9-2014 19:52.
 */
public class RoomUserDataComposer extends MessageComposer {

    private final Habbo habbo;

    public RoomUserDataComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomUserDataComposer);
        this.response.appendInt32(this.habbo.getRoomUnit() == null ? -1 : this.habbo.getRoomUnit().getId());
        this.response.appendString(this.habbo.getHabboInfo().getLook());
        this.response.appendString(this.habbo.getHabboInfo().getGender() + "");
        this.response.appendString(this.habbo.getHabboInfo().getMotto());
        this.response.appendInt32(this.habbo.getHabboInfo().getAchievementScore());
        return this.response;
    }
}
