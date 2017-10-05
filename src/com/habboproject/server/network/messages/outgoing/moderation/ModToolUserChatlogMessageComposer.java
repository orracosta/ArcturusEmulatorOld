package com.habboproject.server.network.messages.outgoing.moderation;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.moderation.chatlog.UserChatlogContainer;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.logging.entries.RoomChatLogEntry;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.habboproject.server.storage.queries.player.PlayerDao;


public class ModToolUserChatlogMessageComposer extends MessageComposer {
    private final static String ROOM_ID = "roomId";
    private final static String ROOM_NAME = "roomName";
    private final static String ROOM_NAME_UNKNOWN = "Unknown Room";

    private final int playerId;
    private final UserChatlogContainer userChatlogContainer;

    public ModToolUserChatlogMessageComposer(final int playerId, final UserChatlogContainer userChatlogContainer) {
        this.playerId = playerId;
        this.userChatlogContainer = userChatlogContainer;
    }

    @Override
    public short getId() {
        return Composers.ModeratorUserChatlogMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        String username = PlayerDao.getUsernameByPlayerId(this.playerId);

        msg.writeInt(this.playerId);
        msg.writeString(username);
        msg.writeInt(this.userChatlogContainer.size());

        for (UserChatlogContainer.LogSet logSet : this.userChatlogContainer.getLogs()) {
            RoomData roomData = RoomManager.getInstance().getRoomData(logSet.getRoomId());
            msg.writeByte(1);
            msg.writeShort(2);
            msg.writeString(ROOM_NAME);
            msg.writeByte(2); // type of following data (string = 2)
            msg.writeString(roomData == null ? ROOM_NAME_UNKNOWN : roomData.getName());
            msg.writeString(ROOM_ID);
            msg.writeByte(1); //type of following data i guess (int = 1)
            msg.writeInt(roomData == null ? 0 : roomData.getId());

            msg.writeShort(logSet.getLogs().size());

            for (RoomChatLogEntry chatLogEntry : logSet.getLogs()) {
                chatLogEntry.compose(msg);
            }
        }
    }
}
