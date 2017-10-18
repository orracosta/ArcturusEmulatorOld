package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementsEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorAlertCommandEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorVisitCommandEvent;
import com.eu.habbo.messages.incoming.catalog.*;
import com.eu.habbo.messages.incoming.catalog.marketplace.*;
import com.eu.habbo.messages.incoming.catalog.recycler.OpenRecycleBoxEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RecycleEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.ReloadRecyclerEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RequestRecyclerLogicEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestBlockedTilesEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestDoorSettingsEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorSaveEvent;
import com.eu.habbo.messages.incoming.friends.*;
import com.eu.habbo.messages.incoming.guilds.*;
import com.eu.habbo.messages.incoming.handshake.*;
import com.eu.habbo.messages.incoming.helper.RequestTalentTrackEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewDataEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewEvent;
import com.eu.habbo.messages.incoming.hotelview.HotelViewRequestBonusRareEvent;
import com.eu.habbo.messages.incoming.hotelview.RequestNewsListEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBadgesEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBotsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryItemsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryPetsEvent;
import com.eu.habbo.messages.incoming.modtool.*;
import com.eu.habbo.messages.incoming.navigator.*;
import com.eu.habbo.messages.incoming.polls.AnswerPollEvent;
import com.eu.habbo.messages.incoming.polls.CancelPollEvent;
import com.eu.habbo.messages.incoming.polls.GetPollDataEvent;
import com.eu.habbo.messages.incoming.rooms.*;
import com.eu.habbo.messages.incoming.rooms.bots.BotPickupEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotPlaceEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSaveSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.items.*;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxEventOne;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxEventTwo;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.JukeBoxRequestPlayListEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceCancelEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceEvent;
import com.eu.habbo.messages.incoming.rooms.pets.*;
import com.eu.habbo.messages.incoming.rooms.users.*;
import com.eu.habbo.messages.incoming.trading.*;
import com.eu.habbo.messages.incoming.unknown.RequestResolutionEvent;
import com.eu.habbo.messages.incoming.unknown.UnknownEvent1;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.WiredConditionSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredEffectSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredTriggerSaveDataEvent;
import gnu.trove.map.hash.THashMap;

public class PacketManager_1006
{

    private final THashMap<Integer, Class<? extends MessageHandler>> incoming;

    public PacketManager_1006()
    {
        this.incoming = new THashMap<Integer, Class<? extends MessageHandler>>();

        this.registerCatalog();
        this.registerHandshake();
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
    }

    void registerHandler(Integer header, Class<? extends MessageHandler> handler)
    {
        this.incoming.putIfAbsent(header, handler);
    }

