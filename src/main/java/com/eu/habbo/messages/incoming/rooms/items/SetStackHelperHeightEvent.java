package com.eu.habbo.messages.incoming.rooms.items;

import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.UpdateStackHeightComposer;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemUpdateComposer;
import com.eu.habbo.messages.outgoing.rooms.items.UpdateStackHeightTileHeightComposer;
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
                if(stackerHeight >= 0)
                {
                    double height = stackerHeight / 100.0D;

                    //item.setExtradata(((height / 100.0D) + "").replace(",", "."));
                    //item.setExtradata((height + "").replace(".0", "").replace(",0", ""));
                    //item.setZ((height / 100.0D));

                    THashSet<Tile> tiles = Tile.getTilesAt(item.getX(), item.getY(), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

                    for (Tile t : tiles)
                    {
                        t.Z = height * 256.0D;
                    }

                    item.setZ(height);
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().updateTiles(tiles);
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UpdateStackHeightComposer(tiles).compose());
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                    //this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                }
                else
                {
                    if(stackerHeight == -1)
                    {
                        double height = this.client.getHabbo().getHabboInfo().getCurrentRoom().getTopHeightAt(item.getX(), item.getY());
                        item.setZ(height);
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().updateItem(item);
                        THashSet<Tile> tiles = new THashSet<Tile>();
                        tiles.add(new Tile(item.getX(), item.getY(), height * 256.0D));
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().updateTiles(tiles);
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UpdateStackHeightComposer(tiles).compose());
                        this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UpdateStackHeightTileHeightComposer(item, height).compose());
                    }
                }
            }
        }
    }
}
