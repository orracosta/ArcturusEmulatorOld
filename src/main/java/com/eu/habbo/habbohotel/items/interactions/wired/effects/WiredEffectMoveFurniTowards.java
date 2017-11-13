package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.items.interactions.InteractionStackHelper;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.games.battlebanzai.InteractionBattleBanzaiTile;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.rooms.*;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.items.FloorItemOnRollerComposer;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;

public class WiredEffectMoveFurniTowards extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.CHASE;

    private THashSet<HabboItem> items;

    public WiredEffectMoveFurniTowards(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        items = new THashSet<HabboItem>();
    }

    public WiredEffectMoveFurniTowards(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        items = new THashSet<HabboItem>();
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if (item.getRoomId() == 0 || item instanceof InteractionRoller || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
                items.add(item);
        }

        this.items.removeAll(items);

        for(HabboItem item : this.items)
        {
            RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
            double shortest = 1000.0D;

            Habbo target = null;
            RoomTile nextStep = t;

            for(Habbo habbo : room.getHabbos())
            {
                RoomTile h = habbo.getRoomUnit().getCurrentLocation();

                double distance = t.distance(h);

                RoomTile checkstep = t;
                Deque<RoomTile> findpath = room.getLayout().findPath(t, h, false);

                if(!findpath.isEmpty() && findpath != null){
                    checkstep = findpath.getFirst();
                }

                if(distance <= shortest && checkstep != t)
                {
                    target = habbo;
                    shortest = distance;
                    nextStep = checkstep;
                }
            }

            if(target != null)
            {
                if(RoomLayout.tilesAdjecent(target.getRoomUnit().getCurrentLocation(), room.getLayout().getTile(item.getX(), item.getY())) && (target.getRoomUnit().getX() == item.getX() || target.getRoomUnit().getY() == item.getY()))
                {
                    final Habbo finalTarget = target;
                    Emulator.getThreading().run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            WiredHandler.handle(WiredTriggerType.COLLISION, finalTarget.getRoomUnit(), room, new Object[]{item});
                        }
                    }, 500);

                    continue;
                }

                RoomTile newTile = room.getLayout().getTile(nextStep.x, nextStep.y);

                if (newTile != null && newTile.state == RoomTileState.OPEN)
                {
                    if (room.getLayout().tileExists(newTile.x, newTile.y))
                    {
                        HabboItem topItem = room.getTopItemAt(newTile.x, newTile.y);

                        if (topItem == null || topItem.getBaseItem().allowWalk())
                        {
                            room.sendComposer(new FloorItemOnRollerComposer(item, null, newTile, newTile.getStackHeight() - item.getZ(), room).compose());
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = getDelay() + "\t";

        if(items != null && !items.isEmpty())
        {
            for (HabboItem item : this.items)
            {
                wiredData += item.getId() + ";";
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        this.items = new THashSet<HabboItem>();
        String[] wiredData = set.getString("wired_data").split("\t");

        if (wiredData.length >= 1)
        {
            this.setDelay(Integer.valueOf(wiredData[0]));
        }
        if (wiredData.length == 2)
        {
            if (wiredData[1].contains(";"))
            {
                for (String s : wiredData[1].split(";"))
                {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp()
    {
        this.items.clear();
        this.setDelay(0);
    }

    @Override
    public WiredEffectType getType()
    {
        return type;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null
               || item instanceof InteractionRoller || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
            {
                items.add(item);
            }
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }
        message.appendBoolean(false);
        message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
        message.appendInt(this.items.size());
        for(HabboItem item : this.items)
            message.appendInt(item.getId());

        message.appendInt(this.getBaseItem().getSpriteId());
        message.appendInt(this.getId());
        message.appendString("");
        message.appendInt(0);
        message.appendInt(0);
        message.appendInt(this.getType().code);
        message.appendInt(this.getDelay());
        message.appendInt(0);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();
        packet.readString();

        this.items.clear();

        int count = packet.readInt();

        for(int i = 0; i < count; i++)
        {
            this.items.add(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt()));
        }

        this.setDelay(packet.readInt());

        return true;
    }
}
