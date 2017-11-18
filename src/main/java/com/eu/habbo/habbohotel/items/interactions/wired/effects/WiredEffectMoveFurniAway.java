package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
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
import java.util.List;

public class WiredEffectMoveFurniAway extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.FLEE;

    private THashSet<HabboItem> items = new THashSet<HabboItem>();

    public WiredEffectMoveFurniAway(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public WiredEffectMoveFurniAway(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        for(HabboItem item : this.items)
        {
            if (item.getRoomId() == 0 || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
                items.add(item);
        }

        this.items.removeAll(items);

        for(HabboItem item : this.items)
        {
            RoomTile t = room.getLayout().getTile(item.getX(), item.getY());
            double shortest = 1000.0D;

            Habbo target = null;

            for(Habbo habbo : room.getHabbos())
            {
                if(habbo.getRoomUnit().getCurrentLocation().distance(t) <= shortest)
                {
                    shortest = habbo.getRoomUnit().getCurrentLocation().distance(t);
                    target = habbo;
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

                RoomTile oldTile = room.getLayout().getTile(item.getX(), item.getY());
                RoomTile nextStep = oldTile;

                List<RoomTile> TilesAround = room.getLayout().getTilesAround(room.getLayout().getTile(item.getX(), item.getY()));

                for(int i = 0; i < TilesAround.size(); i++){

                    RoomTile tt = room.getLayout().getTile(TilesAround.get(i).x, TilesAround.get(i).y);

                    if(tt != null && room.getLayout().tileExists(tt.x, tt.y) && tt.state == RoomTileState.OPEN) {
                        Deque<RoomTile> findpath = room.getLayout().findPath(oldTile, tt, false, false);
                        if (!findpath.isEmpty() && findpath != null) {
                            HabboItem topItem = room.getTopItemAt(tt.x, tt.y);

                            if (topItem == null || topItem.getBaseItem().allowWalk()) {
                                if (target.getRoomUnit().getCurrentLocation().distance(tt) > target.getRoomUnit().getCurrentLocation().distance(nextStep)) {
                                    nextStep = findpath.getFirst();
                                }
                            }
                        }
                    }
                }

                if(oldTile != nextStep)
                    room.sendComposer(new FloorItemOnRollerComposer(item, null, nextStep, nextStep.getStackHeight() - item.getZ(), room).compose());
            }
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = this.getDelay() + "\t";

        if(this.items != null && !this.items.isEmpty())
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
                    || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
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
