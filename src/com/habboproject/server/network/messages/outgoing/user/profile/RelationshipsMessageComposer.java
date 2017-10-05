package com.habboproject.server.network.messages.outgoing.user.profile;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.components.RelationshipComponent;
import com.habboproject.server.game.players.components.types.messenger.RelationshipLevel;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class RelationshipsMessageComposer extends MessageComposer {
    private final int playerId;
    private final Map<Integer, RelationshipLevel> relationships;

    public RelationshipsMessageComposer(final int playerId, final Map<Integer, RelationshipLevel> relationships) {
        this.playerId = playerId;
        this.relationships = relationships;
    }

    public RelationshipsMessageComposer(final int playerId) {
        this.playerId = playerId;
        this.relationships = null;
    }

    @Override
    public short getId() {
        return Composers.GetRelationshipsMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(playerId);

        if (relationships == null || relationships.size() == 0) {
            msg.writeInt(0);
            return;
        }

        msg.writeInt(relationships.size());

        int hearts = RelationshipComponent.countByLevel(RelationshipLevel.HEART, relationships);
        int smiles = RelationshipComponent.countByLevel(RelationshipLevel.SMILE, relationships);
        int bobbas = RelationshipComponent.countByLevel(RelationshipLevel.BOBBA, relationships);

        List<Integer> relationshipKeys = Lists.newArrayList(relationships.keySet());
        Collections.shuffle(relationshipKeys);

        for (Integer relationshipKey : relationshipKeys) {
            RelationshipLevel level = relationships.get(relationshipKey);

            PlayerAvatar data = PlayerManager.getInstance().getAvatarByPlayerId(relationshipKey, PlayerAvatar.USERNAME_FIGURE);

            if (data == null) {
                msg.writeInt(0);
                msg.writeInt(0);
                msg.writeInt(0); // id
                msg.writeString("Username");
                msg.writeString("hr-115-42.hd-190-1.ch-215-62.lg-285-91.sh-290-62");
            } else {
                msg.writeInt(level.getLevelId());
                msg.writeInt(level == RelationshipLevel.HEART ? hearts : level == RelationshipLevel.SMILE ? smiles : bobbas);
                msg.writeInt(data.getId());
                msg.writeString(data.getUsername());
                msg.writeString(data.getFigure());
            }

        }
    }
}
