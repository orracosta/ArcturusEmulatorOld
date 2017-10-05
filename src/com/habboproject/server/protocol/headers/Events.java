package com.habboproject.server.protocol.headers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Events {
    public static final short GetGameListMessageEvent = 1242;
    public static final short GetGameAchievementsMessageEvent = 1011;

    public static final short GetRoomBannedUsersMessageEvent = 2078;
    public static final short GetPetInventoryMessageEvent = 194;
    public static final short DropHandItemMessageEvent = 369;
    public static final short ReleaseTicketMessageEvent = 3967;
    public static final short GetModeratorRoomInfoMessageEvent = 3398;
    public static final short KickUserMessageEvent = 3642;
    public static final short SaveWiredEffectConfigMessageEvent = 172;
    public static final short RespectPetMessageEvent = 3571;
    public static final short GenerateSecretKeyMessageEvent = 460;
    public static final short GetModeratorTicketChatlogsMessageEvent = 2836;
    public static final short GetAchievementsMessageEvent = 1749;
    public static final short SaveWiredTriggerConfigMessageEvent = 3139;
    public static final short AcceptGroupMembershipMessageEvent = 1115;
    public static final short GetGroupFurniSettingsMessageEvent = 1043;
    public static final short TakeAdminRightsMessageEvent = 691;
    public static final short RemoveAllRightsMessageEvent = 1666;
    public static final short UpdateThreadMessageEvent = 3161;
    public static final short ManageGroupMessageEvent = 3934;
    public static final short ModifyRoomFilterListMessageEvent = 3838;
    public static final short SSOTicketMessageEvent = 127;
    public static final short JoinGroupMessageEvent = 3907;
    public static final short DeclineGroupMembershipMessageEvent = 2118;
    public static final short UniqueIDMessageEvent = 2220;
    public static final short RemoveMyRightsMessageEvent = 3923;
    public static final short ApplyHorseEffectMessageEvent = 1768;
    public static final short GetPetInformationMessageEvent = 3928;
    public static final short GiveHandItemMessageEvent = 1951;
    public static final short UpdateFigureDataMessageEvent = 1476;
    public static final short RemoveGroupMemberMessageEvent = 499;
    public static final short EventLogMessageEvent = 2544;
    public static final short RefreshCampaignMessageEvent = 3388;
    public static final short GetPromotableRoomsMessageEvent = 17;
    public static final short UseOneWayGateMessageEvent = 2253;
    public static final short AddStickyNoteMessageEvent = 2870;
    public static final short GetSelectedBadgesMessageEvent = 1594;
    public static final short UpdateStickyNoteMessageEvent = 867;
    public static final short CloseTicketMesageEvent = 2485;
    public static final short RequestBuddyMessageEvent = 2457;
    public static final short GetFurnitureAliasesMessageEvent = 3675;
    public static final short GetRoomSettingsMessageEvent = 177;
    public static final short RequestFurniInventoryMessageEvent = 696;
    public static final short ModerationKickMessageEvent = 246;
    public static final short OpenFlatConnectionMessageEvent = 3785;
    public static final short DanceMessageEvent = 1551;
    public static final short RemoveBuddyMessageEvent = 579;
    public static final short LatencyTestMessageEvent = 1717;
    public static final short InfoRetrieveMessageEvent = 2139;
    public static final short YouTubeGetNextVideo = 1488;
    public static final short SetObjectDataMessageEvent = 989;
    public static final short MessengerInitMessageEvent = 3058;
    public static final short PickUpBotMessageEvent = 1757;
    public static final short ActionMessageEvent = 3417;
    public static final short LookToMessageEvent = 2988;
    public static final short ToggleMoodlightMessageEvent = 3599;
    public static final short FollowFriendMessageEvent = 3906;
    public static final short PickUpPetMessageEvent = 3090;
    public static final short GetSellablePetBreedsMessageEvent = 1531;
    public static final short GetForumUserProfileMessageEvent = 2518;
    public static final short GetForumsListDataMessageEvent = 1004;
    public static final short IgnoreUserMessageEvent = 505;
    public static final short DeleteRoomMessageEvent = 1054;
    public static final short StartQuestMessageEvent = 1524;
    public static final short GetGiftWrappingConfigurationMessageEvent = 2570;
    public static final short TradingAcceptMessageEvent = 2937;
    public static final short UpdateGroupIdentityMessageEvent = 413;
    public static final short RideHorseMessageEvent = 2598;
    public static final short ApplySignMessageEvent = 3184;
    public static final short FindRandomFriendingRoomMessageEvent = 113;
    public static final short GetModeratorUserChatlogMessageEvent = 3574;
    public static final short TradingOfferItemMessageEvent = 1949;
    public static final short GetWardrobeMessageEvent = 277;
    public static final short MuteUserMessageEvent = 329;
    public static final short UpdateForumSettingsMessageEvent = 683;
    public static final short ApplyDecorationMessageEvent = 2743;
    public static final short GetBotInventoryMessageEvent = 1379;
    public static final short UseHabboWheelMessageEvent = 3615;
    public static final short EditRoomPromotionMessageEvent = 257;
    public static final short GetModeratorUserInfoMessageEvent = 1120;
    public static final short PlaceBotMessageEvent = 2303;
    public static final short GetCatalogPageMessageEvent = 878;
    public static final short GetThreadsListDataMessageEvent = 71;
    public static final short ShoutMessageEvent = 1134;
    public static final short DiceOffMessageEvent = 1857;
    public static final short LetUserInMessageEvent = 1332;
    public static final short SetActivatedBadgesMessageEvent = 1447;
    public static final short UpdateGroupSettingsMessageEvent = 1467;
    public static final short ApproveNameMessageEvent = 2961;
    public static final short TradingCancelMessageEvent = 1486;
    public static final short DeleteGroupMessageEvent = 147;
    public static final short DeleteStickyNoteMessageEvent = 2270;
    public static final short GetGroupInfoMessageEvent = 3549;
    public static final short GetStickyNoteMessageEvent = 1920;
    public static final short DeclineBuddyMessageEvent = 3875;
    public static final short OpenGiftMessageEvent = 967;
    public static final short GiveRoomScoreMessageEvent = 3282;
    public static final short SetGroupFavouriteMessageEvent = 1064;
    public static final short SetMannequinNameMessageEvent = 772;
    public static final short CallForHelpMessageEvent = 1141;
    public static final short RoomDimmerSavePresetMessageEvent = 3631;
    public static final short UpdateGroupBadgeMessageEvent = 517;
    public static final short PickTicketMessageEvent = 2272;
    public static final short SetTonerMessageEvent = 3877;
    public static final short RespectUserMessageEvent = 3177;
    public static final short DeleteGroupThreadMessageEvent = 16;
    public static final short CreditFurniRedeemMessageEvent = 2494;
    public static final short ModerationMsgMessageEvent = 3458;
    public static final short ToggleYouTubeVideoMessageEvent = 1013;
    public static final short UpdateNavigatorSettingsMessageEvent = 3753;
    public static final short ToggleMuteToolMessageEvent = 2325;
    public static final short InitTradeMessageEvent = 3876;
    public static final short ChatMessageEvent = 520;
    public static final short SaveRoomSettingsMessageEvent = 1934;
    public static final short PurchaseFromCatalogAsGiftMessageEvent = 2626;
    public static final short GetGroupCreationWindowMessageEvent = 62;
    public static final short GiveAdminRightsMessageEvent = 729;
    public static final short GetGroupMembersMessageEvent = 3646;
    public static final short ModerateRoomMessageEvent = 3062;
    public static final short GetForumStatsMessageEvent = 3446;
    public static final short GetPromoArticlesMessageEvent = 291;
    public static final short SitMessageEvent = 3941;
    public static final short SetSoundSettingsMessageEvent = 2268;
    public static final short ModerationCautionMessageEvent = 3835;
    public static final short InitializeFloorPlanSessionMessageEvent = 1776;
    public static final short ModeratorActionMessageEvent = 2192;
    public static final short PostGroupContentMessageEvent = 3006;
    public static final short GetModeratorRoomChatlogMessageEvent = 1608;
    public static final short GetUserFlatCatsMessageEvent = 187;
    public static final short RemoveRightsMessageEvent = 2011;
    public static final short ModerationBanMessageEvent = 29;
    public static final short CanCreateRoomMessageEvent = 3488;
    public static final short UseWallItemMessageEvent = 2681;
    public static final short PlaceObjectMessageEvent = 1262;
    public static final short OpenBotActionMessageEvent = 3840;
    public static final short GetEventCategoriesMessageEvent = 2919;
    public static final short GetRoomEntryDataMessageEvent = 764;
    public static final short MoveWallItemMessageEvent = 15;
    public static final short UpdateGroupColoursMessageEvent = 3781;
    public static final short HabboSearchMessageEvent = 1021;
    public static final short CommandBotMessageEvent = 940;
    public static final short SetCustomStackingHeightMessageEvent = 501;
    public static final short UnIgnoreUserMessageEvent = 1889;
    public static final short GetGuestRoomMessageEvent = 3933;
    public static final short SetMannequinFigureMessageEvent = 2430;
    public static final short AssignRightsMessageEvent = 1014;
    public static final short GetYouTubeTelevisionMessageEvent = 3452;
    public static final short SetMessengerInviteStatusMessageEvent = 649;
    public static final short UpdateFloorPropertiesMessageEvent = 3736;
    public static final short GetMoodlightConfigMessageEvent = 1367;
    public static final short PurchaseRoomPromotionMessageEvent = 1765;
    public static final short SendRoomInviteMessageEvent = 3233;
    public static final short ModerationMuteMessageEvent = 2178;
    public static final short SetRelationshipMessageEvent = 415;
    public static final short ChangeMottoMessageEvent = 761;
    public static final short UnbanUserFromRoomMessageEvent = 3257;
    public static final short GetRoomRightsMessageEvent = 1298;
    public static final short PurchaseGroupMessageEvent = 575;
    public static final short CreateFlatMessageEvent = 1674;
    public static final short OpenHelpToolMessageEvent = 2714;
    public static final short ThrowDiceMessageEvent = 2595;
    public static final short SaveWiredConditionConfigMessageEvent = 1404;
    public static final short GetCatalogOfferMessageEvent = 3114;
    public static final short PurchaseFromCatalogMessageEvent = 1986;
    public static final short PickupObjectMessageEvent = 3821;
    public static final short CancelQuestMessageEvent = 3295;
    public static final short NavigatorSearchMessageEvent = 105;
    public static final short MoveAvatarMessageEvent = 2935;
    public static final short GetClientVersionMessageEvent = 4000;
    public static final short InitializeNavigatorMessageEvent = 199;
    public static final short TradingOfferItemsMessageEvent = 1137;
    public static final short GetRoomFilterListMessageEvent = 3536;
    public static final short WhisperMessageEvent = 753;
    public static final short InitCryptoMessageEvent = 340;
    public static final short GetPetTrainingPanelMessageEvent = 2033;
    public static final short MoveObjectMessageEvent = 2660;
    public static final short StartTypingMessageEvent = 1022;
    public static final short GoToHotelViewMessageEvent = 1794;
    public static final short GetExtendedProfileMessageEvent = 3412;
    public static final short SendMsgMessageEvent = 3653;
    public static final short CancelTypingMessageEvent = 1096;
    public static final short GetGroupFurniConfigMessageEvent = 3533;
    public static final short TradingConfirmMessageEvent = 1215;
    public static final short RemoveGroupFavouriteMessageEvent = 818;
    public static final short PlacePetMessageEvent = 154;
    public static final short ModifyWhoCanRideHorseMessageEvent = 2906;
    public static final short GetRelationshipsMessageEvent = 1044;
    public static final short GetCatalogIndexMessageEvent = 3048;
    public static final short ScrGetUserInfoMessageEvent = 2100;
    public static final short ConfirmLoveLockMessageEvent = 3399;
    public static final short RemoveSaddleFromHorseMessageEvent = 3358;
    public static final short AcceptBuddyMessageEvent = 109;
    public static final short GetQuestListMessageEvent = 537;
    public static final short SaveWardrobeOutfitMessageEvent = 1377;
    public static final short BanUserMessageEvent = 3441;
    public static final short GetThreadDataMessageEvent = 856;
    public static final short GetBadgesMessageEvent = 3315;
    public static final short UseFurnitureMessageEvent = 2475;
    public static final short GoToFlatMessageEvent = 745;
    public static final short GetModeratorUserRoomVisitsMessageEvent = 634;
    public static final short SetChatPreferenceMessageEvent = 3729;
    public static final short ResizeNavigatorMessageEvent = 2907;
    public static final short SongInventoryMessageEvent = 3847;
    public static final short SongIdMessageEvent = 2299;
    public static final short SongDataMessageEvent = 341;
    public static final short PlaylistMessageEvent = 2304;
    public static final short PlaylistAddMessageEvent = 3138;
    public static final short PlaylistRemoveMessageEvent = 2280;
    public static final short StaffPickRoomMessageEvent = 3426;
    public static final short SubmitPollAnswerMessageEvent = 2815;
    public static final short GetPollMessageEvent = 455;
    public static final short UpdateSnapshotsMessageEvent = 1327;
    public static final short MarkAsReadMessageEvent = 3092;
    public static final short ApplySelectedEffectMessageEvent = 728;
    public static final short DeleteGroupReplyMessageEvent = 2429;
    public static final short GetOffersMessageEvent = 1214;
    public static final short GetSettingsMessageEvent = 95;
    public static final short CanMakeOfferMessageEvent = 2460;
    public static final short MakeOfferMessageEvent = 2562;
    public static final short OwnOffersMessageEvent = 3012;
    public static final short CancelOfferMessageEvent = 3554;
    public static final short BuyOfferMessageEvent = 2786;
    public static final short RedeemCoinsMessageEvent = 819;
    public static final short SaveFootballClothesMessageEvent = 3440;
    public static final short SpectateRoomMessageEvent = 2596;
    public static final short AmbassadorVisiteRoomMessageEvent = 932;
    public static final short AmbassadorSendAlertToPlayerMessageEvent = 2114;
    public static final short UpdateCameraFollowSettingsMessageEvent = 3860;

    public static final short PriceSettingsMessageEvent = 2983; //
    public static final short RenderRoomMessageEvent = 705; //
    public static final short BuyPhotoMessageEvent = 1160; //

    private static Map<Short, String> eventPacketNames = new HashMap<>();

    static {
        try {
            for (Field field : Events.class.getDeclaredFields()) {
                if (!Modifier.isPrivate(field.getModifiers()))
                    eventPacketNames.put(field.getShort(field.getName()), field.getName());
            }
        } catch (Exception ignored) {

        }
    }

    public static String valueOfId(short packetId) {
        if (eventPacketNames.containsKey(packetId)) {
            return eventPacketNames.get(packetId);
        }

        return "UnknownMessageEvent";
    }
}
