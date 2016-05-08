package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.*;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTeleporter;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTimer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGate;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredConditionType;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

/**
 * Created on 2-11-2014 22:21.
 */
public class RoomSpecialTypes
{
    private final THashMap<Integer, InteractionBattleBanzaiTeleporter> banzaiTeleporters;
    private final THashMap<Integer, InteractionNest> nests;
    private final THashMap<Integer, InteractionPetDrink> petDrinks;
    private final THashMap<Integer, InteractionPetFood> petFoods;
    private final THashMap<Integer, InteractionRoller> rollers;

    private final THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>> wiredTriggers;
    private final THashMap<WiredEffectType, THashSet<InteractionWiredEffect>> wiredEffects;
    private final THashMap<WiredConditionType, THashSet<InteractionWiredCondition>> wiredConditions;

    private final THashMap<Integer, InteractionGameScoreboard> gameScoreboards;
    private final THashMap<Integer, InteractionGameGate> gameGates;
    private final THashMap<Integer, InteractionGameTimer> gameTimers;

    private final THashMap<Integer, InteractionFreezeExitTile> freezeExitTile;
    private final THashMap<Integer, HabboItem> undefined;

    public RoomSpecialTypes()
    {
        this.banzaiTeleporters = new THashMap<Integer, InteractionBattleBanzaiTeleporter>();
        this.nests = new THashMap<Integer, InteractionNest>();
        this.petDrinks = new THashMap<Integer, InteractionPetDrink>();
        this.petFoods = new THashMap<Integer, InteractionPetFood>();
        this.rollers = new THashMap<Integer, InteractionRoller>();

        this.wiredTriggers = new THashMap<WiredTriggerType, THashSet<InteractionWiredTrigger>>();
        this.wiredEffects = new THashMap<WiredEffectType, THashSet<InteractionWiredEffect>>();
        this.wiredConditions = new THashMap<WiredConditionType, THashSet<InteractionWiredCondition>>();

        this.gameScoreboards = new THashMap<Integer, InteractionGameScoreboard>();
        this.gameGates = new THashMap<Integer, InteractionGameGate>();
        this.gameTimers = new THashMap<Integer, InteractionGameTimer>();

        this.freezeExitTile = new THashMap<Integer, InteractionFreezeExitTile>();
        this.undefined = new THashMap<Integer, HabboItem>();
    }

    /*
        Banzai Teleporter
     */
    public InteractionBattleBanzaiTeleporter getBanzaiTeleporter(int itemId)
    {
        return this.banzaiTeleporters.get(itemId);
    }

    public void addBanzaiTeleporter(InteractionBattleBanzaiTeleporter item)
    {
        this.banzaiTeleporters.put(item.getId(), item);
    }

    public void removeBanzaiTeleporter(InteractionBattleBanzaiTeleporter item)
    {
        this.banzaiTeleporters.remove(item.getId());
    }

    public synchronized THashSet<InteractionBattleBanzaiTeleporter> getBanzaiTeleporters()
    {
        THashSet<InteractionBattleBanzaiTeleporter> battleBanzaiTeleporters = new THashSet<InteractionBattleBanzaiTeleporter>();
        battleBanzaiTeleporters.addAll(this.banzaiTeleporters.values());

        return battleBanzaiTeleporters;
    }

    public synchronized InteractionBattleBanzaiTeleporter getRandomTeleporter()
    {
        return (InteractionBattleBanzaiTeleporter)this.banzaiTeleporters.values().toArray()[Emulator.getRandom().nextInt(this.banzaiTeleporters.size())];
    }

    /*
        Nests
     */
    public InteractionNest getNest(int itemId)
    {
        return this.nests.get(itemId);
    }

    public void addNest(InteractionNest item)
    {
        this.nests.put(item.getId(), item);
    }

    public void removeNest(InteractionNest item)
    {
        this.nests.remove(item.getId());
    }

    public synchronized THashSet<InteractionNest> getNests()
    {
        THashSet<InteractionNest> nests = new THashSet<InteractionNest>();
        nests.addAll(this.nests.values());

        return nests;
    }

    /*
        Pet Drinks
     */
    public InteractionPetDrink getPetDrink(int itemId)
    {
        return this.petDrinks.get(itemId);
    }

    public void addPetDrink(InteractionPetDrink item)
    {
        this.petDrinks.put(item.getId(), item);
    }

    public void removePetDrink(InteractionPetDrink item)
    {
        this.petDrinks.remove(item.getId());
    }

    public synchronized THashSet<InteractionPetDrink> getPetDrinks()
    {
        THashSet<InteractionPetDrink> petDrinks = new THashSet<InteractionPetDrink>();
        petDrinks.addAll(this.petDrinks.values());

        return petDrinks;
    }

