package com.eu.habbo.habbohotel.wired;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredCondition;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredTriggerReset;
import com.eu.habbo.habbohotel.items.interactions.wired.effects.WiredEffectGiveReward;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.WiredRewardAlertComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import com.eu.habbo.messages.outgoing.users.UserCreditsComposer;
import com.eu.habbo.messages.outgoing.users.UserCurrencyComposer;
import com.eu.habbo.plugin.events.furniture.wired.WiredConditionFailedEvent;
import com.eu.habbo.plugin.events.furniture.wired.WiredStackExecutedEvent;
import com.eu.habbo.plugin.events.furniture.wired.WiredStackTriggeredEvent;
import com.eu.habbo.plugin.events.users.UserWiredRewardReceived;
import com.eu.habbo.threading.runnables.HabboItemNewState;
import com.eu.habbo.util.pathfinding.Tile;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredHandler
{
    public static boolean handle(WiredTriggerType triggerType, RoomUnit roomUnit, Room room, Object[] stuff)
    {
        boolean talked = false;

        if(!room.isLoaded())
            return false;

        if(room.getRoomSpecialTypes() == null)
            return false;

        THashSet<InteractionWiredTrigger> triggers =  room.getRoomSpecialTypes().getTriggers(triggerType);

        if(triggers == null || triggers.isEmpty())
            return false;

        for(InteractionWiredTrigger trigger : triggers)
        {
            if(handle(trigger, roomUnit, room, stuff))
            {
                if(triggerType.equals(WiredTriggerType.SAY_SOMETHING))
                    talked = true;

                trigger.setExtradata("1");
                room.updateItem(trigger);
                Emulator.getThreading().run(new HabboItemNewState(trigger, room, "0"), 500);
            }
        }

        return talked;
    }

    public static boolean handle(InteractionWiredTrigger trigger, RoomUnit roomUnit, Room room, Object[] stuff)
    {
        if(trigger.execute(roomUnit, room, stuff))
        {
            THashSet<InteractionWiredCondition> conditions = room.getRoomSpecialTypes().getConditions(trigger.getX(), trigger.getY());

            THashSet<InteractionWiredEffect> effects = room.getRoomSpecialTypes().getEffects(trigger.getX(), trigger.getY());

            if(Emulator.getPluginManager().fireEvent(new WiredStackTriggeredEvent(roomUnit, trigger, effects, conditions)).isCancelled())
                return false;

            for(InteractionWiredCondition condition : conditions)
            {
                if(condition.execute(roomUnit, room, stuff))
                {
                    condition.setExtradata("1");
                    room.updateItem(condition);
                    Emulator.getThreading().run(new HabboItemNewState(condition, room, "0"), 500);
                }
                else
                {
                    if(!Emulator.getPluginManager().fireEvent(new WiredConditionFailedEvent(roomUnit, trigger, condition)).isCancelled())
                        return false;
                }
            }

            for(InteractionWiredEffect effect : effects)
            {
                if(effect.execute(roomUnit, room, stuff))
                {
                    effect.setExtradata("1");
                    room.updateItem(effect);
                    Emulator.getThreading().run(new HabboItemNewState(effect, room, "0"), 500);
                }
            }

            trigger.setExtradata("1");
            room.updateItem(trigger);
            Emulator.getThreading().run(new HabboItemNewState(trigger, room, "0"), 500);

            return !Emulator.getPluginManager().fireEvent(new WiredStackExecutedEvent(roomUnit, trigger, effects, conditions)).isCancelled();
        }

        return false;
    }

    public static boolean executeEffectsAtTiles(THashSet<Tile> tiles, RoomUnit roomUnit, Room room, Object[] stuff)
    {
        for(Tile tile : tiles)
        {
            if(room != null)
            {
                THashSet<HabboItem> items = room.getItemsAt(tile.X, tile.Y);

                for(HabboItem item : items)
                {
                    if(item instanceof InteractionWiredEffect)
                    {
                        ((InteractionWiredEffect) item).execute(roomUnit, room, stuff);

                        item.setExtradata("1");
                        room.updateItem(item);
                        Emulator.getThreading().run(new HabboItemNewState(item, room, "0"), 500);
                    }
                }
            }
        }

        return true;
    }

    public static void dropRewards(int wiredId)
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("DELETE FROM wired_rewards_given WHERE wired_item = ?");
            statement.setInt(1, wiredId);
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private static void giveReward(Habbo habbo, WiredEffectGiveReward wiredBox, WiredGiveRewardItem reward)
    {
        if(wiredBox.limit > 0)
            wiredBox.given++;

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO wired_rewards_given (wired_item, user_id, reward_id, timestamp) VALUES ( ?, ?, ?, ?)");
            statement.setInt(1, wiredBox.getId());
            statement.setInt(2, habbo.getHabboInfo().getId());
            statement.setInt(3, reward.id);
            statement.setInt(4, Emulator.getIntUnixTimestamp());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if(reward.badge)
        {
            UserWiredRewardReceived rewardReceived = new UserWiredRewardReceived(habbo, wiredBox, "badge", reward.data);
            if(Emulator.getPluginManager().fireEvent(rewardReceived).isCancelled())
                return;

            if(rewardReceived.value.isEmpty())
                return;

            HabboBadge badge = new HabboBadge(0, rewardReceived.value, 0, habbo);
            Emulator.getThreading().run(badge);
            habbo.getHabboInventory().getBadgesComponent().addBadge(badge);
            habbo.getClient().sendResponse(new AddUserBadgeComposer(badge));
            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_RECEIVED_BADGE));
        }
        else
        {
            String[] data = reward.data.split("#");

            if (data.length == 2)
            {
                UserWiredRewardReceived rewardReceived = new UserWiredRewardReceived(habbo, wiredBox, data[0], data[1]);
                if(Emulator.getPluginManager().fireEvent(rewardReceived).isCancelled())
                    return;

                if(rewardReceived.value.isEmpty())
                    return;

                if(rewardReceived.type.equalsIgnoreCase("credits"))
                {
                    int credits = Integer.valueOf(rewardReceived.value);
                    habbo.getHabboInfo().addCredits(credits);
                    habbo.getClient().sendResponse(new UserCreditsComposer(habbo));
                }
                else if(rewardReceived.type.equalsIgnoreCase("pixels"))
                {
                    int pixels = Integer.valueOf(rewardReceived.value);
                    habbo.getHabboInfo().addPixels(pixels);
                    habbo.getClient().sendResponse(new UserCurrencyComposer(habbo));
                }
                else  if(rewardReceived.type.equalsIgnoreCase("points"))
                {
                    int points = Integer.valueOf(rewardReceived.value);
                    habbo.getHabboInfo().addCurrencyAmount(Emulator.getConfig().getInt("seasonal.primary.type"), points);
                    habbo.getClient().sendResponse(new UserCurrencyComposer(habbo));
                }
                else if(rewardReceived.type.equalsIgnoreCase("furni"))
                {
                    Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(Integer.valueOf(rewardReceived.value));
                    if(baseItem != null)
                    {
                        HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(habbo.getHabboInfo().getId(), baseItem, 0, 0, "");

                        if(item != null)
                        {
                            habbo.getClient().sendResponse(new AddHabboItemComposer(item));
                            habbo.getClient().getHabbo().getHabboInventory().getItemsComponent().addItem(item);
                            habbo.getClient().sendResponse(new PurchaseOKComposer(null, null));
                            habbo.getClient().sendResponse(new InventoryRefreshComposer());
                            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_RECEIVED_ITEM));
                        }
                    }
                }
                else if(rewardReceived.type.equalsIgnoreCase("respect"))
                {
                    habbo.getHabboStats().respectPointsReceived += Integer.valueOf(rewardReceived.value);
                }
            }
        }
    }

    public static boolean getReward(Habbo habbo, WiredEffectGiveReward wiredBox)
    {
        if(wiredBox.limit > 0)
        {
            if(wiredBox.limit - wiredBox.given == 0)
            {
                habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.LIMITED_NO_MORE_AVAILABLE));
                return false;
            }
        }

        PreparedStatement statement = null;
        ResultSet set = null;
        try
        {
            statement = Emulator.getDatabase().prepare("SELECT COUNT(*) as rows, wired_rewards_given.* FROM wired_rewards_given WHERE user_id = ? AND wired_item = ? ORDER BY timestamp DESC LIMIT ?");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, wiredBox.getId());
            statement.setInt(3, wiredBox.rewardItems.size());

            set = statement.executeQuery();

            if(set.first())
            {
                if (set.getInt("rows") >= 1)
                {
                    if (wiredBox.rewardTime == 0)
                    {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALREADY_RECEIVED));
                        return false;
                    }
                }

                set.beforeFirst();
                if (set.next())
                {
                    if (Emulator.getIntUnixTimestamp() - set.getInt("timestamp") <= 60)
                    {
                        habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALREADY_RECEIVED_THIS_MINUTE));
                        return false;
                    }

                    if (wiredBox.uniqueRewards)
                    {
                        if (set.getInt("rows") == wiredBox.rewardItems.size())
                        {
                            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALL_COLLECTED));
                            return false;
                        }
                    }

                    if (wiredBox.rewardTime == 1)
                    {
                        if (!(Emulator.getIntUnixTimestamp() - set.getInt("timestamp") >= 3600))
                        {
                            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALREADY_RECEIVED_THIS_HOUR));
                            return false;
                        }
                    }

                    if (wiredBox.rewardTime == 2)
                    {
                        if (!(Emulator.getIntUnixTimestamp() - set.getInt("timestamp") >= 86400))
                        {
                            habbo.getClient().sendResponse(new WiredRewardAlertComposer(WiredRewardAlertComposer.REWARD_ALREADY_RECEIVED_THIS_TODAY));
                            return false;
                        }
                    }
                }

                if (wiredBox.uniqueRewards)
                {
                    for (WiredGiveRewardItem item : wiredBox.rewardItems)
                    {
                        set.beforeFirst();
                        boolean found = false;

                        while (set.next())
                        {
                            if (set.getInt("reward_id") == item.id)
                                found = true;
                        }

                        if (!found)
                        {
                            giveReward(habbo, wiredBox, item);
                            return true;
                        }
                    }
                } else
                {
                    int randomNumber = Emulator.getRandom().nextInt(101);

                    int count = 0;
                    for (WiredGiveRewardItem item : wiredBox.rewardItems)
                    {
                        if (randomNumber >= count && randomNumber <= (count + item.probability))
                        {
                            giveReward(habbo, wiredBox, item);
                            return true;
                        }

                        count += item.probability;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
        finally
        {
            if(set != null)
            {
                try
                {
                    set.close();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }

            if(statement != null)
            {
                try
                {
                    statement.close();
                    statement.getConnection().close();
                }
                catch (SQLException e)
                {
                    Emulator.getLogging().logSQLException(e);
                }
            }
        }

        return false;
    }

    public static void resetTimers(Room room)
    {
        if(!room.isLoaded())
            return;

        for(InteractionWiredTrigger trigger : room.getRoomSpecialTypes().getTriggers(WiredTriggerType.AT_GIVEN_TIME))
        {
            ((WiredTriggerReset)trigger).resetTimer();
        }

        room.setLastTimerReset(Emulator.getIntUnixTimestamp());
    }
}
