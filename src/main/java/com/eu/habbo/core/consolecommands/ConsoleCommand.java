package com.eu.habbo.core.consolecommands;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

public abstract class ConsoleCommand
{
    /**
     * Holds all console commands.
     */
    public static THashMap<String, ConsoleCommand> commands = new THashMap<String, ConsoleCommand>();

    /**
     * The key of the command. First word.
     */
    public final String key;

    /**
     * Usage of the command (Arguments).
     */
    public final String usage;

    /**
     * Constructs a new ConsoleCommand.
     * @param key The key of the command. First word.
     * @param usage Usage of the command (Arguments).
     */
    public ConsoleCommand(String key, String usage)
    {
        this.key    = key;
        this.usage  = usage;
    }

    /**
     * Loads all default ConsoleCommands.
     */
    public static void load()
    {
        addCommand(new ConsoleShutdownCommand());
        addCommand(new ConsoleInfoCommand());
        addCommand(new ConsoleTestCommand());
        addCommand(new ConsoleReconnectCameraCommand());
    }

    /**
     * Handles a command
     * @param args The arguments entered in the console including the key at index 0.
     * @throws Exception
     */
    public abstract void handle(String[] args) throws Exception;

    /**
     * Add a new consolecommand.
     * @param command The consolecommand to add.
     */
    public static void addCommand(ConsoleCommand command)
    {
        commands.put(command.key, command);
    }

    /**
     * Searches for the consolecommand using a given key.
     * @param key The key to find the associated ConsoleCommand for.
     * @return The ConsoleCommand associated with the key. return null if not found.
     */
    public static ConsoleCommand findCommand(String key)
    {
        return commands.get(key);
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

                for (ConsoleCommand c : commands.values())
                {
                    System.out.println(c.key + " " + c.usage);
                }
            }
        }

        return false;
    }
}