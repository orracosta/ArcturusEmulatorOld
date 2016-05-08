package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.inventory.BadgesComponent;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;

import java.util.Collection;
import java.util.Map;

/**
 * Created on 11-1-2016 19:16.
 */
public class MassBadgeCommand extends Command
{
    public MassBadgeCommand()
    {
        super.permission = "cmd_massbadge";
        super.keys = Emulator.getTexts().getValue("commands.keys.cmd_massbadge").split(";");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            String badge;

            badge = params[1];

            if(!badge.isEmpty())
            {
                for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
                {
                    Habbo habbo = set.getValue();

                    if(!habbo.getHabboInventory().getBadgesComponent().hasBadge(badge))
                    {
                        HabboBadge b = BadgesComponent.createBadge(badge, habbo);

                        if (b != null)
                        {
                            habbo.getClient().sendResponse(new AddUserBadgeComposer(b));

                            if (habbo.getHabboInfo().getCurrentRoom() != null)
                            {
                                habbo.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.generic.cmd_badge.received"), habbo, habbo, RoomChatMessageBubbles.ALERT)));
                            }
                        }
                    }
                }
            }
            return true;
        }
        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_massbadge.no_badge"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        return true;
    }
}
