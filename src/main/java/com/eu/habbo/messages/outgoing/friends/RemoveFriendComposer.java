package com.eu.habbo.messages.outgoing.friends;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.list.array.TIntArrayList;

public class RemoveFriendComposer extends MessageComposer
{
    private TIntArrayList unfriendIds;

    public RemoveFriendComposer(TIntArrayList unfriendIds)
    {
        this.unfriendIds = unfriendIds;
    }

    public RemoveFriendComposer(int i)
    {
        this.unfriendIds = new TIntArrayList();
        this.unfriendIds.add(i);
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UpdateFriendComposer);

        this.response.appendInt(0);
        this.response.appendInt(this.unfriendIds.size());
        for(int i = 0; i < this.unfriendIds.size(); i++)
        {
            this.response.appendInt(-1);
            this.response.appendInt(unfriendIds.get(i));
        }

        return this.response;
    }
}
