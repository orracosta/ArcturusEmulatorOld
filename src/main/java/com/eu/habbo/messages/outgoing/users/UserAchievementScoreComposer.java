package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 14-2-2015 12:03.
 */
public class UserAchievementScoreComposer extends MessageComposer
{
    private final Habbo habbo;

    public UserAchievementScoreComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserAchievementScoreComposer);
        this.response.appendInt32(habbo.getHabboInfo().getAchievementScore());
        return this.response;
    }
}
