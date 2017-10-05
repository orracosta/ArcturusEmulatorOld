package com.habboproject.server.game.rooms.objects.entities.types;

import com.habboproject.server.api.game.rooms.entities.PlayerRoomEntity;
import com.habboproject.server.api.game.rooms.settings.RoomAccessType;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.commands.CommandManager;
import com.habboproject.server.game.commands.vip.TransformCommand;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.Group;
import com.habboproject.server.game.moderation.BanManager;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.quests.types.QuestType;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.RoomQueue;
import com.habboproject.server.game.rooms.RoomSpectator;
import com.habboproject.server.game.rooms.objects.entities.PlayerEntityAccess;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.RoomEntityStatus;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.triggers.WiredTriggerPlayerSaysKeyword;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.game.rooms.types.components.types.Trade;
import com.habboproject.server.logging.LogManager;
import com.habboproject.server.logging.entries.RoomVisitLogEntry;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.incoming.room.engine.InitializeRoomMessageEvent;
import com.habboproject.server.network.messages.outgoing.room.access.DoorbellRequestComposer;
import com.habboproject.server.network.messages.outgoing.room.access.RoomReadyMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.alerts.DoorbellNoAnswerComposer;
import com.habboproject.server.network.messages.outgoing.room.alerts.RoomConnectionErrorMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.alerts.RoomErrorMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.AvatarsMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.IdleStatusMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.avatar.MutedMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.HotelViewMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.OpenConnectionMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomForwardMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.RoomPropertyMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.events.RoomPromotionMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.FloodFilterMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreControllerMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreOwnerMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.permissions.YouAreSpectatorMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.queue.RoomQueueStatusMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.settings.RoomRatingMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.utilities.attributes.Attributable;
import org.apache.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class PlayerEntity extends RoomEntity implements PlayerEntityAccess, Attributable, PlayerRoomEntity {
    private static final Logger log = Logger.getLogger(PlayerEntity.class.getName());
    private Player player;
    private PlayerData playerData;

    private int playerId;

    private Map<String, Object> attributes = new HashMap<>();
    private RoomVisitLogEntry visitLogEntry;

    private boolean isFinalized = false;
    private boolean isKicked = false;

    private GameTeam gameTeam = GameTeam.NONE;
    private int kickWalkStage = 0;

    private boolean isQueueing = false;

    private int banzaiPlayerAchievement = 0;

    private boolean hasPlacedPet = false;

    private boolean teleporting = false;
    private int lastPhoto = 0;

    private boolean answeredBubble = false;
    private int onBubble = 0;

    public PlayerEntity(Player player, int identifier, Position startPosition, int startBodyRotation, int startHeadRotation, Room roomInstance) {
        super(identifier, startPosition, startBodyRotation, startHeadRotation, roomInstance);

        this.player = player;

        // create reference to the PlayerDa
        this.playerId = player.getId();
        this.playerData = player.getData();

        if (this.player.isInvisible()) {
            this.updateVisibility(false);
        }

        if (this.getPlayer().isTeleporting() && this.getPlayer().getTeleportRoomId() == roomInstance.getId())
            this.setOverriden(true);

        if (LogManager.ENABLED)
            this.visitLogEntry = LogManager.getInstance().getStore().getRoomVisitContainer().put(player.getId(), roomInstance.getId(), Comet.getTime());
    }

    @Override
    public void joinRoom(Room room, String password) {
        if (this.isFinalized()) return;

        boolean isAuthFailed = false;
        boolean isSpectating = this.getPlayer().isSpectating(room.getId());

        if (this.getRoom() == null) {
            this.getPlayer().getSession().send(new HotelViewMessageComposer());
            isAuthFailed = true;
        }

        // Room full, no slot available
        if (!isSpectating && !this.getPlayer().hasQueued(room.getId()) && !isAuthFailed && this.getPlayerId() != this.getRoom().getData().getOwnerId() && this.getRoom().getEntities().playerCount() >= this.getRoom().getData().getMaxUsers() &&
                !this.getPlayer().getPermissions().getRank().roomEnterFull()) {

            if (RoomQueue.getInstance().hasQueue(room.getId())) {
                RoomQueue.getInstance().addPlayerToQueue(room.getId(), this.playerId);

                this.isQueueing = true;
                this.getPlayer().getSession().send(new RoomQueueStatusMessageComposer(RoomQueue.getInstance().getQueueCount(room.getId(), this.playerId), RoomSpectator.getInstance().getSpectatorsCount(room.getId())));
                return;
            }

            this.getPlayer().getSession().send(new RoomConnectionErrorMessageComposer(1, ""));
            this.getPlayer().getSession().send(new HotelViewMessageComposer());
            isAuthFailed = true;
        }

        // Room bans
        if (!isAuthFailed && this.getRoom().getRights().hasBan(this.getPlayerId()) && this.getPlayer().getPermissions().getRank().roomKickable()) {
            this.getPlayer().getSession().send(new RoomConnectionErrorMessageComposer(4, ""));
            isAuthFailed = true;
        }

        boolean isOwner = (this.getRoom().getData().getOwnerId() == this.getPlayerId());
        boolean isTeleporting = this.getPlayer().isTeleporting() && (this.getPlayer().getTeleportRoomId() == this.getRoom().getId());

        this.getPlayer().getSession().send(new OpenConnectionMessageComposer());

        if (!isAuthFailed && !this.getPlayer().isBypassingRoomAuth() && (!isOwner && !this.getPlayer().getPermissions().getRank().roomEnterLocked() && !this.isDoorbellAnswered()) && !isTeleporting) {
            if (this.getRoom().getData().getAccess() == RoomAccessType.PASSWORD) {
                boolean matched;

                if (CometSettings.roomEncryptPasswords) {
                    matched = BCrypt.checkpw(password, this.getRoom().getData().getPassword());
                } else {
                    matched = this.getRoom().getData().getPassword().equals(password);
                }

                if (!matched) {
                    this.getPlayer().getSession().send(new RoomErrorMessageComposer(-100002));
                    this.getPlayer().getSession().send(new HotelViewMessageComposer());
                    isAuthFailed = true;
                }
            } else if (this.getRoom().getData().getAccess() == RoomAccessType.DOORBELL) {
                if (!this.getRoom().getRights().hasRights(this.playerId)) {
                    if (this.getRoom().getEntities().playerCount() < 1) {
                        this.getPlayer().getSession().send(new DoorbellNoAnswerComposer());
                        this.getPlayer().getSession().send(new HotelViewMessageComposer());

                        isAuthFailed = true;
                    } else {
                        this.getRoom().getEntities().broadcastMessage(new DoorbellRequestComposer(this.getUsername()), true);
                        this.getPlayer().getSession().send(new DoorbellRequestComposer(""));
                        isAuthFailed = true;
                    }
                }
            }
        }

        this.getPlayer().bypassRoomAuth(false);
        this.getPlayer().setTeleportId(0);
        this.getPlayer().setTeleportRoomId(0);

        this.getPlayer().setRoomQueueId(0);

        if (isAuthFailed) {
            return;
        }

        this.getRoom().getEntities().addEntity(this);

        this.finalizeJoinRoom();
    }

    @Override
    protected void finalizeJoinRoom() {
        Session session = this.player.getSession();

        session.send(new RoomReadyMessageComposer(this.getRoom().getId(), this.getRoom().getModel().getId()));

        for (Map.Entry<String, String> decoration : this.getRoom().getData().getDecorations().entrySet()) {
            if (decoration.getKey().equals("wallpaper") || decoration.getKey().equals("floor")) {
                if (decoration.getValue().equals("0.0")) {
                    continue;
                }
            }

            session.send(new RoomPropertyMessageComposer(decoration.getKey(), decoration.getValue()));
        }

        int accessLevel = 0;

        if (this.getRoom().getData().getOwnerId() == this.getPlayerId() || this.getPlayer().getPermissions().getRank().roomFullControl()) {
            this.addStatus(RoomEntityStatus.CONTROLLER, "useradmin");
            session.send(new YouAreOwnerMessageComposer());
            accessLevel = 4;
        } else if (this.getRoom().getRights().hasRights(this.getPlayerId())) {
            this.addStatus(RoomEntityStatus.CONTROLLER, "1");
            accessLevel = 1;
        }

        session.send(new YouAreControllerMessageComposer(accessLevel));

        boolean isSpectating = this.getPlayer().isSpectating(this.getRoom().getId());

        if (!isSpectating) {
            if (this.getRoom().getData().getRequiredBadge() != null) {
                if (!this.getPlayer().getInventory().hasBadge(this.getRoom().getData().getRequiredBadge())) {
                    isSpectating = true;
                } else if (this.getPlayer().getInventory().getBadges().get(this.getRoom().getData().getRequiredBadge()) == 0) {
                    isSpectating = true;
                }
            }
        }

        if (isSpectating) {
            session.send(new YouAreSpectatorMessageComposer());
            this.updateVisibility(false);
        }

        session.send(new RoomRatingMessageComposer(this.getRoom().getData().getScore(), this.canRateRoom()));

        InitializeRoomMessageEvent.heightmapMessageEvent.handle(session, null);

        if (RoomManager.getInstance().hasPromotion(this.getRoom().getId())) {
            session.send(new RoomPromotionMessageComposer(this.getRoom().getData(), this.getRoom().getPromotion()));
        } else {
            session.send(new RoomPromotionMessageComposer(null, null));
        }

        if (this.getPlayer().getEntity().isVisible())
            this.getRoom().getEntities().broadcastMessage(new AvatarsMessageComposer(this.getPlayer().getEntity()));

        this.isFinalized = true;
        this.getPlayer().setSpectatorRoomId(0);
        this.getPlayer().getAchievements().progressAchievement(AchievementType.ROOM_ENTRY, 1);
    }

    public boolean canRateRoom() {
        return !this.getRoom().getRatings().contains(this.getPlayerId());
    }

    @Override
    public void leaveRoom(boolean isOffline, boolean isKick, boolean toHotelView) {
        if (this.isQueueing) {
            RoomQueue.getInstance().removePlayerFromQueue(this.getRoom().getId(), this.playerId);
        }

        RoomSpectator.getInstance().removePlayerFromSpectateMode(this.getRoom().getId(), this.getPlayer().getId());

        try {
            if (RoomQueue.getInstance().hasQueue(this.getRoom().getId()) && !this.isQueueing) {
                int nextPlayer = RoomQueue.getInstance().getNextPlayer(this.getRoom().getId());

                RoomQueue.getInstance().removePlayerFromQueue(this.getRoom().getId(), nextPlayer);
                Session nextPlayerSession = NetworkManager.getInstance().getSessions().getByPlayerId(nextPlayer);

                if (nextPlayerSession != null) {
                    nextPlayerSession.getPlayer().setRoomQueueId(this.getRoom().getId());

                    if (nextPlayerSession.getPlayer().getEntity() != null && nextPlayerSession.getPlayer().getEntity().getRoom().getId() == this.getRoom().getId()) {
                        nextPlayerSession.send(new RoomForwardMessageComposer(this.getRoom().getId()));
                    }
                }
            }
        } catch (Exception ignored) {

        }

        for (BotEntity entity : this.getRoom().getEntities().getBotEntities()) {
            if (entity.getAI().onPlayerLeave(this)) break;
        }

        for (Map.Entry<Long, RoomItemFloor> floorItem : this.getRoom().getItems().getFloorItems().entrySet()) {
            if (floorItem.getValue() == null) continue;

            floorItem.getValue().onEntityLeaveRoom(this);
        }

        // Check and cancel any active trades
        Trade trade = this.getRoom().getTrade().get(this);

        if (trade != null) {
            trade.cancel(this.getPlayerId());
        }

        if (this.getMountedEntity() != null) {
            this.getMountedEntity().setOverriden(false);
            this.getMountedEntity().setHasMount(false);
        }

        // Step off
        for (RoomItemFloor item : this.getRoom().getItems().getItemsOnSquare(this.getPosition().getX(), this.getPosition().getY())) {
            if (item == null) continue;
            item.onEntityStepOff(this);
        }

        if (isKick && !isOffline && this.getPlayer() != null && this.getPlayer().getSession() != null) {
            this.getPlayer().getSession().send(new RoomErrorMessageComposer(4008));
        }

        // Send leave room message to all current entities
        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));

        // Sending this user to the hotel view?
        if (!isOffline && toHotelView && this.getPlayer() != null && this.getPlayer().getSession() != null) {
            this.getPlayer().getSession().send(new HotelViewMessageComposer());

            if (this.getPlayer().getData() != null) {
                this.getPlayer().getMessenger().sendStatus(true, false);
            }
        }

        if (this.hasPlacedPet && this.getRoom().getData().getOwnerId() != this.playerId) {
            for (PetEntity petEntity : this.getRoom().getEntities().getPetEntities()) {
                if (petEntity.getData().getOwnerId() == this.getPlayerId()) {
                    petEntity.kick();
                }
            }
        }

        // Remove entity from the room
        this.getRoom().getEntities().removeEntity(this);

        if (this.player != null) {
            this.getPlayer().setEntity(null);
        }

        if (this.visitLogEntry != null) {
            this.visitLogEntry.setExitTime((int) Comet.getTime());

            LogManager.getInstance().getStore().getRoomVisitContainer().updateExit(this.visitLogEntry);
        }

        this.getStatuses().clear();
        this.attributes.clear();

        // De-reference things
        this.player = null;
        this.playerData = null;
    }

    @Override
    public void kick() {
        this.isKicked = true;
        this.setCanWalk(false);

        this.moveTo(this.getRoom().getModel().getDoorX(), this.getRoom().getModel().getDoorY());
    }

    private int lastMessageCounter = 0;
    private String lastMessage = "";

    @Override
    public boolean onChat(String message) {
        final long time = System.currentTimeMillis();

        if (WiredTriggerPlayerSaysKeyword.executeTriggers(this, message)) {
            return false;
        }

        if (!this.getPlayer().getPermissions().getRank().floodBypass()) {
            if (this.lastMessage.equals(message)) {
                this.lastMessageCounter++;

                if (this.lastMessageCounter >= 3) {
                    this.getPlayer().setRoomFloodTime(this.getPlayer().getPermissions().getRank().floodTime());
                }
            } else {
                this.lastMessage = message;
                this.lastMessageCounter = 0;
            }

            if (time - this.getPlayer().getRoomLastMessageTime() < 750) {
                this.getPlayer().setRoomFloodFlag(this.getPlayer().getRoomFloodFlag() + 1);

                if (this.getPlayer().getRoomFloodFlag() >= 3) {
                    this.getPlayer().setRoomFloodTime(this.getPlayer().getPermissions().getRank().floodTime());
                    this.getPlayer().setRoomFloodFlag(0);

                    this.getPlayer().getSession().send(new FloodFilterMessageComposer(player.getRoomFloodTime()));
                }
            } else {
                this.getPlayer().setRoomFloodFlag(0);
            }

            if (this.getPlayer().getRoomFloodTime() >= 1) {
                return false;
            }

            player.setRoomLastMessageTime(time);
            player.setLastMessage(message);
        }

        if (message.isEmpty() || message.length() > 100)
            return false;

        try {
            if (CommandManager.getInstance().isCommand(message)) {
                if (CommandManager.getInstance().parse(message, this.getPlayer().getSession()))
                    return false;
            } else if (CommandManager.getInstance().getNotifications().isNotificationExecutor(message, this.getPlayer().getData().getRank())) {
                CommandManager.getInstance().getNotifications().execute(this.player, message.substring(1));
            }
        } catch (Exception e) {
            log.error("Error while executing command", e);
            return false;
        }

        if (this.isRoomMuted() && !this.getPlayer().getPermissions().getRank().roomMuteBypass() && this.getRoom().getData().getOwnerId() != this.getPlayerId()) {
            return false;
        }

        if ((this.getRoom().getRights().hasMute(this.getPlayerId()) || BanManager.getInstance().isMuted(this.getPlayerId())) && !this.getPlayer().getPermissions().getRank().roomMuteBypass()) {
            this.getPlayer().getSession().send(new MutedMessageComposer(this.getRoom().getRights().getMuteTime(this.getPlayerId())));

            return false;
        }

//        for (PetEntity entity : this.getRoom().getEntities().getPetEntities()) {
//            if (message.split(" ").length > 0) {
//                if (entity.getDatav().getName().toLowerCase().equals(message.split(" ")[0].toLowerCase())) {
//                    if (entity.getAI().onTalk(this, message)) {
//                        return false;
//                    }
//                }
//            }
//        }

        if (this.getRoom().getEntities().playerCount() > 1) {
            this.getPlayer().getQuests().progressQuest(QuestType.SOCIAL_CHAT);
        }

        this.unIdle();
        return true;
    }

    public void postChat(String message) {
        for (Map.Entry<Integer, RoomEntity> entity : this.getRoom().getEntities().getAllEntities().entrySet()) {
            if (entity.getValue().getAI() != null)
                entity.getValue().getAI().onTalk(this, message);
        }
    }

    @Override
    public boolean onRoomDispose() {
        // Clear all  statuses
        this.getStatuses().clear();

        // Send leave room message to all current entities
        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));

        // Sending this user to the hotel view?
        this.getPlayer().getSession().send(new HotelViewMessageComposer());
        this.getPlayer().getSession().getPlayer().getMessenger().sendStatus(true, false);

        // Check and cancel any active trades
        Trade trade = this.getRoom().getTrade().get(this);

        if (trade != null) {
            trade.cancel(this.getPlayerId());
        }

