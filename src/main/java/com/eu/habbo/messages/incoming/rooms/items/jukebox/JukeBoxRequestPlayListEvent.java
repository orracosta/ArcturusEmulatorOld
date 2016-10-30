package com.eu.habbo.messages.incoming.rooms.items.jukebox;

import com.eu.habbo.habbohotel.items.SoundTrack;
import com.eu.habbo.habbohotel.items.interactions.InteractionJukeBox;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.jukebox.JukeBoxPlayListComposer;
import gnu.trove.set.hash.THashSet;

public class JukeBoxRequestPlayListEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null)
        {
            HabboItem jukeBox = null;
            for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionJukeBox.class))
            {
                jukeBox = item;
                break;
            }

            if (jukeBox != null)
            {
                if (jukeBox instanceof InteractionJukeBox)
                {
                    this.client.sendResponse(new JukeBoxPlayListComposer((InteractionJukeBox) jukeBox, room));
                }
            }
        }
    }
}
