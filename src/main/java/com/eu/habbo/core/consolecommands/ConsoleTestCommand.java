package com.eu.habbo.core.consolecommands;


public class ConsoleTestCommand extends ConsoleCommand
{
    public ConsoleTestCommand()
    {
        super("test", "test");
    }

    @Override
    public void handle(String[] args) throws Exception
    {
        System.out.println("This is a test command for live debugging.");



    }
}