    public void handlePacket(GameClient client, ClientMessage packet)
    {
        if(client == null)
            return;

        try
        {
            if(this.isRegistered(packet.getMessageId()))
            {
                if(Emulator.getConfig().getBoolean("debug.show.packets"))
                    Emulator.getLogging().logPacketLine("[CLIENT][" + packet.getMessageId() + "] => " + packet.getMessageBody());

                MessageHandler handler = this.incoming.get(packet.getMessageId()).newInstance();

                handler.client = client;
                handler.packet = packet;

                handler.handle();

            }
            else
            {
                if(Emulator.getConfig().getBoolean("debug.show.packets"))
                    Emulator.getLogging().logPacketLine("[CLIENT][UNDEFINED][" + packet.getMessageId() + "] => " + packet.getMessageBody());
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

    private void registerAmbassadors()
    {
        this.registerHandler(Incoming.AmbassadorAlertCommandEvent, AmbassadorAlertCommandEvent.class);
        this.registerHandler(Incoming.AmbassadorVisitCommandEvent, AmbassadorVisitCommandEvent.class);
    }

    private void registerCatalog()
    {
        this.registerHandler(Incoming.RequestRecylerLogicEvent, RequestRecyclerLogicEvent.class);
        this.registerHandler(Incoming.RequestDiscountEvent, RequestDiscountEvent.class);
        this.registerHandler(Incoming.RequestGiftConfigurationEvent, RequestGiftConfigurationEvent.class);
        this.registerHandler(Incoming.GetMarketplaceConfigEvent, RequestMarketplaceConfigEvent.class);
        this.registerHandler(Incoming.RequestCatalogModeEvent, RequestCatalogModeEvent.class);
        this.registerHandler(Incoming.RequestCatalogIndexEvent, RequestCatalogIndexEvent.class);
        this.registerHandler(Incoming.RequestCatalogPageEvent, RequestCatalogPageEvent.class);
        this.registerHandler(Incoming.CatalogBuyItemAsGiftEvent, CatalogBuyItemAsGiftEvent.class);
        this.registerHandler(Incoming.CatalogBuyItemEvent, CatalogBuyItemEvent.class);
        this.registerHandler(Incoming.RedeemVoucherEvent, RedeemVoucherEvent.class);
        this.registerHandler(Incoming.ReloadRecyclerEvent, ReloadRecyclerEvent.class);
        this.registerHandler(Incoming.RecycleEvent, RecycleEvent.class);
        this.registerHandler(Incoming.OpenRecycleBoxEvent, OpenRecycleBoxEvent.class);
        this.registerHandler(Incoming.RequestOwnItemsEvent, RequestOwnItemsEvent.class);
        this.registerHandler(Incoming.TakeBackItemEvent, TakeBackItemEvent.class);
        this.registerHandler(Incoming.RequestOffersEvent, RequestOffersEvent.class);
        this.registerHandler(Incoming.RequestItemInfoEvent, RequestItemInfoEvent.class);
        this.registerHandler(Incoming.BuyItemEvent, BuyItemEvent.class);
        this.registerHandler(Incoming.RequestSellItemEvent, RequestSellItemEvent.class);
        this.registerHandler(Incoming.SellItemEvent, SellItemEvent.class);
        this.registerHandler(Incoming.RequestCreditsEvent, RequestCreditsEvent.class);
        this.registerHandler(Incoming.RequestPetBreedsEvent, RequestPetBreedsEvent.class);
        this.registerHandler(Incoming.CheckPetNameEvent, CheckPetNameEvent.class);
        this.registerHandler(Incoming.GetClubDataEvent, RequestClubDataEvent.class);
        this.registerHandler(Incoming.RequestClubGiftsEvent, RequestClubGiftsEvent.class);
        this.registerHandler(Incoming.CatalogSearchedItemEvent, CatalogSearchedItemEvent.class);
    }

    private void registerHandshake()
    {
        this.registerHandler(Incoming.ReleaseVersionEvent, ReleaseVersionEvent.class);
        this.registerHandler(Incoming.GenerateSecretKeyEvent, GenerateSecretKeyEvent.class);
        this.registerHandler(Incoming.RequestBannerToken, RequestBannerToken.class);
        this.registerHandler(Incoming.SecureLoginEvent, SecureLoginEvent.class);
        this.registerHandler(Incoming.MachineIDEvent, MachineIDEvent.class);
        this.registerHandler(Incoming.UsernameEvent, UsernameEvent.class);
        this.registerHandler(Incoming.PingEvent, PingEvent.class);
    }

    private void registerFriends()
    {
        this.registerHandler(Incoming.RequestFriendsEvent, RequestFriendsEvent.class);
        this.registerHandler(Incoming.ChangeRelationEvent, ChangeRelationEvent.class);
        this.registerHandler(Incoming.RemoveFriendEvent, RemoveFriendEvent.class);
        this.registerHandler(Incoming.SearchUserEvent, SearchUserEvent.class);
        this.registerHandler(Incoming.FriendRequestEvent, FriendRequestEvent.class);
        this.registerHandler(Incoming.AcceptFriendRequest, AcceptFriendRequestEvent.class);
        this.registerHandler(Incoming.FriendPrivateMessageEvent, FriendPrivateMessageEvent.class);
        this.registerHandler(Incoming.RequestFriendRequestEvent, RequestFriendRequestsEvent.class);
        this.registerHandler(Incoming.StalkFriendEvent, StalkFriendEvent.class);
        this.registerHandler(Incoming.RequestInitFriendsEvent , RequestInitFriendsEvent.class);
        this.registerHandler(Incoming.FindNewFriendsEvent, FindNewFriendsEvent.class);
        this.registerHandler(Incoming.InviteFriendsEvent, InviteFriendsEvent.class);
    }

    private void registerUsers()
    {
        this.registerHandler(Incoming.RequestUserDataEvent, RequestUserDataEvent.class);
        this.registerHandler(Incoming.RequestUserCreditsEvent, RequestUserCreditsEvent.class);
        this.registerHandler(Incoming.RequestUserClubEvent, RequestUserClubEvent.class);
        this.registerHandler(Incoming.RequestMeMenuSettingsEvent, RequestMeMenuSettingsEvent.class);
        this.registerHandler(Incoming.RequestUserCitizinShipEvent, RequestUserCitizinShipEvent.class);
        this.registerHandler(Incoming.RequestUserProfileEvent, RequestUserProfileEvent.class);
        this.registerHandler(Incoming.RequestProfileFriendsEvent, RequestProfileFriendsEvent.class);
        this.registerHandler(Incoming.RequestUserWardrobeEvent, RequestUserWardrobeEvent.class);
        this.registerHandler(Incoming.SaveWardrobeEvent, SaveWardrobeEvent.class);
        this.registerHandler(Incoming.SaveMottoEvent, SaveMottoEvent.class);
        this.registerHandler(Incoming.UserSaveLookEvent, UserSaveLookEvent.class);
        this.registerHandler(Incoming.UserWearBadgeEvent, UserWearBadgeEvent.class);
        this.registerHandler(Incoming.RequestWearingBadgesEvent, RequestWearingBadgesEvent.class);
        this.registerHandler(Incoming.SaveUserVolumesEvent, SaveUserVolumesEvent.class);
        this.registerHandler(Incoming.SaveBlockCameraFollowEvent, SaveBlockCameraFollowEvent.class);
        this.registerHandler(Incoming.SaveIgnoreRoomInvitesEvent, SaveIgnoreRoomInvitesEvent.class);
        this.registerHandler(Incoming.SavePreferOldChatEvent, SavePreferOldChatEvent.class);
    }

    private void registerNavigator()
    {
        this.registerHandler(Incoming.RequestRoomCategoriesEvent, RequestRoomCategoriesEvent.class);
        this.registerHandler(Incoming.RequestPublicRoomsEvent, RequestPublicRoomsEvent.class);
        this.registerHandler(Incoming.RequestPopularRoomsEvent, RequestPopularRoomsEvent.class);
        this.registerHandler(Incoming.RequestHighestScoreRoomsEvent, RequestHighestScoreRoomsEvent.class);
        this.registerHandler(Incoming.RequestMyRoomsEvent, RequestMyRoomsEvent.class);
        this.registerHandler(Incoming.RequestCanCreateRoomEvent, RequestCanCreateRoomEvent.class);
        this.registerHandler(Incoming.RequestPromotedRoomsEvent, RequestPromotedRoomsEvent.class);
        this.registerHandler(Incoming.RequestCreateRoomEvent, RequestCreateRoomEvent.class);
        this.registerHandler(Incoming.RequestTagsEvent, RequestTagsEvent.class);
        this.registerHandler(Incoming.SearchRoomsByTagEvent, SearchRoomsByTagEvent.class);
        this.registerHandler(Incoming.SearchRoomsEvent, SearchRoomsEvent.class);
        this.registerHandler(Incoming.SearchRoomsFriendsNowEvent, SearchRoomsFriendsNowEvent.class);
        this.registerHandler(Incoming.SearchRoomsFriendsOwnEvent, SearchRoomsFriendsOwnEvent.class);
        this.registerHandler(Incoming.SearchRoomsWithRightsEvent, SearchRoomsWithRightsEvent.class);
        this.registerHandler(Incoming.SearchRoomsInGroupEvent, SearchRoomsInGroupEvent.class);
        this.registerHandler(Incoming.SearchRoomsMyFavoriteEvent, SearchRoomsMyFavouriteEvent.class);
        this.registerHandler(Incoming.SearchRoomsVisitedEvent, SearchRoomsVisitedEvent.class);
        this.registerHandler(Incoming.RequestNewNavigatorDataEvent, RequestNewNavigatorDataEvent.class);
        this.registerHandler(Incoming.RequestNewNavigatorRoomsEvent, RequestNewNavigatorRoomsEvent.class);
        this.registerHandler(Incoming.NewNavigatorActionEvent, NewNavigatorActionEvent.class);
    }

    private void registerHotelview()
    {
        this.registerHandler(Incoming.HotelViewEvent, HotelViewEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBonusRareEvent, HotelViewRequestBonusRareEvent.class);
        this.registerHandler(Incoming.RequestNewsListEvent, RequestNewsListEvent.class);
        this.registerHandler(Incoming.HotelViewDataEvent, HotelViewDataEvent.class);
    }

    private void registerInventory()
    {
        this.registerHandler(Incoming.RequestInventoryBadgesEvent, RequestInventoryBadgesEvent.class);
        this.registerHandler(Incoming.RequestInventoryBotsEvent, RequestInventoryBotsEvent.class);
        this.registerHandler(Incoming.RequestInventoryItemsEvent, RequestInventoryItemsEvent.class);
        this.registerHandler(Incoming.RequestInventoryPetsEvent, RequestInventoryPetsEvent.class);
    }

    void registerRooms()
    {
        this.registerHandler(Incoming.RequestRoomLoadEvent, RequestRoomLoadEvent.class);
        this.registerHandler(Incoming.RequestHeightmapEvent, RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.RequestRoomHeightmapEvent, RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.RoomVoteEvent, RoomVoteEvent.class);
        this.registerHandler(Incoming.RequestRoomDataEvent, RequestRoomDataEvent.class);
        this.registerHandler(Incoming.RoomSettingsSaveEvent, RoomSettingsSaveEvent.class);
        this.registerHandler(Incoming.RoomPlaceItemEvent, RoomPlaceItemEvent.class);
        this.registerHandler(Incoming.RotateMoveItemEvent, RotateMoveItemEvent.class);
        this.registerHandler(Incoming.MoveWallItemEvent, MoveWallItemEvent.class);
        this.registerHandler(Incoming.RoomPickupItemEvent, RoomPickupItemEvent.class);
        this.registerHandler(Incoming.RoomPlacePaintEvent, RoomPlacePaintEvent.class);
        this.registerHandler(Incoming.RoomUserStartTypingEvent, RoomUserStartTypingEvent.class);
        this.registerHandler(Incoming.RoomUserStopTypingEvent, RoomUserStopTypingEvent.class);
        this.registerHandler(Incoming.ToggleFloorItemEvent, ToggleFloorItemEvent.class);
        this.registerHandler(Incoming.ToggleWallItemEvent, ToggleWallItemEvent.class);
        this.registerHandler(Incoming.RoomBackgroundEvent, RoomBackgroundEvent.class);
        this.registerHandler(Incoming.MannequinSaveNameEvent, MannequinSaveNameEvent.class);
        this.registerHandler(Incoming.MannequinSaveLookEvent, MannequinSaveLookEvent.class);
        this.registerHandler(Incoming.AdvertisingSaveEvent, AdvertisingSaveEvent.class);
        this.registerHandler(Incoming.RequestRoomSettingsEvent, RequestRoomSettingsEvent.class);
        this.registerHandler(Incoming.MoodLightSettingsEvent, MoodLightSettingsEvent.class);
        this.registerHandler(Incoming.MoodLightTurnOnEvent, MoodLightTurnOnEvent.class);
        this.registerHandler(Incoming.RoomUserDropHandItemEvent, RoomUserDropHandItemEvent.class);
        this.registerHandler(Incoming.RoomUserLookAtPoint, RoomUserLookAtPoint.class);
        this.registerHandler(Incoming.RoomUserTalkEvent, RoomUserTalkEvent.class);
        this.registerHandler(Incoming.RoomUserShoutEvent, RoomUserShoutEvent.class);
        this.registerHandler(Incoming.RoomUserWhisperEvent, RoomUserWhisperEvent.class);
        this.registerHandler(Incoming.RoomUserActionEvent, RoomUserActionEvent.class);
        this.registerHandler(Incoming.RoomUserSitEvent, RoomUserSitEvent.class);
        this.registerHandler(Incoming.RoomUserDanceEvent, RoomUserDanceEvent.class);
        this.registerHandler(Incoming.RoomUserSignEvent, RoomUserSignEvent.class);
        this.registerHandler(Incoming.RoomUserWalkEvent, RoomUserWalkEvent.class);
        this.registerHandler(Incoming.RoomUserGiveRespectEvent, RoomUserGiveRespectEvent.class);
        this.registerHandler(Incoming.RoomUserGiveRightsEvent, RoomUserGiveRightsEvent.class);
        this.registerHandler(Incoming.RequestRoomRightsEvent, RequestRoomRightsEvent.class);
        this.registerHandler(Incoming.RoomRemoveAllRightsEvent, RoomRemoveAllRightsEvent.class);
        this.registerHandler(Incoming.RoomUserRemoveRightsEvent, RoomUserRemoveRightsEvent.class);
        this.registerHandler(Incoming.BotPlaceEvent, BotPlaceEvent.class);
        this.registerHandler(Incoming.BotPickupEvent, BotPickupEvent.class);
        this.registerHandler(Incoming.BotSaveSettingsEvent, BotSaveSettingsEvent.class);
        this.registerHandler(Incoming.BotSettingsEvent, BotSettingsEvent.class);
        this.registerHandler(Incoming.TriggerDiceEvent, TriggerDiceEvent.class);
        this.registerHandler(Incoming.CloseDiceEvent, CloseDiceEvent.class);
        this.registerHandler(Incoming.TriggerColorWheelEvent, TriggerColorWheelEvent.class);
        this.registerHandler(Incoming.RedeemItemEvent, RedeemItemEvent.class);
        this.registerHandler(Incoming.PetPlaceEvent, PetPlaceEvent.class);
        this.registerHandler(Incoming.RoomUserKickEvent, RoomUserKickEvent.class);
        this.registerHandler(Incoming.SetStackHelperHeightEvent, SetStackHelperHeightEvent.class);
        this.registerHandler(Incoming.TriggerOneWayGateEvent, TriggerOneWayGateEvent.class);
        this.registerHandler(Incoming.HandleDoorbellEvent, HandleDoorbellEvent.class);
        this.registerHandler(Incoming.RedeemClothingEvent, RedeemClothingEvent.class);
        this.registerHandler(Incoming.PostItPlaceEvent, PostItPlaceEvent.class);
        this.registerHandler(Incoming.PostItRequestDataEvent, PostItRequestDataEvent.class);
        this.registerHandler(Incoming.PostItSaveDataEvent, PostItSaveDataEvent.class);
        this.registerHandler(Incoming.PostItDeleteEvent, PostItDeleteEvent.class);
        this.registerHandler(Incoming.MoodLightSaveSettingsEvent, MoodLightSaveSettingsEvent.class);
        this.registerHandler(Incoming.RentSpaceEvent, RentSpaceEvent.class);
        this.registerHandler(Incoming.RentSpaceCancelEvent, RentSpaceCancelEvent.class);
        this.registerHandler(Incoming.SetHomeRoomEvent, SetHomeRoomEvent.class);
        this.registerHandler(Incoming.RoomUserGiveHandItemEvent, RoomUserGiveHandItemEvent.class);
        this.registerHandler(Incoming.RoomMuteEvent, RoomMuteEvent.class);
        this.registerHandler(Incoming.RequestRoomWordFilterEvent, RequestRoomWordFilterEvent.class);
        this.registerHandler(Incoming.RoomWordFilterModifyEvent, RoomWordFilterModifyEvent.class);
        this.registerHandler(Incoming.RoomStaffPickEvent, RoomStaffPickEvent.class);
        this.registerHandler(Incoming.RoomRequestBannedUsersEvent, RoomRequestBannedUsersEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestTrackCodeEvent, JukeBoxRequestTrackCodeEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestTrackDataEvent, JukeBoxRequestTrackDataEvent.class);
        this.registerHandler(Incoming.JukeBoxRequestPlayListEvent, JukeBoxRequestPlayListEvent.class);
        this.registerHandler(Incoming.JukeBoxEventOne, JukeBoxEventOne.class);
        this.registerHandler(Incoming.JukeBoxEventTwo, JukeBoxEventTwo.class);
    }

    void registerPolls()
    {
        this.registerHandler(Incoming.CancelPollEvent, CancelPollEvent.class);
        this.registerHandler(Incoming.GetPollDataEvent, GetPollDataEvent.class);
        this.registerHandler(Incoming.AnswerPollEvent, AnswerPollEvent.class);
    }

    void registerModTool()
    {
        this.registerHandler(Incoming.ModToolRequestRoomInfoEvent, ModToolRequestRoomInfoEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomChatlogEvent, ModToolRequestRoomChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestUserInfoEvent, ModToolRequestUserInfoEvent.class);
        this.registerHandler(Incoming.ModToolPickTicketEvent, ModToolPickTicketEvent.class);
        this.registerHandler(Incoming.ModToolCloseTicketEvent, ModToolCloseTicketEvent.class);
        this.registerHandler(Incoming.ModToolReleaseTicketEvent, ModToolReleaseTicketEvent.class);
        this.registerHandler(Incoming.ModToolAlertEvent, ModToolAlertEvent.class);
        this.registerHandler(Incoming.ModToolWarnEvent, ModToolAlertEvent.class);
        this.registerHandler(Incoming.ModToolKickEvent, ModToolKickEvent.class);
        this.registerHandler(Incoming.ModToolRoomAlertEvent, ModToolRoomAlertEvent.class);
        this.registerHandler(Incoming.ModToolRequestUserChatlogEvent, ModToolRequestUserChatlogEvent.class);
        this.registerHandler(Incoming.ModToolChangeRoomSettingsEvent, ModToolChangeRoomSettingsEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomVisitsEvent, ModToolRequestRoomVisitsEvent.class);
        this.registerHandler(Incoming.ModToolRequestIssueChatlogEvent, ModToolRequestIssueChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomUserChatlogEvent, ModToolRequestRoomUserChatlogEvent.class);

        this.registerHandler(Incoming.RequestReportRoomEvent, RequestReportRoomEvent.class);
        this.registerHandler(Incoming.ReportEvent, ReportEvent.class);
    }

    void registerTrading()
    {
        this.registerHandler(Incoming.TradeStartEvent, TradeStartEvent.class);
        this.registerHandler(Incoming.TradeOfferItemEvent, TradeOfferItemEvent.class);
        this.registerHandler(Incoming.TradeCancelOfferItemEvent, TradeCancelOfferItemEvent.class);
        this.registerHandler(Incoming.TradeAcceptEvent, TradeAcceptEvent.class);
        this.registerHandler(Incoming.TradeUnAcceptEvent, TradeUnAcceptEvent.class);
        this.registerHandler(Incoming.TradeConfirmEvent, TradeConfirmEvent.class);
        this.registerHandler(Incoming.TradeCloseEvent, TradeCloseEvent.class);
    }

    void registerGuilds()
    {
        this.registerHandler(Incoming.RequestGuildBuyRoomsEvent, RequestGuildBuyRoomsEvent.class);
        this.registerHandler(Incoming.RequestGuildPartsEvent, RequestGuildPartsEvent.class);
        this.registerHandler(Incoming.RequestGuildBuyEvent, RequestGuildBuyEvent.class);
        this.registerHandler(Incoming.RequestGuildInfoEvent, RequestGuildInfoEvent.class);
        this.registerHandler(Incoming.RequestGuildManageEvent, RequestGuildManageEvent.class);
        this.registerHandler(Incoming.RequestGuildMembersEvent, RequestGuildMembersEvent.class);
        this.registerHandler(Incoming.RequestGuildJoinEvent, RequestGuildJoinEvent.class);
        this.registerHandler(Incoming.GuildChangeNameDescEvent, GuildChangeNameDescEvent.class);
        this.registerHandler(Incoming.GuildChangeBadgeEvent, GuildChangeBadgeEvent.class);
        this.registerHandler(Incoming.GuildChangeColorsEvent, GuildChangeColorsEvent.class);
        this.registerHandler(Incoming.GuildRemoveAdminEvent, GuildRemoveAdminEvent.class);
        this.registerHandler(Incoming.GuildRemoveMemberEvent, GuildRemoveMemberEvent.class);
        this.registerHandler(Incoming.GuildChangeSettingsEvent, GuildChangeSettingsEvent.class);
        this.registerHandler(Incoming.GuildAcceptMembershipEvent, GuildAcceptMembershipEvent.class);
        this.registerHandler(Incoming.GuildDeclineMembershipEvent, GuildDeclineMembershipEvent.class);
        this.registerHandler(Incoming.GuildSetAdminEvent, GuildSetAdminEvent.class);
        this.registerHandler(Incoming.GuildSetFavoriteEvent, GuildSetFavoriteEvent.class);
        this.registerHandler(Incoming.RequestOwnGuildsEvent, RequestOwnGuildsEvent.class);
        this.registerHandler(Incoming.RequestGuildFurniWidgetEvent, RequestGuildFurniWidgetEvent.class);
        this.registerHandler(Incoming.GuildConfirmRemoveMemberEvent, GuildConfirmRemoveMemberEvent.class);
        //this.registerHandler(Incoming.GuildRemoveFavoriteEvent, GuildRemoveFavoriteEvent.class);
        this.registerHandler(Incoming.GuildDeleteEvent, GuildDeleteEvent.class);
    }

    void registerPets()
    {
        this.registerHandler(Incoming.RequestPetInformationEvent, RequestPetInformationEvent.class);
        this.registerHandler(Incoming.PetPickupEvent, PetPickupEvent.class);
        this.registerHandler(Incoming.ScratchPetEvent, ScratchPetEvent.class);
        this.registerHandler(Incoming.RequestPetTrainingPanelEvent, RequestPetTrainingPanelEvent.class);
        this.registerHandler(Incoming.HorseUseItemEvent, HorseUseItemEvent.class);
        this.registerHandler(Incoming.HorseRideSettingsEvent, HorseRideSettingsEvent.class);
        this.registerHandler(Incoming.HorseRideEvent, HorseRideEvent.class);
    }

    void registerWired()
    {
        this.registerHandler(Incoming.WiredTriggerSaveDataEvent, WiredTriggerSaveDataEvent.class);
        this.registerHandler(Incoming.WiredEffectSaveDataEvent, WiredEffectSaveDataEvent.class);
        this.registerHandler(Incoming.WiredConditionSaveDataEvent, WiredConditionSaveDataEvent.class);
    }

    void registerUnknown()
    {
        this.registerHandler(Incoming.RequestResolutionEvent, RequestResolutionEvent.class);
        this.registerHandler(Incoming.RequestTalenTrackEvent, RequestTalentTrackEvent.class); //TODO
        this.registerHandler(Incoming.UnknownEvent1, UnknownEvent1.class);
    }

    void registerFloorPlanEditor()
    {
        this.registerHandler(Incoming.FloorPlanEditorSaveEvent, FloorPlanEditorSaveEvent.class);
        this.registerHandler(Incoming.FloorPlanEditorRequestBlockedTilesEvent, FloorPlanEditorRequestBlockedTilesEvent.class);
        this.registerHandler(Incoming.FloorPlanEditorRequestDoorSettingsEvent, FloorPlanEditorRequestDoorSettingsEvent.class);
    }

    void registerAchievements()
    {
        this.registerHandler(Incoming.RequestAchievementsEvent, RequestAchievementsEvent.class);
    }
}