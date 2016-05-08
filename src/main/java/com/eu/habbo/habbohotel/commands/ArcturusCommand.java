package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

/**
 * Created on 19-2-2015 11:11.
 */
public class ArcturusCommand extends Command
{
    public ArcturusCommand()
    {
        super.permission = null;
        super.keys = new String[]{ "arcturus", "emulator" };
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage("This hotel is powered by Arcturus Emulator! \r" +
                    "Cet hôtel est alimenté par Arcturus émulateur! \r" +
                    "Dit hotel draait op Arcturus Emulator! \r" +
                    "Este hotel está propulsado por Arcturus emulador! \r" +
                    "Hotellet drivs av Arcturus Emulator! \r" +
                    "Das Hotel gehört zu Arcturus Emulator betrieben!"
                    , gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }

        return true;
    }
}
