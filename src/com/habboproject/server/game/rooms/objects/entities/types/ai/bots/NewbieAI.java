package com.habboproject.server.game.rooms.objects.entities.types.ai.bots;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.AbstractBotAI;
import com.habboproject.server.threads.executors.newbie.NewbieBubbleEvent;
import com.habboproject.server.threads.executors.newbie.NewbieFrankLeaveEvent;
import com.habboproject.server.threads.executors.newbie.NewbieFrankSayEvent;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;
import com.habboproject.server.threads.ThreadManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by brend on 04/03/2017.
 */
public class NewbieAI extends AbstractBotAI {
    private final String[] phrases = new String[] {
            "<p style=\"color: #000\">Bem-vindo ao Habbo Hotel,<br/>{0}! - Sou Frank. Clique no <br/>chão para começar a caminhar. Entre<br/>e fique à vontade.</p>",
            "<p style=\"color: #000\">Nos próximos dias você<br/>receberá um monte de brindes super<br/>bacanas!</p>",
            "<p style=\"color: #000\">Lá vai seu primeiro presente:<br/>uma geladeirinha que está sempre<br/>cheia! Você a encontra no seu<br/>inventário.</p>",
            "<p style=\"color: #000\">Coloque sua geladeira no<br/>chão e depois dê 2 cliques nela para<br/>pegar um refresco.</p>",
            "<p style=\"color: #000\">Amanhã você receberá um<br/>mascote só seu: um gato, cachorro<br/>ou porquinho!</p>",
            "<p style=\"color: #000\">Até amanhã, {0}!</p>"
    };

    private int currentStep = 1;
    private int tickToLeave = 0;
    private boolean nextStep = true;
    private PlayerEntity newbieEntity = null;

    public NewbieAI(RoomEntity entity) {
        super(entity);
    }

    @Override
    public boolean onPlayerEnter(PlayerEntity entity) {
        if (entity.getId() == this.getNewbieEntity().getId()) {
            this.getEntity().moveTo(6, 11);
        }

        return false;
    }

    @Override
    public void onTick() {
        if (this.getNewbieEntity() != null) {
            if (!this.getNewbieEntity().isWalking()) {
                int rotation = Position.calculateRotation(this.getEntity().getPosition(), this.getNewbieEntity().getPosition());

                if (this.getEntity().getHeadRotation() != rotation && this.currentStep < 6) {
                    this.getEntity().lookTo(this.getNewbieEntity().getPosition());
                }
            }

            switch (this.currentStep) {
                case 1:
                    if (this.nextStep && !this.getBotEntity().isWalking()) {
                        this.nextStep = false;
                        this.getNewbieEntity().getPlayer().getData().setNewbieStep("2");
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[0].replace("{0}", this.getNewbieEntity().getUsername())), 3, TimeUnit.SECONDS);
                    }
                    break;
                case 2:
                    if (nextStep) {
                        this.nextStep = false;
                        this.getBotEntity().lookTo(this.getNewbieEntity().getPosition());
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[1]), 5, TimeUnit.SECONDS);
                    }
                    break;
                case 3:
                    if (nextStep) {
                        this.nextStep = false;
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[2]), 10, TimeUnit.SECONDS);
                    }
                    break;
                case 4:
                    if (nextStep) {
                        this.nextStep = false;
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[3]), 5, TimeUnit.SECONDS);
                        this.getNewbieEntity().getPlayer().getData().setNewbieStep("3");
                    }
                    break;
                case 5:
                    if (nextStep) {
                        this.nextStep = false;
                        ThreadManager.getInstance().executeSchedule(new NewbieBubbleEvent(this, this.getNewbieEntity(), "helpBubble/add/BOTTOM_BAR_INVENTORY/nux.bot.info.inventory.1", 1), 2, TimeUnit.SECONDS);
                    }
                    break;
                case 6:
                    if (nextStep) {
                        if (this.getNewbieEntity().getOnBubble() == 0 && this.getNewbieEntity().getPlayer().getData().getNewbieStep().equals("4")) {
                            this.nextStep = false;
                            this.getBotEntity().moveTo(5, 4);
                        }
                    }
                    break;
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                    if (nextStep) {
                        this.increaseStep();
                    }
                    break;
                case 12:
                    if (nextStep) {
                        this.nextStep = false;
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[4]), 1, TimeUnit.SECONDS);
                    }
                    break;
                case 13:
                    if (nextStep) {
                        this.nextStep = false;
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankSayEvent(this, phrases[5].replace("{0}", this.getNewbieEntity().getUsername())), 5, TimeUnit.SECONDS);
                    }
                    break;
                case 14:
                    if (nextStep) {
                        this.nextStep = false;
                        this.getEntity().getRoom().getEntities().broadcastMessage(new ActionMessageComposer(this.getEntity().getId(), 1));
                        ThreadManager.getInstance().executeSchedule(new NewbieFrankLeaveEvent(this, this.getNewbieEntity()), 2, TimeUnit.SECONDS);
                    }
                    break;
                default:
                    break;
            }

            if (this.currentStep >= 7 && this.currentStep <= 11)
                this.nextStep = true;

            if (this.currentStep == 6 && this.getNewbieEntity().isWalking()) {
                this.increaseStep();
            } else if (this.currentStep == 6 && !this.getNewbieEntity().isWalking() && this.getNewbieEntity().getOnBubble() == 0 &&
                    this.getNewbieEntity().getPlayer().getData().getNewbieStep().equals("4")) {
                this.getBotEntity().moveTo(5, 4);
                this.increaseStep();
            }
        }

        if (this.tickToLeave >= 10) {
            this.getBotEntity().leaveRoom();
        }

        if (this.getNewbieEntity() == null || this.getNewbieEntity().getPlayer() == null || this.getNewbieEntity().getPlayer().getSession() == null ||
                this.getNewbieEntity().getRoom() == null || this.getNewbieEntity().getRoom().getId() != this.getBotEntity().getRoom().getId()) {
            this.tickToLeave++;
        }
    }

    public void increaseStep() {
        currentStep++;
    }

    public PlayerEntity getNewbieEntity() {
        return newbieEntity;
    }

    public void setNewbieEntity(PlayerEntity newbieEntity) {
        this.newbieEntity = newbieEntity;
    }

    public void setNextStep(boolean nextStep) {
        this.nextStep = nextStep;
    }
}
