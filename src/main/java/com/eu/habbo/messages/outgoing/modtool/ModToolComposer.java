package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolCategory;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TObjectProcedure;

public class ModToolComposer extends MessageComposer implements TObjectProcedure<ModToolCategory>
{
    private final Habbo habbo;

    public ModToolComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolComposer);

        if(this.habbo.hasPermission("acc_modtool_ticket_q"))
        {
            this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getTickets().size()); //tickets

            for (ModToolIssue issue : Emulator.getGameEnvironment().getModToolManager().getTickets().values())
            {
                issue.serialize(this.response);
            }
        }
        else
        {
            this.response.appendInt(0);
        }

        synchronized (Emulator.getGameEnvironment().getModToolManager().getPresets())
        {
            this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getPresets().get("user").size());
            for (String s : Emulator.getGameEnvironment().getModToolManager().getPresets().get("user"))
            {
                this.response.appendString(s);
            }
        }

        this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getCategory().size());

        Emulator.getGameEnvironment().getModToolManager().getCategory().forEachValue(this);

        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_ticket_q")); //ticketQueueueuhuehuehuehue
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_user_logs")); //user chatlogs
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_user_alert")); //can send caution
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_user_kick")); //can send kick
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_user_ban")); //can send ban
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_room_info")); //room info ??Not sure
        this.response.appendBoolean(this.habbo.hasPermission("acc_modtool_room_logs")); //room chatlogs ??Not sure

        synchronized (Emulator.getGameEnvironment().getModToolManager().getPresets())
        {
            this.response.appendInt(Emulator.getGameEnvironment().getModToolManager().getPresets().get("room").size());
            for (String s : Emulator.getGameEnvironment().getModToolManager().getPresets().get("room"))
            {
                this.response.appendString(s);
            }
        }

        return this.response;
    }

    @Override
    public boolean execute(ModToolCategory category)
    {
        this.response.appendString(category.getName());
//        this.response.appendBoolean(false);
//        this.response.appendInt(category.getPresets().size());
//
//        TIntObjectIterator<ModToolPreset> iterator = category.getPresets().iterator();
//
//        for(int i = category.getPresets().size(); i-- > 0;)
//        {
//            try
//            {
//                iterator.advance();
//            }
//            catch (NoSuchElementException e)
//            {
//                break;
//            }
//            this.response.appendString(iterator.value().name);
//            this.response.appendString(iterator.value().message);
//            this.response.appendInt(iterator.value().banLength);
//            this.response.appendInt(1); //avatarban
//            this.response.appendInt(iterator.value().muteLength);
//            this.response.appendInt(1); //tradelock
//            this.response.appendString(iterator.value().reminder);
//            this.response.appendBoolean(false); //ShowHabboWay
//        }

        return true;
    }
}
