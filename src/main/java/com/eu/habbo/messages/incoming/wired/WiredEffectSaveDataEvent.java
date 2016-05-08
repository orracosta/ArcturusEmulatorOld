package com.eu.habbo.messages.incoming.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.wired.WiredSavedComposer;

/**
 * Created on 14-12-2014 13:10.
 */
public class WiredEffectSaveDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        int itemId = this.packet.readInt();

        Room room = this.client.getHabbo().getHabboInfo().getCurrentRoom();

        if(room != null)
        {
            if(room.getOwnerId() == this.client.getHabbo().getHabboInfo().getId() || this.client.getHabbo().hasPermission("acc_anyroomowner") || this.client.getHabbo().hasPermission("acc_moverotate"))
            {
                InteractionWiredEffect effect = room.getRoomSpecialTypes().getEffect(itemId);

                if(effect != null)
                {
                    if(effect instanceof WiredEffectGiveReward && !this.client.getHabbo().hasPermission("acc_superwired"))
                    {
                        this.client.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage("U cannot do this.", this.client.getHabbo(), this.client.getHabbo(), RoomChatMessageBubbles.ALERT)));
                        return;
                    }

                    if(effect.saveData(this.packet))
                    {
                        this.client.sendResponse(new WiredSavedComposer());

                        effect.needsUpdate(true);
                        Emulator.getThreading().run(effect);
                    }
                }
            }
        }
    }
}
