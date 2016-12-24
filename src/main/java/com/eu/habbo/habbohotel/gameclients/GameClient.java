package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class GameClient
{
    /**
     * The Channel this client is using.
     */
    private final Channel channel;

    /**
     * The Habbo it is linked to.
     */
    private Habbo habbo;

    /**
     * The MAC Address of the connected client.
     */
    private String machineId = "";

    public GameClient(Channel channel)
    {
        this.channel = channel;
    }

    /**
     * Sends an composer to the client.
     * @param composer The composer to send.
     */
    public void sendResponse(MessageComposer composer)
    {
        if(this.channel.isOpen())
        {
            try
            {
                ServerMessage msg = composer.compose();
                sendResponse(msg);
            } catch (Exception e)
            {
                Emulator.getLogging().logPacketError(e);
            }
        }
    }

    /**
     * Sends an response to the client.
     * @param response The response to send.
     */
    public void sendResponse(ServerMessage response)
    {
        if(this.channel.isOpen())
        {
            if (response == null || response.getHeader() <= 0)
            {
                return;
            }

            if (Emulator.getConfig().getBoolean("debug.show.packets"))
                Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + response.getHeader() + "] -> " + response.getBodyString());

            this.channel.write(response.get(), this.channel.voidPromise());
            this.channel.flush();
        }
    }

    /**
     * Sends multiple responses to the client.
     * @param responses The responses to send.
     */
    public void sendResponses(ArrayList<ServerMessage> responses)
    {
        ByteBuf buffer = Unpooled.buffer();

        if(this.channel.isOpen())
        {
            for(ServerMessage response : responses)
            {
                if (response == null || response.getHeader() <= 0)
                {
                    return;
                }

                if (Emulator.getConfig().getBoolean("debug.show.packets"))
                    Emulator.getLogging().logPacketLine("[" + Logging.ANSI_PURPLE + "SERVER" + Logging.ANSI_RESET + "] => [" + response.getHeader() + "] -> " + response.getBodyString());

                buffer.writeBytes(response.get());
            }
            this.channel.write(buffer.copy(), this.channel.voidPromise());
            this.channel.flush();
        }
        buffer.release();
    }

    /**
     * Disposes the client. Disconnection mostly.
     */
    public void dispose()
    {
        try
        {
            this.channel.close();

            if(this.habbo != null)
            {
                if(this.habbo.isOnline())
                    this.habbo.disconnect();

                this.habbo = null;
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    public Channel getChannel()
    {
        return this.channel;
    }

    public Habbo getHabbo()
    {
        return this.habbo;
    }

    public void setHabbo(Habbo habbo)
    {
        this.habbo = habbo;
    }

    public String getMachineId()
    {
        return this.machineId;
    }

    public void setMachineId(String machineId)
    {
        if (machineId == null)
        {
            throw new NullPointerException("Cannot set machineID to NULL");
        }
        this.machineId = machineId;
    }
}