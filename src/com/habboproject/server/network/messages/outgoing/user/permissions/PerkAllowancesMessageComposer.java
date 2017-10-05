package com.habboproject.server.network.messages.outgoing.user.permissions;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class PerkAllowancesMessageComposer extends MessageComposer {
    private final int rank;

    public PerkAllowancesMessageComposer(final int rank) {
        this.rank = rank;
    }

    @Override
    public short getId() {
        return Composers.PerkAllowancesMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(17);

        msg.writeString("USE_GUIDE_TOOL");
        msg.writeString("");
        msg.writeBoolean(false);

        msg.writeString("GIVE_GUIDE_TOURS");
        msg.writeString("requirement.unfulfilled.helper_le");
        msg.writeBoolean(false);

        msg.writeString("JUDGE_CHAT_REVIEWS");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("VOTE_IN_COMPETITIONS");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("CALL_ON_HELPERS");
        msg.writeString("");
        msg.writeBoolean(false);

        msg.writeString("CITIZEN");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("TRADE");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("HEIGHTMAP_EDITOR_BETA");
        msg.writeString("");
        msg.writeBoolean(false);

        msg.writeString("EXPERIMENTAL_CHAT_BETA");
        msg.writeString("requirement.unfulfilled.helper_level_2");
        msg.writeBoolean(true);

        msg.writeString("EXPERIMENTAL_TOOLBAR");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("BUILDER_AT_WORK");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("NAVIGATOR_PHASE_ONE_2014");
        msg.writeString("");
        msg.writeBoolean(false);

        msg.writeString("CAMERA");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("NAVIGATOR_PHASE_TWO_2014");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("MOUSE_ZOOM");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("NAVIGATOR_ROOM_THUMBNAIL_CAMERA");
        msg.writeString("");
        msg.writeBoolean(true);

        msg.writeString("HABBO_CLUB_OFFER_BETA");
        msg.writeString("");
        msg.writeBoolean(true);
    }
}

