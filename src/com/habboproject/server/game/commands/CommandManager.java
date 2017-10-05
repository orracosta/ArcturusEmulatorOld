package com.habboproject.server.game.commands;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.development.*;
import com.habboproject.server.game.commands.gimmicks.KissCommand;
import com.habboproject.server.game.commands.gimmicks.PunchCommand;
import com.habboproject.server.game.commands.gimmicks.SexCommand;
import com.habboproject.server.game.commands.gimmicks.SlapCommand;
import com.habboproject.server.game.commands.misc.BigFixCommand;
import com.habboproject.server.game.commands.notifications.NotificationManager;
import com.habboproject.server.game.commands.staff.*;
import com.habboproject.server.game.commands.staff.alerts.*;
import com.habboproject.server.game.commands.staff.banning.BanCommand;
import com.habboproject.server.game.commands.staff.banning.IpBanCommand;
import com.habboproject.server.game.commands.staff.banning.MachineBanCommand;
import com.habboproject.server.game.commands.staff.bundles.BundleCommand;
import com.habboproject.server.game.commands.staff.cache.UpdateCacheCommand;
import com.habboproject.server.game.commands.staff.cache.ReloadGroupCommand;
import com.habboproject.server.game.commands.staff.fun.RollCommand;
import com.habboproject.server.game.commands.staff.muting.MuteCommand;
import com.habboproject.server.game.commands.staff.muting.RoomMuteCommand;
import com.habboproject.server.game.commands.staff.muting.UnmuteCommand;
import com.habboproject.server.game.commands.staff.rewards.*;
import com.habboproject.server.game.commands.staff.rewards.mass.MassBadgeCommand;
import com.habboproject.server.game.commands.staff.rewards.mass.MassCoinsCommand;
import com.habboproject.server.game.commands.staff.rewards.mass.MassDucketsCommand;
import com.habboproject.server.game.commands.staff.rewards.mass.MassPointsCommand;
import com.habboproject.server.game.commands.user.*;
import com.habboproject.server.game.commands.user.fun.DisableEventAlertCommand;
import com.habboproject.server.game.commands.user.fun.EnableEventAlertCommand;
import com.habboproject.server.game.commands.user.group.DeleteGroupCommand;
import com.habboproject.server.game.commands.user.room.*;
import com.habboproject.server.game.commands.user.settings.DisableCommand;
import com.habboproject.server.game.commands.user.settings.EnableCommand;
import com.habboproject.server.game.commands.user.settings.ToggleFriendsCommand;
import com.habboproject.server.game.commands.vip.*;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.CommandLogEntry;
import com.habboproject.server.modules.ModuleManager;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CommandManager implements Initializable {
    private static CommandManager commandManagerInstance;
    private static Logger log = Logger.getLogger(CommandManager.class.getName());

    private NotificationManager notifications;
    private Map<String, ChatCommand> commands;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Initialize the commands map and load all commands
     */
    public CommandManager() {

    }

    @Override
    public void initialize() {
        this.commands = new HashMap<>();

        this.reloadAllCommands();
        log.info("Loaded " + commands.size() + " chat commands");

        this.notifications = new NotificationManager();
        log.info("CommandManager initialized");
    }

    public static CommandManager getInstance() {
        if (commandManagerInstance == null) {
            commandManagerInstance = new CommandManager();
        }

        return commandManagerInstance;
    }

    public void reloadAllCommands() {
        this.commands.clear();

        this.loadMiscCommands();
        this.loadUserCommands();
        this.loadStaffCommands();

        if (Comet.isDebugging) {
            this.addCommand("reloadmapping", new ReloadMappingCommand());
        }

        this.addCommand("instancestats", new InstanceStatsCommand());
        this.addCommand("roomgrid", new RoomGridCommand());
        this.addCommand("processtimes", new ProcessTimesCommand());
        //this.addCommand("011039812438qwidjkasjo83", new FastProcessCommand());
        this.addCommand("itemid", new ItemVirtualIdCommand());
        //this.addCommand("comet", new CometCommand());
    }

    public void loadMiscCommands() {
        this.addCommand("arrumarhebbo123", new BigFixCommand());
    }

    /**
     * Loads all user commands
     */
    public void loadUserCommands() {
        this.addCommand(Locale.get("command.commands.name"), new CommandsCommand());
        this.addCommand(Locale.get("command.about.name"), new AboutCommand());
        this.addCommand(Locale.get("command.pickall.name"), new PickAllCommand());
        this.addCommand(Locale.get("command.empty.name"), new EmptyCommand());
        this.addCommand(Locale.get("command.sit.name"), new SitCommand());
        this.addCommand(Locale.get("command.lay.name"), new LayCommand());
        this.addCommand(Locale.get("command.home.name"), new HomeCommand());
        this.addCommand(Locale.get("command.setmax.name"), new SetMaxCommand());
        this.addCommand(Locale.get("command.position.name"), new PositionCommand());
        this.addCommand(Locale.get("command.deletegroup.name"), new DeleteGroupCommand());
        this.addCommand(Locale.get("command.togglefriends.name"), new ToggleFriendsCommand());
        this.addCommand(Locale.get("command.enablecommand.name"), new EnableCommand());
        this.addCommand(Locale.get("command.disablecommand.name"), new DisableCommand());
        this.addCommand("screenshot", new ScreenshotCommand());

        // VIP commands
        this.addCommand(Locale.get("command.push.name"), new PushCommand());
        this.addCommand(Locale.get("command.pull.name"), new PullCommand());
        this.addCommand(Locale.get("command.moonwalk.name"), new MoonwalkCommand());
        this.addCommand(Locale.get("command.enable.name"), new EffectCommand());
        this.addCommand(Locale.get("command.setspeed.name"), new SetSpeedCommand());
        this.addCommand(Locale.get("command.mimic.name"), new MimicCommand());
        this.addCommand(Locale.get("command.transform.name"), new TransformCommand());
        this.addCommand(Locale.get("command.noface.name"), new NoFaceCommand());
        this.addCommand(Locale.get("command.follow.name"), new FollowCommand());
        this.addCommand(Locale.get("command.superpull.name"), new SuperPullCommand());
        this.addCommand(Locale.get("command.redeemcredits.name"), new RedeemCreditsCommand());
        this.addCommand(Locale.get("command.handitem.name"), new HandItemCommand());
        this.addCommand(Locale.get("command.togglediagonal.name"), new ToggleDiagonalCommand());

        // Gimmick commands
        this.addCommand(Locale.get("command.slap.name"), new SlapCommand());
        this.addCommand(Locale.get("command.kiss.name"), new KissCommand());
        this.addCommand(Locale.get("command.sex.name"), new SexCommand());
        this.addCommand(Locale.get("command.punch.name"), new PunchCommand());

        // Room commands
        this.addCommand(Locale.get("command.reloadroom.name"), new ReloadCommand());
        this.addCommand(Locale.get("command.unloadroom.name"), new UnloadCommand());

        // Fun commands
        this.addCommand(Locale.get("command.enableeventnotif.name"), new EnableEventAlertCommand());
        this.addCommand(Locale.get("command.disableeventnotif.name"), new DisableEventAlertCommand());
    }

    /**
     * Loads all staff commands
     */
    public void loadStaffCommands() {
        this.addCommand(Locale.get("command.promotionpoints.name"), new PromoPointsCommand());
        this.addCommand(Locale.get("command.eventpoints.name"), new EventPointsCommand());
        this.addCommand(Locale.get("command.teleport.name"), new TeleportCommand());
        this.addCommand(Locale.get("command.massmotd.name"), new MassMotdCommand());
        this.addCommand(Locale.get("command.hotelalert.name"), new HotelAlertCommand());
        this.addCommand(Locale.get("command.invisible.name"), new InvisibleCommand());
        this.addCommand(Locale.get("command.ban.name"), new BanCommand());
        this.addCommand(Locale.get("command.kick.name"), new KickCommand());
        this.addCommand(Locale.get("command.disconnect.name"), new DisconnectCommand());
        this.addCommand(Locale.get("command.ipban.name"), new IpBanCommand());
        this.addCommand(Locale.get("command.alert.name"), new AlertCommand());
        this.addCommand(Locale.get("command.roomalert.name"), new RoomAlertCommand());
        this.addCommand(Locale.get("command.givebadge.name"), new GiveBadgeCommand());
        this.addCommand(Locale.get("command.removebadge.name"), new RemoveBadgeCommand());
        this.addCommand(Locale.get("command.roomkick.name"), new RoomKickCommand());
        this.addCommand(Locale.get("command.coins.name"), new CoinsCommand());
        this.addCommand(Locale.get("command.points.name"), new PointsCommand());
        this.addCommand(Locale.get("command.duckets.name"), new DucketsCommand());
        this.addCommand(Locale.get("command.roommute.name"), new RoomMuteCommand());
        this.addCommand(Locale.get("command.reload.name"), new UpdateCacheCommand());
        this.addCommand(Locale.get("command.maintenance.name"), new MaintenanceCommand());
        this.addCommand(Locale.get("command.roomaction.name"), new RoomActionCommand());
        this.addCommand(Locale.get("command.eventalert.name"), new EventAlertCommand());
        this.addCommand(Locale.get("command.machineban.name"), new MachineBanCommand());
        this.addCommand(Locale.get("command.makesay.name"), new MakeSayCommand());
        this.addCommand(Locale.get("command.mute.name"), new MuteCommand());
        this.addCommand(Locale.get("command.unmute.name"), new UnmuteCommand());
        this.addCommand(Locale.get("command.masscoins.name"), new MassCoinsCommand());
        this.addCommand(Locale.get("command.massbadge.name"), new MassBadgeCommand());
        this.addCommand(Locale.get("command.massduckets.name"), new MassDucketsCommand());
        this.addCommand(Locale.get("command.masspoints.name"), new MassPointsCommand());
        this.addCommand(Locale.get("command.playerinfo.name"), new PlayerInfoCommand());
        this.addCommand(Locale.get("command.roombadge.name"), new RoomBadgeCommand());
        this.addCommand(Locale.get("command.shutdown.name"), new ShutdownCommand());
        this.addCommand(Locale.get("command.summon.name"), new SummonCommand());
        this.addCommand(Locale.get("command.hotelalertlink.name"), new HotelAlertLinkCommand());
        this.addCommand(Locale.get("command.gotoroom.name"), new GotoRoomCommand());
        this.addCommand(Locale.get("command.notification.name"), new NotificationCommand());
        this.addCommand(Locale.get("command.quickpoll.name"), new QuickPollCommand());

        // Room bundles
        this.addCommand(Locale.get("command.bundle.name"), new BundleCommand());

        // Cache
        this.addCommand(Locale.get("command.reloadgroup.name"), new ReloadGroupCommand());

        // Fun
        this.addCommand(Locale.get("command.roll.name"), new RollCommand());
    }

    /**
     * Checks whether the request is a valid command alias
     *
     * @param message The requested command alias
     * @return The result of the check
     */
    public boolean isCommand(String message) {
        if (message.length() <= 1 || message.startsWith(" "))
            return false;

        String command = null;
        if ((command = message.split(" ")[0]) == null) {
            return false;
        }

        String executor = command.toLowerCase();

        boolean isCommand = executor.equals(Locale.get("command.commands.name")) || commands.containsKey(executor.substring(1)) || ModuleManager.getInstance().getEventHandler().getCommands().containsKey(executor);

        if (!isCommand) {
            for (String keys : this.commands.keySet()) {
                final List<String> keyList = Lists.newArrayList(keys.split(","));

                if (keyList.contains(executor)) {
                    return true;
                }
            }
        }

        return isCommand;
    }

    /**
     * Attempts to execute the given command
     *
     * @param message The alias of the command and the parameters
     * @param client  The player who is attempting to execute the command
     * @throws Exception
     */
    public boolean parse(String message, Session client) throws Exception {
        if (message.startsWith(" ")) return false;

        String executor = message.split(" ")[0].toLowerCase();

        final ChatCommand chatCommand = this.get(executor);

        if (chatCommand == null) return false;

        String commandName = chatCommand.getPermission();

        if (client.getPlayer().getPermissions().hasCommand(commandName) || commandName.equals("")) {
            if (client.getPlayer().getEntity().getRoom().getData().getDisabledCommands().contains(commandName)) {
                ChatCommand.sendNotif(Locale.get("command.disabled"), client);
                return true;
            }

            final String[] params = getParams(message.split(" "));

            if (chatCommand == null) {
                ModuleManager.getInstance().getEventHandler().handleCommand(client, executor, params);
            } else {
                if (chatCommand.isAsync()) {
                    this.executorService.submit(new ChatCommand.Execution(chatCommand, params, client));
                } else {
                    chatCommand.execute(client, params);
                }
            }

            try {
                if (LogManager.ENABLED)
                    LogManager.getInstance().getStore().getLogEntryContainer().put(new CommandLogEntry(client.getPlayer().getEntity().getRoom().getId(), client.getPlayer().getId(), message));
            } catch (Exception ignored) {

            }

            return true;
        } else {
            if (PermissionsManager.getInstance().getCommands().containsKey(commandName) &&
                    PermissionsManager.getInstance().getCommands().get(commandName).isVipOnly() &&
                    !client.getPlayer().getData().isVip())
                ChatCommand.sendNotif(Locale.get("command.vip"), client);

            log.debug(client.getPlayer().getData().getUsername() + " tried executing command: :" + message);
            return false;
        }
    }

    /**
     * Gets the parameters from the command that was executed (removing the first record of this array)
     *
     * @param splitStr The executed command, split by " "
     * @return The parameters for the command
     */
    private String[] getParams(String[] splitStr) {
        String[] a = new String[splitStr.length - 1];

        for (int i = 0; i < splitStr.length; i++) {
            if (i == 0) {
                continue;
            }

            a[i - 1] = splitStr[i];
        }

        return a;
    }

    public ChatCommand get(final String executor) {
        if (this.commands.containsKey(executor))
            return this.commands.get(executor);

        for (String keys : this.commands.keySet()) {
            final List<String> keyList = Lists.newArrayList(keys.split(","));

            if (keyList.contains(executor)) {
                return this.commands.get(keys);
            }
        }

        return null;
    }

    private void addCommand(String executor, ChatCommand command) {
        this.commands.put(":" + executor, command);
    }

    public NotificationManager getNotifications() {
        return notifications;
    }

    public Map<String, ChatCommand> getChatCommands() {
        return this.commands;
    }
}
