
package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionPetBreedingNest;
import com.eu.habbo.habbohotel.items.interactions.InteractionWater;
import com.eu.habbo.habbohotel.rooms.RoomUserRotation;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetExperienceComposer;
import com.eu.habbo.messages.outgoing.rooms.pets.RoomPetRespectComposer;
import com.eu.habbo.threading.runnables.PetClearPosture;
import com.eu.habbo.threading.runnables.PetFollowHabbo;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.sql.*;

public class Pet extends AbstractPet
{
    public int levelThirst;
    public int levelHunger;

    public boolean packetUpdate = false;

    private int tickTimeout = Emulator.getIntUnixTimestamp();
    private int happynessDelay = Emulator.getIntUnixTimestamp();
    private int gestureTickTimeout = Emulator.getIntUnixTimestamp();
    private int randomActionTickTimeout = Emulator.getIntUnixTimestamp();
    private int postureTimeout = Emulator.getIntUnixTimestamp();
    private int idleCommandTicks = 0;
    private int freeCommandTicks = -1;

    private PetTasks task = PetTasks.FREE;

    private boolean muted = false;

    public Pet(ResultSet set) throws SQLException
    {
        super();
        this.id = set.getInt("id");
        this.userId = set.getInt("user_id");
        this.room = null;
        this.name = set.getString("name");
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(set.getInt("type"));
        this.race = set.getInt("race");
        this.experience = set.getInt("experience");
        this.happyness = set.getInt("happyness");
        this.energy = set.getInt("energy");
        this.respect = set.getInt("respect");
        this.created = set.getInt("created");
        this.color = set.getString("color");
        this.levelThirst = set.getInt("thirst");
        this.levelHunger = set.getInt("hunger");
        this.level = PetManager.getLevel(this.experience);
    }

    public Pet(int type, int race, String color, String name, int userId)
    {
        this.id = 0;
        this.userId = userId;
        this.room = null;
        this.name = name;
        this.petData = Emulator.getGameEnvironment().getPetManager().getPetData(type);

        if(this.petData == null)
        {
            Emulator.getLogging().logErrorLine(new Exception("Non existing pet data for type: " + type));
        }

        this.race = race;
        this.color = color;
        this.experience = 0;
        this.happyness = 100;
        this.energy = 100;
        this.respect = 0;
        this.levelThirst = 0;
        this.levelHunger = 0;
        this.created = Emulator.getIntUnixTimestamp();
        this.level = 1;
    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
            {
                if (this.id > 0)
                {
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE users_pets SET room_id = ?, experience = ?, energy = ?, respect = ?, x = ?, y = ?, z = ?, rot = ?, hunger = ?, thirst = ?, happyness = ? WHERE id = ?"))
                    {
                        statement.setInt(1, (this.room == null ? 0 : this.room.getId()));
                        statement.setInt(2, this.experience);
                        statement.setInt(3, this.energy);
                        statement.setInt(4, this.respect);
                        statement.setInt(5, this.getRoomUnit() != null ? this.getRoomUnit().getX() : 0);
                        statement.setInt(6, this.getRoomUnit() != null ? this.getRoomUnit().getY() : 0);
                        statement.setDouble(7, this.getRoomUnit() != null ? this.getRoomUnit().getZ() : 0.0);
                        statement.setInt(8, this.getRoomUnit() != null ? this.getRoomUnit().getBodyRotation().getValue() : 0);
                        statement.setInt(9, this.levelHunger);
                        statement.setInt(10, this.levelThirst);
                        statement.setInt(11, this.happyness);
                        statement.setInt(12, this.id);
                        statement.execute();
                    }
                }
                else if (this.id == 0)
                {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_pets (user_id, room_id, name, race, type, color, experience, energy, respect, created) VALUES (?, 0, ?, ?, ?, ?, 0, 0, 0, ?)", Statement.RETURN_GENERATED_KEYS))
                    {
                        statement.setInt(1, this.userId);
                        statement.setString(2, this.name);
                        statement.setInt(3, this.race);
                        statement.setInt(4, 0);

                        if (this.petData != null)
                        {
                            statement.setInt(4, this.petData.getType());
                        }

                        statement.setString(5, this.color);
                        statement.setInt(6, this.created);
                        statement.execute();

                        try (ResultSet set = statement.getGeneratedKeys())
                        {
                            if (set.next())
                            {
                                this.id = set.getInt(1);
                            }
                        }
                    }
                }
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.needsUpdate = false;
        }
    }

