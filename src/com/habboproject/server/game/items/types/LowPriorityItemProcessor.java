package com.habboproject.server.game.items.types;

import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.threads.CometThread;
import org.apache.lucene.util.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LowPriorityItemProcessor implements CometThread {
    private final ScheduledExecutorService asyncItemEventQueue;
    private static final int processTime = 50;

    private List<RoomItemFloor> itemsToProcess;

    public LowPriorityItemProcessor() {
        this.asyncItemEventQueue = Executors.newScheduledThreadPool(2, new NamedThreadFactory("LowPriorityItemProcessor-%d"));
        this.itemsToProcess = new CopyOnWriteArrayList<>();

        this.asyncItemEventQueue.scheduleWithFixedDelay(this, processTime, processTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        List<RoomItemFloor> itemsToRemove = new ArrayList<>();

        for (RoomItemFloor roomItemFloor : itemsToProcess) {
            if (roomItemFloor.requiresTick()) {
                roomItemFloor.tick();
            } else {
                itemsToRemove.add(roomItemFloor);
            }
        }

        for (RoomItemFloor roomItemFloor : itemsToRemove) {
            this.itemsToProcess.remove(roomItemFloor);
        }

        itemsToRemove.clear();
    }

    public void submit(RoomItemFloor floorItem) {
        this.itemsToProcess.add(floorItem);
    }

    private static LowPriorityItemProcessor instance;

    public static LowPriorityItemProcessor getInstance() {
        if (instance == null) {
            instance = new LowPriorityItemProcessor();
        }

        return instance;
    }

    public static int getProcessTime(double time) {
        long realTime = Math.round(time * 1000 / processTime);

        if (realTime < 1) {
            realTime = 1;
        }

        return (int) realTime;
    }
}
