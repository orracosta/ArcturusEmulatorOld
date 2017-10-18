package com.eu.habbo.messages.incoming;

public class Incoming_1006
{


    /*
        Achievements
     */
    //public static final int RequestAchievementsEvent = 2562;

    /*
        Wired
     */
    /*public static final int WiredTriggerSaveDataEvent = 3201;
    public static final int WiredEffectSaveDataEvent = 3006;
    public static final int WiredConditionSaveDataEvent = 1041;*/

    /*
        Pets
     */
    /*public static final int RequestPetInformationEvent = 777;
    public static final int PetPickupEvent = 863;
    public static final int ScratchPetEvent = 3464;
    public static final int RequestPetTrainingPanelEvent = 2476;
    public static final int HorseUseItemEvent = 1616;
    public static final int HorseRideSettingsEvent = 2448;
    public static final int HorseRideEvent = 3268;*/

    /*
        Groups
     */
   /* public static final int RequestGuildBuyRoomsEvent = 701;
    public static final int RequestGuildPartsEvent = 2344;
    public static final int RequestGuildBuyEvent = 872;
    public static final int RequestGuildInfoEvent = 2079;
    public static final int RequestGuildManageEvent = 2418;
    public static final int RequestGuildMembersEvent = 2382;
    public static final int RequestGuildJoinEvent = 77;
    public static final int GuildChangeNameDescEvent = 3232;
    public static final int GuildChangeBadgeEvent = 1475;
    public static final int GuildChangeColorsEvent = 463;
    public static final int GuildRemoveMemberEvent = 3002;
    public static final int GuildRemoveAdminEvent = 3264;
    public static final int GuildChangeSettingsEvent = 2156;
    public static final int GuildAcceptMembershipEvent = 1066;
    public static final int GuildDeclineMembershipEvent = 1320;
    public static final int GuildSetAdminEvent = 2957;
    public static final int GuildSetFavoriteEvent = 1463;
    public static final int RequestOwnGuildsEvent = 2240;
    public static final int RequestGuildFurniWidgetEvent = 3031;

    /*
        Trading
     */
    /*public static final int TradeStartEvent = 466;
    public static final int TradeOfferItemEvent = 2223;
    public static final int TradeCancelOfferItemEvent = 373;
    public static final int TradeAcceptEvent = 1062;
    public static final int TradeUnAcceptEvent = 1131;
    public static final int TradeConfirmEvent = 1144;
    public static final int TradeCloseEvent = 3387;

    /*
        ModTool
     */
   /* public static final int ModToolRequestRoomInfoEvent = 225;
    public static final int ModToolRequestRoomChatlogEvent = 1903;
    public static final int ModToolRequestUserInfoEvent = 3077;
    public static final int ModToolPickTicketEvent = 3975;
    public static final int ModToolAlertEvent = 1980;
    public static final int ModToolKickEvent = 3418;
    public static final int ModToolBanEvent = 2930;
    public static final int ModToolWarnEvent = 3763;
    public static final int ModToolRequestUserChatlogEvent = 203;
    public static final int ModToolRoomAlertEvent = 295;
    public static final int ModToolChangeRoomSettingsEvent = 2530;
    public static final int ModToolRequestRoomVisitsEvent = 2549;

    /*
        Catalog
     */
    /*public static final int Unknown3 = 3794; //Catalogue Initialized Composer?
    public static final int RequestRecylerLogicEvent = 1077;
    public static final int RequestCatalogIndexEvent = 81;
    public static final int GetMarketplaceConfigEvent = 1952; //Configuration IDs composer?
    public static final int RequestDiscountEvent = 3794;
    public static final int RequestCatalogModeEvent = 3406;
    public static final int RequestGiftConfigurationEvent = 382;
    public static final int RequestCatalogPageEvent = 1754;
    public static final int CatalogBuyItemEvent = 2967;
    public static final int CatalogBuyItemAsGiftEvent = 2474;
    public static final int RedeemVoucherEvent = 1571;
    public static final int ReloadRecyclerEvent = 2779;
    public static final int RecycleEvent = 2318;
    public static final int OpenRecycleBoxEvent = 3977;
    public static final int RequestPetBreedsEvent = 1064;
    public static final int CheckPetNameEvent = 678;
    public static final int GetClubDataEvent = 1608;

    /*
        Polls
     */
   /* public static final int CancelPollEvent = 276;
    public static final int GetPollDataEvent = 1960;
    public static final int AnswerPollEvent = 1847;

    /*
        Marketplace
     */
    /*public static final int RequestOwnItemsEvent = 829;
    public static final int TakeBackItemEvent = 2910;
    public static final int RequestOffersEvent = 541;
    public static final int RequestItemInfoEvent = 36;
    public static final int BuyItemEvent = 3878;
    public static final int RequestSellItemEvent = 2087;
    public static final int SellItemEvent = 172;
    public static final int RequestCreditsEvent = 2138;


    /*
        Handshake
     */
   /* public static final int ReleaseVersionEvent = 4000;
    public static final int RequestBannerToken = 2619;
    public static final int GenerateSecretKeyEvent = 3575;
    public static final int MachineIDEvent = 512;
    public static final int SecureLoginEvent = 1309;

    /*
        Friends
     */
    /*public static final int RequestFriendsEvent = 776;
    public static final int ChangeRelationEvent = 2521;
    public static final int RemoveFriendEvent = 2786;
    public static final int SearchUserEvent = 1267;
    public static final int FriendRequestEvent = 3765;
    public static final int AcceptFriendRequest = 333;
    public static final int FriendPrivateMessageEvent = 1527;
    public static final int RequestFriendRequestEvent = 275;
    public static final int StalkFriendEvent = 3394;

    /*
        HotelView
     */
   /* public static final int HotelViewEvent = 3266;
    public static final int RequestBonusRareEvent = 1199;
    public static final int RequestNewsListEvent = 896;
    public static final int HotelViewDataEvent = 595;

    /*
        Navigator
     */
    /*public static final int RequestRoomCategoriesEvent = 1431;
    public static final int RequestPublicRoomsEvent = 3735;
    public static final int RequestPopularRoomsEvent = 235;
    public static final int RequestHighestScoreRoomsEvent = 1728;
    public static final int RequestMyRoomsEvent = 2676;
    public static final int RequestCanCreateRoomEvent = 3844;
    public static final int RequestPromotedRoomsEvent = 3030;
    public static final int RequestCreateRoomEvent = 3524;
    public static final int RequestTagsEvent = 1337;
    public static final int SearchRoomsByTagEvent = 1956;
    public static final int SearchRoomsEvent = 3551;
    public static final int SearchRoomsFriendsNowEvent = 3306;
    public static final int SearchRoomsFriendsOwnEvent = 3478;
    public static final int SearchRoomsWithRightsEvent = 3785;
    public static final int SearchRoomsInGroupEvent = 1160;
    public static final int SearchRoomsMyFavoriteEvent = 1532;
    public static final int SearchRoomsVisitedEvent = 589;


    /*
        Rooms
     */
   /* public static final int RequestRoomLoadEvent = 3751; //3751
    public static final int RoomVoteEvent = 242;
    public static final int RequestRoomSettingsEvent = 3741;
    public static final int RequestHeightmapEvent = 308;
    public static final int RequestRoomDataEvent = 183;
    public static final int RoomSettingsSaveEvent = 2384;
    public static final int RoomUserTalkEvent = 3349;
    public static final int RoomUserShoutEvent = 936;
    public static final int RoomUserWhisperEvent = 1362;
    public static final int RoomUserActionEvent = 475;
    public static final int RoomUserSitEvent = 484;
    public static final int RoomUserDanceEvent = 1139;
    public static final int RoomUserSignEvent = 2189;
    public static final int RoomUserGiveRespectEvent = 2041;
    public static final int RoomUserWalkEvent = 951;
    public static final int RoomPlaceItemEvent = 474;
    public static final int RotateMoveItemEvent = 194;
    public static final int MoveWallItemEvent = 1029;
    public static final int RoomPickupItemEvent = 152;
    public static final int RoomPlacePaintEvent = 874;
    public static final int RoomUserStartTypingEvent = 3684;
    public static final int RoomUserStopTypingEvent = 2005;
    public static final int ToggleFloorItemEvent = 120;
    public static final int ToggleWallItemEvent = 2650;
    public static final int RoomUserLookAtPoint = 1365;
    public static final int RoomBackgroundEvent = 2715;
    public static final int MannequinSaveNameEvent = 2098;
    public static final int MannequinSaveLookEvent = 1804;
    public static final int AdvertisingSaveEvent = 68;
    public static final int RoomUserDropHandItemEvent = 157;
    public static final int BotPlaceEvent = 148;
    public static final int BotPickupEvent = 2865;
    public static final int BotSaveSettingsEvent = 1384;
    public static final int BotSettingsEvent = 3098;
    public static final int TriggerDiceEvent = 987;
    public static final int CloseDiceEvent = 1739;
    public static final int TriggerColorWheelEvent = 2367;
    public static final int MoodLightSettingsEvent = 2468;
    public static final int MoodLightTurnOnEvent = 1181;
    public static final int RedeemItemEvent = 3599;
    public static final int RequestRoomRightsEvent = 2928;
    public static final int RoomRemoveAllRightsEvent = 405;
    public static final int RoomUserGiveRightsEvent = 2221;
    public static final int RoomUserRemoveRightsEvent = 3639;
    public static final int PetPlaceEvent = 3711;
    public static final int RoomUserKickEvent = 1874;
    public static final int SetStackHelperHeightEvent = 2540;

    /*
        Users
     */
   /* public static final int RequestUserDataEvent = 112;
    public static final int RequestUserCreditsEvent = 938;
    public static final int RequestUserClubEvent = 3874;
    public static final int RequestMeMenuSettingsEvent = 671;
    public static final int RequestUserCitizinShipEvent = 3096;
    public static final int RequestUserProfileEvent = 532;
    public static final int RequestProfileFriendsEvent = 3925;
    public static final int RequestUserTagsEvent = 3102;
    public static final int RequestUserWardrobeEvent = 1251;
    public static final int SaveWardrobeEvent = 637;
    public static final int UserSaveLookEvent = 2404;
    public static final int SaveMottoEvent = 2832;
    public static final int UserWearBadgeEvent = 2473;
    public static final int RequestWearingBadgesEvent = 3968;

    /*
        Inventory
     */
   /* public static final int RequestInventoryItemsEvent = 372; // 1223
    public static final int RequestInventoryPetsEvent = 1900;
    public static final int RequestInventoryBadgesEvent = 3072;
    public static final int RequestInventoryBotsEvent = 1351;

    /*
        Not correctly implemented
     */
    /*public static final int RequestResolutionEvent = 1854;*/

