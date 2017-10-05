package com.habboproject.server.network.messages.outgoing.room.pets;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class PetInformationMessageComposer extends MessageComposer {

    private final PetEntity petEntity;
    private final PlayerEntity player;

    public PetInformationMessageComposer(final PetEntity petEntity) {
        this.petEntity = petEntity;
        this.player = null;
    }

    public PetInformationMessageComposer(final PlayerEntity playerEntity) {
        this.petEntity = null;
        this.player = playerEntity;
    }

    @Override
    public short getId() {
        return Composers.PetInformationMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        if (this.petEntity != null && this.petEntity.getData() != null) {
            final PlayerAvatar owner = PlayerManager.getInstance().getAvatarByPlayerId(this.petEntity.getData().getOwnerId(), PlayerAvatar.USERNAME_FIGURE);

            msg.writeInt(this.petEntity.getData().getId());
            msg.writeString(this.petEntity.getData().getName());
            msg.writeInt(this.petEntity.getData().getLevel());
            msg.writeInt(20); // MAX_LEVEL
            msg.writeInt(this.petEntity.getData().getExperience());
            msg.writeInt(200); // EXPERIENCE_GOAL
            msg.writeInt(this.petEntity.getData().getEnergy());
            msg.writeInt(100); // MAX_ENERGY
            msg.writeInt(100); // NUTRITION
            msg.writeInt(100); // MAX_NUTRITION
            msg.writeInt(this.petEntity.getData().getScratches()); // SCRATCHES
            msg.writeInt(this.petEntity.getData().getOwnerId());
            msg.writeInt(this.petEntity.getData().getAge() == 17443 ? 0 : this.petEntity.getData().getAge());
            msg.writeString(owner == null ? "trylix" : owner.getUsername());
            msg.writeInt(0);
            msg.writeBoolean(this.petEntity.getData().isSaddled()); // HAS_SADDLE
            msg.writeBoolean(this.petEntity.hasMount()); // HAS_RIDER
            msg.writeInt(0);
            msg.writeInt(this.petEntity.getData().isAnyRider() ? 1 : 0); // yes = 1 no = 0
            msg.writeBoolean(false);
            msg.writeBoolean(true);
            msg.writeBoolean(false);
            msg.writeInt(0);
            msg.writeInt(129600);
            msg.writeInt(128000);
            msg.writeInt(1000);
            msg.writeBoolean(true);
        } else {
            msg.writeInt(this.player.getPlayerId());
            msg.writeString(this.player.getUsername());
            msg.writeInt(20);
            msg.writeInt(20); // MAX_LEVEL
            msg.writeInt(0);
            msg.writeInt(200); // EXPERIENCE_GOAL
            msg.writeInt(0);
            msg.writeInt(100); // MAX_ENERGY
            msg.writeInt(100); // NUTRITION
            msg.writeInt(100); // MAX_NUTRITION
            msg.writeInt(0); // SCRATCHES
            msg.writeInt(this.player.getPlayerId());
            msg.writeInt(0); // AGE
            msg.writeString(this.player.getUsername());
            msg.writeInt(1);
            msg.writeBoolean(this.player.getMotto().toLowerCase().startsWith("rideable")); // HAS_SADDLE
            msg.writeBoolean(false); // HAS_RIDER
            msg.writeInt(0);

            // CAN ANYONE MOUNT?
            msg.writeInt(1); // yes = 1 no = 0

            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(false);
            msg.writeBoolean(true); // all can mount
            msg.writeInt(0);
            msg.writeString("");
            msg.writeBoolean(false);
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeBoolean(false);

        }
    }
}
