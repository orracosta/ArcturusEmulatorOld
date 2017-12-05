package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.essentials.Essentials;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.rooms.users.RoomUserWalkEvent;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.users.UserExitRoomEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrbCommand extends Command implements EventListener
{
    public static HashMap<Integer, Integer> userAFKMap = new HashMap<Integer, Integer>();
    public static HashMap<Integer, Integer> requiresUpdate = new HashMap<>();
    public static boolean started = false;

    public BrbCommand(String permission, String[] keys)
    {
        super(permission, keys);
        Emulator.getPluginManager().registerEvents(Essentials.INSTANCE, this);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (!gameClient.getHabbo().getRoomUnit().isIdle())
        {
            afk(gameClient.getHabbo());
        }

        return true;
    }

    private static void afk(Habbo habbo)
    {
        habbo.talk(Emulator.getTexts().getValue("essentials.cmd_brb.brb").replace("%username%", habbo.getHabboInfo().getUsername()));
        habbo.getHabboInfo().getCurrentRoom().idle(habbo);
        userAFKMap.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp());
        requiresUpdate.put(habbo.getHabboInfo().getId(), Emulator.getIntUnixTimestamp() + 60);
    }

    private static void back(Habbo habbo)
    {
        if (userAFKMap.containsKey(habbo.getHabboInfo().getId()))
        {
            habbo.talk(Emulator.getTexts().getValue("essentials.cmd_brb.back").replace("%username%", habbo.getHabboInfo().getUsername()));
            habbo.getHabboInfo().getCurrentRoom().unIdle(habbo);
            userAFKMap.remove(habbo.getHabboInfo().getId());
            requiresUpdate.remove(habbo.getHabboInfo().getId());
        }
    }

    @EventHandler
    public static void onUserExitRoomEvent(UserExitRoomEvent event)
    {
        userAFKMap.remove(event.habbo.getHabboInfo().getId());
        requiresUpdate.remove(event.habbo.getHabboInfo().getId());
    }

    @EventHandler
    public static void onUserIdleEvent(UserIdleEvent event)
    {
        if (event.reason == UserIdleEvent.IdleReason.TIMEOUT)
        {
            afk(event.habbo);
        }
        else if (!event.idle)
        {
            back(event.habbo);
        }
    }

    public static void startBackgroundThread()
    {
        if (started)
            return;
        started = true;
        Emulator.getThreading().run(new Runnable()
        {
            @Override
            public void run()
            {
                List<Integer> toRemove = new ArrayList<Integer>();
                Emulator.getThreading().run(this, 2 * 1000);
                int timeStamp = Emulator.getIntUnixTimestamp();
                for (Map.Entry<Integer, Integer> entry : requiresUpdate.entrySet())
                {
                    if (entry.getValue() < timeStamp)
                    {
                        Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(entry.getKey());

                        if (habbo != null)
                        {
                            System.out.println(habbo.getHabboInfo().getUsername());
                            if (habbo.getRoomUnit().isIdle())
                            {
                                habbo.getHabboInfo().getCurrentRoom().sendComposer(
                                        new RoomUserTalkComposer(
                                                new RoomChatMessage(
                                                        Emulator.getTexts().getValue("essentials.cmd_brb.time")
                                                                .replace("%username%", habbo.getHabboInfo().getUsername())
                                                                .replace("%time%", (int)Math.floor((timeStamp - userAFKMap.get(habbo.getHabboInfo().getId())) / 60) + "")
                                                        , habbo.getRoomUnit(), RoomChatMessageBubbles.ALERT)).compose());
                                requiresUpdate.put(entry.getKey(), timeStamp + 60);
                                continue;
                            }
                        }

                        toRemove.add(entry.getKey());
                    }
                }

                for (Integer i : toRemove)
                {
                    userAFKMap.remove(i);
                    requiresUpdate.remove(i);
                }
            }
        });
    }
}