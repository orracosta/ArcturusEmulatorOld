package com.eu.habbo.messages.outgoing;

public class Outgoing_Back
{

    /*
        Talents
     */
    //public static final int TalentTrackComposer = 2842;

    /*
        Wired
     */
    /*public static final int WiredTriggerDataComposer = 861;
    public static final int WiredEffectDataComposer = 1279;
    public static final int WiredConditionDataComposer = 1639;
    public static final int WiredSavedComposer = 39;

    /*
        Pets
     */
    /*public static final int PetInformationComposer = 2286;
    public static final int RoomPetRespectComposer = 778;
    public static final int RoomPetExperienceComposer = 717;
    public static final int PetTrainingPanelComposer = 456;
    public static final int RoomPetHorseFigureComposer = 1644;

    /*
        Groups
     */
    /*public static final int GuildBuyRoomsComposer = 3938;
    public static final int GroupPartsComposer = 567;
    public static final int GuildBoughtComposer = 389;
    public static final int GuildInfoComposer = 2978;
    public static final int GuildManageComposer = 601;
    public static final int GuildMembersComposer = 3986;
    public static final int GuildJoinErrorComposer = 1952;
    public static final int GuildRefreshMembersListComposer = 2055;
    public static final int GuildMemberUpdateComposer = 862;
    public static final int GuildListComposer = 587;
    public static final int GuildFurniWidgetComposer = 269;

    /*
        Trading
     */
    /*public static final int TradeUpdateComposer = 1221;
    public static final int TradeStartComposer = 3713;
    public static final int TradeStartFailComposer = 2320;
    public static final int TradeCompleteComposer = 3766;
    public static final int TradeCloseWindowComposer = 2000;
    public static final int TradeStoppedComposer = 2020;
    public static final int TradeAcceptedComposer = 741;

    /*
        ModTool
     */
    /*public static final int ModToolComposer = 1901;
    public static final int ModToolRoomInfoComposer = 3722;
    public static final int ModToolRoomChatlogComposer = 3869;
    public static final int ModToolRoomEntryInfoComposer = 2644;
    public static final int ModToolUserInfoComposer = 2583;
    public static final int ModToolUserChatlogComposer = 3738;
    public static final int ModToolUserRoomVisitsComposer = 2129;
    public static final int ModToolIssueInfoComposer = 2473;
    public static final int ModToolIssueChatlogComposer = 1445;
    public static final int ModToolIssueUpdateComposer = 3019;
    public static final int ModToolComposerOne = 2303;
    public static final int ModToolComposerTwo = 1917;

    /*
        Catalog
     */
    /*public static final int GiftConfigurationComposer = 1048;
    public static final int DiscountComposer = 1061;
    public static final int MarketplaceConfigComposer = 2524;
    public static final int CatalogUpdatedComposer = 2028;
    public static final int CatalogModeComposer = 3442;
    public static final int CatalogPagesListComposer = 1684;
    public static final int CatalogPageComposer = 3518;
    public static final int PurchaseOKComposer = 1189;
    public static final int RecyclerLogicComposer = 1317;
    public static final int ReloadRecyclerComposer = 1093;
    public static final int RecyclerCompleteComposer = 2125;
    public static final int ClubDataComposer = 1919;

    public static final int MarketplaceOwnItemsComposer = 593;
    public static final int MarketplaceCancelSaleComposer = 1959;
    public static final int MarketplaceOffersComposer = 3289;
    public static final int MarketplaceItemInfoComposer = 1066;
    public static final int MarketplaceBuyErrorComposer = 1823;
    public static final int MarketplaceSellItemComposer = 1932;

    public static final int PetBreedsComposer = 3302;
    public static final int PetNameErrorComposer = 3575;

    /*
        Alerts (All of them)
     */
    /*public static final int AlertLimitedSoldOutComposer = 3290;
    public static final int AlertPurchaseFailedComposer = 2602;
    public static final int RedeemVoucherOKComposer = 2719;
    public static final int RedeemVoucherErrorComposer = 2630;
    public static final int RoomEnterErrorComposer = 1619;
    public static final int WiredRewardAlertComposer = 723;
    public static final int PetErrorComposer = 779;
    public static final int BotErrorComposer = 3420;
    public static final int BubbleAlertComposer = 777;
    public static final int GenericErrorMessages = 2839;

    /*
        Handshake
     */
    /*public static final int BannerTokenComposer = 3033;
    public static final int SecureLoginOKComposer = 2753;
    public static final int SessionRightsComposer = 1313;
    public static final int DebugConsoleComposer = 336;

    /*
        HotelView
     */
   /* public static final int HotelViewComposer = 3152;
    public static final int HallOfFameComposer = 670;
    public static final int NewsWidgetsComposer = 1345;
    public static final int BonusRareComposer = 3517;
    public static final int HotelViewDataComposer = 1374;

    /*
        Generic
     */
   /* public static final int GenericAlertComposer = 1548;
    public static final int MinimailCountComposer = 2253;
    public static final int FavoriteRoomsCountComposer = 3829;

    /*
        Polls
     */
    //public static final int PollStartComposer = 749;
    //public static final int PollQuestionsComposer = 3187;

