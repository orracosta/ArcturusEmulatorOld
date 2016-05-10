package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import gnu.trove.iterator.TIntIntIterator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

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
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_userinfo.forgot_username"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
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
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_userinfo.not_found").replace("%user%", params[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        String message = "Userinfo <b>" + habbo.getUsername() + "</b> (<b>" + habbo.getId() + "</b>)\r\n" +
                        "ID: " + habbo.getId() + "\r" +
                        "Username: " + habbo.getUsername() + "\r" +
                        "Motto: " + habbo.getMotto().replace("<", "[").replace(">", "]") + "\r" +
                        "Rank: " + Emulator.getGameEnvironment().getPermissionsManager().getRankName(habbo.getRank()) + " (" + habbo.getRank() + ") \r" +
                        "Online: " + (onlineHabbo == null ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" +
                        "Email: " + habbo.getMail() + "\r" +
                        ((Emulator.getGameEnvironment().getPermissionsManager().getPermissionsForRank(habbo.getRank()).contains("acc_hide_ip")) ? "" : "Register IP: " + habbo.getIpRegister() + "\r") +
                        ((Emulator.getGameEnvironment().getPermissionsManager().getPermissionsForRank(habbo.getRank()).contains("acc_hide_ip")) || onlineHabbo == null ? "" : "Current IP: " + onlineHabbo.getClient().getChannel().remoteAddress().toString() + "\r") +
                        "Credits: " + habbo.getCredits() + "\r";

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

        message +=                (onlineHabbo != null ? "Score: " + onlineHabbo.getHabboStats().achievementScore + "\r": "") +
                        "\r" +

                        (onlineHabbo != null ? "<b>Current Activity</b>\r" : "") +
                        (onlineHabbo != null ? "Room: " + onlineHabbo.getHabboInfo().getCurrentRoom().getName() + "("+onlineHabbo.getHabboInfo().getCurrentRoom().getId()+")\r" : "") +
                        (onlineHabbo != null ? "Respect Left: " + onlineHabbo.getHabboStats().respectPointsToGive + "\r" : "") +
                        (onlineHabbo != null ? "Pet Respect Left: " + onlineHabbo.getHabboStats().petRespectPointsToGive + "\r" : "") +
                        (onlineHabbo != null ? "Allow Trade: " + ((onlineHabbo.getHabboStats().allowTrade) ? Emulator.getTexts().getValue("generic.yes") : Emulator.getTexts().getValue("generic.no")) + "\r" : "") +
                        (onlineHabbo != null ? "Allow Follow: " + ((onlineHabbo.getHabboStats().blockFollowing) ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" : "") +
                        (onlineHabbo != null ? "Allow Friend Request: " + ((onlineHabbo.getHabboStats().blockFriendRequests) ? Emulator.getTexts().getValue("generic.no") : Emulator.getTexts().getValue("generic.yes")) + "\r" : "");


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

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            for(HabboInfo info : users)
            {
                message += info.getUsername() + ",\t" + info.getId() + ",\t" + format.format(new Date((long)info.getAccountCreated() * 1000L)) + ",\t" + format.format(new Date((long)info.getLastOnline() * 1000L)) + "\r";
            }
        }
                gameClient.sendResponse(new GenericAlertComposer(message));

        return true;
    }
}
