package com.habboproject.server.game.commands.vip;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.pets.PetManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class TransformCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String type = "human";

        if (params.length == 1) {
            type = params[0].toLowerCase();
        }

        if (type.equals("human")) {
            client.getPlayer().getEntity().removeAttribute("transformation");
        } else {
            String data = PetManager.getInstance().getTransformationData(type);

            if(data == null || data.isEmpty()) {
                return;
            }

            client.getPlayer().getEntity().setAttribute("transformation", data);
        }

        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(client.getPlayer().getEntity().getId()));
        client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(client.getPlayer().getEntity()));
    }

    @Override
    public String getPermission() {
        return "transform_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.transform.description");
    }

    @Override
    public boolean canDisable() {
        return true;
    }

    public static void composeTransformation(IComposer msg, String[] transformationData, PlayerEntity entity) {
        // TODO: create global composer for entity types maybe
        msg.writeInt(entity.getPlayerId());
        msg.writeString(entity.getUsername());
        msg.writeString(entity.getMotto());
        msg.writeString(transformationData[0]);
        msg.writeInt(entity.getId());

        msg.writeInt(entity.getPosition().getX());
        msg.writeInt(entity.getPosition().getY());
        msg.writeDouble(entity.getPosition().getZ());

        msg.writeInt(0); // 2 = user 4 = bot 0 = pet ??????
        msg.writeInt(2); // 1 = user 2 = pet 3 = bot ??????n

        msg.writeInt(Integer.parseInt(transformationData[1]));
        msg.writeInt(entity.getPlayerId());
        msg.writeString(entity.getUsername());
        msg.writeInt(1);
        msg.writeBoolean(true); // has saddle
        msg.writeBoolean(false); // has rider?

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString("");
    }
}
