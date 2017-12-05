package com.eu.habbo.messages.incoming.rooms.items.youtube;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.YoutubeManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionYoutubeTV;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.items.youtube.YoutubeVideoComposer;

public class YoutubeRequestNextVideoEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();
        int next = this.packet.readInt();

        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            HabboItem item = this.client.getHabbo().getHabboInfo().getCurrentRoom().getHabboItem(itemId);

            if (item != null && item instanceof InteractionYoutubeTV)
            {
                YoutubeManager.YoutubeItem video = Emulator.getGameEnvironment().getItemManager().getYoutubeManager().getVideo(item.getBaseItem(), next);

                if (video != null)
                {
                    this.client.sendResponse(new YoutubeVideoComposer(itemId, video));
                }
            }
        }
    }
}