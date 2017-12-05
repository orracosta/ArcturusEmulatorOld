package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AllowTradingCommand extends Command
{
    public AllowTradingCommand()
    {
        super("cmd_allow_trading", Emulator.getTexts().getValue("commands.keys.cmd_allow_trading").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (params.length == 1)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_allow_trading.forgot_username"));
            return true;
        }

        if (params.length == 2)
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_allow_trading.forgot_trade").replace("%username%", params[1]));
            return true;
        }

        if (params[2].equalsIgnoreCase(Emulator.getTexts().getValue("generic.yes")) || params[2].equalsIgnoreCase(Emulator.getTexts().getValue("generic.no")))
        {
            String username = params[1];
            boolean enabled = params[2].equalsIgnoreCase(Emulator.getTexts().getValue("generic.yes"));

            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

            if (habbo != null)
            {
                habbo.getHabboStats().allowTrade = enabled;
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_allow_trading." + (enabled ? "enabled" : "disabled")).replace("%username%", params[1]));
                return true;
            }
            else
            {
                boolean found = false;
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_settings INNER JOIN users ON users.id = users_settings.id SET can_trade = ? WHERE users.username LIKE ?"))
                {
                    statement.setString(1, enabled ? "1" : "0");
                    statement.setString(2, username);
                    found = statement.executeUpdate() > 0;
                }

                if (!found)
                {
                    gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_allow_trading.user_not_found").replace("%username%", params[1]));
                    return true;
                }

                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_allow_trading." + (enabled ? "enabled" : "disabled")).replace("%username%", params[1]));
            }
        }
        else
        {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_allow_trading.incorrect_setting").replace("%enabled%", Emulator.getTexts().getValue("generic.yes")).replace("%disabled%", Emulator.getTexts().getValue("generic.no")));
        }
        return true;
    }
}
