package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.football.FootballScoreFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerScoreAchieved;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GamePlayer;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.games.GameType;
import com.habboproject.server.game.rooms.types.components.games.RoomGame;
import com.habboproject.server.game.rooms.types.components.games.banzai.BanzaiGame;
import com.habboproject.server.game.rooms.types.components.games.football.FootballGame;
import com.habboproject.server.game.rooms.types.components.games.freeze.FreezeGame;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class GameComponent {
    private Room room;
    private RoomGame instance;

    private Map<GameTeam, List<GamePlayer>> teams;
    private Map<GameTeam, Integer> scores;
    private Map<GameTeam, Integer> teamScores;
    private Map<Long, Integer> giveScoreTrigger;

    public GameComponent(Room room) {
        this.teams = new HashMap<GameTeam, List<GamePlayer>>() {{
            put(GameTeam.BLUE, Lists.newArrayList());
            put(GameTeam.YELLOW, Lists.newArrayList());
            put(GameTeam.RED, Lists.newArrayList());
            put(GameTeam.GREEN, Lists.newArrayList());
        }};

        this.resetScores();

        this.room = room;
        this.giveScoreTrigger = Maps.newHashMap();
    }

    public void resetScores() {
        if (this.scores != null)
            this.scores.clear();

        this.scores = new ConcurrentHashMap<GameTeam, Integer>() {{
            put(GameTeam.BLUE, 0);
            put(GameTeam.YELLOW, 0);
            put(GameTeam.GREEN, 0);
            put(GameTeam.RED, 0);
        }};

        if (this.teamScores != null) {
            this.teamScores.clear();
        }

        this.teamScores = new ConcurrentHashMap<GameTeam, Integer>(){{
            put(GameTeam.BLUE, 0);
            put(GameTeam.YELLOW, 0);
            put(GameTeam.GREEN, 0);
            put(GameTeam.RED, 0);
        }};

        if (this.teams != null && this.teams.size() > 0) {
            for (Map.Entry<GameTeam, List<GamePlayer>> entry : this.teams.entrySet()) {
                if (entry.getValue() == null || entry.getValue().size() <= 0)
                    continue;

                for (GamePlayer player : entry.getValue()) {
                    player.dispose();
                }
            }
        }
    }

    public void dispose() {
        for (Map.Entry<GameTeam, List<GamePlayer>> entry : this.teams.entrySet()) {
            for (GamePlayer player : entry.getValue()) {
                player.dispose();
            }

            entry.getValue().clear();
        }

        this.teams.clear();
    }

    public void stop() {
        if (this.instance != null) {
            this.instance.stop();
        }

        this.instance = null;
    }

    public void createNew(GameType game) {
        switch (game) {
            case BANZAI:
                this.instance = new BanzaiGame(this.room);
                break;

            case FREEZE:
                this.instance = new FreezeGame(this.room);
                break;

            case FOOTBALL:
                this.instance = new FootballGame(this.room);
                break;

            case TAG:
                //TODO: tag game
                break;
        }
    }

    public void removeFromTeam(GameTeam team, Integer playerId) {
        GamePlayer requestedPlayer = null;

        if (!this.getTeams().containsKey(team))
            return;

        for (GamePlayer player : this.getTeams().get(team)) {
            if (player.getPlayerId() != playerId)
                continue;

            requestedPlayer = player;
            break;
        }

        if (requestedPlayer != null) {
            this.getTeams().get(team).remove(requestedPlayer);
        }
    }

    public GameTeam getTeam(int playerId) {
        for (Map.Entry<GameTeam, List<GamePlayer>> entry : this.getTeams().entrySet()) {
            for (GamePlayer player : entry.getValue()) {
                if (player.getPlayerId() != playerId)
                    continue;

                return entry.getKey();
            }
        }

        return GameTeam.NONE;
    }

    public void increaseScoreToTeam(long itemId, GameTeam team, int perGame, int amount) {
        if (!this.giveScoreTrigger.containsKey(itemId)) {
            this.giveScoreTrigger.put(itemId, 1);
            this.increaseTeamScore(team, amount);
        } else {
            int received = this.giveScoreTrigger.get(itemId);
            if (received >= perGame) {
                return;
            }

            this.giveScoreTrigger.remove(itemId);
            this.giveScoreTrigger.put(itemId, received++);

            this.increaseTeamScore(team, amount);
        }
    }

    public void increaseScoreToPlayer(long itemId, PlayerEntity playerEntity, GameTeam team, int perGame, int amount) {
        if (!this.giveScoreTrigger.containsKey(itemId)) {
            this.giveScoreTrigger.put(itemId, 1);
            this.increasePlayerScore(team, playerEntity, amount);
        } else {
            int received = this.giveScoreTrigger.get(itemId);
            if (received >= perGame) {
                return;
            }

            this.giveScoreTrigger.remove(itemId);
            this.giveScoreTrigger.put(itemId, received++);

            this.increasePlayerScore(team, playerEntity, amount);
        }
    }

    public int getScoreByTeam(GameTeam team) {
        int teamScore = this.teamScores.get(team);
        for (GamePlayer player : this.teams.get(team)) {
            teamScore += player.getPlayerScore();
        }

        return teamScore;
    }

    public void increaseTeamScore(GameTeam team, int amount) {
        if (!this.scores.containsKey(team)) {
            return;
        }

        this.teamScores.replace(team, this.teamScores.get(team) + amount);
        this.scores.replace(team, this.getScoreByTeam(team));

        this.updateScore(team, null);
    }

    public void decreaseTeamScore(GameTeam team, int amount) {
        if (!this.scores.containsKey(team)) {
            return;
        }

        int score = this.teamScores.get(team) - amount;
        if (score < 0) {
            score = 0;
        }

        this.teamScores.replace(team, score);
        this.scores.replace(team, this.getScoreByTeam(team));

        this.updateScore(team, null);
    }

    public void increasePlayerScore(GameTeam team, PlayerEntity playerEntity, int amount) {
        if (!this.scores.containsKey(team)) {
            return;
        }

        for (GamePlayer player : this.teams.get(team)) {
            if (player.getPlayerId() != playerEntity.getPlayerId())
                continue;

            player.increaseScore(amount);
            break;
        }

        this.scores.replace(team, this.getScoreByTeam(team));

        this.updateScore(team, playerEntity);
    }

    public void updateScore(GameTeam team, PlayerEntity playerEntity) {
        for (RoomItemFloor scoreItem : this.getRoom().getItems().getByClass(FootballScoreFloorItem.class)) {
            scoreItem.sendUpdate();
        }

        for (RoomItemFloor scoreboard : this.getRoom().getItems().getByInteraction("%_score")) {
            if (team != null && !scoreboard.getDefinition().getInteraction().toUpperCase().startsWith(team.name()))
                continue;

            scoreboard.setExtraData(team == null ? "0" : String.valueOf(this.getScore(team)));
            scoreboard.sendUpdate();
        }

        WiredTriggerScoreAchieved.executeTriggers(this.getRoom().getGame().getScore(team), team, this.getRoom(), playerEntity);
    }

    public int getScore(GameTeam team) {
        return this.scores.get(team);
    }

    public int getScoreByPlayer(GameTeam team, int playerId) {
        for (GamePlayer player : this.teams.get(team)) {
            if (player.getPlayerId() != playerId)
                continue;

            return player.getPlayerScore();
        }

        return 0;
    }

    public Map<GameTeam, List<GamePlayer>> getTeams() {
        return this.teams;
    }

    public RoomGame getInstance() {
        return this.instance;
    }

    public Room getRoom() {
        return this.room;
    }

    public Map<GameTeam, Integer> getScores() {
        return this.scores;
    }

    public Map<Long, Integer> getGiveScoreTrigger() {
        return this.giveScoreTrigger;
    }
}
