package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.CommandLog;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.pets.AbstractPet;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTypingComposer;
import com.eu.habbo.plugin.events.users.UserCommandEvent;
import com.eu.habbo.plugin.events.users.UserExecuteCommandEvent;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.*;

public class CommandHandler
{
    private final static THashMap<String, Command> commands = new THashMap<String, Command>(5);

    public CommandHandler()
    {
        long millis = System.currentTimeMillis();
        reloadCommands();
        Emulator.getLogging().logStart("Command Handler -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reloadCommands()
    {
        addCommand(new AboutCommand());
        addCommand(new AlertCommand());
        addCommand(new AllowTradingCommand());
        addCommand(new ArcturusCommand());
        addCommand(new BadgeCommand());
        addCommand(new BanCommand());
        addCommand(new BlockAlertCommand());
        addCommand(new BotsCommand());
        addCommand(new ChangeNameCommand());
        addCommand(new ChatTypeCommand());
        addCommand(new CommandsCommand());
        addCommand(new ConnectCameraCommand());
        addCommand(new ControlCommand());
        addCommand(new CoordsCommand());
        addCommand(new CreditsCommand());
        addCommand(new DiagonalCommand());
        addCommand(new DisconnectCommand());
        addCommand(new EjectAllCommand());
        addCommand(new EmptyInventoryCommand());
        addCommand(new EmptyBotsInventoryCommand());
        addCommand(new EmptyPetsInventoryCommand());
        addCommand(new EnableCommand());
        addCommand(new EventCommand());
        addCommand(new FacelessCommand());
        addCommand(new FastwalkCommand());
        addCommand(new FreezeBotsCommand());
        addCommand(new FreezeCommand());
        addCommand(new GiftCommand());
        addCommand(new GiveRankCommand());
        addCommand(new HabnamCommand());
        addCommand(new HandItemCommand());
        addCommand(new HappyHourCommand());
        addCommand(new HotelAlertCommand());
        addCommand(new HotelAlertLinkCommand());
        addCommand(new IPBanCommand());
        addCommand(new LayCommand());
        addCommand(new MachineBanCommand());
        addCommand(new MassBadgeCommand());
        addCommand(new MassCreditsCommand());
        addCommand(new MassGiftCommand());
        addCommand(new MassPixelsCommand());
        addCommand(new MassPointsCommand());
        addCommand(new MimicCommand());
        addCommand(new MoonwalkCommand());
        addCommand(new MultiCommand());
        addCommand(new MuteBotsCommand());
        addCommand(new MuteCommand());
        addCommand(new MutePetsCommand());
        addCommand(new PetInfoCommand());
        addCommand(new PickallCommand());
        addCommand(new PixelCommand());
        addCommand(new PluginsCommand());
        addCommand(new PointsCommand());
        addCommand(new PullCommand());
        addCommand(new PushCommand());
        addCommand(new RedeemCommand());
        addCommand(new ReloadRoomCommand());
        addCommand(new RoomAlertCommand());
        addCommand(new RoomBundleCommand());
        addCommand(new RoomCreditsCommand());
        addCommand(new RoomDanceCommand());
        addCommand(new RoomEffectCommand());
        addCommand(new RoomItemCommand());
        addCommand(new RoomKickCommand());
        addCommand(new RoomMuteCommand());
        addCommand(new RoomPixelsCommand());
        addCommand(new RoomPointsCommand());
        addCommand(new SayAllCommand());
        addCommand(new SayCommand());
        addCommand(new SetMaxCommand());
        addCommand(new SetPollCommand());
        addCommand(new SetSpeedCommand());
        addCommand(new ShoutAllCommand());
        addCommand(new ShoutCommand());
        addCommand(new ShutdownCommand());
        addCommand(new SitCommand());
        addCommand(new SitDownCommand());
        addCommand(new StaffAlertCommand());
        addCommand(new StaffOnlineCommand());
        addCommand(new StalkCommand());
        addCommand(new SummonCommand());
        addCommand(new SummonRankCommand());
        addCommand(new SuperbanCommand());
        addCommand(new SuperPullCommand());
        addCommand(new TakeBadgeCommand());
        addCommand(new TeleportCommand());
        addCommand(new TransformCommand());
        addCommand(new TrashCommand());
        addCommand(new UnbanCommand());
        addCommand(new UnloadRoomCommand());
        addCommand(new UnmuteCommand());
        addCommand(new UpdateBotsCommand());
        addCommand(new UpdateCatalogCommand());
        addCommand(new UpdateConfigCommand());
        addCommand(new UpdateGuildPartsCommand());
        addCommand(new UpdateHotelViewCommand());
        addCommand(new UpdateItemsCommand());
        addCommand(new UpdateNavigatorCommand());
        addCommand(new UpdatePermissionsCommand());
        addCommand(new UpdatePetDataCommand());
        addCommand(new UpdatePluginsCommand());
        addCommand(new UpdatePollsCommand());
        addCommand(new UpdateTextsCommand());
        addCommand(new UpdateWordFilterCommand());
        addCommand(new UserInfoCommand());
        addCommand(new WordQuizCommand());

        addCommand(new TestCommand());
    }

    /**
     * Adds a new command to the commands list.
     * @param command The command to be added.
     */
    public static void addCommand(Command command)
    {
        if(command == null)
            return;

        commands.put(command.getClass().getName(), command);
    }

    /**
     * Adds a new command to the commands list.
     * @param command The command class to be added.
     */
    public static void addCommand(Class<? extends Command> command)
    {
        try
        {
            //command.getConstructor().setAccessible(true);
            addCommand(command.newInstance());
            Emulator.getLogging().logDebugLine("Added command: " + command.getName());
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    /**
     * Handles a command executed by the connected GameClient.
     * This includes handling the petcommands aswell.
     * @param gameClient The GameClient that executed the command.
     * @param commandLine The whole sentence the GameClient used including the start:
     * @return Wether the command has been succesfully executed.
     */
    public static boolean handleCommand(GameClient gameClient, String commandLine)
    {
        if(gameClient != null)
        {
            if (commandLine.startsWith(":"))
            {
                commandLine = commandLine.replaceFirst(":", "");

                String[] parts = commandLine.split(" ");

                if (parts.length >= 1)
                {
                    for (Command command : commands.values())
                    {
                        for (String s : command.keys)
                        {
                            if (s.toLowerCase().equals(parts[0].toLowerCase()))
                            {
                                boolean succes = false;
                                if (command.permission == null || gameClient.getHabbo().hasPermission(command.permission, gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && (gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasRights(gameClient.getHabbo())) || gameClient.getHabbo().hasPermission("acc_placefurni") || (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null && gameClient.getHabbo().getHabboInfo().getCurrentRoom().getGuildId() > 0 && gameClient.getHabbo().getHabboInfo().getCurrentRoom().guildRightLevel(gameClient.getHabbo()) >= 2)  ))
                                {
                                    try
                                    {
                                        Emulator.getPluginManager().fireEvent(new UserExecuteCommandEvent(gameClient.getHabbo(), command, parts));

                                        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
                                            gameClient.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new RoomUserTypingComposer(gameClient.getHabbo().getRoomUnit(), false).compose());

                                        UserCommandEvent event = new UserCommandEvent(gameClient.getHabbo(), parts, command.handle(gameClient, parts));
                                        Emulator.getPluginManager().fireEvent(event);

                                        succes = event.succes;
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                    if (gameClient.getHabbo().hasPermission("log_commands"))
                                    {
                                        Emulator.getLogging().addLog(new CommandLog(gameClient.getHabbo().getHabboInfo().getId(), command, commandLine, succes));
                                    }
                                }

                                return succes;
                            }
                        }
                    }
                }
            }
            else
            {
                String[] args = commandLine.split(" ");

                if (args.length <= 1)
                    return false;

                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
                {
                    Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

                    if (room.getCurrentPets().isEmpty())
                        return false;

                    TIntObjectIterator<AbstractPet> petIterator = room.getCurrentPets().iterator();

                    for (int j = room.getCurrentPets().size(); j-- > 0; )
                    {
                        try
                        {
                            petIterator.advance();
                        }
                        catch (NoSuchElementException e)
                        {
                            break;
                        }

                        AbstractPet pet = petIterator.value();

                        if (pet instanceof Pet)
                        {
                            if (pet.getName().equalsIgnoreCase(args[0]))
                            {
                                String s = "";

                                for (int i = 1; i < args.length; i++)
                                {
                                    s += args[i] + " ";
                                }

                                s = s.substring(0, s.length() - 1);

                                for (PetCommand command : pet.getPetData().getPetCommands())
                                {
                                    if (command.key.equalsIgnoreCase(s))
                                    {
                                        if (command.level <= pet.getLevel())
                                            ((Pet) pet).handleCommand(command, gameClient.getHabbo());
                                        else
                                            pet.say(pet.getPetData().randomVocal(PetVocalsType.UNKNOWN_COMMAND));

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns an arraylist of all commands that the given rank has access to based on the permissions that have been set.
     * @param rankId The rank ID to search commands for.
     * @return ArrayList of commands.
     */
    public List<Command> getCommandsForRank(int rankId)
    {
        List<Command> allowedCommands = new ArrayList<Command>();
        if (Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId))
        {
            Collection<String> permissions = Emulator.getGameEnvironment().getPermissionsManager().getRank(rankId).getPermissions().keySet();

            for (Command command : commands.values())
            {
                if (allowedCommands.contains(command))
                    continue;

                if (permissions.contains(command.permission))
                {
                    allowedCommands.add(command);
                }
            }
        }

        Collections.sort(allowedCommands, CommandHandler.ALPHABETICAL_ORDER);

        return allowedCommands;
    }

    public static Command getCommand(String key)
    {
        for (Command command : commands.values())
        {
            for (String k : command.keys)
            {
                if (key.equalsIgnoreCase(k))
                {
                    return command;
                }
            }
        }

        return null;
    }

    /**
     * Disposes the CommandHandler.
     */
    public void dispose()
    {
        commands.clear();

        Emulator.getLogging().logShutdownLine("Command Handler -> Disposed!");
    }

    /**
     * Sort all commands based on their permission in alphabetical order.
     */
    private static final Comparator<Command> ALPHABETICAL_ORDER = new Comparator<Command>() {
        public int compare(Command c1, Command c2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(c1.permission, c2.permission);
            return (res != 0) ? res : c1.permission.compareTo(c2.permission);
        }
    };
}