    /*
        Friends
     */
    /*public static final int FriendsComposer = 2067;
    public static final int UpdateFriendComposer = 170;
    public static final int UserSearchResultComposer = 1573;
    public static final int FriendRequestComposer = 2468;
    public static final int RemoveFriendComposer = 1106;
    public static final int FriendChatMessageComposer = 2094;
    public static final int LoadFriendRequestsComposer = 2193;
    public static final int FriendRequestErrorComposer = 2467;
    public static final int StalkErrorComposer = 1135;

    /*
        Inventory
     */
    /*public static final int InventoryItemsComposer = 3029;
    public static final int InventoryPetsComposer = 435;
    public static final int InventoryBotsComposer = 320;
    public static final int InventoryBadgesComposer = 1629;
    public static final int AddHabboItemComposer = 274;
    public static final int AddBotComposer = 3043;
    public static final int RemoveBotComposer = 2565;
    public static final int RemoveHabboItemComposer = 3233;
    public static final int InventoryRefreshComposer = 3243;
    public static final int InventoryItemUpdateComposer = 1577;
    public static final int AddPetComposer = 3099;
    public static final int RemovePetComposer = 467;
    public static final int InventoryAchievementsComposer = 3295;

    /*
        Navigator
     */
    /*public static final int RoomCategoriesComposer = 3241;
    public static final int PublicRoomsComposer = 2506;
    public static final int PrivateRoomsComposer = 2653;
    public static final int CanCreateRoomComposer = 2463;
    public static final int RoomCreatedComposer = 3373;
    public static final int TagsComposer = 3487;

    /*
        Rooms
     */
    /*public static final int RoomPaintComposer = 2979;
    public static final int RoomRightsComposer = 520;
    public static final int RoomOwnerComposer = 1069;
    public static final int RoomScoreComposer = 2805;
    public static final int RoomDataComposer = 3995;
    public static final int RoomHeightMapComposer = 1624;
    public static final int RoomUsersComposer = 1073;
    public static final int RoomThicknessComposer = 2627;
    public static final int RoomOpenComposer = 925;
    public static final int RoomModelComposer = 2297;
    public static final int RoomRelativeMapComposer = 1975;
    public static final int RoomPaneComposer = 2644;
    public static final int RoomFloorItemsComposer = 2124;
    public static final int RoomWallItemsComposer = 395;
    public static final int RoomUserTalkComposer = 612;
    public static final int RoomUserShoutComposer = 3271;
    public static final int RoomUserWhisperComposer = 2323;
    public static final int RoomUserActionComposer = 12;
    public static final int RoomUserRespectComposer = 2143;
    public static final int RoomUserDanceComposer = 2091;
    public static final int RoomUserEffectComposer = 3040;
    public static final int RoomUserStatusComposer = 3386;
    public static final int RoomUserRemoveComposer = 2819;
    public static final int AddWallItemComposer = 2156;
    public static final int AddFloorItemComposer = 1590;
    public static final int RemoveWallItemComposer = 2466;
    public static final int RemoveFloorItemComposer = 36;
    public static final int FloorItemUpdateComposer = 648;
    public static final int UpdateStackHeightComposer = 1576;
    public static final int RoomUserTypingComposer = 3624;
    public static final int WallItemUpdateComposer = 2190;
    public static final int RoomUserHandItemComposer = 3664;
    public static final int RoomUserRemoveRightsComposer = 1496;
    public static final int RoomRightsListComposer = 1891;
    public static final int RoomAddRightsListComposer = 3611;
    public static final int ChangeNameUpdatedComposer = 542;
    public static final int BotSettingsComposer = 1522;
    public static final int RoomSettingsUpdatedComposer = 543;
    public static final int RoomChatSettingsComposer = 3710;
    public static final int DoorbellAddUserComposer = 3198;
    public static final int RoomAccessDeniedComposer = 3953;
    public static final int MoodLightDataComposer = 2033;
    public static final int ObjectOnRollerComposer = 1488;
    public static final int PresentItemOpenedComposer = 1540;
    public static final int FreezeLivesComposer = 3526;

    /*
        Users
     */
    /*public static final int UserBCLimitsComposer = 3442;
    public static final int UserCurrencyComposer = 3454;
    public static final int UserDataComposer = 3491;
    public static final int UserPerksComposer = 1218;
    public static final int UserPermissionsComposer = 3872;
    public static final int UserCreditsComposer = 2204;
    public static final int UserClubComposer = 2869;
    public static final int MeMenuSettingsComposer = 15;
    public static final int UserCitizinShipComposer = 3439;
    public static final int UserTagsComposer = 933;
    public static final int UserHomeRoomComposer = 40;
    public static final int UserProfileComposer = 1503;
    public static final int ProfileFriendsComposer = 322;
    public static final int UserUpdateCurrencyComposer = 1290;
    public static final int UserWardrobeComposer = 370;
    public static final int RoomUserDataComposer = 3944;
    public static final int UserBadgesComposer = 3486;
    public static final int AddUserBadgeComposer = 3197;

    /*
        Ones that are someway implemented but not correctly.
     */
    public static final int PetStatusUpdateComposer = 3566; //Updated

