package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.events.rooms.RoomUncachedEvent;

import java.util.HashMap;

public class SellRoomCommand extends Command implements EventListener
{
    public final static HashMap<Integer, Integer> sellingRooms = new HashMap<Integer, Integer>();

    public SellRoomCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        synchronized (sellingRooms)
        {
            Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
            if (room.getOwnerId() == gameClient.getHabbo().getHabboInfo().getId())
            {
                if (sellingRooms.containsKey(room.getId()))
                {
                    sellingRooms.remove(room.getId());
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.removed"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
                else if (strings.length >= 2)
                {
                    int credits = 0;
                    try
                    {
                        credits = Integer.valueOf(strings[1]);
                    }
                    catch (Exception e)
                    {
                        gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.invalid_credits").replace("%credits%", strings[1]), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                        return true;
                    }

                    if (credits >= 1)
                    {
                        if (room.hasGuild())
                        {
                            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.has_guild"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                        }
                        else
                        {
                            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.confirmed").replace("%credits%", credits + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                            sellingRooms.put(room.getId(), credits);
                        }
                        return true;
                    }

                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.invalid_credits").replace("%credits%", credits + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;

                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.usage"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
            }
            else
            {
                if (sellingRooms.containsKey(room.getId()))
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.forsale").replace("%credits%", sellingRooms.get(room.getId()) + "").replace("%ownername%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
                else
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.notforsale"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                }
            }

            return true;
        }
    }

    @EventHandler
    public static void onRoomUncached(RoomUncachedEvent event)
    {
        sellingRooms.remove(event.room.getId());
    }
}