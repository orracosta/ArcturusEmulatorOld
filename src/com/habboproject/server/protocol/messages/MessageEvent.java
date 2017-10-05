package com.habboproject.server.protocol.messages;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public final class MessageEvent {
    private final int id;
    private final ByteBuf buffer;

    public MessageEvent(ByteBuf buf) {
        this.buffer = (buf != null) && (buf.readableBytes() > 0) ? buf : Unpooled.EMPTY_BUFFER;

        if (this.content().readableBytes() >= 2) {
            this.id = this.readShort();
        } else {
            this.id = 0;
        }
    }

    public short readShort() {
        return this.content().readShort();
    }

    public int readInt() {
        try {
            return this.content().readInt();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean readBoolean() {
        return this.content().readByte() == 1;
    }

    public String readString() {
        int length = this.readShort();
        byte[] data = this.content().readBytes((length)).array();

        return new String(data);
    }

    public String toString() {
        String body = this.content().toString((Charset.defaultCharset()));

        for (int i = 0; i < 13; i++) {
            body = body.replace(Character.toString((char) i), "[" + i + "]");
        }

        return body;
    }

    public int getId() {
        return this.id;
    }

    private ByteBuf content() {
        return this.buffer;
    }
}