package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.ChangeNameCheckResultComposer;

import java.util.ArrayList;
import java.util.List;

public class ChangeNameCheckUsernameEvent extends MessageHandler
{
    public static String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_-=!?@:,.";
    @Override
    public void handle() throws Exception
    {
        String name = this.packet.readString();

        int errorCode = ChangeNameCheckResultComposer.AVAILABLE;

        List<String> suggestions = new ArrayList<String>(4);
        if (!HabboManager.NAMECHANGE_ENABLED)
        {
            errorCode = ChangeNameCheckResultComposer.DISABLED;
        }
        else if (name.length() < 3)
        {
            errorCode = ChangeNameCheckResultComposer.TOO_SHORT;
        }
        else if (name.length() > 15)
        {
            errorCode = ChangeNameCheckResultComposer.TOO_LONG;
        }
        else if (HabboManager.getOfflineHabboInfo(name) != null)
        {
            errorCode = ChangeNameCheckResultComposer.TAKEN_WITH_SUGGESTIONS;
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
        }
        else if (!Emulator.getGameEnvironment().getWordFilter().filter(name, this.client.getHabbo()).equalsIgnoreCase(name) || !name.toUpperCase().matches("*." + VALID_CHARACTERS + ".*"))
        {
            errorCode = ChangeNameCheckResultComposer.NOT_VALID;
        }
        else
        {
            this.client.getHabbo().getHabboStats().changeNameChecked = name;
        }

        this.client.sendResponse(new ChangeNameCheckResultComposer(errorCode, name, suggestions));
    }
}
