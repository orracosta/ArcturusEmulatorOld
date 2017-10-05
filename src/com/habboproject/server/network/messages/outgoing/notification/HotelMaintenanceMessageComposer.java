package com.habboproject.server.network.messages.outgoing.notification;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class HotelMaintenanceMessageComposer extends MessageComposer {
    private final int hour;
    private final int minute;
    private final boolean logout;

    public HotelMaintenanceMessageComposer(final int hour, final int minute, final boolean logout) {
        this.hour = hour;
        this.minute = minute;
        this.logout = logout;
    }

    @Override
    public short getId() {
        return Composers.MaintenanceStatusMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.hour);
        msg.writeInt(this.minute);
        msg.writeBoolean(logout);

    }
}