    /*
        Not implemented but know what they are:
     */
    /*public static final int UserAddEffectComposer = 2181; //int, int, int (type, ?? (effectId?), duration

    public final static int FavoriteRoomsCountComposer = 2420;//3829
    public final static int UserCurrencyComposer = 584;//3454
    public final static int RedeemVoucherOKComposer = 2136;//2719
    public final static int RoomUserShoutComposer = 3274;//3271
    public final static int RoomUserStatusComposer = 2566;//3386
    public final static int RoomUserDataComposer = 2538;//3944
    public final static int RoomAddRightsListComposer = 1992;//3611
    public final static int RoomRemoveRightsListComposer = 2021;
    public final static int RoomRightsListComposer = 2857;//1891
    public final static int RoomUserHandItemComposer = 2425;//3664
    public final static int RoomUsersComposer = 961;//1073
    public final static int FriendRequestComposer = 3550;//2468
    public final static int GuildBoughtComposer = 2087;//389
    public final static int AddUserBadgeComposer = 10;//3197
    public final static int RecyclerCompleteComposer = 1513;//2125
    public final static int GuildBuyRoomsComposer = 3938;//3938
    public final static int FriendsComposer = 1535;//2067
    public final static int StalkErrorComposer = 1739;//1135
    public final static int TradeCloseWindowComposer = 3545;//2000
    public final static int RemoveFloorItemComposer = 679;//36
    public final static int InventoryPetsComposer = 2799;//435
    public final static int UserCreditsComposer = 689;//2204
    public final static int WiredTriggerDataComposer = 3639;//861
    public final static int TradeStoppedComposer = 1941;//2020
    public final static int ModToolUserChatlogComposer = 912;//3738
    public final static int GuildInfoComposer = 3398;//2978
    public final static int UserPermissionsComposer = 3514;//3872
    public final static int PetNameErrorComposer = 2773;//3575
    public final static int TradeStartFailComposer = 796;//2320
    public final static int AddHabboItemComposer = 828;//274
    public final static int InventoryBotsComposer = 3908;//320
    public final static int CanCreateRoomComposer = 1458;//2463
    public final static int MarketplaceBuyErrorComposer = 3313;//1823
    public final static int BonusRareComposer = 2194;//3517
    public final static int HotelViewComposer = 2553;//3152
    public final static int UpdateFriendComposer = 2605;//170 //2605
    public final static int FloorItemUpdateComposer = 1334;//648
    public final static int RoomAccessDeniedComposer = 1504;//3953
    public final static int GuildFurniWidgetComposer = 2909;//269
    public final static int GiftConfigurationComposer = 1965;//1048
    public final static int UserClubComposer = 3926;//2869
    public final static int InventoryBadgesComposer = 79;//1629
    public final static int RoomUserTypingComposer = 3765;//3624
    public final static int GuildJoinErrorComposer = 1551;//1952
    public final static int RoomCategoriesComposer = 815;//3241
    public final static int InventoryAchievementsComposer = 894;//3295
    public final static int MarketplaceItemInfoComposer = 688;//1066
    public final static int UserTagsComposer = 1578;//933
    public final static int RoomRelativeMapComposer = 124;//1975
    public final static int ModToolComposerTwo = 1761;//1917
    public final static int ModToolComposerOne = 2621;//2303
    public final static int RoomRightsComposer = 3358;//520
    public final static int ObjectOnRollerComposer = 1294;//1488
    public final static int PollStartComposer = 3353;//749
    public final static int GuildRefreshMembersListComposer = 3650;//2055
    public final static int UserPerksComposer = 2284;//1218
    public final static int UserCitizinShipComposer = 2486;//3439
    public final static int PublicRoomsComposer = 2433;//2506
    public final static int MarketplaceOffersComposer = 3738;//3289
    public final static int ModToolComposer = 3064;//1901
    public final static int UserBadgesComposer = 820;//3486
    public final static int GuildManageComposer = 1797;//601
    public final static int RemoveFriendComposer = -1106;//1106
    public final static int BannerTokenComposer = -3033;//3033
    public final static int UserDataComposer = 2337;//3491
    public final static int UserSearchResultComposer = 1542;//1573
    public final static int ModToolUserRoomVisitsComposer = 2728;//2129
    public final static int RoomUserRespectComposer = 2019;//2143
    public final static int RoomChatSettingsComposer = 864;//3710
    public final static int RemoveHabboItemComposer = 2192;//3233
    public final static int RoomUserRemoveComposer = 2529;//2819
    public final static int RoomHeightMapComposer = 3157;//1624
    public final static int RoomPetHorseFigureComposer = 1312;//1644
    public final static int PetErrorComposer = 1940;//779
    public final static int TradeUpdateComposer = 77;//1221
    public final static int PrivateRoomsComposer = 1251;//2653
    public final static int RoomModelComposer = 617;//2297
    public final static int RoomScoreComposer = 2925;//2805
    public final static int DoorbellAddUserComposer = 3556;//3198
    public final static int SecureLoginOKComposer = 294;//2753
    public final static int SessionRightsComposer = 376;//1313
    public final static int GuildMemberUpdateComposer = 3609;//862
    public final static int RoomFloorItemsComposer = 2648;//2124
    public final static int InventoryItemsComposer = 1927;//3029
    public final static int RoomUserTalkComposer = 2735;//612
    public final static int TradeStartComposer = 181;//3713
    public final static int InventoryItemUpdateComposer = 369;//1577
    public final static int ModToolIssueUpdateComposer = 2951;//3019
    public final static int MeMenuSettingsComposer = 1362;//15
    public final static int ModToolRoomInfoComposer = 84;//3722
    public final static int GuildListComposer = 3445;//587
    public final static int RecyclerLogicComposer = 3486;//1317
    public final static int UserHomeRoomComposer = 1730;//40
    public final static int RoomUserDanceComposer = 3848;//2091
    public final static int RoomSettingsUpdatedComposer = 3128;//543
    public final static int AlertPurchaseFailedComposer = 1860;//2602
    public final static int RoomDataComposer = 3986;//3995
    public final static int TagsComposer = 3149;//3487
    public final static int InventoryRefreshComposer = 1276;//3243
    public final static int RemovePetComposer = 3118;//467
    public final static int RemoveWallItemComposer = -2466;//2466
    public final static int TradeCompleteComposer = 418;//3766
    public final static int NewsWidgetsComposer = 72;//1345
    public final static int WiredEffectDataComposer = 1532;//1279
    public final static int BubbleAlertComposer = 1950;//777
    public final static int ReloadRecyclerComposer = 3918;//1093
    public final static int MoodLightDataComposer = 1869;//2033
    public final static int WiredRewardAlertComposer = 1603;//723
    public final static int CatalogPageComposer = 2551;//3518
    public final static int CatalogModeComposer = 1942;//3442
    public final static int ChangeNameUpdatedComposer = 311;//542
    public final static int AddFloorItemComposer = 1880;//1590
    public final static int DebugConsoleComposer = 2718;//336
    public final static int HallOfFameComposer = 92;//670
    public final static int WiredSavedComposer = 2429;//39
    public final static int RoomPaintComposer = 2205;//2979
    public final static int MarketplaceConfigComposer = 2120;//2524
    public final static int AddBotComposer = 254;//3043
    public final static int FriendRequestErrorComposer = 3134;//2467
    public final static int GuildMembersComposer = 1648;//3986
    public final static int RoomOpenComposer = 2561;//925 //2561 1945??
    public final static int ModToolRoomChatlogComposer = 3893;//3869
    public final static int DiscountComposer = 1768;//1061
    public final static int MarketplaceCancelSaleComposer = 2239;//1959
    public final static int RoomPetRespectComposer = 3841;//778
    public final static int RoomSettingsComposer = 2461;
    public final static int TalentTrackComposer = 2912;//2842
    public final static int CatalogPagesListComposer = 2353;//1684
    public final static int AlertLimitedSoldOutComposer = 604;//3290
    public final static int CatalogUpdatedComposer = 1311;//2028
    public final static int PurchaseOKComposer = 1636;//1189
    public final static int WallItemUpdateComposer = 3292;//2190
    public final static int TradeAcceptedComposer = 2316;//741
    public final static int AddWallItemComposer = 3887;//2156
    public final static int ModToolRoomEntryInfoComposer = 2561;//2644
    public final static int HotelViewDataComposer = 1579;//1374
    public final static int PresentItemOpenedComposer = 3424;//1540
    public final static int RoomUserRemoveRightsComposer = 3566;//1496
    public final static int UserBCLimitsComposer = 1942;//3442
    public final static int PetTrainingPanelComposer = 3760;//456
    public final static int RoomPaneComposer = 2561;//2644
    public final static int RedeemVoucherErrorComposer = 2571;//2630
    public final static int RoomCreatedComposer = 3648;//3373
    public final static int GenericAlertComposer = 3406;//1548
    public final static int GroupPartsComposer = 1817;//567
    public final static int ModToolIssueInfoComposer = 1848;//2473
    public final static int RoomUserWhisperComposer = 1428;//2323
    public final static int BotErrorComposer = 3932;//3420
    public final static int FreezeLivesComposer = 1128;//3526
    public final static int LoadFriendRequestsComposer = 1926;//2193
    public final static int MarketplaceSellItemComposer = 1165;//1932
    public final static int ClubDataComposer = 3753;//1919
    public final static int ProfileFriendsComposer = 168;//322
    public final static int MarketplaceOwnItemsComposer = 1181;//593
    public final static int RoomOwnerComposer = 2146;//1069
    public final static int WiredConditionDataComposer = 856;//1639
    public final static int ModToolUserInfoComposer = 1557;//2583
    public final static int UserWardrobeComposer = 773;//370
    public final static int UserUpdateCurrencyComposer = 1245;//1290
    public final static int RoomPetExperienceComposer = 2143;//717
    public final static int FriendChatMessageComposer = 625;//2094
    public final static int PetInformationComposer = 3752;//2286
    public final static int RoomThicknessComposer = 299;//2627
    public final static int AddPetComposer = 211;//3099
    public final static int UpdateStackHeightComposer = 1350;//1576
    public final static int RemoveBotComposer = 397;//2565
    public final static int RoomEnterErrorComposer = 1754;//1619
    public final static int PollQuestionsComposer = 1745;//3187
    public final static int GenericErrorMessages = 3478;//2839
    public final static int RoomWallItemsComposer = 3583;//395
    public final static int RoomUserEffectComposer = 3863;//3040
    public final static int PetBreedsComposer = 1433;//3302
    public final static int ModToolIssueChatlogComposer = 610;//1445
    public final static int RoomUserActionComposer = 2482;//12
    public final static int BotSettingsComposer = 772;//1522
    public final static int UserProfileComposer = 2023;//1503
    public final static int MinimailCountComposer = 1162;//2253
    public final static int UserAchievementScoreComposer = 2994;
    public final static int PetLevelUpComposer = 2329;
    public final static int UserPointsComposer = 1245;
    public final static int ReportRoomFormComposer = 3216;
    public final static int ModToolIssueHandledComposer = 2876;
    public final static int FloodCounterComposer = 1774;
    public final static int UpdateFailedComposer = 2612;
    public final static int FloorPlanEditorDoorSettingsComposer = 1633;
    public final static int FloorPlanEditorBlockedTilesComposer = 1874;
    public final static int BuildersClubExpiredComposer = 108;
    public final static int RoomSettingsSavedComposer = 0;*/

