package com.eu.habbo.habbohotel.games;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredHighscore;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.games.GameHabboJoinEvent;
import com.eu.habbo.plugin.events.games.GameHabboLeaveEvent;
import com.eu.habbo.plugin.events.games.GameStartedEvent;
import com.eu.habbo.plugin.events.games.GameStoppedEvent;
import com.eu.habbo.threading.runnables.SaveScoreForTeam;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public abstract class Game implements Runnable
{
    /**
     * The class that should be used that defines teams.
     */
    public final Class<? extends GameTeam> gameTeamClazz;

    /**
     * The class that should be used that defines a player.
     */
    public final Class<? extends GamePlayer> gamePlayerClazz;

    /**
     * All the teams.
     */
    protected final THashMap<GameTeamColors, GameTeam> teams = new THashMap<GameTeamColors, GameTeam>();

    /**
     * The room this game is affected by.
     */
    protected Room room;

    /**
     * Should this game progress any achievements.
     */
    protected boolean countsAchievements;

    /**
     * Time the game started.
     */
    protected int startTime;

    /**
     * Time the game ended.
     */
    protected int endTime;

    /**
     * Wether the game is running.
     */
    public boolean isRunning;

    public Game(Class<? extends GameTeam> gameTeamClazz, Class<? extends GamePlayer> gamePlayerClazz, Room room, boolean countsAchievements)
    {
        this.gameTeamClazz = gameTeamClazz;
        this.gamePlayerClazz = gamePlayerClazz;
        this.room = room;
        this.countsAchievements = countsAchievements;
    }

    /**
     * Should reset the game to it's default state and call start() when done initialising.
     * Only call start if you need to start a game.
     */
    public abstract void initialise();

    /**
     * When overridden call super first!
     * Adds a particular Habbo to a specific team.
     * @param habbo The Habbo to add to an team.
     * @param teamColor The teamcolor to add the Habbo too.
     * @return True when user has joined the game.
     */
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        try
        {
            if (habbo != null)
            {
                if(Emulator.getPluginManager().isRegistered(GameHabboJoinEvent.class, true))
                {
                    Event gameHabboJoinEvent = new GameHabboJoinEvent(this, habbo);
                    Emulator.getPluginManager().fireEvent(gameHabboJoinEvent);
                    if(gameHabboJoinEvent.isCancelled())
                        return false;
                }

                synchronized (this.teams)
                {
                    GameTeam team = this.getTeam(teamColor);
                    if (team == null)
                    {
                        team = this.gameTeamClazz.getDeclaredConstructor(GameTeamColors.class).newInstance(teamColor);
                        this.addTeam(team);
                    }

                    GamePlayer player = this.gamePlayerClazz.getDeclaredConstructor(Habbo.class, GameTeamColors.class).newInstance(habbo, teamColor);
                    team.addMember(player);
                    habbo.getHabboInfo().setCurrentGame(this.getClass());
                    habbo.getHabboInfo().setGamePlayer(player);
                }

                return true;
            }
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }

        return false;
    }

    /**
     * When overridden call super first!
     * Removes an Habbo when the following events occur:
     * <li>The Habbo gets disconnected.</li>
     * <li>The Habbo leaves the room.</li>
     * <li>The Habbo triggers leave team wired.</li>
     * @param habbo The Habbo to be removed.
     */
    public void removeHabbo(Habbo habbo)
    {
        if (habbo != null)
        {
            if(Emulator.getPluginManager().isRegistered(GameHabboLeaveEvent.class, true))
            {
                Event gameHabboLeaveEvent = new GameHabboLeaveEvent(this, habbo);
                Emulator.getPluginManager().fireEvent(gameHabboLeaveEvent);
                if(gameHabboLeaveEvent.isCancelled())
                    return;
            }

            GameTeam team = this.getTeamForHabbo(habbo);
            if (team != null && team.isMember(habbo))
            {
                team.removeMember(habbo.getHabboInfo().getGamePlayer());
                habbo.getHabboInfo().getGamePlayer().reset();
                habbo.getHabboInfo().setCurrentGame(null);
                habbo.getHabboInfo().setGamePlayer(null);

                if(this.countsAchievements && this.endTime > this.startTime)
                {
                    AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GamePlayed"));
                }
            }
        }

        boolean deleteGame = true;
        for (GameTeam team : this.teams.values())
        {
            if (team.getMembers().size() > 0 )
            {
                deleteGame = false;
                break;
            }
        }

        if (deleteGame)
        {
            room.deleteGame(this);
        }
    }

    /**
     * This method should start a game. Make sure to call super.start()
     * to register the time the game was started.
     */
    public void start()
    {
        this.isRunning = true;
        this.startTime = Emulator.getIntUnixTimestamp();

        if(Emulator.getPluginManager().isRegistered(GameStartedEvent.class, true))
        {
            Event gameStartedEvent = new GameStartedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStartedEvent);
        }

        WiredHandler.handle(WiredTriggerType.GAME_STARTS, null, this.room, new Object[]{this});
    }

    /**
     * Main game loop.
     */
    public abstract void run();

    /**
     * Should stop the game.
     *
     * Called upon room unload or when the timer runs out.
     *
     * Make sure to call super.stop() when overriden to save the scores to the database.
     */
    public void stop()
    {
        this.isRunning = false;
        this.endTime = Emulator.getIntUnixTimestamp();

        this.saveScores();

        if(Emulator.getPluginManager().isRegistered(GameStoppedEvent.class, true))
        {
            Event gameStoppedEvent = new GameStoppedEvent(this);
            Emulator.getPluginManager().fireEvent(gameStoppedEvent);
        }

        WiredHandler.handle(WiredTriggerType.GAME_ENDS, null, this.room, new Object[]{this});

        for (HabboItem item : this.room.getRoomSpecialTypes().getItemsOfType(InteractionWiredHighscore.class))
        {
            this.room.updateItem(item);
        }
    }

    /**
     * Saves all scores to the game.
     * Used for the Wired Highscores.
     * Must have set the room.
     */
    private void saveScores()
    {
        if(this.room == null)
            return;

        for(Map.Entry<GameTeamColors, GameTeam> teamEntry : this.teams.entrySet())
        {
            Emulator.getThreading().run(new SaveScoreForTeam(teamEntry.getValue(), this));
        }
    }

    /**
     * Gets the team for the given Habbo.
     * @param habbo The Habbo to look for the team.
     * @return The team the Habbo is in.
     */
    public GameTeam getTeamForHabbo(Habbo habbo)
    {
        if(habbo != null)
        {
            synchronized (this.teams)
            {
                for (GameTeam team : this.teams.values())
                {
                    if (team.isMember(habbo))
                    {
                        return team;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the team by the team color.
     * @param teamColor The teamcolor to look up.
     * @return The team for the given team color.
     */
    public GameTeam getTeam(GameTeamColors teamColor)
    {
        synchronized (this.teams)
        {
            return this.teams.get(teamColor);
        }
    }

    /**
     * Adds a new team to the game.
     * Note: Overwrites any existing team for the team.teamColor
     * @param team The team to add.
     */
    public void addTeam(GameTeam team)
    {
        synchronized (this.teams)
        {
            this.teams.put(team.teamColor, team);
        }
    }

    /**
     * @return The room this game is affected by.
     */
    public Room getRoom()
    {
        return this.room;
    }

    /**
     * @return The time the game was started.
     */
    public int getStartTime()
    {
        return this.startTime;
    }

    /**
     * @return Last time this game ended.
     */
    public int getEndTime()
    {
        return this.endTime;
    }

    /**
     * Adds time to the current game.
     * @param time The amount of time to add.
     */
    public void addTime(int time)
    {
        this.endTime += time;
    }
}
