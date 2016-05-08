package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GameClientManager
{
    private final ConcurrentMap<ChannelId, GameClient> clients;

    public static final AttributeKey<GameClient> CLIENT = AttributeKey.valueOf("GameClient");
    
    public GameClientManager()
    {
        this.clients = new ConcurrentHashMap<ChannelId, GameClient>();
    }
    
    public ConcurrentMap<ChannelId, GameClient> getSessions()
    {
        return this.clients;
    }
    
    public boolean containsClient(Channel channel)
    {
        return this.clients.containsKey(channel.id());
    }
    
    public GameClient getClient(Channel channel)
    {
        if (this.clients.containsKey(channel.id())) {
            return this.clients.get(channel.id());
        }
        return null;
    }

    public GameClient getClient(Habbo habbo)
    {
        for(GameClient client : this.clients.values())
        {
            if(client.getHabbo() == habbo)
                return client;
        }

        return null;
    }
    
    public boolean addClient(ChannelHandlerContext ctx)
    {
        GameClient client = new GameClient(ctx.channel());

        ctx.attr(CLIENT).set(client);
        ctx.fireChannelRegistered();

        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }
    
    public void disposeClient(Channel channel)
    {
        GameClient client = this.getClient(channel);

        if (client != null) {
            client.dispose();
            client.setHabbo(null);
        }
        this.clients.remove(channel.id());
    }
    
    public boolean containsHabbo(Integer id)
    {
        for (GameClient client : this.clients.values())
        {
            if(client.getHabbo() != null)
            {
                if(client.getHabbo().getHabboInfo() != null)
                {
                    if (client.getHabbo().getHabboInfo().getId() == id)
                        return true;
                }
            }
        }
        return false;
    }

    public Habbo getHabbo(int id)
    {
        for(GameClient client : this.clients.values())
        {
            if(client.getHabbo() == null)
            {
                client.dispose();
                continue;
            }

            if(client.getHabbo().getHabboInfo().getId() == id)
                return client.getHabbo();
        }

        return null;
    }

    public Habbo getHabbo(String username)
    {
        for(GameClient client : this.clients.values())
        {
            if(client.getHabbo() == null)
                continue;

            if(client.getHabbo().getHabboInfo().getUsername().equals(username))
                return client.getHabbo();
        }

        return null;
    }

    public void sendBroadcastResponse(MessageComposer composer)
    {
        sendBroadcastResponse(composer.compose());
    }

    void sendBroadcastResponse(ServerMessage msg)
    {
        for (GameClient client : this.clients.values()) {
            client.sendResponse(msg);
        }
    }

    public synchronized void sendBroadcastResponse(ServerMessage msg, GameClient exclude)
    {
        for (GameClient client : this.clients.values()) {
            if(client.equals(exclude))
                continue;

            client.sendResponse(msg);
        }
    }
    
    public synchronized void sendBroadcastResponse(MessageComposer composer, String minPermission)
    {
        sendBroadcastResponse(composer.compose(), minPermission);
    }

    public synchronized void sendBroadcastResponse(ServerMessage message, String minPermission, GameClient exclude)
    {
        for (GameClient client : this.clients.values())
        {
            if(client.equals(exclude))
                continue;

            if(client.getHabbo() != null)
            {
                if (client.getHabbo().hasPermission(minPermission))
                {
                    client.sendResponse(message);
                }
            }
        }
    }
    
    void sendBroadcastResponse(ServerMessage msg, String minPermission)
    {
        for (GameClient client : this.clients.values())
        {

        }
    }

    public void sendRoomResponse(Room room, MessageComposer message)
    {
        for(Habbo habbo : room.getCurrentHabbos().valueCollection())
        {
            habbo.getClient().sendResponse(message);
        }
    }
}