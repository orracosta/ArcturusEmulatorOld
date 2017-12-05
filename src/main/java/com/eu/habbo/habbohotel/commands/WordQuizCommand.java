package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;

public class WordQuizCommand extends Command
{
    public WordQuizCommand()
    {
        super("cmd_word_quiz", Emulator.getTexts().getValue("commands.keys.cmd_word_quiz").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if (!gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasActiveWordQuiz())
        {
            String question = "";
            int duration = 60;

            if (params.length > 2)
            {
                for (int i = 1; i < params.length - 1; i++)
                {
                    question += " " + params[i];
                }

                try
                {
                    duration = Integer.valueOf(params[params.length - 1]);
                }
                catch (Exception e)
                {
                    question += " " + params[params.length -1];
                }
            }
            else
            {
                question = params[1];
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().startWordQuiz(question, duration * 1000);
        }
        return true;
    }
}