    public void cycle()
    {
        this.idleCommandTicks++;

        int time = Emulator.getIntUnixTimestamp();
        if(this.roomUnit != null && this.task != PetTasks.RIDE)
        {
            if(time - this.gestureTickTimeout > 5)
            {
                this.roomUnit.getStatus().remove("gst");
                this.packetUpdate = true;
            }

            if(time - this.postureTimeout > 1 && this.task == null)
            {
                this.clearPosture();
                this.postureTimeout = time;
            }

            if (this.freeCommandTicks > 0)
            {
                this.freeCommandTicks--;

                if (this.freeCommandTicks == 0)
                {
                    freeCommand();
                }
            }

            if(!this.roomUnit.isWalking())
            {
                this.roomUnit.getStatus().remove("mv");

                if (this.roomUnit.getWalkTimeOut() < time && this.canWalk())
                {
                    this.roomUnit.setGoalLocation(this.room.getRandomWalkableTile());
                }

                if (this.task == PetTasks.NEST || this.task == PetTasks.DOWN)
                {
                    if (this.levelHunger > 0)
                        this.levelHunger--;

                    if (this.levelThirst > 0)
                        this.levelThirst--;

                    this.addEnergy(5);

                    this.addHappyness(1);

                    if (this.energy == PetManager.maxEnergy(this.level))
                    {
                        this.roomUnit.getStatus().remove("lay");
                        this.roomUnit.setCanWalk(true);
                        this.roomUnit.setGoalLocation(this.room.getRandomWalkableTile());
                        this.task = null;
                        this.roomUnit.getStatus().put("gst", PetGestures.ENERGY.getKey());
                        this.gestureTickTimeout = time;
                    }
                }
                else if(this.tickTimeout >= 5)
                {
                    if(this.levelHunger < 100)
                        this.levelHunger++;

                    if(this.levelThirst < 100)
                        this.levelThirst++;

                    if(this.energy < PetManager.maxEnergy(this.level))
                        this.energy++;

                    this.tickTimeout = time;
                }
            }
            else
            {
                int timeout = Emulator.getRandom().nextInt(10) * 2;
                this.roomUnit.setWalkTimeOut(timeout < 20 ? 20 + time : timeout + time);

                if(this.energy >= 2)
                    this.addEnergy(-1);

                if(this.levelHunger < 100)
                    this.levelHunger++;

                if(this.levelThirst < 100)
                    this.levelThirst++;

                if(this.happyness > 0 && time - this.happynessDelay >= 30)
                {
                    this.happyness--;
                    this.happynessDelay = time;
                }
            }

            if(time - this.gestureTickTimeout > 15)
            {
                updateGesture(time);
            }
            else if(time - this.randomActionTickTimeout > 30)
            {
                this.randomAction();
                this.randomActionTickTimeout = time + (10 * Emulator.getRandom().nextInt(60));
            }

            if(!this.muted)
            {
                if (super.chatTimeout <= time)
                {
                    if (this.energy <= 30)
                    {
                        super.say(this.petData.randomVocal(PetVocalsType.TIRED));
                        if(this.energy <= 10)
                            this.findNest();
                    } else if (this.happyness > 85)
                    {
                        super.say(this.petData.randomVocal(PetVocalsType.GENERIC_HAPPY));
                    } else if (this.happyness < 15)
                    {
                        super.say(this.petData.randomVocal(PetVocalsType.GENERIC_SAD));
                    } else if (this.levelHunger > 50)
                    {
                        super.say(this.petData.randomVocal(PetVocalsType.HUNGRY));
                        this.eat();
                    } else if (this.levelThirst > 50)
                    {
                        super.say(this.petData.randomVocal(PetVocalsType.THIRSTY));
                        this.drink();
                    }

                    int timeOut = Emulator.getRandom().nextInt(30);
                    super.chatTimeout = time + (timeOut < 3 ? 30 : timeOut);
                }
            }
        }
    }

