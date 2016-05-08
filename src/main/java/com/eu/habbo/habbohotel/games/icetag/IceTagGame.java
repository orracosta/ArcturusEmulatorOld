package com.eu.habbo.habbohotel.games.icetag;

import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GameTeamColors;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Created on 11-4-2015 16:03.
 *
 * Needs more work. Bug when you walk onto another ice tile.
 */
public class IceTagGame extends Game
{
    private static final int MALE_SKATES = 38;
    private static final int FEMALE_SKATES = 39;

    public IceTagGame(Room room) throws Exception
    {
        super(null, null, room);

        throw new InstantiationException();
    }

    @Override
    public void initialise()
    {

    }

    @Override
    public boolean addHabbo(Habbo habbo, GameTeamColors teamColor)
    {
        return super.addHabbo(habbo, teamColor);
    }

    @Override
    public void removeHabbo(Habbo habbo)
    {

    }

    @Override
    public void start()
    {
    }

    @Override
    public void run()
    {

    }

    @Override
    public void stop()
    {
    }

    /*@EventHandler
    public static void onUserLookAtPoint(RoomUnitLookAtPointEvent event)
    {
        if(!isValidEffect(event.roomUnit.getEffectId()))
            return;

        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(event.roomUnit.)

        boolean found = false;

        if (room != null)
        {
            for(Habbo habbo : room.getCurrentHabbos().valueCollection())
            {
                if(habbo.getRoomUnit().getEffectId() == (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES + 7 : FEMALE_SKATES + 7))
                    found = true;
            }

            if(!found)
            {
                room.giveEffect(event.habbo, (event.habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES) + 7);
                return;
            }

            THashSet<HabboItem> items = room.getRoomSpecialTypes().getItemsOfType(InteractionIceTagPole.class);

            if(items.isEmpty())
                return;

            if(PathFinder.tilesAdjecent(event.target.X, event.target.Y, event.habbo.getRoomUnit().getX(), event.habbo.getRoomUnit().getY()))
            {
                if (event.habbo.getRoomUnit().getEffectId() == (event.habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES + 7 : FEMALE_SKATES + 7))
                {
                    boolean affectedHabbo = false;
                    for (Habbo habbo : room.getHabbosAt(event.target))
                    {
                        if (habbo == event.habbo)
                            continue;

                        if (habbo.getRoomUnit().getEffectId() == (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES))
                        {
                            room.giveEffect(habbo, (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES) + 7);
                            room.giveEffect(event.habbo, (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES));
                        }

                        affectedHabbo = true;

                        break;
                    }

                    if (affectedHabbo)
                    {
                        for (HabboItem item : items)
                        {
                            item.setExtradata("1");
                            room.updateItem(item);
                            Emulator.getThreading().run(new HabboItemNewState(item, room, "0"), 1000);
                        }
                    }
                }
            }
        }
    }*/

    private static boolean isValidEffect(int effectId)
    {
        if(effectId == MALE_SKATES)
            return true;

        if(effectId == FEMALE_SKATES)
            return true;

        if(effectId - 7 == MALE_SKATES)
            return true;

        return effectId - 7 == FEMALE_SKATES;
    }
}
