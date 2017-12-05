package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;

import java.util.concurrent.TimeUnit;

/**
 * This is part of the open source license. Modification is prohobited.
 * If you don't like it, use another emulator. Or otherwise deal with it.
 * Respect the authors work. Don't be a dick.
 */
public class AboutCommand extends Command
{
    public AboutCommand()
    {
        super(null, new String[]{ "about", "info", "online", "server" });
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        Emulator.getRuntime().gc();

        int seconds = Emulator.getIntUnixTimestamp() - Emulator.getTimeStarted();
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        String message = "";

        if (Emulator.getConfig().getBoolean("info.shown", true))
        {
            message += "<b>Estatisticas do Hotel</b>\r" +
                    "- Usuários Online: " + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "\r" +
                    "- Quartos Ativos: " + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "\r" +
                    "- Loja:  " + Emulator.getGameEnvironment().getCatalogManager().catalogPages.size() + " páginas e " + CatalogManager.catalogItemAmount + " items. \r" +
                    "- Mobis: " + Emulator.getGameEnvironment().getItemManager().getItems().size() + " definições de items" + "\r" +
                    "\n" +
                    "<b>Estatisticas do servidor</b>\r" +
                    "- Uptime: " + day + (day > 1 ? " dias, " : " dia, ") + hours + (hours > 1 ? " horas, " : " hora, ") + minute + (minute > 1 ? " minutos, " : " minuto, ") + second + (second > 1 ? " segundos!" : " segundo!") + "\r" +
                    "- Uso de RAM: " + (Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "/" + (Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "MB\r" +
                    "- CPU: " + Emulator.getRuntime().availableProcessors() + "\r" +
                    "- Memoria Total: " + Emulator.getRuntime().maxMemory() / (1024 * 1024) + "MB" + "\r\n";
        }

                message += "\r" +

                "<b>This hotel was based on Arcturus Emulator. \r" +
                "All thanks to The General";

        gameClient.sendResponse(new GenericAlertComposer(message));

        return true;
    }
}
