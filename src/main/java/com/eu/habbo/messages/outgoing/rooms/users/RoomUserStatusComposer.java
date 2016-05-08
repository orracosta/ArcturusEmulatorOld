package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

/**
 * Created on 14-9-2014 14:34.
 */
public class RoomUserStatusComposer extends MessageComposer {

    private TIntObjectMap<Habbo> habbos;
    private THashSet<RoomUnit> roomUnits;

    public RoomUserStatusComposer(RoomUnit roomUnit)
    {
        this.roomUnits = new THashSet<RoomUnit>();
        this.roomUnits.add(roomUnit);
    }

    public RoomUserStatusComposer(THashSet<RoomUnit> roomUnits, boolean value)
    {
        this.roomUnits = roomUnits;
    }

    public RoomUserStatusComposer(TIntObjectMap<Habbo> habbos)
    {
        this.habbos = habbos;
    }

    @Override
    public ServerMessage compose() {
        this.response.init(Outgoing.RoomUserStatusComposer);
        if(this.roomUnits != null)
        {
            this.response.appendInt32(this.roomUnits.size());
            for(RoomUnit roomUnit : this.roomUnits)
            {
                this.response.appendInt32(roomUnit.getId());
                this.response.appendInt32(roomUnit.getX());
                this.response.appendInt32(roomUnit.getY());
                this.response.appendString(roomUnit.getZ() + "");
                this.response.appendInt32(roomUnit.getHeadRotation().getValue());
                this.response.appendInt32(roomUnit.getBodyRotation().getValue());

                String status = "/";

                for (Map.Entry<String, String> keys : roomUnit.getStatus().entrySet())
                {
                    status = status + keys.getKey() + " " + keys.getValue() + "/";
                }

                this.response.appendString(status);
            }
        }
        else {
            this.response.appendInt32(this.habbos.size());
            for (Habbo habbo : this.habbos.valueCollection()) {
                this.response.appendInt32(habbo.getRoomUnit().getId());
                this.response.appendInt32(habbo.getRoomUnit().getX());
                this.response.appendInt32(habbo.getRoomUnit().getY());
                this.response.appendString(habbo.getRoomUnit().getZ() + "");
                this.response.appendInt32(habbo.getRoomUnit().getHeadRotation().getValue());
                this.response.appendInt32(habbo.getRoomUnit().getBodyRotation().getValue());

                String status = "/";
                for (Map.Entry<String, String> keys : habbo.getRoomUnit().getStatus().entrySet()) {
                    status = status + keys.getKey() + " " + keys.getValue() + "/";
                }
                this.response.appendString(status);
            }
        }
        return this.response;
    }
}
