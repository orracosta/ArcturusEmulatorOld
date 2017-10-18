package com.eu.habbo.messages.incoming.handshake;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.achievements.AchievementListComposer;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterAccountInfoComposer;
import com.eu.habbo.messages.outgoing.gamecenter.GameCenterGameListComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.GenericAlertComposer;
import com.eu.habbo.messages.outgoing.handshake.*;
import com.eu.habbo.messages.outgoing.inventory.InventoryAchievementsComposer;
import com.eu.habbo.messages.outgoing.inventory.UserEffectsListComposer;
import com.eu.habbo.messages.outgoing.modtool.CfhTopicsMessageComposer;
import com.eu.habbo.messages.outgoing.users.FavoriteRoomsCountComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.modtool.ModToolComposer;
import com.eu.habbo.messages.outgoing.navigator.*;
import com.eu.habbo.messages.outgoing.unknown.BuildersClubExpiredComposer;
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

        String sso = this.packet.readString().replace(" ", "");

        if (sso == null || sso.isEmpty())
        {
            this.client.getChannel().close();
        }

        if(this.client.getHabbo() == null)
        {
            if (Emulator.getGameEnvironment().getModToolManager().hasIPBan(this.client.getChannel()))
            {
                this.client.getChannel().close();
                return;
            }

            Habbo habbo = null;
            synchronized (this.client.getChannel())
            {
                habbo = Emulator.getGameEnvironment().getHabboManager().loadHabbo(sso);
            }
            if(habbo != null)
            {
                try
                {
                    habbo.setClient(this.client);
                    this.client.setHabbo(habbo);
                    this.client.getHabbo().connect();

                    if (this.client.getHabbo().getHabboInfo() == null)
                    {
                        Emulator.getGameServer().getGameClientManager().disposeClient(this.client.getChannel());
                        return;
                    }
                    Emulator.getThreading().run(habbo);
                    Emulator.getGameEnvironment().getHabboManager().addHabbo(habbo);
                }
                catch (Exception e)
                {
                    Emulator.getGameServer().getGameClientManager().disposeClient(this.client.getChannel());
                    return;
                }
                ArrayList<ServerMessage> messages = new ArrayList<ServerMessage>();

                messages.add(new SecureLoginOKComposer().compose());
                messages.add(new UserHomeRoomComposer(this.client.getHabbo().getHabboInfo().getHomeRoom(), 0).compose());
                messages.add(new UserEffectsListComposer(habbo).compose());
                messages.add(new UserClothesComposer(this.client.getHabbo()).compose());
                messages.add(new NewUserIdentityComposer().compose());
                messages.add(new UserPermissionsComposer(this.client.getHabbo()).compose());
                messages.add(new SessionRightsComposer().compose());
                messages.add(new SomeConnectionComposer().compose());
                messages.add(new DebugConsoleComposer().compose());
                messages.add(new UserAchievementScoreComposer(this.client.getHabbo()).compose());
                messages.add(new UnknownComposer4().compose());
                messages.add(new UnknownComposer5().compose());
                messages.add(new BuildersClubExpiredComposer().compose());
                messages.add(new CfhTopicsMessageComposer().compose());
                messages.add(new FavoriteRoomsCountComposer(this.client.getHabbo()).compose());
                messages.add(new GameCenterGameListComposer().compose());
                messages.add(new GameCenterAccountInfoComposer(3, 100).compose());
                messages.add(new GameCenterAccountInfoComposer(0, 100).compose());

                //messages.add(new MessengerInitComposer(this.client.getHabbo()).compose());
                //messages.add(new FriendsComposer(this.client.getHabbo()).compose());
                messages.add(new UserClubComposer(this.client.getHabbo()).compose());

                if(this.client.getHabbo().hasPermission("acc_supporttool"))
                {
                    messages.add(new ModToolComposer(this.client.getHabbo()).compose());
                }

                this.client.sendResponses(messages);

                //Hardcoded
                this.client.sendResponse(new NewNavigatorSettingsComposer(this.client.getHabbo().getHabboStats().navigatorWindowSettings));
                this.client.sendResponse(new NewNavigatorMetaDataComposer());
                this.client.sendResponse(new NewNavigatorLiftedRoomsComposer());
                this.client.sendResponse(new NewNavigatorCollapsedCategoriesComposer());
                this.client.sendResponse(new NewNavigatorSavedSearchesComposer());
                this.client.sendResponse(new NewNavigatorEventCategoriesComposer());
                this.client.sendResponse(new InventoryRefreshComposer());
                this.client.sendResponse(new ForumsTestComposer());
                this.client.sendResponse(new InventoryAchievementsComposer());
                this.client.sendResponse(new AchievementListComposer(this.client.getHabbo()));

                Emulator.getPluginManager().fireEvent(new UserLoginEvent(habbo, this.client.getChannel().localAddress()));

                if (Emulator.getConfig().getBoolean("hotel.welcome.alert.enabled"))
                {
                    final Habbo finalHabbo = habbo;
                    Emulator.getThreading().run(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            client.sendResponse(new GenericAlertComposer(HabboManager.WELCOME_MESSAGE.replace("%username%", finalHabbo.getHabboInfo().getUsername()).replace("%user%", finalHabbo.getHabboInfo().getUsername())));
                        }
                    }, Emulator.getConfig().getInt("hotel.welcome.alert.delay", 5000));
                }
            }
            else
            {
                this.client.getChannel().close();
            }
        }
    }
}
