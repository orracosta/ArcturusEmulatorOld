package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolBanType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;

public class ModToolSanctionBanEvent extends MessageHandler
{
    public final int BAN_18_HOURS = 3;
    public final int BAN_7_DAYS = 4;
    public final int BAN_30_DAYS_STEP_1 = 5;
    public final int BAN_30_DAYS_STEP_2 = 7;
    public final int BAN_100_YEARS = 6;
    public final int BAN_AVATAR_ONLY_100_YEARS = 106;

    public final int DAY_IN_SECONDS = 24 * 60 * 60;

    @Override
    public void handle() throws Exception
    {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int cfhTopic = this.packet.readInt();
        int banType = this.packet.readInt();
        boolean unknown = this.packet.readBoolean();

        int duration = 0;

        switch (banType)
        {
            case BAN_18_HOURS: duration = 18 * 60 * 60; break;
            case BAN_7_DAYS: duration = 7 * DAY_IN_SECONDS; break;
            case BAN_30_DAYS_STEP_1:
            case BAN_30_DAYS_STEP_2:
                duration = 30 * DAY_IN_SECONDS; break;
            case BAN_100_YEARS:
            case BAN_AVATAR_ONLY_100_YEARS:
                duration = Emulator.getIntUnixTimestamp();
        }
        if (this.client.getHabbo().hasPermission("acc_supporttool"))
        {
            Emulator.getGameEnvironment().getModToolManager().ban(userId, this.client.getHabbo(), message, duration, ModToolBanType.ACCOUNT, cfhTopic);
        }
        else
        {
            Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.modtools.ban").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()));
        }
    }
}