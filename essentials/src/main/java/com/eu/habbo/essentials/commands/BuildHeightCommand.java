package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.essentials.Essentials;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.furniture.FurnitureMovedEvent;
import com.eu.habbo.plugin.events.furniture.FurniturePlacedEvent;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;

public class BuildHeightCommand extends Command implements EventListener
{
    public static String BUILD_HEIGHT_KEY = "essentials.build_height";
    public BuildHeightCommand(String permission, String[] keys)
    {
        super(permission, keys);

        Emulator.getPluginManager().registerEvents(Essentials.INSTANCE, this);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length == 2)
        {
            Double height = -1.0;

            try
            {
                height = Double.valueOf(strings[1]);
            }
            catch (Exception e)
            {
            }

            if(height > 40 || height < 0) {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_buildheight.invalid_height"));
                return true;
            }

            gameClient.getHabbo().getHabboStats().cache.put(BUILD_HEIGHT_KEY, height);
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_buildheight.changed").replace("%height%", height + ""));
        }
        else
        {
            if(gameClient.getHabbo().getHabboStats().cache.containsKey(BUILD_HEIGHT_KEY))
            {
                gameClient.getHabbo().getHabboStats().cache.remove(BUILD_HEIGHT_KEY);
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_buildheight.disabled"));
                return true;
            }
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.cmd_buildheight.not_specified"));
        }
        return true;
    }

    @EventHandler
    public static void onUserExitRoomEvent(UserExitRoomEvent event)
    {
        event.habbo.getHabboStats().cache.remove(BUILD_HEIGHT_KEY);
    }


    @EventHandler
    public static void onFurniturePlaced(final FurniturePlacedEvent event)
    {
        if (event.location != null)
        {
            if (event.habbo.getHabboStats().cache.containsKey(BUILD_HEIGHT_KEY))
            {
                Emulator.getThreading().run(new Runnable() {
                    public void run() {
                        event.furniture.setZ((Double) event.habbo.getHabboStats().cache.get(BUILD_HEIGHT_KEY));
                        event.habbo.getHabboInfo().getCurrentRoom().updateItem(event.furniture);
                        event.furniture.needsUpdate(true);
                    }
                }, 25);
            }
        }
    }

    @EventHandler
    public static void onFurnitureMoved(final FurnitureMovedEvent event)
    {
        if (event.newPosition != null)
        {
            if (event.habbo.getHabboStats().cache.containsKey(BUILD_HEIGHT_KEY))
            {
                Emulator.getThreading().run(new Runnable() {
                    public void run() {
                        event.furniture.setZ((Double) event.habbo.getHabboStats().cache.get(BUILD_HEIGHT_KEY));
                        event.habbo.getHabboInfo().getCurrentRoom().updateItem(event.furniture);
                        event.furniture.needsUpdate(true);
                    }
                }, 25);
            }
        }
    }
}