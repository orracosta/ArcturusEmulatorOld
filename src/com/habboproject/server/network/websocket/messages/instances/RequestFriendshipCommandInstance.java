package com.habboproject.server.network.websocket.messages.instances;

import com.habboproject.server.utilities.JsonData;

/**
 * Created by brend on 02/03/2017.
 */
public class RequestFriendshipCommandInstance implements JsonData {
    private final int peddingId;
    private final int requestId;

    public RequestFriendshipCommandInstance(int peddingId, int requestId) {
        this.peddingId = peddingId;
        this.requestId = requestId;
    }

    public int getPeddingId() {
        return peddingId;
    }

    public int getRequestId() {
        return requestId;
    }
}
