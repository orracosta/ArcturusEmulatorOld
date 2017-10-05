package com.habboproject.server.game.rooms.objects.entities.types.ai.pets;

import com.habboproject.server.game.pets.commands.PetCommandManager;
import com.habboproject.server.game.pets.data.PetMessageType;
import com.habboproject.server.game.pets.data.PetSpeech;
import com.habboproject.server.game.pets.races.BreedingType;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.AbstractBotAI;
import com.habboproject.server.game.rooms.objects.entities.types.ai.PetAction;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetBreedingBoxFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetToyFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.outgoing.room.pets.AddExperiencePointsMessageComposer;
import com.habboproject.server.utilities.RandomInteger;

public class PetAI extends AbstractBotAI {
    private static final PetAction[] possibleActions = {
            PetAction.LAY, PetAction.SIT, PetAction.TALK, PetAction.PLAY
    };

    private String ownerName = "";

    private int playTimer = 0;
    private int gestureTimer = 0;
    private int interactionTimer = 0;
    private int waitTimer = 0;

    private PetToyFloorItem toyItem;

    public PetAI(RoomEntity entity) {
        super(entity);

        PlayerAvatar playerAvatar = PlayerManager.getInstance().getAvatarByPlayerId(this.getPetEntity().getData().getOwnerId(), PlayerAvatar.USERNAME_FIGURE);

        if (playerAvatar != null) {
            this.ownerName = playerAvatar.getUsername();
        }

        this.setTicksUntilCompleteInSeconds(25);
    }

    @Override
    public boolean onPlayerEnter(PlayerEntity entity) {
        if(this.getPetEntity().getData() == null) {
            return false;
        }

        if (entity.getPlayerId() == this.getPetEntity().getData().getOwnerId()) {
            this.onAddedToRoom();
        }

        return false;
    }

    @Override
    public boolean onAddedToRoom() {
        this.say(this.getMessage(PetMessageType.WELCOME_HOME));

        int playerId = this.getPetEntity().getData().getOwnerId();
        PlayerEntity playerEntity = this.getEntity().getRoom().getEntities().getEntityByPlayerId(playerId);

        if (playerEntity != null) {
            Position position = playerEntity.getPosition().squareInFront(playerEntity.getBodyRotation());

            RoomTile tile = this.getPetEntity().getRoom().getMapping().getTile(position.getX(), position.getY());

            if (tile != null) {
                this.moveTo(position);

                tile.scheduleEvent(this.getPetEntity().getId(), (entity) -> {
                    entity.lookTo(playerEntity.getPosition().getX(), playerEntity.getPosition().getY());
                });
            }
        }

        return false;
    }

    @Override
    public void onTickComplete() {
        if (this.playTimer != 0 || this.waitTimer != 0 || this.getPetEntity().getData().isSaddled()) {
            return;
        }

        PetAction petAction = possibleActions[RandomInteger.getRandom(0, possibleActions.length - 1)];

        switch (petAction) {
            case TALK:
                this.say(this.getMessage(PetMessageType.GENERIC));
                break;

            case LAY:
                this.lay();
                break;

            case SIT:
                this.sit();
                break;

            case PLAY:
                this.play();
                break;
        }

        this.setTicksUntilCompleteInSeconds(25);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (this.playTimer != 0) {
            this.playTimer--;

            if (this.playTimer == 0) {
                if(this.toyItem != null) {
                    this.toyItem.onEntityStepOff(this.getPetEntity());
                }

                this.getPetEntity().removeStatus(RoomEntityStatus.PLAY);
                this.getPetEntity().markNeedsUpdate();
            }
        }

        if (this.gestureTimer != 0) {
            this.gestureTimer--;

            if (this.gestureTimer == 0) {
                this.getPetEntity().removeStatus(RoomEntityStatus.GESTURE);
                this.getPetEntity().markNeedsUpdate();
            }
        }

        if (this.interactionTimer != 0) {
            this.interactionTimer--;

            if(this.interactionTimer == 0) {
                if (this.getPetEntity().hasStatus(RoomEntityStatus.PLAY_DEAD)) {
                    this.getPetEntity().removeStatus(RoomEntityStatus.PLAY_DEAD);
                    this.getPetEntity().markNeedsUpdate();
                }
            }
        }

        if (this.waitTimer != 0) {
            this.waitTimer--;
        }
    }

    @Override
    public boolean onTalk(PlayerEntity entity, String message) {
        if (message.startsWith(this.getPetEntity().getData().getName())) {
            String commandKey = message.replace(this.getPetEntity().getData().getName() + " ", "");

            if (PetCommandManager.getInstance().executeCommand(commandKey.toLowerCase(), entity, this.getPetEntity())) {
                // drain energy.
                this.interactionTimer += 25;
            }
        }

        return false;
    }

    public void applyGesture(String gestureType) {
        this.gestureTimer = 15;

        this.getPetEntity().addStatus(RoomEntityStatus.GESTURE, gestureType);
        this.getPetEntity().markNeedsUpdate();
    }

    public void waitForInteraction() {
        this.waitTimer = 20;
    }

    public void possibleBreeding() {
        this.waitTimer = 40;
    }

