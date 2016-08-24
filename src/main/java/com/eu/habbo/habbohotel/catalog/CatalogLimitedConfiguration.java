package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;

public class CatalogLimitedConfiguration implements Runnable
{
    private final int itemId;
    private int totalSet;
    private final LinkedList<Integer> limitedNumbers;

    public CatalogLimitedConfiguration(int itemId, LinkedList<Integer> availableNumbers)
    {
        this.itemId = itemId;
        this.limitedNumbers = availableNumbers;
        Collections.shuffle(this.limitedNumbers);
    }

    public int getNumber()
    {
        synchronized (this.limitedNumbers)
        {
            int num = this.limitedNumbers.pop();
            return num;
        }
    }

    public void limitedSold(int catalogItemId, Habbo habbo, HabboItem item)
    {
        synchronized (this.limitedNumbers)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE catalog_items_limited SET user_id = ?, timestamp = ?, item_id = ? WHERE catalog_item_id = ? AND number = ? AND user_id = 0 LIMIT 1");
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, Emulator.getIntUnixTimestamp());
                statement.setInt(3, item.getId());
                statement.setInt(4, catalogItemId);
                statement.setInt(5, item.getLimitedSells());
                statement.executeUpdate();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }

    public void generateNumbers(int starting, int amount)
    {
        synchronized (this.limitedNumbers)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO catalog_items_limited (catalog_item_id, number) VALUES (?, ?)");
                statement.setInt(1, this.itemId);

                for (int i = starting; i <= amount; i++)
                {
                    statement.setInt(2, i);
                    statement.execute();
                    this.limitedNumbers.push(i);
                }

                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.totalSet += amount;
            Collections.shuffle(this.limitedNumbers);
        }
    }

    public int available()
    {
        return this.limitedNumbers.size();
    }

    public int getTotalSet()
    {
        return this.totalSet;
    }

    public void setTotalSet(int totalSet)
    {
        this.totalSet = totalSet;
    }

    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE catalog_items SET limited_stack = ?, limited_sells = ? WHERE id = ?");
            statement.setInt(1, this.totalSet);
            statement.setInt(2, this.totalSet - this.available());
            statement.setInt(3, this.itemId);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }
}