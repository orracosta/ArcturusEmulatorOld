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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WiredEffectChangeFurniDirection extends InteractionWiredEffect {
    public static final WiredEffectType type = WiredEffectType.MOVE_DIRECTION;
    private final List<HabboItem> items = new ArrayList<>();
    private int direction;
    private int spacing = 1;
    public static double MAXIMUM_STEP_HEIGHT = 1.1;
    public static boolean ALLOW_FALLING = true;
    private Map<Integer, Integer> indexOffset = new LinkedHashMap<>();

    public WiredEffectChangeFurniDirection(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public WiredEffectChangeFurniDirection(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean saveData(ClientMessage packet, GameClient gameClient) {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        if (room == null)
            return false;

        this.items.clear();
        this.indexOffset.clear();

        packet.readInt();

        this.direction = packet.readInt();
        this.spacing = packet.readInt();
        packet.readString();

        int count = packet.readInt();
        for (int i = 0; i < count; i++) {
            HabboItem item = room.getHabboItem(packet.readInt());
            if (item != null) {
                item.setRotation(this.direction);
            }
            this.items.add(item);
        }

        this.setDelay(packet.readInt());

        return true;
    }

    @Override
    public WiredEffectType getType() {
        return type;
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        List<HabboItem> items = new ArrayList<HabboItem>();

        synchronized (this.items) {
            for (HabboItem item : this.items)
            {
                if (item.getRoomId() == 0 || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile)
                    items.add(item);
            }

            this.items.removeAll(items);

            if (this.items.isEmpty())
                return false;

            if (stuff != null && stuff.length > 0) {
                synchronized (this.items) {
                    for (HabboItem item : this.items) {
                        if (item != null && item.getBaseItem() != null) {
                            int indexOffset = 0;

                            if (!this.indexOffset.containsKey(item.getId())) {
                                this.indexOffset.put(item.getId(), indexOffset);
                            } else {
                                indexOffset = this.indexOffset.get(item.getId());
                            }

                            RoomTile objectTile = room.getLayout().getTile(item.getX(), item.getY());

                            if (objectTile != null) {
                                // habbo colide
                                RoomTile t = room.getLayout().getTile(item.getX(), item.getY());

                                double shortest = 1000.0D;

                                Habbo target = null;

                                for (Habbo habbo : room.getHabbos()) {
                                    RoomTile h = habbo.getRoomUnit().getCurrentLocation();

                                    double distance = t.distance(h);
                                    if (distance <= shortest && !room.getLayout().findPath(t, h, false, true).isEmpty()) {
                                        target = habbo;
                                        shortest = distance;
                                    }
                                }

                                if(room.getLayout().getTile(item.getX(), item.getY()) == null)
                                    continue;

                                THashSet<RoomTile> refreshTiles = room.getLayout().getTilesAt(room.getLayout().getTile(item.getX(), item.getY()), item.getBaseItem().getWidth(), item.getBaseItem().getLength(), item.getRotation());

                                RoomTile tile = room.getLayout().getTileInFront(objectTile, item.getRotation(), indexOffset);

                                double height = item.getZ();

                                if(tile != null) {
                                    height = (room.getStackHeight(tile.x, tile.y, false) - objectTile.z);
                                }

                                if (tile != null && target != null) {
                                    if ((target.getRoomUnit().getX() == tile.x && target.getRoomUnit().getY() == tile.y)) {
                                        final Habbo finalTarget = target;
                                        Emulator.getThreading().run(new Runnable() {
                                            @Override
                                            public void run() {
                                                WiredHandler.handle(WiredTriggerType.COLLISION, finalTarget.getRoomUnit(), room, new Object[]{item});
                                            }
                                        }, 500);

                                        continue;
                                    }
                                }

                                if (tile == null || !tile.isWalkable() || tile.state == RoomTileState.BLOCKED || room.hasHabbosAt(tile.x, tile.y) || room.getLayout().findPath(objectTile, tile, false, true).isEmpty()
                                        || (!ALLOW_FALLING && height < -MAXIMUM_STEP_HEIGHT) || (height > MAXIMUM_STEP_HEIGHT)) {
                                    switch (this.spacing) {
                                        case 1:
                                            item.setRotation(item.getRotation() + 1);
                                            break;
                                        case 2:
                                            item.setRotation(item.getRotation() + 2);
                                            break;
                                        case 3:
                                            item.setRotation(item.getRotation() - 1);
                                            break;
                                        case 4:
                                            item.setRotation(item.getRotation() - 2);
                                            break;
                                        case 5:
                                            if (item.getRotation() == 0) {
                                                item.setRotation(4);
                                                continue;
                                            }
                                            if (item.getRotation() == 1) {
                                                item.setRotation(5);
                                                continue;
                                            }
                                            if (item.getRotation() == 2) {
                                                item.setRotation(6);
                                                continue;
                                            }
                                            if (item.getRotation() == 3) {
                                                item.setRotation(7);
                                                continue;
                                            }
                                            if (item.getRotation() == 4) {
                                                item.setRotation(0);
                                                continue;
                                            }
                                            if (item.getRotation() == 5) {
                                                item.setRotation(1);
                                                continue;
                                            }
                                            if (item.getRotation() == 6) {
                                                item.setRotation(2);
                                                continue;
                                            }
                                            if (item.getRotation() == 7) {
                                                item.setRotation(3);
                                                continue;
                                            }
                                            break;
                                        case 6:
                                            item.setRotation(Emulator.getRandom().nextInt(7));
                                            break;
                                    }

                                    if (item.getRotation() > 7) {
                                        item.setRotation(0);
                                    }

                                    if (item.getRotation() < 0) {
                                        item.setRotation(7);
                                    }
                                } else {
                                    room.sendComposer(new FloorItemOnRollerComposer((HabboItem) item, null, tile, tile.getStackHeight() - ((HabboItem) item).getZ(), room).compose());
                                    refreshTiles.addAll(room.getLayout().getTilesAt(room.getLayout().getTile(((HabboItem) item).getX(), ((HabboItem) item).getY()), ((HabboItem) item).getBaseItem().getWidth(), ((HabboItem) item).getBaseItem().getLength(), ((HabboItem) item).getRotation()));
                                    room.updateTiles(refreshTiles);
                                    room.updateTile(objectTile);
                                    this.indexOffset.put(item.getId(), indexOffset);
                                    switch (item.getRotation()) {
                                        case 1:
                                            item.setRotation(2);
                                            break;
                                        case 3:
                                            item.setRotation(4);
                                            break;
                                        case 5:
                                            item.setRotation(6);
                                            break;
                                        case 7:
                                            item.setRotation(0);
                                            break;
                                    }
                                }
                            }
                        }
                        //}
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String getWiredData() {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        this.items.remove(null);
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId());

        for(HabboItem item : this.items)
        {
            if(item.getRoomId() != this.getRoomId() || (room != null && room.getHabboItem(item.getId()) == null)
                    || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
            {
                items.add(item);
            }
        }

        for(HabboItem item : items)
        {
            this.items.remove(item);
        }

        String data = this.direction + "\t" + this.spacing + "\t" + this.getDelay() + "\t";

        for (HabboItem item : this.items) {
            data += item.getId() + "\r";
        }

        return data;
    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {
        THashSet<HabboItem> items = new THashSet<HabboItem>();

        synchronized (this.items) {
            for (HabboItem item : this.items)
            {
                if (item.getRoomId() != this.getRoomId() || Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()).getHabboItem(item.getId()) == null
                        || item instanceof InteractionFreezeTile || item instanceof InteractionBattleBanzaiTile || item instanceof InteractionStackHelper)
                {
                    items.add(item);
                }
            }

            for (HabboItem item : items)
            {
                this.items.remove(item);
            }

            message.appendBoolean(false);
            message.appendInt(WiredHandler.MAXIMUM_FURNI_SELECTION);
            message.appendInt(this.items.size());
            for (HabboItem item : this.items)
                message.appendInt(item.getId());
            message.appendInt(this.getBaseItem().getSpriteId());
            message.appendInt(this.getId());
            message.appendString("");
            message.appendInt(2);
            message.appendInt(this.direction);
            message.appendInt(this.spacing);
            message.appendInt(0);
            message.appendInt(this.getType().code);
            message.appendInt(this.getDelay());
            message.appendInt(0);
        }
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {
        synchronized (this.items) {
            this.items.clear();

            String[] data = set.getString("wired_data").split("\t");

            if (data.length == 4) {
                try {
                    this.direction = Integer.valueOf(data[0]);
                    this.spacing = Integer.valueOf(data[1]);
                    this.setDelay(Integer.valueOf(data[2]));
                } catch (Exception e) {
                }

                for (String s : data[3].split("\r")) {
                    HabboItem item = room.getHabboItem(Integer.valueOf(s));

                    if (item != null)
                        this.items.add(item);
                }
            }
        }
    }

    @Override
    public void onPickUp() {
        this.setDelay(0);
        this.items.clear();
        this.direction = 0;
        this.spacing = 0;
        this.indexOffset.clear();
    }
}