    public void handleCommand(PetCommand command, Habbo habbo)
    {
        this.idleCommandTicks = 0;
        if(Emulator.getRandom().nextInt((this.level - command.level <= 0 ? 2 : this.level - command.level) + 2) == 0)
        {
            super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
            return;
        }

        this.addEnergy(-command.energyCost);
        this.addHappyness(-command.happynessCost);

        switch(command.id)
        {
            case 0:
            {
                freeCommand();
            }
            break;

            case 1:
            {
                this.getRoomUnit().getStatus().put("sit", room.getStackHeight(this.getRoomUnit().getX(), this.getRoomUnit().getY(), false) - 0.50 + "");
                this.getRoomUnit().setGoalLocation(this.room.getLayout().getTile(this.getRoomUnit().getX(), this.getRoomUnit().getY()));
                this.task = PetTasks.SIT;
                this.packetUpdate = true;

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 2:
            {
                if(this.task == PetTasks.DOWN)
                    return;

                this.getRoomUnit().setGoalLocation(this.room.getLayout().getTile(this.getRoomUnit().getX(), this.getRoomUnit().getY()));
                this.getRoomUnit().getStatus().remove("mv");
                this.getRoomUnit().getStatus().remove("sit");
                this.getRoomUnit().getStatus().put("lay", room.getStackHeight(this.getRoomUnit().getX(), this.getRoomUnit().getY(), false) + "");
                this.task = PetTasks.DOWN;
                this.packetUpdate = true;

                if(this.happyness > 50)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 3:
            {
                this.getRoomUnit().setGoalLocation(this.room.getLayout().getTileInFront(habbo.getRoomUnit().getCurrentLocation(), habbo.getRoomUnit().getBodyRotation().getValue()));
                this.task = PetTasks.HERE;
                this.roomUnit.setCanWalk(true);

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 4:
            {
                clearPosture();
                if(this.task == PetTasks.BEG)
                    return;

                this.getRoomUnit().getStatus().put("beg", "0");
                this.task = PetTasks.BEG;
                this.packetUpdate = true;

                if(this.happyness > 90)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 5:
            {
                clearPosture();
                if(this.task == PetTasks.PLAY_DEAD)
                    return;

                this.getRoomUnit().getStatus().remove("mv");
                this.getRoomUnit().getStatus().remove("lay");
                this.getRoomUnit().getStatus().remove("ded");
                this.getRoomUnit().getStatus().put("ded", room.getStackHeight(this.roomUnit.getX(), this.roomUnit.getY(), false) + "");
                this.task = PetTasks.PLAY_DEAD;
                this.packetUpdate = true;

                if(this.happyness > 50)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 6:
            {
                clearPosture();
                if(this.task == PetTasks.STAY)
                    return;

                this.getRoomUnit().setCanWalk(false);
                this.getRoomUnit().getStatus().remove("mv");
                this.getRoomUnit().getStatus().remove("lay");
                this.getRoomUnit().getStatus().remove("ded");
                this.task = PetTasks.STAY;

                super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 7:
            {
                clearPosture();
                if(this.task == PetTasks.FOLLOW)
                    return;

                Emulator.getThreading().run(new PetFollowHabbo(this, habbo, 0));
                this.getRoomUnit().getStatus().remove("mv");
                this.getRoomUnit().getStatus().remove("lay");
                this.getRoomUnit().getStatus().remove("ded");
                this.task = PetTasks.FOLLOW;

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));

            }
            break;

            case 8:
            {
                clearPosture();
                if(this.task == PetTasks.STAND)
                    return;

                this.task = PetTasks.STAND;
                this.roomUnit.getStatus().remove("lay");
                this.getRoomUnit().getStatus().remove("mv");
                this.getRoomUnit().getStatus().remove("lay");
                this.getRoomUnit().getStatus().remove("ded");

                if(this.happyness > 30)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 9:
            {
                clearPosture();
                if(this.task == PetTasks.JUMP)
                    return;

                this.roomUnit.getStatus().put("jmp", "");
                this.task = PetTasks.JUMP;
                this.packetUpdate = true;
                Emulator.getThreading().run(new PetClearPosture(this, "jmp", null, false), 2000);

                if(this.happyness > 60)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 10:
            {
                this.muted = false;
                this.roomUnit.getStatus().put("spk", "0");
                Emulator.getThreading().run(new PetClearPosture(this, "spk", null, false), 2000);

                if(this.happyness > 70)
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_HAPPY));
                else if(this.happyness < 30)
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_SAD));
                else if(this.levelHunger > 65)
                    super.say(this.petData.randomVocal(PetVocalsType.HUNGRY));
                else if(this.levelThirst > 65)
                    super.say(this.petData.randomVocal(PetVocalsType.THIRSTY));
                else if(this.energy < 25)
                    super.say(this.petData.randomVocal(PetVocalsType.TIRED));
                else if(this.task == PetTasks.NEST || this.task == PetTasks.DOWN)
                    super.say(this.petData.randomVocal(PetVocalsType.SLEEPING));
            }
            break;

            case 11:
            {
                //Play
                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                {
                    super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
                }
            }
            break;

            case 12:
            {
                //Silent
                this.muted = true;
                this.roomUnit.getStatus().remove("spk");
                super.say(this.petData.randomVocal(PetVocalsType.MUTED));
            }
            break;

            case 13:
            {
                //Nest
                if(this.energy < 65)
                {
                    this.findNest();

                    if (this.energy < 30)
                        super.say(this.petData.randomVocal(PetVocalsType.TIRED));
                }
                else
                {
                    super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
                }
            }
            break;

            case 14:
            {
                //Drink
                if(this.levelThirst > 40)
                {
                    this.drink();

                    if(this.levelThirst > 65)
                        super.say(this.petData.randomVocal(PetVocalsType.THIRSTY));
                }
                else
                {
                    super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
                }

            }
            break;

            case 15:
            {
                //Follow left.
                clearPosture();
                if(this.task == PetTasks.FOLLOW)
                    return;

                Emulator.getThreading().run(new PetFollowHabbo(this, habbo, - 2));
                this.task = PetTasks.FOLLOW;

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 16:
            {
                //Follow right.
                clearPosture();
                if(this.task == PetTasks.FOLLOW)
                    return;

                Emulator.getThreading().run(new PetFollowHabbo(this, habbo, + 2));
                this.task = PetTasks.FOLLOW;

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 17:
            {
                //Play football

                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 18:
            {
                this.getRoomUnit().setGoalLocation(this.room.getLayout().getTileInFront(habbo.getRoomUnit().getCurrentLocation(), habbo.getRoomUnit().getBodyRotation().getValue()));
                this.roomUnit.setCanWalk(true);
            }
            break;

            case 19:
            {
                //Bounce
            }
            break;

            case 20:
            {
                //Flat
                //Same as down?
            }
            break;

            case 21:
            {
                //Dance
            }
            break;

            case 22:
            {
                //Spin -> Schildpad?
            }
            break;

            case 23:
            {
                //Switch TV -> Monkey?
            }
            break;

            case 24:
            {
                //Move forward:
                this.getRoomUnit().setGoalLocation(this.room.getLayout().getTileInFront(this.roomUnit.getCurrentLocation(), this.roomUnit.getBodyRotation().getValue()));
                this.roomUnit.setCanWalk(true);

                super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 25:
            {
                this.getRoomUnit().setBodyRotation(RoomUserRotation.values()[(this.roomUnit.getBodyRotation().getValue() - 1  < 0 ? 7 : this.roomUnit.getBodyRotation().getValue() - 1)]);
                super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 26:
            {
                this.getRoomUnit().setBodyRotation(RoomUserRotation.values()[(this.roomUnit.getBodyRotation().getValue() + 1  > 7 ? 0 : this.roomUnit.getBodyRotation().getValue() + 1)]);
                super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
            }
            break;

            case 27:
            {
                //Relax
                if(this.happyness > 75)
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_HAPPY));
                else if(this.happyness < 30)
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_SAD));
                else
                    super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));

                this.getRoomUnit().getStatus().put("rlx", "0");
            }
            break;

            case 28:
            {
                //Croak
                this.getRoomUnit().getStatus().put("crk", "0");

                Emulator.getThreading().run(new PetClearPosture(this, "crk", null, false), 2000);

                if(this.happyness > 80)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
            }
            break;

            case 29:
            {
                //Dip

                if(this.roomUnit.isWalking())
                    return;

                THashSet<HabboItem> waterItems = room.getRoomSpecialTypes().getItemsOfType(InteractionWater.class);

                if (waterItems.isEmpty())
                    return;

                HabboItem waterPatch = (HabboItem) waterItems.toArray()[Emulator.getRandom().nextInt(waterItems.size())];

                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(waterPatch.getX(), waterPatch.getY()));
            }
            break;

            case 30:
            {
                //WAV
                if(this.happyness > 65)
                {
                    this.getRoomUnit().getStatus().put("wav", "0");

                    Emulator.getThreading().run(new PetClearPosture(this, "wav", null, false), 2000);
                }
            }
            break;

            case 31:
            {
                //Mambo!
            }
            break;

            case 32:
            {
                //High jump
            }
            break;

            case 33:
            {
                //Chicken dance
            }
            break;

            case 34:
            {
                //Triple jump
            }
            break;

            case 35:
            {
                //Spread wings
                if(this.task == PetTasks.SPREAD_WINGS)
                    return;

                this.roomUnit.getStatus().put("wng", "0");
                Emulator.getThreading().run(new PetClearPosture(this, "wng", null, false), 2000);

                if(this.happyness > 50)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
            }
            break;

            case 36:
            {
                //Breathe fire
                this.roomUnit.getStatus().put("flm", "0");
                Emulator.getThreading().run(new PetClearPosture(this, "fla", null, false), 1000);

                if(this.happyness > 50)
                    super.say(this.petData.randomVocal(PetVocalsType.PLAYFUL));
            }
            break;

            case 37:
            {
                //Hang
            }
            break;

            case 38:
            {
                if(this.happyness < 30)
                {
                    super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
                    return;
                }

                this.roomUnit.getStatus().put("eat", "0");
                Emulator.getThreading().run(new PetClearPosture(this, "eat", null, false), 500);
            }
            break;

            case 39:
            {
                //Not found O.o ?
            }
            break;

            case 40:
            {
                //Swing
            }
            break;

            case 41:
            {
                //Roll
            }
            break;

            case 42:
            {
                //Ring of fire
            }
            break;

            case 43:
            {
                //Eat
                if(this.levelHunger > 40)
                {
                    super.say(this.petData.randomVocal(PetVocalsType.HUNGRY));

                    this.roomUnit.getStatus().put("eat", "0");
                    Emulator.getThreading().run(new PetClearPosture(this, "eat", null, false), 500);
                    this.eat();
                }
                else
                {
                    super.say(this.petData.randomVocal(PetVocalsType.DISOBEY));
                    return;
                }
            }
            break;

            case 44:
            {
                //Wag tail
            }
            break;

            case 45:
            {
                //Count
            }
            break;

            case 46:
            {
                InteractionPetBreedingNest nest = null;
                for (HabboItem item : room.getRoomSpecialTypes().getItemsOfType(InteractionPetBreedingNest.class))
                {
                    if (item.getBaseItem().getName().contains(this.petData.getName()))
                    {
                        if (!((InteractionPetBreedingNest)item).boxFull())
                        {
                            nest = (InteractionPetBreedingNest) item;
                            break;
                        }
                    }
                }

                if (nest != null)
                {
                    this.roomUnit.setGoalLocation(this.room.getLayout().getTile(nest.getX(), nest.getY()));
                }
            }
            break;
        }

        this.addExperience(command.xp);
    }

