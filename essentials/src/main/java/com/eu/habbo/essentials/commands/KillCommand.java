package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;

public class KillCommand extends Command
{
    public KillCommand(String permission, String[] keys)
    {
        super(permission, keys);
    }

    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (strings.length >= 2)
        {
            Habbo habbo = gameClient.getHabbo().getHabboInfo().getCurrentRoom().getHabbo(strings[1]);

            if (habbo != null)
            {
                if (habbo != gameClient.getHabbo())
                {
                    {
                        Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();
                        if (room == null)
                            return false;

                        habbo.getRoomUnit().cmdLay = true;
                        room.updateHabbo(gameClient.getHabbo());
                        habbo.getRoomUnit().cmdSit = true;
                        habbo.getRoomUnit().setBodyRotation(RoomUserRotation.values()[gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() - gameClient.getHabbo().getRoomUnit().getBodyRotation().getValue() % 2]);
                        habbo.getRoomUnit().getStatus().put("lay", 0.5 + "");
                        room.sendComposer(new RoomUserStatusComposer(gameClient.getHabbo().getRoomUnit()).compose());

                        String[] killerEffects = Emulator.getConfig().getValue("essentials.cmd_kill.effect.killer").split(";");
                        if (killerEffects.length > 0)
                        {
                            room.giveEffect(gameClient.getHabbo(), Integer.valueOf(killerEffects[Emulator.getRandom().nextInt(killerEffects.length)]));
                        }
                        String[] killmsg = Emulator.getTexts().getValue("essentials.cmd_kill.killmessages.killer").split(";");
                        gameClient.getHabbo().talk(killmsg[Emulator.getRandom().nextInt(killmsg.length)].replace("%killer%", gameClient.getHabbo().getHabboInfo().getUsername()).replace("%victim%", habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);

                        String[] deadEffects = Emulator.getConfig().getValue("essentials.cmd_kill.effect.victim").split(";");
                        if (deadEffects.length > 0)
                        {
                            room.giveEffect(habbo, Integer.valueOf(deadEffects[Emulator.getRandom().nextInt(deadEffects.length)]));
                        }
                        String[] deadmsg = Emulator.getTexts().getValue("essentials.cmd_kill.killmessages.victim").split(";");
                        habbo.talk(deadmsg[Emulator.getRandom().nextInt(deadmsg.length)].replace("%killer%", gameClient.getHabbo().getHabboInfo().getUsername()).replace("%victim%", habbo.getHabboInfo().getUsername()), RoomChatMessageBubbles.ALERT);
                    }
                }
            }
        }
        return true;
    }
}
