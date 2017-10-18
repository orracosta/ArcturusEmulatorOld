package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

public class EmptyBotsInventoryCommand extends Command
{
    public EmptyBotsInventoryCommand()
    {
        super("cmd_empty_bots", Emulator.getTexts().getValue("commands.keys.cmd_empty_bots").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length == 1 || (params.length >= 2 && !params[1].equals(Emulator.getTexts().getValue("generic.yes"))))
        {
            if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
            {
                if(gameClient.getHabbo().getHabboInfo().getCurrentRoom().getUserCount() > 10)
                {
                    gameClient.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.succes.cmd_empty_bots.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes"))));
                }
                else
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_empty_bots.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes")), RoomChatMessageBubbles.ALERT);
                }
            }

            return true;
        }

        if(params.length >= 2 && params[1].equalsIgnoreCase(Emulator.getTexts().getValue("generic.yes")))
        {
            Habbo habbo = gameClient.getHabbo();
            if (params.length == 3 && gameClient.getHabbo().hasPermission("acc_empty_others"))
            {
                habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[2]);
            }

            if (habbo != null)
            {
                TIntObjectHashMap<Bot> bots = new TIntObjectHashMap<>();
                bots.putAll(habbo.getInventory().getBotsComponent().getBots());
                habbo.getInventory().getBotsComponent().getBots().clear();
                bots.forEachValue(new TObjectProcedure<Bot>()
                {
                    @Override
                    public boolean execute(Bot object)
                    {
                        Emulator.getGameEnvironment().getBotManager().deleteBot(object);
                        return true;
                    }
                });

                habbo.getClient().sendResponse(new InventoryRefreshComposer());

                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_empty_bots.cleared"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}