    public final static int FavoriteRoomsCountComposer = 235;//3829 //Updated
    public final static int UserCurrencyComposer = 3574;//3454 //Updated
    public final static int RedeemVoucherOKComposer = 391;//2719 //Updated
    public final static int RoomUserShoutComposer = 59;//3271 //Updated
    public final static int RoomUserStatusComposer = 12;//3386 //Updated
    public final static int RoomUserDataComposer = 2421;//3944 //Updated
    public final static int RoomAddRightsListComposer = 2589;//3611 //Updated
    public final static int RoomRemoveRightsListComposer = 3258; //Updated
    public final static int RoomRightsListComposer = 1964;//1891 //Updated
    public final static int RoomUserHandItemComposer = 2479;//3664 //Updated
    public final static int RoomUsersComposer = 1018;//1073 //Updated
    public final static int FriendRequestComposer = 2725;//2468 //Updated
    public final static int GuildBoughtComposer = 2244;//389 //Updated
    public final static int AddUserBadgeComposer = 3489;//3197 //Updated
    public final static int RecyclerCompleteComposer = 899;//2125 //Updated
    public final static int GuildBuyRoomsComposer = 575;//3938 //Updated
    public final static int FriendsComposer = 7;//2067 //Updated
    public final static int StalkErrorComposer = 1543;//1135 //Updated
    public final static int TradeCloseWindowComposer = 1850;//2000 //Updated
    public final static int RemoveFloorItemComposer = 2658;//36 //Updated
    public final static int InventoryPetsComposer = 3249;//435 //Updated
    public final static int UserCreditsComposer = 1557;//2204 //Updated
    public final static int WiredTriggerDataComposer = 951;//861 //Updated
    public final static int TradeStoppedComposer = 3869;//2020 //Updated
    public final static int ModToolUserChatlogComposer = 342;//3738 //Updated
    public final static int GuildInfoComposer = 3198;//2978 //Updated
    public final static int UserPermissionsComposer = 3409;//3872 //Updated
    public final static int PetNameErrorComposer = 1991;//3575 //Updated
    public final static int TradeStartFailComposer = 283;//2320 //Updated
    public final static int AddHabboItemComposer = 3454;//274 //Updated
    public final static int InventoryBotsComposer = 307;//320 //Updated
    public final static int CanCreateRoomComposer = 2877;//2463 //Updated
    public final static int MarketplaceBuyErrorComposer = 1299;//1823 //Updated
    public final static int BonusRareComposer = 2261;//3517 //Updated
    public final static int HotelViewComposer = 0x0404;//3152 //Updated
    public final static int UpdateFriendComposer = 3525;//170 //Updated
    public final static int FloorItemUpdateComposer = 2277;//648 //Updated
    public final static int RoomAccessDeniedComposer = 2897;//3953 //Updated
    public final static int GuildFurniWidgetComposer = 1875;//269 //Updated
    public final static int GiftConfigurationComposer = 1715;//1048 //Updated
    public final static int UserClubComposer = 2018;//2869 //Updated
    public final static int InventoryBadgesComposer = 399;//1629 //Updated
    public final static int RoomUserTypingComposer = 2327;//3624 //Updated
    public final static int GuildJoinErrorComposer = 1761;//1952 //Updated
    public final static int RoomCategoriesComposer = 1824;//3241 //Updated
    public final static int InventoryAchievementsComposer = 2539;//3295 //Updated
    public final static int MarketplaceItemInfoComposer = 894;//1066 //Updated
    public final static int RoomRelativeMapComposer = 3287;//1975 //Updated
    public final static int ModToolComposerTwo = 1890;//1917 //Updated
    public final static int ModToolComposerOne = 1722;//2303 //Updated
    public final static int RoomRightsComposer = 1421;//520 //Updated
    public final static int ObjectOnRollerComposer = 450;//1488 //Updated
    public final static int PollStartComposer = 3206;//749 //Updated
    public final static int GuildRefreshMembersListComposer = 1661;//2055 //Updated
    public final static int UserPerksComposer = 3281;//1218 //Updated
    public final static int UserCitizinShipComposer = 3285;//3439 //Updated
    public final static int PublicRoomsComposer = 972;//2506 //Updated //-> 580 (Oud)?
    public final static int MarketplaceOffersComposer = 2440;//3289 //Updated
    public final static int ModToolComposer = 3087;//1901 //Updated
    public final static int UserBadgesComposer = 2442;//3486 //Updated
    public final static int GuildManageComposer = 2129;//601 //Updated
    public final static int RemoveFriendComposer = -1106;//1106
    public final static int BannerTokenComposer = -3033;//3033
    public final static int UserDataComposer = 558;//3491 //Updated
    public final static int UserSearchResultComposer = 1590;//1573 //Updated
    public final static int ModToolUserRoomVisitsComposer = 520;//2129 //Updated
    public final static int RoomUserRespectComposer = 3137;//2143 //Updated
    public final static int RoomChatSettingsComposer = 901;//3710 //Updated
    public final static int RemoveHabboItemComposer = 2368;//3233 //Updated
    public final static int RoomUserRemoveComposer = 364;//2819 //Updated
    public final static int RoomHeightMapComposer = 3491;//1624 //Updated
    public final static int RoomPetHorseFigureComposer = 3140;//1644 //Updated
    public final static int PetErrorComposer = 3013;//779 //Updated
    public final static int TradeUpdateComposer = 1649;//1221 //Update
    public final static int PrivateRoomsComposer = 2523;//2653 //Updated
    public final static int RoomModelComposer = 1186;//2297 //Updated
    public final static int RoomScoreComposer = 1781;//2805 //Updated
    public final static int KnockKnockComposer = 2988;//3198 //Updated
    public final static int SecureLoginOKComposer = 1992;//2753 //Updated
    public final static int SessionRightsComposer = 2934;//1313 //Updated
    public final static int GuildMemberUpdateComposer = 3641;//862 //Updated
    public final static int RoomFloorItemsComposer = 144;//2124 //Updated
    public final static int InventoryItemsComposer = 1559;//3029 //Updated
    public final static int RoomUserTalkComposer = 874;//612 //Updated
    public final static int TradeStartComposer = 833;//3713 //Updated
    public final static int InventoryItemUpdateComposer = 2204;//1577 //Updated
    public final static int ModToolIssueUpdateComposer = 471;//3019 //Updated
    public final static int MeMenuSettingsComposer = 3437;//15 //Updated
    public final static int ModToolRoomInfoComposer = 1328;//3722 //Updated
    public final static int GuildListComposer = 3082;//587 //Updated
    public final static int RecyclerLogicComposer = 1527;//1317 //Updated
    public final static int UserHomeRoomComposer = 3876;//40 //Updated
    public final static int RoomUserDanceComposer = 2184;//2091 //Updated
    public final static int RoomSettingsUpdatedComposer = 1069;//543 //Updated
    public final static int AlertPurchaseFailedComposer = 2663;//2602 //Updated
    public final static int RoomDataComposer = 624;//3995 //Updated
    public final static int TagsComposer = 456;//3487 //Updated
    public final static int InventoryRefreshComposer = 2352;//3243 //Updated
    public final static int RemovePetComposer = 775;//467 //Updated
    public final static int RemoveWallItemComposer = 1980;//2466 //Updated
    public final static int TradeCompleteComposer = 1668;//3766 //Updated
    public final static int NewsWidgetsComposer = 3958;//1345 //Updated
    public final static int WiredEffectDataComposer = 2101;//1279 //Updated
    public final static int BubbleAlertComposer = 1569;//777 //Updated
    public final static int ReloadRecyclerComposer = 3704;//1093 //Updated
    public final static int MoodLightDataComposer = 2780;//2033 //Updated
    public final static int WiredRewardAlertComposer = 1539;//723 //Updated
    public final static int CatalogPageComposer = 1384;//3518 //Updated
    public final static int CatalogModeComposer = 3408;//3442 //Updated
    public final static int RoomUserUpdateNameComposer = 1263;//542 //Updated
    public final static int AddFloorItemComposer = 2502;//1590 //Updated
    public final static int DebugConsoleComposer = 1003;//336 //Updated
    public final static int HallOfFameComposer = 3736;//670 //Updated
    public final static int WiredSavedComposer = 598;//39 //Updated
    public final static int RoomPaintComposer = 2225;//2979 //Updated
    public final static int MarketplaceConfigComposer = 739;//2524 //Updated
    public final static int AddBotComposer = 1771;//3043 //Updated
    public final static int FriendRequestErrorComposer = 3601;//2467 //Updated
    public final static int GuildMembersComposer = 1788;//3986 //Updated
    public final static int RoomOpenComposer = 2266;//925 //2561 1945?? //Updated
    public final static int ModToolRoomChatlogComposer = 1633;//3869 //Updated
    public final static int DiscountComposer = 3254;//1061 //Updated
    public final static int MarketplaceCancelSaleComposer = 751;//1959 //Updated
    public final static int RoomPetRespectComposer = 2348;//778 //Updated
    public final static int RoomSettingsComposer = 3622; //Updated
    public final static int TalentTrackComposer = 3235;//2842 //Updated
    public final static int CatalogPagesListComposer = 402;//1684 //Updated
    public final static int AlertLimitedSoldOutComposer = 3467;//3290 //Updated
    public final static int CatalogUpdatedComposer = 2452;//2028 //Updated
    public final static int PurchaseOKComposer = 2316;//1189 //Updated
    public final static int WallItemUpdateComposer = 21;//2190 //Updated
    public final static int TradeAcceptedComposer = 3042;//741 //Updated
    public final static int AddWallItemComposer = 706;//2156 //Updated
    public final static int ModToolRoomEntryInfoComposer = 381;//2644 //Updated
    public final static int HotelViewDataComposer = 3810;//1374 //Updated
    public final static int PresentItemOpenedComposer = 2004;//1540 //Updated TODO: -> BELANGRIJK: Zoek op: .push(new RoomUsersHandler voor andere items??
    public final static int RoomUserRemoveRightsComposer = 61;//1496 //Updated TODO: Doet geen ene flikker?
    public final static int UserBCLimitsComposer = 3408;//3442 //Updated
    public final static int PetTrainingPanelComposer = 1897;//456 //Updated
    public final static int RoomPaneComposer = 381;//2644 //Updated
    public final static int RedeemVoucherErrorComposer = 1809;//2630 //Updated
    public final static int RoomCreatedComposer = 3168;//3373 //Updated
    public final static int GenericAlertComposer = 1406;//1548 //Updated
    public final static int GroupPartsComposer = 3569;//567 //Updated
    public final static int ModToolIssueInfoComposer = 3928;//2473 //Updated
    public final static int RoomUserWhisperComposer = 2945;//2323 //Updated
    public final static int BotErrorComposer = 1932;//3420 //Updated
    public final static int FreezeLivesComposer = 3985;//3526 //Updated
    public final static int LoadFriendRequestsComposer = 683;//2193 //Updated
    public final static int MarketplaceSellItemComposer = 92;//1932 //Updated
    public final static int ClubDataComposer = 2273;//1919 //Updated
    public final static int ProfileFriendsComposer = 2231;//322 //Updated
    public final static int MarketplaceOwnItemsComposer = 311;//593 //Updated
    public final static int RoomOwnerComposer = 3608;//1069 //Updated
    public final static int WiredConditionDataComposer = 3969;//1639 //Updated
    public final static int ModToolUserInfoComposer = 828;//2583 //Updated
    public final static int UserWardrobeComposer = 406;//370 //Updated
    public final static int RoomPetExperienceComposer = 3711;//717 //Updated
    public final static int FriendChatMessageComposer = 3957;//2094 //Updated
    public final static int PetInformationComposer = 64;//2286 //Updated
    public final static int RoomThicknessComposer = 2145;//2627 //Updated
    public final static int AddPetComposer = 2194;//3099 //Updated
    public final static int UpdateStackHeightComposer = 597;//1576 //Updated
    public final static int RemoveBotComposer = 3691;//2565 //Updated
    public final static int RoomEnterErrorComposer = 817;//1619 //Updated
    public final static int PollQuestionsComposer = 1143;//3187 //Updated
    public final static int GenericErrorMessages = 1169;//2839 //Updated
    public final static int RoomWallItemsComposer = 3524;//395 //Updated
    public final static int RoomUserEffectComposer = 579;//3040 //Updated
    public final static int PetBreedsComposer = 1947;//3302 //Updated
    public final static int ModToolIssueChatlogComposer = 3053;//1445 //Updated
    public final static int RoomUserActionComposer = 3741;//12 //Updated
    public final static int BotSettingsComposer = 3092;//1522 //Updated
    public final static int UserProfileComposer = 3049;//1503 //Updated
    public final static int MinimailCountComposer = 761;//2253 //Updated
    public final static int UserAchievementScoreComposer = 3681; //Updated
    public final static int PetLevelUpComposer = 3100; //Updated
    public final static int UserPointsComposer = 1439; //Updated
    public final static int ReportRoomFormComposer = 2573; //Updated
    public final static int ModToolIssueHandledComposer = 1297; //Updated
    public final static int FloodCounterComposer = 2106; //Updated
    public final static int UpdateFailedComposer = 3006; //Updated
    public final static int FloorPlanEditorDoorSettingsComposer = 1988; //Updated
    public final static int FloorPlanEditorBlockedTilesComposer = 3388; //Updated
    public final static int BuildersClubExpiredComposer = 25; //Updated
    public final static int RoomSettingsSavedComposer = 2769; //Updated
    public final static int MessengerInitComposer = 3682; //Updated
    public final static int UserClothesComposer = 2651; //Updated
    public final static int UserEffectsListComposer = 1348; //Updated
    public final static int NewUserIdentityComposer = 1733; //Updated
    public final static int NewNavigatorEventCategoriesComposer = 1391;
    public final static int NewNavigatorCollapsedCategoriesComposer = 3110;
    public final static int NewNavigatorLiftedRoomsComposer = 2469;
    public final static int NewNavigatorSavedSearchesComposer = 3349;
    public final static int RoomUnitUpdateUsernameComposer = 2063;
    public final static int PostItDataComposer = 3443;
    public final static int ModToolReportReceivedAlertComposer = 3453;
    public final static int ModToolIssueResponseAlertComposer = 971;
    public final static int AchievementListComposer = 1417;
    public final static int AchievementProgressComposer = 1550;
    public final static int AchievementUnlockedComposer = 1335;
    public final static int ClubGiftsComposer = 197;
    public final static int MachineIDComposer = 3609;
    public final static int PongComposer = 1349;

