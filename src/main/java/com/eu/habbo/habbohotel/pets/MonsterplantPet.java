package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 21-1-2015 12:57.
 */
public class MonsterplantPet extends Pet
{
    private int type;
    private int hue;
    private int nose;
    private int eyes;
    private int mouth;

    public String look;

    public MonsterplantPet(ResultSet set) throws SQLException
    {
        super(set);
        this.type = set.getInt("mp_type");
        this.hue = set.getInt("mp_color");
        this.nose = set.getInt("mp_nose");
        this.eyes = set.getInt("mp_eyes");
        this.mouth = set.getInt("mp_mouth");
    }

    public MonsterplantPet(int userId, int type, int hue, int nose, int eyes, int mouth)
    {
        super(16, 0, "", "", userId);

        this.type = type;
        this.hue = hue;
        this.nose = nose;
        this.eyes = eyes;
        this.mouth = mouth;

    }

    @Override
    public void run()
    {
        if(this.needsUpdate)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_pets SET mp_type = ?, mp_color = ?, mp_nose = ?, mp_eyes = ?, mp_mouth = ? WHERE id = ?");
                statement.setInt(1, this.type);
                statement.setInt(2, this.hue);
                statement.setInt(3, this.nose);
                statement.setInt(4, this.eyes);
                statement.setInt(5, this.mouth);
                statement.setInt(6, this.id);
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            super.run();
        }
    }

    @Override
    public boolean cycle()
    {

        return true;
    }

    public int getType()
    {
        return type;
    }

    public int getHue()
    {
        return hue;
    }

    public int getNose()
    {
        return nose;
    }

    public int getEyes()
    {
        return eyes;
    }

    public int getMouth()
    {
        return mouth;
    }
}
