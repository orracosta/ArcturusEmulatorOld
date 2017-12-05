package com.eu.habbo.essentials;

import com.eu.habbo.Emulator;
import com.eu.habbo.essentials.commands.*;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventListener;
import com.eu.habbo.plugin.HabboPlugin;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Essentials extends HabboPlugin implements EventListener
{
    public static Essentials INSTANCE = null;

    @Override
    public void onEnable()
    {
        INSTANCE = this;
        Emulator.getPluginManager().registerEvents(this, this);

        if (Emulator.isReady)
        {
            this.checkDatabase();
            BrbCommand.startBackgroundThread();
        }

        Emulator.getLogging().logStart("[Essentials] Started Essential Commands Plugin!");
    }

    @Override
    public void onDisable()
    {

    }

    @EventHandler
    public static void onEmulatorLoaded(EmulatorLoadedEvent event)
    {
        INSTANCE.checkDatabase();
        BrbCommand.startBackgroundThread();
    }

    @Override
    public boolean hasPermission(Habbo habbo, String s)
    {
        return false;
    }

    private void checkDatabase()
    {
        boolean reloadPermissions = false;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement())
        {
            statement.execute("ALTER TABLE  `emulator_texts` CHANGE  `value`  `value` VARCHAR( 4096 ) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL");
        }
        catch (SQLException e){}
        Emulator.getTexts().register("essentials.cmd_pay.keys", "pay;transfer");
        Emulator.getTexts().register("essentials.cmd_pay.incorrect.usage", "Missing arguments for pay command!");
        Emulator.getTexts().register("commands.description.cmd_pay", ":pay <username> <amount> <currency>");
        Emulator.getTexts().register("generic.habbo.notfound", "Habbo not found");
        Emulator.getTexts().register("essentials.cmd_pay.self", "You cannot pay yourself, silly!");
        Emulator.getTexts().register("essentials.cmd_pay.invalid_amount", "Invalid amount! Please use a positive value.");
        Emulator.getTexts().register("essentials.cmd_pay.not_enough", "Cannot pay %username%. You don't have %amount% %type%!");
        Emulator.getTexts().register("essentials.cmd_pay.invalid_type", "The currency %type% does not exist!");
        Emulator.getTexts().register("essentials.cmd_pay.received", "%username% has paid you %amount% %type%!");
        Emulator.getTexts().register("essentials.cmd_pay.transferred", "You paid %username% %amount% %type%!");
        reloadPermissions = this.registerPermission("cmd_pay", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_kill", ":kill <username>");
        Emulator.getTexts().register("essentials.cmd_kill.keys", "kill;murder");
        Emulator.getConfig().register("essentials.cmd_kill.effect.victim", "93;89");
        Emulator.getConfig().register("essentials.cmd_kill.effect.killer", "164;182");
        Emulator.getTexts().register("essentials.cmd_kill.killmessages.killer", "* Murders %victim% *;* %victim% was slain by %killer% *;* Takes %victim%s head off *");
        Emulator.getTexts().register("essentials.cmd_kill.killmessages.victim", "* Bleeds to dead while twitching *; * Oh noes. %killer% took my head off *");
        reloadPermissions = this.registerPermission("cmd_kill", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_hoverboard", ":hoverboard");
        Emulator.getTexts().register("essentials.cmd_hoverboard.keys", "hoverboard");
        Emulator.getTexts().register("essentials.cmd_hoverboard.texts", "* Grabs Blue hoverboard and flies away *;* Jumps onto a pink hoverboard *;* Nice day to ride my yellow hoverboard *;* Pulls out a green hoverboard to explore the hotel on *");
        reloadPermissions = this.registerPermission("cmd_hoverboard", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_kiss", ":kiss <username>");
        Emulator.getTexts().register("essentials.kissedmessages.kissedperson", "* %kissedperson% gets kissed by %kisser% *");
        Emulator.getTexts().register("essentials.kissedmessages.kisser", "* Kisses %kissedperson% passionately *");
        Emulator.getTexts().register("essentials.cmd_kiss.keys", "kiss;frenchkiss");
        Emulator.getTexts().register("essentials.kissmessages.tofar", "%kissedperson% is too far to kiss!");
        reloadPermissions = this.registerPermission("cmd_kiss", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_hug", ":hug <username>");
        Emulator.getTexts().register("essentials.cmd_hug.keys", "hug;cuddle");
        Emulator.getTexts().register("essentials.hugmessages.huggedperson", "* Gets a warm hug from %hugger% *");
        Emulator.getTexts().register("essentials.hugmessages.hugger", "* Hugs %huggedperson% tightly *");
        Emulator.getTexts().register("essentials.hugmessages.tofar", "%huggedperson% is too far to hug. Get a bit closer and try again!");
        reloadPermissions = this.registerPermission("cmd_hug", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_welcome", ":welcome <username>");
        Emulator.getTexts().register("essentials.cmd_welcome.keys", "welcome;willkommen;welkom;bienvenue;bienvenida;bem-vindo");
        Emulator.getTexts().register("essentials.cmd_welcome.text", "Welcome %username% to %hotelname%;My name is %greeter_username% and I am %greeter_rank% at %hotelname%;At the moment there are %onlinecount% %hotelplayername%s online!;If you have any further questions feel free to ask me or any of our staff. You can recognize our staff from the badge they wear;Enjoy your stay in %hotelname% %username%!");
        reloadPermissions = this.registerPermission("cmd_welcome", "'0', '1', '2'", "0", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_disable_effects", ":disableffects");
        Emulator.getTexts().register("essentials.cmd_disable_effects.keys", "antieffects;disableffects");
        Emulator.getTexts().register("essentials.cmd_disable_effects.effects_enabled", "Effects in this room have been enabled!");
        Emulator.getTexts().register("essentials.cmd_disable_effects.effects_disabled", "Effects in this room have been disabled!");
        reloadPermissions = this.registerPermission("cmd_disable_effects", "'0', '1', '2'", "2", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_brb", ":brb");
        Emulator.getTexts().register("essentials.cmd_brb.keys", "afk;brb;");
        Emulator.getTexts().register("essentials.cmd_brb.brb", "* %username% is now AFK! *");
        Emulator.getTexts().register("essentials.cmd_brb.back", "* %username% is now back! *");
        Emulator.getTexts().register("essentials.cmd_brb.time", "* %username% has now been away for %time% minutes *");
        reloadPermissions = this.registerPermission("cmd_brb", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_nuke", ":nuke <username>");
        Emulator.getTexts().register("essentials.cmd_nuke.keys", "nuke;nade;grenade;explode;boom");
        Emulator.getTexts().register("essentials.nuke.self", "You can't nuke yourself, silly!");
        Emulator.getTexts().register("essentials.nuke.action", "* Launches nuke towards %username% *");
        Emulator.getTexts().register("essentials.nuke.nuked", "* %username% gets obliterated *");
        reloadPermissions = this.registerPermission("cmd_nuke", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_buildheight", ":buildheight [height]");
        Emulator.getTexts().register("essentials.cmd_buildheight.keys", "buildheight;bh");
        Emulator.getTexts().register("essentials.cmd_buildheight.invalid_height", "Invalid height! Build height must be between 0 - 40!");
        Emulator.getTexts().register("essentials.cmd_buildheight.changed", "Changed build height to %height%");
        Emulator.getTexts().register("essentials.cmd_buildheight.disabled", "Build height removed.");
        Emulator.getTexts().register("essentials.cmd_buildheight.not_specified", "No buildheight set. Height must be between 0 - 40.");
        reloadPermissions = this.registerPermission("cmd_buildheight", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_slime", ":slime <username>");
        Emulator.getTexts().register("essentials.cmd_slime.keys", "slime;slimed;goop;nickelodeon");
        Emulator.getTexts().register("essentials.cmd_slime.throws", "* Throws slime at %username%s direction *");
        Emulator.getTexts().register("essentials.cmd_slime.missed", "* Missed %username%s face *");
        Emulator.getTexts().register("essentials.cmd_slime.slimed", "* Gets covered in slime by %username% *");
        reloadPermissions = this.registerPermission("cmd_slime", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_explain", ":explain <command>");
        Emulator.getTexts().register("essentials.cmd_explain.keys", "explain;commandinfo;commandhelp;aliases");
        Emulator.getTexts().register("essentials.cmd_explain.help", "Specify a command to lookup. Example: :explain mimic");
        Emulator.getTexts().register("essentials.cmd_explain.notfound", "Too bad but '%command%'-command does not seem to exist");
        Emulator.getTexts().register("essentials.cmd_explain.nopermission", "You don't have permission to use %command%");
        Emulator.getTexts().register("essentials.cmd_explain.info", "Information about the '%command%'-command:\rUsage: %description%\rKeys / Aliases:%keys%");
        reloadPermissions = this.registerPermission("cmd_explain", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_buyroom", ":buyroom");
        Emulator.getTexts().register("commands.description.cmd_sellroom", ":sellroom <credits>");
        Emulator.getTexts().register("essentials.sellroom.keys", "sellroom");
        Emulator.getTexts().register("essentials.sellroom.removed", "This room was remove from sale.");
        Emulator.getTexts().register("essentials.sellroom.invalid_credits", "%credits% credits is an invalid amount!");
        Emulator.getTexts().register("essentials.sellroom.has_guild", "Cannot sell this room, room has a guild.");
        Emulator.getTexts().register("essentials.sellroom.confirmed", "This room is now up for sale for %credits% credits!");
        Emulator.getTexts().register("essentials.sellroom.usage", "Usage: :sellroom <credits>");
        Emulator.getTexts().register("essentials.sellroom.forsale", "This room is being sold by %ownername% for %credits%.");
        Emulator.getTexts().register("essentials.sellroom.notforsale", "This room is not being sold.");
        Emulator.getTexts().register("essentials.sellroom.confirmkey", "confirm");
        Emulator.getTexts().register("essentials.buyroom.has_guild", "This room has a guild and therefor cannot be bought.");
        Emulator.getTexts().register("essentials.sellroom.owneroffline", "Owner of this room, %username%, is offline.");
        Emulator.getTexts().register("essentials.sellroom.selfbuy", "Silly %username%. You cannot buy your own room.");
        Emulator.getTexts().register("essentials.sellroom.buyroom.usage", "To buy a room use :%key% %confirmkey%");
        Emulator.getTexts().register("essentials.buyroom.keys", "buyroom");
        reloadPermissions = this.registerPermission("cmd_sellroom", "'0', '1', '2'", "1", reloadPermissions);
        reloadPermissions = this.registerPermission("cmd_buyroom", "'0', '1', '2'", "1", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_closedice", ":closedice [all]");
        Emulator.getTexts().register("essentials.cmd_closedice.keys", "closedice;cd;sluitdices");
        Emulator.getTexts().register("essentials.cmd_closedice.keywordall", "all");
        Emulator.getTexts().register("essentials.cmd_closedice.closed", "* Closes %count% dices *");
        reloadPermissions = this.registerPermission("cmd_closedice", "'0', '1', '2'", "1", reloadPermissions);
        reloadPermissions = this.registerPermission("acc_closedice_room", "'0', '1', '2'", "2", reloadPermissions);

        Emulator.getTexts().register("commands.description.cmd_set", ":set info");
        Emulator.getTexts().register("essentials.cmd_set.keys", "set;changefurni");
        Emulator.getTexts().register("essentials.cmd_set.error", "Use ':set info' for more information!");
        Emulator.getTexts().register("essentials.cmd_set.invalid", "Invalid item id %value%");
        Emulator.getTexts().register("essentials.cmd_set.notfound", "Item not found.");
        Emulator.getTexts().register("essentials.cmd_set.wrong", "Something went wrong.");
        Emulator.getTexts().register("essentials.cmd_set.succes", "Updated!");
        reloadPermissions = this.registerPermission("cmd_set", "'0', '1', '2'", "1", reloadPermissions);

        if (reloadPermissions)
        {
            Emulator.getGameEnvironment().getPermissionsManager().reload();
        }

        CommandHandler.addCommand(new PayCommand("cmd_pay", Emulator.getTexts().getValue("essentials.cmd_pay.keys").split(";")));
        CommandHandler.addCommand(new KillCommand("cmd_kill", Emulator.getTexts().getValue("essentials.cmd_kill.keys").split(";")));
        CommandHandler.addCommand(new HoverBoardCommand());
        CommandHandler.addCommand(new KissCommand("cmd_kiss", Emulator.getTexts().getValue("essentials.cmd_kiss.keys").split(";")));
        CommandHandler.addCommand(new HugCommand("cmd_hug", Emulator.getTexts().getValue("essentials.cmd_hug.keys").split(";")));
        CommandHandler.addCommand(new WelcomeCommand("cmd_welcome", Emulator.getTexts().getValue("essentials.cmd_welcome.keys").split(";")));
        CommandHandler.addCommand(new DisableEffects("cmd_disable_effects", Emulator.getTexts().getValue("essentials.cmd_disable_effects.keys").split(";")));
        CommandHandler.addCommand(new BrbCommand("cmd_brb", Emulator.getTexts().getValue("essentials.cmd_brb.keys").split(";")));
        CommandHandler.addCommand(new NukePlayerCommand("cmd_nuke", Emulator.getTexts().getValue("essentials.cmd_nuke.keys").split(";")));
        CommandHandler.addCommand(new BuildHeightCommand("cmd_buildheight", Emulator.getTexts().getValue("essentials.cmd_buildheight.keys").split(";")));
        CommandHandler.addCommand(new SlimeCommand("cmd_slime", Emulator.getTexts().getValue("essentials.cmd_slime.keys").split(";")));
        CommandHandler.addCommand(new ExplainCommand("cmd_explain", Emulator.getTexts().getValue("essentials.cmd_explain.keys").split(";")));
        CommandHandler.addCommand(new SellRoomCommand("cmd_sellroom", Emulator.getTexts().getValue("essentials.sellroom.keys").split(";")));
        CommandHandler.addCommand(new BuyRoomCommand("cmd_buyroom", Emulator.getTexts().getValue("essentials.buyroom.keys").split(";")));
        CommandHandler.addCommand(new SellRoomCommand("cmd_sellroom", Emulator.getTexts().getValue("essentials.sellroom.keys").split(";")));
        CommandHandler.addCommand(new CloseDiceCommand("cmd_closedice", Emulator.getTexts().getValue("essentials.cmd_closedice.keys").split(";")));
        CommandHandler.addCommand(new SetFurnitureCommand("cmd_set", Emulator.getTexts().getValue("essentials.cmd_set.keys").split(";")));
    }

    private boolean registerPermission(String name, String options, String defaultValue, boolean defaultReturn)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE  `permissions` ADD  `" + name +"` ENUM(  " + options + " ) NOT NULL DEFAULT  '" + defaultValue + "'"))
            {
                statement.execute();
                return true;
            }
        }
        catch (SQLException e)
        {}

        return defaultReturn;
    }

    public static void main(String[] args)
    {
        System.out.println("Don't run this seperately");
    }
}