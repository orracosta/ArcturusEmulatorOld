package com.eu.habbo.plugin;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Easter;
import com.eu.habbo.habbohotel.games.battlebanzai.BattleBanzaiGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.tag.TagGame;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.messages.PacketManager;
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
                    throw new NullPointerException("Invalid Jar! Missing plugin.json in: " + file.getName());
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
            this.methods.add(RoomLayout.class.getMethod("configurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(FreezeGame.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(PacketManager.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserEvent", UserDisconnectEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserEvent", UserExitRoomEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserEvent", UserSavedLookEvent.class));
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
}