    /*public static final int MannequinSaveLookEvent = 2252;//1804
    public static final int RequestCatalogPageEvent = 1904;//1754
    public static final int RequestWearingBadgesEvent = 3364;//3968
    public static final int BotPickupEvent = 1890;//2865
    public static final int HorseRideEvent = 1425;//3268
    public static final int RequestCreateRoomEvent = 1075;//3524
    public static final int SaveMottoEvent = 3060;//2832
    public static final int GenerateSecretKeyEvent = 2342;//3575
    public static final int ModToolAlertEvent = 641;//1980
    public static final int TradeAcceptEvent = 843;//1062
    public static final int RequestCatalogModeEvent = 2941;//3406
    public static final int RequestUserCreditsEvent = 3985;//938
    public static final int FriendPrivateMessageEvent = 2046;//1527
    public static final int CloseDiceEvent = 2438;//1739
    public static final int RoomUserRemoveRightsEvent = 1872;//3639
    public static final int GuildDeclineMembershipEvent = 3464;//1320
    public static final int AnswerPollEvent = 3647;//1847
    public static final int UserWearBadgeEvent = 2265;//2473
    public static final int RoomVoteEvent = -242;//242
    public static final int RoomUserSignEvent = 1037;//2189
    public static final int RequestUserDataEvent = 3243;//112
    public static final int RoomUserShoutEvent = 1559;//936
    public static final int ScratchPetEvent = 2363;//3464
    public static final int RoomUserWalkEvent = 3264;//951
    public static final int RequestUserTagsEvent = 3364;//3102
    public static final int RequestTagsEvent = 3906;//1337
    public static final int GetMarketplaceConfigEvent = 1142;//1952
    public static final int RequestHeightmapEvent = 2450;//308
    public static final int TradeCloseEvent = 759;//3387
    public static final int CatalogBuyItemEvent = 3269;//2967
    public static final int RequestGuildMembersEvent = 556;//2382
    public static final int RequestPetInformationEvent = 1330;//777
    public static final int RoomUserWhisperEvent = 3700;//1362
    public static final int ModToolRequestUserInfoEvent = 852;//3077
    public static final int RotateMoveItemEvent = 3753;//194
    public static final int CancelPollEvent = 797;//276
    public static final int RequestRoomLoadEvent = 2144;//3751 //2086 //2144
    public static final int RequestGuildPartsEvent = 2165;//2344
    public static final int RoomPlacePaintEvent = 2534;//874
    public static final int RequestPopularRoomsEvent = 1627;//2346;//235
    public static final int ModToolRequestRoomInfoEvent = 346;//225
    public static final int FriendRequestEvent = 3667;//3765
    public static final int RecycleEvent = 2627;//2318
    public static final int RequestRoomCategoriesEvent = 783;//1431
    public static final int ToggleWallItemEvent = 3649;//2650
    public static final int RoomUserTalkEvent = 2462;//3349
    public static final int HotelViewDataEvent = 3813;//595
    public static final int RoomUserDanceEvent = 1764;//1139
    public static final int RequestUserProfileEvent = 1299;//532
    public static final int SearchRoomsFriendsNowEvent = 542;//3306
    public static final int SetStackHelperHeightEvent = 3280;//2540
    public static final int RedeemVoucherEvent = 114;//1571
    public static final int HorseUseItemEvent = 1105;//1616
    public static final int BuyItemEvent = 3161;//3878
    public static final int AdvertisingSaveEvent = 3645;//68
    public static final int RequestPetTrainingPanelEvent = 2867;//2476
    public static final int RoomBackgroundEvent = 1275;//2715
    public static final int RequestNewsListEvent = 1205;//896
    public static final int RequestPromotedRoomsEvent = 3527;//3030
    public static final int GuildSetAdminEvent = 1954;//2957
    public static final int GetClubDataEvent = 1232;//1608
    public static final int RequestMeMenuSettingsEvent = 254;//671
    public static final int MannequinSaveNameEvent = 2872;//2098
    public static final int SellItemEvent = 1147;//172
    public static final int GuildAcceptMembershipEvent = 816;//1066
    public static final int RequestBannerToken = 1497;//2619
    public static final int RequestRecylerLogicEvent = 2669;//1077
    public static final int RequestGuildJoinEvent = 1971;//77
    public static final int RequestCatalogIndexEvent = 1789;//81
    public static final int RequestInventoryPetsEvent = 3555;//1900
    public static final int ModToolRequestRoomVisitsEvent = 1854;//2549
    public static final int ModToolWarnEvent = 2891;//3763
    public static final int RequestItemInfoEvent = 2326;//36
    public static final int ModToolRequestRoomChatlogEvent = 2285;//1903
    public static final int UserSaveLookEvent = 2889;//2404
    public static final int ToggleFloorItemEvent = 2970;//120
    public static final int TradeUnAcceptEvent = 1287;//1131
    public static final int WiredTriggerSaveDataEvent = 3931;//3201
    public static final int RoomRemoveAllRightsEvent = 36;//405
    public static final int TakeBackItemEvent = 560;//2910
    public static final int OpenRecycleBoxEvent = 3502;//3977
    public static final int GuildChangeNameDescEvent = 3503;//3232
    public static final int RequestSellItemEvent = 1380;//2087
    public static final int ModToolChangeRoomSettingsEvent = 2571;//2530
    public static final int ModToolRequestUserChatlogEvent = 600;//203
    public static final int GuildChangeSettingsEvent = 1026;//2156
    public static final int RoomUserDropHandItemEvent = 1699;//157
    public static final int RequestProfileFriendsEvent = 3510;//3925
    public static final int TradeCancelOfferItemEvent = 2646;//373
    public static final int TriggerDiceEvent = 1000;//987
    public static final int GetPollDataEvent = 491;//1960
    public static final int MachineIDEvent = -512;//512
    public static final int RequestDiscountEvent = 458;//3794
    public static final int RequestFriendRequestEvent = 1823;//275
    public static final int RoomSettingsSaveEvent = 1824;//2384
    public static final int AcceptFriendRequest = 3110;//333
    public static final int ReleaseVersionEvent = 4000;//4000
    public static final int SearchRoomsMyFavoriteEvent = 1181;//1532
    public static final int TradeStartEvent = 2842;//466
    public static final int ChangeRelationEvent = 668;//2521
    public static final int RoomUserSitEvent = 2292;//484
    public static final int RequestCanCreateRoomEvent = 3929;//3844
    public static final int ModToolKickEvent = 1132;//3418
    public static final int MoveWallItemEvent = 3043;//1029
    public static final int SearchRoomsEvent = 2188;//3551
    public static final int RequestHighestScoreRoomsEvent = 1379;//1728
    public static final int CatalogBuyItemAsGiftEvent = 3186;//2474
    public static final int RoomUserGiveRespectEvent = 2901;//2041
    public static final int RemoveFriendEvent = 3767;//2786
    public static final int SearchRoomsFriendsOwnEvent = 1870;//3478
    public static final int GuildSetFavoriteEvent = 2864;//1463
    public static final int PetPlaceEvent = 81;//3711
    public static final int BotSettingsEvent = 3630;//3098
    public static final int StalkFriendEvent = 3829;//3394
    public static final int RoomPickupItemEvent = 2316;//152
    public static final int RedeemItemEvent = 3333;//3599
    public static final int RequestFriendsEvent = 2386;//776
    public static final int RequestAchievementsEvent = 3474;//2562
    public static final int GuildChangeColorsEvent = 3260;//463
    public static final int RequestInventoryBadgesEvent = 2480;//3072
    public static final int RequestPetBreedsEvent = 3851;//1064
    public static final int GuildChangeBadgeEvent = 2754;//1475
    public static final int ModToolBanEvent = 574;//2930
    public static final int SaveWardrobeEvent = 1188;//637
    public static final int HotelViewEvent = 1784;//3266
    public static final int ModToolPickTicketEvent = 3088;//3975
    public static final int ModToolReleaseTicketEvent = 734;
    public static final int ModToolCloseTicketEvent = 1837;
    public static final int TriggerColorWheelEvent = 2882;//2367
    public static final int SearchRoomsByTagEvent = -1956;//1956
    public static final int RequestPublicRoomsEvent = 2021;//3735 //UPDATED OLD 3178
    public static final int RequestResolutionEvent = 3871;//1854
    public static final int Unknown3 = 458;//3794
    public static final int RequestInventoryItemsEvent = 1920;//372
    public static final int ModToolRoomAlertEvent = 1863;//295
    public static final int WiredEffectSaveDataEvent = 1645;//3006
    public static final int CheckPetNameEvent = 3567;//678
    public static final int SecureLoginEvent = 1957;//1309
    public static final int BotSaveSettingsEvent = 736;//1384
    public static final int RequestGuildBuyEvent = 2594;//872
    public static final int SearchUserEvent = 3026;//1267
    public static final int GuildRemoveMemberEvent = 353;//3002
    public static final int WiredConditionSaveDataEvent = 2096;//1041
    public static final int RoomUserLookAtPoint = 2115;//1365
    public static final int MoodLightTurnOnEvent = 883;//1181
    public static final int MoodLightSettingsEvent = 3878;//2468
    public static final int RequestMyRoomsEvent = 2360;//2676
    public static final int RequestCreditsEvent = 1623;//2138
    public static final int SearchRoomsInGroupEvent = 2066;//1160
    public static final int HorseRideSettingsEvent = 1731;//2448
    public static final int HandleDoorbellEvent = 91;
    public static final int RoomUserKickEvent = 1718;//1874
    public static final int RoomPlaceItemEvent = 2554;//474
    public static final int RequestInventoryBotsEvent = 3549;//1351
    public static final int RequestUserWardrobeEvent = 3057;//1251
    public static final int RequestRoomRightsEvent = 2197;//2928
    public static final int RequestGuildBuyRoomsEvent = 1035;//701
    public static final int BotPlaceEvent = 2143;//148
    public static final int SearchRoomsWithRightsEvent = 307;//3785
    public static final int RequestBonusRareEvent = 1966;//1199
    public static final int GuildRemoveAdminEvent = 864;//3264
    public static final int RequestRoomSettingsEvent = 2176;//3741
    public static final int RequestOffersEvent = 2688;//541
    public static final int RequestUserCitizinShipEvent = 2151;//3096
    public static final int RoomUserStopTypingEvent = 3890;//2005
    public static final int RoomUserStartTypingEvent = 3896;//3684
    public static final int RequestGuildManageEvent = 3169;//2418
    public static final int RequestUserClubEvent = 2189;//3874
    public static final int PetPickupEvent = -3167;//863
    public static final int RequestOwnGuildsEvent = 484;//2240
    public static final int SearchRoomsVisitedEvent = 24;//589
    public static final int TradeOfferItemEvent = 1324;//2223
    public static final int TradeConfirmEvent = 3866;//1144
    public static final int RoomUserGiveRightsEvent = 3167;//2221
    public static final int RequestGuildInfoEvent = 331;//2079
    public static final int ReloadRecyclerEvent = 72;//2779
    public static final int RoomUserActionEvent = 2000;//475
    public static final int RequestGiftConfigurationEvent = 102;//382
    public static final int RequestRoomDataEvent = 2086;//183
    public static final int RequestRoomHeightmapEvent = 3075;
    public static final int RequestGuildFurniWidgetEvent = 3239;//3031
    public static final int RequestOwnItemsEvent = 1486;//829
    public static final int RequestReportRoomEvent = 2343;
    public static final int ReportEvent = 330;
    public static final int TriggerOneWayGateEvent = 408;
    public static final int FloorPlanEditorSaveEvent = 2324;
    public static final int FloorPlanEditorRequestDoorSettingsEvent = 1168;
    public static final int FloorPlanEditorRequestBlockedTilesEvent = 1241;*/

