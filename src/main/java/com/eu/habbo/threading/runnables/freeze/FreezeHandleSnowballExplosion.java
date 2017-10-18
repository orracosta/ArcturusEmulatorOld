package com.eu.habbo.threading.runnables.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.set.hash.THashSet;

class FreezeHandleSnowballExplosion implements Runnable
{
    private final FreezeThrowSnowball thrownData;

    public FreezeHandleSnowballExplosion(FreezeThrowSnowball thrownData)
    {
        this.thrownData = thrownData;
    }

    @Override
    public void run()
    {
        try
        {
            if(this.thrownData == null || this.thrownData.habbo.getHabboInfo().getGamePlayer() == null)
                return;
            
            FreezeGamePlayer player = (FreezeGamePlayer)this.thrownData.habbo.getHabboInfo().getGamePlayer();

            if (player == null)
                return;

            player.addSnowball();

            THashSet<RoomTile> tiles = new THashSet<RoomTile>();

            FreezeGame game = ((FreezeGame)this.thrownData.room.getGame(FreezeGame.class));

            if(game == null)
                return;

            if (player.nextHorizontal)
            {
                tiles.addAll(game.affectedTilesByExplosion(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
            }

            if (player.nextDiagonal)
            {
                tiles.addAll(game.affectedTilesByExplosionDiagonal(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
                player.nextDiagonal = false;
            }

            THashSet<InteractionFreezeTile> freezeTiles = new THashSet<InteractionFreezeTile>();

            for (RoomTile t : tiles)
            {
                THashSet<HabboItem> items = this.thrownData.room.getItemsAt(t);

                for (HabboItem i : items)
                {
                    if (i instanceof InteractionFreezeTile)
                    {
                        i.setExtradata("11000");
                        freezeTiles.add((InteractionFreezeTile) i);
                        this.thrownData.room.updateItem(i);
                        i.setExtradata("0");

                        THashSet<Habbo> habbos = new THashSet<Habbo>();
                        habbos.addAll(this.thrownData.room.getHabbosAt(i.getX(), i.getY()));

                        for (Habbo habbo : habbos)
                        {
                            if (habbo.getHabboInfo().getGamePlayer() != null && habbo.getHabboInfo().getGamePlayer() instanceof FreezeGamePlayer)
                            {
                                FreezeGamePlayer hPlayer = (FreezeGamePlayer) habbo.getHabboInfo().getGamePlayer();
                                if(!hPlayer.canGetFrozen())
                                    continue;

                                if (hPlayer.getTeamColor().equals(player.getTeamColor()))
                                    player.addScore(-FreezeGame.FREEZE_LOOSE_POINTS);
                                else
                                    player.addScore(FreezeGame.FREEZE_LOOSE_POINTS);

                                ((FreezeGamePlayer)habbo.getHabboInfo().getGamePlayer()).freeze();

                                AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("EsA"));
                            }
                        }
                    } else if (i instanceof InteractionFreezeBlock)
                    {
                        if (i.getExtradata().equalsIgnoreCase("0"))
                        {
                            game.explodeBox((InteractionFreezeBlock) i);
                            player.addScore(FreezeGame.DESTROY_BLOCK_POINTS);
                        }
                    }
                }
            }

            Emulator.getThreading().run(new FreezeResetExplosionTiles(freezeTiles, this.thrownData.room), 1000);
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }
}
