package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;

public class KissCommand extends Command
{
    public KissCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length >= 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(strings[1]);

            if (habbo != null)
            {
                if (habbo != gameClient.getHabbo())
                {
                    if (habbo.getRoomUnit().getCurrentLocation().distance(gameClient.getHabbo().getRoomUnit().getCurrentLocation()) <= 1.5)
                    {
                        habbo.getRoomUnit().lookAtPoint(gameClient.getHabbo().getRoomUnit().getCurrentLocation());
                        gameClient.getHabbo().getRoomUnit().lookAtPoint(habbo.getRoomUnit().getCurrentLocation());
                        habbo.talk(Emulator.getTexts().getValue("essentials.kissedmessages.kissedperson").replace("%kisser%", gameClient.getHabbo().getHabboInfo().getUsername()).replace("%kissedperson%", habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.HEARTS);
                        gameClient.getHabbo().talk(Emulator.getTexts().getValue("essentials.kissedmessages.kisser").replace("%kisser%", gameClient.getHabbo().getHabboInfo().getUsername()).replace("%kissedperson%", habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.HEARTS);
                    }
                    else
                    {
                        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.kissmessages.tofar").replace("%kissedperson%", habbo.getHabboInfo().getUsername()));
                    }
                }
            }
        }

        return true;
    }
}