    /*
        Pet Foods.
     */
    public InteractionPetFood getPetFood(int itemId)
    {
        return this.petFoods.get(itemId);
    }

    public void addPetFood(InteractionPetFood item)
    {
        this.petFoods.put(item.getId(), item);
    }

    public void removePetFood(InteractionPetFood petFood)
    {
        this.petFoods.remove(petFood.getId());
    }

    public synchronized THashSet<InteractionPetFood> getPetFoods()
    {
        THashSet<InteractionPetFood> petFoods = new THashSet<InteractionPetFood>();
        petFoods.addAll(this.petFoods.values());

        return petFoods;
    }

    /*
        Rollers
     */
    public InteractionRoller getRoller(int itemId)
    {
        return this.rollers.get(itemId);
    }

    public void addRoller(InteractionRoller item)
    {
        this.rollers.put(item.getId(), item);
    }

    public void removeRoller(InteractionRoller roller)
    {
        this.rollers.remove(roller.getId());
    }

    public synchronized THashSet<InteractionRoller> getRollers()
    {
        THashSet<InteractionRoller> rollers = new THashSet<InteractionRoller>();
        rollers.addAll(this.rollers.values());

        return rollers;
    }

    /*
        Wired Triggers
     */
    public synchronized InteractionWiredTrigger getTrigger(int itemId)
    {
        for(Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet())
        {
            for(InteractionWiredTrigger trigger : map.getValue())
            {
                if(trigger.getId() == itemId)
                    return trigger;
            }
        }

        return null;
    }

    public synchronized THashSet<InteractionWiredTrigger> getTriggers()
    {
        THashSet<InteractionWiredTrigger> triggers = new THashSet<InteractionWiredTrigger>();

        for(Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet())
        {
            triggers.addAll(map.getValue());
        }

        return triggers;
    }

    public THashSet<InteractionWiredTrigger> getTriggers(WiredTriggerType type)
    {
        return this.wiredTriggers.get(type);
    }

    public THashSet<InteractionWiredTrigger> getTriggers(int x, int y)
    {
        THashSet<InteractionWiredTrigger> triggers = new THashSet<InteractionWiredTrigger>();

        for(Map.Entry<WiredTriggerType, THashSet<InteractionWiredTrigger>> map : this.wiredTriggers.entrySet())
        {
            for(InteractionWiredTrigger trigger : map.getValue())
            {
                if(trigger.getX() == x && trigger.getY() == y)
                    triggers.add(trigger);
            }
        }

        return triggers;
    }

    public void addTrigger(InteractionWiredTrigger trigger)
    {
        if(!this.wiredTriggers.containsKey(trigger.getType()))
            this.wiredTriggers.put(trigger.getType(), new THashSet<InteractionWiredTrigger>());

        this.wiredTriggers.get(trigger.getType()).add(trigger);
    }

    public void removeTrigger(InteractionWiredTrigger trigger)
    {
        this.wiredTriggers.get(trigger.getType()).remove(trigger);

        if(this.wiredTriggers.get(trigger.getType()).isEmpty())
        {
            this.wiredTriggers.remove(trigger.getType());
        }
    }

    /*
        Wired Effects
     */
    public InteractionWiredEffect getEffect(int itemId)
    {
        for(Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet())
        {
            for(InteractionWiredEffect effect : map.getValue())
            {
                if(effect.getId() == itemId)
                    return effect;
            }
        }

        return null;
    }

    public synchronized THashSet<InteractionWiredEffect> getEffects()
    {
        THashSet<InteractionWiredEffect> effects = new THashSet<InteractionWiredEffect>();

        for(Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet())
        {
            effects.addAll(map.getValue());
        }

        return effects;
    }

    public THashSet<InteractionWiredEffect> getEffects(WiredEffectType type)
    {
        return this.wiredEffects.get(type);
    }

    public synchronized THashSet<InteractionWiredEffect> getEffects(int x, int y)
    {
        THashSet<InteractionWiredEffect> effects = new THashSet<InteractionWiredEffect>();

        for(Map.Entry<WiredEffectType, THashSet<InteractionWiredEffect>> map : this.wiredEffects.entrySet())
        {
            for(InteractionWiredEffect effect : map.getValue())
            {
                if(effect.getX() == x && effect.getY() == y)
                    effects.add(effect);
            }
        }

        return effects;
    }

    public void addEffect(InteractionWiredEffect effect)
    {
        if(!this.wiredEffects.containsKey(effect.getType()))
            this.wiredEffects.put(effect.getType(), new THashSet<InteractionWiredEffect>());

        this.wiredEffects.get(effect.getType()).add(effect);
    }

