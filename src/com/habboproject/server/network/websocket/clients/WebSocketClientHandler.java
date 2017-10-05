package com.habboproject.server.network.websocket.clients;

import com.google.gson.JsonObject;
import com.habboproject.server.network.websocket.WebSocketManager;
import com.habboproject.server.utilities.JsonFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by brend on 28/02/2017.
 */
public class WebSocketClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        String address = ctx.channel().remoteAddress().toString().split(":")[0].replace("/", "");

        for (String allowedAddress : WebSocketManager.getInstance().getAllowedAddress()) {
            if (allowedAddress.equalsIgnoreCase(address)) {
                return;
            }
        }

        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object out) throws Exception {
        ByteBuf in = (ByteBuf) out;

        byte[] data = new byte[in.readableBytes()];
        in.getBytes(0, data);

        String message = new String(data);

        try {
            JsonObject object = JsonFactory.getInstance().fromJson(message, JsonObject.class);
            String command = object.get("command").getAsString();

            WebSocketManager.getInstance().getCommandHandler().handle(command, object.get("data").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctx.channel().close();
        in.release();
    }
}