    //Uknown but work
    public final static int UnknownComposer4 = 1411;
    public final static int UnknownComposer5 = 568;
    public final static int UnknownComposer6 = 32;
    public final static int NewNavigatorMetaDataComposer = 1129;
    public final static int NewNavigatorSearchResultsComposer = 3203;
    public final static int MysticBoxStartOpenComposer = 3513;
    public final static int MysticBoxCloseComposer = 2581;
    public final static int MysticBoxPrizeComposer = 3040;
    public final static int RentableSpaceInfoComposer = 194;
    public final static int RentableSpaceUnknownComposer = 2051;
    public final static int RentableSpaceUnknown2Composer = 1075;
    public final static int GuildConfirmRemoveMemberComposer = 3940;

    public final static int LandingViewGetBadgeButtonConfig = 940;
    public final static int EpicPopupFrameComposer = 916;
    public final static int BaseJumpLoadGameURL = 871;
    public final static int RoomUserTagsComposer = 799;
    public final static int RoomInviteErrorCodeComposer = 76;
    public final static int PostItStickyPoleOpenComposer = 753;
    public final static int NewYearResolutionProgressComposer = 741;
    public final static int ClubGiftReceivedComposer = 735; //:test 735 s:t i:1 s:s i:230 s:throne i:1 b:1 i:1 i:10;
    public final static int ItemStateComposer = 664; //TODO: (i: itemId, i:state)
    public final static int ItemExtraDataComposer = 654; //:test 654 s:92015 i:1 i:1 s:renterId s:0 s:0.0 s:0

