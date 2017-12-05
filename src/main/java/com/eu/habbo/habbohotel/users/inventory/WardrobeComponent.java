package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import gnu.trove.TIntCollection;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WardrobeComponent
{
    private final THashMap<Integer, WardrobeItem> looks;
    private final TIntSet clothing;

    public WardrobeComponent(Habbo habbo)
    {
        this.looks = new THashMap<>();
        this.clothing = new TIntHashSet();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
        {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_wardrobe WHERE user_id = ?"))
            {
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        this.looks.put(set.getInt("slot_id"), new WardrobeItem(set, habbo));
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_clothing WHERE user_id = ?"))
            {
                statement.setInt(1, habbo.getHabboInfo().getId());
                try (ResultSet set = statement.executeQuery())
                {
                    while (set.next())
                    {
                        Integer value = set.getInt("clothing_id");

                        if (value != null)
                        {
                            this.clothing.add(value);
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public WardrobeItem createLook(Habbo habbo, int slotId, String look)
    {
        return new WardrobeItem(habbo.getHabboInfo().getGender(), look, slotId, habbo);
    }

    public THashMap<Integer, WardrobeItem> getLooks()
    {
        return this.looks;
    }

    public TIntCollection getClothing()
    {
        return this.clothing;
    }

    public void dispose()
    {
        this.looks.values().stream().filter(item -> item.needsInsert || item.needsUpdate).forEach(item -> {
            Emulator.getThreading().run(item);
        });

        this.looks.clear();
    }

    public class WardrobeItem implements Runnable
    {
        private int slotId;
        private HabboGender gender;
        private Habbo habbo;
        private String look;
        private boolean needsInsert = false;
        private boolean needsUpdate = false;

        private WardrobeItem(ResultSet set, Habbo habbo) throws SQLException
        {
            this.gender = HabboGender.valueOf(set.getString("gender"));
            this.look = set.getString("look");
            this.slotId = set.getInt("slot_id");
            this.habbo = habbo;
        }

        private WardrobeItem(HabboGender gender, String look, int slotId, Habbo habbo)
        {
            this.gender = gender;
            this.look = look;
            this.slotId = slotId;
            this.habbo = habbo;
        }

        public synchronized HabboGender getGender()
        {
            return gender;
        }

        public synchronized void setGender(HabboGender gender)
        {
            this.gender = gender;
        }

        public Habbo getHabbo()
        {
            return habbo;
        }

        public void setHabbo(Habbo habbo)
        {
            this.habbo = habbo;
        }

        public synchronized String getLook()
        {
            return look;
        }

        public synchronized void setLook(String look)
        {
            this.look = look;
        }

        public void setNeedsInsert(boolean needsInsert)
        {
            this.needsInsert = needsInsert;
        }

        public void setNeedsUpdate(boolean needsUpdate)
        {
            this.needsUpdate = needsUpdate;
        }

        public int getSlotId()
        {
            return slotId;
        }

        @Override
        public void run()
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection())
            {
                if(this.needsInsert)
                {
                    this.needsInsert = false;
                    this.needsUpdate = false;
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users_wardrobe (slot_id, look, user_id, gender) VALUES (?, ?, ?, ?)"))
                    {
                        statement.setInt(1, this.slotId);
                        statement.setString(2, this.look);
                        statement.setInt(3, this.habbo.getHabboInfo().getId());
                        statement.setString(4, this.gender.name());
                        statement.execute();
                    }
                }

                if(this.needsUpdate)
                {
                    this.needsUpdate = false;
                    try (PreparedStatement statement = connection.prepareStatement("UPDATE users_wardrobe SET look = ? WHERE slot_id = ? AND user_id = ?"))
                    {
                        statement.setString(1, this.look);
                        statement.setInt(2, this.slotId);
                        statement.setInt(3, this.habbo.getHabboInfo().getId());
                        statement.execute();
                    }
                }
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
