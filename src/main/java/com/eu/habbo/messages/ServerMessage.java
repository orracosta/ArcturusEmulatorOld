package com.eu.habbo.messages;

import com.eu.habbo.Emulator;

import java.io.IOException;
import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

public class ServerMessage
{
    private int header;
    private ByteBufOutputStream stream;
    private ByteBuf channelBuffer;

    public ServerMessage() {}

    public ServerMessage(int header)
    {
        this.header = header;
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);
        try
        {
            this.stream.writeInt(0);
            this.stream.writeShort(header);
        }
        catch (Exception e)
        {
            Emulator.getLogging().handleException(e);
        }
    }

    public ServerMessage init(int id)
    {
        this.header = id;
        this.channelBuffer = Unpooled.buffer();
        this.stream = new ByteBufOutputStream(this.channelBuffer);

        try
        {
            this.stream.writeInt(0);
            this.stream.writeShort(id);
        }
        catch (Exception e)
        {
            Emulator.getLogging().handleException(e);
        }
        return this;
    }

    public void appendRawBytes(byte[] bytes)
    {
        try
        {
            this.stream.write(bytes);
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendString(String obj)
    {
        if (obj == null)
        {
            this.appendString("");
            return;
        }

        try
        {
            byte[] data = obj.getBytes();
            this.stream.writeShort(data.length);
            this.stream.write(data);
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendChar(int obj)
    {
        try
        {
            this.stream.writeChar(obj);
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendChars(Object obj)
    {
        try
        {
            this.stream.writeChars(obj.toString());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    @Deprecated
    public void appendInt32(Integer obj)
    {
        try
        {
            this.stream.writeInt(obj.intValue());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendInt(Integer obj)
    {
        try
        {
            this.stream.writeInt(obj.intValue());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendInt32(Short obj)
    {
        this.appendShort(0);
        this.appendShort(obj);
    }

    public void appendInt32(Byte obj)
    {
        try
        {
            this.stream.writeInt((int) obj);
        }
        catch (IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendInt32(Boolean obj)
    {
        try
        {
            this.stream.writeInt(obj ? 1 : 0);
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendShort(int obj)
    {
        try
        {
            this.stream.writeShort((short) obj);
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendByte(Integer b)
    {
        try
        {
            this.stream.writeByte(b.intValue());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendBoolean(Boolean obj)
    {
        try
        {
            this.stream.writeBoolean(obj.booleanValue());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendDouble(double d)
    {
        try
        {
            this.stream.writeDouble(d);
        }
        catch (IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public void appendDouble(Double obj)
    {
        try
        {
            this.stream.writeDouble(obj.doubleValue());
        }
        catch (IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }
    }

    public ServerMessage appendResponse(ServerMessage obj)
    {
        try
        {
            this.stream.write(obj.get().array());
        }
        catch(IOException e)
        {
            Emulator.getLogging().logPacketError(e);
        }

        return this;
    }

    public void append(ISerialize obj)
    {
        obj.serialize(this);
    }

    public String getBodyString()
    {
        ByteBuf buffer = this.stream.buffer().duplicate();

        buffer.setInt(0, buffer.writerIndex() - 4);

        String consoleText = buffer.toString(Charset.forName("UTF-8"));

        for (int i = 0; i < 14; i++) {
            consoleText = consoleText.replace(Character.toString((char)i), "[" + i + "]");
        }

        buffer.discardSomeReadBytes();

        return consoleText;
    }

    public int getHeader()
    {
        return this.header;
    }

    public ByteBuf get()
    {
        this.channelBuffer.setInt(0, this.channelBuffer.writerIndex() - 4);

        return this.channelBuffer.copy();
    }
}