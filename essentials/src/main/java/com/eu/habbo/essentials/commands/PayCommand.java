package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

public class PayCommand extends Command
{
    public PayCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length < 4)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.incorrect.usage"));
            return true;
        }

        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(strings[1]);
        if (habbo == null)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.habbo.notfound"));
            return true;
        }

        if (habbo.getHabboInfo().getId() == gameClient.getHabbo().getHabboInfo().getId())
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.self"));
            return true;
        }

        int amount = 0;
        boolean valid = true;
        try
        {
            amount = Integer.valueOf(strings[2]);
        }
        catch (Exception e)
        {
            valid = false;
        }

        if (!valid || amount <= 0)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.invalid_amount"));
            return true;
        }

        if (strings[3].equalsIgnoreCase("credits") || strings[3].equalsIgnoreCase("coins"))
        {
            if (gameClient.getHabbo().getHabboInfo().getCredits() >= amount)
            {
                habbo.giveCredits(amount);
                gameClient.getHabbo().giveCredits(-amount);
            }
            else
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.not_enough").replace("%amount%", amount + "").replace("%type%", strings[3]).replace("%username%", habbo.getHabboInfo().getUsername()));
                return true;
            }
        }
        else
        {
            int type = -1;
            boolean found = false;
            String currency = "";
            System.out.println(Emulator.getConfig().getValue("seasonal.currency.names"));
            for (String s : Emulator.getConfig().getValue("seasonal.currency.names").split(";"))
            {
                System.out.println(s);
                System.out.println(strings[3]);
                System.out.println(strings[3].startsWith(s));
                System.out.println(Math.abs(s.length() - strings[3].length()));
                System.out.println("----");
                if (s.equalsIgnoreCase(strings[3]) || (strings[3].startsWith(s) && Math.abs(s.length() - strings[3].length()) < 3))
                {
                    currency = s;
                    found = true;
                    break;
                }
            }

            type = Emulator.getConfig().getInt("seasonal.currency." + currency, -1);

            if (!found || type == -1)
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.invalid_type").replace("%type%", strings[3]));
                return true;
            }

            if (gameClient.getHabbo().getHabboInfo().getCurrencyAmount(type) >= amount)
            {
                habbo.givePoints(type, amount);
                gameClient.getHabbo().givePoints(type, -amount);
            }
            else
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.not_enough").replace("%amount%", amount + "").replace("%type%", strings[3]).replace("%username%", habbo.getHabboInfo().getUsername()));
                return true;
            }
        }

        habbo.whisper(Emulator.getTexts().getValue("essentials.cmd_pay.received").replace("%amount%", amount + "").replace("%type%", strings[3]).replace("%username%", gameClient.getHabbo().getHabboInfo().getUsername()));
        gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_pay.transferred").replace("%amount%", amount + "").replace("%type%", strings[3]).replace("%username%", habbo.getHabboInfo().getUsername()));
        return true;
    }
}