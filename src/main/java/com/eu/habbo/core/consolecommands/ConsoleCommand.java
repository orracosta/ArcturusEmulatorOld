package com.eu.habbo.core.consolecommands;

import gnu.trove.set.hash.THashSet;

public abstract class ConsoleCommand
{
    /**
     * Holds all console commands.
     */
    public static THashSet<ConsoleCommand> commands = new THashSet<ConsoleCommand>()
    {
        {
            add(new ConsoleShutdownCommand());
            add(new ConsoleInfoCommand());
        }
    };

    /**
     * The key of the command. First word.
     */
    public final String key;

    /**
     * Usage of the command (Arguments).
     */
    public final String usage;

    public ConsoleCommand(String key, String usage)
    {
        this.key    = key;
        this.usage  = usage;
    }

    /**
     * Handles a command
     * @param args The arguments entered in the console including the key at index 0.
     * @throws Exception
     */
    public abstract void handle(String[] args) throws Exception;

    public static ConsoleCommand findCommand(String key)
    {
        for (ConsoleCommand consoleCommand : commands)
        {
            if (consoleCommand.key.equalsIgnoreCase(key))
            {
                return consoleCommand;
            }
        }

        return null;
    }

    /**
     * Handles a line entered in the console.
     * @param line The line entered in the console.
     * @return True if the command was succesfully handled. Otherwise false.
     */
    public static boolean handle(String line)
    {
        String[] message = line.split(" ");

        if (message.length > 0)
        {
            ConsoleCommand command = ConsoleCommand.findCommand(message[0]);

            if (command != null)
            {
                try
                {
                    command.handle(message);
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Unknown Console Command " + message[0]);
                System.out.println("Commands Available (" + commands.size() + "): ");

                for (ConsoleCommand c : commands)
                {
                    System.out.println(c.key + " " + c.usage);
                }
            }
        }

        return false;
    }
}