package com.habboproject.server.game.rooms.types.components.games;

import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiSphereFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreTeamEntry;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.types.HighscorePlayer;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreClassicFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreMostWinFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore.HighscoreTeamFloorItem;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.GameComponent;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public abstract class RoomGame implements CometThread {
    private GameType type;

    protected int timer;
    protected int gameLength;
    protected int countDown;
    protected boolean active;
    protected Room room;

    private ScheduledFuture future;
    private Logger log;

    public RoomGame(Room room, GameType gameType) {
        this.type = gameType;
        this.room = room;

        this.log = Logger.getLogger("RoomGame [" + room.getData().getName() + "][" + room.getData().getId() + "][" + this.type + "]");

        this.countDown = 3;
    }

    @Override
    public void run() {
        try {
            if (this.type == GameType.BANZAI && this.countDown > 0) {
                --this.countDown;

                if (this.countDown == 0) {
                    for (RoomItemFloor item : this.room.getItems().getByClass(BanzaiSphereFloorItem.class)) {
                        item.setExtraData("2");
                        item.sendUpdate();
                    }
                }

                if (this.countDown > 1) {
                    ThreadManager.getInstance().executeSchedule(this, 500, TimeUnit.MILLISECONDS);
                    return;
                }
            }

            if (this.timer == 0) {
                this.onGameStarts();
            }

            this.tick();

            if (this.timer >= this.gameLength) {
                this.onGameEnds();
                this.room.getGame().stop();
            }

            ++this.timer;
        }
        catch (Exception e) {
            this.log.error("Error during game tick", e);
        }
    }

    public void stop() {
        if (this.active && this.future != null) {
            this.future.cancel(false);

            this.active = false;

            this.gameLength = 0;
            this.timer = 0;
        }
    }

    public void startTimer(int amount) {
        if (this.active && this.future != null) {
            this.future.cancel(false);
        }

        this.future = ThreadManager.getInstance().executePeriodic(this, 0, 1, TimeUnit.SECONDS);

        this.gameLength = amount;
        this.active = true;

        this.log.debug("Game active for " + amount + " seconds");
    }

    public void updateHighscoreItems() {
        List<HighscoreClassicFloorItem> classicScoreboards = this.room.getItems().getByClass(HighscoreClassicFloorItem.class);
        List<HighscoreTeamFloorItem> teamScoreboards = this.room.getItems().getByClass(HighscoreTeamFloorItem.class);
        List<HighscoreMostWinFloorItem> mostWinScoreboards = this.room.getItems().getByClass(HighscoreMostWinFloorItem.class);

        List<GamePlayer> winningPlayers = this.room.getGame().getTeams().get(this.winningTeam());

        if (classicScoreboards.size() != 0) {
            List<String> winningPlayerUsernames = Lists.newArrayList();
            for (GamePlayer player : winningPlayers) {
                PlayerEntity playerEntity = this.room.getEntities().getEntityByPlayerId(player.getPlayerId());
                if (playerEntity != null) {
                    String playerName = playerEntity.getUsername();
                    winningPlayerUsernames.add(playerName);
                }
            }

            if (winningPlayerUsernames.size() != 0) {
                for (RoomItemFloor scoreboard : classicScoreboards) {
                    ((HighscoreClassicFloorItem)scoreboard).addEntry(winningPlayerUsernames, this.getScore(this.winningTeam()));
                }
            }
        }

        if (teamScoreboards.size() != 0) {
            List<HighscorePlayer> winningPlayersData = Lists.newArrayList();
            for (GamePlayer player : winningPlayers) {
                String playerName = this.room.getEntities().getEntityByPlayerId(player.getPlayerId()).getUsername();
                winningPlayersData.add(new HighscorePlayer(playerName, this.getScoreByPlayer(this.winningTeam(), player.getPlayerId())));
            }

            if (winningPlayersData.size() != 0) {
                boolean other = true;
                int entryId = 0;

                for (RoomItemFloor scoreboard : teamScoreboards) {
                    if (((HighscoreTeamFloorItem)scoreboard).getEntries().size() > 0) {
                        for (Map.Entry<Integer, List<HighscoreTeamEntry>> entries : ((HighscoreTeamFloorItem)scoreboard).getEntries().entrySet()) {
                            boolean isOther = entries.getValue().size() <= 0;
                            block6 : for (HighscoreTeamEntry entry : entries.getValue()) {
                                if (entry.getUsers().size() <= 0 || entry.getUsers().size() != winningPlayersData.size()) {
                                    isOther = true;
                                    break;
                                }

                                for (HighscorePlayer playerEntry : entry.getUsers()) {
                                    if (winningPlayersData.stream().filter(x -> x != null && x.getUsername().equals(playerEntry.getUsername())).count() == 1)
                                        continue;

                                    isOther = true;
                                    continue block6;
                                }
                            }

                            if (isOther)
                                continue;

                            entryId = entries.getKey();
                            other = false;
                        }
                    }

                    if (other) {
                        ((HighscoreTeamFloorItem)scoreboard).addEntry(winningPlayersData, this.getScore(this.winningTeam()));
                        continue;
                    }

                    ((HighscoreTeamFloorItem)scoreboard).updateEntry(entryId, winningPlayersData, this.getScore(this.winningTeam()));
                }
            }
        }

        if (mostWinScoreboards.size() != 0) {
            List<HighscorePlayer> winningPlayersData = Lists.newArrayList();
            for (GamePlayer player : winningPlayers) {
                String playerName = this.room.getEntities().getEntityByPlayerId(player.getPlayerId()).getUsername();
                winningPlayersData.add(new HighscorePlayer(playerName, this.getScoreByPlayer(this.winningTeam(), player.getPlayerId())));
            }

            if (winningPlayersData.size() != 0) {
                boolean other = true;
                int entryId = 0;

                for (RoomItemFloor scoreboard : mostWinScoreboards) {
                    if (((HighscoreMostWinFloorItem)scoreboard).getEntries().size() > 0) {
                        for (Map.Entry<Integer, List<HighscoreTeamEntry>> entries : ((HighscoreMostWinFloorItem)scoreboard).getEntries().entrySet()) {
                            boolean isOther = entries.getValue().size() <= 0;

                            block11 : for (HighscoreTeamEntry entry : entries.getValue()) {
                                if (entry.getUsers().size() <= 0 || entry.getUsers().size() != winningPlayersData.size()) {
                                    isOther = true;
                                    break;
                                }

                                for (HighscorePlayer playerEntry : entry.getUsers()) {
                                    if (winningPlayersData.stream().filter(x -> x != null && x.getUsername().equals(playerEntry.getUsername())).count() == 1)
                                        continue;

                                    isOther = true;
                                    continue block11;
                                }
                            }

                            if (isOther)
                                continue;

                            entryId = entries.getKey();
                            other = false;
                        }
                    }

                    if (other) {
                        ((HighscoreMostWinFloorItem)scoreboard).addEntry(winningPlayersData, 1);
                        continue;
                    }

                    ((HighscoreMostWinFloorItem)scoreboard).updateEntry(entryId, winningPlayersData, 1);
                }
            }
        }
    }

    public GameTeam winningTeam() {
        Map.Entry<GameTeam, Integer> winningTeam = null;
        for (Map.Entry<GameTeam, Integer> score : this.getGameComponent().getScores().entrySet()) {
            if (winningTeam != null && winningTeam.getValue() >= score.getValue())
                continue;

            winningTeam = score;
        }

        return winningTeam != null ? winningTeam.getKey() : GameTeam.NONE;
    }

    public void updateScoreboard(GameTeam team) {
        for (RoomItemFloor scoreboard : this.getGameComponent().getRoom().getItems().getByInteraction("%_score")) {
            if (team != null && !scoreboard.getDefinition().getInteraction().toUpperCase().startsWith(team.name()))
                continue;

            scoreboard.setExtraData(team == null ? "0" : String.valueOf(this.getScore(team)));
            scoreboard.sendUpdate();
        }
    }

    public int getScore(GameTeam team) {
        return this.getGameComponent().getScore(team);
    }

    public int getScoreByPlayer(GameTeam team, int playerId) {
        return this.getGameComponent().getScoreByPlayer(team, playerId);
    }

    public abstract void tick();

    public abstract void onGameEnds();

    public abstract void onGameStarts();

    public GameComponent getGameComponent() {
        return this.room.getGame();
    }

    public GameType getType() {
        return this.type;
    }

    public Logger getLog() {
        return this.log;
    }

    public boolean isActive() {
        return this.active;
    }
}