    public void removeEffect(InteractionWiredEffect effect)
    {
        this.wiredEffects.get(effect.getType()).remove(effect);

        if(this.wiredEffects.get(effect.getType()).isEmpty())
        {
            this.wiredEffects.remove(effect.getType());
        }
    }

    /*
        Wired Conditions
     */
    public InteractionWiredCondition getCondition(int itemId)
    {
        for(Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet())
        {
            for(InteractionWiredCondition condition : map.getValue())
            {
                if(condition.getId() == itemId)
                    return condition;
            }
        }

        return null;
    }

    public THashSet<InteractionWiredCondition> getConditions()
    {
        THashSet<InteractionWiredCondition> conditions = new THashSet<InteractionWiredCondition>();

        for(Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet())
        {
            conditions.addAll(map.getValue());
        }

        return conditions;
    }

    public synchronized THashSet<InteractionWiredCondition> getConditions(WiredConditionType type)
    {
        return this.wiredConditions.get(type);
    }

    public synchronized THashSet<InteractionWiredCondition> getConditions(int x, int y)
    {
        THashSet<InteractionWiredCondition> conditions = new THashSet<InteractionWiredCondition>();

        for(Map.Entry<WiredConditionType, THashSet<InteractionWiredCondition>> map : this.wiredConditions.entrySet())
        {
            for(InteractionWiredCondition condition : map.getValue())
            {
                if(condition.getX() == x && condition.getY() == y)
                    conditions.add(condition);
            }
        }

        return conditions;
    }

    public void addCondition(InteractionWiredCondition condition)
    {
        if(!this.wiredConditions.containsKey(condition.getType()))
            this.wiredConditions.put(condition.getType(), new THashSet<InteractionWiredCondition>());

        this.wiredConditions.get(condition.getType()).add(condition);
    }

    public void removeCondition(InteractionWiredCondition condition)
    {
        this.wiredConditions.get(condition.getType()).remove(condition);

        if(this.wiredConditions.get(condition.getType()).isEmpty())
        {
            this.wiredConditions.remove(condition.getType());
        }
    }

    /*
        GameScoreboards
     */
    public InteractionGameScoreboard getGameScorebord(int itemId)
    {
        return this.gameScoreboards.get(itemId);
    }

    public void addGameScoreboard(InteractionGameScoreboard scoreboard)
    {
        this.gameScoreboards.put(scoreboard.getId(), scoreboard);
    }

    public void removeScoreboard(InteractionGameScoreboard scoreboard)
    {
        this.gameScoreboards.remove(scoreboard.getId());
    }

    public synchronized THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards()
    {
        THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<Integer, InteractionFreezeScoreboard>();

        for(Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet())
        {
            if(set.getValue() instanceof InteractionFreezeScoreboard)
            {
                boards.put(set.getValue().getId(), (InteractionFreezeScoreboard) set.getValue());
            }
        }

        return boards;
    }

    public synchronized THashMap<Integer, InteractionFreezeScoreboard> getFreezeScoreboards(GameTeamColors teamColor)
    {
        THashMap<Integer, InteractionFreezeScoreboard> boards = new THashMap<Integer, InteractionFreezeScoreboard>();

        for(Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet())
        {
            if(set.getValue() instanceof InteractionFreezeScoreboard)
            {
                if(((InteractionFreezeScoreboard) set.getValue()).teamColor.equals(teamColor))
                    boards.put(set.getValue().getId(), (InteractionFreezeScoreboard) set.getValue());
            }
        }

        return boards;
    }

