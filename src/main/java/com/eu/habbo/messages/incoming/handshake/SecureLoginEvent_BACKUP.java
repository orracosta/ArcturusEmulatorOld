package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.friends.FriendsComposer;
import com.eu.habbo.messages.outgoing.users.FavoriteRoomsCountComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.handshake.DebugConsoleComposer;
import com.eu.habbo.messages.outgoing.handshake.SecureLoginOKComposer;
import com.eu.habbo.messages.outgoing.handshake.SessionRightsComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.navigator.*;
import com.eu.habbo.messages.outgoing.unknown.NewUserIdentityComposer;
import com.eu.habbo.messages.outgoing.users.*;
import com.eu.habbo.plugin.events.users.UserLoginEvent;

import java.util.ArrayList;

public class SecureLoginEvent_BACKUP extends MessageHandler
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
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().loadHabbo(sso);
            if(habbo != null)
            {
                habbo.setClient(this.client);
                this.client.setHabbo(habbo);
                this.client.getHabbo().connect();
                //this.client.sendResponse(new DebugConsoleComposer());
                Emulator.getThreading().run(habbo);
                Emulator.getGameEnvironment().getHabboManager().addHabbo(habbo);

                ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();

                /*messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()).compose());
                messages.add(new FriendsComposer(this.client.getHabbo()).compose());
                messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                messages.add(new HotelViewDataComposer(",2013-05-08 13:00,gamesmaker;2013-05-11 13:00", "").compose());
                messages.add(new AddHabboItemComposer(this.client.getHabbo().getInventory().getItemsComponent().getItemsAsValueCollection()).compose());

                for(int i = 0; i < 9; i++)
                {
                    messages.add(new MinimailCountComposer().compose());
                }
                messages.add(new UserEffectsListComposer().compose());

                this.client.sendResponses(messages);*/

                /*messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()).compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                messages.add(new FriendsComposer(this.client.getHabbo()).compose());*/

                messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), 0).compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new UserClubComposer(this.client.getHabbo()).compose());
                messages.add(new DebugConsoleComposer().compose());
                messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
                messages.add(new NewUserIdentityComposer().compose());
                messages.add(new UserPerksComposer(habbo).compose());
                messages.add(new SessionRightsComposer().compose());
                messages.add(new FavoriteRoomsCountComposer(habbo).compose());
                messages.add(new FriendsComposer(this.client.getHabbo()).compose());
                //messages.add(new NewUserIdentityComposer().compose());
                //messages.add(new UserDataComposer(this.client.getHabbo()).compose());
                //messages.add(new SessionRightsComposer().compose());
                //messages.add(new MinimailCountComposer().compose());
                //messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                //messages.add(new FriendsComposer(this.client.getHabbo()).compose());

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
                //this.client.sendResponse(new HotelViewComposer());
                //this.client.sendResponse(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()));
                //this.client.sendResponse(new UserEffectsListComposer());


               /* ServerMessage msg = new MinimailCountComposer().compose();
                for(int i = 0; i < 9; i++)
                {
                    //Not sure why habbo does this. ohwell.
                    this.client.sendResponse(msg);
                }*/
/*
                this.client.sendResponse(new DebugConsoleComposer());
                this.client.sendResponse(new MessengerInitComposer(this.client.getHabbo()));
                this.client.sendResponse(new NewUserIdentityComposer());
                this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
                this.client.sendResponse(new UserPerksComposer());
                this.client.sendResponse(new SessionRightsComposer());


                //this.client.sendResponse(new AddHabboItemComposer(this.client.getHabbo().getInventory().getItemsComponent().getItemsAsValueCollection()));
                this.client.sendResponse(new UserClothesComposer());

                //this.client.sendResponse(new UnknownComposer4());
                //this.client.sendResponse(new UnknownComposer5());
                this.client.sendResponse(new BuildersClubExpiredComposer());
                this.client.sendResponse(new FavoriteRoomsCountComposer());
                this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
                this.client.sendResponse(new UserAchievementScoreComposer(this.client.getHabbo()));
                this.client.sendResponse(new HotelViewDataComposer(",2013-05-08 13:00,gamesmaker;2013-05-11 13:00", ""));*/


                Emulator.getPluginManager().fireEvent(new UserLoginEvent(habbo, this.client.getChannel().localAddress()));

            }
            else
            {
                this.client.sendResponse(new GenericAlertComposer("Can't connect *sadpanda*"));

                this.client.getChannel().close();
            }
        }
    }
}
