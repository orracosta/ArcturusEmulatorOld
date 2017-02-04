package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionNest;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetDrink;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetFood;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetToy;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PetManager
{
    public static final int[] experiences = new int[]{ 100, 200, 400, 600, 900, 1300, 1800, 2400, 3200, 4300, 5700, 7600, 10100, 13300, 17500, 23000, 30200, 39600, 51900};

    private final THashMap<Integer, THashSet<PetRace>> petRaces;
    private final THashMap<Integer, PetData> petData;
    private final TIntIntMap breedingPetType;
    private final THashMap<Integer, TIntObjectHashMap<ArrayList<PetBreedingReward>>> breedingReward;

    public PetManager()
    {
        long millis = System.currentTimeMillis();

        this.petRaces = new THashMap<Integer, THashSet<PetRace>>();
        this.petData = new THashMap<Integer, PetData>();
        this.breedingPetType = new TIntIntHashMap();
        this.breedingReward = new THashMap<Integer, TIntObjectHashMap<ArrayList<PetBreedingReward>>>();

        this.loadRaces();
        this.loadPetData();
        this.loadPetCommands();

        Emulator.getLogging().logStart("Pet Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reloadPetData()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_actions ORDER BY pet_type ASC");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                PetData petData = this.petData.get(set.getInt("pet_type"));

                if (petData != null)
                {
                    petData.update(set);
                }
                else
                {
                    this.petData.put(set.getInt("pet_type"), new PetData(set));
                }
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        PetData.generalNestItems.clear();
        PetData.generalFoodItems.clear();
        PetData.generalDrinkItems.clear();

        for(PetData data : this.petData.values())
        {
            data.getDrinkItems().clear();
            data.getFoodItems().clear();
            data.getToyItems().clear();
            data.getNests().clear();
            data.petVocals.clear();
        }

        this.loadPetItems();

        this.loadPetVocals();

        this.loadRaces();
    }

    void loadRaces()
    {
        this.petRaces.clear();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_breeds ORDER BY race, color_one, color_two ASC");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                if(this.petRaces.get(set.getInt("race")) == null)
                    this.petRaces.put(set.getInt("race"), new THashSet<PetRace>());

                this.petRaces.get(set.getInt("race")).add(new PetRace(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    void loadPetData()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_actions ORDER BY pet_type ASC");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                this.petData.put(set.getInt("pet_type"), new PetData(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.loadPetItems();

        this.loadPetVocals();
    }

    void loadPetItems()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_items");
            ResultSet set = statement.executeQuery();

            Item baseItem;

            while(set.next())
            {
                baseItem = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("item_id"));

                if(baseItem != null)
                {
                    if(set.getInt("pet_id") == 0)
                    {
                        if(baseItem.getInteractionType().getType() == InteractionNest.class) PetData.generalNestItems.add(baseItem);
                        else if(baseItem.getInteractionType().getType() == InteractionPetFood.class) PetData.generalFoodItems.add(baseItem);
                        else if(baseItem.getInteractionType().getType() == InteractionPetDrink.class) PetData.generalDrinkItems.add(baseItem);
                        else if(baseItem.getInteractionType().getType() == InteractionPetToy.class) PetData.generalToyItems.add(baseItem);
                    }
                    else
                    {
                        PetData data = this.getPetData(set.getInt("pet_id"));

                        if(data != null)
                        {
                            if(baseItem.getInteractionType().getType() == InteractionNest.class) data.addNest(baseItem);
                            else if(baseItem.getInteractionType().getType() == InteractionPetFood.class) data.addFoodItem(baseItem);
                            else if(baseItem.getInteractionType().getType() == InteractionPetDrink.class) data.addDrinkItem(baseItem);
                            else if(baseItem.getInteractionType().getType() == InteractionPetToy.class) data.addToyItem(baseItem);
                        }
                    }
                }
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    void loadPetVocals()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_vocals");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                if(set.getInt("pet_id") > 0)
                {
                    this.petData.get(set.getInt("pet_id")).petVocals.get(PetVocalsType.valueOf(set.getString("type").toUpperCase())).add(new PetVocal(set.getString("message")));
                }
                else
                {
                    if(!PetData.generalPetVocals.containsKey(PetVocalsType.valueOf(set.getString("type").toUpperCase())))
                        PetData.generalPetVocals.put(PetVocalsType.valueOf(set.getString("type").toUpperCase()), new THashSet<PetVocal>());

                    PetData.generalPetVocals.get(PetVocalsType.valueOf(set.getString("type").toUpperCase())).add(new PetVocal(set.getString("message")));
                }
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    void loadPetCommands()
    {
        THashMap<Integer, PetCommand> commandsList = new THashMap<Integer, PetCommand>();
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_commands_data");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
               commandsList.put(set.getInt("command_id"), new PetCommand(set));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_commands ORDER BY pet_id ASC");
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                PetData data = this.petData.get(set.getInt("pet_id"));

                if(data != null)
                {
                    data.getPetCommands().add(commandsList.get(set.getInt("command_id")));
                }
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    void loadPetBreeding()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_breeding");
            ResultSet set = statement.executeQuery();

            while (set.next())
            {
                this.breedingPetType.put(set.getInt("pet_id"), set.getInt("offspring_id"));
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM pet_breeding_races");
            ResultSet set = statement.executeQuery();

            while (set.next())
            {
                PetBreedingReward reward = new PetBreedingReward(set);
                if (!this.breedingReward.containsKey(reward.petType))
                {
                    this.breedingReward.put(reward.petType, new TIntObjectHashMap<ArrayList<PetBreedingReward>>());
                }

                if (!this.breedingReward.get(reward.petType).containsKey(reward.rarityLevel))
                {
                    this.breedingReward.get(reward.petType).put(reward.rarityLevel, new ArrayList<PetBreedingReward>());
                }

                this.breedingReward.get(reward.petType).get(reward.rarityLevel).add(reward);
            }

            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
    public THashSet<PetRace> getBreeds(String petName)
    {
        if(!petName.startsWith("a0 pet"))
        {
            Emulator.getLogging().logErrorLine("Pet " + petName + " not found. Make sure it matches the pattern \"a0 pet<pet_id>\"!");
            return null;
        }

        try
        {
            int petId = Integer.valueOf(petName.split("t")[1]);
            return this.petRaces.get(petId);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static int getLevel(int experience)
    {
        int index = 0;

        for(int i = 0; i < experiences.length; i++)
        {
            if(experiences[i] > experience)
            {
                index = i;
                break;
            }
        }

        return index + 1;
    }

    public static int maxEnergy(int level)
    {
        //TODO: Add energy calculation.
        return 100 * level;
    }

    public PetData getPetData(int type)
    {
        return this.petData.get(type);
    }

    public Pet createPet(Item item, String name, String race, String color, GameClient client)
    {
        int type = Integer.valueOf(item.getName().toLowerCase().replace("a0 pet", ""));

        Pet pet;
        if(type == 15)
            pet = new HorsePet(type, Integer.valueOf(race), color, name, client.getHabbo().getHabboInfo().getId());
        else if(type == 16)
            pet = createMonsterplant(null, client.getHabbo(), false, null);
        else
            pet = new Pet(type,
                Integer.valueOf(race),
                color,
                name,
                client.getHabbo().getHabboInfo().getId()
        );

        pet.needsUpdate = true;
        pet.run();
        return pet;
    }

    public MonsterplantPet createMonsterplant(Room room, Habbo habbo, boolean rare, RoomTile t)
    {
        return new MonsterplantPet(habbo.getHabboInfo().getId(), 0, 0, 0, 0, 0);
    }
}
