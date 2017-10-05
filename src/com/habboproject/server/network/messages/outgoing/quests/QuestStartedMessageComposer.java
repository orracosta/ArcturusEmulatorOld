package com.habboproject.server.network.messages.outgoing.quests;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.quests.types.Quest;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class QuestStartedMessageComposer extends MessageComposer {
    private final Player player;
    private final Quest quest;

    public QuestStartedMessageComposer(Quest quest, Player player) {
        this.quest = quest;
        this.player = player;
    }

    @Override
    public short getId() {
        return Composers.QuestStartedMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        this.quest.compose(player, msg);
    }
}
