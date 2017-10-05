package com.habboproject.server.network.messages.outgoing.group;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.groups.types.GroupMember;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.headers.Composers;

import java.util.ArrayList;
import java.util.List;


public class GroupMembersMessageComposer extends MessageComposer {
    private static final int MEMBERS_PER_PAGE = 14;

    private final GroupData group;
    private final int page;
    private final List<Object> groupMembers;
    private final int requestType;
    private final String searchQuery;
    private final boolean isAdmin;

    public GroupMembersMessageComposer(final GroupData group, final int page, final List<Object> groupMembers, final int requestType, final String searchQuery, final boolean isAdmin) {
        this.group = group;
        this.page = page;
        this.groupMembers = groupMembers;
        this.requestType = requestType;
        this.searchQuery = searchQuery;
        this.isAdmin = isAdmin;
    }

    @Override
    public short getId() {
        return Composers.GroupMembersMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(group.getId());
        msg.writeString(group.getTitle());
        msg.writeInt(group.getRoomId());
        msg.writeString(group.getBadge());

        msg.writeInt(groupMembers.size());

        if (groupMembers.size() == 0) {
            msg.writeInt(0);
        } else {
            List<List<Object>> paginatedMembers = paginateMembers(groupMembers, MEMBERS_PER_PAGE);

            msg.writeInt(paginatedMembers.get(page).size());

            for (Object memberObject : paginatedMembers.get(page)) {
                int playerId;
                int joinDate = 0;

                if (memberObject instanceof Integer) {
                    playerId = (int) memberObject;

                    if (requestType == 1) {
                        msg.writeInt(playerId == group.getOwnerId() ? 0 : 1);
                    } else {
                        msg.writeInt(3);
                    }
                } else {
                    playerId = ((GroupMember) memberObject).getPlayerId();

                    if (((GroupMember) memberObject).getAccessLevel().isAdmin()) {
                        msg.writeInt(group.getOwnerId() == ((GroupMember) memberObject).getPlayerId() ? 0 : 1);
                    } else {
                        msg.writeInt(2);
                    }

                    joinDate = ((GroupMember) memberObject).getDateJoined();
                }

                PlayerAvatar playerAvatar = null;

                if (PlayerManager.getInstance().isOnline(playerId)) {
                    Session session = NetworkManager.getInstance().getSessions().getByPlayerId(playerId);

                    if (session != null && session.getPlayer() != null && session.getPlayer().getData() != null) {
                        playerAvatar = session.getPlayer().getData();
                    }
                }

                if (playerAvatar == null) {
                    playerAvatar = PlayerManager.getInstance().getAvatarByPlayerId(playerId, PlayerAvatar.USERNAME_FIGURE);
                }

                if (playerAvatar != null) {
                    msg.writeInt(playerId);
                    msg.writeString(playerAvatar.getUsername());
                    msg.writeString(playerAvatar.getFigure());
                } else {
                    msg.writeInt(playerId);
                    msg.writeString("Unknown Player");
                    msg.writeString("");
                }

                msg.writeString(joinDate != 0 ? GroupInformationMessageComposer.getDate(joinDate) : "");
            }
        }

        msg.writeBoolean(isAdmin);
        msg.writeInt(MEMBERS_PER_PAGE);
        msg.writeInt(page);

        msg.writeInt(requestType);
        msg.writeString(searchQuery);
    }

    private List<List<Object>> paginateMembers(List<Object> originalList, int chunkSize) {
        List<List<Object>> listOfChunks = new ArrayList<>();

        for (int i = 0; i < originalList.size() / chunkSize; i++) {
            listOfChunks.add(originalList.subList(i * chunkSize, i * chunkSize + chunkSize));
        }

        if (originalList.size() % chunkSize != 0) {
            listOfChunks.add(originalList.subList(originalList.size() - originalList.size() % chunkSize, originalList.size()));
        }

        return listOfChunks;
    }
}