    public void stay() {
        this.interactionTimer = RandomInteger.getRandom(40, 80);
    }

    public void onScratched() {
        this.waitTimer = 0;

        this.say(this.getMessage(PetMessageType.SCRATCHED), ChatEmotion.SMILE);

        this.getPetEntity().cancelWalk();
        this.applyGesture("sml");

        this.increaseExperience(10);

        this.getPetEntity().getData().incrementScratches();
    }

    public void increaseExperience(int amount) {
        this.getPetEntity().getData().increaseExperience(amount);
        this.getEntity().getRoom().getEntities().broadcastMessage(new AddExperiencePointsMessageComposer(this.getPetEntity().getData().getId(), this.getPetEntity().getId(), amount));

        // level up
    }

    public void breed() {
        Room room;
        if ((room = this.getPetEntity().getRoom()) == null) return;

        BreedingType type;
        if ((type = BreedingType.getType(this.getPetEntity().getData().getTypeId())) == null) return;

        PetBreedingBoxFloorItem floorItem = (PetBreedingBoxFloorItem) room.getItems().getRandomBreedingBox(type);
        if (floorItem == null) return;

        //floorItem.getEntities().add(this.getPetEntity());
        //this.getPetEntity().setBreedingBox(floorItem);

        this.clearPetStatuses();

        this.getPetEntity().setOverriden(true);
        this.getPetEntity().moveTo(floorItem.getPosition());
    }

    public void sit() {
        this.getEntity().addStatus(RoomEntityStatus.SIT, "");// + this.getPetEntity().getRoom().getModel().getSquareHeight()[this.getEntity().getPosition().getX()][this.getEntity().getPosition().getY()]);
        this.getEntity().markNeedsUpdate();
    }

    public void lay() {
        this.getEntity().addStatus(RoomEntityStatus.LAY, "");// + this.getPetEntity().getRoom().getModel().getSquareHeight()[this.getEntity().getPosition().getX()][this.getEntity().getPosition().getY()]);
        this.getEntity().markNeedsUpdate();
    }

    public void free() {
        this.interactionTimer = 0;
        this.playTimer = 0;

        this.clearPetStatuses();
        this.walkNow();

        this.getPetEntity().markNeedsUpdate();

        if(this.followingPlayer != null) {
            this.followingPlayer.getFollowingEntities().remove(this.getPetEntity());
            this.followingPlayer = null;
        }

        if (this.toyItem != null) {
            this.toyItem.onEntityStepOff(this.getPetEntity());
        }
    }

    private void clearPetStatuses() {
        if (this.getPetEntity().hasStatus(RoomEntityStatus.LAY)) {
            this.getPetEntity().removeStatus(RoomEntityStatus.LAY);
        }

        if (this.getPetEntity().hasStatus(RoomEntityStatus.SIT)) {
            this.getPetEntity().removeStatus(RoomEntityStatus.SIT);
        }


        if (this.getPetEntity().hasStatus(RoomEntityStatus.PLAY_DEAD)) {
            this.getPetEntity().removeStatus(RoomEntityStatus.PLAY_DEAD);
        }

        if (this.getPetEntity().hasStatus(RoomEntityStatus.PLAY)) {
            this.getPetEntity().removeStatus(RoomEntityStatus.PLAY);
        }
    }

    public void play() {
        // Find item
        for (RoomItemFloor floorItem : this.getPetEntity().getRoom().getItems().getByClass(PetToyFloorItem.class)) {
            this.toyItem = (PetToyFloorItem) floorItem;

            // 1 min play timer.
            this.playTimer = RandomInteger.getRandom(10, 50);

            this.moveTo(floorItem.getPosition());

            return;
        }
    }

    public void playDead() {
        this.getPetEntity().cancelWalk();

        this.clearPetStatuses();

        this.getPetEntity().addStatus(RoomEntityStatus.PLAY_DEAD, "");
        this.getPetEntity().markNeedsUpdate();
    }

    private PetSpeech getPetSpeech() {
        return this.getPetEntity().getData().getSpeech();
    }

    private String getMessage(PetMessageType type) {
        if (this.getPetSpeech() == null) {
            return null;
        }

        String message = this.getPetSpeech().getMessageByType(type);

        if (message.contains("%ownerName%")) {
            if (this.ownerName.isEmpty()) {
                PlayerAvatar playerAvatar = PlayerManager.getInstance().getAvatarByPlayerId(this.getPetEntity().getData().getOwnerId(), PlayerAvatar.USERNAME_FIGURE);

                if (playerAvatar != null) {
                    this.ownerName = playerAvatar.getUsername();
                }
            }

            message = message.replace("%ownerName%", this.ownerName);
        }

        return message;
    }

    @Override
    public boolean canMove() {
        return this.waitTimer == 0 && this.playTimer == 0 && this.interactionTimer == 0 & this.getPetEntity().getMountedEntity() == null;
    }

    public void setFollowingPlayer(PlayerEntity followingPlayer) {
        if(followingPlayer == null && this.followingPlayer != null) {
            this.followingPlayer.getFollowingEntities().remove(this.getPetEntity());
        }

        this.followingPlayer = followingPlayer;
    }
}
