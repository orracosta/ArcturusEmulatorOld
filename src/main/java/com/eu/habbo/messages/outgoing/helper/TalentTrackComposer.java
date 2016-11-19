package com.eu.habbo.messages.outgoing.helper;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.*;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.users.AddUserBadgeComposer;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TObjectIntProcedure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TalentTrackComposer extends MessageComposer
{
    public enum TalentTrackState
    {
        LOCKED(0),
        IN_PROGRESS(1),
        COMPLETED(2);

        public final int id;

        TalentTrackState(int id)
        {
            this.id = id;
        }
    }

    public final Habbo habbo;
    public final TalentTrackType type;

    public TalentTrackComposer(Habbo habbo, TalentTrackType type)
    {
        this.habbo = habbo;
        this.type = type;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TalentTrackComposer);
        this.response.appendString(this.type.name().toLowerCase());

        LinkedHashMap<Integer, TalentTrackLevel> talentTrackLevels = Emulator.getGameEnvironment().getAchievementManager().getTalenTrackLevels(this.type);

        if (talentTrackLevels != null)
        {
            this.response.appendInt32(talentTrackLevels.size()); //Count

            final boolean[] allCompleted = {true};
            for (Map.Entry<Integer, TalentTrackLevel> set : talentTrackLevels.entrySet())
            {
                try
                {
                    TalentTrackLevel level = set.getValue();

                    this.response.appendInt32(level.level);

                    TalentTrackState state = TalentTrackState.LOCKED;

                    int currentLevel = habbo.getHabboStats().talentTrackLevel(this.type);

                    if (currentLevel == level.level)
                    {
                        state = TalentTrackState.IN_PROGRESS;
                    }
                    else if (currentLevel > level.level)
                    {
                        state = TalentTrackState.COMPLETED;
                    }

                    this.response.appendInt32(state.id);
                    this.response.appendInt32(level.achievements.size());

                    level.achievements.forEachEntry(new TObjectIntProcedure<Achievement>()
                    {
                        @Override
                        public boolean execute(Achievement achievement, int b)
                        {
                            response.appendInt32(achievement.id);

                            //TODO Move this to TalenTrackLevel class
                            response.appendInt32(1); //idk
                            response.appendString("ACH_" + achievement.name + b);

                            int progress = habbo.getHabboStats().getAchievementProgress(achievement);
                            AchievementLevel achievementLevel = achievement.getLevelForProgress(progress);

                            if (progress > 0)
                            {
                                if (achievementLevel.progress <= progress)
                                {
                                    response.appendInt32(2);
                                }
                                else
                                {
                                    response.appendInt32(1);
                                    allCompleted[0] = false;
                                }
                            }
                            else
                            {
                                response.appendInt32(0);
                                allCompleted[0] = false;
                            }
                            response.appendInt32(progress);
                            response.appendInt32(achievementLevel.progress);

                            return true;
                        }
                    });

                    boolean giveRewards = allCompleted[0] && currentLevel < level.level && currentLevel >= 0;

                    this.response.appendInt32(level.perks.length);
                    for (String perk : level.perks)
                    {
                        this.response.appendString(perk);
                    }

                    this.response.appendInt32(level.items.size());
                    for (Item item : level.items)
                    {
                        if (giveRewards)
                        {
                            HabboItem rewardItem = Emulator.getGameEnvironment().getItemManager().createItem(this.habbo.getHabboInfo().getId(), item, 0, 0, "");
                            this.habbo.getHabboInventory().getItemsComponent().addItem(rewardItem);
                            this.habbo.getClient().sendResponse(new AddHabboItemComposer(rewardItem));
                        }
                        this.response.appendString(item.getName());
                        this.response.appendInt32(0);
                    }

                    if (giveRewards)
                    {
                        for (String badge : level.badges)
                        {
                            if (!badge.isEmpty())
                            {
                                HabboBadge b = new HabboBadge(0, badge, 0, habbo);
                                Emulator.getThreading().run(b);
                                this.habbo.getHabboInventory().getBadgesComponent().addBadge(b);
                                this.habbo.getClient().sendResponse(new AddUserBadgeComposer(b));
                            }
                        }
                    }

                    habbo.getHabboStats().setTalentLevel(type, level.level);
                }
                catch (NoSuchElementException e)
                {
                    return null;
                }
            }
        }
        else
        {
            this.response.appendInt32(0);
        }
        return this.response;
    }
}
