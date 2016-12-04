package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.procedure.TObjectProcedure;

public class RoomCreditsCommand extends Command
{
    public RoomCreditsCommand()
    {
        super("cmd_roomcredits", Emulator.getTexts().getValue("commands.keys.cmd_roomcredits").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 2)
        {
            int amount;

            try
            {
                amount = Integer.valueOf(params[1]);
            }
            catch (Exception e)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_masscredits.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            if(amount != 0)
            {
                final String message = Emulator.getTexts().getValue("commands.generic.cmd_credits.received").replace("%amount%", amount + "");
                final int finalAmount = amount;
                gameClient.getHabbo().getHabboInfo().getCurrentRoom().getCurrentHabbos().forEachValue(new TObjectProcedure<Habbo>()
                {
                    @Override
                    public boolean execute(Habbo object)
                    {
                        object.giveCredits(finalAmount);
                        object.getClient().sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(message, object, object, RoomChatMessageBubbles.ALERT)));
                        return true;
                    }
                });
            }
            return true;
        }
        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_masscredits.invalid_amount"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        return true;
    }
}