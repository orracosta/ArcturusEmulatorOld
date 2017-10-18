package com.habboproject.server.network.messages;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.incoming.ambassador.AmbassadorSendAlertToPlayerMessageEvent;
import com.habboproject.server.network.messages.incoming.ambassador.AmbassadorVisiteRoomMessageEvent;
import com.habboproject.server.network.messages.incoming.camera.BuyPhotoMessageEvent;
import com.habboproject.server.network.messages.incoming.camera.PhotoPriceMessageEvent;
import com.habboproject.server.network.messages.incoming.camera.RenderRoomMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.ads.CatalogPromotionGetRoomsMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.ads.PromoteRoomMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.ads.PromotionUpdateMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.data.GetGiftWrappingConfigurationMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.groups.BuyGroupDialogMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.groups.BuyGroupMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.groups.GroupFurnitureCatalogMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.pets.PetRacesMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.pets.ValidatePetNameMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.purchase.PurchaseGiftMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.purchase.PurchaseItemMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.root.GetCatalogIndexMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.root.GetCatalogOfferMessageEvent;
import com.habboproject.server.network.messages.incoming.catalog.root.GetCatalogPageMessageEvent;
import com.habboproject.server.network.messages.incoming.gamecenter.*;
import com.habboproject.server.network.messages.incoming.group.*;
import com.habboproject.server.network.messages.incoming.group.favourite.ClearFavouriteGroupMessageEvent;
import com.habboproject.server.network.messages.incoming.group.favourite.SetFavouriteGroupMessageEvent;
import com.habboproject.server.network.messages.incoming.group.forum.data.ForumDataMessageEvent;
import com.habboproject.server.network.messages.incoming.group.forum.data.GetForumsMessageEvent;
import com.habboproject.server.network.messages.incoming.group.forum.data.MarkAsReadMessageEvent;
import com.habboproject.server.network.messages.incoming.group.forum.settings.SaveForumSettingsMessageEvent;
import com.habboproject.server.network.messages.incoming.group.forum.threads.*;
import com.habboproject.server.network.messages.incoming.group.settings.*;
import com.habboproject.server.network.messages.incoming.handshake.*;
import com.habboproject.server.network.messages.incoming.help.HelpTicketMessageEvent;
import com.habboproject.server.network.messages.incoming.help.InitHelpToolMessageEvent;
import com.habboproject.server.network.messages.incoming.landing.LandingLoadWidgetMessageEvent;
import com.habboproject.server.network.messages.incoming.landing.RefreshPromoArticlesMessageEvent;
import com.habboproject.server.network.messages.incoming.marketplace.*;
import com.habboproject.server.network.messages.incoming.messenger.*;
import com.habboproject.server.network.messages.incoming.moderation.*;
import com.habboproject.server.network.messages.incoming.moderation.tickets.ModToolCloseIssueMessageEvent;
import com.habboproject.server.network.messages.incoming.moderation.tickets.ModToolPickTicketMessageEvent;
import com.habboproject.server.network.messages.incoming.moderation.tickets.ModToolReleaseIssueMessageEvent;
import com.habboproject.server.network.messages.incoming.moderation.tickets.ModToolTicketChatlogMessageEvent;
import com.habboproject.server.network.messages.incoming.music.SongDataMessageEvent;
import com.habboproject.server.network.messages.incoming.music.SongIdMessageEvent;
import com.habboproject.server.network.messages.incoming.music.playlist.PlaylistAddMessageEvent;
import com.habboproject.server.network.messages.incoming.music.playlist.PlaylistMessageEvent;
import com.habboproject.server.network.messages.incoming.music.playlist.PlaylistRemoveMessageEvent;
import com.habboproject.server.network.messages.incoming.navigator.*;
import com.habboproject.server.network.messages.incoming.navigator.GoToRoomByNameMessageEvent;
import com.habboproject.server.network.messages.incoming.navigator.InitializeNavigatorMessageEvent;
import com.habboproject.server.network.messages.incoming.navigator.NavigatorSearchMessageEvent;
import com.habboproject.server.network.messages.incoming.navigator.ResizeNavigatorMessageEvent;
import com.habboproject.server.network.messages.incoming.performance.EventLogMessageEvent;
import com.habboproject.server.network.messages.incoming.performance.RequestLatencyTestMessageEvent;
import com.habboproject.server.network.messages.incoming.polls.GetPollMessageEvent;
import com.habboproject.server.network.messages.incoming.polls.SubmitPollAnswerMessageEvent;
import com.habboproject.server.network.messages.incoming.quests.CancelQuestMessageEvent;
import com.habboproject.server.network.messages.incoming.quests.OpenQuestsMessageEvent;
import com.habboproject.server.network.messages.incoming.quests.StartQuestMessageEvent;
import com.habboproject.server.network.messages.incoming.room.access.AnswerDoorbellMessageEvent;
import com.habboproject.server.network.messages.incoming.room.access.LoadRoomByDoorBellMessageEvent;
import com.habboproject.server.network.messages.incoming.room.access.SpectateRoomMessageEvent;
import com.habboproject.server.network.messages.incoming.room.action.*;
import com.habboproject.server.network.messages.incoming.room.bots.BotConfigMessageEvent;
import com.habboproject.server.network.messages.incoming.room.bots.ModifyBotMessageEvent;
import com.habboproject.server.network.messages.incoming.room.bots.PlaceBotMessageEvent;
import com.habboproject.server.network.messages.incoming.room.bots.RemoveBotMessageEvent;
import com.habboproject.server.network.messages.incoming.room.engine.*;
import com.habboproject.server.network.messages.incoming.room.filter.EditWordFilterMessageEvent;
import com.habboproject.server.network.messages.incoming.room.filter.WordFilterListMessageEvent;
import com.habboproject.server.network.messages.incoming.room.floor.GetTilesInUseMessageEvent;
import com.habboproject.server.network.messages.incoming.room.floor.SaveFloorMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.*;
import com.habboproject.server.network.messages.incoming.room.item.gifts.OpenGiftMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.lovelock.ConfirmLoveLockMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.mannequins.SaveMannequinFigureMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.mannequins.SaveMannequinMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.stickies.DeletePostItMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.stickies.OpenPostItMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.stickies.PlacePostitMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.stickies.SavePostItMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.wired.SaveWiredDataMessageEvent;
import com.habboproject.server.network.messages.incoming.room.item.wired.UpdateSnapshotsMessageEvent;
import com.habboproject.server.network.messages.incoming.room.moderation.*;
import com.habboproject.server.network.messages.incoming.room.pets.*;
import com.habboproject.server.network.messages.incoming.room.pets.horse.ApplyHorseEffectMessageEvent;
import com.habboproject.server.network.messages.incoming.room.pets.horse.ModifyWhoCanRideHorseMessageEvent;
import com.habboproject.server.network.messages.incoming.room.pets.horse.RemoveHorseSaddleMessageEvent;
import com.habboproject.server.network.messages.incoming.room.pets.horse.RideHorseMessageEvent;
import com.habboproject.server.network.messages.incoming.room.settings.*;
import com.habboproject.server.network.messages.incoming.room.trading.*;
import com.habboproject.server.network.messages.incoming.user.achievements.AchievementsListMessageEvent;
import com.habboproject.server.network.messages.incoming.user.club.ClubStatusMessageEvent;
import com.habboproject.server.network.messages.incoming.user.details.*;
import com.habboproject.server.network.messages.incoming.user.inventory.*;
import com.habboproject.server.network.messages.incoming.user.newbie.AnsweredHelpBubbleMessageEvent;
import com.habboproject.server.network.messages.incoming.user.profile.*;
import com.habboproject.server.network.messages.incoming.user.wardrobe.ChangeLooksMessageEvent;
import com.habboproject.server.network.messages.incoming.user.wardrobe.SaveWardrobeMessageEvent;
import com.habboproject.server.network.messages.incoming.user.wardrobe.WardrobeMessageEvent;
import com.habboproject.server.network.messages.incoming.user.youtube.LoadPlaylistMessageEvent;
import com.habboproject.server.network.messages.incoming.user.youtube.NextVideoMessageEvent;
import com.habboproject.server.network.messages.incoming.user.youtube.PlayVideoMessageEvent;
import com.habboproject.server.network.messages.outgoing.group.DeleteGroupMessageEvent;
import com.habboproject.server.network.messages.types.tasks.MessageEventThread;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.headers.Events;
import com.habboproject.server.protocol.messages.MessageEvent;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;

