package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.util.HashMap;

public class BuyRoomCommand extends Command
{
    public BuyRoomCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        synchronized (SellRoomCommand.sellingRooms)
        {
            final Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

            if (!SellRoomCommand.sellingRooms.containsKey(room.getId()) || room.hasGuild())
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.notforsale"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }

            if (strings.length == 1)
            {
                gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.forsale").replace("%credits%", SellRoomCommand.sellingRooms.get(room.getId()) + "").replace("%ownername%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                return true;
            }
            else if (strings.length == 2 && strings[1].equalsIgnoreCase(Emulator.getTexts().getValue("essentials.sellroom.confirmkey")))
            {
                Habbo owner = Emulator.getGameEnvironment().getHabboManager().getHabbo(room.getOwnerId());

                if (owner == null)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.owneroffline").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }
                else if (owner == gameClient.getHabbo())
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.selfbuy").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                if (room.hasGuild())
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.buyroom.has_guild"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }


                int credits = SellRoomCommand.sellingRooms.get(room.getId());

                if (credits < 1)
                {
                    gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("essentials.sellroom.invalid_credits").replace("%username%", room.getOwnerName()), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
                    return true;
                }

                if (gameClient.getHabbo().getHabboInfo().getCredits() >= credits)
                {
                    synchronized (room)
                    {
                        SellRoomCommand.sellingRooms.remove(room.getId());

                        for (HabboItem item : room.getFloorItems())
                        {
                            if (item.getUserId() == room.getOwnerId())
                            {
                                item.setUserId(gameClient.getHabbo().getHabboInfo().getId());
                                item.needsUpdate(true);
                            }
                        }

                        for (HabboItem item : room.getWallItems())
                        {
                            if (item.getUserId() == room.getOwnerId())
                            {
                                item.setUserId(gameClient.getHabbo().getHabboInfo().getId());
                                item.needsUpdate(true);
                            }
                        }

                        owner.giveCredits(credits);
                        gameClient.getHabbo().giveCredits(-credits);
                        room.setOwnerId(gameClient.getHabbo().getHabboInfo().getId());
                        room.setOwnerName(gameClient.getHabbo().getHabboInfo().getUsername());
                        room.setNeedsUpdate(true);
                    }
                }
            }
            else
            {
                gameClient.getHabbo().whisper(Emulator.getTexts().getValue("essentials.sellroom.buyroom.usage").replace("%key%", this.keys[0]).replace("%confirmkey%", Emulator.getTexts().getValue("essentials.sellroom.confirmkey")));
            }
        }

        return true;
    }
}