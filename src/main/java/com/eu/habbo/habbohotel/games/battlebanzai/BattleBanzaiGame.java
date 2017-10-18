package com.eu.habbo.habbohotel.games.battlebanzai;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiSphere;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTimer;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.gates.InteractionBattleBanzaiGate;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.scoreboards.InteractionBattleBanzaiScoreboard;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.EventPriority;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.BattleBanzaiTilesFlicker;
import com.eu.habbo.threading.runnables.GameStop;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.Map;

public class BattleBanzaiGame extends Game
{
    /**
     * The effect id BB effects start at.
     */
    public static final int effectId = 33;

    /**
     * Points for hijacking another users tile.
     */
    public static final int POINTS_HIJACK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.steal");

    /**
     * Points for coloring a grey tile.
     */
    public static final int POINTS_FILL_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.fill");

    /**
     * Points for locking a tile.
     */
    public static final int POINTS_LOCK_TILE = Emulator.getConfig().getInt("hotel.banzai.points.tile.lock");

    private int timeLeft;

    private int tileCount;

    private int countDown;

    /**
     * All locked tiles.
     */
    private final THashMap<GameTeamColors, THashSet<HabboItem>> lockedTiles;

    public BattleBanzaiGame(Room room)
    {
        super(BattleBanzaiGameTeam.class, BattleBanzaiGamePlayer.class, room, true);

        this.lockedTiles = new THashMap<GameTeamColors, THashSet<HabboItem>>();

        room.setAllowEffects(true);
    }