//        if (this.visitLogEntry != null) {
//            this.visitLogEntry.setExitTime((int) Comet.getTime());
//
//            if (LogManager.ENABLED)
//                LogManager.getInstance().getStore().getRoomVisitContainer().updateExit(this.visitLogEntry);
//        }

        if (this.getPlayer() != null) {
            // De-reference things
            this.getPlayer().setEntity(null);
            this.player = null;
        }

        return false;
    }

    @Override
    public void setIdle() {
        super.setIdle();

        this.getRoom().getEntities().broadcastMessage(new IdleStatusMessageComposer(this.getPlayerId(), true));
    }

    public int getPlayerId() {
        return this.playerId;
    }

    @Override
    public String getUsername() {
        return this.playerData == null ? "UnknownPlayer" + this.playerId : this.playerData.getUsername();
    }

    @Override
    public String getMotto() {
        return this.playerData == null ? "" : this.playerData.getMotto();
    }

    @Override
    public String getFigure() {
        return this.playerData == null ? "" : this.playerData.getFigure();
    }

    @Override
    public String getGender() {
        return this.playerData == null ? "M" : this.playerData.getGender();
    }

    @Override
    public void compose(IComposer msg) {
        if (this.hasAttribute("transformation")) {
            String[] transformationData = ((String) this.getAttribute("transformation")).split("#");

            TransformCommand.composeTransformation(msg, transformationData, this);
            return;
        }

        msg.writeInt(this.getPlayerId());
        msg.writeString(this.getUsername().replace("<", "").replace(">", "")); // Client sometimes parses the username as HTML...
        msg.writeString(this.getMotto());
        msg.writeString(this.getFigure());
        msg.writeInt(this.getId());

        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeDouble(this.getPosition().getZ());

        msg.writeInt(this.getBodyRotation()); // 2 = user 4 = bot
        msg.writeInt(1); // 1 = user 2 = pet 3 = bot

        msg.writeString(this.getGender().toLowerCase());

        if (this.playerData == null || this.playerData.getFavouriteGroup() == 0) {
            msg.writeInt(-1);
            msg.writeInt(-1);
            msg.writeInt(0);
        } else {
            Group group = GroupManager.getInstance().get(this.playerData.getFavouriteGroup());

            if (group == null) {
                msg.writeInt(-1);
                msg.writeInt(-1);
                msg.writeInt(0);

                this.playerData.setFavouriteGroup(0);
                this.playerData.save();
            } else {
                msg.writeInt(group.getId());
                msg.writeInt(2);
                msg.writeString(group.getData().getTitle());
                msg.writeString("");
            }
        }

        msg.writeInt(this.playerData == null ? 0 : this.playerData.getAchievementPoints()); //achv points
        msg.writeBoolean(false);
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Deprecated
    public void dispose() {
        this.leaveRoom(true, false, false);
        this.attributes.clear();
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        if (this.attributes.containsKey(attributeKey)) {
            this.attributes.replace(attributeKey, attributeValue);
        } else {
            this.attributes.put(attributeKey, attributeValue);
        }
    }

    @Override
    public Object getAttribute(String attributeKey) {
        return this.attributes.get(attributeKey);
    }

    @Override
    public boolean hasAttribute(String attributeKey) {
        return this.attributes.containsKey(attributeKey);
    }

    @Override
    public void removeAttribute(String attributeKey) {
        this.attributes.remove(attributeKey);
    }

    @Override
    public boolean isWalkCancelled() {
        if (this.getPlayer().getFreeze().isFreezed()) {
            return true;
        }

        return super.isWalkCancelled();
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public GameTeam getGameTeam() {
        return gameTeam;
    }

    public void setGameTeam(GameTeam gameTeam) {
        this.gameTeam = gameTeam;
    }

    public boolean isKicked() {
        return isKicked;
    }

    public int getKickWalkStage() {
        return kickWalkStage;
    }

    public void increaseKickWalkStage() {
        this.kickWalkStage++;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getBanzaiPlayerAchievement() {
        return banzaiPlayerAchievement;
    }

    public void setBanzaiPlayerAchievement(int banzaiPlayerAchievement) {
        this.banzaiPlayerAchievement = banzaiPlayerAchievement;
    }

    public void incrementBanzaiPlayerAchievement() {
        this.banzaiPlayerAchievement++;
    }

    public void setPlacedPet(boolean hasPlacedPet) {
        this.hasPlacedPet = hasPlacedPet;
    }

    public boolean isTeleporting() {
        return this.teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    public int getLastPhoto() {
        return lastPhoto;
    }

    public void setLastPhoto(int lastPhoto) {
        this.lastPhoto = lastPhoto;
    }

    public boolean isAnsweredBubble() {
        return answeredBubble;
    }

    public void setAnsweredBubble(boolean answeredBubble) {
        this.answeredBubble = answeredBubble;
    }

    public int getOnBubble() {
        return onBubble;
    }

    public void setOnBubble(int onBubble) {
        this.onBubble = onBubble;
    }
}
