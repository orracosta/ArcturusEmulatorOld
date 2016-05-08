package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.TestComposer;
import com.eu.habbo.messages.outgoing.crafting.CraftingRecipeComposer;
import com.eu.habbo.messages.outgoing.friends.FriendsComposer;
import com.eu.habbo.messages.outgoing.friends.MessengerInitComposer;
import com.eu.habbo.messages.outgoing.generic.FavoriteRoomsCountComposer;
import com.eu.habbo.messages.outgoing.generic.MinimailCountComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.handshake.DebugConsoleComposer;
import com.eu.habbo.messages.outgoing.handshake.SecureLoginOKComposer;
import com.eu.habbo.messages.outgoing.handshake.SessionRightsComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewComposer;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewDataComposer;
import com.eu.habbo.messages.outgoing.inventory.AddHabboItemComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryItemsComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.navigator.*;
import com.eu.habbo.messages.outgoing.unknown.NewUserIdentityComposer;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.plugin.events.users.UserLoginEvent;

import java.util.ArrayList;

public class SecureLoginEvent extends MessageHandler
{
    /*************************************************************************************************
     *
             #####   #######        #     # ####### #######        ####### ######  ### #######
             #     # #     #        ##    # #     #    #           #       #     #  #     #
             #     # #     #        # #   # #     #    #           #       #     #  #     #
             #     # #     #        #  #  # #     #    #           #####   #     #  #     #
             #     # #     #        #   # # #     #    #           #       #     #  #     #
             #     # #     #        #    ## #     #    #           #       #     #  #     #
             #####   #######        #     # #######    #           ####### ######  ###    #
     *                                                                                  -The General
     *************************************************************************************************/

    @Override
    public void handle() throws Exception
    {
        if(!Emulator.isReady)
            return;

        String sso = this.packet.readString();

        if(this.client.getHabbo() == null)
        {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().loadHabbo(sso, this.client);
            if(habbo != null)
            {
                habbo.setClient(this.client);
                this.client.setHabbo(habbo);
                this.client.getHabbo().connect();
                Emulator.getThreading().run(habbo);
                Emulator.getGameEnvironment().getHabboManager().addHabbo(habbo);

                ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();
                messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), 0).compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                messages.add(new FriendsComposer(this.client.getHabbo()).compose());
                messages.add(new UserClubComposer(this.client.getHabbo()).compose());
                messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
                messages.add(new NewUserIdentityComposer().compose());
                messages.add(new UserPerksComposer().compose());
                messages.add(new SessionRightsComposer().compose());
                messages.add(new FavoriteRoomsCountComposer().compose());
                messages.add(new UserEffectsListComposer().compose());

                if(this.client.getHabbo().hasPermission("acc_supporttool"))
                {
                    messages.add(new ModToolComposer(this.client.getHabbo()).compose());
                }

                this.client.sendResponses(messages);

                //Hardcoded
                this.client.sendResponse(new NewNavigatorMetaDataComposer());
                this.client.sendResponse(new NewNavigatorLiftedRoomsComposer());
                this.client.sendResponse(new NewNavigatorCollapsedCategoriesComposer());
                this.client.sendResponse(new NewNavigatorSavedSearchesComposer());
                this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
                this.client.sendResponse(new DebugConsoleComposer());
                this.client.sendResponse(new InventoryRefreshComposer());

                Emulator.getPluginManager().fireEvent(new UserLoginEvent(habbo, this.client.getChannel().localAddress()));

            }
            else
            {
                System.out.println("LOGIN FAILED");
                this.client.sendResponse(new GenericAlertComposer("Can't connect *sadpanda*"));

                this.client.getChannel().close();
            }
        }
    }
}
