package com.eu.habbo.habbohotel.catalog.layouts;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.bots.Bot;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class RoomBundleLayout extends SingleBundle
{
    public Room room;
    private int lastUpdate = 0;

    public RoomBundleLayout(ResultSet set) throws SQLException
    {
        super(set);
    }

    @Override
    public TIntObjectMap<CatalogItem> getCatalogItems()
    {
        if(Emulator.getIntUnixTimestamp() - this.lastUpdate < 120)
        {
            this.lastUpdate = Emulator.getIntUnixTimestamp();
            return super.getCatalogItems();
        }

        if(this.room == null)
        {
            this.room = Emulator.getGameEnvironment().getRoomManager().loadRoom(Integer.valueOf(this.getPageName().replace("room_bundle_", "")));

            if(this.room != null)
                this.room.preventUnloading = true;
        }

        if(this.room == null)
        {
            return super.getCatalogItems();
        }

        final CatalogItem[] item = {null};

        super.getCatalogItems().forEachValue(new TObjectProcedure<CatalogItem>()
        {
            @Override
            public boolean execute(CatalogItem object)
            {
                if(object == null)
                    return true;

                item[0] = object;
                return false;
            }
        });

        if(item[0] != null)
        {
            item[0].getBundle().clear();

            THashMap<Item, Integer> items = new THashMap<Item, Integer>();

            for(HabboItem i : this.room.getFloorItems())
            {
                if(!items.contains(i.getBaseItem()))
                {
                    items.put(i.getBaseItem(), 0);
                }

                items.put(i.getBaseItem(), items.get(i.getBaseItem()) + 1);
            }

            for(HabboItem i : this.room.getWallItems())
            {
                if(!items.contains(i.getBaseItem()))
                {
                    items.put(i.getBaseItem(), 0);
                }

                items.put(i.getBaseItem(), items.get(i.getBaseItem()) + 1);
            }

            String data = "";

            for(Map.Entry<Item, Integer> set : items.entrySet())
            {
                data += set.getKey().getId() + ":" + set.getValue() + ";";
            }

            item[0].setItemId(data);
            item[0].loadBundle();
        }

        return super.getCatalogItems();
    }

    public void loadItems(Room room)
    {
        if(this.room != null)
        {
            this.room.preventUnloading = false;
        }

        this.room = room;
        this.room.preventUnloading = true;
        this.getCatalogItems();
    }

    public void buyRoom(Habbo habbo)
    {
        if(this.room == null)
            return;

        this.room.save();

        for(HabboItem item : this.room.getFloorItems())
        {
            item.run();
        }

        for(HabboItem item : this.room.getWallItems())
        {
            item.run();
        }

        this.getCatalogItems();
        int roomId = 0;

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO rooms (owner_id, owner_name, name, description, model, password, state, users_max, category, paper_floor, paper_wall, paper_landscape, thickness_wall, thickness_floor, moodlight_data, override_model)  (SELECT ?, ?, name, description, model, password, state, users_max, category, paper_floor, paper_wall, paper_landscape, thickness_wall, thickness_floor, moodlight_data, override_model FROM rooms WHERE id = ?)");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setString(2, habbo.getHabboInfo().getUsername());
            statement.setInt(3, this.room.getId());
            statement.execute();
            ResultSet set = statement.getGeneratedKeys();

            if(set.next())
            {
                roomId = set.getInt(1);
            }
            set.close();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if(roomId == 0)
            return;

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO items (user_id, room_id, item_id, wall_pos, x, y, z, rot, extra_data, wired_data, limited_data, guild_id) (SELECT ?, ?, item_id, wall_pos, x, y, z, rot, extra_data, wired_data, ?, ? FROM items WHERE room_id = ?)");
            statement.setInt(1, habbo.getHabboInfo().getId());
            statement.setInt(2, roomId);
            statement.setString(3, "0:0");
            statement.setInt(4, 0);
            statement.setInt(5, this.room.getId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if(this.room.hasCustomLayout())
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO room_models_custom (id, name, door_x, door_y, door_dir, heightmap) (SELECT ?, ?, door_x, door_y, door_dir, heightmap FROM room_models_custom WHERE id = ? LIMIT 1)");
                statement.setInt(1, roomId);
                statement.setString(2, "custom_" + roomId);
                statement.setInt(3, this.room.getId());
                statement.execute();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        if(Emulator.getConfig().getBoolean("bundle.bots.enabled"))
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO bots (user_id, room_id, name, motto, figure, gender, x, y, z, chat_lines, chat_auto, chat_random, chat_delay, dance, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setInt(1, habbo.getHabboInfo().getId());
                statement.setInt(2, roomId);

                for(Bot bot : this.room.getCurrentBots().valueCollection())
                {
                    statement.setString(3, bot.getName());
                    statement.setString(4, bot.getMotto());
                    statement.setString(5, bot.getFigure());
                    statement.setString(6, bot.getGender().name());
                    statement.setInt(7, bot.getRoomUnit().getX());
                    statement.setInt(8, bot.getRoomUnit().getY());
                    statement.setDouble(9, bot.getRoomUnit().getZ());
                    String text = "";
                    for(String s : bot.getChatLines())
                    {
                        text += s + "\r";
                    }
                    statement.setString(10, text);
                    statement.setString(11, bot.isChatAuto() ? "1" : "0");
                    statement.setString(12, bot.isChatRandom() ? "1" : "0");
                    statement.setInt(13, bot.getChatDelay());
                    statement.setInt(14, bot.getRoomUnit().getDanceType().getType());
                    statement.setString(15, bot.getType());
                    statement.execute();
                }

                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        Emulator.getLogging().logUserLine(habbo.getHabboInfo().getUsername() + " bought room bundle: " + this.room.getId());

        Emulator.getGameEnvironment().getRoomManager().loadRoom(roomId);

        habbo.getClient().sendResponse(new ForwardToRoomComposer(roomId));
    }
}