    public boolean canWalk()
    {
        if(this.task == null)
            return true;

        switch(this.task)
        {
            case DOWN:
            case FLAT:
            case HERE:
            case SIT:
            case BEG:
            case PLAY:
            case PLAY_FOOTBALL:
            case PLAY_DEAD:
            case FOLLOW:
            case JUMP:
            case STAND:
            case NEST:
            case RIDE:

                return false;
        }

        return true;
    }

    void clearPosture()
    {
        THashMap<String, String> keys = new THashMap<String, String>();

        if(this.getRoomUnit().getStatus().containsKey("mv"))
            keys.put("mv", this.getRoomUnit().getStatus().get("mv"));

        if(this.getRoomUnit().getStatus().containsKey("sit"))
            keys.put("sit", this.getRoomUnit().getStatus().get("sit"));

        if(this.getRoomUnit().getStatus().containsKey("lay"))
            keys.put("lay", this.getRoomUnit().getStatus().get("lay"));

        if(this.getRoomUnit().getStatus().containsKey("gst"))
            keys.put("gst", this.getRoomUnit().getStatus().get("gst"));

        if(this.task == null)
        {
            this.getRoomUnit().getStatus().clear();
            this.getRoomUnit().getStatus().putAll(keys);
        }

        this.packetUpdate = true;
    }

