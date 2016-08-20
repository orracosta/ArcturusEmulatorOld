package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;

public class UpdateWordfilter extends RCONMessage<UpdateWordfilter.WordFilterJSON>
{
    public UpdateWordfilter()
    {
        super(WordFilterJSON.class);
    }

    @Override
    public void handle(WordFilterJSON object)
    {
        Emulator.getGameEnvironment().getWordFilter().reload();
    }

    protected class WordFilterJSON
    {
    }
}