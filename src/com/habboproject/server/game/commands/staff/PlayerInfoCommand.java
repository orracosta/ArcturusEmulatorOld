package com.habboproject.server.game.commands.staff;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.permissions.PermissionsManager;
import com.habboproject.server.game.permissions.types.Rank;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.AdvancedAlertMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;

public class PlayerInfoCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length != 1) return;

        final String username = params[0];
        Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);

        PlayerData playerData;

        if (session == null || session.getPlayer() == null || session.getPlayer().getData() == null) {
            playerData = PlayerDao.getDataByUsername(username);
        } else {
            playerData = session.getPlayer().getData();
        }

        if (playerData == null) return;

        final Rank playerRank = PermissionsManager.getInstance().getRank(playerData.getRank());

        if (playerRank.modTool() && !client.getPlayer().getPermissions().getRank().modTool()) {
            // send player info failed alert
            client.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.playerinfo.title", "Player Information") + ": " + username, Locale.getOrDefault("command.playerinfo.staff", "You cannot view the information of a staff member!")));
            return;
        }

        final StringBuilder userInfo = new StringBuilder();

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.id", "ID") + "</b>: " + playerData.getId() + "<br>");
        }

        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.username", "Username") + "</b>: " + playerData.getUsername() + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.motto", "Motto") + "</b>: " + playerData.getMotto() + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.gender", "Gender") + "</b>: " + (playerData.getGender().toLowerCase().equals("m") ? Locale.getOrDefault("command.playerinfo.male", "Male") : Locale.getOrDefault("command.playerinfo.female", "Female")) + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.status", "Status") + "</b>: " + (session == null ? Locale.getOrDefault("command.playerinfo.offline", "Offline") : Locale.getOrDefault("command.playerinfo.online", "Online")) + "<br>");
        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.achievementPoints", "Achievement Points") + "</b>: " + playerData.getAchievementPoints() + "<br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.rank", "Rank") + "</b>: " + playerData.getRank() + "<br><br>");
        }

        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.currencyBalances", "Currency Balances") + "</b><br>");
        userInfo.append("<i>" + playerData.getCredits() + " " + Locale.getOrDefault("command.playerinfo.credits", "credits") + "</i><br>");

        if (client.getPlayer().getPermissions().getRank().modTool()) {
            userInfo.append("<i>" + playerData.getVipPoints() + " " + Locale.getOrDefault("command.playerinfo.diamonds", "diamonds") + "</i><br>");
        }

        userInfo.append("<i>" + playerData.getActivityPoints() + " " + Locale.getOrDefault("command.playerinfo.activityPoints", "duckets") + "</i><br><br>");


        userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomInfo", "Room Info") + "</b><br>");

        if (session != null && session.getPlayer().getEntity() != null) {
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomId", "Room ID") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getId() + "<br>");
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomName", "Room Name") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getName() + "<br>");
            userInfo.append("<b>" + Locale.getOrDefault("command.playerinfo.roomOwner", "Room Owner") + "</b>: " + session.getPlayer().getEntity().getRoom().getData().getOwner() + "<br>");
        } else {
            if (session == null)
                userInfo.append("<i>" + Locale.getOrDefault("command.playerinfo.notOnline", "This player is not online!") + "</i>");
            else
                userInfo.append("<i>" + Locale.getOrDefault("command.playerinfo.notInRoom", "This player is not in a room!") + "</i>");
        }

        client.send(new AdvancedAlertMessageComposer(Locale.getOrDefault("command.playerinfo.title", "Player Information") + ": " + username, userInfo.toString()));
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getPermission() {
        return "playerinfo_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.playerinfo.description");
    }
}
