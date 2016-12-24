package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;

public class GiveRankCommand extends Command
{
    public GiveRankCommand()
    {
        super("cmd_give_rank", Emulator.getTexts().getValue("commands.keys.cmd_give_rank").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        int rankId = 0;
        String rankName = "";

        if (params.length == 1)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.missing_username") + Emulator.getTexts().getValue("commands.description.cmd_give_rank"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if (params.length == 2)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.missing_rank") + Emulator.getTexts().getValue("commands.description.cmd_give_rank"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if (params.length == 3)
        {
            try
            {
                rankId = Integer.valueOf(params[2]);
            }
            catch (Exception e)
            {
                rankId = -1;
                rankName = params[2];
            }

            if (rankId >= 0)
            {
                rankName = Emulator.getGameEnvironment().getPermissionsManager().getRankName(rankId);
            }

            if (rankName != null)
            {
                rankId = Emulator.getGameEnvironment().getPermissionsManager().getRankId(rankName);
            }

            if (rankId != -1 && !rankName.isEmpty())
            {
                if (rankId > gameClient.getHabbo().getHabboInfo().getRank())
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.higher").replace("%username%", params[1]).replace("%id%", rankName), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

                if (habbo != null)
                {
                    habbo.getHabboInfo().setRank(rankId);
                    habbo.getClient().sendResponse(new UserPerksComposer(habbo));

                    if (habbo.hasPermission("acc_supporttool"))
                    {
                        habbo.getClient().sendResponse(new ModToolComposer(habbo));
                    }

                    habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_give_rank.new_rank").replace("id", rankName)));
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_give_rank.updated").replace("%id%", rankName).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;

                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.user_offline").replace("%id%", rankName).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
            else
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.errors.cmd_give_rank.not_found").replace("%id%", rankName).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
        }

        return true;
    }
}