    //NotSure Needs Testing
    public final static int QuestionInfoComposer = 3047;
    public final static int TalentTrackEmailVerifiedComposer = 866;
    public final static int TalentTrackEmailFailedComposer = 369;
    public final static int UnknownAvatarEditorComposer = 72;

    public final static int UnknownGuildComposer = 588; //Could be related to forums.

    public final static int UnknownGuildForumComposer1 = 600;
    public final static int UnknownGuildForumComposer2 = 1291;
    public final static int UnknownGuildForumComposer3 = 1147;
    public final static int UnknownGuildForumComposer4 = 3420;
    public final static int UnknownGuildForumComposer5 = 324;
    public final static int UnknownGuildForumComposer6 = 2161;
    public final static int UnknownGuildForumComposer7 = 3853;
    public final static int UnknownGuildForumComposer8 = 2695;
    public final static int UnknownGuildForumComposer9 = 3077;

    public final static int GuideSessionAttachedComposer = 3549;
    public final static int GuideSessionDetachedComposer = 2987;
    public final static int GuideSessionStartedComposer = 3048;
    public final static int GuideSessionEndedComposer = 3222;
    public final static int GuideSessionErrorComposer = 978;
    public final static int GuideSessionMessageComposer = 1860;
    public final static int GuideSessionRequesterRoomComposer = 1861;
    public final static int GuideSessionInvitedToGuideRoomComposer = 2959;
    public final static int GuideSessionPartnerIsTypingComposer = 2955;

