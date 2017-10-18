package com.eu.habbo.messages;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientMessage
{
    private final int header;
    private final ByteBuf buffer;

    public ByteBuf getBuffer()
    {
        return this.buffer;
    }

    public int getMessageId()
    {
        return this.header;
    }

    public ClientMessage(int messageId, ByteBuf buffer)
    {
        this.header = messageId;
        this.buffer = ((buffer == null) || (buffer.readableBytes() == 0) ? Unpooled.EMPTY_BUFFER : buffer);
    }

    public ClientMessage clone() throws CloneNotSupportedException
    {
        return new ClientMessage(this.header, this.buffer.duplicate());
    }

    public int readShort()
    {
        try
        {
            return this.buffer.readShort();
        }
        catch (Exception e)
        {
        }

        return 0;
    }

    public Integer readInt()
    {
        try
        {
            return this.buffer.readInt();
        }
        catch (Exception e)
        {
        }

        return 0;
    }

    public boolean readBoolean()
    {
        try
        {
            return this.buffer.readByte() == 1;
        }
        catch (Exception e)
        {
        }

        return false;
    }

    public String readString()
    {
        try
        {
            int length = readShort();
            byte[] data = new byte[length];
            this.buffer.readBytes(data);
            String s = new String(data);
            return s;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public String getMessageBody()
    {
        String consoleText = this.buffer.toString(Charset.defaultCharset());

        for (int i = -1; i < 31; i++) {
            consoleText = consoleText.replace(Character.toString((char)i), "[" + i + "]");
        }

        return consoleText;
    }

    public int bytesAvailable()
    {
        return this.buffer.readableBytes();
    }

}