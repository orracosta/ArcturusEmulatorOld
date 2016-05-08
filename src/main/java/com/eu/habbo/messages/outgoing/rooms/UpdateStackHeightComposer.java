package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

public class UpdateStackHeightComposer extends MessageComposer{

    private int x;
    private int y;
    private double height;

    private THashSet<Tile> updateTiles;

    public UpdateStackHeightComposer(int x, int y, double height)
    {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public UpdateStackHeightComposer(THashSet<Tile> updateTiles)
    {
        this.updateTiles = updateTiles;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateStackHeightComposer);
        if(this.updateTiles != null)
        {
            this.response.appendByte(this.updateTiles.size());
            for(Tile t : this.updateTiles)
            {
                this.response.appendByte(t.X);
                this.response.appendByte(t.Y);
                this.response.appendShort((int)(t.Z));
            }
        }
        else
        {
            this.response.appendByte(1);
            this.response.appendByte(this.x);
            this.response.appendByte(this.y);
            this.response.appendShort((int) (this.height));
        }
        return this.response;
    }
}
