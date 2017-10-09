package com.habboproject.server.game.permissions;

import com.habboproject.server.game.permissions.types.CommandPermission;
import com.habboproject.server.game.permissions.types.Perk;
import com.habboproject.server.game.permissions.types.Rank;
import com.habboproject.server.storage.queries.permissions.PermissionsDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PermissionsManager implements Initializable {
    private static PermissionsManager permissionsManagerInstance;

    private Map<Integer, Perk> perks;
    private Map<Integer, Rank> ranks;
    private Map<String, CommandPermission> commands;

    private static Logger log = Logger.getLogger(PermissionsManager.class.getName());

    public PermissionsManager() {

    }

    @Override
    public void initialize() {
        this.perks = new HashMap<>();
        this.commands = new HashMap<>();
        this.ranks = new HashMap<>();

        this.loadPerks();
        this.loadRankPermissions();
        this.loadCommands();


        log.info("PermissionsManager initialized");
    }

    public static PermissionsManager getInstance() {
        if (permissionsManagerInstance == null)
            permissionsManagerInstance = new PermissionsManager();

        return permissionsManagerInstance;
    }

    public void loadPerks() {
        try {
            if (this.getPerks().size() != 0) {
                this.getPerks().clear();
            }

            this.perks = PermissionsDao.getPerks();

        } catch (Exception e) {
            log.error("Error while loading perk permissions", e);
            return;
        }

        log.info("Loaded " + this.getPerks().size() + " perks");
    }

    public void loadRankPermissions() {
        try {
            if (this.getRankPermissions().size() != 0) {
                this.getRankPermissions().clear();
            }

            this.ranks = PermissionsDao.getRankPermissions();
        } catch (Exception e) {
            log.error("Error while loading rank permissions", e);
            return;
        }

        log.info("Loaded " + this.getRankPermissions().size() + " ranks");
    }

    public void loadCommands() {
        try {
            if (this.getCommands().size() != 0) {
                this.getCommands().clear();
            }

            this.commands = PermissionsDao.getCommandPermissions();

        } catch (Exception e) {
            log.error("Error while reloading command permissions", e);
            return;
        }

        log.info("Loaded " + this.getCommands().size() + " command permissions");
    }

    public Rank getRank(final int playerRankId) {
        final Rank rank = this.ranks.get(playerRankId);

        if (rank == null) {
            log.warn("Failed to find rank by rank ID: " + playerRankId + ", are you sure it exists?");
            return this.ranks.get(1);
        }

        return rank;
    }

    public List<Integer> getRankByPermission(String permission) {
        List<Integer> enabledRanks = Lists.newArrayList();

        for (Rank rank : ranks.values()) {
            switch (permission) {
                case "flood_bypass":
                    if (rank.floodBypass()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "disconnectable":
                    if (rank.disconnectable()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "mod_tool":
                    if (rank.modTool()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "bannable":
                    if (rank.bannable()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_kickable":
                    if (rank.roomKickable()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_full_control":
                    if (rank.roomFullControl()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_mute_bypass":
                    if (rank.roomMuteBypass()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_filter_bypass":
                    if (rank.roomFilterBypass()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_ignorable":
                    if (rank.roomIgnorable()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_enter_full":
                    if (rank.roomEnterFull()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_enter_locked":
                    if (rank.roomEnterLocked()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_staff_pick":
                    if (rank.roomStaffPick()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_see_whispers":
                    if (rank.roomSeeWhispers()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "messenger_staff_chat":
                    if (rank.messengerStaffChat()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "about_detailed":
                    if (rank.aboutDetailed()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "about_stats":
                    if (rank.aboutStats()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "ambassador_tool":
                    if (rank.isAmbassador()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "room_public_full_acess":
                    if (rank.roomFullAcessPublic()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;

                case "helper_tool":
                    if (rank.isHelper()) {
                        enabledRanks.add(rank.getId());
                    }
                    break;
            }
        }

        return enabledRanks;
    }

    public Map<Integer, Rank> getRankPermissions() {
        return this.ranks;
    }

    public Map<String, CommandPermission> getCommands() {
        return this.commands;
    }

    public Map<Integer, Perk> getPerks() {
        return perks;
    }
}
