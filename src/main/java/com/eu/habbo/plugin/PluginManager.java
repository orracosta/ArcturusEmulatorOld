package com.eu.habbo.plugin;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Easter;
import com.eu.habbo.habbohotel.bots.BotManager;
import com.eu.habbo.habbohotel.catalog.marketplace.MarketPlace;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.tag.TagGame;
import com.eu.habbo.habbohotel.items.ItemManager;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.messenger.Messenger;
import com.eu.habbo.habbohotel.modtool.WordFilter;
import com.eu.habbo.habbohotel.navigation.NavigatorManager;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.HabboInventory;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.messages.incoming.users.UserSaveLookEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.users.*;
import com.eu.habbo.threading.runnables.RoomTrashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.NoSuchElementException;

public class PluginManager
{
    private final THashSet<HabboPlugin> plugins = new THashSet<HabboPlugin>();
    private final THashSet<Method>      methods = new THashSet<Method>();

    public void loadPlugins()
    {
        this.disposePlugins();

        File loc = new File("plugins");

        if (!loc.exists())
        {
            if (loc.mkdirs())
            {
                Emulator.getLogging().logStart("Created plugins directory!");
            }
        }

        for (File file : loc.listFiles(new FileFilter() {
            public boolean accept(File file) {return file.getPath().toLowerCase().endsWith(".jar");}
        }))
        {
            URLClassLoader urlClassLoader = null;
            InputStream stream            = null;
            try
            {
                urlClassLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});
                stream         = urlClassLoader.getResourceAsStream("plugin.json");

                if (stream == null)
                {
                    throw new RuntimeException("Invalid Jar! Missing plugin.json in: " + file.getName());
                }

                byte[] content = new byte[stream.available()];

                if (stream.read(content) > 0)
                {
                    String body = new String(content);

                    Gson gson = new GsonBuilder().create();
                    HabboPluginConfiguration pluginConfigurtion = gson.fromJson(body, HabboPluginConfiguration.class);

                    try
                    {
                        Class<?> clazz = urlClassLoader.loadClass(pluginConfigurtion.main);
                        Class<? extends HabboPlugin> stackClazz = clazz.asSubclass(HabboPlugin.class);
                        Constructor<? extends HabboPlugin> constructor = stackClazz.getConstructor();
                        HabboPlugin plugin = constructor.newInstance();
                        plugin.configuration = pluginConfigurtion;
                        plugin.classLoader = urlClassLoader;
                        plugin.stream = stream;
                        this.plugins.add(plugin);
                        plugin.onEnable();
                    }
                    catch (Exception e)
                    {
                        Emulator.getLogging().logErrorLine("Could not load plugin " + pluginConfigurtion.name + "!");
                        Emulator.getLogging().logErrorLine(e);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register a new event listener for the given HabboPlugin
     * @param plugin The HabboPlugin to register this event for.
     * @param listener The EventListener this plugin should handle.
     */
    public void registerEvents(HabboPlugin plugin, EventListener listener)
    {
        synchronized (plugin.registeredEvents)
        {
            Method[] methods = listener.getClass().getMethods();

            for (Method method : methods)
            {
                if (method.getAnnotation(EventHandler.class) != null)
                {
                    if (method.getParameterTypes().length == 1)
                    {
                        if(Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                        {
                            final Class<?> eventClass = method.getParameterTypes()[0];

                            if (!plugin.registeredEvents.containsKey(eventClass.asSubclass(Event.class)))
                            {
                                plugin.registeredEvents.put(eventClass.asSubclass(Event.class), new THashSet<Method>());
                            }

                            plugin.registeredEvents.get(eventClass.asSubclass(Event.class)).add(method);
                        }
                    }
                }
            }
        }
    }

    /**
     * Fires an event and passes it down to all plugins handling it.
     * @param event The event to be passed down.
     */
    public Event fireEvent(Event event)
    {
        for (Method method : this.methods)
        {
            if(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
            {
                try
                {
                    method.invoke(null, event);
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine("Could not pass default event " + event.getClass().getName() + " to " + method.getClass().getName() + ":" + method.getName());
                    Emulator.getLogging().logErrorLine(e);
                }
            }
        }

        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin plugin = iterator.next();

                if (plugin != null)
                {
                    THashSet<Method> methods = plugin.registeredEvents.get(event.getClass().asSubclass(Event.class));

                    if(methods != null)
                    {
                        for(Method method : methods)
                        {
                            try
                            {
                                method.invoke(plugin, event);
                            }
                            catch (Exception e)
                            {
                                Emulator.getLogging().logErrorLine("Could not pass event " + event.getClass().getName() + " to " + plugin.configuration.name);
                                Emulator.getLogging().logErrorLine(e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        return event;
    }

    /**
     * Checks wether an event has been registered.
     * @param clazz The event class that has to be checked.
     * @param pluginsOnly Wether this should only be checked in plugin events and thus ignoring emulator events.
     * @return Wether the event is registered.
     */
    public boolean isRegistered(Class<? extends Event> clazz, boolean pluginsOnly)
    {
        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin plugin = iterator.next();
                if(plugin.isRegistered(clazz))
                    return true;
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }

        if(!pluginsOnly)
        {
            for (Method method : this.methods)
            {
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(clazz))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Disposes the plugin manager.
     */
    public void dispose()
    {
        this.disposePlugins();

        Emulator.getLogging().logShutdownLine("Disposed Plugin Manager!");
    }

    private void disposePlugins()
    {
        TObjectHashIterator<HabboPlugin> iterator = this.plugins.iterator();
        while (iterator.hasNext())
        {
            try
            {
                HabboPlugin p = iterator.next();

                if (p != null)
                {
                    p.onDisable();

                    try
                    {
                        p.stream.close();
                        p.classLoader.close();
                    }
                    catch (IOException e)
                    {
                        Emulator.getLogging().logErrorLine(e);
                    }
                }
            }
            catch (NoSuchElementException e)
            {
                break;
            }
        }
        this.plugins.clear();
    }

    public void reload()
    {
        long millis = System.currentTimeMillis();

        this.methods.clear();

        this.loadPlugins();

        Emulator.getLogging().logStart("Plugin Manager -> Loaded! " + this.plugins.size() + " plugins! (" + (System.currentTimeMillis() - millis) + " MS)");

        this.registerDefaultEvents();
    }

    private void registerDefaultEvents()
    {
        try
        {
            this.methods.add(FreezeGame.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(BattleBanzaiGame.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(RoomTrashing.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(Easter.class.getMethod("onUserChangeMotto", UserSavedMottoEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserLookAtPoint", RoomUnitLookAtPointEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(FreezeGame.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(PacketManager.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserDisconnectEvent", UserDisconnectEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserExitRoomEvent", UserExitRoomEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserSavedLookEvent", UserSavedLookEvent.class));
            this.methods.add(PluginManager.class.getMethod("globalOnConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
        }
        catch (NoSuchMethodException e)
        {
            Emulator.getLogging().logStart("Failed to define default events!");
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public THashSet<HabboPlugin> getPlugins()
    {
        return this.plugins;
    }

    @EventHandler
    public static void globalOnConfigurationUpdated(EmulatorConfigUpdatedEvent event)
    {
        ItemManager.RECYCLER_ENABLED = Emulator.getConfig().getBoolean("hotel.catalog.recycler.enabled");
        MarketPlace.MARKETPLACE_ENABLED = Emulator.getConfig().getBoolean("hotel.marketplace.enabled");
        MarketPlace.MARKETPLACE_CURRENCY = Emulator.getConfig().getInt("hotel.marketplace.currency");
        Messenger.SAVE_PRIVATE_CHATS = Emulator.getConfig().getBoolean("save.private.chats", false);
        PacketManager.DEBUG_SHOW_PACKETS = Emulator.getConfig().getBoolean("debug.show.packets");
        Room.HABBO_CHAT_DELAY = Emulator.getConfig().getBoolean("room.chat.delay", false);
        RoomChatMessage.SAVE_ROOM_CHATS = Emulator.getConfig().getBoolean("save.room.chats", false);
        RoomLayout.MAXIMUM_STEP_HEIGHT = Emulator.getConfig().getDouble("pathfinder.step.maximum.height", 1.1);
        RoomLayout.ALLOW_FALLING = Emulator.getConfig().getBoolean("pathfinder.step.allow.falling", true);
        RoomTrade.TRADING_ENABLED = Emulator.getConfig().getBoolean("hotel.trading.enabled");
        WordFilter.ENABLED_FRIENDCHAT = Emulator.getConfig().getBoolean("hotel.wordfilter.messenger");

        BotManager.MINIMUM_CHAT_SPEED = Emulator.getConfig().getInt("hotel.bot.chat.minimum.interval");
        HabboInventory.MAXIMUM_ITEMS = Emulator.getConfig().getInt("hotel.inventory.max.items");
        Messenger.MAXIMUM_FRIENDS = Emulator.getConfig().getInt("hotel.max.friends");
        Room.MAXIMUM_BOTS = Emulator.getConfig().getInt("hotel.max.bots.room");
        Room.MAXIMUM_PETS = Emulator.getConfig().getInt("hotel.pets.max.room");
        Room.HAND_ITEM_TIME = Emulator.getConfig().getInt("hotel.rooms.handitem.time");
        Room.IDLE_CYCLES = Emulator.getConfig().getInt("hotel.roomuser.idle.cycles", 240);
        Room.IDLE_CYCLES_KICK = Emulator.getConfig().getInt("hotel.roomuser.idle.cycles.kick", 480);
        RoomManager.MAXIMUM_ROOMS_VIP = Emulator.getConfig().getInt("hotel.max.rooms.vip");
        RoomManager.MAXIMUM_ROOMS_USER = Emulator.getConfig().getInt("hotel.max.rooms.user");
        RoomManager.HOME_ROOM_ID = Emulator.getConfig().getInt("hotel.home.room");
        WiredHandler.MAXIMUM_FURNI_SELECTION = Emulator.getConfig().getInt("hotel.wired.furni.selection.count");
        WiredHandler.TELEPORT_DELAY = Emulator.getConfig().getInt("wired.effect.teleport.delay", 500);
        NavigatorManager.MAXIMUM_RESULTS_PER_PAGE = Emulator.getConfig().getInt("hotel.navigator.search.maxresults");

        String[] bannedBubbles = Emulator.getConfig().getValue("commands.cmd_chatcolor.banned_numbers").split(";");
        RoomChatMessage.BANNED_BUBBLES = new int[bannedBubbles.length];
        for (int i = 0; i < RoomChatMessage.BANNED_BUBBLES.length; i++)
        {
            try
            {
                RoomChatMessage.BANNED_BUBBLES[i] = Integer.valueOf(bannedBubbles[i]);
            }
            catch (Exception e)
            {}
        }

        HabboManager.WELCOME_MESSAGE = Emulator.getConfig().getValue("hotel.welcome.alert.message");
        Room.PREFIX_FORMAT = Emulator.getConfig().getValue("room.chat.prefix.format");
    }
}
