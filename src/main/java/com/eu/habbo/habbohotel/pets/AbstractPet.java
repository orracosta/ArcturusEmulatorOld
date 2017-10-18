package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTalkComposer;
import com.eu.habbo.plugin.events.pets.PetTalkEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public abstract class AbstractPet implements ISerialize, Runnable
{
    protected int id;
    protected int userId;
    protected Room room;
    protected String name;
    protected PetData petData;
    protected int race;
    protected String color;
    protected int happyness;
    protected int experience;
    protected int energy;
    protected int respect;
    protected int created;
    protected int level;
    public boolean needsUpdate = false;

    RoomUnit roomUnit;
    int chatTimeout;

    void say(String message)
    {
        if(this.roomUnit != null && this.room != null && !message.isEmpty())
        {
            RoomChatMessage chatMessage = new RoomChatMessage(message, this.roomUnit, RoomChatMessageBubbles.NORMAL);
            PetTalkEvent talkEvent = new PetTalkEvent(this, chatMessage);
            if (!Emulator.getPluginManager().fireEvent(talkEvent).isCancelled())
            {
                this.room.petChat(new RoomUserTalkComposer(chatMessage).compose());
            }
        }
    }

    public void say(PetVocal vocal)
    {
        if (true)
            return;

        if(vocal != null)
            this.say(vocal.message);
    }

    public int getId()
    {
        return id;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public Room getRoom()
    {
        return room;
    }

    public void setRoom(Room room)
    {
        this.room = room;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public PetData getPetData()
    {
        return this.petData;
    }

    public void setPetData(PetData petData)
    {
        this.petData = petData;
    }

    public int getRace()
    {
        return race;
    }

    public void setRace(int race)
    {
        this.race = race;
    }

    public String getColor()
    {
        return this.color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public int getHappyness()
    {
        return happyness;
    }

    public void setHappyness(int happyness)
    {
        this.happyness = happyness;
    }

    public int getExperience()
    {
        return experience;
    }

    public void setExperience(int experience)
    {
        this.experience = experience;
    }

    public synchronized void addExperience(int amount)
    {
        this.experience += amount;
    }

    public int getEnergy()
    {
        return energy;
    }

    public int getMaxEnergy()
    {
        return this.level * 100;
    }

    public synchronized void setEnergy(int energy)
    {
        this.energy = energy;
    }

    public synchronized void addEnergy(int amount)
    {
        this.energy += amount;

        if(this.energy > PetManager.maxEnergy(this.level))
            this.energy = PetManager.maxEnergy(this.level);

        if(this.energy < 0)
            this.energy = 0;
    }

    public synchronized void addHappyness(int amount)
    {
        this.happyness += amount;

        if(this.happyness > 100)
            this.happyness = 100;

        if(this.happyness < 0)
            this.happyness = 0;
    }

    public int getRespect()
    {
        return respect;
    }

    public synchronized void addRespect()
    {
        this.respect++;
    }

    public int getCreated()
    {
        return created;
    }

    public void setCreated(int created)
    {
        this.created = created;
    }

    public int daysAlive()
    {
        return (Emulator.getIntUnixTimestamp() - this.created) / 86400;
    }

    public String bornDate()
    {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(new Date(this.created));

        return cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR);
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public RoomUnit getRoomUnit()
    {
        return roomUnit;
    }

    public void setRoomUnit(RoomUnit roomUnit)
    {
        this.roomUnit = roomUnit;
    }
}
