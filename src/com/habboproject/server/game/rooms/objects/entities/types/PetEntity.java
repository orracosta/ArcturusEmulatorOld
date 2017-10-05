package com.habboproject.server.game.rooms.objects.entities.types;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.pets.data.PetData;
import com.habboproject.server.game.players.PlayerManager;
import com.habboproject.server.game.players.data.PlayerAvatar;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.ai.BotAI;
import com.habboproject.server.game.rooms.objects.entities.types.ai.pets.PetAI;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetBreedingBoxFloorItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.network.messages.outgoing.room.avatar.LeaveRoomMessageComposer;
import com.habboproject.server.storage.queries.pets.PetDao;
import com.habboproject.server.utilities.RandomInteger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PetEntity extends RoomEntity {
    private PetData data;
    private PetAI ai;

    private PetBreedingBoxFloorItem breedingBox;

    private int cycleCount = 0;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    public PetEntity(PetData data, int identifier, Position startPosition, int startBodyRotation, int startHeadRotation, Room roomInstance) {
        super(identifier, startPosition, startBodyRotation, startHeadRotation, roomInstance);

        this.data = data;

        this.ai = new PetAI(this);
    }

    @Override
    public void joinRoom(Room room, String password) {

    }

    @Override
    protected void finalizeJoinRoom() {

    }

    @Override
    public void leaveRoom(boolean isOffline, boolean isKick, boolean toHotelView) {
        this.leaveRoom(false);
    }

    public void leaveRoom(boolean save) {
        if (save) {
            PetDao.savePosition(this.getPosition().getX(), this.getPosition().getY(), this.data.getId());
        }

        this.getRoom().getEntities().removeEntity(this);
        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));
        this.attributes.clear();
    }

    @Override
    public boolean onChat(String message) {
        return false;
    }

    @Override
    public boolean onRoomDispose() {
        if (this.getData().needsUpdate()) {
            this.getData().saveStats();
        }

        PetDao.savePosition(this.getPosition().getX(), this.getPosition().getY(), this.data.getId());

        this.getRoom().getEntities().broadcastMessage(new LeaveRoomMessageComposer(this.getId()));

        this.attributes.clear();

        return true;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getMotto() {
        return null;
    }

    @Override
    public String getFigure() {
        return null;
    }

    @Override
    public String getGender() {
        return null;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.getData().getId());
        msg.writeString(this.getData().getName());
        msg.writeString("PET_MOTTO");

        String look = this.getData().getLook();

        switch (this.getData().getTypeId()) {
            default:
                look += " 2 2 -1 0 3 -1 0";
                break;

            case 12: // dragon
                look = "12 0 ffffff 2 2 -1 0 3 -1 0";
                break;

            case 15: // horse
                look += new StringBuilder().append(" ").append(this.getData().isSaddled() ? "3" : "2").append(" 2 ").append(this.getData().getHair()).append(" ").append(this.getData().getHairDye()).append(" 3 ").append(this.getData().getHair()).append(" ").append(this.getData().getHairDye()).append(this.getData().isSaddled() ? "0 4 9 0" : "").toString();
                break;

            case 16: // monsterplant
            case 17: // bunnyeaster
            case 18: // bunnyevil
            case 19: // bunnydepressed
            case 20: // bunnylove
                break;

            case 21: // pigeongood
            case 22: // pigeonevil
            case 23: // demonmonkey
                break;

            // 24 bearbaby
            // 25 terrierbaby

            case 26: // gnome
            case 27: // leprechaun
                look +=  new StringBuilder().append(" ").append(this.getData().getExtraData());
                break;
        }

        msg.writeString(look);

        msg.writeInt(this.getId());
        msg.writeInt(this.getPosition().getX());
        msg.writeInt(this.getPosition().getY());
        msg.writeDouble(this.getPosition().getZ());
        msg.writeInt(0);
        msg.writeInt(2);

        msg.writeInt(this.data.getRaceId());

        msg.writeInt(this.data.getOwnerId());

        final PlayerAvatar petOwner = PlayerManager.getInstance().getAvatarByPlayerId(this.getData().getOwnerId(), PlayerAvatar.USERNAME_FIGURE);

        msg.writeString(petOwner == null ? "trylix" : petOwner.getUsername());

        msg.writeInt(0);
        msg.writeBoolean(true); // has saddle
        msg.writeBoolean(this.hasMount()); // has rider?

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeString("");
    }

    public PetData getData() {
        return data;
    }

    public int getCycleCount() {
        return this.cycleCount;
    }

    public void decrementCycleCount() {
        cycleCount--;
    }

    public void incrementCycleCount() {
        cycleCount++;
    }

    public void resetCycleCount() {
        this.cycleCount = 0;
    }

    @Override
    public BotAI getAI() {
        return ai;
    }

    public PetAI getPetAI() {
        return this.ai;
    }

    public PetBreedingBoxFloorItem getBreedingBox() {
        return breedingBox;
    }

    public void setBreedingBox(PetBreedingBoxFloorItem breedingBox) {
        this.breedingBox = breedingBox;
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
}
