package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.UpdateStackHeightTileHeightComposer;
import com.eu.habbo.util.pathfinding.PathFinder;
import com.eu.habbo.util.pathfinding.Tile;
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

                THashSet<Tile> tiles = PathFinder.getTilesAt(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

                if(stackerHeight >= 0)
                {
                    height = stackerHeight / 100.0D;
                }
                else
                {
                    for (Tile tile : tiles)
                    {
                        double tileHeight = this.client.getHabbo().getHabboInfo().getCurrentRoom().getTopHeightAt(tile.X, tile.Y);

                        if (tileHeight > height)
                        {
                            height = tileHeight;
                        }
                    }
                }

                for (Tile tile : tiles)
                {
                    tile.Z = height * 256.0D;
                }

                item.setZ(height);
                item.setExtradata((int)(height * 100) + "");

                System.out.println(height);
                System.out.println(item.getZ());
                System.out.println(item.getExtradata());
                this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().updateTiles(tiles);
                this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UpdateStackHeightComposer(tiles).compose());
            }
        }
    }
}
