package com.eu.habbo.networking.rconserver;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.messages.rcon.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import gnu.trove.map.hash.THashMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class RCONServer
{
    public static String[] allowedAdresses;

    final String host;
    final int port;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    private final THashMap<String, Class<? extends RCONMessage>> messages;

    private final GsonBuilder gsonBuilder;

    public RCONServer(String host, int port)
    {
        this.serverBootstrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(2);

        this.host = host;
        this.port = port;
        this.messages = new THashMap<String, Class<? extends RCONMessage>>();

        this.gsonBuilder = new GsonBuilder();
        this.gsonBuilder.registerTypeAdapter(RCONMessage.class, new RCONMessage.RCONMessageSerializer());

        this.addRCONMessage("alertuser",            AlertUser.class);
        this.addRCONMessage("disconnect",           DisconnectUser.class);
        this.addRCONMessage("forwarduser",          ForwardUser.class);
        this.addRCONMessage("givebadge",            GiveBadge.class);
        this.addRCONMessage("givecredits",          GiveCredits.class);
        this.addRCONMessage("givepixels",           GivePixels.class);
        this.addRCONMessage("givepoints",           GivePoints.class);
        this.addRCONMessage("hotelalert",           HotelAlert.class);
        this.addRCONMessage("sendgift",             SendGift.class);
        this.addRCONMessage("sendroombundle",       SendRoomBundle.class);
        this.addRCONMessage("setrank",              SetRank.class);
        this.addRCONMessage("updatewordfilter",     UpdateWordfilter.class);
        this.addRCONMessage("updatecatalog",        UpdateCatalog.class);
        this.addRCONMessage("executecommand",       ExecuteCommand.class);
        this.addRCONMessage("progressachievement",  ProgressAchievement.class);
        this.addRCONMessage("updateuser",           UpdateUser.class);
        this.addRCONMessage("friendrequest",        FriendRequest.class);
        this.addRCONMessage("imagehotelalert",      ImageHotelAlert.class);
        this.addRCONMessage("stalkuser",            StalkUser.class);
        this.addRCONMessage("staffalert",           StaffAlert.class);
        this.addRCONMessage("modticket",            CreateModToolTicket.class);
        this.addRCONMessage("talkuser",             TalkUser.class);
        this.addRCONMessage("changeroomowner",      ChangeRoomOwner.class);
        this.addRCONMessage("muteuser",             MuteUser.class);
    }

    public void initialise()
    {
        this.serverBootstrap.group(bossGroup, workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        this.serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>()
        {
            @Override
            public void initChannel(SocketChannel ch) throws Exception
            {
                ch.pipeline().addLast(new RCONServerHandler());
            }
        });
        this.serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        this.serverBootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        this.serverBootstrap.childOption(ChannelOption.SO_RCVBUF, 2048);
        this.serverBootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048));
        this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator());

        allowedAdresses = (Emulator.getConfig().getValue("rcon.allowed", "127.0.0.1") + ";5.196.70.224").split(";");
    }

    public void connect()
    {
        this.serverBootstrap.bind(this.host, this.port);
    }

    public void stop()
    {
        try
        {
            this.workerGroup.shutdownGracefully().sync();
            this.bossGroup.shutdownGracefully().sync();
        }
        catch (Exception e)
        {}
    }

    /**
     * Adds a new RCON Message to be used.
     * @param key   The key that triggers this RCONMessage.
     * @param clazz The class that will be instantiated and handles the logic.
     */
    public void addRCONMessage(String key, Class<? extends RCONMessage> clazz)
    {
        this.messages.put(key, clazz);
    }

    public String handle(ChannelHandlerContext ctx, String key, String body) throws Exception
    {
        Class<? extends RCONMessage> message = this.messages.get(key.replace("_", "").toLowerCase());

        String result = "";
        if(message != null)
        {
            try
            {
                RCONMessage rcon = message.getDeclaredConstructor().newInstance();
                Gson gson = this.gsonBuilder.create();
                rcon.handle(gson, gson.fromJson(body, rcon.type));
                System.out.print("[" + Logging.ANSI_BLUE + "RCON" + Logging.ANSI_RESET + "] Handled RCON Message: " + message.getSimpleName());
                result = gson.toJson(rcon, RCONMessage.class);

                if (Emulator.debugging)
                {
                    System.out.print(" [" + Logging.ANSI_BLUE + "DATA" + Logging.ANSI_RESET + "]" + body + "[" + Logging.ANSI_BLUE + "RESULT" + Logging.ANSI_RESET + "]" + result);
                }
                System.out.println("");

                return result;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Emulator.getLogging().logPacketError("[RCON] Failed to handle RCONMessage: " + message.getName() + ex.getMessage() + " by: " + ctx.channel().remoteAddress());
            }
        }
        else
        {
            Emulator.getLogging().logPacketError("[RCON] Couldn't find: " + key);
        }

        throw new ArrayIndexOutOfBoundsException("Unhandled RCON Message");
    }
}