    public void updateGesture(int time)
    {
        this.gestureTickTimeout = time;
        if (this.getEnergy() < 30)
        {
            this.roomUnit.getStatus().put("gst", PetGestures.TIRED.getKey());
            this.findNest();
        }
        else if(this.getHappyness() == 100)
        {
            this.roomUnit.getStatus().put("gst", PetGestures.LOVE.getKey());
        } else if (this.happyness >= 90)
        {
            this.randomHappyAction();
            this.roomUnit.getStatus().put("gst", PetGestures.HAPPY.getKey());
        } else if (this.happyness <= 5)
        {
            this.randomSadAction();
            this.roomUnit.getStatus().put("gst", PetGestures.SAD.getKey());
        } else if (this.levelHunger > 80)
        {
            this.roomUnit.getStatus().put("gst", PetGestures.HUNGRY.getKey());
            this.eat();
        } else if (this.levelThirst > 80)
        {
            this.roomUnit.getStatus().put("gst", PetGestures.THIRSTY.getKey());
            this.drink();
        }
        else if(this.idleCommandTicks > 240)
        {
            this.idleCommandTicks = 0;

            this.roomUnit.getStatus().put("gst", PetGestures.QUESTION.getKey());
        }
    }
    @Override
    public void serialize(ServerMessage message)
    {
        message.appendInt(this.getId());
        message.appendString(this.getName());
        message.appendInt(this.petData.getType());
        message.appendInt(this.race);
        message.appendString(this.color);
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(0);
    }

