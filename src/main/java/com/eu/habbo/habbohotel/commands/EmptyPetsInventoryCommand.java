package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;

public class EmptyPetsInventoryCommand extends Command
{
    public EmptyPetsInventoryCommand()
    {
        super("cmd_empty_pets", Emulator.getTexts().getValue("commands.keys.cmd_empty_pets").split(";"));
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
                    gameClient.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.succes.cmd_empty_pets.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes"))));
                }
                else
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_empty_pets.verify").replace("%generic.yes%", Emulator.getTexts().getValue("generic.yes")), RoomChatMessageBubbles.ALERT);
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
                TIntObjectHashMap<AbstractPet> pets = new TIntObjectHashMap<>();
                pets.putAll(habbo.getInventory().getPetsComponent().getPets());
                habbo.getInventory().getPetsComponent().getPets().clear();
                pets.forEachValue(new TObjectProcedure<AbstractPet>()
                {
                    @Override
                    public boolean execute(AbstractPet object)
                    {
                        Emulator.getGameEnvironment().getPetManager().deletePet(object);
                        return true;
                    }
                });

                habbo.getClient().sendResponse(new InventoryRefreshComposer());
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_empty_pets.cleared"), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }
}