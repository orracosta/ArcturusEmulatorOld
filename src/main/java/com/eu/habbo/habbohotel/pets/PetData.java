package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetToy;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PetData implements Comparable<PetData>
{
    private int type;
    private String name;

    public static final String BLINK = "eyb";
    public static final String SPEAK = "spk";
    public static final String EAT = "eat";
    public static final String PLAYFUL = "pla";

    public String[] actionsHappy;
    public String[] actionsTired;
    public String[] actionsRandom;

    private List<PetCommand> petCommands;

    private List<Item> nestItems;
    private List<Item> foodItems;
    private List<Item> drinkItems;
    private List<Item> toyItems;

    public static final List<Item> generalDrinkItems = new ArrayList<Item>();
    public static final List<Item> generalFoodItems = new ArrayList<Item>();
    public static final List<Item> generalNestItems = new ArrayList<Item>();
    public static final List<Item> generalToyItems = new ArrayList<Item>();

    public THashMap<PetVocalsType, THashSet<PetVocal>> petVocals;
    public static final THashMap<PetVocalsType, THashSet<PetVocal>> generalPetVocals = new THashMap<PetVocalsType, THashSet<PetVocal>>();

    public PetData(ResultSet set) throws SQLException
    {
        this.type = set.getInt("pet_type");
        this.name = set.getString("pet_name");
        this.actionsHappy = set.getString("happy_actions").split(";");
        this.actionsTired = set.getString("tired_actions").split(";");
        this.actionsRandom = set.getString("random_actions").split(";");

        this.petCommands = new ArrayList<PetCommand>();
        this.nestItems = new ArrayList<Item>();
        this.foodItems = new ArrayList<Item>();
        this.drinkItems = new ArrayList<Item>();
        this.toyItems = new ArrayList<Item>();

        this.petVocals = new THashMap<PetVocalsType, THashSet<PetVocal>>();

        for(PetVocalsType type : PetVocalsType.values())
        {
            this.petVocals.put(type, new THashSet<PetVocal>());
        }

        if(PetData.generalPetVocals.isEmpty())
        {
            for(PetVocalsType type : PetVocalsType.values())
            {
                PetData.generalPetVocals.put(type, new THashSet<PetVocal>());
            }
        }
    }

    public void setPetCommands(List<PetCommand> petCommands)
    {
        this.petCommands = petCommands;
    }

    public List<PetCommand> getPetCommands()
    {
        return this.petCommands;
    }

    public int getType()
    {
        return this.type;
    }

    public String getName()
    {
        return this.name;
    }

    public void addNest(Item item)
    {
        if(item != null)
            this.nestItems.add(item);
    }

    public List<Item> getNests()
    {
        return this.nestItems;
    }

    public boolean haveNest(HabboItem item)
    {
        return this.haveNest(item.getBaseItem());
    }

    boolean haveNest(Item item)
    {
        return PetData.generalNestItems.contains(item) || this.nestItems.contains(item);
    }

    public HabboItem randomNest(THashSet<InteractionNest> items)
    {
        List<HabboItem> nestList = new ArrayList<HabboItem>();

        for(InteractionNest nest : items)
        {
            if(this.haveNest(nest))
            {
                nestList.add(nest);
            }
        }

        if(!nestList.isEmpty())
        {
            Collections.shuffle(nestList);

            return nestList.get(0);
        }

        return null;
    }

    public void addFoodItem(Item item)
    {
        this.foodItems.add(item);
    }

    public List<Item> getFoodItems()
    {
        return this.foodItems;
    }

    public boolean haveFoodItem(HabboItem item)
    {
        return this.haveFoodItem(item.getBaseItem());
    }

    boolean haveFoodItem(Item item)
    {
        return this.foodItems.contains(item) || PetData.generalFoodItems.contains(item);
    }

    public HabboItem randomFoodItem(THashSet<InteractionPetFood> items)
    {
        List<HabboItem> foodList = new ArrayList<HabboItem>();

        for(InteractionPetFood food : items)
        {
            if(this.haveFoodItem(food))
            {
                foodList.add(food);
            }
        }

        if(!foodList.isEmpty())
        {
            Collections.shuffle(foodList);
            return foodList.get(0);
        }

        return null;
    }

    public void addDrinkItem(Item item)
    {
        this.drinkItems.add(item);
    }

    public List<Item> getDrinkItems()
    {
        return this.drinkItems;
    }

    public boolean haveDrinkItem(HabboItem item)
    {
        return this.haveDrinkItem(item.getBaseItem());
    }

    boolean haveDrinkItem(Item item)
    {
        return this.drinkItems.contains(item) || PetData.generalDrinkItems.contains(item);
    }

    public HabboItem randomDrinkItem(THashSet<InteractionPetDrink> items)
    {
        List<HabboItem> drinkList = new ArrayList<HabboItem>();

        for(InteractionPetDrink drink : items)
        {
            if(this.haveDrinkItem(drink))
            {
                drinkList.add(drink);
            }
        }

        if(!drinkList.isEmpty())
        {
            Collections.shuffle(drinkList);
            return drinkList.get(0);
        }

        return null;
    }

    public void addToyItem(Item item)
    {
        this.toyItems.add(item);
    }

    public List<Item> getToyItems()
    {
        return this.toyItems;
    }

    public boolean haveToyItem(HabboItem item)
    {
        return this.haveToyItem(item.getBaseItem());
    }

    public boolean haveToyItem(Item item)
    {
        return this.toyItems.contains(item) || PetData.generalToyItems.contains(item);
    }

    public HabboItem randomToyItem(THashSet<InteractionPetToy> items)
    {
        List<HabboItem> toyList = new ArrayList<HabboItem>();

        for(InteractionPetToy toy : items)
        {
            if(this.haveToyItem(toy))
            {
                toyList.add(toy);
            }
        }

        if(!toyList.isEmpty())
        {
            Collections.shuffle(toyList);
            return toyList.get(0);
        }

        return null;
    }

    public PetVocal randomVocal(PetVocalsType type)
    {
        //TODO: Remove this useless copying.
        List<PetVocal> vocals = new ArrayList<PetVocal>();

        if(this.petVocals.get(type) != null)
            vocals.addAll(this.petVocals.get(type));

        if(PetData.generalPetVocals.get(type) != null)
            vocals.addAll(PetData.generalPetVocals.get(type));

        if(vocals.isEmpty() || vocals.size() == 0)
            return null;

        return vocals.get(Emulator.getRandom().nextInt(vocals.size()));
    }

    public void update(ResultSet set) throws SQLException
    {
        this.type = set.getInt("pet_type");
        this.actionsHappy = set.getString("happy_actions").split(";");
        this.actionsTired = set.getString("tired_actions").split(";");
        this.actionsRandom = set.getString("random_actions").split(";");
    }

    @Override
    public int compareTo(PetData o)
    {
        return this.getType() - o.getType();
    }
}
