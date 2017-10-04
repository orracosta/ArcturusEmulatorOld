package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Logging;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.*;
import com.eu.habbo.messages.incoming.ambassadors.*;
import com.eu.habbo.messages.incoming.camera.*;
import com.eu.habbo.messages.incoming.catalog.*;
import com.eu.habbo.messages.incoming.catalog.marketplace.*;
import com.eu.habbo.messages.incoming.catalog.recycler.*;
import com.eu.habbo.messages.incoming.crafting.*;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarForceOpenEvent;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarOpenDayEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.*;
import com.eu.habbo.messages.incoming.friends.*;
import com.eu.habbo.messages.incoming.gamecenter.*;
import com.eu.habbo.messages.incoming.guardians.*;
import com.eu.habbo.messages.incoming.guides.*;
import com.eu.habbo.messages.incoming.guilds.*;
import com.eu.habbo.messages.incoming.handshake.*;
import com.eu.habbo.messages.incoming.helper.*;
import com.eu.habbo.messages.incoming.hotelview.*;
import com.eu.habbo.messages.incoming.inventory.*;
import com.eu.habbo.messages.incoming.modtool.*;
import com.eu.habbo.messages.incoming.navigator.*;
import com.eu.habbo.messages.incoming.polls.*;
import com.eu.habbo.messages.incoming.rooms.*;
import com.eu.habbo.messages.incoming.rooms.bots.*;
import com.eu.habbo.messages.incoming.rooms.items.*;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.*;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.*;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestNextVideoEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlayListEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestVideoDataEvent;
import com.eu.habbo.messages.incoming.rooms.pets.*;
import com.eu.habbo.messages.incoming.rooms.promotions.*;
import com.eu.habbo.messages.incoming.rooms.users.*;
import com.eu.habbo.messages.incoming.trading.*;
import com.eu.habbo.messages.incoming.unknown.*;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.*;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketManager
{
    private final THashMap<Integer, Class<? extends MessageHandler>> incoming;
    private final THashMap<Integer, Map.Entry<Object, Method>> callables;
    private static final List<Integer> logList = new ArrayList<Integer>();

    public PacketManager() throws Exception
    {
        this.incoming = new THashMap<Integer, Class<? extends MessageHandler>>();
        this.callables = new THashMap<Integer, Map.Entry<Object, Method>>();

        this.registerHandshake();
        this.registerCatalog();
        this.registerEvent();
        this.registerFriends();
        this.registerNavigator();
        this.registerUsers();
        this.registerHotelview();
        this.registerInventory();
        this.registerRooms();
        this.registerPolls();
        this.registerUnknown();
        this.registerModTool();
        this.registerTrading();
        this.registerGuilds();
        this.registerPets();
        this.registerWired();
        this.registerAchievements();
        this.registerFloorPlanEditor();
        this.registerAmbassadors();
        this.registerGuides();
        this.registerCrafting();
        this.registerCamera();
        this.registerGameCenter();
    }

    public void registerHandler(Integer header, Class<? extends MessageHandler> handler) throws Exception
    {
        if (header < 0)
            return;

        if (this.incoming.containsKey(header))
        {
            throw new Exception("Header already registered. Failed to register " + handler.getName() + " with header " + header);
        }

        this.incoming.putIfAbsent(header, handler);
    }

    public void registerCallable(Integer header, Map.Entry<Object, Method> objectMethodEntry)
    {
        this.callables.put(header, objectMethodEntry);
    }

    public void unregisterCallable(Integer header)
    {
        this.callables.remove(header);
    }

    public void handlePacket(GameClient client, ClientMessage packet)
    {
        if(client == null || Emulator.isShuttingDown)
            return;

        if (client.getHabbo() == null && !(packet.getMessageId() == Incoming.SecureLoginEvent || packet.getMessageId() == Incoming.MachineIDEvent))
            return;

        try
        {
            if(this.isRegistered(packet.getMessageId()))
            {
                if(PacketManager.DEBUG_SHOW_PACKETS)
                    Emulator.getLogging().logPacketLine("[" + Logging.ANSI_CYAN + "CLIENT" + Logging.ANSI_RESET + "][" + packet.getMessageId() + "] => " + packet.getMessageBody());

                if (logList.contains(packet.getMessageId()) && client.getHabbo() != null)
                {
                    System.out.println(("[" + Logging.ANSI_CYAN + "CLIENT" + Logging.ANSI_RESET + "][" + client.getHabbo().getHabboInfo().getUsername() + "][" + packet.getMessageId() + "] => " + packet.getMessageBody()));
                }

                final MessageHandler handler = this.incoming.get(packet.getMessageId()).newInstance();

                handler.client = client;
                handler.packet = packet;

                if (this.callables.containsKey(packet.getMessageId()))
                {
                    Map.Entry<Object, Method> entry = this.callables.get(packet.getMessageId());

                    if (entry.getKey() != null)
                    {
                        entry.getValue().invoke(entry.getKey(), handler);
                    }
                }

                if (!handler.isCancelled)
                {
                    handler.handle();
                }
            }
            else
            {
                if(PacketManager.DEBUG_SHOW_PACKETS)
                    Emulator.getLogging().logPacketLine("[" + Logging.ANSI_CYAN + "CLIENT" + Logging.ANSI_RESET + "][" + Logging.ANSI_RED + "UNDEFINED" + Logging.ANSI_RESET + "][" + packet.getMessageId() + "] => " + packet.getMessageBody());
            }
        }
        catch(Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
    }

    boolean isRegistered(int header)
    {
        return this.incoming.containsKey(header);
    }

    private void registerAmbassadors() throws Exception
    {
        this.registerHandler(Incoming.AmbassadorAlertCommandEvent,              AmbassadorAlertCommandEvent.class);
        this.registerHandler(Incoming.AmbassadorVisitCommandEvent,              AmbassadorVisitCommandEvent.class);
    }

    private void registerCatalog() throws Exception
    {
        this.registerHandler(Incoming.RequestRecylerLogicEvent,                 RequestRecyclerLogicEvent.class);
        this.registerHandler(Incoming.RequestDiscountEvent,                     RequestDiscountEvent.class);
        this.registerHandler(Incoming.RequestGiftConfigurationEvent,            RequestGiftConfigurationEvent.class);
        this.registerHandler(Incoming.GetMarketplaceConfigEvent,                RequestMarketplaceConfigEvent.class);
        this.registerHandler(Incoming.RequestCatalogModeEvent,                  RequestCatalogModeEvent.class);
        this.registerHandler(Incoming.RequestCatalogIndexEvent,                 RequestCatalogIndexEvent.class);
        this.registerHandler(Incoming.RequestCatalogPageEvent,                  RequestCatalogPageEvent.class);
        this.registerHandler(Incoming.CatalogBuyItemAsGiftEvent,                CatalogBuyItemAsGiftEvent.class);
        this.registerHandler(Incoming.CatalogBuyItemEvent,                      CatalogBuyItemEvent.class);
        this.registerHandler(Incoming.RedeemVoucherEvent,                       RedeemVoucherEvent.class);
        this.registerHandler(Incoming.ReloadRecyclerEvent,                      ReloadRecyclerEvent.class);
        this.registerHandler(Incoming.RecycleEvent,                             RecycleEvent.class);
        this.registerHandler(Incoming.OpenRecycleBoxEvent,                      OpenRecycleBoxEvent.class);
        this.registerHandler(Incoming.RequestOwnItemsEvent,                     RequestOwnItemsEvent.class);
        this.registerHandler(Incoming.TakeBackItemEvent,                        TakeBackItemEvent.class);
        this.registerHandler(Incoming.RequestOffersEvent,                       RequestOffersEvent.class);
        this.registerHandler(Incoming.RequestItemInfoEvent,                     RequestItemInfoEvent.class);
        this.registerHandler(Incoming.BuyItemEvent,                             BuyItemEvent.class);
        this.registerHandler(Incoming.RequestSellItemEvent,                     RequestSellItemEvent.class);
        this.registerHandler(Incoming.SellItemEvent,                            SellItemEvent.class);
        this.registerHandler(Incoming.RequestCreditsEvent,                      RequestCreditsEvent.class);
        this.registerHandler(Incoming.RequestPetBreedsEvent,                    RequestPetBreedsEvent.class);
        this.registerHandler(Incoming.CheckPetNameEvent,                        CheckPetNameEvent.class);
        this.registerHandler(Incoming.GetClubDataEvent,                         RequestClubDataEvent.class);
        this.registerHandler(Incoming.RequestClubGiftsEvent,                    RequestClubGiftsEvent.class);
        this.registerHandler(Incoming.CatalogSearchedItemEvent,                 CatalogSearchedItemEvent.class);
    }

    private void registerEvent() throws Exception
    {
        this.registerHandler(Incoming.AdventCalendarOpenDayEvent,               AdventCalendarOpenDayEvent.class);
        this.registerHandler(Incoming.AdventCalendarForceOpenEvent,             AdventCalendarForceOpenEvent.class);
    }

    private void registerHandshake() throws Exception
    {
        this.registerHandler(Incoming.ReleaseVersionEvent,                      ReleaseVersionEvent.class);
        this.registerHandler(Incoming.GenerateSecretKeyEvent,                   GenerateSecretKeyEvent.class);
        this.registerHandler(Incoming.RequestBannerToken,                       RequestBannerToken.class);
        this.registerHandler(Incoming.SecureLoginEvent,                         SecureLoginEvent.class);
        this.registerHandler(Incoming.MachineIDEvent,                           MachineIDEvent.class);
        this.registerHandler(Incoming.UsernameEvent,                            UsernameEvent.class);
        this.registerHandler(Incoming.PingEvent,                                PingEvent.class);
    }

    private void registerFriends() throws Exception
    {
        this.registerHandler(Incoming.RequestFriendsEvent,                      RequestFriendsEvent.class);
        this.registerHandler(Incoming.ChangeRelationEvent,                      ChangeRelationEvent.class);
        this.registerHandler(Incoming.RemoveFriendEvent,                        RemoveFriendEvent.class);
        this.registerHandler(Incoming.SearchUserEvent,                          SearchUserEvent.class);
        this.registerHandler(Incoming.FriendRequestEvent,                       FriendRequestEvent.class);
        this.registerHandler(Incoming.AcceptFriendRequest,                      AcceptFriendRequestEvent.class);
        this.registerHandler(Incoming.DeclineFriendRequest,                     DeclineFriendRequestEvent.class);
        this.registerHandler(Incoming.FriendPrivateMessageEvent,                FriendPrivateMessageEvent.class);
        this.registerHandler(Incoming.RequestFriendRequestEvent,                RequestFriendRequestsEvent.class);
        this.registerHandler(Incoming.StalkFriendEvent,                         StalkFriendEvent.class);
        this.registerHandler(Incoming.RequestInitFriendsEvent,                  RequestInitFriendsEvent.class);
        this.registerHandler(Incoming.FindNewFriendsEvent,                      FindNewFriendsEvent.class);
        this.registerHandler(Incoming.InviteFriendsEvent,                       InviteFriendsEvent.class);
    }

    private void registerUsers() throws Exception
    {
        this.registerHandler(Incoming.RequestUserDataEvent,                     RequestUserDataEvent.class);
        this.registerHandler(Incoming.RequestUserCreditsEvent,                  RequestUserCreditsEvent.class);
        this.registerHandler(Incoming.RequestUserClubEvent,                     RequestUserClubEvent.class);
        this.registerHandler(Incoming.RequestMeMenuSettingsEvent,               RequestMeMenuSettingsEvent.class);
        this.registerHandler(Incoming.RequestUserCitizinShipEvent,              RequestUserCitizinShipEvent.class);
        this.registerHandler(Incoming.RequestUserProfileEvent,                  RequestUserProfileEvent.class);
        this.registerHandler(Incoming.RequestProfileFriendsEvent,               RequestProfileFriendsEvent.class);
        this.registerHandler(Incoming.RequestUserWardrobeEvent,                 RequestUserWardrobeEvent.class);
        this.registerHandler(Incoming.SaveWardrobeEvent,                        SaveWardrobeEvent.class);
        this.registerHandler(Incoming.SaveMottoEvent,                           SaveMottoEvent.class);
        this.registerHandler(Incoming.UserSaveLookEvent,                        UserSaveLookEvent.class);
        this.registerHandler(Incoming.UserWearBadgeEvent,                       UserWearBadgeEvent.class);
        this.registerHandler(Incoming.RequestWearingBadgesEvent,                RequestWearingBadgesEvent.class);
        this.registerHandler(Incoming.SaveUserVolumesEvent,                     SaveUserVolumesEvent.class);
        this.registerHandler(Incoming.SaveBlockCameraFollowEvent,               SaveBlockCameraFollowEvent.class);
        this.registerHandler(Incoming.SaveIgnoreRoomInvitesEvent,               SaveIgnoreRoomInvitesEvent.class);
        this.registerHandler(Incoming.SavePreferOldChatEvent,                   SavePreferOldChatEvent.class);
        this.registerHandler(Incoming.ActivateEffectEvent,                      ActivateEffectEvent.class);
        this.registerHandler(Incoming.EnableEffectEvent,                        EnableEffectEvent.class);
        this.registerHandler(Incoming.UserActivityEvent,                        UserActivityEvent.class);
        this.registerHandler(Incoming.UserNuxEvent,                             UserNuxEvent.class);
        this.registerHandler(Incoming.PickNewUserGiftEvent,                     PickNewUserGiftEvent.class);
        this.registerHandler(Incoming.ChangeNameCheckUsernameEvent,             ChangeNameCheckUsernameEvent.class);
        this.registerHandler(Incoming.ConfirmChangeNameEvent,                   ConfirmChangeNameEvent.class);
    }

    private void registerNavigator() throws Exception
    {
        this.registerHandler(Incoming.RequestRoomCategoriesEvent,               RequestRoomCategoriesEvent.class);
        this.registerHandler(Incoming.RequestPopularRoomsEvent,                 RequestPopularRoomsEvent.class);
        this.registerHandler(Incoming.RequestHighestScoreRoomsEvent,            RequestHighestScoreRoomsEvent.class);
        this.registerHandler(Incoming.RequestMyRoomsEvent,                      RequestMyRoomsEvent.class);
        this.registerHandler(Incoming.RequestCanCreateRoomEvent,                RequestCanCreateRoomEvent.class);
        this.registerHandler(Incoming.RequestPromotedRoomsEvent,                RequestPromotedRoomsEvent.class);
        this.registerHandler(Incoming.RequestCreateRoomEvent,                   RequestCreateRoomEvent.class);
        this.registerHandler(Incoming.RequestTagsEvent,                         RequestTagsEvent.class);
        this.registerHandler(Incoming.SearchRoomsByTagEvent,                    SearchRoomsByTagEvent.class);
        this.registerHandler(Incoming.SearchRoomsEvent,                         SearchRoomsEvent.class);
        this.registerHandler(Incoming.SearchRoomsFriendsNowEvent,               SearchRoomsFriendsNowEvent.class);
        this.registerHandler(Incoming.SearchRoomsFriendsOwnEvent,               SearchRoomsFriendsOwnEvent.class);
        this.registerHandler(Incoming.SearchRoomsWithRightsEvent,               SearchRoomsWithRightsEvent.class);
        this.registerHandler(Incoming.SearchRoomsInGroupEvent,                  SearchRoomsInGroupEvent.class);
        this.registerHandler(Incoming.SearchRoomsMyFavoriteEvent,               SearchRoomsMyFavouriteEvent.class);
        this.registerHandler(Incoming.SearchRoomsVisitedEvent,                  SearchRoomsVisitedEvent.class);
        this.registerHandler(Incoming.RequestNewNavigatorDataEvent,             RequestNewNavigatorDataEvent.class);
        this.registerHandler(Incoming.RequestNewNavigatorRoomsEvent,            RequestNewNavigatorRoomsEvent.class);
        this.registerHandler(Incoming.NewNavigatorActionEvent,                  NewNavigatorActionEvent.class);
        this.registerHandler(Incoming.RequestNavigatorSettingsEvent,            RequestNavigatorSettingsEvent.class);
        this.registerHandler(Incoming.SaveWindowSettingsEvent,                  SaveWindowSettingsEvent.class);
        this.registerHandler(Incoming.RequestDeleteRoomEvent,                   RequestDeleteRoomEvent.class);
        this.registerHandler(Incoming.NavigatorCategoryListModeEvent,           NavigatorCategoryListModeEvent.class);
        this.registerHandler(Incoming.NavigatorCollapseCategoryEvent,           NavigatorCollapseCategoryEvent.class);
        this.registerHandler(Incoming.NavigatorUncollapseCategoryEvent,         NavigatorUncollapseCategoryEvent.class);
    }

    private void registerHotelview() throws Exception
    {
        this.registerHandler(Incoming.HotelViewEvent,                           HotelViewEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBonusRareEvent,           HotelViewRequestBonusRareEvent.class);
        this.registerHandler(Incoming.RequestNewsListEvent,                     RequestNewsListEvent.class);
        this.registerHandler(Incoming.HotelViewDataEvent,                       HotelViewDataEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBadgeRewardEvent,         HotelViewRequestBadgeRewardEvent.class);
        this.registerHandler(Incoming.HotelViewClaimBadgeRewardEvent,           HotelViewClaimBadgeRewardEvent.class);
    }

    private void registerInventory() throws Exception
    {
        this.registerHandler(Incoming.TestInventoryEvent,                       TestInventoryEvent.class);
        this.registerHandler(Incoming.RequestInventoryBadgesEvent,              RequestInventoryBadgesEvent.class);
        this.registerHandler(Incoming.RequestInventoryBotsEvent,                RequestInventoryBotsEvent.class);
        this.registerHandler(Incoming.RequestInventoryItemsEvent,               RequestInventoryItemsEvent.class);
        this.registerHandler(Incoming.RequestInventoryPetsEvent,                RequestInventoryPetsEvent.class);
    }

    void registerRooms() throws Exception
    {
        this.registerHandler(Incoming.RequestRoomLoadEvent,                     RequestRoomLoadEvent.class);
        this.registerHandler(Incoming.RequestHeightmapEvent,                    RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.RequestRoomHeightmapEvent,                RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.RoomVoteEvent,                            RoomVoteEvent.class);
        this.registerHandler(Incoming.RequestRoomDataEvent,                     RequestRoomDataEvent.class);
        this.registerHandler(Incoming.RoomSettingsSaveEvent,                    RoomSettingsSaveEvent.class);
        this.registerHandler(Incoming.RoomPlaceItemEvent,                       RoomPlaceItemEvent.class);
        this.registerHandler(Incoming.RotateMoveItemEvent,                      RotateMoveItemEvent.class);
        this.registerHandler(Incoming.MoveWallItemEvent,                        MoveWallItemEvent.class);
        this.registerHandler(Incoming.RoomPickupItemEvent,                      RoomPickupItemEvent.class);
        this.registerHandler(Incoming.RoomPlacePaintEvent,                      RoomPlacePaintEvent.class);
        this.registerHandler(Incoming.RoomUserStartTypingEvent,                 RoomUserStartTypingEvent.class);
        this.registerHandler(Incoming.RoomUserStopTypingEvent,                  RoomUserStopTypingEvent.class);
        this.registerHandler(Incoming.ToggleFloorItemEvent,                     ToggleFloorItemEvent.class);
        this.registerHandler(Incoming.ToggleWallItemEvent,                      ToggleWallItemEvent.class);
        this.registerHandler(Incoming.RoomBackgroundEvent,                      RoomBackgroundEvent.class);
        this.registerHandler(Incoming.MannequinSaveNameEvent,                   MannequinSaveNameEvent.class);
        this.registerHandler(Incoming.MannequinSaveLookEvent,                   MannequinSaveLookEvent.class);
        this.registerHandler(Incoming.FootballGateSaveLookEvent,                FootballGateSaveLookEvent.class);
        this.registerHandler(Incoming.AdvertisingSaveEvent,                     AdvertisingSaveEvent.class);
        this.registerHandler(Incoming.RequestRoomSettingsEvent,                 RequestRoomSettingsEvent.class);
        this.registerHandler(Incoming.MoodLightSettingsEvent,                   MoodLightSettingsEvent.class);
        this.registerHandler(Incoming.MoodLightTurnOnEvent,                     MoodLightTurnOnEvent.class);
        this.registerHandler(Incoming.RoomUserDropHandItemEvent,                RoomUserDropHandItemEvent.class);
        this.registerHandler(Incoming.RoomUserLookAtPoint,                      RoomUserLookAtPoint.class);
        this.registerHandler(Incoming.RoomUserTalkEvent,                        RoomUserTalkEvent.class);
        this.registerHandler(Incoming.RoomUserShoutEvent,                       RoomUserShoutEvent.class);
        this.registerHandler(Incoming.RoomUserWhisperEvent,                     RoomUserWhisperEvent.class);
        this.registerHandler(Incoming.RoomUserActionEvent,                      RoomUserActionEvent.class);
        this.registerHandler(Incoming.RoomUserSitEvent,                         RoomUserSitEvent.class);
        this.registerHandler(Incoming.RoomUserDanceEvent,                       RoomUserDanceEvent.class);
        this.registerHandler(Incoming.RoomUserSignEvent,                        RoomUserSignEvent.class);
        this.registerHandler(Incoming.RoomUserWalkEvent,                        RoomUserWalkEvent.class);
        this.registerHandler(Incoming.RoomUserGiveRespectEvent,                 RoomUserGiveRespectEvent.class);
        this.registerHandler(Incoming.RoomUserGiveRightsEvent,                  RoomUserGiveRightsEvent.class);
        this.registerHandler(Incoming.RoomRemoveRightsEvent,                    RoomRemoveRightsEvent.class);
        this.registerHandler(Incoming.RequestRoomRightsEvent,                   RequestRoomRightsEvent.class);
        this.registerHandler(Incoming.RoomRemoveAllRightsEvent,                 RoomRemoveAllRightsEvent.class);
        this.registerHandler(Incoming.RoomUserRemoveRightsEvent,                RoomUserRemoveRightsEvent.class);
        this.registerHandler(Incoming.BotPlaceEvent,                            BotPlaceEvent.class);
        this.registerHandler(Incoming.BotPickupEvent,                           BotPickupEvent.class);
        this.registerHandler(Incoming.BotSaveSettingsEvent,                     BotSaveSettingsEvent.class);
        this.registerHandler(Incoming.BotSettingsEvent,                         BotSettingsEvent.class);
        this.registerHandler(Incoming.TriggerDiceEvent,                         TriggerDiceEvent.class);
        this.registerHandler(Incoming.CloseDiceEvent,                           CloseDiceEvent.class);
        this.registerHandler(Incoming.TriggerColorWheelEvent,                   TriggerColorWheelEvent.class);
        this.registerHandler(Incoming.RedeemItemEvent,                          RedeemItemEvent.class);
        this.registerHandler(Incoming.PetPlaceEvent,                            PetPlaceEvent.class);
        this.registerHandler(Incoming.RoomUserKickEvent,                        RoomUserKickEvent.class);
        this.registerHandler(Incoming.SetStackHelperHeightEvent,                SetStackHelperHeightEvent.class);
        this.registerHandler(Incoming.TriggerOneWayGateEvent,                   TriggerOneWayGateEvent.class);
        this.registerHandler(Incoming.HandleDoorbellEvent,                      HandleDoorbellEvent.class);
        this.registerHandler(Incoming.RedeemClothingEvent,                      RedeemClothingEvent.class);
        this.registerHandler(Incoming.PostItPlaceEvent,                         PostItPlaceEvent.class);
        this.registerHandler(Incoming.PostItRequestDataEvent,                   PostItRequestDataEvent.class);
        this.registerHandler(Incoming.PostItSaveDataEvent,                      PostItSaveDataEvent.class);
        this.registerHandler(Incoming.PostItDeleteEvent,                        PostItDeleteEvent.class);
        this.registerHandler(Incoming.MoodLightSaveSettingsEvent,               MoodLightSaveSettingsEvent.class);
        this.registerHandler(Incoming.RentSpaceEvent,                           RentSpaceEvent.class);
        this.registerHandler(Incoming.RentSpaceCancelEvent,                     RentSpaceCancelEvent.class);
        this.registerHandler(Incoming.SetHomeRoomEvent,                         SetHomeRoomEvent.class);
        this.registerHandler(Incoming.RoomUserGiveHandItemEvent,                RoomUserGiveHandItemEvent.class);
        this.registerHandler(Incoming.RoomMuteEvent,                            RoomMuteEvent.class);
        this.registerHandler(Incoming.RequestRoomWordFilterEvent,               RequestRoomWordFilterEvent.class);
        this.registerHandler(Incoming.RoomWordFilterModifyEvent,                RoomWordFilterModifyEvent.class);
        this.registerHandler(Incoming.RoomStaffPickEvent,                       RoomStaffPickEvent.class);
        this.registerHandler(Incoming.RoomRequestBannedUsersEvent,              RoomRequestBannedUsersEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestTrackCodeEvent,             JukeBoxRequestTrackCodeEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestTrackDataEvent,             JukeBoxRequestTrackDataEvent.class);
        this.registerHandler(Incoming.JukeBoxAddSoundTrackEvent,                JukeBoxAddSoundTrackEvent.class);
        this.registerHandler(Incoming.JukeBoxRemoveSoundTrackEvent,             JukeBoxRemoveSoundTrackEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestPlayListEvent,              JukeBoxRequestPlayListEvent.class);
        this.registerHandler(Incoming.JukeBoxEventOne,                          JukeBoxEventOne.class);
        this.registerHandler(Incoming.JukeBoxEventTwo,                          JukeBoxEventTwo.class);
        this.registerHandler(Incoming.SavePostItStickyPoleEvent,                SavePostItStickyPoleEvent.class);
        this.registerHandler(Incoming.RequestPromotionRoomsEvent,               RequestPromotionRoomsEvent.class);
        this.registerHandler(Incoming.BuyRoomPromotionEvent,                    BuyRoomPromotionEvent.class);
        this.registerHandler(Incoming.EditRoomPromotionMessageEvent,            UpdateRoomPromotionEvent.class);
        this.registerHandler(Incoming.IgnoreRoomUserEvent,                      IgnoreRoomUserEvent.class);
        this.registerHandler(Incoming.UnIgnoreRoomUserEvent,                    UnIgnoreRoomUserEvent.class);
        this.registerHandler(Incoming.RoomUserMuteEvent,                        RoomUserMuteEvent.class);
        this.registerHandler(Incoming.RoomUserBanEvent,                         RoomUserBanEvent.class);
        this.registerHandler(Incoming.UnbanRoomUserEvent,                       UnbanRoomUserEvent.class);
        this.registerHandler(Incoming.RequestRoomUserTagsEvent,                 RequestRoomUserTagsEvent.class);
        this.registerHandler(Incoming.YoutubeRequestPlayListEvent,              YoutubeRequestPlayListEvent.class);
        this.registerHandler(Incoming.YoutubeRequestNextVideoEvent,             YoutubeRequestNextVideoEvent.class);
        this.registerHandler(Incoming.YoutubeRequestVideoDataEvent,             YoutubeRequestVideoDataEvent.class);
        this.registerHandler(Incoming.RoomFavoriteEvent,                        RoomFavoriteEvent.class);
    }

    void registerPolls() throws Exception
    {
        this.registerHandler(Incoming.CancelPollEvent,                          CancelPollEvent.class);
        this.registerHandler(Incoming.GetPollDataEvent,                         GetPollDataEvent.class);
        this.registerHandler(Incoming.AnswerPollEvent,                          AnswerPollEvent.class);
    }

    void registerModTool() throws Exception
    {
        this.registerHandler(Incoming.ModToolRequestRoomInfoEvent,              ModToolRequestRoomInfoEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomChatlogEvent,           ModToolRequestRoomChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestUserInfoEvent,              ModToolRequestUserInfoEvent.class);
        this.registerHandler(Incoming.ModToolPickTicketEvent,                   ModToolPickTicketEvent.class);
        this.registerHandler(Incoming.ModToolCloseTicketEvent,                  ModToolCloseTicketEvent.class);
        this.registerHandler(Incoming.ModToolReleaseTicketEvent,                ModToolReleaseTicketEvent.class);
        this.registerHandler(Incoming.ModToolAlertEvent,                        ModToolAlertEvent.class);
        this.registerHandler(Incoming.ModToolWarnEvent,                         ModToolAlertEvent.class);
        this.registerHandler(Incoming.ModToolKickEvent,                         ModToolKickEvent.class);
        this.registerHandler(Incoming.ModToolRoomAlertEvent,                    ModToolRoomAlertEvent.class);
        this.registerHandler(Incoming.ModToolChangeRoomSettingsEvent,           ModToolChangeRoomSettingsEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomVisitsEvent,            ModToolRequestRoomVisitsEvent.class);
        this.registerHandler(Incoming.ModToolRequestIssueChatlogEvent,          ModToolRequestIssueChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomUserChatlogEvent,       ModToolRequestRoomUserChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestUserChatlogEvent,           ModToolRequestUserChatlogEvent.class);
        this.registerHandler(Incoming.ModToolSanctionAlertEvent,                ModToolSanctionAlertEvent.class);
        this.registerHandler(Incoming.ModToolSanctionMuteEvent,                 ModToolSanctionMuteEvent.class);
        this.registerHandler(Incoming.ModToolSanctionBanEvent,                  ModToolSanctionBanEvent.class);
        this.registerHandler(Incoming.ModToolSanctionTradeLockEvent,            ModToolSanctionTradeLockEvent.class);
        this.registerHandler(Incoming.ModToolIssueChangeTopicEvent,             ModToolIssueChangeTopicEvent.class);
        this.registerHandler(Incoming.ModToolIssueDefaultSanctionEvent,         ModToolIssueDefaultSanctionEvent.class);

        this.registerHandler(Incoming.RequestReportRoomEvent,                   RequestReportRoomEvent.class);
        this.registerHandler(Incoming.RequestReportUserBullyingEvent,           RequestReportUserBullyingEvent.class);
        this.registerHandler(Incoming.ReportBullyEvent,                         ReportBullyEvent.class);
        this.registerHandler(Incoming.ReportEvent,                              ReportEvent.class);
        this.registerHandler(Incoming.ReportFriendPrivateChatEvent,             ReportFriendPrivateChatEvent.class);
    }

    void registerTrading() throws Exception
    {
        this.registerHandler(Incoming.TradeStartEvent,                          TradeStartEvent.class);
        this.registerHandler(Incoming.TradeOfferItemEvent,                      TradeOfferItemEvent.class);
        this.registerHandler(Incoming.TradeOfferMultipleItemsEvent,             TradeOfferMultipleItemsEvent.class);
        this.registerHandler(Incoming.TradeCancelOfferItemEvent,                TradeCancelOfferItemEvent.class);
        this.registerHandler(Incoming.TradeAcceptEvent,                         TradeAcceptEvent.class);
        this.registerHandler(Incoming.TradeUnAcceptEvent,                       TradeUnAcceptEvent.class);
        this.registerHandler(Incoming.TradeConfirmEvent,                        TradeConfirmEvent.class);
        this.registerHandler(Incoming.TradeCloseEvent,                          TradeCloseEvent.class);
        this.registerHandler(Incoming.TradeCancelEvent,                         TradeCancelEvent.class);
    }

    void registerGuilds() throws Exception
    {
        this.registerHandler(Incoming.RequestGuildBuyRoomsEvent,                RequestGuildBuyRoomsEvent.class);
        this.registerHandler(Incoming.RequestGuildPartsEvent,                   RequestGuildPartsEvent.class);
        this.registerHandler(Incoming.RequestGuildBuyEvent,                     RequestGuildBuyEvent.class);
        this.registerHandler(Incoming.RequestGuildInfoEvent,                    RequestGuildInfoEvent.class);
        this.registerHandler(Incoming.RequestGuildManageEvent,                  RequestGuildManageEvent.class);
        this.registerHandler(Incoming.RequestGuildMembersEvent,                 RequestGuildMembersEvent.class);
        this.registerHandler(Incoming.RequestGuildJoinEvent,                    RequestGuildJoinEvent.class);
        this.registerHandler(Incoming.GuildChangeNameDescEvent,                 GuildChangeNameDescEvent.class);
        this.registerHandler(Incoming.GuildChangeBadgeEvent,                    GuildChangeBadgeEvent.class);
        this.registerHandler(Incoming.GuildChangeColorsEvent,                   GuildChangeColorsEvent.class);
        this.registerHandler(Incoming.GuildRemoveAdminEvent,                    GuildRemoveAdminEvent.class);
        this.registerHandler(Incoming.GuildRemoveMemberEvent,                   GuildRemoveMemberEvent.class);
        this.registerHandler(Incoming.GuildChangeSettingsEvent,                 GuildChangeSettingsEvent.class);
        this.registerHandler(Incoming.GuildAcceptMembershipEvent,               GuildAcceptMembershipEvent.class);
        this.registerHandler(Incoming.GuildDeclineMembershipEvent,              GuildDeclineMembershipEvent.class);
        this.registerHandler(Incoming.GuildSetAdminEvent,                       GuildSetAdminEvent.class);
        this.registerHandler(Incoming.GuildSetFavoriteEvent,                    GuildSetFavoriteEvent.class);
        this.registerHandler(Incoming.RequestOwnGuildsEvent,                    RequestOwnGuildsEvent.class);
        this.registerHandler(Incoming.RequestGuildFurniWidgetEvent,             RequestGuildFurniWidgetEvent.class);
        this.registerHandler(Incoming.GuildConfirmRemoveMemberEvent,            GuildConfirmRemoveMemberEvent.class);
        this.registerHandler(Incoming.GuildRemoveFavoriteEvent,                 GuildRemoveFavoriteEvent.class);
        this.registerHandler(Incoming.GuildDeleteEvent,                         GuildDeleteEvent.class);
        //this.registerHandler(Incoming.GetHabboGuildBadgesMessageEvent, GetHabboGuildBadgesMessageEvent.class);
    }

    void registerPets() throws Exception
    {
        this.registerHandler(Incoming.RequestPetInformationEvent,               RequestPetInformationEvent.class);
        this.registerHandler(Incoming.PetPickupEvent,                           PetPickupEvent.class);
        this.registerHandler(Incoming.ScratchPetEvent,                          ScratchPetEvent.class);
        this.registerHandler(Incoming.RequestPetTrainingPanelEvent,             RequestPetTrainingPanelEvent.class);
        this.registerHandler(Incoming.HorseUseItemEvent,                        HorseUseItemEvent.class);
        this.registerHandler(Incoming.HorseRideSettingsEvent,                   HorseRideSettingsEvent.class);
        this.registerHandler(Incoming.HorseRideEvent,                           HorseRideEvent.class);
        this.registerHandler(Incoming.ToggleMonsterplantBreedableEvent,         ToggleMonsterplantBreedableEvent.class);
        this.registerHandler(Incoming.CompostMonsterplantEvent,                 CompostMonsterplantEvent.class);
        this.registerHandler(Incoming.BreedPetsEvent,                           BreedPetsEvent.class);
        this.registerHandler(Incoming.MovePetEvent,                             MovePetEvent.class);
        this.registerHandler(Incoming.PetPackageNameEvent,                      PetPackageNameEvent.class);
    }

    void registerWired() throws Exception
    {
        this.registerHandler(Incoming.WiredTriggerSaveDataEvent,                WiredTriggerSaveDataEvent.class);
        this.registerHandler(Incoming.WiredEffectSaveDataEvent,                 WiredEffectSaveDataEvent.class);
        this.registerHandler(Incoming.WiredConditionSaveDataEvent,              WiredConditionSaveDataEvent.class);
    }

    void registerUnknown() throws Exception
    {
        this.registerHandler(Incoming.RequestResolutionEvent,                   RequestResolutionEvent.class);
        this.registerHandler(Incoming.RequestTalenTrackEvent,                   RequestTalentTrackEvent.class);
        this.registerHandler(Incoming.UnknownEvent1,                            UnknownEvent1.class);
    }

    void registerFloorPlanEditor() throws Exception
    {
        this.registerHandler(Incoming.FloorPlanEditorSaveEvent,                 FloorPlanEditorSaveEvent.class);
        this.registerHandler(Incoming.FloorPlanEditorRequestBlockedTilesEvent,  FloorPlanEditorRequestBlockedTilesEvent.class);
        this.registerHandler(Incoming.FloorPlanEditorRequestDoorSettingsEvent,  FloorPlanEditorRequestDoorSettingsEvent.class);
    }

    void registerAchievements() throws Exception
    {
        this.registerHandler(Incoming.RequestAchievementsEvent,                 RequestAchievementsEvent.class);
        this.registerHandler(Incoming.RequestAchievementConfigurationEvent,     RequestAchievementConfigurationEvent.class);
    }

    void registerGuides() throws Exception
    {
        this.registerHandler(Incoming.RequestGuideToolEvent,                    RequestGuideToolEvent.class);
        this.registerHandler(Incoming.RequestGuideAssistanceEvent,              RequestGuideAssistanceEvent.class);
        this.registerHandler(Incoming.GuideUserTypingEvent,                     GuideUserTypingEvent.class);
        this.registerHandler(Incoming.GuideReportHelperEvent,                   GuideReportHelperEvent.class);
        this.registerHandler(Incoming.GuideRecommendHelperEvent,                GuideRecommendHelperEvent.class);
        this.registerHandler(Incoming.GuideUserMessageEvent,                    GuideUserMessageEvent.class);
        this.registerHandler(Incoming.GuideCancelHelpRequestEvent,              GuideCancelHelpRequestEvent.class);
        this.registerHandler(Incoming.GuideHandleHelpRequestEvent,              GuideHandleHelpRequestEvent.class);
        this.registerHandler(Incoming.GuideInviteUserEvent,                     GuideInviteUserEvent.class);
        this.registerHandler(Incoming.GuideVisitUserEvent,                      GuideVisitUserEvent.class);
        this.registerHandler(Incoming.GuideCloseHelpRequestEvent,               GuideCloseHelpRequestEvent.class);

        this.registerHandler(Incoming.GuardianNoUpdatesWantedEvent,             GuardianNoUpdatesWantedEvent.class);
        this.registerHandler(Incoming.GuardianAcceptRequestEvent,               GuardianAcceptRequestEvent.class);
        this.registerHandler(Incoming.GuardianVoteEvent,                        GuardianVoteEvent.class);
    }

    void registerCrafting() throws Exception
    {
        this.registerHandler(Incoming.RequestCraftingRecipesEvent,              RequestCraftingRecipesEvent.class);
        this.registerHandler(Incoming.CraftingAddRecipeEvent,                   CraftingAddRecipeEvent.class);
        this.registerHandler(Incoming.CraftingCraftItemEvent,                   CraftingCraftItemEvent.class);
        this.registerHandler(Incoming.CraftingCraftSecretEvent,                 CraftingCraftSecretEvent.class);
        this.registerHandler(Incoming.RequestCraftingRecipesAvailableEvent,     RequestCraftingRecipesAvailableEvent.class);
    }

    void registerCamera() throws Exception
    {
        this.registerHandler(Incoming.CameraRoomPictureEvent,                   CameraRoomPictureEvent.class);
        this.registerHandler(Incoming.RequestCameraConfigurationEvent,          RequestCameraConfigurationEvent.class);
        this.registerHandler(Incoming.CameraPurchaseEvent,                      CameraPurchaseEvent.class);
        this.registerHandler(Incoming.CameraRoomThumbnailEvent,                 CameraRoomThumbnailEvent.class);
        this.registerHandler(Incoming.CameraPublishToWebEvent,                  CameraPublishToWebEvent.class);
    }

    void registerGameCenter() throws Exception
    {
        this.registerHandler(Incoming.GameCenterRequestGamesEvent,              GameCenterRequestGamesEvent.class);
        this.registerHandler(Incoming.GameCenterRequestAccountStatusEvent,      GameCenterRequestAccountStatusEvent.class);
        this.registerHandler(Incoming.GameCenterJoinGameEvent,                  GameCenterJoinGameEvent.class);
        this.registerHandler(Incoming.GameCenterLoadGameEvent,                  GameCenterLoadGameEvent.class);
        this.registerHandler(Incoming.GameCenterLeaveGameEvent,                 GameCenterLeaveGameEvent.class);
        this.registerHandler(Incoming.GameCenterEvent,                          GameCenterEvent.class);
        this.registerHandler(Incoming.GameCenterRequestGameStatusEvent,         GameCenterRequestGameStatusEvent.class);
    }

    @EventHandler
    public static void onConfigurationUpdated(EmulatorConfigUpdatedEvent event)
    {
        logList.clear();

        for (String s : Emulator.getConfig().getValue("debug.show.headers").split(";"))
        {
            try
            {
                logList.add(Integer.valueOf(s));
            }
            catch (Exception e)
            {

            }
        }
    }

    public static boolean DEBUG_SHOW_PACKETS = false;
}