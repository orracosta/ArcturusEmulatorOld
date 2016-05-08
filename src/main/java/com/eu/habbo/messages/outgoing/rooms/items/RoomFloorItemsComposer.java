package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;

import java.util.NoSuchElementException;

public class RoomFloorItemsComposer extends MessageComposer {

    private final Room room;

    public RoomFloorItemsComposer(Room room)
    {
        this.room = room;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomFloorItemsComposer);

        TIntObjectMap<String> furniOwnerNames = this.room.getFurniOwnerNames();
        TIntObjectIterator<String> iterator = furniOwnerNames.iterator();

        this.response.appendInt32(furniOwnerNames.size());
        for(int i = furniOwnerNames.size(); i-- > 0;)
        {
            try
            {
                iterator.advance();
                this.response.appendInt32(iterator.key());
                this.response.appendString(iterator.value());
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        THashSet<HabboItem> items = this.room.getFloorItems();

        this.response.appendInt32(items.size());

        for(HabboItem item : items)
        {
            item.serializeFloorData(this.response);
            this.response.appendInt32(item instanceof InteractionGift ? ((((InteractionGift) item).getColorId() * 1000) + ((InteractionGift) item).getRibbonId()) : (item instanceof InteractionMusicDisc ? ((InteractionMusicDisc)item).getSongId() : 1));
            item.serializeExtradata(this.response);
            this.response.appendInt32(-1);
            this.response.appendInt32(item.getBaseItem().getStateCount() > 1 ? 1 : 0);
            this.response.appendInt32(this.room.getOwnerId());
        }
        return this.response;
    }
}
