package com.habboproject.server.threads.executors.newbie;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.bots.NewbieAI;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/03/2017.
 */
public class NewbieFrankLeaveEvent implements CometThread {
    private final NewbieAI newbieAI;
    private final PlayerEntity playerEntity;

    public NewbieFrankLeaveEvent(NewbieAI newbieAI, PlayerEntity playerEntity) {
        this.newbieAI = newbieAI;
        this.playerEntity = playerEntity;
    }

    @Override
    public void run() {
        newbieAI.getBotEntity().leaveRoom();

        ThreadManager.getInstance().executeSchedule(new NewbieLobbyOfferEvent(playerEntity), 3, TimeUnit.SECONDS);
    }
}
