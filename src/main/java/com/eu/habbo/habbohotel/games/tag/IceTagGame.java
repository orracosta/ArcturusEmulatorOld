package com.eu.habbo.habbohotel.games.tag;

import com.eu.habbo.habbohotel.games.GameTeam;
import com.eu.habbo.habbohotel.items.interactions.games.tag.InteractionTagPole;
import com.eu.habbo.habbohotel.items.interactions.games.tag.icetag.InteractionIceTagPole;
import com.eu.habbo.habbohotel.rooms.Room;

/*public class IceTagGame extends Game
{
    private static final int MALE_SKATES = 38;
    private static final int FEMALE_SKATES = 39;

    public boolean hasIceTagGame = false;

    public IceTagGame(Room room) throws Exception
    {
        super(null, null, room);
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

    @EventHandler
    public static void onUserLookAtPoint(RoomUnitLookAtPointEvent event)
    {
        if(!isValidEffect(event.roomUnit.getEffectId()))
            return;

        Room room = event.room;
        
        Habbo h = event.room.getHabbo(event.roomUnit);

        boolean found = false;

        if (room != null && h != null)
        {
            for(Habbo habbo : room.getCurrentHabbos().valueCollection())
            {
                if(habbo.getRoomUnit().getEffectId() == (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES + 7 : FEMALE_SKATES + 7))
                    found = true;
            }

            if(!found)
            {
                room.giveEffect(h, (h.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES) + 7);
                return;
            }

            THashSet<HabboItem> items = room.getRoomSpecialTypes().getItemsOfType(InteractionTagPole.class);

            if(items.isEmpty())
                return;

            if(PathFinder.tilesAdjecent(event.location.X, event.location.Y, h.getRoomUnit().getX(), h.getRoomUnit().getY()))
            {
                if (h.getRoomUnit().getEffectId() == (h.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES + 7 : FEMALE_SKATES + 7))
                {
                    boolean affectedHabbo = false;
                    for (Habbo habbo : room.getHabbosAt(event.location))
                    {
                        if (habbo == h)
                            continue;

                        if (habbo.getRoomUnit().getEffectId() == (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES))
                        {
                            room.giveEffect(habbo, (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES) + 7);
                            room.giveEffect(h, (habbo.getHabboInfo().getGender().equals(HabboGender.M) ? MALE_SKATES : FEMALE_SKATES));
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
    }

    @EventHandler
    public static void onUserWalkEvent(UserTakeStepEvent event)
    {
        if (event.habbo.getHabboInfo().getCurrentGame() == IceTagGame.class)
        {

        }
        else
        {
            THashSet<InteractionIceTagIce>
        }
    }

    private static boolean isValidEffect(int effectId)
    {
        if(effectId == MALE_SKATES)
            return true;

        if(effectId == FEMALE_SKATES)
            return true;

        if(effectId - 7 == MALE_SKATES)
            return true;

        if (effectId - 7 == FEMALE_SKATES)
            return true;

        return false;
    }

    private static boolean hasValidEffect(Habbo habbo)
    {
        if (habbo.getHabboInfo().getGender().equals(HabboGender.M))
        {
            return habbo.getRoomUnit().getEffectId() == MALE_SKATES || habbo.getRoomUnit().getEffectId() - 7 == MALE_SKATES;
        }
        else
        {
            return habbo.getRoomUnit().getEffectId() == FEMALE_SKATES || habbo.getRoomUnit().getEffectId() - 7 == FEMALE_SKATES;
        }
    }
}
*/

public class IceTagGame extends TagGame
{
    private static final int MALE_SKATES = 38;
    private static final int FEMALE_SKATES = 39;

    public IceTagGame(Room room)
    {
        super(GameTeam.class, TagGamePlayer.class, room);
    }

    @Override
    public Class<? extends InteractionTagPole> getTagPole()
    {
        return InteractionIceTagPole.class;
    }

    @Override
    public int getMaleEffect()
    {
        return MALE_SKATES;
    }

    @Override
    public int getMaleTaggerEffect()
    {
        return MALE_SKATES + 7;
    }

    @Override
    public int getFemaleEffect()
    {
        return FEMALE_SKATES;
    }

    @Override
    public int getFemaleTaggerEffect()
    {
        return FEMALE_SKATES + 7;
    }
}