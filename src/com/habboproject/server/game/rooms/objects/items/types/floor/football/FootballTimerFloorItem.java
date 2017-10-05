package com.habboproject.server.game.rooms.objects.items.types.floor.football;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.football.FootballGame;


public class FootballTimerFloorItem extends RoomItemFloor {
    private int time = 0;

    private boolean interrupted = false;
    private boolean running = false;

    public FootballTimerFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTriggered) {
        if (!isWiredTriggered) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity)entity;
            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return true;
            }
        }

        if (isWiredTriggered) {
            return this.trigger(requestData);
        }

        if (requestData == 1) {
            int time;
            if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FootballGame) {
                this.running = false;
                this.getRoom().getGame().getInstance().onGameEnds();
                this.getRoom().getGame().stop();
            }

            if ((time = Integer.parseInt(this.getExtraData())) == 0 || time == 30 || time == 60 || time == 120 || time == 180 || time == 300 || time == 600) {
                switch (time) {
                    default: {
                        time = 0;
                        break;
                    }

                    case 0: {
                        time = 30;
                        break;
                    }

                    case 30: {
                        time = 60;
                        break;
                    }

                    case 60: {
                        time = 120;
                        break;
                    }

                    case 120: {
                        time = 180;
                        break;
                    }

                    case 180: {
                        time = 300;
                        break;
                    }

                    case 300: {
                        time = 600;
                        break;
                    }
                }
            } else {
                time = 0;
            }

            this.time = time;

            this.setExtraData(String.valueOf(this.time));
            this.sendUpdate();
        } else if (this.getRoom().getGame().getInstance() == null) {
            this.getRoom().getGame().createNew(GameType.FOOTBALL);
            this.getRoom().getGame().getInstance().startTimer(this.time);
        } else if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FootballGame) {
            this.running = false;
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
        }

        return true;
    }

    public boolean trigger(int requestData) {
        if (requestData == -1) {
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
            this.running = false;
            return false;
        }

        if (this.interrupted && requestData != -2) {
            if (requestData == 0) {
                int gameLength;
                if (this.getExtraData().equals("0") && this.time > 0) {
                    this.setExtraData(String.valueOf(this.time));
                }

                if ((gameLength = Integer.parseInt(this.getExtraData())) == 0) {
                    gameLength = 30;
                }

                this.time = gameLength;

                if (this.getRoom().getGame().getInstance() == null) {
                    this.getRoom().getGame().createNew(GameType.FOOTBALL);
                    this.getRoom().getGame().getInstance().startTimer(gameLength);
                }

                return false;
            }

            if (this.getRoom().getGame().getInstance() == null) {
                this.getRoom().getGame().createNew(GameType.FOOTBALL);
                this.getRoom().getGame().getInstance().startTimer(requestData);
            }

            return false;
        }

        if (this.running) {
            if (this.getRoom().getGame().getInstance() == null) {
                this.running = false;
                return false;
            }

            this.running = false;
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
        } else {
            int gameLength;

            if (this.getExtraData().equals("0") && this.time > 0) {
                this.setExtraData(String.valueOf(this.time));
            }

            if ((gameLength = Integer.parseInt(this.getExtraData())) == 0) {
                gameLength = 30;
            }

            this.time = gameLength;

            if (this.getRoom().getGame().getInstance() == null) {
                this.getRoom().getGame().createNew(GameType.FOOTBALL);
                this.getRoom().getGame().getInstance().startTimer(requestData == 0 || requestData == -2 ? gameLength : requestData);
            }
        }

        return true;
    }

    @Override
    public void onPickup() {
        if (this.getRoom().getGame().getInstance() != null && this.getRoom().getGame().getInstance() instanceof FootballGame) {
            this.getRoom().getGame().getInstance().onGameEnds();
            this.getRoom().getGame().stop();
        }
    }

    public boolean isInterrupted() {
        return this.interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
