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

public class SuperbanCommand extends Command
{
    public SuperbanCommand()
    {
        super("cmd_super_ban", Emulator.getTexts().getValue("commands.keys.cmd_super_ban").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        HabboInfo habbo = null;
        String reason = "";
        if (params.length >= 2)
        {
            Habbo h = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);

            if (h != null)
            {
                habbo = h.getHabboInfo();
            }
            else
            {
                habbo = HabboManager.getOfflineHabboInfo(params[1]);
            }
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
            if (habbo == gameClient.getHabbo().getHabboInfo())
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_super_ban.ban_self"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            if (habbo.getRank().getId() >= gameClient.getHabbo().getHabboInfo().getRank().getId())
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_ban.target_rank_higher"), RoomChatMessageBubbles.ALERT);
                return true;
            }

            count = Emulator.getGameEnvironment().getModToolManager().ban(habbo.getId(), gameClient.getHabbo(), reason, IPBanCommand.TEN_YEARS, ModToolBanType.SUPER, -1).size();
        }
        else
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_ban.user_offline"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_super_ban").replace("%count%", count + ""), RoomChatMessageBubbles.ALERT);

        return true;
    }
}