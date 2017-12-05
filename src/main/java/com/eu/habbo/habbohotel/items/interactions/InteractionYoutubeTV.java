package com.eu.habbo.habbohotel.items.interactions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionYoutubeTV extends HabboItem
{
    public InteractionYoutubeTV(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
    }

    public InteractionYoutubeTV(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return false;
    }

    @Override
    public boolean isWalkable()
    {
        return false;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {

    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        if(this.getExtradata().length() == 0)
            this.setExtradata("");

        serverMessage.appendInt(1 + (this.isLimited() ? 256 : 0));
        serverMessage.appendInt(1);
        serverMessage.appendString("THUMBNAIL_URL");
        serverMessage.appendString(Emulator.getConfig().getValue("imager.url.youtube").replace("%video%", Emulator.getGameEnvironment().getItemManager().getYoutubeManager().getPreviewImage(this.getBaseItem())));

        super.serializeExtradata(serverMessage);
    }
}