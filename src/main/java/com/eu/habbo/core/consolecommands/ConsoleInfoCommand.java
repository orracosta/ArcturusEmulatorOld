package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogManager;

import java.util.concurrent.TimeUnit;

public class ConsoleInfoCommand extends ConsoleCommand
{
    public ConsoleInfoCommand()
    {
        super("info", "");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        int seconds = Emulator.getIntUnixTimestamp() - Emulator.getTimeStarted();
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

        System.out.println("Emulator version: " + Emulator.version);
        System.out.println("Hotel Statistics");
        System.out.println("- Users: " + Emulator.getGameEnvironment().getHabboManager().getOnlineCount());
        System.out.println("- Rooms: " + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size());
        System.out.println("- Shop:  " + Emulator.getGameEnvironment().getCatalogManager().catalogPages.size() + " pages and " + CatalogManager.catalogItemAmount + " items.");
        System.out.println("- Furni: " + Emulator.getGameEnvironment().getItemManager().getItems().size() + " items.");
        System.out.println("");
        System.out.println("Server Statistics");
        System.out.println("- Uptime: " + day + (day > 1 ? " days, " : " day, ") + hours + (hours > 1 ? " hours, " : " hour, ") + minute + (minute > 1 ? " minutes, " : " minute, ") + second + (second > 1 ? " seconds!" : " second!"));
        System.out.println("- RAM Usage: " + (Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "/" + (Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "MB");
        System.out.println("- CPU Cores: " + Emulator.getRuntime().availableProcessors());
        System.out.println("- Total Memory: " + Emulator.getRuntime().maxMemory() / (1024 * 1024) + "MB");
    }
}