    public final static int GuideToolsComposer = 117;
    public final static int GuardianNewReportReceivedComposer = 3123;
    public final static int GuardianVotingRequestedComposer = 2331;
    public final static int GuardianVotingVotesComposer = 3107; //:test 3107 i:10 i:0 i:1 i:2 i:3 i:4 i:5 i:0 i:1 i:2 i:3
    public final static int GuardianVotingResultComposer = 91;
    public final static int GuardianVotingTimeEnded = 556;

    public final static int RoomMutedComposer = 554;

    public final static int RoomUnknown1Composer = 1241;
    public final static int RoomUnknown2Composer = 535;
    public final static int RoomUnknown3Composer = 2342;

    public final static int UnknownEffectsComposer = 53; //:test 53 i:10

    public final static int OldPublicRoomsComposer = 580;
    public final static int ItemStateComposer2 = 569;

    public final static int HotelWillCloseInMinutesComposer = 518; //:test 518 i:10
    public final static int HotelWillCloseInMinutesAndBackInComposer = 2860; //:test 2860 b:1 i:1 i:2
    public final static int HotelClosesAndWillOpenAtComposer = 2168; //:test 2168 i:0 i:1 b:1
    public final static int HotelClosedAndOpensComposer = 888;
    public final static int StaffAlertAndOpenHabboWayComposer = 1035;
    public final static int StaffAlertWithLinkComposer = 1189;
    public final static int StaffAlertWIthLinkAndOpenHabboWayComposer = 3964;

