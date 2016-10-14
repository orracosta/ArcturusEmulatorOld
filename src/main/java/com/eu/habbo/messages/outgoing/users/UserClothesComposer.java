package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.ClothItem;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.ArrayList;

public class UserClothesComposer extends MessageComposer
{
    private final ArrayList<Integer> idList = new ArrayList<Integer>();
    private final ArrayList<String> nameList = new ArrayList<String>();

    public UserClothesComposer(Habbo habbo)
    {
        for (int i : habbo.getHabboInventory().getWardrobeComponent().getClothing())
        {
            ClothItem item = Emulator.getGameEnvironment().getCatalogManager().clothing.get(i);

            if (item != null)
            {
                for (Integer j : item.setId)
                {
                    idList.add(j);
                }

                nameList.add(item.name);
            }
        }
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserClothesComposer);

        this.response.appendInt32(this.idList.size());

        for (Integer id : this.idList)
        {
            this.response.appendInt32(id);
        }

        this.response.appendInt32(this.nameList.size());

        for (String name : this.nameList)
        {
            this.response.appendString(name);
        }

        return this.response;
    }
}
