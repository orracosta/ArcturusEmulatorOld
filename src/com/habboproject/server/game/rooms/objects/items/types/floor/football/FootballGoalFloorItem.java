package com.habboproject.server.game.rooms.objects.items.types.floor.football;

import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.network.messages.outgoing.room.avatar.ActionMessageComposer;


public class FootballGoalFloorItem extends RoomItemFloor {
    private GameTeam gameTeam;

    public FootballGoalFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        switch (this.getDefinition().getItemName()) {
            case "fball_goal_b": {
                this.gameTeam = GameTeam.BLUE;
                break;
            }

            case "fball_goal_g": {
                this.gameTeam = GameTeam.GREEN;
                break;
            }

            case "fball_goal_r": {
                this.gameTeam = GameTeam.RED;
                break;
            }

            case "fball_goal_y": {
                this.gameTeam = GameTeam.YELLOW;
                break;
            }

            default:
                break;
        }
    }

    public void onItemAddedToStack(RoomItemFloor floorItem) {
        if (floorItem instanceof FootballFloorItem) {
            this.getRoom().getGame().increaseTeamScore(this.gameTeam, 1);

            FootballFloorItem footballFloorItem = (FootballFloorItem)floorItem;
            if (footballFloorItem.getPusher() != null) {
                PlayerEntity entity = footballFloorItem.getPusher();

                if (entity != null && entity.getPlayer() != null && entity.getPlayer().getSession() != null) {
                    entity.getPlayer().getAchievements().progressAchievement(AchievementType.FOOTBALL_GOAL, 1);
                    this.getRoom().getEntities().broadcastMessage(new ActionMessageComposer(entity.getId(), 1));
                }
            }
        }
    }

    @Override
    public boolean isMovementCancelled(RoomEntity entity) {
        return true;
    }

    public GameTeam getGameTeam() {
        return this.gameTeam;
    }
}
