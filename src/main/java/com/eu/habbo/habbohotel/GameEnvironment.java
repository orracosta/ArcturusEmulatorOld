package com.eu.habbo.habbohotel;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.CreditsScheduler;
import com.eu.habbo.core.PixelScheduler;
import com.eu.habbo.core.PointsScheduler;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.commands.CommandHandler;
import com.eu.habbo.habbohotel.crafting.CraftingManager;
import com.eu.habbo.habbohotel.guides.GuideManager;
import com.eu.habbo.habbohotel.guilds.GuildManager;
import com.eu.habbo.habbohotel.guilds.forums.GuildForumManager;
import com.eu.habbo.habbohotel.hotelview.HotelViewManager;
import com.eu.habbo.habbohotel.items.ItemManager;
import com.eu.habbo.habbohotel.modtool.ModToolManager;
import com.eu.habbo.habbohotel.modtool.WordFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorManager;
import com.eu.habbo.habbohotel.permissions.PermissionsManager;
import com.eu.habbo.habbohotel.pets.PetManager;
import com.eu.habbo.habbohotel.polls.PollManager;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.habbohotel.users.HabboManager;

public class GameEnvironment
{
    private HabboManager habboManager;
    private NavigatorManager navigatorManager;
    private GuildManager guildManager;
    private GuildForumManager guildForumManager;
    private ItemManager itemManager;
    private CatalogManager catalogManager;
    private HotelViewManager hotelViewManager;
    private RoomManager roomManager;
    private CommandHandler commandHandler;
    private PermissionsManager permissionsManager;
    private BotManager botManager;
    private ModToolManager modToolManager;
    private PetManager petManager;
    private AchievementManager achievementManager;
    private GuideManager guideManager;
    private WordFilter wordFilter;
    private CraftingManager craftingManager;
    private PollManager pollManager;

    public CreditsScheduler creditsScheduler;
    public PixelScheduler pixelScheduler;
    public PointsScheduler pointsScheduler;

    public void load()
    {
        Emulator.getLogging().logStart("GameEnvironment -> Loading...");

        this.permissionsManager = new PermissionsManager();
        this.habboManager       = new HabboManager();
        this.hotelViewManager   = new HotelViewManager();
        this.itemManager        = new ItemManager();
        this.itemManager.load();
        this.botManager         = new BotManager();
        this.petManager         = new PetManager();
        this.guildManager       = new GuildManager();
        this.guildForumManager  = new GuildForumManager();
        this.catalogManager     = new CatalogManager();
        this.roomManager        = new RoomManager();
        this.navigatorManager   = new NavigatorManager();
        this.commandHandler     = new CommandHandler();
        this.modToolManager     = new ModToolManager();
        this.achievementManager = new AchievementManager();
        this.achievementManager.reload();
        this.guideManager       = new GuideManager();
        this.wordFilter         = new WordFilter();
        this.craftingManager    = new CraftingManager();
        this.pollManager        = new PollManager();

        this.roomManager.loadPublicRooms();

        this.creditsScheduler   = new CreditsScheduler();
        Emulator.getThreading().run(this.creditsScheduler);
        this.pixelScheduler     = new PixelScheduler();
        Emulator.getThreading().run(this.pixelScheduler);
        this.pointsScheduler    = new PointsScheduler();
        Emulator.getThreading().run(this.pointsScheduler);

        Emulator.getLogging().logStart("GameEnvironment -> Loaded!");
    }

    public void dispose()
    {
        this.pointsScheduler.setDisposed(true);
        this.pixelScheduler.setDisposed(true);
        this.creditsScheduler.setDisposed(true);
        this.craftingManager.dispose();
        this.habboManager.dispose();
        this.commandHandler.dispose();
        this.guildManager.dispose();
        this.catalogManager.dispose();
        this.roomManager.dispose();
        this.itemManager.dispose();
        this.hotelViewManager.dispose();
        Emulator.getLogging().logShutdownLine("GameEnvironment -> Disposed!");
    }

    public HabboManager getHabboManager()
    {
        return this.habboManager;
    }

    public NavigatorManager getNavigatorManager()
    {
        return this.navigatorManager;
    }

    public GuildManager getGuildManager()
    {
        return this.guildManager;
    }

    public GuildForumManager getGuildForumManager()
    {
        return this.guildForumManager;
    }

    public ItemManager getItemManager()
    {
        return this.itemManager;
    }

    public CatalogManager getCatalogManager()
    {
        return this.catalogManager;
    }

    public HotelViewManager getHotelViewManager()
    {
        return this.hotelViewManager;
    }

    public RoomManager getRoomManager()
    {
        return this.roomManager;
    }

    public CommandHandler getCommandHandler()
    {
        return this.commandHandler;
    }

    public PermissionsManager getPermissionsManager()
    {
        return this.permissionsManager;
    }

    public BotManager getBotManager()
    {
        return this.botManager;
    }

    public ModToolManager getModToolManager()
    {
        return this.modToolManager;
    }

    public PetManager getPetManager()
    {
        return this.petManager;
    }

    public AchievementManager getAchievementManager()
    {
        return this.achievementManager;
    }

    public GuideManager getGuideManager()
    {
        return this.guideManager;
    }

    public WordFilter getWordFilter()
    {
        return this.wordFilter;
    }

    public CraftingManager getCraftingManager()
    {
        return this.craftingManager;
    }

    public PollManager getPollManager()
    {
        return this.pollManager;
    }
}
