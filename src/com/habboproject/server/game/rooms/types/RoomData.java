package com.habboproject.server.game.rooms.types;

import com.habboproject.server.api.game.rooms.IRoomData;
import com.habboproject.server.api.game.rooms.RoomCategory;
import com.habboproject.server.api.game.rooms.settings.*;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.storage.queries.camera.CameraDao;
import com.habboproject.server.storage.queries.rooms.RoomDao;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoomData implements IRoomData {
    private int id;
    private RoomType type;

    private String name;
    private String description;
    private int ownerId;
    private String owner;
    private int category;
    private int maxUsers;
    private RoomAccessType access;
    private String password;
    private String originalPassword;
    private RoomTradeState tradeState;

    private int score;

    private String[] tags;
    private Map<String, String> decorations;

    private String model;

    private boolean hideWalls;
    private int thicknessWall;
    private int thicknessFloor;
    private boolean allowWalkthrough;
    private boolean allowPets;
    private String heightmap;

    private RoomMuteState muteState;
    private RoomKickState kickState;
    private RoomBanState banState;

    private int bubbleMode;
    private int bubbleType;
    private int bubbleScroll;
    private int chatDistance;

    private int antiFloodSettings;

    private List<String> disabledCommands;

    private int groupId;

    private long lastReferenced = Comet.getTime();

    private String requiredBadge;

    private String thumbnail;

    public RoomData(ResultSet room) throws SQLException {
        this.id = room.getInt("id");
        this.type = RoomType.valueOf(room.getString("type"));
        this.name = room.getString("name");
        this.description = room.getString("description");
        this.ownerId = room.getInt("owner_id");
        this.owner = RoomDao.getRoomOwnerNameById(this.ownerId);
        this.category = room.getInt("category");
        this.maxUsers = room.getInt("max_users");

        String accessType = room.getString("access_type");
        if (!accessType.equals("open") && !accessType.equals("doorbell") && !accessType.equals("password") && !accessType.equals("invisible")) {
            accessType = "open";
        }

        this.password = room.getString("password");
        this.access = RoomAccessType.valueOf(accessType.toUpperCase());
        this.originalPassword = this.password;

        this.score = room.getInt("score");

        this.tags = room.getString("tags").isEmpty() ? new String[0] : room.getString("tags").split(",");
        this.decorations = new HashMap<>();

        String[] decorations = room.getString("decorations").split(",");

        for (int i = 0; i < decorations.length; i++) {
            String[] decoration = decorations[i].split("=");

            if (decoration.length == 2)
                this.decorations.put(decoration[0], decoration[1]);
        }

        this.model = room.getString("model");

        this.hideWalls = room.getString("hide_walls").equals("1");
        this.thicknessWall = room.getInt("thickness_wall");
        this.thicknessFloor = room.getInt("thickness_floor");
        this.allowWalkthrough = room.getString("allow_walkthrough").equals("1");
        this.allowPets = room.getString("allow_pets").equals("1");
        this.heightmap = room.getString("heightmap");
        this.tradeState = RoomTradeState.valueOf(room.getString("trade_state"));

        this.kickState = RoomKickState.valueOf(room.getString("kick_state"));
        this.banState = RoomBanState.valueOf(room.getString("ban_state"));
        this.muteState = RoomMuteState.valueOf(room.getString("mute_state"));

        this.bubbleMode = room.getInt("bubble_mode");
        this.bubbleScroll = room.getInt("bubble_scroll");
        this.bubbleType = room.getInt("bubble_type");
        this.antiFloodSettings = room.getInt("flood_level");
        this.chatDistance = room.getInt("chat_distance");

        this.disabledCommands = Lists.newArrayList(room.getString("disabled_commands").split(","));
        this.groupId = room.getInt("group_id");
        this.requiredBadge = room.getString("required_badge");

        this.thumbnail = CameraDao.getThumbnail(this.id);
    }

    public void save() {
        String tagString = "";

        for (int i = 0; i < tags.length; i++) {
            if (i != 0) {
                tagString += ",";
            }

            tagString += tags[i];
        }

        String decorString = "";

        for (Map.Entry<String, String> decoration : decorations.entrySet()) {
            decorString += decoration.getKey() + "=" + decoration.getValue() + ",";
        }

        if (CometSettings.roomEncryptPasswords) {
            if (!this.password.equals(this.originalPassword)) {
                this.password = BCrypt.hashpw(this.password, BCrypt.gensalt(CometSettings.roomPasswordEncryptionRounds));
            }
        }

        RoomDao.updateRoom(id, name, StringUtils.abbreviate(description, 255), ownerId, owner, category, maxUsers, access, password, score,
                tagString, decorString.equals("") ? "" : decorString.substring(0, decorString.length() - 1),
                model, hideWalls, thicknessWall, thicknessFloor, allowWalkthrough, allowPets, heightmap, tradeState,
                muteState, kickState, banState, bubbleMode, bubbleType, bubbleScroll, chatDistance, antiFloodSettings, this.disabledCommands.isEmpty() ? "" : StringUtils.join(this.disabledCommands, ","), this.groupId, this.requiredBadge
        );
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getOwnerId() {
        return this.ownerId;
    }

    public String getOwner() {
        return this.owner;
    }

    public RoomCategory getCategory() {
        return NavigatorManager.getInstance().getCategory(this.category);
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public RoomAccessType getAccess() {
        return this.access;
    }

    public String getPassword() {
        return this.password;
    }

    public int getScore() {
        return this.score;
    }

    public String[] getTags() {
        return this.tags;
    }

    public Map<String, String> getDecorations() {
        return this.decorations;
    }

    public String getModel() {
        return this.model;
    }

    public boolean getHideWalls() {
        return this.hideWalls;
    }

    public int getWallThickness() {
        return this.thicknessWall;
    }

    public int getFloorThickness() {
        return this.thicknessFloor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public void setAccess(RoomAccessType access) {
        this.access = access;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setDecorations(Map<String, String> decorations) {
        this.decorations = decorations;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setHideWalls(boolean hideWalls) {
        this.hideWalls = hideWalls;
    }

    public void setThicknessWall(int thicknessWall) {
        this.thicknessWall = thicknessWall;
    }

    public void setThicknessFloor(int thicknessFloor) {
        this.thicknessFloor = thicknessFloor;
    }

    public boolean getAllowWalkthrough() {
        return this.allowWalkthrough;
    }

    public void setAllowWalkthrough(boolean allowWalkthrough) {
        this.allowWalkthrough = allowWalkthrough;
    }

    public void setHeightmap(String heightmap) {
        this.heightmap = heightmap;
    }

    public String getHeightmap() {
        return this.heightmap;
    }

    public boolean isAllowPets() {
        return allowPets;
    }

    public void setAllowPets(boolean allowPets) {
        this.allowPets = allowPets;
    }

    public long getLastReferenced() {
        return lastReferenced;
    }

    public RoomData setLastReferenced(long lastReferenced) {
        this.lastReferenced = lastReferenced;

        return this;
    }

    public RoomTradeState getTradeState() {
        return tradeState;
    }

    public void setTradeState(RoomTradeState tradeState) {
        this.tradeState = tradeState;
    }

    public int getBubbleMode() {
        return bubbleMode;
    }

    public void setBubbleMode(int bubbleMode) {
        this.bubbleMode = bubbleMode;
    }

    public int getBubbleType() {
        return bubbleType;
    }

    public void setBubbleType(int bubbleType) {
        this.bubbleType = bubbleType;
    }

    public int getBubbleScroll() {
        return bubbleScroll;
    }

    public void setBubbleScroll(int bubbleScroll) {
        this.bubbleScroll = bubbleScroll;
    }

    public int getChatDistance() {
        return chatDistance;
    }

    public void setChatDistance(int chatDistance) {
        this.chatDistance = chatDistance;
    }

    public int getAntiFloodSettings() {
        return antiFloodSettings;
    }

    public void setAntiFloodSettings(int antiFloodSettings) {
        this.antiFloodSettings = antiFloodSettings;
    }

    public RoomMuteState getMuteState() {
        return muteState;
    }

    public void setMuteState(RoomMuteState muteState) {
        this.muteState = muteState;
    }

    public RoomKickState getKickState() {
        return kickState;
    }

    public void setKickState(RoomKickState kickState) {
        this.kickState = kickState;
    }

    public RoomBanState getBanState() {
        return banState;
    }

    public void setBanState(RoomBanState banState) {
        this.banState = banState;
    }

    @Override
    public List<String> getDisabledCommands() {
        return this.disabledCommands;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public String getDecorationString() {
        String decorString = "";

        for (Map.Entry<String, String> decoration : decorations.entrySet()) {
            decorString += decoration.getKey() + "=" + decoration.getValue() + ",";
        }

        return decorString;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getRequiredBadge() {
        return this.requiredBadge;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