    public void findNest()
    {
        HabboItem item = this.petData.randomNest(this.room.getRoomSpecialTypes().getNests());
        this.roomUnit.setCanWalk(true);
        if(item != null)
        {
            this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
        }
        else
        {
            this.roomUnit.getStatus().put("lay", this.room.getStackHeight(this.roomUnit.getX(), this.roomUnit.getY(), false) + "");
            super.say(this.petData.randomVocal(PetVocalsType.SLEEPING));
            this.task = PetTasks.DOWN;
        }
    }

    public void drink()
    {
        HabboItem item = this.petData.randomDrinkItem(this.room.getRoomSpecialTypes().getPetDrinks());
        if(item != null)
        {
            this.roomUnit.setCanWalk(true);
            this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
        }
    }

    public void eat()
    {
        HabboItem item = this.petData.randomFoodItem(this.room.getRoomSpecialTypes().getPetFoods());
        {
            if(item != null)
            {
                this.roomUnit.setCanWalk(true);
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
            }
        }
    }

    public void findToy()
    {
        HabboItem item = this.petData.randomToyItem(this.room.getRoomSpecialTypes().getPetToys());
        {
            if(item != null)
            {
                this.roomUnit.setCanWalk(true);
                this.roomUnit.setGoalLocation(this.room.getLayout().getTile(item.getX(), item.getY()));
            }
        }
    }

    public void randomHappyAction()
    {
        this.roomUnit.getStatus().put(this.petData.actionsHappy[Emulator.getRandom().nextInt(this.petData.actionsHappy.length)], "");
    }

    public void randomSadAction()
    {
        this.roomUnit.getStatus().put(this.petData.actionsTired[Emulator.getRandom().nextInt(this.petData.actionsTired.length)], "");
    }

    public void randomAction()
    {
        this.roomUnit.getStatus().put(this.petData.actionsRandom[Emulator.getRandom().nextInt(this.petData.actionsRandom.length)], "");
    }

    @Override
    public synchronized void addExperience(int amount)
    {
        this.experience += amount;

        if(this.room != null)
        {
            this.room.sendComposer(new RoomPetExperienceComposer(this, amount).compose());

            if(this.experience >= PetManager.experiences[this.level - 1])
            {
                this.levelUp();
            }
        }
    }

    protected void levelUp()
    {
        this.level++;
        super.say(this.petData.randomVocal(PetVocalsType.LEVEL_UP));
        this.addHappyness(100);
        this.getRoomUnit().getStatus().put("gst", "exp");
        this.gestureTickTimeout = Emulator.getIntUnixTimestamp();
        AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.userId), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetLevelUp"));
    }

    public synchronized void addThirst(int amount)
    {
        this.levelThirst += amount;

        if(this.levelThirst > 100)
            this.levelThirst = 100;

        if(this.levelThirst < 0)
            this.levelThirst = 0;
    }

    public synchronized void addHunger(int amount)
    {
        this.levelHunger += amount;

        if(this.levelHunger > 100)
            this.levelHunger = 100;

        if(this.levelHunger < 0)
            this.levelHunger = 0;
    }

    public PetTasks getTask()
    {
        return this.task;
    }

    public void setTask(PetTasks newTask)
    {
        this.task = newTask;
    }

    public void freeCommand()
    {
        this.task = null;
        this.roomUnit.getStatus().clear();
        this.roomUnit.setCanWalk(true);
        super.say(this.petData.randomVocal(PetVocalsType.GENERIC_NEUTRAL));
    }

    public void scratched(Habbo habbo)
    {
        this.addExperience(10);
        this.addRespect();

        if (habbo != null)
        {
            habbo.getHabboStats().petRespectPointsToGive--;
            habbo.getHabboInfo().getCurrentRoom().sendComposer(new RoomPetRespectComposer(this).compose());

            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectGiver"));
        }

        AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("PetRespectReceiver"));
    }
}