package com.eu.habbo.essentials.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class EffectCommand extends Command
{
    private final int[] effectIds;
    private String[] messages;

    public EffectCommand(int[] effectIds, String[] keys, String[] messages)
    {
        super("cmd_hoverboard", keys);

        this.effectIds = effectIds;
        this.messages = messages;
    }
    @Override
    public boolean handle(GameClient gameClient, String[] strings) throws Exception
    {
        if (this.effectIds.length > 0)
        {
            int effectIndex = Emulator.getRandom().nextInt(effectIds.length);
            int messageIndex = 0;
            if (this.effectIds.length == this.messages.length)
            {
                messageIndex = effectIndex;
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().giveEffect(gameClient.getHabbo(), this.effectIds[effectIndex]);

            if (this.messages.length >= messageIndex + 1)
            {
                gameClient.getHabbo().talk(this.messages[messageIndex]);
            }
        }

        return true;
    }
}