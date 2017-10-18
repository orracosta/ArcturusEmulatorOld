package com.eu.habbo.habbohotel.items.interactions.wired.effects;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionCrackable;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredEffect;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameTimer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.habbohotel.wired.WiredHandler;
import com.eu.habbo.messages.ClientMessage;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WiredEffectToggleFurni extends InteractionWiredEffect
{
    public static final WiredEffectType type = WiredEffectType.TOGGLE_STATE;

    private final THashSet<HabboItem> items;

    public WiredEffectToggleFurni(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        this.items = new THashSet<HabboItem>();
    }

    public WiredEffectToggleFurni(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        this.items = new THashSet<HabboItem>();
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room)
    {
        synchronized (this.items)
        {
            THashSet<HabboItem> items = new THashSet<HabboItem>();

            for (HabboItem item : this.items)
            {
                if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null)
                    items.add(item);
            }

            for (HabboItem item : items)
            {
                this.items.remove(item);
            }

            message.appendBoolean(false);
            message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
            message.appendInt(this.items.size());
            for (HabboItem item : this.items)
            {
                message.appendInt(item.getId());
            }
            message.appendInt(this.getBaseItem().getSpriteId());
            message.appendInt(this.getId());
            message.appendString("");
            message.appendInt(0);
            message.appendInt(0);
            message.appendInt(this.getType().code);
            message.appendInt(this.getDelay());

            if (this.requiresTriggeringUser())
            {
                List<Integer> invalidTriggers = new ArrayList<>();
                room.getRoomSpecialTypes().getTriggers(this.getX(), this.getY()).forEach(new TObjectProcedure<InteractionWiredTrigger>()
                {
                    @Override
                    public boolean execute(InteractionWiredTrigger object)
                    {
                        if (!object.isTriggeredByRoomUnit())
                        {
                            invalidTriggers.add(object.getBaseItem().getSpriteId());
                        }
                        return true;
                    }
                });
                message.appendInt(invalidTriggers.size());
                for (Integer i : invalidTriggers)
                {
                    message.appendInt(i);
                }
            }
            else
            {
                message.appendInt(0);
            }
        }
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient)
    {
        packet.readInt();
        packet.readString();

        synchronized (this.items)
        {
            this.items.clear();

            int count = packet.readInt();

            for (int i = 0; i < count; i++)
            {
                HabboItem item = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(packet.readInt());

                if (item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile || item instanceof InteractionCrackable)
                    continue;

                this.items.add(item);
            }
        }

        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff)
    {
        synchronized (this.items)
        {
            Habbo habbo = room.getHabbo(roomUnit);

            HabboItem triggerItem = null;

            if (stuff != null && stuff.length > 0)
            {
                if (stuff[0] instanceof HabboItem)
                {
                    triggerItem = (HabboItem) stuff[0];
                }
            }

            THashSet<HabboItem> itemsToRemove = new THashSet<>();
            for (HabboItem item : this.items)
            {
                if (item == null || item.getRoomId() == 0 || item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile)
                {
                    itemsToRemove.add(item);
                    continue;
                }

                if (triggerItem == null && roomUnit == null)// && triggerItem.getId() == item.getId())
                {
                    continue;
                }

                try
                {
                    if (item.getBaseItem().getStateCount() > 1 || item instanceof InteractionGameTimer)
                    {
                        if (item instanceof InteractionGameTimer)
                        {
                            Game game = room.getGame(((InteractionGameTimer)item).getGameType());
                            if (game == null || game.isRunning)
                            {
                                continue;
                            }
                        }

                        item.onClick(habbo != null ? habbo.getClient() : null, room, new Object[]{item.getExtradata().length() == 0 ? 0 : Integer.valueOf(item.getExtradata()), this.getType()});
                    }
                }
                catch (Exception e)
                {
                    Emulator.getLogging().logErrorLine(e);
                }
            }

            this.items.removeAll(itemsToRemove);
        }
        return true;
    }

    @Override
    public String getWiredData()
    {
        String wiredData = this.getDelay() + "\t";

        synchronized (this.items)
        {
            if(items != null && !items.isEmpty())
            {
                for (HabboItem item : this.items)
                {
                    wiredData += item.getId() + ";";
                }
            }
        }

        return wiredData;
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException
    {
        synchronized (this.items)
        {
            this.items.clear();
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

                        if (item instanceof InteractionFreezeBlock || item instanceof InteractionFreezeTile || item instanceof InteractionCrackable)
                            continue;

                        if (item != null)
                            this.items.add(item);
                    }
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
}
