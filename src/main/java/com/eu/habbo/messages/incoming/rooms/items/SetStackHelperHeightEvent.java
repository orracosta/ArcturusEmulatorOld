package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import gnu.trove.set.hash.THashSet;

public class SetStackHelperHeightEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        if(this.client.getHabbo().getHabboInfo().getCurrentRoom() == null)
            return;

        if(this.client.getHabbo().getHabboInfo().getId() == this.client.getHabbo().getHabboInfo().getCurrentRoom().getOwnerId() || this.client.getHabbo().getHabboInfo().getCurrentRoom().hasRights(this.client.getHabbo()))
        {
            HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

            if(item instanceof InteractionStackHelper)
            {
                int stackerHeight = this.packet.readInt();

                item.setExtradata(stackerHeight + "");

                double height = 0;

                Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();
                THashSet<RoomTile> tiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

                if(stackerHeight >= 0)
                {
                    height = stackerHeight / 100.0D;
                }
                else
                {
                    for (RoomTile tile : tiles)
                    {
                        double tileHeight = this.client.getHabbo().getHabboInfo().getCurrentRoom().getTopHeightAt(tile.x, tile.y);

                        if (tileHeight > height)
                        {
                            height = tileHeight;
                        }
                    }
                }

                for (RoomTile tile : tiles)
                {
                    if (height < tile.z)
                    {
                        height = tile.z;
                    }
                }

                for (RoomTile tile : tiles)
                {
                    tile.setStackHeight(height);
                }

                item.setZ(height);
                item.setExtradata((int)(height * 100) + "");
                item.needsUpdate(true);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().updateTiles(tiles);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UpdateStackHeightComposer(tiles).compose());
            }
        }
    }
}
