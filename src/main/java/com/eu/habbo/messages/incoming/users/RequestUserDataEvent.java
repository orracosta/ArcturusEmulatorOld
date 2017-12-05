package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.RoomManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.ForwardToRoomComposer;
import com.eu.habbo.messages.outgoing.users.*;

import java.util.ArrayList;

public class RequestUserDataEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
        if (this.client.getHabbo() != null)
        {
            //this.client.sendResponse(new TestComposer());
            /*this.client.sendResponse(new MinimailCountComposer());
            this.client.sendResponse(new MessengerInitComposer(this.client.getHabbo()));
            this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
            this.client.sendResponse(new SessionRightsComposer());*/
            //this.client.sendResponse(new UserDataComposer(this.client.getHabbo()));
            //this.client.sendResponse(new HotelViewComposer());
            //this.client.sendResponse(new UserHomeRoomComposer());
            //this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));

            //this.client.sendResponse(new UserCreditsComposer(this.client.getHabbo()));
            //this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
            //this.client.sendResponse(new FavoriteRoomsCountComposer());
           // this.client.sendResponse(new UserBCLimitsComposer());
            //this.client.sendResponse(new UserAchievementScoreComposer(this.client.getHabbo()));
            //this.client.sendResponse(new UserClothesComposer());
            //this.client.sendResponse(new GenericAlertComposer(Emulator.getTexts().getValue("hotel.alert.message.welcome").replace("%user%", this.client.getHabbo().getHabboInfo().getUsername()), this.client.getHabbo()));


            //******//

            ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();

/*
            messages.add(new MinimailCountComposer().compose());
            messages.add(new FavoriteRoomsCountComposer().compose());
            messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
            messages.add(new SessionRightsComposer().compose());
            messages.add(new DebugConsoleComposer().compose());
            messages.add(new UserCurrencyComposer(this.client.getHabbo()).compose());
            messages.add(new UserDataComposer(this.client.getHabbo()).compose());
            messages.add(new UserPerksComposer().compose());
            messages.add(new BuildersClubExpiredComposer().compose());
            messages.add(new UserBCLimitsComposer().compose());
            messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
            messages.add(new UserClothesComposer().compose());
            messages.add(new UserClubComposer(this.client.getHabbo()).compose());
            messages.add(new MeMenuSettingsComposer().compose());

            if(this.client.getHabbo().hasPermission("acc_supporttool"))
            {
                this.client.sendResponse(new ModToolComposer());
            }

            messages.add(new CatalogUpdatedComposer().compose());
            messages.add(new CatalogModeComposer(0).compose());
            messages.add(new DiscountComposer().compose());
            messages.add(new MarketplaceConfigComposer().compose());
            messages.add(new GiftConfigurationComposer().compose());
            messages.add(new RecyclerLogicComposer().compose());
            //

            //messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
            this.client.sendResponses(messages);
*/

//            messages.add(new DebugConsoleComposer().compose());
//            messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), this.client.getHabbo().getHabboInfo().getHomeRoom()).compose());
//            messages.add(new NewUserIdentityComposer().compose());
//            //messages.add(new UserDataComposer(this.client.getHabbo()).compose());
//            messages.add(new SessionRightsComposer().compose());
//            messages.add(new MinimailCountComposer().compose());
//            messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
//            messages.add(new FriendsComposer(this.client.getHabbo()).compose());
//            messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
//            messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
//            //
//            messages.add(new UserClubComposer(this.client.getHabbo()).compose());
//            messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
//            messages.add(new UserBCLimitsComposer().compose());
//            messages.add(new UserClothesComposer().compose());
//            messages.add(new MeMenuSettingsComposer(this.client.getHabbo()).compose());
//            messages.add(new FavoriteRoomsCountComposer().compose());
            messages.add(new UserDataComposer(this.client.getHabbo()).compose());
            messages.add(new UserPerksComposer(this.client.getHabbo()).compose());

            if(this.client.getHabbo().getHabboInfo().getHomeRoom() != 0)
                messages.add(new ForwardToRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom()).compose());
            else if (RoomManager.HOME_ROOM_ID > 0)
                messages.add(new ForwardToRoomComposer(RoomManager.HOME_ROOM_ID).compose());

            messages.add(new MeMenuSettingsComposer(this.client.getHabbo()).compose());

//            messages.add(new UserPerksComposer().compose());
//            messages.add(new ForwardToRoomComposer(0).compose());
//            if(this.client.getHabbo().hasPermission("acc_supporttool"))
//            {
//                messages.add(new ModToolComposer().compose());
//            }
//
//
//            //
//
//
//
//            //messages.add(new UserDataComposer(this.client.getHabbo()).compose());
//            //messages.add(new UserPerksComposer().compose());
//            //messages.add(new SessionRightsComposer().compose());
//            messages.add(new UserEffectsListComposer().compose());
//            messages.add(new UserCurrencyComposer(this.client.getHabbo()).compose());
//            messages.add(new UserCreditsComposer(this.client.getHabbo()).compose());
//            messages.add(new UserPointsComposer(this.client.getHabbo().getHabboInfo().getPoints(), 0, 5).compose());
//            messages.add(new CatalogModeComposer(0).compose());
//            messages.add(new DiscountComposer().compose());
//            messages.add(new MarketplaceConfigComposer().compose());
//            messages.add(new GiftConfigurationComposer().compose());
//            messages.add(new RecyclerLogicComposer().compose());
//            messages.add(new CatalogUpdatedComposer().compose());
//            messages.add(new NewNavigatorMetaDataComposer().compose());
//            messages.add(new NewNavigatorLiftedRoomsComposer().compose());
//            messages.add(new NewNavigatorCollapsedCategoriesComposer().compose());
//            messages.add(new NewNavigatorSavedSearchesComposer().compose());
//            messages.add(new NewNavigatorEventCategoriesComposer().compose());
//            messages.add(new RoomCategoriesComposer(Emulator.getGameEnvironment().getRoomManager().roomCategoriesForHabbo(this.client.getHabbo())).compose());

            this.client.sendResponses(messages);


            /*this.client.sendResponse(new MinimailCountComposer());
            this.client.sendResponse(new UserPermissionsComposer(this.client.getHabbo()));
            this.client.sendResponse(new SessionRightsComposer());
            this.client.sendResponse(new DebugConsoleComposer());
            this.client.sendResponse(new UserCurrencyComposer(this.client.getHabbo()));
            this.client.sendResponse(new UserDataComposer(this.client.getHabbo()));
            this.client.sendResponse(new UserPerksComposer());
            this.client.sendResponse(new HotelViewDataComposer(",2013-05-08 13:00,gamesmaker;2013-05-11 13:00", ""));
            //this.client.sendResponse(new MessengerInitComposer(this.client.getHabbo()));
            this.client.sendResponse(new FavoriteRoomsCountComposer());
            this.client.sendResponse(new UserBCLimitsComposer());
            this.client.sendResponse(new UserAchievementScoreComposer(this.client.getHabbo()));
            this.client.sendResponse(new UserClothesComposer());*/
        }
        else
        {
            Emulator.getLogging().logDebugLine("Habbo is NULL!");
            this.client.getChannel().close();
        }
    }
}
