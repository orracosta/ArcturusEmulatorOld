package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.habbohotel.achievements.TalentTrackType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import javafx.util.Pair;

import java.util.List;

public class TalentLevelUpdateComposer extends MessageComposer
{
    private final TalentTrackType talentTrackType;
    private final int level;
    private final List<String> unknownStringList;
    private final List<Pair<String, Integer>> unknownPairs;

    public TalentLevelUpdateComposer(TalentTrackType talentTrackType, int level, List<String> unknownStringList, List<Pair<String, Integer>> unknownPairs)
    {
        this.talentTrackType = talentTrackType;
        this.level = level;
        this.unknownStringList = unknownStringList;
        this.unknownPairs = unknownPairs;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TalentLevelUpdateComposer);
        this.response.appendString(this.talentTrackType.name());
        this.response.appendInt(this.level);
        this.response.appendInt(this.unknownStringList.size());
        for (String s : this.unknownStringList)
        {
            this.response.appendString(s);
        }

        this.response.appendInt(this.unknownPairs.size());
        for (Pair<String, Integer> pair : this.unknownPairs)
        {
            this.response.appendString(pair.getKey());
            this.response.appendInt(pair.getValue());
        }
        return this.response;
    }
}