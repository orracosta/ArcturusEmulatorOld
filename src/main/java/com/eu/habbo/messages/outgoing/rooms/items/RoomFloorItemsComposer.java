package com.eu.habbo.messages.outgoing.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionGift;
import com.eu.habbo.habbohotel.items.interactions.InteractionMusicDisc;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.set.hash.THashSet;

import java.util.NoSuchElementException;

public class RoomFloorItemsComposer extends MessageComposer
{

    private final TIntObjectMap<String> furniOwnerNames;
    private final THashSet<HabboItem> items;

    public RoomFloorItemsComposer(TIntObjectMap<String> furniOwnerNames, THashSet<HabboItem> items)
    {
        this.furniOwnerNames = furniOwnerNames;
        this.items = items;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.RoomFloorItemsComposer);

        TIntObjectIterator<String> iterator = furniOwnerNames.iterator();

        this.response.appendInt(furniOwnerNames.size());
        for(int i = furniOwnerNames.size(); i-- > 0;)
        {
            try
            {
                iterator.advance();
                this.response.appendInt(iterator.key());
                this.response.appendString(iterator.value());
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        this.response.appendInt(items.size());

        for(HabboItem item : items)
        {
            item.serializeFloorData(this.response);
            this.response.appendInt(item instanceof InteractionGift ? ((((InteractionGift) item).getColorId() * 1000) + ((InteractionGift) item).getRibbonId()) : (item instanceof InteractionMusicDisc ? ((InteractionMusicDisc) item).getSongId() : 1));
            item.serializeExtradata(this.response);
            this.response.appendInt(-1);
            this.response.appendInt(item.getBaseItem().getStateCount() > 1 ? 1 : 0);
            this.response.appendInt(item.getUserId());
        }
        return this.response;
    }
}
