package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.catalog.CatalogPageLayouts;
import com.eu.habbo.habbohotel.catalog.layouts.RoomBundleLayout;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserWhisperComposer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomBundleCommand extends Command
{
    public RoomBundleCommand()
    {
        super("cmd_bundle", Emulator.getTexts().getValue("commands.keys.cmd_bundle").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        int parentId;
        int credits;
        int points;
        int pointsType;

        if(params.length < 5)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_bundle.missing_params"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        if(Emulator.getGameEnvironment().getCatalogManager().getCatalogPage("room_bundle_" + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId()) != null)
        {
            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.error.cmd_bundle.duplicate"), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
            return true;
        }

        parentId = Integer.valueOf(params[1]);
        credits = Integer.valueOf(params[2]);
        points = Integer.valueOf(params[3]);
        pointsType = Integer.valueOf(params[4]);

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().createCatalogPage("Room Bundle: " + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getName(), "room_bundle_" + gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId(), 0, CatalogPageLayouts.room_bundle, gameClient.getHabbo().getHabboInfo().getRank(), parentId);

        if(page instanceof RoomBundleLayout)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO catalog_items (page_id, item_ids, catalog_name, cost_credits, cost_points, points_type ) VALUES (?, ?, ?, ?, ?, ?)");
                statement.setInt(1, page.getId());
                statement.setString(2, "");
                statement.setString(3, "room_bundle");
                statement.setInt(4, credits);
                statement.setInt(5, points);
                statement.setInt(6, pointsType);
                statement.execute();
                ResultSet set = statement.getGeneratedKeys();

                if(set.next())
                {
                    PreparedStatement stmt = Emulator.getDatabase().prepare("SELECT * FROM catalog_items WHERE id = ?");
                    stmt.setInt(1, set.getInt(1));
                    ResultSet st = stmt.executeQuery();

                    if(st.next())
                    {
                        page.addItem(new CatalogItem(st));
                    }
                    st.close();
                    stmt.close();
                    stmt.getConnection().close();
                }
                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
            ((RoomBundleLayout)page).loadItems(gameClient.getHabbo().getHabboInfo().getCurrentRoom());

            gameClient.sendResponse(new RoomUserWhisperComposer(new RoomChatMessage(Emulator.getTexts().getValue("commands.succes.cmd_bundle").replace("%id%", page.getId() + ""), gameClient.getHabbo(), gameClient.getHabbo(), RoomChatMessageBubbles.ALERT)));
        }

        return true;
    }
}
