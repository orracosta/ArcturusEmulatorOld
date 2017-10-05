package com.habboproject.server.storage.queue;

import com.habboproject.server.threads.CometThread;
import com.habboproject.server.utilities.Initializable;

public interface StorageQueue<T> extends Initializable, CometThread {

    void queueSave(T object);

    void unqueue(T object);

    boolean isQueued(T object);

    void shutdown();
}
