package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class SuperbanCommand extends Command
{
    public SuperbanCommand()
    {
        super("cmd_super_ban", Emulator.getTexts().getValue("commands.keys.cmd_super_ban").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Habbo habbo = null;
        String reason = "";
        if (params.length >= 2)
        {
            habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);
        }

        if (params.length > 2)
        {
            for (int i = 2; i < params.length; i++)
            {
                reason += params[i];
                reason += " ";
            }
        }

        int count = 0;
        if (habbo != null)
        {
            if (habbo == gameClient.getHabbo())
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_super_ban.ban_self"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            int time = Emulator.getIntUnixTimestamp() + IPBanCommand.TEN_YEARS;
            Emulator.getGameEnvironment().getModToolManager().createBan(habbo, gameClient.getHabbo(), time, reason, "super");
            count++;
            for (Habbo h : Emulator.getGameServer().getGameClientManager().getHabbosWithMachineId(habbo.getClient().getMachineId()))
            {
                if (h != null)
                {
                    count++;
                    Emulator.getGameEnvironment().getModToolManager().createBan(h, gameClient.getHabbo(), time, reason, "super");
                }
            }

            for (Habbo h : Emulator.getGameServer().getGameClientManager().getHabbosWithIP(habbo.getHabboInfo().getIpLogin()))
            {
                if (h != null)
                {
                    count++;
                    Emulator.getGameEnvironment().getModToolManager().createBan(h, gameClient.getHabbo(), time, reason, "super");
                }
            }
        }

        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_super_ban").replace("%count%", count + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));

        return true;
    }
}