    public synchronized THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards()
    {
        THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<Integer, InteractionBattleBanzaiScoreboard>();

        for(Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet())
        {
            if(set.getValue() instanceof InteractionBattleBanzaiScoreboard)
            {
                boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard) set.getValue());
            }
        }

        return boards;
    }

    public synchronized THashMap<Integer, InteractionBattleBanzaiScoreboard> getBattleBanzaiScoreboards(GameTeamColors teamColor)
    {
        THashMap<Integer, InteractionBattleBanzaiScoreboard> boards = new THashMap<Integer, InteractionBattleBanzaiScoreboard>();

        for(Map.Entry<Integer, InteractionGameScoreboard> set : this.gameScoreboards.entrySet())
        {
            if(set.getValue() instanceof InteractionBattleBanzaiScoreboard)
            {
                if(((InteractionBattleBanzaiScoreboard) set.getValue()).teamColor.equals(teamColor))
                    boards.put(set.getValue().getId(), (InteractionBattleBanzaiScoreboard) set.getValue());
            }
        }

        return boards;
    }

    /*
        GameGates
     */
    public InteractionGameGate getGameGate(int itemId)
    {
        return this.gameGates.get(itemId);
    }

    public void addGameGate(InteractionGameGate gameGate)
    {
        this.gameGates.put(gameGate.getId(), gameGate);
    }

    public void removeGameGate(InteractionGameGate gameGate)
    {
        this.gameGates.remove(gameGate.getId());
    }

    public synchronized THashMap<Integer, InteractionFreezeGate> getFreezeGates()
    {
        THashMap<Integer, InteractionFreezeGate> gates = new THashMap<Integer, InteractionFreezeGate>();

        for(Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet())
        {
            if(set.getValue() instanceof InteractionFreezeGate)
            {
                gates.put(set.getValue().getId(), (InteractionFreezeGate) set.getValue());
            }
        }

        return gates;
    }

    public synchronized THashMap<Integer, InteractionBattleBanzaiGate> getBattleBanzaiGates()
    {
        THashMap<Integer, InteractionBattleBanzaiGate> gates = new THashMap<Integer, InteractionBattleBanzaiGate>();

        for(Map.Entry<Integer, InteractionGameGate> set : this.gameGates.entrySet())
        {
            if(set.getValue() instanceof InteractionBattleBanzaiGate)
            {
                gates.put(set.getValue().getId(), (InteractionBattleBanzaiGate) set.getValue());
            }
        }

        return gates;
    }

    /*
        GameTimers
    */
    public InteractionGameTimer getGameTimer(int itemId)
    {
        return this.gameTimers.get(itemId);
    }

    public void addGameTimer(InteractionGameTimer gameTimer)
    {
        this.gameTimers.put(gameTimer.getId(), gameTimer);
    }

    public void removeGameTimer(InteractionGameTimer gameTimer)
    {
        this.gameTimers.remove(gameTimer.getId());
    }

    public synchronized THashMap<Integer, InteractionFreezeTimer> getFreezeTimers()
    {
        THashMap<Integer, InteractionFreezeTimer> timers = new THashMap<Integer, InteractionFreezeTimer>();

        for(Map.Entry<Integer, InteractionGameTimer> set : this.gameTimers.entrySet())
        {
            if(set.getValue() instanceof InteractionFreezeTimer)
            {
                timers.put(set.getValue().getId(), (InteractionFreezeTimer) set.getValue());
            }
        }

        return timers;
    }

    public synchronized THashMap<Integer, InteractionBattleBanzaiTimer> getBattleBanzaiTimers()
    {
        THashMap<Integer, InteractionBattleBanzaiTimer> timers = new THashMap<Integer, InteractionBattleBanzaiTimer>();

        for(Map.Entry<Integer, InteractionGameTimer> set : this.gameTimers.entrySet())
        {
            if(set.getValue() instanceof InteractionBattleBanzaiTimer)
            {
                timers.put(set.getValue().getId(), (InteractionBattleBanzaiTimer) set.getValue());
            }
        }

        return timers;
    }

    /*
        Freeze Exit Tile
    */
    public InteractionFreezeExitTile getFreezeExitTile()
    {
        for(InteractionFreezeExitTile t : this.freezeExitTile.values())
        {
            return t;
        }

        return null;
    }

    public void addFreezeExitTile(InteractionFreezeExitTile freezeExitTile)
    {
        this.freezeExitTile.put(freezeExitTile.getId(), freezeExitTile);
    }

    public THashMap<Integer, InteractionFreezeExitTile> getFreezeExitTiles()
    {
        return this.freezeExitTile;
    }

    public void removeFreezeExitTile(InteractionFreezeExitTile freezeExitTile)
    {
        this.freezeExitTile.remove(freezeExitTile.getId());
    }

    public void addUndefined(HabboItem item)
    {
        synchronized (this.undefined)
        {
            this.undefined.put(item.getId(), item);
        }
    }

    public void removeUndefined(HabboItem item)
    {
        synchronized (this.undefined)
        {
            this.undefined.remove(item.getId());
        }
    }

    public THashSet<HabboItem> getItemsOfType(Class<? extends HabboItem> type)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();
        synchronized (this.undefined)
        {
            for(HabboItem item : this.undefined.values())
            {
                if(item.getClass() == type)
                    items.add(item);
            }
        }

        return items;
    }

    public void dispose()
    {
        this.banzaiTeleporters.clear();
        this.nests.clear();
        this.petDrinks.clear();
        this.petFoods.clear();
        this.rollers.clear();

        this.wiredTriggers.clear();
        this.wiredEffects.clear();
        this.wiredConditions.clear();

        this.gameScoreboards.clear();
        this.gameGates.clear();
        this.gameTimers.clear();

        this.freezeExitTile.clear();
        this.undefined.clear();
    }
}
