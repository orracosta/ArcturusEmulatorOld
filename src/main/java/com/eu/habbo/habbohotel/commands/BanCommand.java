package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

public class BanCommand extends Command
{
    public BanCommand()
    {
        super("cmd_ban", Emulator.getTexts().getValue("commands.keys.cmd_ban").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length < 2)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.forgot_user"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if(params.length < 3)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.forgot_time"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        int banTime;
        try
        {
            banTime = Integer.valueOf(params[2]);
        }
        catch (Exception e)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.invalid_time"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if(banTime < 600)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.time_to_short"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if(params[1].toLowerCase().equals(gameClient.getHabbo().getHabboInfo().getUsername().toLowerCase()))
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.ban_self"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        Habbo t = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

        HabboInfo target = null;
        if (t != null)
        {
            target = t.getHabboInfo();
        }
        else
        {
            target = HabboManager.getOfflineHabboInfo(params[1]);
        }

        if (target == null)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.user_offline"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if (target.getRank().getId() >= gameClient.getHabbo().getHabboInfo().getRank().getId())
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_ban.target_rank_higher"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }


        String reason = "";

        if(params.length > 3)
        {
            for(int i = 3; i < params.length; i++)
            {
                reason += params[i] + " ";
            }
        }


        try
        {
            ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().ban(target.getId(), gameClient.getHabbo(), reason, banTime, ModToolBanType.ACCOUNT, -1).get(0);
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_ban.ban_issued").replace("%user%", target.getUsername()).replace("%time%", ban.expireDate - Emulator.getIntUnixTimestamp() + "").replace("%reason%", ban.reason), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }
        catch (Exception e)
        {

        }
        return true;
    }
}
