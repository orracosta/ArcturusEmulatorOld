package com.habboproject.server.api.networking.messages;

import io.netty.buffer.ByteBuf;

public interface IMessageComposer {
    IComposer writeMessage(ByteBuf buffer);

    short getId();

    void compose(IComposer msg);

    void dispose();
}