    public static final int MannequinSaveLookEvent = 3770;//1804 //Updated
    public static final int RequestCatalogPageEvent = 2914;//1754 //Updated
    public static final int RequestWearingBadgesEvent = 3466;//3968 //Updated
    public static final int BotPickupEvent = 665;//2865 //Updated
    public static final int HorseRideEvent = 1027;//3268 //Updated
    public static final int CreateRoomEvent = 238;//3524 //Updated
    public static final int SaveMottoEvent = 1037;//2832 //Updated
    public static final int GenerateSecretKeyEvent = -1;//3575
    public static final int ModToolAlertEvent = 2890;//1980 //Updated
    public static final int TradeAcceptEvent = 368;//1062 //Updated
    public static final int RequestCatalogModeEvent = 2711;//3406 //Updated
    public static final int RequestUserCreditsEvent = 2845;//938 //Updated
    public static final int FriendPrivateMessageEvent = 3710;//1527 //Updated
    public static final int CloseDiceEvent = 57;//1739 //Updated
    public static final int RoomUserRemoveRightsEvent = 1599;//3639 //Updated
    public static final int GuildDeclineMembershipEvent = 1328;//1320 //Updated
    public static final int AnswerPollEvent = 2135;//1847 //Updated
    public static final int UserWearBadgeEvent = 741;//2473 //Updated
    public static final int RoomVoteEvent = -242;//242
    public static final int RoomUserSignEvent = 2065;//2189 //Updated
    public static final int RequestUserDataEvent = 882;//112 //Updated
    public static final int RoomUserShoutEvent = 1788;//936 //Updated
    public static final int ScratchPetEvent = 965;//3464 //Updated
    public static final int RoomUserWalkEvent = 3690;//951 //Updated
    public static final int RequestUserTagsEvent = 3430;//3102 //Updated
    public static final int RequestTagsEvent = 753;//1337 //Updated
    public static final int GetMarketplaceConfigEvent = 3774;//1952 //Updated
    public static final int RequestHeightmapEvent = 859;//308 //Updated
    public static final int TradeCloseEvent = 3012;//3387 //Updated
    public static final int CatalogBuyItemEvent = 3964;//2967 //Updated
    public static final int RequestGuildMembersEvent = 3204;//2382 //Updated
    public static final int RequestPetInformationEvent = 2827;//777 //Updated
    public static final int RoomUserWhisperEvent = 3908;//1362 //Updated
    public static final int ModToolRequestUserInfoEvent = 607;//3077 //Updated
    public static final int RotateMoveItemEvent = 1219;//194 //Updated
    public static final int CancelPollEvent = 1628;//276 //Updated
    public static final int RequestRoomLoadEvent = 1988;//3751 //2086 //2144 //Updated
    public static final int RequestGuildPartsEvent = 1902;//2344 //Updated
    public static final int RoomPlacePaintEvent = 1892;//874 //Updated
    public static final int RequestPopularRoomsEvent = 464;//2346;//235 //Updated
    public static final int ModToolRequestRoomInfoEvent = 1450;//225 //Updated
    public static final int FriendRequestEvent = 2791;//3765 //Updated
    public static final int RecycleEvent = 3277;//2318 //Updated
    public static final int RequestRoomCategoriesEvent = 3498;//1431 //Updated
    public static final int ToggleWallItemEvent = 3050;//2650 //Updated
    public static final int RoomUserTalkEvent = 104;//3349 //Updated
    public static final int HotelViewDataEvent = 2507;//595 //Updated //2269
    public static final int RoomUserDanceEvent = 649;//1139 //Updated
    public static final int RequestUserProfileEvent = 1389;//532 //Updated
    public static final int SearchRoomsFriendsNowEvent = 1082;//3306 //Updated
    public static final int SetStackHelperHeightEvent = 2790;//2540 //Updated
    public static final int RedeemVoucherEvent = 3024;//1571 //Updated
    public static final int HorseUseItemEvent = 2590;//1616 //Updated
    public static final int BuyItemEvent = 2847;//3878 //Updated
    public static final int AdvertisingSaveEvent = 1829;//68 //Updated
    public static final int RequestPetTrainingPanelEvent = 2251;//2476 //Updated
    public static final int RoomBackgroundEvent = 2496;//2715 //Updated
    public static final int RequestNewsListEvent = 1134;//896 //Updated
    public static final int RequestPromotedRoomsEvent = 1558;//3030 //Updated
    public static final int GuildSetAdminEvent = 3704;//2957 //Updated
    public static final int GetClubDataEvent = 3240;//1608 //Updated
    public static final int RequestMeMenuSettingsEvent = 2169;//671 //Updated
    public static final int MannequinSaveNameEvent = 1289;//2098 //Updated
    public static final int SellItemEvent = 1522;//172 //Updated
    public static final int GuildAcceptMembershipEvent = 538;//1066 //Updated
    public static final int RequestBannerToken = -1;//2619
    public static final int RequestRecylerLogicEvent = 1170;//1077 //Updated
    public static final int RequestGuildJoinEvent = 794;//77 //Updated
    public static final int RequestCatalogIndexEvent = 2806;//81 //Updated
    public static final int RequestInventoryPetsEvent = 3760;//1900 //Updated
    public static final int ModToolRequestRoomVisitsEvent = 2287;//2549 //Updated
    public static final int ModToolWarnEvent = 2890;//3763 //Updated
    public static final int RequestItemInfoEvent = 2042;//36 //Updated
    public static final int ModToolRequestRoomChatlogEvent = 3209;//1903 //Updated
    public static final int UserSaveLookEvent = 2242;//2404 //Updated
    public static final int ToggleFloorItemEvent = 2693;//120 //Updated
    public static final int TradeUnAcceptEvent = 126;//1131 //Updated
    public static final int WiredTriggerSaveDataEvent = 3593;//3201 //Updated
    public static final int RoomRemoveAllRightsEvent = 3764;//405 //Updated
    public static final int TakeBackItemEvent = 1455;//2910 //Updated
    public static final int OpenRecycleBoxEvent = 0x0F00;//3977 //Updated
    public static final int GuildChangeNameDescEvent = 1485;//3232 //Updated
    public static final int RequestSellItemEvent = 876;//2087 //Updated
    public static final int ModToolChangeRoomSettingsEvent = 3244;//2530 //Updated
    public static final int ModToolRequestUserChatlogEvent = 3209;//203 //Updated
    public static final int GuildChangeSettingsEvent = 2258;//2156 //Updated
    public static final int RoomUserDropHandItemEvent = 1108;//157 //Updated
    public static final int RequestProfileFriendsEvent = 3701;//3925 //Updated
    public static final int TradeCancelOfferItemEvent = 2170;//373 //Updated
    public static final int TriggerDiceEvent = 386;//987 //Updated
    public static final int GetPollDataEvent = 2263;//1960 //Updated
    public static final int MachineIDEvent = 686;//512
    public static final int RequestDiscountEvent = 1995;//3794 //Updated
    public static final int RequestFriendRequestEvent = 3739;//275 //Updated
    public static final int RoomSettingsSaveEvent = 1290;//2384 //Updated
    public static final int AcceptFriendRequest = 3383;//333 //Updated
    public static final int ReleaseVersionEvent = 4000;//4000 //Updated
    public static final int SearchRoomsMyFavoriteEvent = 3124;//1532 //Updated
    public static final int TradeStartEvent = 3304;//466 //Updated
    public static final int ChangeRelationEvent = 2632;//2521 //Updated
    public static final int RoomUserSitEvent = 272;//484 //Updated
    public static final int RequestCanCreateRoomEvent = 3903;//3844 //Updated //unsure
    public static final int ModToolKickEvent = 1459;//3418 //Updated
    public static final int MoveWallItemEvent = 3455;//1029 //Updated
    public static final int SearchRoomsEvent = 3844;//3551 //Updated
    public static final int RequestHighestScoreRoomsEvent = 1814;//1728 //Updaed
    public static final int CatalogBuyItemAsGiftEvent = 1066;//2474 //Updated
    public static final int RoomUserGiveRespectEvent = 3121;//2041 //Updated
    public static final int RemoveFriendEvent = 1535;//2786 //Updated
    public static final int SearchRoomsFriendsOwnEvent = 725;//3478 //Updated
    public static final int GuildSetFavoriteEvent = 2754;//1463 //Updated
    public static final int PetPlaceEvent = 1961;//3711 //Updated
    public static final int BotSettingsEvent = 3292;//3098 //Updated
    public static final int StalkFriendEvent = 1925;//3394 //Updated
    public static final int RoomPickupItemEvent = 2463;//152 //Updated
    public static final int RedeemItemEvent = 1242;//3599 //Updated
    public static final int RequestFriendsEvent = 1904;//776 //Updated
    public static final int RequestAchievementsEvent = 2697;//2562 //Updated
    public static final int GuildChangeColorsEvent = 3873;//463 //Updated
    public static final int RequestInventoryBadgesEvent = 2450;//3072 //Updated
    public static final int RequestPetBreedsEvent = 2828;//1064 //Updated
    public static final int GuildChangeBadgeEvent = 838;//1475 //Updated
    public static final int ModToolBanEvent = 701;//2930 //Updated
    public static final int SaveWardrobeEvent = 2921;//637 //Updated
    public static final int HotelViewEvent = 3603;//3266 //Updated
    public static final int ModToolPickTicketEvent = 2980;//3975 //Updated
    public static final int ModToolReleaseTicketEvent = 3905; //Updated
    public static final int ModToolCloseTicketEvent = 3904; //Updated
    public static final int TriggerColorWheelEvent = 2556;//2367 //Updated
    public static final int SearchRoomsByTagEvent = 3844;//1956 //Updated
    public static final int RequestPublicRoomsEvent = 2057;//3735 //UPDATED OLD 3178 //Updated
    public static final int RequestResolutionEvent = 305;//1854 //Updated
    public static final int RequestInventoryItemsEvent = 2194;//372 //Updated
    public static final int ModToolRoomAlertEvent = 2550;//295 //Updated
    public static final int WiredEffectSaveDataEvent = 2856;//3006 //Updated
    public static final int CheckPetNameEvent = 3699;//678 //Updated
    public static final int SecureLoginEvent = 2243;//1309 //Updated
    public static final int BotSaveSettingsEvent = 1607;//1384 //Updated
    public static final int RequestGuildBuyEvent = 569;//872 //Updated
    public static final int SearchUserEvent = 3576;//1267 //Updated
    public static final int GuildConfirmRemoveMemberEvent = 3394;
    public static final int GuildRemoveMemberEvent = 1764;//3002 //Updated
    public static final int WiredConditionSaveDataEvent = 3268;//1041 //Updated
    public static final int RoomUserLookAtPoint = 2283;//1365 //Updated
    public static final int MoodLightTurnOnEvent = 3924;//1181 //Updated
    public static final int MoodLightSettingsEvent = 2821;//2468 //Updated
    public static final int RequestMyRoomsEvent = 948;//2676 //Updated
    public static final int RequestCreditsEvent = 3896;//2138 //Updated
    public static final int SearchRoomsInGroupEvent = 2851;//1160 //Updated
    public static final int HorseRideSettingsEvent = 427;//2448 //Updated
    public static final int KnockKnockResponseEvent = 853; //Updated
    public static final int RoomUserKickEvent = 313;//1874 //Updated
    public static final int RoomPlaceItemEvent = 3571;//474 //Updated
    public static final int RequestInventoryBotsEvent = 1343;//1351 //Updated
    public static final int RequestUserWardrobeEvent = 2976;//1251 //Updated
    public static final int RequestRoomRightsEvent = 63;//2928 //Updated
    public static final int RequestGuildBuyRoomsEvent = 2945;//701 //Updated
    public static final int BotPlaceEvent = 3906;//148 //Updated
    public static final int SearchRoomsWithRightsEvent = 1699;//3785 //Updated
    public static final int HotelViewRequestBonusRareEvent = 1521;//1199 //Updated
    public static final int GuildRemoveAdminEvent = 904;//3264 //Updated
    public static final int RequestRoomSettingsEvent = 1794;//3741 //Updated
    public static final int RequestOffersEvent = 100;//541 //Updated
    public static final int RequestUserCitizinShipEvent = 3556;//3096 //Updated
    public static final int RoomUserStopTypingEvent = 1712;//2005 //Updated
    public static final int RoomUserStartTypingEvent = 3128;//3684 //Updated
    public static final int RequestGuildManageEvent = 3931;//2418 //Updated
    public static final int RequestUserClubEvent = 675;//3874 //Updated
    public static final int PetPickupEvent = 1294;//863 //Updated
    public static final int RequestOwnGuildsEvent = 2690;//2240 //Updated
    public static final int SearchRoomsVisitedEvent = 3561;//589 //Updated
    public static final int TradeOfferItemEvent = 2090;//2223 //Updated
    public static final int TradeConfirmEvent = 1503;//1144 //Updated
    public static final int RoomUserGiveRightsEvent = 503;//2221 //Updated
    public static final int RequestGuildInfoEvent = 844;//2079 //Updated
    public static final int ReloadRecyclerEvent = 3895;//2779 //Updated
    public static final int RoomUserActionEvent = 2522;//475 //Updated
    public static final int RequestGiftConfigurationEvent = 756;//382 //Updated
    public static final int RequestRoomDataEvent = 2022;//183 //Updated
    public static final int RequestRoomHeightmapEvent = 3936; //Updated
    public static final int RequestGuildFurniWidgetEvent = 54;//3031 //Updated
    public static final int RequestOwnItemsEvent = 1816;//829 //Updated
    public static final int RequestReportRoomEvent = 2842; //Updated
    public static final int ReportEvent = 2073; //Updated
    public static final int TriggerOneWayGateEvent = 3752; //Updated
    public static final int FloorPlanEditorSaveEvent = 3509; //Updated
    public static final int FloorPlanEditorRequestDoorSettingsEvent = 2691; //Updated
    public static final int FloorPlanEditorRequestBlockedTilesEvent = 2246; //Updated
    public static final int UnknownEvent1 = 3365;
    public static final int RequestTalenTrackEvent = 1741;
    public static final int RequestNewNavigatorDataEvent = 1192;
    public static final int RequestNewNavigatorRoomsEvent = 3669;
    public static final int RedeemClothingEvent = 2536;
    public static final int NewNavigatorActionEvent = 1344;
    public static final int PostItPlaceEvent = 1688;
    public static final int PostItRequestDataEvent = 3702;
    public static final int PostItSaveDataEvent = 3504;
    public static final int PostItDeleteEvent = 779;
    public static final int MoodLightSaveSettingsEvent = 900;
    public static final int ModToolRequestIssueChatlogEvent = 620;
    public static final int ModToolRequestRoomUserChatlogEvent = 1352;
    public static final int UsernameEvent = 820;
    public static final int RequestClubGiftsEvent = 425;
    public static final int RentSpaceEvent = 1382;
    public static final int RentSpaceCancelEvent = 3038;
    public static final int RequestInitFriendsEvent = 3503;
    public static final int RequestCameraConfigurationEvent = 2257;
    public static final int PingEvent = 862;
    public static final int FindNewFriendsEvent = 3421;
    public static final int InviteFriendsEvent = 370;
    public static final int GuildeRemoveFavoriteEvent = 3259;
    public static final int GuildDeleteEvent = 1581;
    public static final int SetHomeRoomEvent = 1772;
    public static final int RoomUserGiveHandItemEvent = 2797;
    public static final int AmbassadorVisitCommandEvent = 487;
    public static final int AmbassadorAlertCommandEvent = 3992;
    public static final int SaveUserVolumesEvent = 1914;
    public static final int SavePreferOldChatEvent = 3131;
    public static final int SaveIgnoreRoomInvitesEvent = 542;
    public static final int SaveBlockCameraFollowEvent = 2477;
    public static final int RoomMuteEvent = 2571;
    public static final int RequestRoomWordFilterEvent = 3305;
    public static final int RoomWordFilterModifyEvent = 3415;
    public static final int CatalogSearchedItemEvent = 346;
    public static final int JukeBoxRequestTrackCodeEvent = 716;
    public static final int JukeBoxRequestTrackDataEvent = 3453;
    public static final int RoomStaffPickEvent = 3139;
    public static final int RoomRequestBannedUsersEvent = 2226;
    public static final int JukeBoxRequestPlayListEvent = 68;
    public static final int JukeBoxEventOne = 3448;
    public static final int JukeBoxEventTwo = 2457;
    public static final int JukeBoxEventThree = 2693;
}
