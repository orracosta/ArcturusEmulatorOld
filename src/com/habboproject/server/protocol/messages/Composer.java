package com.habboproject.server.protocol.messages;

import com.habboproject.server.api.networking.messages.IComposer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

import java.nio.charset.Charset;

public class Composer implements ByteBufHolder, IComposer {
    protected final int id;
    protected final ByteBuf body;

    public Composer(short id, ByteBuf body) {
        this.id = id;
        this.body = body;

        try {
            this.body.writeInt(-1);
            this.body.writeShort(id);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }

    private void exceptionCaught(Exception e) {
        e.printStackTrace();
    }

    public Composer(int id, ByteBuf body) {
        this.id = id;
        this.body = body;
    }

    @Override
    public ByteBuf content() {
        return this.body;
    }

    @Override
    public Composer copy() {
        return new Composer(this.id, this.body.copy());
    }

    @Override
    public Composer duplicate() {
        return new Composer(this.id, this.body.duplicate());
    }

    @Override
    public int refCnt() {
        return this.body.refCnt();
    }

    @Override
    public Composer retain() {
        return new Composer(this.id, this.body.retain());
    }

    @Override
    public Composer retain(int increment) {
        return new Composer(this.id, this.body.retain(increment));
    }

    @Override
    public boolean release() {
        return this.body.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.body.release(decrement);
    }

    public int getId() {
        return this.id;
    }

    public void clear() {
        this.body.clear();
    }

    public boolean hasLength() {
        return (this.body.getInt(0) > -1);
    }

    public void writeString(Object obj) {
        try {
            String string = "";

            if (obj != null) {
                string = String.valueOf(obj);
            }

            byte[] dat = string.getBytes(Charset.forName("UTF-8"));
            this.body.writeShort(dat.length);
            this.body.writeBytes(dat);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }

    public void writeDouble(double d) {
        this.writeString(Double.toString(d));
    }

    public void writeInt(int i) {
        try {
            this.body.writeInt(i);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }

    public void writeLong(long i) {
        try {
            this.body.writeLong(i);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }

    public void writeBoolean(Boolean b) {
        try {
            this.body.writeByte(b ? 1 : 0);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }

    public void writeByte(int b) {
        try {
            this.body.writeByte(b);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }


    public void writeShort(int s) {
        try {
            this.body.writeShort((short) s);
        } catch (Exception e) {
            exceptionCaught(e);
        }
    }
}