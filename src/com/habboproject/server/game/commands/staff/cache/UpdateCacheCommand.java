package com.habboproject.server.game.commands.staff.cache;

import com.habboproject.server.api.networking.sessions.BaseSession;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.achievements.AchievementManager;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.effects.EffectManager;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.landing.LandingManager;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.moderation.ModerationManager;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.pets.PetManager;
import com.habboproject.server.game.pets.commands.PetCommandManager;
import com.habboproject.server.game.polls.PollManager;
import com.habboproject.server.game.polls.types.Poll;
import com.habboproject.server.game.quests.QuestManager;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.catalog.CatalogPublishMessageComposer;
import com.habboproject.server.network.messages.outgoing.moderation.ModToolMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.MotdNotificationMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.polls.InitializePollMessageComposer;
import com.habboproject.server.network.sessions.Session;


public class UpdateCacheCommand extends ChatCommand {

    @Override
    public void execute(Session client, String[] params) {
        String command = params.length == 0 ? "" : params[0];

        switch (command) {
            default:
                client.send(new MotdNotificationMessageComposer(
                        "Here's a list of what you can reload using the :reload <type> command!\n\n" +
                                "- bans\n" +
                                "- catalog\n" +
                                "- navigator\n" +
                                "- permissions\n" +
                                "- catalog\n" +
                                "- news\n" +
                                "- config\n" +
                                "- items\n" +
                                "- filter\n" +
                                "- locale\n" +
                                "- modpresets\n" +
                                "- groupitems\n" +
                                "- models\n" +
                                "- music\n" +
                                "- quests\n" +
                                "- achievements\n" +
                                "- pets\n" +
                                "- polls\n" +
                                "- effects"
                ));

                break;
            case "bans":
                BanManager.getInstance().loadBans();

                sendNotif(Locale.get("command.reload.bans"), client);
                break;

            case "catalog":
                CatalogManager.getInstance().loadItemsAndPages();
                CatalogManager.getInstance().loadGiftBoxes();

                NetworkManager.getInstance().getSessions().broadcast(new CatalogPublishMessageComposer(true));
                sendNotif(Locale.get("command.reload.catalog"), client);
                break;

            case "navigator":
                NavigatorManager.getInstance().loadCategories();
                NavigatorManager.getInstance().loadPublicRooms();
                NavigatorManager.getInstance().loadStaffPicks();

                sendNotif(Locale.get("command.reload.navigator"), client);
                break;

            case "permissions":
                PermissionsManager.getInstance().loadRankPermissions();
                PermissionsManager.getInstance().loadPerks();
                PermissionsManager.getInstance().loadCommands();

                sendNotif(Locale.get("command.reload.permissions"), client);
                break;

            case "config":
                CometSettings.initialize();

                sendNotif(Locale.get("command.reload.config"), client);
                break;

            case "news":
                LandingManager.getInstance().loadArticles();

                sendNotif(Locale.get("command.reload.news"), client);
                break;

            case "items":
                ItemManager.getInstance().loadItemDefinitions();

                sendNotif(Locale.get("command.reload.items"), client);
                break;

            case "filter":
                RoomManager.getInstance().getFilter().loadFilter();

                sendNotif(Locale.get("command.reload.filter"), client);
                break;

            case "locale":
                Locale.reload();
                CommandManager.getInstance().reloadAllCommands();

                sendNotif(Locale.get("command.reload.locale"), client);
                break;

            case "modpresets":
                ModerationManager.getInstance().loadPresets();

                sendNotif(Locale.get("command.reload.modpresets"), client);

                for (BaseSession session : NetworkManager.getInstance().getSessions().getByPlayerPermission("mod_tool")) {
                    session.send(new ModToolMessageComposer());
                }
                break;

            case "groupitems":
                GroupManager.getInstance().getGroupItems().load();
                sendNotif(Locale.get("command.reload.groupitems"), client);
                break;

            case "models":
                RoomManager.getInstance().loadModels();
                sendNotif(Locale.get("command.reload.models"), client);
                break;

            case "music":
                ItemManager.getInstance().loadMusicData();
                sendNotif(Locale.get("command.reload.music"), client);
                break;

            case "quests":
                QuestManager.getInstance().loadQuests();
                sendNotif(Locale.get("command.reload.quests"), client);
                break;

            case "achievements":
                AchievementManager.getInstance().loadAchievements();

                sendNotif(Locale.get("command.reload.achievements"), client);
                break;

            case "pets":
                PetManager.getInstance().loadPetRaces();
                PetManager.getInstance().loadPetSpeech();
                PetManager.getInstance().loadTransformablePets();
                PetCommandManager.getInstance().initialize();

                sendNotif(Locale.get("command.reload.pets"), client);
                break;

            case "polls":
                PollManager.getInstance().initialize();

                if(PollManager.getInstance().roomHasPoll(client.getPlayer().getEntity().getRoom().getId())) {
                    Poll poll = PollManager.getInstance().getPollByRoomId(client.getPlayer().getEntity().getRoom().getId());

                    client.getPlayer().getEntity().getRoom().getEntities().broadcastMessage(new InitializePollMessageComposer(poll.getPollId(), poll.getPollTitle(), poll.getThanksMessage()));
                }

                sendNotif(Locale.get("command.reload.polls"), client);
                break;

            case "effects":
            {
                EffectManager.getInstance().initialize();

                sendNotif(Locale.get("command.reload.effects"), client);
                break;
            }
        }
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getPermission() {
        return "reload_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.reload.description");
    }
}
