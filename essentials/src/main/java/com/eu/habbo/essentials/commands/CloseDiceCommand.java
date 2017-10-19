package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.interactions.InteractionDice;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;

public class CloseDiceCommand extends Command
{
    public CloseDiceCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

        if (room != null)
        {
            int count = 0;
            if (strings.length == 1)
            {
                for (RoomTile tile : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getLayout().getTilesAround(gameClient.getHabbo().getRoomUnit().getCurrentLocation()))
                {
                    for (HabboItem item : gameClient.getHabbo().getHabboInfo().getCurrentRoom().getItemsAt(tile))
                    {
                        if (item instanceof InteractionDice && !item.getExtradata().equalsIgnoreCase("0"))
                        {
                            closeDice(item, room);
                            ++count;
                        }
                    }
                }

                if (count > 0)
                {
                    gameClient.getHabbo().shout(Emulator.getTexts().getValue("essentials.cmd_closedice.closed").replace("%count%", count + ""), RoomChatMessageBubbles.ALERT);
                }
            }
            else if (strings.length >= 2 && strings[1].equalsIgnoreCase(Emulator.getTexts().getValue("essentials.cmd_closedice.keywordall")) && gameClient.getHabbo().hasPermission("acc_closedice_room", gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo())))
            {
                for (HabboItem item : room.getFloorItems())
                {
                    if (item instanceof InteractionDice && !item.getExtradata().equalsIgnoreCase("0"))
                    {
                        closeDice(item, room);
                        ++count;
                    }
                }

                gameClient.getHabbo().shout(Emulator.getTexts().getValue("essentials.cmd_closedice.closed").replace("%count%", Emulator.getTexts().getValue("essentials.cmd_closedice.keywordall")), RoomChatMessageBubbles.ALERT);
            }
        }

        return true;
    }

    private void closeDice(HabboItem item, Room room)
    {
        item.setExtradata("0");
        item.needsUpdate(true);
        room.updateItem(item);
    }
}