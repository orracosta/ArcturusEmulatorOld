package com.eu.habbo.plugin;

import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 * Created on 28-2-2015 11:11.
 */
public abstract class HabboPlugin
{
    public final THashMap<Class<? extends Event>, THashSet<Method>> registeredEvents = new THashMap<Class <? extends Event>, THashSet<Method>>();

    public HabboPluginConfiguration configuration;

    public abstract void onEnable();

    public abstract void onDisable();

    public boolean isRegistered(Class<? extends  Event> clazz)
    {
        return this.registeredEvents.containsKey(clazz);
    }

    public abstract boolean hasPermission(Habbo habbo, String key);

    public URLClassLoader classLoader;

    public InputStream stream;
}
