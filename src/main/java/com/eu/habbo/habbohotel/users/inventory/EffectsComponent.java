package com.eu.habbo.habbohotel.users.inventory;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.inventory.EffectsListAddComposer;
import com.eu.habbo.messages.outgoing.inventory.EffectsListEffectEnableComposer;
import com.eu.habbo.messages.outgoing.inventory.EffectsListRemoveComposer;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EffectsComponent
{
    public final THashMap<Integer, HabboEffect> effects;
    public final Habbo habbo;
    public int activatedEffect = 0;

    public EffectsComponent(Habbo habbo)
    {
        this.effects = new THashMap<>();
        this.habbo   = habbo;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_effects WHERE user_id = ?"))
        {
            statement.setInt(1, habbo.getHabboInfo().getId());
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.effects.put(set.getInt("effect"), new HabboEffect(set));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public HabboEffect createEffect(int effectId)
    {
        HabboEffect effect = null;
        synchronized (this.effects)
        {
            if (this.effects.containsKey(effectId))
            {
                effect = this.effects.get(effectId);

                if (effect.total <= 99)
                {
                    effect.total++;
                }
            }
            else
            {
                effect = new HabboEffect(effectId, this.habbo.getHabboInfo().getId());
                effect.insert();
            }

            this.addEffect(effect);
        }

        return effect;
    }

    public void addEffect(HabboEffect effect)
    {
        this.effects.put(effect.effect, effect);

        this.habbo.getClient().sendResponse(new EffectsListAddComposer(effect));
    }

    public void dispose()
    {
        synchronized (this.effects)
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users_effects SET duration = ?, activation_timestamp = ?, total = ? WHERE user_id = ? AND effect = ?"))
            {
                this.effects.forEachValue(new TObjectProcedure<HabboEffect>()
                {
                    @Override
                    public boolean execute(HabboEffect effect)
                    {
                        try
                        {
                            statement.setInt(1, effect.duration);
                            statement.setInt(2, effect.activationTimestamp);
                            statement.setInt(3, effect.total);
                            statement.setInt(4, effect.userId);
                            statement.setInt(5, effect.effect);
                            statement.addBatch();
                        }
                        catch (SQLException e)
                        {
                            Emulator.getLogging().logSQLException(e);
                        }

                        return true;
                    }
                });

                statement.executeBatch();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            this.effects.clear();
        }
    }

    public boolean ownsEffect(int effectId)
    {
        return this.effects.containsKey(effectId);
    }

    public void activateEffect(int effectId)
    {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null)
        {
            if (effect.isRemaining())
            {
                effect.activationTimestamp = Emulator.getIntUnixTimestamp();
            }
            else
            {
                this.habbo.getClient().sendResponse(new EffectsListRemoveComposer(effect));
            }
        }
    }

    public void enableEffect(int effectId)
    {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null)
        {
            if (!effect.isActivated())
            {
                this.activateEffect(effect.effect);
            }

            this.activatedEffect = effectId;

            if (this.habbo.getHabboInfo().getCurrentRoom() != null)
            {
                this.habbo.getHabboInfo().getCurrentRoom().giveEffect(this.habbo, effectId);
            }

            this.habbo.getClient().sendResponse(new EffectsListEffectEnableComposer(effect));
        }
    }

    public boolean hasActivatedEffect(int effectId)
    {
        HabboEffect effect = this.effects.get(effectId);

        if (effect != null)
        {
            return effect.isActivated();
        }

        return false;
    }

    public static class HabboEffect
    {
        public int effect;
        public int userId;
        public int duration = 86400;
        public int activationTimestamp = -1;
        public int total = 1;
        public boolean enabled = false;

        public HabboEffect(ResultSet set) throws SQLException
        {
            this.effect = set.getInt("effect");
            this.userId = set.getInt("user_id");
            this.duration = set.getInt("duration");
            this.activationTimestamp = set.getInt("activation_timestamp");
            this.total = set.getInt("total");
        }

        public HabboEffect(int effect, int userId)
        {
            this.effect = effect;
            this.userId = userId;
        }

        public boolean isActivated()
        {
            return this.activationTimestamp >= 0;
        }

        public boolean isRemaining()
        {
            if (this.total > 0)
            {
                if (this.activationTimestamp >= 0)
                {
                    if (Emulator.getIntUnixTimestamp() - this.activationTimestamp >= this.duration)
                    {
                        this.activationTimestamp = -1;
                        this.total--;
                    }
                }
            }

            return this.total > 0;
        }

        public void insert()
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_effects (user_id, effect, total, duration) VALUES (?, ?, ?, ?)"))
            {
                statement.setInt(1, this.userId);
                statement.setInt(2, this.effect);
                statement.setInt(3, this.total);
                statement.setInt(4, this.duration);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        public void delete()
        {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM users_effects WHERE user_id = ? AND effect = ?"))
            {
                statement.setInt(1, this.userId);
                statement.setInt(2, this.effect);
                statement.execute();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }
    }
}
