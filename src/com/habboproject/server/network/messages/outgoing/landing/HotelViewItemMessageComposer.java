package com.habboproject.server.network.messages.outgoing.landing;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class HotelViewItemMessageComposer extends MessageComposer {

    private final String campaignString;
    private final String campaignName;

    public HotelViewItemMessageComposer(String campaignString, String campaignName) {
        this.campaignString = campaignString;
        this.campaignName = campaignName;
    }

    @Override
    public short getId() {
        return Composers.CampaignMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeString(this.campaignString);
        msg.writeString(this.campaignName);
    }
}
