package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.objects.items.queue.RoomItemEventQueue;
import com.habboproject.server.game.rooms.objects.items.types.floor.rollable.RollableFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.rollers.RollerFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreMostWinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreTeamFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPeriodically;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.utilities.TimeSpan;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ItemProcessComponent implements CometThread {
    //    private final int INTERVAL = Integer.parseInt(Configuration.currentConfig().get("comet.system.item_process.interval"));
    private static final int INTERVAL = 500;
    private static final int FLAG = 400;

    private final Room room;
    private final Logger log;

    private ScheduledFuture myFuture;

    private boolean active = false;

    // TODO: Finish the item event queue.
    private RoomItemEventQueue eventQueue;// = new RoomItemEventQueue();

    public ItemProcessComponent(Room room) {
        this.room = room;

        log = Logger.getLogger("Item Process [" + room.getData().getName() + "]");
    }

    public RoomItemEventQueue getEventQueue() {
        return this.eventQueue;
    }

    public void start() {
        if (Room.useCycleForItems) {
            this.active = true;
            return;
        }

        if (this.myFuture != null && this.active) {
            stop();
        }

        this.active = true;
        this.myFuture = ThreadManager.getInstance().executePeriodic(this, 0, INTERVAL, TimeUnit.MILLISECONDS);

        log.debug("Processing started");
    }

    public void stop() {
        if (Room.useCycleForItems) {
            this.active = false;
            return;
        }

        if (this.myFuture != null) {
            this.active = false;
            this.myFuture.cancel(false);

            log.debug("Processing stopped");
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public void tick() {
        if (!this.active) {
            return;
        }

        long timeStart = System.currentTimeMillis();

        if (this.getRoom().getEntities().realPlayerCount() == 0) return;

        final Set<String> positionsWithPeriodicTrigger = new HashSet<>();

        for (RoomItemFloor item : this.getRoom().getItems().getFloorItems().values()) {
            try {
                if (item != null && !(item instanceof RollableFloorItem) && item.requiresTick() || item instanceof RollerFloorItem) {
                    if(item instanceof WiredTriggerPeriodically) {
                        final String posStr = item.getPosition().getX() + "_" + item.getPosition().getY();

                        if(positionsWithPeriodicTrigger.contains(posStr)) {
                            continue;
                        } else {
                            positionsWithPeriodicTrigger.add(posStr);
                        }
                    }

                    item.tick();
                }

                if (item != null && item instanceof HighscoreClassicFloorItem) {
                    if (((HighscoreClassicFloorItem)item).needsReset()) {
                        ((HighscoreClassicFloorItem)item).reset();
                    }
                }

                if (item != null && item instanceof HighscoreTeamFloorItem) {
                    if (((HighscoreTeamFloorItem)item).needsReset()) {
                        ((HighscoreTeamFloorItem)item).reset();
                    }
                }

                if (item != null && item instanceof HighscoreMostWinFloorItem) {
                    if (((HighscoreMostWinFloorItem)item).needsReset()) {
                        ((HighscoreMostWinFloorItem)item).reset();
                    }
                }
            } catch (Exception e) {
                this.handleException(item, e);
            }
        }

        for (RoomItemWall item : this.getRoom().getItems().getWallItems().values()) {
            try {
                if (item != null && item.requiresTick()) {
                    item.tick();
                }
            } catch (Exception e) {
                this.handleException(item, e);
            }
        }

        TimeSpan span = new TimeSpan(timeStart, System.currentTimeMillis());

        if (span.toMilliseconds() > FLAG && Comet.isDebugging) {
            log.warn("ItemProcessComponent process took: " + span.toMilliseconds() + "ms to execute.");
        }
    }

    @Override
    public void run() {
        this.tick();
    }

    protected void handleException(RoomItem item, Exception e) {
        log.error("Error while processing item: " + item.getId() + " (" + item.getClass().getSimpleName() + ")", e);
    }

    public Room getRoom() {
        return this.room;
    }
}