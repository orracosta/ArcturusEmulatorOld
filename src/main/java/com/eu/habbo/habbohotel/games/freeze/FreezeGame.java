package com.eu.habbo.habbohotel.games.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeExitTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTimer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.gates.InteractionFreezeGate;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.scoreboards.InteractionFreezeScoreboard;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUserAction;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserActionComposer;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.freeze.FreezeClearEffects;
import com.eu.habbo.threading.runnables.freeze.FreezeThrowSnowball;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class FreezeGame extends Game
{
    public static final int effectId = 40;
    public static int POWER_UP_POINTS;
    public static int POWER_UP_CHANCE;
    public static int POWER_UP_PROTECT_TIME;
    public static int DESTROY_BLOCK_POINTS;
    public static int FREEZE_TIME;
    public static int FREEZE_LOOSE_SNOWBALL;
    public static int FREEZE_LOOSE_BOOST;
    public static int MAX_LIVES;
    public static int MAX_SNOWBALLS;
    public static int FREEZE_LOOSE_POINTS;
    public static boolean POWERUP_STACK;

    private int timeLeft;

    public FreezeGame(Room room)
    {
        super(FreezeGameTeam.class, FreezeGamePlayer.class, room, true);
        room.setAllowEffects(true);
    }

    @Override
    public synchronized void initialise()
    {
        if(this.isRunning)
            return;

        int highestTime = 0;

        this.resetMap();

        for (Map.Entry<Integer, InteractionFreezeTimer> set : this.room.getRoomSpecialTypes().getFreezeTimers().entrySet())
        {
            if(set.getValue().getExtradata().isEmpty())
                continue;

            if(highestTime < Integer.valueOf(set.getValue().getExtradata()))
            {
                highestTime = Integer.valueOf(set.getValue().getExtradata());
            }
        }

        for (GameTeam t : this.teams.values())
        {
            t.initialise();
        }

        if (this.room.getRoomSpecialTypes().hasFreezeExitTile())
        {
            for (Habbo habbo : this.room.getHabbos())
            {
                if (this.getTeamForHabbo(habbo) == null)
                {
                    for (HabboItem item : room.getItemsAt(habbo.getRoomUnit().getCurrentLocation()))
                    {
                        if (item instanceof InteractionFreezeTile)
                        {
                            this.room.teleportHabboToItem(habbo, this.room.getRoomSpecialTypes().getRandomFreezeExitTile());
                        }
                    }
                }
            }
        }
        this.timeLeft = highestTime;

        if (this.timeLeft == 0)
        {
            this.timeLeft = 30;
        }

        this.start();
    }

    synchronized void resetMap()
    {
        for (HabboItem item : this.room.getFloorItems())
        {
            if (item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeScoreboard)
            {
                item.setExtradata("0");
                this.room.updateItemState(item);
            }
        }
    }

    public synchronized void placebackHelmet(GameTeamColors teamColor)
    {
        for (InteractionFreezeGate gate : this.room.getRoomSpecialTypes().getFreezeGates().values())
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

    public void throwBall(Habbo habbo, InteractionFreezeTile item)
    {
        if (!this.isRunning || !habbo.getHabboInfo().isInGame() || habbo.getHabboInfo().getCurrentGame() != this.getClass())
            return;

        if (!item.getExtradata().equalsIgnoreCase("0") && !item.getExtradata().isEmpty())
            return;

        if (RoomLayout.tilesAdjecent(habbo.getRoomUnit().getCurrentLocation(), room.getLayout().getTile(item.getX(), item.getY())))
        {
            if(((FreezeGamePlayer)habbo.getHabboInfo().getGamePlayer()).canThrowSnowball())
            {
                Emulator.getThreading().run(new FreezeThrowSnowball(habbo, item, this.room));
            }
        }
    }

    public THashSet<RoomTile> affectedTilesByExplosion(short x, short y, int radius)
    {
        THashSet<RoomTile> tiles = new THashSet<RoomTile>();

        RoomTile t = this.room.getLayout().getTile(x, y);

        tiles.add(t);
        for(int i = 0; i < 4; i++)
        {
            for(int j = 0; j < radius; j++)
            {
                t = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(x, y), i * 2, (short) radius);

                if(t == null || t.x < 0 || t.y < 0 || t.x >= this.room.getLayout().getMapSizeX() || t.y >= this.room.getLayout().getMapSizeY())
                    continue;
                tiles.add(t);
            }
        }

        return tiles;
    }

    public THashSet<RoomTile>affectedTilesByExplosionDiagonal(short x, short y, int radius)
    {
        THashSet<RoomTile> tiles = new THashSet<RoomTile>();

        for(int i = 0; i < 4; i++)
        {
            RoomTile t = room.getLayout().getTile(x, y);

            for(int j = 0; j < radius; j++)
            {
                t = this.room.getLayout().getTileInFront(this.room.getLayout().getTile(x, y), (i * 2) + 1, (short) radius);

                if (t != null)
                {
                    if (t.x < 0 || t.y < 0 || t.x >= this.room.getLayout().getMapSizeX() || t.y >= this.room.getLayout().getMapSizeY())
                        continue;

                    tiles.add(t);
                }
            }
        }

        return tiles;
    }

    public synchronized void explodeBox(InteractionFreezeBlock block)
    {
        int powerUp = 0;
        if(Emulator.getRandom().nextInt(100) + 1 <= FreezeGame.POWER_UP_CHANCE)
        {
            powerUp += Emulator.getRandom().nextInt(6) + 1;
        }

        block.setExtradata((powerUp + 1) * 1000 + "");

        this.room.updateItemState(block);
    }

    public synchronized void givePowerUp(FreezeGamePlayer player, int powerUpId)
    {
        player.addScore(FreezeGame.POWER_UP_POINTS);

        switch(powerUpId)
        {
            case 2:
            {
                player.increaseExplosion();
                break;
            }

            case 3:
            {
                player.addSnowball();
                break;
            }

            case 4:
            {
                player.nextDiagonal = true;
                break;
            }

            case 5:
            {
                player.nextHorizontal = true;
                player.nextDiagonal = true;
                player.tempMassiveExplosion = true;
                break;
            }

            case 6:
            {
                player.addLife();
                break;
            }

            case 7:
            {
                player.addProtection();
                break;
            }
        }
    }

    public synchronized void playerDies(GamePlayer player)
    {
        Emulator.getThreading().run(new FreezeClearEffects(player.getHabbo()), 1000);
        if(this.room.getRoomSpecialTypes().hasFreezeExitTile())
        {
            InteractionFreezeExitTile tile = this.room.getRoomSpecialTypes().getRandomFreezeExitTile();
            tile.setExtradata("1");
            this.room.updateItemState(tile);
            this.room.teleportHabboToItem(player.getHabbo(), tile);
        }
        this.removeHabbo(player.getHabbo());
    }

    @Override
    public void start()
    {
        if (this.isRunning)
        {
            return;
        }

        super.start();

        WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, this.room, null);
        this.setFreezeTileState("1");
        this.run();
    }

    @Override
    public synchronized void run()
    {
        try
        {
            if (!this.isRunning)
                return;

            if (timeLeft > 0)
            {
                Emulator.getThreading().run(this, 1000);

                this.timeLeft--;

                for (GameTeam team : this.teams.values())
                {
                    for (GamePlayer player : team.getMembers())
                    {
                        ((FreezeGamePlayer)player).cycle();
                    }

                    int totalScore = team.getTotalScore();

                    THashMap<Integer, InteractionFreezeScoreboard> scoreBoards = this.room.getRoomSpecialTypes().getFreezeScoreboards(team.teamColor);

                    for (InteractionFreezeScoreboard scoreboard : scoreBoards.values())
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

                for (Map.Entry<Integer, InteractionFreezeTimer> set : this.room.getRoomSpecialTypes().getFreezeTimers().entrySet())
                {
                    set.getValue().setExtradata(timeLeft + "");
                    this.room.updateItemState(set.getValue());
                }
            } else
            {
                this.stop();
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

        GameTeam winningTeam = null;

        for(GameTeam team : this.teams.values())
        {
            if(winningTeam == null || team.getTotalScore() > winningTeam.getTotalScore())
            {
                winningTeam = team;
            }
        }

        for (GameTeam team : this.teams.values())
        {
            THashSet<GamePlayer> players = new THashSet<GamePlayer>();

            players.addAll(team.getMembers());

            for(GamePlayer p : players)
            {
                if (p.getScore() > 0)
                {
                    if (team.equals(winningTeam))
                    {
                        AchievementManager.progressAchievement(p.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FreezeWinner"), p.getScore());
                        this.room.sendComposer(new RoomUserActionComposer(p.getHabbo().getRoomUnit(), RoomUserAction.WAVE).compose());
                    }

                    AchievementManager.progressAchievement(p.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("FreezePlayer"));
                }
            }
        }

        Map<GameTeamColors, Integer> teamMemberCount = new HashMap<GameTeamColors, Integer>();
        for (Map.Entry<GameTeamColors, GameTeam> teamEntry : this.teams.entrySet())
        {
            teamMemberCount.put(teamEntry.getKey(), teamEntry.getValue().getMembers().size());
        }

        for (Map.Entry<Integer, InteractionFreezeGate> set : this.room.getRoomSpecialTypes().getFreezeGates().entrySet())
        {
            if (teamMemberCount.containsKey(set.getValue()))
            {
                int amount = Math.min(teamMemberCount.get(set.getValue().teamColor), 5);
                set.getValue().setExtradata(amount + "");
                teamMemberCount.put(set.getValue().teamColor, teamMemberCount.get(set.getValue().teamColor) - amount);
                this.room.updateItemState(set.getValue());
            }
        }

        this.setFreezeTileState("0");
    }

    public void setFreezeTileState(String state)
    {
        this.room.getRoomSpecialTypes().getFreezeExitTiles().forEachValue(new TObjectProcedure<InteractionFreezeExitTile>()
        {
            @Override
            public boolean execute(InteractionFreezeExitTile object)
            {
                object.setExtradata(state);
                room.updateItemState(object);
                return true;
            }
        });

    }

    @EventHandler
    public static void onUserWalkEvent(UserTakeStepEvent event)
    {
        if(event.habbo.getHabboInfo().getCurrentGame() == FreezeGame.class)
        {
            FreezeGame game = (FreezeGame) event.habbo.getHabboInfo().getCurrentRoom().getGame(FreezeGame.class);
            if (game != null && game.isRunning)
            {
                if (!game.room.hasObjectTypeAt(InteractionFreezeTile.class, event.toLocation.x, event.toLocation.y))
                {
                    event.setCancelled(true);
                    event.habbo.getRoomUnit().setGoalLocation(event.habbo.getRoomUnit().getCurrentLocation());
                    event.habbo.getRoomUnit().getStatus().remove("mv");
                    game.room.sendComposer(new RoomUserStatusComposer(event.habbo.getRoomUnit()).compose());
                }
            }
        }
    }

    @EventHandler
    public static void onConfigurationUpdated(EmulatorConfigUpdatedEvent event)
    {
        POWER_UP_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.effect");
        POWER_UP_CHANCE = Emulator.getConfig().getInt("hotel.freeze.powerup.chance");
        POWER_UP_PROTECT_TIME = Emulator.getConfig().getInt("hotel.freeze.powerup.protection.time");
        DESTROY_BLOCK_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.block");
        FREEZE_TIME = Emulator.getConfig().getInt("hotel.freeze.onfreeze.time.frozen");
        FREEZE_LOOSE_SNOWBALL = Emulator.getConfig().getInt("hotel.freeze.onfreeze.loose.snowballs");
        FREEZE_LOOSE_BOOST = Emulator.getConfig().getInt("hotel.freeze.onfreeze.loose.explosionboost");
        MAX_LIVES = Emulator.getConfig().getInt("hotel.freeze.powerup.max.lives");
        MAX_SNOWBALLS = Emulator.getConfig().getInt("hotel.freeze.powerup.max.snowballs");
        FREEZE_LOOSE_POINTS = Emulator.getConfig().getInt("hotel.freeze.points.freeze");
        POWERUP_STACK = Emulator.getConfig().getBoolean("hotel.freeze.powerup.protection.stack");
    }
}
