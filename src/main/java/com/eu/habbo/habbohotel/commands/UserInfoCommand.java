package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.modtool.ModToolBan;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntIntIterator;

import java.text.SimpleDateFormat;
import java.util.*;

public class UserInfoCommand extends Command
{
    public UserInfoCommand()
    {
        super("cmd_userinfo", Emulator.getTexts().getValue("commands.keys.cmd_userinfo").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(params.length < 2)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_userinfo.forgot_username"), RoomChatMessageBubbles.ALERT);
            return true;
        }

        Habbo onlineHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(params[1]);
        HabboInfo habbo = (onlineHabbo != null ? onlineHabbo.getHabboInfo() : null);

        if(habbo == null)
        {
            habbo = HabboManager.getOfflineHabboInfo(params[1]);
        }

        if(habbo == null)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_userinfo.not_found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);
            return true;
        }

        String message = Emulator.getTexts().getValue("command.cmd_userinfo.userinfo") + ": " + " <b>" + habbo.getUsername() + "</b> (<b>" + habbo.getId() + "</b>)\r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.user_id") + ": " + habbo.getId() + "\r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.user_name") + ": " + habbo.getUsername() + "\r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.motto") + ": " + habbo.getMotto().replace("<", "[").replace(">", "]") + "\r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.rank") + ": " +habbo.getRank().getName() + " (" + habbo.getRank().getId() + ") \r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.online") + ": " + (onlineHabbo == null ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" +
                Emulator.getTexts().getValue("command.cmd_userinfo.email") + ": " + habbo.getMail() + "\r" +
                        ((habbo.getRank().hasPermission("acc_hide_ip", true))?"" : Emulator.getTexts().getValue("command.cmd_userinfo.ip_register") + ": "+ habbo.getIpRegister() + "\r") +
                        ((habbo.getRank().hasPermission("acc_hide_ip", true)) || onlineHabbo == null ? "" : Emulator.getTexts().getValue("command.cmd_userinfo.ip_current") + ": " + onlineHabbo.getClient().getChannel().remoteAddress().toString() + "\r") +
                        (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.achievement_score") + ": " + onlineHabbo.getHabboStats().achievementScore + "\r": "");

        ModToolBan ban = Emulator.getGameEnvironment().getModToolManager().checkForBan(habbo.getId());

        message += Emulator.getTexts().getValue("command.cmd_userinfo.total_bans") + ": " + Emulator.getGameEnvironment().getModToolManager().totalBans(habbo.getId()) + "\r";
        message += Emulator.getTexts().getValue("command.cmd_userinfo.banned") + ": " + Emulator.getTexts().getValue(ban != null ? "generic.yes" : "generic.no") + "\r\r";
        if (ban != null)
        {
            message += "<b>" + Emulator.getTexts().getValue("command.cmd_userinfo.ban_info") + "</b>\r";
            message += ban.listInfo() + "\r";
        }

        message += "<b>" + Emulator.getTexts().getValue("command.cmd_userinfo.currencies") + "</b>\r";
        message += Emulator.getTexts().getValue("command.cmd_userinfo.credits") + ": " + habbo.getCredits() + "\r";
        TIntIntIterator iterator = habbo.getCurrencies().iterator();

        for(int i = habbo.getCurrencies().size(); i-- > 0;)
        {
            try
            {
                iterator.advance();
            }
            catch (Exception e)
            {
                break;
            }

            message += (Emulator.getTexts().getValue("seasonal.name." + iterator.key()) + ": " + iterator.value() + "\r");
        }
        message += "\r" +

                (onlineHabbo != null ? "<b>" + Emulator.getTexts().getValue("command.cmd_userinfo.current_activity") + "</b>\r" : "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.room") + ": " + (onlineHabbo.getHabboInfo().getCurrentRoom() != null ? onlineHabbo.getHabboInfo().getCurrentRoom().getName() + "(" + onlineHabbo.getHabboInfo().getCurrentRoom().getId() + ")\r" : "-" ): "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.respect_left") + ": " + onlineHabbo.getHabboStats().respectPointsToGive + "\r" : "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.pet_respect_left") + ": " + onlineHabbo.getHabboStats().petRespectPointsToGive + "\r" : "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.allow_trade") + ": " + ((onlineHabbo.getHabboStats().allowTrade) ? Emulator.getTexts().getValue("generic.yes") : Emulator.getTexts().getValue("generic.no")) + "\r" : "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.allow_follow") + ": " + ((onlineHabbo.getHabboStats().blockFollowing) ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" : "") +
                (onlineHabbo != null ? Emulator.getTexts().getValue("command.cmd_userinfo.allow_friend_request") + ": " + ((onlineHabbo.getHabboStats().blockFriendRequests) ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" : "");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map.Entry<Integer, String>> nameChanges = Emulator.getGameEnvironment().getHabboManager().getNameChanges(habbo.getId(), 3);
        if (!nameChanges.isEmpty())
        {
            message += "\r<b>Latest name changes:<b><br/>";
            for (Map.Entry<Integer, String> entry : nameChanges)
            {
                message += format.format(new Date((long)entry.getKey() * 1000L)) + " : " + entry.getValue() + "<br/>";
            }
        }

        if(onlineHabbo != null)
        {
            message += "\r" +
                    "<b>Other accounts (";

            ArrayList<HabboInfo> users = Emulator.getGameEnvironment().getHabboManager().getCloneAccounts(onlineHabbo, 10);
            Collections.sort(users, new Comparator<HabboInfo>()
            {
                @Override
                public int compare(HabboInfo o1, HabboInfo o2)
                {
                    return o1.getId() - o2.getId();
                }
            });

            message += users.size() + "):</b>\r";


            message += "<b>Username,\tID,\tDate register,\tDate last online</b>\r";

            for(HabboInfo info : users)
            {
                message += info.getUsername() + ",\t" + info.getId() + ",\t" + format.format(new Date((long)info.getAccountCreated() * 1000L)) + ",\t" + format.format(new Date((long)info.getLastOnline() * 1000L)) + "\r";
            }
        }
                gameClient.sendResponse(new GenericAlertComposer(message));

        return true;
    }
}
