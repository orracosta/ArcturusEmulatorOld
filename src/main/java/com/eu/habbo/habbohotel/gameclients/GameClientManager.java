package com.eu.habbo.habbohotel.gameclients;

import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;

import io.netty.channel.*;
import io.netty.util.AttributeKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

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
        ctx.channel().closeFuture().addListener(new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception
            {
                disposeClient(ctx.channel());
            }
        });

        ctx.attr(CLIENT).set(client);
        ctx.fireChannelRegistered();

        return this.clients.putIfAbsent(ctx.channel().id(), client) == null;
    }
    
    public void disposeClient(Channel channel)
    {
        GameClient client = this.getClient(channel);

        if (client != null)
        {
            client.dispose();
        }

        channel.attr(CLIENT).set(null);
        channel.deregister();
        channel.closeFuture();
        channel.close();
        this.clients.remove(channel.id());
    }
    
    public boolean containsHabbo(Integer id)
    {
        if (!this.clients.isEmpty())
        {
            for (GameClient client : this.clients.values())
            {
                if (client.getHabbo() != null)
                {
                    if (client.getHabbo().getHabboInfo() != null)
                    {
                        if (client.getHabbo().getHabboInfo().getId() == id)
                            return true;
                    }
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
                continue;

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

            if(client.getHabbo().getHabboInfo().getUsername().equalsIgnoreCase(username))
                return client.getHabbo();
        }

        return null;
    }

    public List<Habbo> getHabbosWithIP(String ip)
    {
        List<Habbo> habbos = new ArrayList<Habbo>();

        for (GameClient client : this.clients.values())
        {
            if (client.getHabbo() != null && client.getHabbo().getHabboInfo() != null)
            {
                if (client.getHabbo().getHabboInfo().getIpLogin().equalsIgnoreCase(ip))
                {
                    habbos.add(client.getHabbo());
                }
            }
        }

        return habbos;
    }

    public List<Habbo> getHabbosWithMachineId(String machineId)
    {
        List<Habbo> habbos = new ArrayList<Habbo>();

        for (GameClient client : this.clients.values())
        {
            if (client.getHabbo() != null && client.getHabbo().getHabboInfo() != null && client.getMachineId().equalsIgnoreCase(machineId))
            {
                habbos.add(client.getHabbo());
            }
        }

        return habbos;
    }

    public void sendBroadcastResponse(MessageComposer composer)
    {
        sendBroadcastResponse(composer.compose());
    }

    public void sendBroadcastResponse(ServerMessage msg)
    {
        for (GameClient client : this.clients.values()) {
            client.sendResponse(msg);
        }
    }

    public void sendBroadcastResponse(ServerMessage msg, GameClient exclude)
    {
        for (GameClient client : this.clients.values()) {
            if(client.equals(exclude))
                continue;

            client.sendResponse(msg);
        }
    }

    public void sendBroadcastResponse(ServerMessage message, String minPermission, GameClient exclude)
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
}