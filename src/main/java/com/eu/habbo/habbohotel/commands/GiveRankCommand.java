package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Rank;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.messages.outgoing.users.UserPerksComposer;
import org.apache.commons.lang3.StringUtils;

public class GiveRankCommand extends Command
{
    public GiveRankCommand()
    {
        super("cmd_give_rank", Emulator.getTexts().getValue("commands.keys.cmd_give_rank").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        Rank rank = null;
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
            if (StringUtils.isNumeric(params[2]))
            {
                int rankId = Integer.valueOf(params[2]);
                if (Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId))
                rank = Emulator.getGameEnvironment().getPermissionsManager().getRank(rankId);
            }
            else
            {
                rank = Emulator.getGameEnvironment().getPermissionsManager().getRank(params[2]);
            }

            if (rank != null)
            {
                if (rank.getId() > gameClient.getHabbo().getHabboInfo().getRank().getId())
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.higher").replace("%username%", params[1]).replace("%id%", rank.getName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

                if (habbo != null)
                {
                    if (habbo.getHabboInfo().getRank().getId() > gameClient.getHabbo().getHabboInfo().getRank().getId())
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.higher.other").replace("%username%", params[1]).replace("%id%", rank.getName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                        return true;
                    }

                    habbo.getHabboInfo().setRank(rank);
                    habbo.getHabboInfo().run();
                    habbo.getClient().sendResponse(new UserPerksComposer(habbo));

                    if (habbo.hasPermission("acc_supporttool"))
                    {
                        habbo.getClient().sendResponse(new ModToolComposer(habbo));
                    }

                    habbo.getClient().sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("commands.generic.cmd_give_rank.new_rank").replace("id", rank.getName())));
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_give_rank.updated").replace("%id%", rank.getName()).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;

                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_give_rank.user_offline").replace("%id%", rank.getName()).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
        }

        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.errors.cmd_give_rank.not_found").replace("%id%", params[2]).replace("%username%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        return true;
    }
}