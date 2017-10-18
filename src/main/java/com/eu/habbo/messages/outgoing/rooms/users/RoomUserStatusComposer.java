package com.eu.habbo.messages.outgoing.rooms.users;

import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.Collection;

public class RoomUserStatusComposer extends MessageComposer
{
    private Collection<Habbo> habbos;
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

    public RoomUserStatusComposer(Collection<Habbo> habbos)
    {
        this.habbos = habbos;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomUserStatusComposer);
        if(this.roomUnits != null)
        {
            this.response.appendInt(this.roomUnits.size());
            for(RoomUnit roomUnit : this.roomUnits)
            {
                this.response.appendInt(roomUnit.getId());
                this.response.appendInt32(roomUnit.getPreviousLocation().x);
                this.response.appendInt32(roomUnit.getPreviousLocation().y);
                this.response.appendString(roomUnit.getPreviousLocation().getStackHeight() + "");
                this.response.appendInt(roomUnit.getHeadRotation().getValue());
                this.response.appendInt(roomUnit.getBodyRotation().getValue());

                final String[] status = {"/"};
                synchronized (roomUnit.getStatus())
                {
                    roomUnit.getStatus().forEachEntry(new TObjectObjectProcedure<String, String>()
                    {
                        @Override
                        public boolean execute(String key, String value)
                        {
                            status[0] = status[0] + key + " " + value + "/";
                            return true;
                        }
                    });
                }

                this.response.appendString(status[0]);
                roomUnit.setPreviousLocation(roomUnit.getCurrentLocation());
                roomUnit.getPreviousLocation().setStackHeight(roomUnit.getZ());
            }
        }
        else {
            synchronized (this.habbos)
            {
                this.response.appendInt(this.habbos.size());
                for (Habbo habbo : this.habbos)
                {
                    this.response.appendInt(habbo.getRoomUnit().getId());
                    this.response.appendInt32(habbo.getRoomUnit().getPreviousLocation().x);
                    this.response.appendInt32(habbo.getRoomUnit().getPreviousLocation().y);
                    this.response.appendString(habbo.getRoomUnit().getPreviousLocation().getStackHeight() + "");
                    this.response.appendInt(habbo.getRoomUnit().getHeadRotation().getValue());
                    this.response.appendInt(habbo.getRoomUnit().getBodyRotation().getValue());

                    final String[] status = {"/"};
                    synchronized (habbo.getRoomUnit().getStatus())
                    {
                        habbo.getRoomUnit().getStatus().forEachEntry(new TObjectObjectProcedure<String, String>()
                        {
                            @Override
                            public boolean execute(String key, String value)
                            {
                                status[0] = status[0] + key + " " + value + "/";
                                return true;
                            }
                        });
                    }
                    this.response.appendString(status[0]);
                    habbo.getRoomUnit().setPreviousLocation(habbo.getRoomUnit().getCurrentLocation());
                    habbo.getRoomUnit().getPreviousLocation().setStackHeight(habbo.getRoomUnit().getZ());
                }
            }
        }
        return this.response;
    }
}
