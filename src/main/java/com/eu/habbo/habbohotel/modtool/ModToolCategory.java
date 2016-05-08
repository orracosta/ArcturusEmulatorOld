package com.eu.habbo.habbohotel.modtool;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * Created on 4-11-2014 16:11.
 */
public class ModToolCategory
{
    private final String name;
    private final TIntObjectMap<ModToolPreset> presets;

    public ModToolCategory(String name)
    {
        this.name = name;

        this.presets = TCollections.synchronizedMap(new TIntObjectHashMap<ModToolPreset>());
    }

    public void addPreset(ModToolPreset preset)
    {
        this.presets.put(preset.id, preset);
    }

    public TIntObjectMap<ModToolPreset> getPresets()
    {
        return this.presets;
    }

    public String getName()
    {
        return name;
    }
}
