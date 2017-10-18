package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;
import com.eu.habbo.threading.runnables.RoomTrashing;

public class TrashCommand extends Command
{
    public TrashCommand()
    {
        super("cmd_trash", Emulator.getTexts().getValue("commands.keys.cmd_trash").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        gameClient.getHabbo().whisper("Sorry. Lulz mode removed |");
        return false;
    }
}