    @Override
    public void initialise()
    {
        if(this.isRunning)
            return;

        int highestTime = 0;
        this.countDown = 3;

        this.resetMap();

        for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet())
        {
            if(set.getValue().getExtradata().isEmpty())
                continue;

            if(highestTime < Integer.valueOf(set.getValue().getExtradata()))
            {
                highestTime = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        synchronized (this.teams)
        {
            for (GameTeam t : this.teams.values())
            {
                t.initialise();
            }
        }

        for(HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
        {
            item.setExtradata("1");
            this.room.updateItemState(item);
        }

        this.timeLeft = highestTime;

        if (this.timeLeft == 0)
        {
            this.timeLeft = 30;
        }

        this.start();
    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void start()
    {
        if(this.isRunning)
            return;

        super.start();

        Emulator.getThreading().run(this, 0);
    }

    @Override
    public void run()
    {
        try
        {
            if (!this.isRunning)
                return;

            if(this.countDown > 0)
            {
                this.countDown--;

                if(this.countDown == 0)
                {
                    for(HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                    {
                        item.setExtradata("2");
                        this.room.updateItemState(item);
                    }
                }

                if(this.countDown > 1)
                {
                    Emulator.getThreading().run(this, 500);

                    return;
                }
            }

            if (this.timeLeft > 0)
            {
                Emulator.getThreading().run(this, 1000);

                this.timeLeft--;

                for (Map.Entry<Integer, InteractionBattleBanzaiTimer> set : this.room.getRoomSpecialTypes().getBattleBanzaiTimers().entrySet())
                {
                    set.getValue().setExtradata(timeLeft + "");
                    this.room.updateItemState(set.getValue());
                }

                int total = 0;
                synchronized (this.lockedTiles)
                {
                    for (Map.Entry<GameTeamColors, THashSet<HabboItem>> set : this.lockedTiles.entrySet())
                    {
                        total += set.getValue().size();
                    }
                }

                GameTeam highestScore = null;

                synchronized (this.teams)
                {
                    for (Map.Entry<GameTeamColors, GameTeam> set : this.teams.entrySet())
                    {
                        if (highestScore == null || highestScore.getTotalScore() < set.getValue().getTotalScore())
                        {
                            highestScore = set.getValue();
                        }
                    }
                }

                if(highestScore != null)
                {
                    for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                    {
                        item.setExtradata((highestScore.teamColor.type + 3) + "");
                        this.room.updateItemState(item);
                    }
                }

                if(total >= this.tileCount && this.tileCount != 0)
                {
                    this.timeLeft = 0;
                }
            }
            else
            {
                this.stop();

                GameTeam winningTeam = null;

                for (GameTeam team : this.teams.values())
                {
                    for(GamePlayer player : team.getMembers())
                    {
                        if (player.getScore() > 0)
                        {
                            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallPlayer"));
                            AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallQuestCompleted"));
                        }
                    }

                    if (winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore())
                    {
                        winningTeam = team;
                    }
                }

                if (winningTeam != null)
                {
                    synchronized (winningTeam)
                    {
                        for (GamePlayer player : winningTeam.getMembers())
                        {
                            if (player.getScore() > 0)
                            {
                                this.room.sendComposer(new RoomUserActionComposer(player.getHabbo().getRoomUnit(), RoomUserAction.WAVE).compose());
                                AchievementManager.progressAchievement(player.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallWinner"));
                            }
                        }

                        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionBattleBanzaiSphere.class))
                        {
                            item.setExtradata((7 + winningTeam.teamColor.type) + "");
                            this.room.updateItemState(item);
                        }

                        Emulator.getThreading().run(new BattleBanzaiTilesFlicker(this.lockedTiles.get(winningTeam.teamColor), winningTeam.teamColor, this.room));
                    }
                }

                this.isRunning = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void stop()
    {
        super.stop();

        this.timeLeft = 0;

        for (HabboItem item : this.room.getFloorItems())
        {
            if (item instanceof InteractionBattleBanzaiTile || item instanceof InteractionBattleBanzaiScoreboard)
            {
                item.setExtradata("0");
                this.room.updateItemState(item);
            }
        }

        this.lockedTiles.clear();
    }

    /**
     * Resets the map.
     */
    protected synchronized void resetMap()
    {
        for (HabboItem item : this.room.getFloorItems())
        {
            if (item instanceof InteractionBattleBanzaiTile)
            {
                item.setExtradata("1");
                this.room.updateItemState(item);
                this.tileCount++;
            }

            if (item instanceof InteractionBattleBanzaiScoreboard)
            {
                item.setExtradata("0");
                this.room.updateItemState(item);
            }
        }
    }

    public void addPositionToGate(GameTeamColors teamColor)
    {
        for (InteractionBattleBanzaiGate gate : this.room.getRoomSpecialTypes().getBattleBanzaiGates().values())
        {
            if (gate.teamColor != teamColor)
                continue;

            if (gate.getExtradata().isEmpty() || gate.getExtradata().equals("0"))
                continue;

            gate.setExtradata(Integer.valueOf(gate.getExtradata()) - 1 + "");
            this.room.updateItemState(gate);
            break;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public static void onUserWalkEvent(UserTakeStepEvent event)
    {
        if(event.habbo.getHabboInfo().getCurrentGame() == BattleBanzaiGame.class)
        {
            BattleBanzaiGame game = (BattleBanzaiGame) event.habbo.getHabboInfo().getCurrentRoom().getGame(BattleBanzaiGame.class);
            if (game != null && game.isRunning)
            {
                if(!event.habbo.getHabboInfo().getCurrentRoom().hasObjectTypeAt(InteractionBattleBanzaiTile.class, event.toLocation.x, event.toLocation.y))
                {
                    event.setCancelled(true);
                    event.habbo.getRoomUnit().setGoalLocation(event.habbo.getRoomUnit().getCurrentLocation());
                    event.habbo.getRoomUnit().getStatus().remove("mv");
                    game.room.sendComposer(new RoomUserStatusComposer(event.habbo.getRoomUnit()).compose());
                }
            }
        }
    }

    /**
     * Locks an tile
     * @param teamColor The color to lock.
     * @param item The item to lock.
     */
    public void tileLocked(GameTeamColors teamColor, HabboItem item, Habbo habbo)
    {
        if(item instanceof InteractionBattleBanzaiTile)
        {
            if(!this.lockedTiles.containsKey(teamColor))
            {
                this.lockedTiles.put(teamColor, new THashSet<HabboItem>());
            }

            this.lockedTiles.get(teamColor).add(item);
        }

        if(habbo != null)
        {
            AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("BattleBallTilesLocked"));
        }
    }

    /**
     * Updates the counters in the room.
     */
    public void refreshCounters()
    {
        for(GameTeam team : this.teams.values())
        {
            if(team.getMembers().isEmpty())
                continue;

            this.refreshCounters(team.teamColor);
        }
    }

    /**
     * Updates the counters for the given GameTeamColors in the room.
     * @param teamColors The color that should be updated.
     */
    public void refreshCounters(GameTeamColors teamColors)
    {
        int totalScore = this.teams.get(teamColors).getTotalScore();

        THashMap<Integer, InteractionBattleBanzaiScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getBattleBanzaiScoreboards(teamColors);

        for (InteractionBattleBanzaiScoreboard scoreboard : scoreBoards.values())
        {
            if(scoreboard.getExtradata().isEmpty())
            {
                scoreboard.setExtradata("0");
            }

            int oldScore = Integer.valueOf(scoreboard.getExtradata());

            if(oldScore == totalScore)
                continue;

            scoreboard.setExtradata(totalScore + "");
            this.room.updateItemState(scoreboard);
        }
    }
}
