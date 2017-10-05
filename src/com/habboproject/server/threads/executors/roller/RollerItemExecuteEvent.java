package com.habboproject.server.threads.executors.roller;

import com.habboproject.server.threads.executors.item.FloorItemExecuteEvent;

/**
 * Created by brend on 29/04/2017.
 */
public class RollerItemExecuteEvent extends FloorItemExecuteEvent {
    public RollerItemExecuteEvent(int totalTicks) {
        super(totalTicks);
    }
}
