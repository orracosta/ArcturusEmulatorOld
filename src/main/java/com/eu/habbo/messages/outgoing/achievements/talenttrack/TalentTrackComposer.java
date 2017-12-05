package com.eu.habbo.messages.outgoing.achievements.talenttrack;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.*;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
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
            this.response.appendInt(talentTrackLevels.size()); //Count
            final boolean[] allCompleted = {true};
            for (Map.Entry<Integer, TalentTrackLevel> set : talentTrackLevels.entrySet())
            {
                try
                {
                    TalentTrackLevel level = set.getValue();

                    this.response.appendInt(level.level);

                    TalentTrackState state = TalentTrackState.LOCKED;

                    int currentLevel = habbo.getHabboStats().talentTrackLevel(this.type);

                    if (currentLevel + 1 == level.level)
                    {
                        state = TalentTrackState.IN_PROGRESS;
                    }
                    else if (currentLevel >= level.level)
                    {
                        state = TalentTrackState.COMPLETED;
                    }

                    this.response.appendInt(state.id);
                    this.response.appendInt(level.achievements.size());

                    final TalentTrackState finalState = state;
                    level.achievements.forEachEntry(new TObjectIntProcedure<Achievement>()
                    {
                        @Override
                        public boolean execute(Achievement achievement, int b)
                        {
                            if (achievement != null)
                            {
                                response.appendInt(achievement.id);

                                //TODO Move this to TalenTrackLevel class
                                response.appendInt(1); //idk
                                response.appendString("ACH_" + achievement.name + b);

                                int progress = habbo.getHabboStats().getAchievementProgress(achievement);
                                AchievementLevel achievementLevel = achievement.getLevelForProgress(progress);

                                if (finalState != TalentTrackState.LOCKED)
                                {
                                    if (achievementLevel.progress <= progress)
                                    {
                                        response.appendInt(2);
                                    }
                                    else
                                    {
                                        response.appendInt(1);
                                        allCompleted[0] = false;
                                    }
                                }
                                else
                                {
                                    response.appendInt(0);
                                    allCompleted[0] = false;
                                }
                                response.appendInt(progress);
                                response.appendInt(achievementLevel.progress);
                            }
                            else
                            {
                                response.appendInt(0);
                                response.appendInt(0);
                                response.appendString("");
                                response.appendString("");
                                response.appendInt(0);
                                response.appendInt(0);
                                response.appendInt(0);
                            }
                            return true;
                        }
                    });


                    this.response.appendInt(level.perks.length);
                    for (String perk : level.perks)
                    {
                        this.response.appendString(perk);
                    }

                    this.response.appendInt(level.items.size());
                    for (Item item : level.items)
                    {
                        this.response.appendString(item.getName());
                        this.response.appendInt(0);
                    }
                }
                catch (NoSuchElementException e)
                {
                    return null;
                }
            }
        }
        else
        {
            this.response.appendInt(0);
        }
        return this.response;
    }
}
