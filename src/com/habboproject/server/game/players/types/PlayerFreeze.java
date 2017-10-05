package com.habboproject.server.game.players.types;

import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;
import com.habboproject.server.network.messages.outgoing.room.freeze.UpdateFreezeLivesMessageComposer;

/**
 * Created by brend on 04/02/2017.
 */
public class PlayerFreeze {
    private final Player player;

    private int lives;
    private int balls;
    private int boost;

    private boolean explosionPowerUp;
    private boolean diagonalExplosion;
    private boolean horizontalExplosion;
    private boolean isFreezed;
    private boolean hasFreezed;

    private int freezeTime;
    private boolean isProtected;
    private int protectionTime;
    private boolean isDead;

    public PlayerFreeze(Player player) {
        this.player = player;
        this.reset();
    }

    public void reset() {
        this.lives = 3;
        this.balls = 1;
        this.boost = 2;

        this.explosionPowerUp = false;
        this.diagonalExplosion = false;
        this.horizontalExplosion = true;
        this.isFreezed = false;
        this.hasFreezed = false;
        this.freezeTime = 0;
        this.isProtected = false;
        this.protectionTime = 0;
        this.isDead = false;
    }

    public void cycle() {
        boolean needsUpdate = false;
        if (this.isFreezed) {
            ++this.freezeTime;
            if (this.freezeTime > 10) {
                this.isFreezed = false;
                this.freezeTime = 0;

                if (!this.hasFreezed) {
                    this.protect();
                } else {
                    needsUpdate = true;
                }

                this.hasFreezed = true;
            }
        }

        if (this.isProtected) {
            ++this.protectionTime;
            if (this.protectionTime > 10) {
                this.isProtected = false;
                this.protectionTime = 0;
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            this.updateMyself();
        }
    }

    public void increaseLife() {
        ++this.lives;
        this.updateMyLife();
    }

    public void decreaseLife() {
        FreezeGame freeze = (FreezeGame)this.player.getEntity().getRoom().getGame().getInstance();
        if (freeze == null) {
            return;
        }

        --this.lives;

        if (this.lives <= 0) {
            this.isDead = true;

            freeze.getGameComponent().decreaseTeamScore(this.player.getEntity().getGameTeam(), 20);
            freeze.getGameComponent().removeFromTeam(this.player.getEntity().getGameTeam(), this.player.getEntity().getPlayerId());
            freeze.playerDies(this.player.getEntity());
            return;
        }

        freeze.getGameComponent().decreaseTeamScore(this.player.getEntity().getGameTeam(), 10);

        this.updateMyself();
        this.updateMyLife();
    }

    public void updateMyLife() {
        this.player.getSession().send(new UpdateFreezeLivesMessageComposer(this.player.getId(), this.lives));
    }

    public boolean canThrowBall() {
        return !this.isFreezed;
    }

    public void decreaseBoost() {
        if (this.boost > 0) {
            --this.boost;
        }
    }

    public void increaseBoost() {
        ++this.boost;
    }

    public void increaseBall() {
        ++this.balls;
    }

    public void decreaseBall() {
        if (this.balls > 0) {
            --this.balls;
        }
    }

    public boolean canFreeze() {
        if (this.isFreezed || this.isProtected) {
            return false;
        }
        return true;
    }

    public void freeze() {
        if (this.protectionTime > 0 || this.freezeTime > 0) {
            return;
        }

        this.isFreezed = true;
        this.freezeTime = 5;
        this.protectionTime = 0;

        this.decreaseBall();
        this.decreaseBoost();
        this.decreaseLife();
    }

    public void protect() {
        this.isProtected = true;
        this.updateMyself();
    }

    public void updateMyself() {
        if (this.isDead) {
            return;
        }

        this.player.getEntity().applyEffect(new PlayerEffect(this.needsEffect(), 0));
    }

    private int needsEffect() {
        if (this.isDead) {
            return 0;
        }

        if (this.isFreezed) {
            return 12;
        }

        int needsEffect = this.player.getEntity().getGameTeam().getFreezeEffect();
        if (this.isProtected) {
            needsEffect += 9;
        }

        return needsEffect;
    }

    public int getBoost() {
        if (this.explosionPowerUp) {
            this.explosionPowerUp = false;
            return 6;
        }

        return this.boost < 1 ? 1 : this.boost;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getLives() {
        return this.lives;
    }

    public void setExplosionPowerUp(boolean explosionPowerUp) {
        this.explosionPowerUp = explosionPowerUp;
    }

    public boolean diagonalExplosion() {
        return this.diagonalExplosion;
    }

    public void setDiagonalExplosion(boolean diagonalExplosion) {
        this.diagonalExplosion = diagonalExplosion;
    }

    public boolean horizontalExplosion() {
        return this.horizontalExplosion;
    }

    public void setHorizontalExplosion(boolean horizontalExplosion) {
        this.horizontalExplosion = horizontalExplosion;
    }

    public boolean isFreezed() {
        return this.isFreezed;
    }

    public void setFreezed(boolean freezed) {
        this.isFreezed = freezed;
    }
}
