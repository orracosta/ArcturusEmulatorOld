package com.eu.habbo.habbohotel.pets;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GnomePet extends Pet implements IPetLook
{
    private final String gnomeData;

    public GnomePet(ResultSet set) throws SQLException
    {
        super(set);

        this.gnomeData = set.getString("gnome_data");
    }

    public GnomePet( int type, int race, String color, String name,int userId, String gnomeData)
    {
        super(type, race, color, name, userId);

        this.gnomeData = gnomeData;
    }

    @Override
    public void run()
    {
        if (this.needsUpdate)
        {
            super.run();

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_pets SET gnome_data = ? WHERE id = ? LIMIT 1"))
            {
                statement.setString(1, this.gnomeData);
                statement.setInt(2, this.id);
                statement.executeUpdate();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    @Override
    public String getLook()
    {
        return this.getPetData().getType() + " 0 FFFFFF " + this.gnomeData;
    }

    @Override
    public void scratched(Habbo habbo)
    {
        super.scratched(habbo);

        if (this.getPetData().getType() == 26)
        {
            if (habbo != null)
            {
                AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GnomeRespectGiver"));
            }
            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("GnomeRespectReceiver"));
        }
        else if (this.getPetData().getType() == 27)
        {
            if (habbo != null)
            {
                AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("LeprechaunRespectGiver"));
            }
            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("LeprechaunRespectReceiver"));
        }

    }

    @Override
    protected void levelUp()
    {
        super.levelUp();

        if (this.getPetData().getType() == 26)
        {
            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("GnomeLevelUp"));
        }
        else if (this.getPetData().getType() == 27)
        {
            AchievementManager.progressAchievement(Emulator.getGameEnvironment().getHabboManager().getHabbo(this.getUserId()), Emulator.getGameEnvironment().getAchievementManager().getAchievement("LeprechaunLevelUp"));
        }
    }
}