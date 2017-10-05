package com.habboproject.server.threads.executors.newbie;

import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.ai.bots.NewbieAI;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.threads.CometThread;

/**
 * Created by brend on 04/03/2017.
 */
public class NewbieFrankSayEvent implements CometThread {
    private final NewbieAI newbieAI;
    private final String message;

    public NewbieFrankSayEvent(NewbieAI newbieAI, String message) {
        this.newbieAI = newbieAI;
        this.message = message;
    }

    @Override
    public void run() {
        newbieAI.getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(newbieAI.getEntity().getId(), message,
                RoomManager.getInstance().getEmotions().getEmotion(message), 33));

        newbieAI.increaseStep();
        newbieAI.setNextStep(true);
    }
}
