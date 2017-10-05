package com.habboproject.server.network.messages.outgoing.moderation;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.moderation.types.tickets.HelpTicket;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class ModToolMessageComposer extends MessageComposer {

    public ModToolMessageComposer() {

    }

    @Override
    public short getId() {
        return Composers.ModeratorInitMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(ModerationManager.getInstance().getTickets().size());
        for (HelpTicket helpTicket : ModerationManager.getInstance().getTickets().values()) {
            helpTicket.compose(msg);
        }

        msg.writeInt(ModerationManager.getInstance().getUserPresets().size());
        for (String preset : ModerationManager.getInstance().getUserPresets()) {
            msg.writeString(preset);
        }

        msg.writeInt(0);

        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(true);
        msg.writeBoolean(true);

        msg.writeInt(ModerationManager.getInstance().getRoomPresets().size());
        for (String preset : ModerationManager.getInstance().getRoomPresets()) {
            msg.writeString(preset);
        }

        msg.writeInt(0);
    }
}
