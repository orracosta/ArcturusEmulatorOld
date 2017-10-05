package com.habboproject.server.game.groups.items;

import com.habboproject.server.game.groups.items.types.*;
import com.habboproject.server.storage.queries.groups.GroupItemDao;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupItemManager {
    private List<GroupBase> bases;
    private List<GroupSymbol> symbols;
    private List<GroupBaseColour> baseColours;
    private Map<Integer, GroupSymbolColour> symbolColours;
    private Map<Integer, GroupBackgroundColour> backgroundColours;

    private static final Logger log = Logger.getLogger(GroupItemManager.class.getName());

    public GroupItemManager() {
        this.load();
    }

    public void load() {
        if (bases == null) {
            // If bases is null, gotta assume all the others are...
            bases = new ArrayList<>();
            symbols = new ArrayList<>();
            baseColours = new ArrayList<>();
            symbolColours = new HashMap<>();
            backgroundColours = new HashMap<>();
        } else {
            bases.clear();
            symbols.clear();
            baseColours.clear();
            symbolColours.clear();
            backgroundColours.clear();
        }

        int itemCount = GroupItemDao.loadGroupItems(bases, symbols, baseColours, symbolColours, backgroundColours);

        log.info("Loaded " + itemCount + " group items");
    }

    public List<GroupBase> getBases() {
        return this.bases;
    }

    public List<GroupSymbol> getSymbols() {
        return this.symbols;
    }

    public List<GroupBaseColour> getBaseColours() {
        return this.baseColours;
    }

    public Map<Integer, GroupSymbolColour> getSymbolColours() {
        return this.symbolColours;
    }

    public Map<Integer, GroupBackgroundColour> getBackgroundColours() {
        return this.backgroundColours;
    }
}
