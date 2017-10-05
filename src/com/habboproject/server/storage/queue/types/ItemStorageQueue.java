package com.habboproject.server.storage.queue.types;

import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.storage.queries.items.TradeDao;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.habboproject.server.storage.queue.StorageQueue;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.utilities.Initializable;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ItemStorageQueue implements Initializable, CometThread, StorageQueue<RoomItem> {
    private static final Logger log = Logger.getLogger(ItemStorageQueue.class.getName());
    private static ItemStorageQueue instance;

    private ScheduledFuture future;

    private List<RoomItem> itemsToStoreData;
    private List<RoomItem> itemsToStore;

    private Map<Long, Integer> itemsToChangeOwner;

    public ItemStorageQueue() {
        this.itemsToStoreData = new CopyOnWriteArrayList<>();
        this.itemsToStore = new CopyOnWriteArrayList<>();

        this.itemsToChangeOwner = new ConcurrentHashMap<>();
    }

    @Override
    public void initialize() {
        this.future = ThreadManager.getInstance().executePeriodic(this, 0, 1500, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        if (this.itemsToStoreData.size() == 0 && this.itemsToStore.size() == 0 && this.itemsToChangeOwner.size() == 0) return;

        log.debug("Saving " + (this.itemsToStoreData.size() + this.itemsToStore.size() + this.itemsToChangeOwner.size()) + " items");

        RoomItemDao.processBatch(this.itemsToStoreData);
        RoomItemDao.saveFloorItems(this.itemsToStore);
        TradeDao.updateTradeItems(this.itemsToChangeOwner);

        this.itemsToStoreData.clear();
        this.itemsToStore.clear();
        this.itemsToChangeOwner.clear();
    }

    public void queueSaveData(final RoomItem roomItem) {
        if (this.itemsToStoreData.contains(roomItem)) {
            this.itemsToStoreData.remove(roomItem);
        }

        this.itemsToStoreData.add(roomItem);
    }

    @Override
    public void queueSave(final RoomItem roomItem) {
        if(this.itemsToStore.contains(roomItem)) {
            this.itemsToStore.remove(roomItem);
        }

        this.itemsToStore.add(roomItem);
    }


    @Override
    public boolean isQueued(RoomItem object) {
        return this.itemsToStore.contains(object) || this.itemsToStoreData.contains(object);
    }

    public void changeItemOwner(Long itemId, int newOwner) {
        if(this.itemsToChangeOwner.containsKey(itemId)) {
            this.itemsToChangeOwner.remove(itemId);
        }

        this.itemsToChangeOwner.put(itemId, newOwner);
    }

    @Override
    public void shutdown() {
        this.future.cancel(false);

        log.info("Executing " + this.itemsToStoreData.size() + " item data updates");

        // Run 1 final time, to make sure everything is saved!
        this.run();
    }

    public static ItemStorageQueue getInstance() {
        if (instance == null) {
            instance = new ItemStorageQueue();
        }

        return instance;
    }

    @Override
    public void unqueue(RoomItem floorItem) {
        if(this.itemsToStore.contains(floorItem)) {
            this.itemsToStore.remove(floorItem);
        }
    }
}
