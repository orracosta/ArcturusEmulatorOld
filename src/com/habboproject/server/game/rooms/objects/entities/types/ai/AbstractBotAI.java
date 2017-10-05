package com.habboproject.server.game.rooms.objects.entities.types.ai;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityType;
import com.habboproject.server.game.rooms.objects.entities.types.BotEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PetEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerBotReachedAvatar;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerBotReachedFurni;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.game.rooms.types.misc.ChatEmotion;
import com.habboproject.server.network.messages.outgoing.room.avatar.TalkMessageComposer;
import com.habboproject.server.utilities.RandomInteger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractBotAI implements BotAI {
    private static final ExecutorService botPathCalculator = Executors.newFixedThreadPool(2);

    private RoomEntity entity;

    private long ticksUntilComplete = 0;
    private boolean walkNow = false;

    protected PlayerEntity followingPlayer;

    public AbstractBotAI(RoomEntity entity) {
        this.entity = entity;
    }

    @Override
    public void onTick() {
        if (this.ticksUntilComplete != 0) {
            this.ticksUntilComplete--;

            if (this.ticksUntilComplete == 0) {
                this.onTickComplete();
            }
        }

        int chance = RandomInteger.getRandom(1, (this.getEntity().hasStatus(RoomEntityStatus.SIT) || this.getEntity().hasStatus(RoomEntityStatus.LAY)) ? 50 : 20);

        if (!this.getEntity().hasMount()) {
            boolean newStep = true;

            if (this.getEntity() instanceof BotEntity) {
                BotEntity botEntity = ((BotEntity) this.getEntity());

                if(botEntity.getData() == null) {
                    return;
                }

                if (botEntity.getData().getMode().equals("relaxed")) {
                    newStep = false;
                }
            }

            if ((chance < 3 || this.walkNow) && newStep) {
                if(this.walkNow) {
                    this.walkNow = false;
                }

                if (!this.getEntity().isWalking() && this.canMove() && this.followingPlayer == null) {
                    botPathCalculator.submit(() -> {
                        RoomTile reachableTile = this.getEntity().getRoom().getMapping().getRandomReachableTile(this.getEntity());

                        if (reachableTile != null) {
                            this.getEntity().moveTo(reachableTile.getPosition().getX(), reachableTile.getPosition().getY());
                        }
                    });
                }
            }
        }

        if (this.getEntity() instanceof BotEntity) {
            try {
                if (((BotEntity) this.getEntity()).getCycleCount() == ((BotEntity) this.getEntity()).getData().getChatDelay() * 2) {
                    String message = ((BotEntity) this.getEntity()).getData().getRandomMessage();

                    if (message != null && !message.isEmpty()) {
                        ((BotEntity) this.getEntity()).say(message);
                    }

                    ((BotEntity) this.getEntity()).resetCycleCount();
                }

                ((BotEntity) this.getEntity()).incrementCycleCount();

                BotEntity botEntity = (BotEntity)this.getEntity();
                if (botEntity != null && botEntity.getRoom() != null) {
                    if (botEntity.isWalking()) {
                        if (botEntity.getData().getForcedFurniTargetMovement() != 0) {
                            RoomItemFloor furniToReach = this.getEntity().getRoom().getItems().getFloorItem(botEntity.getData().getForcedFurniTargetMovement());
                            if (furniToReach != null) {
                                double distance = this.getEntity().getPosition().distanceTo(furniToReach.getPosition());
                                if (distance >= 0.0 && distance <= 1.0) {
                                    WiredTriggerBotReachedFurni.executeTriggers(botEntity, furniToReach);
                                    botEntity.getData().setForcedFurniTargetMovement(0);
                                }
                            } else {
                                botEntity.getData().setForcedFurniTargetMovement(0);
                            }
                        }

                        if (botEntity.getData().getForcedUserTargetMovement() != 0) {
                            RoomEntity playerToReach = this.getEntity().getRoom().getEntities().getEntity(botEntity.getData().getForcedUserTargetMovement());
                            if (playerToReach != null && playerToReach.getEntityType() == RoomEntityType.PLAYER) {
                                double distance = this.entity.getPosition().distanceTo(playerToReach.getPosition());
                                if (distance >= 0.0 && distance <= 1.0 || distance == 2.0 && (playerToReach.getBodyRotation() == 1 || playerToReach.getBodyRotation() == 3 || playerToReach.getBodyRotation() == 5 || playerToReach.getBodyRotation() == 7)) {
                                    if (botEntity.getData().isCarryingItemToUser()) {
                                        this.getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(botEntity.getId(), Locale.get("bots.chat.giveItemMessage").replace("%username%", playerToReach.getUsername()), RoomManager.getInstance().getEmotions().getEmotion(":)"), 2));

                                        playerToReach.carryItem(botEntity.getHandItem());
                                        botEntity.carryItem(0);

                                        botEntity.setHandItemTimer(0);

                                        botEntity.getData().setCarryingItemToUser(false);
                                        botEntity.getData().setForcedUserTargetMovement(0);
                                    }

                                    WiredTriggerBotReachedAvatar.executeTriggers(botEntity, (PlayerEntity)playerToReach);
                                }
                            } else {
                                botEntity.getData().setForcedUserTargetMovement(0);
                            }
                        }
                    } else if (botEntity.getData().getForcedUserTargetMovement() != 0) {
                        RoomEntity playerToReach = this.getEntity().getRoom().getEntities().getEntity(botEntity.getData().getForcedUserTargetMovement());
                        if (playerToReach != null && playerToReach.getEntityType() == RoomEntityType.PLAYER) {
                            if (playerToReach.getPosition().getX() != this.getEntity().getRoom().getModel().getDoorX() && playerToReach.getPosition().getY() != this.getEntity().getRoom().getModel().getDoorY()) {
                                botEntity.moveTo(playerToReach.getPosition().getX(), playerToReach.getPosition().getY());
                            }
                        } else {
                            botEntity.getData().setForcedUserTargetMovement(0);
                        }
                    }

                    /*if (botEntity.getData().getOwnerId() == 0 && botEntity.getData().getOwnerName().equals("Mania Hotel") && botEntity.getData().getUsername().equals("Frank")) {
                        switch (botEntity.getFrankStatus()) {
                            case 8: {
                                botEntity.onChat("Bye!");
                                break;
                            }
                            case 10: {
                                botEntity.leaveRoom();
                            }
                        }
                        botEntity.setFrankStatus(botEntity.getFrankStatus() + 1);
                    }*/
                }
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onTickComplete() {

    }

    public void walkNow() {
        this.walkNow = true;
    }

    @Override
    public void setTicksUntilCompleteInSeconds(double seconds) {
        long realTime = Math.round(seconds * 1000 / 500);

        if (realTime < 1) {
            realTime = 1; //0.5s
        }

        this.ticksUntilComplete = realTime;
    }

    @Override
    public void say(String message) {
        this.say(message, ChatEmotion.NONE);
    }

    @Override
    public void say(String message, ChatEmotion emotion) {
        if (message == null) {
            return;
        }

        this.getEntity().getRoom().getEntities().broadcastMessage(new TalkMessageComposer(this.getEntity().getId(), message, emotion, 0));
    }

    @Override
    public void sit() {

    }

    @Override
    public void lay() {

    }

    protected void moveTo(Position position) {
        this.getEntity().moveTo(position.getX(), position.getY());
    }

    @Override
    public boolean onTalk(PlayerEntity entity, String message) {
        return false;
    }

    @Override
    public boolean onPlayerLeave(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onPlayerEnter(PlayerEntity entity) {
        return false;
    }

    @Override
    public boolean onAddedToRoom() {
        return false;
    }

    @Override
    public boolean onRemovedFromRoom() {
        if(this.followingPlayer != null) {
            this.followingPlayer.getFollowingEntities().remove(this);
            this.followingPlayer = null;
        }

        if (this.getEntity() instanceof BotEntity) {
            BotEntity entity = (BotEntity)this.getEntity();
            if (entity != null) {
                if (entity.getData().getForcedFurniTargetMovement() != 0) {
                    entity.getData().setForcedFurniTargetMovement(0);
                }

                if (entity.getData().getForcedUserTargetMovement() != 0) {
                    entity.getData().setForcedUserTargetMovement(0);
                }
            }
        }

        return false;
    }

    @Override
    public boolean canMove() {
        return true;
    }

    public RoomEntity getEntity() {
        return entity;
    }

    public BotEntity getBotEntity() {
        return (BotEntity) entity;
    }

    public PetEntity getPetEntity() {
        return (PetEntity) entity;
    }
}