    public final static int RoomMessagesPostedCountComposer = 1520; //:test 1520 i:12 s:Derp i:100
    public final static int CantScratchPetNotOldEnoughComposer = 118; //:test 118 i:1 i:2
    public final static int PetBoughtNotificationComposer = 1040;
    public final static int MessagesForYouComposer = 2506;
    public final static int UnknownStatusComposer = 1648; //:test 1648 i:0
    public final static int CloseWebPageComposer = 320;
    public final static int PickMonthlyClubGiftNotificationComposer = 2786; //:test 2786 i:0
    public final static int RemoveGuildFromRoomComposer = 514; //:test 514 i:12
    public final static int RoomBannedUsersComposer = 504;
    public final static int OpenRoomCreationWindowComposer = 497; //:test 497
    public final static int ItemsDataUpdateComposer = 466;
    public final static int WelcomeGiftComposer = 445; //:test 445 s:derp@derp.com b:0 b:1 i:230 b:1
    /* TODO: */ public final static int SimplePollStartComposer = 3047; //:test 3047 s:a i:10 i:20 i:10000 i:1 i:1 i:3 s:abcdefghijklmnopqrstuvwxyz12345678901234? i:1 s:a s:b
    public final static int RoomNoRightsComposer = 3929; //Removes also the 'use' button. :D
    public final static int GuildEditFailComposer = 3918;
    public final static int MinimailNewMessageComposer = 3903;
    public final static int RoomFilterWordsComposer = 3889;
    public final static int VerifyMobileNumberComposer = 3872;
    public final static int NewUserGiftComposer = 3098; //:test 3098 i:1 i:1 i:1 i:2 s:a.png i:1 s:a s:b s:a.png i:1 s:a s:b
    public final static int UpdateUserLookComposer = 3864;
    public final static int RoomUserIgnoredComposer = 3858; //:test 3858 i:1 s:a     1/2 ignore. 3 unignore s:Username
    public final static int PetBreedingFailedComposer = 3851; //:test i:0 i:1
    public final static int HabboNameChangedComposer = 3849; //:test 3849 i:0 s:aa i:3 s:a s:b s:c
    public final static int RoomUserNameChangedComposer = 2063; //:test 2063 i:1 i:1 s:Derp
    public final static int LoveLockFurniStartComposer = 2883; //:test 2883 i:91638 b:1
    public final static int LoveLockFurniFriendConfirmedComposer = 2746; //:test 2746 i:91638
    public final static int LoveLockFurniFinishedComposer = 3825; //:test 3825 i:91638
    public final static int OpenPetPackageNameValidationComposer = 38;
    public final static int GameCenterFeaturedPlayersComposer = 3788;
    public final static int HabboMallComposer = 3770; //Unused as far as I can tell.
    public final static int TargetedOfferComposer = 1495; //:test 1495 i:1 i:1 s:a s:b i:1 i:2 i:5 i:9 i:1000000 s:c s:d s:a.png s:e.png i:2 s:f.png s:g.png
    public final static int LeprechaunStarterBundleComposer = 3766; // :test 3766 i:4
    public final static int VerifyMobilePhoneWindowComposer = 3695; //:test 3695 i:0 i:1 i:2
    public final static int VerifyMobilePhoneCodeWindowComposer = 2160; //:test 2160 i:1 i:0
    public final static int VerifyMobilePhoneDoneComposer = 3109;
    public final static int RoomUserReceivedHandItemComposer = 3673; //:test 3673 i:2 i:2
    public final static int MutedWhisperComposer = 3326; //:test 3326 i:1000
    public final static int UnknownHintComposer = 3484;
    public final static int BullyReportClosedComposer = 3401; //:test 3401 i:0
    public final static int PromoteOwnRoomsListComposer = 3376; //:test 3376 b:1 i:1 i:1 s:derp b:1 //NOT SURE
    public final static int NotEnoughPointsTypeComposer = 334; //:test 334 b:0 b:1 i:105
    public final static int WatchAndEarnRewardComposer = 3325; //:test 3325 s:s i:230 s:throne s:derp
    public final static int NewYearResolutionComposer = 327;
    public final static int WelcomeGiftErrorComposer =  3268; //:test 3268 i:3
    public final static int RentableItemBuyOutPriceComposer = 3264;
    public final static int VipTutorialsStartComposer = 3217;
    public final static int NewNavigatorCategoryUserCountComposer = 3166;
    public final static int RoomThumbnailSavedComposer = 3117; //:test 3117
    public final static int RoomEditSettingsErrorComposer = 3094; //:test 3094 i:15 i:7 s:derp
    public final static int GuildAcceptMemberErrorComposer = 305; //:test 305 i:12 i:1
    public final static int MostUselessErrorAlertComposer = 2944;
    public final static int AchievementsConfigurationComposer = 2937;
    public final static int PetBreedingResultComposer = 2907;
    public final static int RoomUserQuestionAnsweredComposer = 2866;
    public final static int PetBreedingStartComposer = 285; //:test 285 i:0 i:1 i:2
    public final static int CustomNotificationComposer = 2835;
    public final static int UpdateStackHeightTileHeightComposer = 2830; //:test 2830 i:86476 i:999
    public final static int HotelViewCustomTimerComposer = 2828;
    public final static int MarketplaceItemPostedComposer = 2797; //:test 2797 i:0
    public final static int UnknownHabboQuizComposer = 2750;
    public final static int GuildFavoriteRoomUserUpdateComposer = 275;
    public final static int RoomAdErrorComposer = 2692;
    public final static int NewNavigatorSettingsComposer = 2617; //:test 2617 i:10 i:10 i:2 i:0 b:0 i:10
    public final static int CameraPublishWaitMessageComposer = 2612; //:test 2612 b:1 i:1 s:derp
    public final static int RoomInviteComposer = 2608; //:test 2608 i:0 s:derp
    public final static int BullyReportRequestComposer = 1917; //:test 1917 i:1 i:1 i:1 b:1 s:a s:b s:c
    public final static int UnknownHelperComposer = 2544;
    public final static int HelperRequestDisabledComposer = 1125; //:test 1125 s:derp
    public final static int RoomUnitIdleComposer = 2462; //:test 2462 i:2 b:0
    public final static int QuestCompletedComposer = 2459;
    public final static int UnkownPetPackegeComposer = 2417; //:test 2417 i:2 s:a s:b s:c s:d
    public final static int ForwardToRoomComposer = 2382; //:test 2382 i:16
    public final static int EffectsListEffectEnableComposer = 2381; //:test 2381 i:20 i:1000 b:1
    public final static int CompetitionEntrySubmitResultComposer = 2308;
    public final static int ExtendClubMessageComposer = 2304; //:test 2304 i:1 s:derp b:1 i:10 i:0 i:0 b:1 i:1 i:1 b:1 i:1 i:1 i:1 i:1 i:1 i:1
    public final static int HotelViewConcurrentUsersComposer = 2286;
    public final static int InventoryAddEffectComposer = 215; //:test 215 i:120 i:0 i:10 b:1
    public final static int TalentLevelUpdateComposer = 1956;
    public final static int BullyReportedMessageComposer = 1858; //:test 1858 i:0
    public final static int UnknownQuestComposer3 = 1851; //_SafeStr_9789
    public final static int FriendToolbarNotificationComposer = 1839; //:test 1839 s:0 i:0 s:ADM
    public final static int MessengerErrorComposer = 1838;//:test 1838 i:1 i:0 i:0
    public final static int CameraPriceComposer = 1676;//:test 1676 i:2 i:2 i:105
    public final static int PetBreedingCompleted = 1628; //:test 1628 i:1 i:0
    public final static int RoomUserUnbannedComposer = 1625;
    public final static int HotelViewCommunityGoalComposer = 1579; //:test 1579 b:1 i:0 i:1 i:2 i:3 i:4 i:5 s:a i:6 i:1 i:1
    public final static int UnknownRoomVisitsComposer = 155; //:test 155 i:2 i:1 s:a s:b i:2 s:c s:d
    public final static int CanCreateEventComposer = 1542;
    public final static int UnknownGroupComposer = 1507;
    public final static int YoutubeMessageComposer1 = 1477;
    public final static int YoutubeMessageComposer2 = 1475;
    public final static int RoomCategoryUpdateMessageComposer = 1433; //:test 1433 i:0
    public final static int UnknownQuestComposer2 = 1415;
    public final static int GiftReceiverNotFoundComposer = 1374; //:test 1374
    public final static int ConvertedForwardToRoomComposer = 1323; //:test 1323 s:lol i:14
    public final static int FavoriteRoomChangedComposer = 1317;
    public final static int AlertPurchaseUnavailableComposer = 1303; //:test 1303 i:0
    public final static int PetBreedingStartFailedComposer = 1252; //:test 1252 i:0
    public final static int DailyQuestComposer = 1244;
    public final static int NewNavigatorRoomEventComposer = 1162;
    public final static int HotelViewHideCommunityVoteButtonComposer = 115;
    public final static int CatalogSearchResultComposer = 1142 ; //:test 1142 i:4 s:a b:1 i:1 i:0 i:0 b:0 i:1 s:s i:230 s:throne i:230 b:0 i:0 b:0
    public final static int FriendFindingRoomComposer = 1037; //:test 1037 b:0
    public final static int UnknownQuestComposer1 = 1010;


    /**
     * Music Disks / Trax / Jukebox
     */
    public final static int JukeBoxMySongsComposer = 2224;
    public final static int JukeBoxNowPlayingMessageComposer = 2197;
    public final static int JukeBoxPlaylistFullComposer = 2732;
    public final static int JukeBoxPlayListUpdatedComposer = 2858;
    public final static int JukeBoxPlayListAddSongComposer = 1558;
    public final static int JukeBoxPlayListComposer = 3011;
    public final static int JukeBoxTrackDataComposer = 442;
    public final static int JukeBoxTrackCodeComposer = 3588;


}
