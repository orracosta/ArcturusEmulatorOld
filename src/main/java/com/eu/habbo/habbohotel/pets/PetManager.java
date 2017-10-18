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
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.THashSet;
import javafx.util.Pair;
import org.apache.commons.math.distribution.ExponentialDistribution;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            this.loadRaces(connection);
            this.loadPetData(connection);
            this.loadPetCommands(connection);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
            Emulator.getLogging().logErrorLine("Pet Manager -> Failed to load!");
            return;
        }

        Emulator.getLogging().logStart("Pet Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void reloadPetData()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM pet_actions ORDER BY pet_type ASC"))
            {
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
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
                }
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

            this.loadPetItems(connection);

            this.loadPetVocals(connection);

            this.loadRaces(connection);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private void loadRaces(Connection connection) throws SQLException
    {
        this.petRaces.clear();

        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_breeds ORDER BY race, color_one, color_two ASC"))
        {
            while(set.next())
            {
                if(this.petRaces.get(set.getInt("race")) == null)
                    this.petRaces.put(set.getInt("race"), new THashSet<PetRace>());

                this.petRaces.get(set.getInt("race")).add(new PetRace(set));
            }
        }
    }

    private void loadPetData(Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_actions ORDER BY pet_type ASC"))
        {
            while(set.next())
            {
                this.petData.put(set.getInt("pet_type"), new PetData(set));
            }
        }

        this.loadPetItems(connection);

        this.loadPetVocals(connection);
    }

    private void loadPetItems(Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_items"))
        {
            while(set.next())
            {
                Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(set.getInt("item_id"));

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
        }
    }

    private void loadPetVocals(Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_vocals"))
        {
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
        }
    }

    private void loadPetCommands(Connection connection) throws SQLException
    {
        THashMap<Integer, PetCommand> commandsList = new THashMap<Integer, PetCommand>();
        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_commands_data"))
        {
            while(set.next())
            {
               commandsList.put(set.getInt("command_id"), new PetCommand(set));
            }
        }

        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_commands ORDER BY pet_id ASC"))
        {
            while(set.next())
            {
                PetData data = this.petData.get(set.getInt("pet_id"));

                if(data != null)
                {
                    data.getPetCommands().add(commandsList.get(set.getInt("command_id")));
                }
            }
        }
    }

    private void loadPetBreeding(Connection connection) throws SQLException
    {
        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_breeding"))
        {
            while (set.next())
            {
                this.breedingPetType.put(set.getInt("pet_id"), set.getInt("offspring_id"));
            }
        }

        try (Statement statement = connection.createStatement(); ResultSet set = statement.executeQuery("SELECT * FROM pet_breeding_races"))
        {
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
        synchronized (this.petData)
        {
            if (this.petData.containsKey(type))
            {
                return this.petData.get(type);
            }
            else
            {
                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
                {
                    Emulator.getLogging().logErrorLine("Missing petdata for type " + type + ". Adding this to the database...");
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO pet_actions (pet_type) VALUES (?)"))
                    {
                        statement.setInt(1, type);
                        statement.execute();
                    }

                    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM pet_actions WHERE pet_type = ? LIMIT 1"))
                    {
                        statement.setInt(1, type);
                        try (ResultSet set = statement.executeQuery())
                        {
                            if (set.next())
                            {
                                PetData petData = new PetData(set);
                                this.petData.put(type, petData);
                                Emulator.getLogging().logErrorLine("Missing petdata for type " + type + " added to the database!");
                                return petData;
                            }
                        }
                    }
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }

        return null;
    }

    public PetData getPetData(String petName)
    {
        synchronized (this.petData)
        {
            for (Map.Entry<Integer, PetData> entry : this.petData.entrySet())
            {
                if (entry.getValue().getName().equalsIgnoreCase(petName))
                {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public Collection<PetData> getPetData()
    {
        return this.petData.values();
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

    public Pet createPet(int type, String name, GameClient client)
    {
        Pet pet = new Pet(type, Emulator.getRandom().nextInt(this.petRaces.get(type).size() + 1), "FFFFFF", name, client.getHabbo().getHabboInfo().getId());
        pet.needsUpdate = true;
        pet.run();
        return pet;
    }

    public MonsterplantPet createMonsterplant(Room room, Habbo habbo, boolean rare, RoomTile t)
    {
        MonsterplantPet pet = new MonsterplantPet(
                habbo.getHabboInfo().getId(),   //Owner ID
                randomBody(rare ? 4 : 0),                              // Type
                randomColor(rare ? 4 : 0),                                  // Color
                Emulator.getRandom().nextInt(12) + 1,                              // Mouth
                Emulator.getRandom().nextInt(11),                                  // Mouthcolor
                Emulator.getRandom().nextInt(12) + 1,                              // Nose
                Emulator.getRandom().nextInt(11),                                  // NoseColor
                Emulator.getRandom().nextInt(12) + 1,                              // Eyes
                Emulator.getRandom().nextInt(11)                                   // EyesColor
        );

        pet.setUserId(habbo.getHabboInfo().getId());
        pet.setRoom(room);
        pet.setRoomUnit(new RoomUnit());
        pet.getRoomUnit().setPathFinderRoom(room);
        pet.needsUpdate = true;
        pet.run();
        return pet;
    }

    public Pet createGnome(String name, Room room, Habbo habbo)
    {
        Pet pet = new GnomePet(26, 0, "FFFFFF", name, habbo.getHabboInfo().getId(),
                "5 " +
                "0 -1 " + randomGnomeSkinColor() + " " +
                "1 10" + (1 + Emulator.getRandom().nextInt(2)) + " " + randomGnomeColor() + " " +
                "2 201 " + randomGnomeColor() + " " +
                "3 30" + (1 + Emulator.getRandom().nextInt(2)) + " " + randomGnomeColor() + " " +
                "4 40" + Emulator.getRandom().nextInt(2) + " " + randomGnomeColor()
                );

        pet.setUserId(habbo.getHabboInfo().getId());
        pet.setRoom(room);
        pet.setRoomUnit(new RoomUnit());
        pet.getRoomUnit().setPathFinderRoom(room);
        pet.needsUpdate = true;
        pet.run();

        return pet;
    }

    public Pet createLeprechaun(String name, Room room, Habbo habbo)
    {
        Pet pet = new GnomePet(27, 0, "FFFFFF", name, habbo.getHabboInfo().getId(),
                "5 " +
                        "0 -1 0 " +
                        "1 102 19 " +
                        "2 201 27 " +
                        "3 302 23 " +
                        "4 401 27"
        );

        pet.setUserId(habbo.getHabboInfo().getId());
        pet.setRoom(room);
        pet.setRoomUnit(new RoomUnit());
        pet.getRoomUnit().setPathFinderRoom(room);
        pet.needsUpdate = true;
        pet.run();

        return pet;
    }

    private int randomGnomeColor()
    {
        int color = 19;

        while (color == 19 || color == 27)
        {
            color = Emulator.getRandom().nextInt(34);
        }

        return color;
    }

    private int randomLeprechaunColor()
    {
        return Emulator.getRandom().nextInt(2) == 1 ? 19 : 27;
    }

    static int[] skins = new int[]{0, 1, 6, 7};
    private int randomGnomeSkinColor()
    {
        return skins[Emulator.getRandom().nextInt(skins.length)];
    }

    public static int randomBody(int minimumRarity)
    {
        //int rarity = MonsterplantPet.indexedBody.get(random(0, MonsterplantPet.bodyRarity.size(), 2.0)).getValue();

        int rarity = -1;
        Integer bodyType = 0;
        while (rarity < minimumRarity)
        {
            bodyType = (Integer) MonsterplantPet.bodyRarity.keySet().toArray()[random(0, MonsterplantPet.bodyRarity.size(), 2.0)];
            rarity = MonsterplantPet.bodyRarity.get(bodyType).getValue();
        }

        return bodyType;
    }

    public static int randomColor(int minimumRarity)
    {
        int rarity = -1;
        Integer colorType = 0;
        while (rarity < minimumRarity)
        {
            colorType = (Integer) MonsterplantPet.colorRarity.keySet().toArray()[random(0, MonsterplantPet.colorRarity.size(), 2.0)];
            rarity = MonsterplantPet.colorRarity.get(colorType).getValue();
        }

        return colorType;
    }

    public static int random(int low, int high, double bias)
    {
        double r = Math.random();    // random between 0 and 1
        r = Math.pow(r, bias);
        return (int) (low + (high - low) * r);
    }

    public static Pet loadPet(ResultSet set) throws SQLException
    {
        if(set.getInt("type") == 15)
            return new HorsePet(set);
        else if(set.getInt("type") == 16)
            return new MonsterplantPet(set);
        else if (set.getInt("type") == 26 || set.getInt("type") == 27)
            return new GnomePet(set);
        else
            return new Pet(set);
    }

    /**
     * Deletes a pet from the database.
     * @param pet The pet to delete.
     * @return true if the pet has been deleted.
     */
    public boolean deletePet(AbstractPet pet)
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users_pets WHERE id = ? LIMIT 1"))
        {
            statement.setInt(1, pet.getId());
            return statement.execute();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return false;
    }
}
