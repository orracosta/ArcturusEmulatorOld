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
        String question = "";

        if (params.length >= 2)
        {
            for (int i = 1; i < params.length; i++)
            {
                question += " " + params[1];
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().startWordQuiz(question, 30000);

            return true;
        }

        return false;
    }
}