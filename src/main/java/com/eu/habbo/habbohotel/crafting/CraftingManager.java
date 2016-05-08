package com.eu.habbo.habbohotel.crafting;

import com.eu.habbo.Emulator;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CraftingManager
{
    private final THashSet<CraftingRecipe> recipes = new THashSet<CraftingRecipe>();

    public CraftingManager()
    {
        long millis = System.currentTimeMillis();
        this.reload();
        Emulator.getLogging().logStart("Catalog Manager -> Loaded! ("+(System.currentTimeMillis() - millis)+" MS)");
    }

    public void reload()
    {
        synchronized (this.recipes)
        {
            this.recipes.clear();

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM crafting_recipes WHERE enabled = ?");
                statement.setString(1, "1");
                ResultSet set = statement.executeQuery();

                while (set.next())
                {
                    this.recipes.add(new CraftingRecipe(set));
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            catch (Exception e)
            {
                Emulator.getLogging().logErrorLine(e);
            }
        }
    }

    public THashSet<CraftingRecipe> getRecipes()
    {
        return this.recipes;
    }

    public void dispose()
    {
        this.recipes.clear();
    }
}