public final class MessageHandler {
    public static Logger log = Logger.getLogger(MessageHandler.class.getName());
    private final Map<Integer, Event> messages = new ConcurrentHashMap<>();

    private final AbstractExecutorService eventExecutor;
    private final boolean asyncEventExecution;

    public MessageHandler() {
        this.asyncEventExecution = Boolean.parseBoolean((String) Comet.getServer().getConfig().getOrDefault("comet.network.alternativePacketHandling.enabled", "false"));

        if (this.asyncEventExecution) {
            switch ((String) Comet.getServer().getConfig().getOrDefault("comet.network.alternativePacketHandling.type", "threadpool")) {
                default:
                    log.info("Using thread-pool event executor");
                    this.eventExecutor = new ThreadPoolExecutor(Integer.parseInt((String) Comet.getServer().getConfig().getOrDefault("comet.network.alternativePacketHandling.coreSize", "8")), // core size
                            Integer.parseInt((String) Comet.getServer().getConfig().getOrDefault("comet.network.alternativePacketHandling.maxSize", "32")), // max size
                            10 * 60, // idle timeout
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>());
                    break;

                case "forkjoin":
                    log.info("Using fork-join event executor");
                    this.eventExecutor = new ForkJoinPool(Integer.parseInt((String) Comet.getServer().getConfig().getOrDefault("comet.network.alternativePacketHandling.coreSize", 16)), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
                    break;
            }
        } else {
            this.eventExecutor = null;
        }

        this.load();
    }

    public void load() {
        registerHandshake();
        registerModTool();
        registerHelpTool();
        registerMessenger();
        registerNavigator();
        registerUser();
        registerBots();
        registerRoom();
        registerRoomTrade();
        registerRoomModeration();
        registerRoomAccess();
        registerItems();
        registerCatalog();
        registerPets();
        registerLanding();
        registerGroups();
        registerGroupForums();
        registerQuests();
        registerPromotions();
        registerAchievements();
        registerPolls();
        registerMisc();
        registerMusic();
        registerCamera();
        registerMarketplace();
        registerAmbassador();
        registerGameCenter();

        log.info("Loaded " + this.getMessages().size() + " message events");
    }

    private void registerMisc() {
        getMessages().put(1717, new RequestLatencyTestMessageEvent());
        getMessages().put(2544, new EventLogMessageEvent());
    }

    private void registerHandshake() {
        getMessages().put(4000, new CheckReleaseMessageEvent());
        getMessages().put(340, new InitCryptoMessageEvent());
        getMessages().put(460, new GenerateSecretKeyMessageEvent());
        getMessages().put(127, new SSOTicketMessageEvent());
        getMessages().put(2220, new UniqueIdMessageEvent());
    }

    private void registerModTool() {
        getMessages().put(1120, new ModToolUserInfoMessageEvent());
        getMessages().put(3574, new ModToolUserChatlogMessageEvent());
        getMessages().put(1608, new ModToolRoomChatlogMessageEvent());
        getMessages().put(29, new ModToolBanUserMessageEvent());
        getMessages().put(3398, new ModToolRoomInfoMessageEvent());
        getMessages().put(634, new ModToolRoomVisitsMessageEvent());
        getMessages().put(3458, new ModToolUserAlertMessageEvent());
        getMessages().put(3835, new ModToolUserCautionMessageEvent());
        getMessages().put(246, new ModToolUserKickMessageEvent());
        getMessages().put(2192, new ModToolRoomAlertMessageEvent());
        getMessages().put(3062, new ModToolRoomActionMessageEvent());
        getMessages().put(2272, new ModToolPickTicketMessageEvent());
        getMessages().put(2836, new ModToolTicketChatlogMessageEvent());
        getMessages().put(2485, new ModToolCloseIssueMessageEvent());
        getMessages().put(3967, new ModToolReleaseIssueMessageEvent());
        getMessages().put(2178, new ModerationMuteUserMessageEvent());
    }

    private void registerHelpTool() {
        getMessages().put(2714, new InitHelpToolMessageEvent());
        getMessages().put(1141, new HelpTicketMessageEvent());
    }

    private void registerMessenger() {
        getMessages().put(3058, new InitializeFriendListMessageEvent());
        getMessages().put(3653, new PrivateChatMessageEvent());
        getMessages().put(2457, new RequestFriendshipMessageEvent());
        getMessages().put(109, new AcceptFriendshipMessageEvent());
        getMessages().put(1021, new SearchFriendsMessageEvent());
        getMessages().put(3906, new FollowFriendMessageEvent());
        getMessages().put(579, new DeleteFriendsMessageEvent());
        getMessages().put(3233, new InviteFriendsMessageEvent());
        getMessages().put(3875, new DeclineFriendshipMessageEvent());
    }

    private void registerNavigator() {
        getMessages().put(187, new LoadCategoriesMessageEvent());
        getMessages().put(199, new InitializeNavigatorMessageEvent());
        getMessages().put(105, new NavigatorSearchMessageEvent());
        getMessages().put(3488, new CanCreateRoomMessageEvent());
        getMessages().put(1674, new CreateRoomMessageEvent());
        getMessages().put(2919, new EventCategoriesMessageEvent());
        getMessages().put(17, new CatalogPromotionGetRoomsMessageEvent());
        getMessages().put(3426, new AddToStaffPickedRoomsMessageEvent());
        getMessages().put(2907, new ResizeNavigatorMessageEvent()); //
        getMessages().put(113, new GoToRoomByNameMessageEvent()); //
    }

    private void registerUser() {
        getMessages().put(3412, new GetProfileMessageEvent());
        getMessages().put(2518, new GetProfileByUsernameMessageEvent());
        getMessages().put(2100, new ClubStatusMessageEvent());
        getMessages().put(2139, new InfoRetrieveMessageEvent());
        getMessages().put(1476, new ChangeLooksMessageEvent());
        getMessages().put(696, new OpenInventoryMessageEvent());
        getMessages().put(3315, new BadgeInventoryMessageEvent());
        getMessages().put(761, new ChangeMottoMessageEvent());
        getMessages().put(1044, new GetRelationshipsMessageEvent());
        getMessages().put(415, new SetRelationshipMessageEvent());
        getMessages().put(1447, new WearBadgeMessageEvent());
        getMessages().put(277, new WardrobeMessageEvent());
        getMessages().put(1377, new SaveWardrobeMessageEvent());
        getMessages().put(3753, new ChangeHomeRoomMessageEvent());
        getMessages().put(2268, new UpdateAudioSettingsMessageEvent());
        getMessages().put(3860, new UpdateCameraFollowSettingsMessageEvent()); //
        getMessages().put(3729, new UpdateChatStyleMessageEvent());
        getMessages().put(649, new IgnoreInvitationsMessageEvent());
        getMessages().put(728, new ApplySelectedEffectMessageEvent()); //
        getMessages().put(1223, new AnsweredHelpBubbleMessageEvent()); //
    }

    private void registerBots() {
        getMessages().put(1379, new BotInventoryMessageEvent());
        getMessages().put(2303, new PlaceBotMessageEvent());
        getMessages().put(940, new ModifyBotMessageEvent());
        getMessages().put(1757, new RemoveBotMessageEvent());
        getMessages().put(3840, new BotConfigMessageEvent());
    }

    private void registerPets() {
        getMessages().put(194, new PetInventoryMessageEvent());
        getMessages().put(154, new PlacePetMessageEvent());
        getMessages().put(3928, new PetInformationMessageEvent());
        getMessages().put(3090, new RemovePetMessageEvent());
        getMessages().put(2598, new RideHorseMessageEvent());
        getMessages().put(3571, new ScratchPetMessageEvent());
        getMessages().put(2033, new GetPetTrainingPanelMessageEvent());
        getMessages().put(1768, new ApplyHorseEffectMessageEvent());
        getMessages().put(3358, new RemoveHorseSaddleMessageEvent());
        getMessages().put(2906, new ModifyWhoCanRideHorseMessageEvent());
    }

    private void registerRoom() {
        getMessages().put(3785, new InitializeRoomMessageEvent());
        getMessages().put(3933, new FollowRoomInfoMessageEvent());
        getMessages().put(764, new AddUserToRoomMessageEvent());
        getMessages().put(1794, new ExitRoomMessageEvent());
        getMessages().put(520, new TalkMessageEvent());
        getMessages().put(1134, new ShoutMessageEvent());
        getMessages().put(753, new WhisperMessageEvent());
        getMessages().put(2935, new WalkMessageEvent());
        getMessages().put(3417, new ApplyActionMessageEvent());
        getMessages().put(3184, new ApplySignMessageEvent());
        getMessages().put(1551, new ApplyDanceMessageEvent());
        getMessages().put(177, new GetRoomSettingsDataMessageEvent());
        getMessages().put(1934, new SaveRoomDataMessageEvent());
        getMessages().put(3177, new RespectUserMessageEvent());
        getMessages().put(1022, new StartTypingMessageEvent());
        getMessages().put(1096, new StopTypingMessageEvent());
        getMessages().put(2988, new LookToMessageEvent());
        getMessages().put(1594, new UserBadgesMessageEvent());
        getMessages().put(2743, new ApplyDecorationMessageEvent());
        getMessages().put(369, new DropHandItemMessageEvent());
        getMessages().put(1054, new DeleteRoomMessageEvent());
        getMessages().put(2325, new MuteRoomMessageEvent());
        getMessages().put(3282, new RateRoomMessageEvent());
        getMessages().put(1951, new GiveHandItemMessageEvent());
        getMessages().put(3736, new SaveFloorMessageEvent());
        getMessages().put(1776, new GetTilesInUseMessageEvent());
        getMessages().put(505, new IgnoreUserMessageEvent());
        getMessages().put(1889, new UnignoreUserMessageEvent());
        getMessages().put(3923, new RemoveOwnRightsMessageEvent());
        getMessages().put(3941, new SitMessageEvent());
        getMessages().put(3675, new GetFurnitureAliasesMessageEvent());
        getMessages().put(584, new SaveRoomThumbnailMessageEvent());
    }

    private void registerRoomTrade() {
        getMessages().put(3876, new BeginTradeMessageEvent());
        getMessages().put(1486, new CancelTradeMessageEvent());
        getMessages().put(1949, new UnacceptTradeMessageEvent());
        getMessages().put(1949, new SendOfferMessageEvent());
        getMessages().put(2937, new AcceptTradeMessageEvent());
        getMessages().put(1215, new ConfirmTradeMessageEvent());
        getMessages().put(1137, new TradingOfferItemsMessageEvent());
    }

    private void registerRoomModeration() {
        getMessages().put(3642, new KickUserMessageEvent());
        getMessages().put(3441, new BanUserMessageEvent());
        getMessages().put(1014, new GiveRightsMessageEvent());
        getMessages().put(2011, new RemoveRightsMessageEvent());
        getMessages().put(1666, new RemoveAllRightsMessageEvent());
        getMessages().put(2078, new GetBannedUsersMessageEvent());
        getMessages().put(3257, new RoomUnbanUserMessageEvent());
        getMessages().put(329, new MutePlayerMessageEvent());
        getMessages().put(1298, new UsersWithRightsMessageEvent());
        getMessages().put(3536, new WordFilterListMessageEvent());
        getMessages().put(3838, new EditWordFilterMessageEvent());
    }

    private void registerRoomAccess() {
        getMessages().put(1332, new AnswerDoorbellMessageEvent());
        getMessages().put(745, new LoadRoomByDoorBellMessageEvent());
        getMessages().put(2596, new SpectateRoomMessageEvent()); //
    }

    private void registerItems() {
        getMessages().put(1262, new PlaceItemMessageEvent());
        getMessages().put(2660, new ChangeFloorItemPositionMessageEvent());
        getMessages().put(15, new ChangeWallItemPositionMessageEvent());
        getMessages().put(3821, new PickUpItemMessageEvent());
        getMessages().put(2475, new ChangeFloorItemStateMessageEvent());
        getMessages().put(2253, new ChangeFloorItemStateMessageEvent());
        getMessages().put(1857, new OpenDiceMessageEvent());
        getMessages().put(2595, new RunDiceMessageEvent());
        getMessages().put(172, new SaveWiredDataMessageEvent());
        getMessages().put(1404, new SaveWiredDataMessageEvent());
        getMessages().put(3139, new SaveWiredDataMessageEvent());
        getMessages().put(1327, new UpdateSnapshotsMessageEvent());
        getMessages().put(2494, new ExchangeItemMessageEvent());
        getMessages().put(2681, new UseWallItemMessageEvent());
        getMessages().put(3615, new UseWallItemMessageEvent());
        getMessages().put(772, new SaveMannequinMessageEvent());
        getMessages().put(2430, new SaveMannequinFigureMessageEvent());
        getMessages().put(3877, new SaveTonerMessageEvent());
        getMessages().put(989, new SaveBrandingMessageEvent());
        getMessages().put(967, new OpenGiftMessageEvent());
        getMessages().put(1367, new UseMoodlightMessageEvent());
        getMessages().put(3599, new ToggleMoodlightMessageEvent());
        getMessages().put(3631, new UpdateMoodlightMessageEvent());
        getMessages().put(501, new SaveStackToolMessageEvent());
        getMessages().put(2870, new PlacePostitMessageEvent());
        getMessages().put(1920, new OpenPostItMessageEvent());
        getMessages().put(867, new SavePostItMessageEvent());
        getMessages().put(2270, new DeletePostItMessageEvent());
        getMessages().put(3452, new LoadPlaylistMessageEvent());
        getMessages().put(1013, new PlayVideoMessageEvent());
        getMessages().put(1488, new NextVideoMessageEvent());
        getMessages().put(3399, new ConfirmLoveLockMessageEvent());
        getMessages().put(3440, new SaveFootballClothesMessageEvent()); //
    }

    private void registerPromotions() {
        getMessages().put(1765, new PromoteRoomMessageEvent());
        getMessages().put(257, new PromotionUpdateMessageEvent());
    }

    private void registerCatalog() {
        getMessages().put(3048, new GetCatalogIndexMessageEvent()); //
        getMessages().put(878, new GetCatalogPageMessageEvent()); //
        getMessages().put(1986, new PurchaseItemMessageEvent());
        getMessages().put(2570, new GetGiftWrappingConfigurationMessageEvent());
        getMessages().put(62, new BuyGroupDialogMessageEvent());
        getMessages().put(575, new BuyGroupMessageEvent());
        getMessages().put(1531, new PetRacesMessageEvent());
        getMessages().put(2961, new ValidatePetNameMessageEvent());
        getMessages().put(2626, new PurchaseGiftMessageEvent());
        getMessages().put(3533, new GroupFurnitureCatalogMessageEvent());
        getMessages().put(3114, new GetCatalogOfferMessageEvent());
    }

    private void registerLanding() {
        getMessages().put(291, new RefreshPromoArticlesMessageEvent());
        getMessages().put(3388, new LandingLoadWidgetMessageEvent());
    }

    private void registerGroups() {
        getMessages().put(3549, new GroupInformationMessageEvent());
        getMessages().put(3646, new GroupMembersMessageEvent());
        getMessages().put(3934, new ManageGroupMessageEvent());
        getMessages().put(499, new RevokeMembershipMessageEvent());
        getMessages().put(3907, new JoinGroupMessageEvent());
        getMessages().put(413, new ModifyGroupTitleMessageEvent());
        getMessages().put(691, new RevokeAdminMessageEvent());
        getMessages().put(729, new GiveGroupAdminMessageEvent());
        getMessages().put(1467, new ModifyGroupSettingsMessageEvent());
        getMessages().put(1115, new AcceptMembershipMessageEvent());
        getMessages().put(517, new ModifyGroupBadgeMessageEvent());
        getMessages().put(1064, new SetFavouriteGroupMessageEvent());
        getMessages().put(1043, new GroupFurnitureWidgetMessageEvent());
        getMessages().put(3781, new GroupUpdateColoursMessageEvent());
        getMessages().put(2118, new DeclineMembershipMessageEvent());
        getMessages().put(818, new ClearFavouriteGroupMessageEvent());
        getMessages().put(147, new DeleteGroupMessageEvent());
    }

    private void registerGroupForums() {
        getMessages().put(3446, new ForumDataMessageEvent());
        getMessages().put(683, new SaveForumSettingsMessageEvent());
        getMessages().put(71, new ForumThreadsMessageEvent());
        getMessages().put(3006, new PostMessageMessageEvent());
        getMessages().put(856, new ViewThreadMessageEvent());
        getMessages().put(3161, new UpdateThreadMessageEvent());
        getMessages().put(1004, new GetForumsMessageEvent());
        getMessages().put(16, new HideThreadMessageEvent()); //
        getMessages().put(2429, new HideReplyMessageEvent()); //
        getMessages().put(3092, new MarkAsReadMessageEvent()); //
    }

    private void registerQuests() {
        getMessages().put(537, new OpenQuestsMessageEvent());
        getMessages().put(1524, new StartQuestMessageEvent());
        getMessages().put(3295, new CancelQuestMessageEvent());
    }

    private void registerCamera() {
        getMessages().put(2983, new PhotoPriceMessageEvent());
        getMessages().put(705, new RenderRoomMessageEvent());
        getMessages().put(1160, new BuyPhotoMessageEvent());
    }

    private void registerMusic() {
        getMessages().put(3847, new SongInventoryMessageEvent());
        getMessages().put(2299, new SongIdMessageEvent());
        getMessages().put(341, new SongDataMessageEvent());
        getMessages().put(3138, new PlaylistAddMessageEvent());
        getMessages().put(2280, new PlaylistRemoveMessageEvent());
        getMessages().put(2304, new PlaylistMessageEvent());
    }

    private void registerPolls() {
        getMessages().put(455, new GetPollMessageEvent());
        getMessages().put(2815, new SubmitPollAnswerMessageEvent());
    }

    private void registerAchievements() {
        getMessages().put(1749, new AchievementsListMessageEvent());
    }

    private void registerMarketplace() {
        getMessages().put(1214, new GetOffersMessageEvent()); //
        getMessages().put(95, new GetSettingsMessageEvent()); //
        getMessages().put(2460, new CanMakeOfferMessageEvent()); //
        getMessages().put(2562, new MakeOfferMessageEvent()); //
        getMessages().put(3012, new GetOwnOffersMessageEvent()); //
        getMessages().put(3554, new CancelOfferMessageEvent()); //
        getMessages().put(2786, new BuyOfferMessageEvent()); //
        getMessages().put(819, new RedeemCoinsMessageEvent()); //
    }

    private void registerAmbassador() {
        getMessages().put(932, new AmbassadorVisiteRoomMessageEvent()); //
        getMessages().put(2114, new AmbassadorSendAlertToPlayerMessageEvent()); //
    }

    private void registerGameCenter() {
        getMessages().put(1242, new GetGameListMessageEvent());
        getMessages().put(3168, new GetGameAccountStatusMessageEvent());
        getMessages().put(1011, new GetGameAchievementsMessageEvent());
        getMessages().put(776, new JoinPlayerQueueMessageEvent());
        getMessages().put(608, new GetPlayableGamesMessageEvent());
    }

    public void handle(MessageEvent message, Session client) {
        final int header = message.getId();
        /*
        System.out.println("--------------------");
        System.out.println("--------------------");
        System.out.println(Integer.toString(header));
        System.out.println("--------------------");
        System.out.println(message.toString());
        System.out.println("--------------------");
        System.out.println("--------------------");*/
        if (Comet.isDebugging) {
            log.debug(message.toString());
        }

        if (!Comet.isRunning)
            return;

        if (this.getMessages().containsKey(header)) {
            try {
                final Event event = this.getMessages().get(header);

                if (event != null) {
                    if (this.asyncEventExecution) {
                        this.eventExecutor.submit(new MessageEventThread(event, client, message));
                    } else {
                        final long start = System.currentTimeMillis();
                        log.debug("Started packet process for packet: [" + event.getClass().getSimpleName() + "][" + header + "]");

                        event.handle(client, message);

                        long timeTakenSinceCreation = ((System.currentTimeMillis() - start));

                        // If the packet took more than 100ms to be handled, red flag!
                        if (timeTakenSinceCreation >= 100) {
                            if (client.getPlayer() != null && client.getPlayer().getData() != null)
                                log.trace("[" + event.getClass().getSimpleName() + "][" + message.getId() + "][" + client.getPlayer().getId() + "][" + client.getPlayer().getData().getUsername() + "] Packet took " + timeTakenSinceCreation + "ms to execute");
                            else
                                log.trace("[" + event.getClass().getSimpleName() + "][" + message.getId() + "] Packet took " + timeTakenSinceCreation + "ms to execute");
                        }

                        log.debug("Finished packet process for packet: [" + event.getClass().getSimpleName() + "][" + header + "] in " + ((System.currentTimeMillis() - start)) + "ms");
                    }
                }
            } catch (Exception e) {
                if (client.getLogger() != null)
                    client.getLogger().error("Error while handling event: " + this.getMessages().get(header).getClass().getSimpleName(), e);
                else
                    log.error("Error while handling event: " + this.getMessages().get(header).getClass().getSimpleName(), e);
            }
        } else if (Comet.isDebugging) {
            log.debug("Unhandled message: " + Events.valueOfId((short) header) + " / " + header);
        }
    }

    public Map<Integer, Event> getMessages() {
        return this.messages;
    }
}
