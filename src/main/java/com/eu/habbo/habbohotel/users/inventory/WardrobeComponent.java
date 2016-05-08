package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import gnu.trove.map.hash.THashMap;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WardrobeComponent
{
    private final THashMap<Integer, WardrobeItem> looks;

    public WardrobeComponent(Habbo habbo)
    {
        looks = new THashMap<Integer, WardrobeItem>();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_wardrobe WHERE user_id = ?");
            statement.setInt(1, habbo.getHabboInfo().getId());
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                this.looks.put(set.getInt("slot_id"), new WardrobeItem(set, habbo));
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch(SQLException e)
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

    public void dispose()
    {
        for(WardrobeItem item : looks.values())
        {
            if(item.needsInsert || item.needsUpdate)
            {
                Emulator.getThreading().run(item);
            }
        }

        looks.clear();
    }


    public class WardrobeItem implements Runnable
    {
        private int slotId;
        private HabboGender gender;
        private Habbo habbo;
        private String look;
        private boolean needsInsert;
        private boolean needsUpdate;

        private WardrobeItem(ResultSet set, Habbo habbo) throws SQLException
        {
            this.gender = HabboGender.valueOf(set.getString("gender"));
            this.look = set.getString("look");
            this.slotId = set.getInt("slot_id");
            this.habbo = habbo;
            this.needsInsert = false;
            this.needsUpdate = false;
        }

        private WardrobeItem(HabboGender gender, String look, int slotId, Habbo habbo)
        {
            this.gender = gender;
            this.look = look;
            this.slotId = slotId;
            this.habbo = habbo;
            this.needsInsert = false;
            this.needsUpdate = false;
        }

        public synchronized HabboGender getGender()
        {
            return gender;
        }

        public synchronized void setGender(HabboGender gender) {
            this.gender = gender;
        }

        public Habbo getHabbo() {
            return habbo;
        }

        public void setHabbo(Habbo habbo) {
            this.habbo = habbo;
        }

        public synchronized String getLook() {
            return look;
        }

        public synchronized void setLook(String look) {
            this.look = look;
        }

        public void setNeedsInsert(boolean needsInsert) {
            this.needsInsert = needsInsert;
        }

        public void setNeedsUpdate(boolean needsUpdate)
        {
            this.needsUpdate = needsUpdate;
        }

        public int getSlotId() {
            return slotId;
        }

        public void setSlotId(int slotId)
        {
            this.slotId = slotId;
        }

        @Override
        public void run()
        {
            try
            {
                if(this.needsInsert)
                {
                    this.needsInsert = false;
                    this.needsUpdate = false;
                    PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO users_wardrobe (slot_id, look, user_id, gender) VALUES (?, ?, ?, ?)");
                    statement.setInt(1, this.slotId);
                    statement.setString(2, this.look);
                    statement.setInt(3, this.habbo.getHabboInfo().getId());
                    statement.setString(4, this.gender.name());
                    statement.execute();
                    statement.close();
                    statement.getConnection().close();
                }
                if(this.needsUpdate) {
                    this.needsUpdate = false;
                    PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_wardrobe SET look = ? WHERE slot_id = ? AND user_id = ?");
                    statement.setString(1, this.look);
                    statement.setInt(2, this.slotId);
                    statement.setInt(3, this.habbo.getHabboInfo().getId());
                    statement.execute();
                    statement.close();
                    statement.getConnection().close();
                }
            }
            catch(SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
