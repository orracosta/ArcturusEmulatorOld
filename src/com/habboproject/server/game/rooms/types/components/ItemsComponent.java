package com.habboproject.server.game.rooms.types.components;

import com.habboproject.server.api.game.furniture.types.FurnitureDefinition;
import com.habboproject.server.api.game.furniture.types.LimitedEditionItem;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.pets.races.BreedingType;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.pathfinding.AffectedTile;
import com.habboproject.server.game.rooms.objects.items.RoomItem;
import com.habboproject.server.game.rooms.objects.items.RoomItemFactory;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.objects.items.types.floor.adjustable.MagicStackFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.banzai.BanzaiTeleportFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.jukebox.SoundMachineFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.DiceFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.GiftFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.others.WaterFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.floor.pet.PetBreedingBoxFloorItem;
import com.habboproject.server.game.rooms.objects.items.types.wall.MoodlightWallItem;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.mapping.RoomTile;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.engine.UpdateStackMapMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.RemoveFloorItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.RemoveWallItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.SendFloorItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.room.items.SendWallItemMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.items.LimitedEditionDao;
import com.habboproject.server.storage.queries.rooms.RoomItemDao;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.habboproject.server.threads.ThreadManager;
import com.habboproject.server.threads.executors.item.FloorItemsCommitEvent;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class ItemsComponent {
    private Room room;
    private final Logger log;

    private final Map<Long, RoomItemFloor> floorItems = new ConcurrentHashMap<>();
    private final Map<Long, RoomItemWall> wallItems = new ConcurrentHashMap<>();

    private Map<Class<? extends RoomItemFloor>, Set<Long>> itemClassIndex = new ConcurrentHashMap<>();
    private Map<String, Set<Long>> itemInteractionIndex = new ConcurrentHashMap<>();

    private long soundMachineId = 0;
    private long moodlightId;

    public ItemsComponent(Room room) {
        this.room = room;
        this.log = Logger.getLogger("Room Items Component [" + room.getData().getName() + "]");

        RoomItemDao.getItems(this.room, this.floorItems, this.wallItems);

        for (RoomItemFloor floorItem : this.floorItems.values()) {
            if (floorItem instanceof SoundMachineFloorItem) {
                soundMachineId = floorItem.getId();
            }

            this.indexItem(floorItem);
        }
    }

    public void onLoaded() {
        for (RoomItemFloor floorItem : floorItems.values()) {
            floorItem.onLoad();
        }

        for (RoomItemWall wallItem : wallItems.values()) {
            wallItem.onLoad();
        }
    }

    public void dispose() {
        for (RoomItemFloor floorItem : floorItems.values()) {
            if (floorItem.needsUpdate()) {
                RoomItemDao.saveItem(floorItem.getId(), floorItem.getPosition().getX(), floorItem.getPosition().getY(),
                        floorItem.getPosition().getZ(), floorItem.getRotation(), floorItem.getDataObject());

                floorItem.needsUpdate(false);
            }

            ItemManager.getInstance().disposeItemVirtualId(floorItem.getId());

            floorItem.onUnload();
        }

        for (RoomItemWall wallItem : wallItems.values()) {
            ItemManager.getInstance().disposeItemVirtualId(wallItem.getId());
            wallItem.onUnload();
        }

        this.floorItems.clear();
        this.wallItems.clear();

        for (Set<Long> itemIds : this.itemClassIndex.values()) {
            itemIds.clear();
        }

        for(Set<Long> itemInteractions : itemInteractionIndex.values()) {
            itemInteractions.clear();
        }

        this.itemInteractionIndex.clear();
        this.itemClassIndex.clear();
    }

    public boolean setMoodlight(long moodlight) {
        if (this.moodlightId != 0)
            return false;

        this.moodlightId = moodlight;
        return true;
    }

    public boolean removeMoodlight() {
        if (this.moodlightId == 0) {
            return false;
        }

        this.moodlightId = 0;
        return true;
    }

    public void commit() {
        synchronized (this.floorItems) {
            ThreadManager.getInstance().execute(new FloorItemsCommitEvent(this.floorItems.values().stream().collect(Collectors.toList())));
        }
    }

    public boolean isMoodlightMatches(RoomItem item) {
        if (this.moodlightId == 0) {
            return false;
        }

        return (this.moodlightId == item.getId());
    }

    public MoodlightWallItem getMoodlight() {
        return (MoodlightWallItem) this.getWallItem(this.moodlightId);
    }

    public RoomItemFloor addFloorItem(long id, int baseId, Room room, int ownerId, int groupId, int x, int y, int rot, double height, String data, LimitedEditionItem limitedEditionItem) {
        RoomItemFloor floor = RoomItemFactory.createFloor(id, baseId, room, ownerId, groupId, x, y, height, rot, data, (LimitedEditionItemData) limitedEditionItem);

        if (floor == null) return null;

        this.floorItems.put(id, floor);
        this.indexItem(floor);

        return floor;
    }

    public RoomItemWall addWallItem(long id, int baseId, Room room, int ownerId, String position, String data) {
        RoomItemWall wall = RoomItemFactory.createWall(id, baseId, room, ownerId, position, data, LimitedEditionDao.get(id));
        this.getWallItems().put(id, wall);

        return wall;
    }

    public List<RoomItemFloor> getItemsOnSquare(Position position) {
        return getItemsOnSquare(position.getX(), position.getY());
    }

    public List<RoomItemFloor> getItemsOnSquare(int x, int y) {
        RoomTile tile = this.getRoom().getMapping().getTile(x, y);

        if (tile == null) {
            return Lists.newArrayList();
        }

        return new ArrayList<>(tile.getItems());
    }

    @Deprecated
    public RoomItemFloor getFloorItem(int id) {
        Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

        if(itemId == null) {
            return null;
        }

        return this.floorItems.get(itemId);
    }

    @Deprecated
    public RoomItemWall getWallItem(int id) {
        Long itemId = ItemManager.getInstance().getItemIdByVirtualId(id);

        if(itemId == null) {
            return null;
        }

        return this.wallItems.get(itemId);
    }

    public RoomItemFloor getFloorItem(long id) {
        return this.floorItems.get(id);
    }

    public RoomItemWall getWallItem(long id) {
        return this.wallItems.get(id);
    }

    public List<RoomItemFloor> getByInteraction(String interaction) {
        List<RoomItemFloor> items = new ArrayList<>();

        for (RoomItemFloor floorItem : this.floorItems.values()) {
            if (floorItem == null || floorItem.getDefinition() == null) continue;

            if (floorItem.getDefinition().getInteraction().equals(interaction)) {
                items.add(floorItem);
            } else if (interaction.contains("%")) {
                if (interaction.startsWith("%") && floorItem.getDefinition().getInteraction().endsWith(interaction.replace("%", ""))) {
                    items.add(floorItem);
                } else if (interaction.endsWith("%") && floorItem.getDefinition().getInteraction().startsWith(interaction.replace("%", ""))) {
                    items.add(floorItem);
                }
            }
        }

        return items;
    }

    public <T extends RoomItemFloor> List<T> getByClass(Class<T> clazz) {
        List<T> items = new ArrayList<>();

        if (this.itemClassIndex.containsKey(clazz)) {
            for (long itemId : this.itemClassIndex.get(clazz)) {
                RoomItemFloor floorItem = this.getFloorItem(itemId);

                if (floorItem == null || floorItem.getDefinition() == null) continue;

                if(!floorItem.getClass().equals(clazz)) {
                    continue;
                }

                T item = ((T) floorItem);
                items.add(item);
            }
        }

        return items;
    }

    public void removeItem(RoomItemWall item, int ownerId, Session client) {
        RoomItemDao.removeItemFromRoom(item.getId(), ownerId);

        room.getEntities().broadcastMessage(new RemoveWallItemMessageComposer(ItemManager.getInstance().getItemVirtualId(item.getId()), ownerId));
        this.getWallItems().remove(item.getId());

        if (client != null && client.getPlayer() != null) {
            client.getPlayer().getInventory().add(item.getId(), item.getItemId(), item.getGroupId(), item.getExtraData(), item.getLimitedEditionItemData());
            client.send(new UpdateInventoryMessageComposer());
        }
    }

    public void removeItem(RoomItemFloor item, Session client) {
        if (item instanceof SoundMachineFloorItem) {
            this.soundMachineId = 0;
        }

        removeItem(item, client, true, false);
    }

    public void removeItem(RoomItemFloor item, Session client, boolean toInventory, boolean delete) {
        List<RoomEntity> affectEntities = room.getEntities().getEntitiesAt(item.getPosition());
        List<Position> tilesToUpdate = new ArrayList<>();

        tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY(), 0d));

        for (RoomEntity entity : affectEntities) {
            item.onEntityStepOff(entity);
        }

        if (item instanceof SoundMachineFloorItem) {
            if (this.soundMachineId == item.getId()) {
                this.soundMachineId = 0;
            }
        }

        for (AffectedTile tile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
            List<RoomEntity> entitiesOnItem = room.getEntities().getEntitiesAt(new Position(tile.x, tile.y));
            tilesToUpdate.add(new Position(tile.x, tile.y, 0d));

            for (RoomEntity entity : entitiesOnItem) {
                item.onEntityStepOff(entity);
            }
        }

        this.getRoom().getEntities().broadcastMessage(new RemoveFloorItemMessageComposer(item.getVirtualId(), client != null ? client.getPlayer().getId() : 0));
        this.getFloorItems().remove(item.getId());

        if (toInventory && client != null) {
            RoomItemDao.removeItemFromRoom(item.getId(), client.getPlayer().getId());

            final PlayerItem playerItem = client.getPlayer().getInventory().add(item.getId(), item.getItemId(), item.getGroupId(), item.getExtraData(), item instanceof GiftFloorItem ? ((GiftFloorItem) item).getGiftData() : null, item.getLimitedEditionItemData());
            client.sendQueue(new UpdateInventoryMessageComposer());
            client.sendQueue(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));
            client.flush();
        } else {
            if (delete)
                RoomItemDao.deleteItem(item.getId());
        }

        for (Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }
    }

    public void removeItem(RoomItemWall item, Session client, boolean toInventory) {
        this.getRoom().getEntities().broadcastMessage(new RemoveWallItemMessageComposer(item.getVirtualId(), client.getPlayer().getId()));
        this.getWallItems().remove(item.getId());

        if (toInventory) {
            RoomItemDao.removeItemFromRoom(item.getId(), client.getPlayer().getId());

            client.getPlayer().getInventory().add(item.getId(), item.getItemId(), item.getGroupId(), item.getExtraData(), item.getLimitedEditionItemData());
            client.send(new UpdateInventoryMessageComposer());
            client.send(new UnseenItemsMessageComposer(new HashMap<Integer, List<Integer>>() {{
                put(1, Lists.newArrayList(item.getVirtualId()));
            }}));
        } else {
            RoomItemDao.deleteItem(item.getId());
        }
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int rotation, boolean save) {
        return moveFloorItem(itemId, newPosition, rotation, save, false);
    }

    public boolean moveFloorItem(long itemId, Position newPosition, int rotation, boolean save, boolean continueRoute) {
        RoomItemFloor item = this.getFloorItem(itemId);
        if (item == null) return false;

        RoomTile tile = this.getRoom().getMapping().getTile(newPosition.getX(), newPosition.getY());

        if (!this.verifyItemPosition(item.getDefinition(), tile, item.getPosition(), continueRoute)) {
        	if (item.getDefinition().getItemName().startsWith(RoomItemFactory.STACK_TOOL)) {
        		if (this.isInvalid(tile, item.getPosition())) {
        			return false;
        		}
        	} else {
        		return false;
        	}
        }

        double height = tile.getStackHeight(item);

        List<RoomItemFloor> floorItemsAt = this.getItemsOnSquare(newPosition.getX(), newPosition.getY());

        for (RoomItemFloor stackItem : floorItemsAt) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(item);
            }
        }

        item.onPositionChanged(newPosition);

        List<RoomEntity> affectEntities0 = room.getEntities().getEntitiesAt(item.getPosition());

        for (RoomEntity entity0 : affectEntities0) {
            item.onEntityStepOff(entity0);
        }

        List<Position> tilesToUpdate = new ArrayList<>();

        tilesToUpdate.add(new Position(item.getPosition().getX(), item.getPosition().getY()));
        tilesToUpdate.add(new Position(newPosition.getX(), newPosition.getY()));

        // Catch this so the item still updates!
        try {
            for (AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), item.getPosition().getX(), item.getPosition().getY(), item.getRotation())) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                List<RoomEntity> affectEntities1 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (RoomEntity entity1 : affectEntities1) {
                    item.onEntityStepOff(entity1);
                }
            }

            for (AffectedTile affectedTile : AffectedTile.getAffectedTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), newPosition.getX(), newPosition.getY(), rotation)) {
                tilesToUpdate.add(new Position(affectedTile.x, affectedTile.y));

                List<RoomEntity> affectEntities2 = room.getEntities().getEntitiesAt(new Position(affectedTile.x, affectedTile.y));

                for (RoomEntity entity2 : affectEntities2) {
                    item.onEntityStepOn(entity2);
                }
            }
        } catch (Exception e) {
            log.error("Failed to update entity positions for changing item position", e);
        }

        item.getPosition().setX(newPosition.getX());
        item.getPosition().setY(newPosition.getY());

        item.getPosition().setZ(height);
        item.setRotation(rotation);

        item.needsUpdate(true);

        List<RoomEntity> affectEntities3 = room.getEntities().getEntitiesAt(newPosition);

        for (RoomEntity entity3 : affectEntities3) {
            item.onEntityStepOn(entity3);
        }

        if (save)
            item.save();

        for (Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }

        tilesToUpdate.clear();

        return true;
    }

    private boolean isInvalid(RoomTile tile, Position currentPosition) {
    	if (tile != null) {
            if (currentPosition != null && currentPosition.getX() == tile.getPosition().getX() && currentPosition.getY() == tile.getPosition().getY())
                return true;

            if (!tile.canPlaceItemHere())
                return true;
            
            return false;
    	}
    	
    	return true;
    }

    public boolean verifyItemPosition(FurnitureDefinition item, RoomTile tile, Position currentPosition) {
        return this.verifyItemPosition(item, tile, currentPosition, false);
    }

    public boolean verifyItemPosition(FurnitureDefinition item, RoomTile tile, Position currentPosition, boolean continueRoute) {
        if (tile != null) {
            if (currentPosition != null && currentPosition.getX() == tile.getPosition().getX() && currentPosition.getY() == tile.getPosition().getY())
                return true;

            if (!tile.canPlaceItemHere())
                return false;
           
            if (item.getItemName().startsWith(RoomItemFactory.STACK_TOOL))
                return true;

            if (this.getItemsOnSquare(tile.getPosition()).stream().filter(x -> x instanceof MagicStackFloorItem).collect(Collectors.toList()).size() > 0)
                return true;

            if (!tile.canStack() && tile.getTopItem() != 0 && tile.getTopItem() != item.getId())
                return false;

            if (!item.getInteraction().equals(RoomItemFactory.TELEPORT_PAD) && tile.getPosition().getX() == this.getRoom().getModel().getDoorX() && tile.getPosition().getY() == this.getRoom().getModel().getDoorY())
                return false;

            if (item.getInteraction().equals("dice")) {
                boolean hasOtherDice = false;
                boolean hasStackTool = false;

                for (RoomItemFloor floorItem : tile.getItems()) {
                    if (floorItem instanceof DiceFloorItem) {
                        hasOtherDice = true;
                    }

                    if (floorItem instanceof MagicStackFloorItem) {
                        hasStackTool = true;
                    }
                }

                if (hasOtherDice && hasStackTool)
                    return false;
            }

            if (!CometSettings.roomCanPlaceItemOnEntity) {
                if (tile.getEntities().size() != 0 && !continueRoute) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

    public void placeWallItem(PlayerItem item, String position, Player player) {
        int roomId = this.room.getId();

        RoomItemDao.placeWallItem(roomId, position, item.getExtraData().trim().isEmpty() ? "0" : item.getExtraData(), item.getId());
        player.getInventory().removeWallItem(item.getId());

        RoomItemWall wallItem = this.addWallItem(item.getId(), item.getBaseId(), this.room, player.getId(), position, (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData());

        this.room.getEntities().broadcastMessage(
                new SendWallItemMessageComposer(wallItem)
        );

        wallItem.onPlaced();
    }

    public Room getRoom() {
        return this.room;
    }

    public Map<Long, RoomItemFloor> getFloorItems() {
        return this.floorItems;
    }

    public Map<Long, RoomItemWall> getWallItems() {
        return this.wallItems;
    }

    public void placeFloorItem(PlayerItem item, int x, int y, int rot, Player player) {
        RoomTile tile = room.getMapping().getTile(x, y);

        if (tile == null)
            return;

        double height = tile.getStackHeight(null);

        if (!this.verifyItemPosition(item.getDefinition(), tile, null))
            return;

        if (item.getDefinition().getInteraction().equals("soundmachine")) {
            if (this.soundMachineId > 0) {
                Map<String, String> notificationParams = Maps.newHashMap();

                notificationParams.put("message", Locale.get("game.room.jukeboxExists"));

                player.getSession().send(new NotificationMessageComposer("furni_placement_error", notificationParams));
                return;
            } else {
                this.soundMachineId = item.getId();
            }
        }

        List<RoomItemFloor> floorItems = room.getItems().getItemsOnSquare(x, y);

        if (item.getDefinition() != null && item.getDefinition().getInteraction() != null) {
            if (item.getDefinition().getInteraction().equals("mannequin")) {
                rot = 2;
            }
        }

        RoomItemDao.placeFloorItem(room.getId(), x, y, height, rot, (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData(), item.getId());
        player.getInventory().removeFloorItem(item.getId());

        RoomItemFloor floorItem = room.getItems().addFloorItem(item.getId(), item.getBaseId(), room, player.getId(), item.getGroupId(), x, y, rot, height, (item.getExtraData().isEmpty() || item.getExtraData().equals(" ")) ? "0" : item.getExtraData(), item.getLimitedEditionItem());

        List<Position> tilesToUpdate = new ArrayList<>();

        for (RoomItemFloor stackItem : floorItems) {
            if (item.getId() != stackItem.getId()) {
                stackItem.onItemAddedToStack(floorItem);
            }
        }

        tilesToUpdate.add(new Position(floorItem.getPosition().getX(), floorItem.getPosition().getY(), 0d));

        for (AffectedTile affTile : AffectedTile.getAffectedBothTilesAt(item.getDefinition().getLength(), item.getDefinition().getWidth(), floorItem.getPosition().getX(), floorItem.getPosition().getY(), floorItem.getRotation())) {
            tilesToUpdate.add(new Position(affTile.x, affTile.y, 0d));

            List<RoomEntity> affectEntities0 = room.getEntities().getEntitiesAt(new Position(affTile.x, affTile.y));

            for (RoomEntity entity0 : affectEntities0) {
                floorItem.onEntityStepOn(entity0);
            }
        }

        for (Position tileToUpdate : tilesToUpdate) {
            final RoomTile tileInstance = this.room.getMapping().getTile(tileToUpdate.getX(), tileToUpdate.getY());

            if (tileInstance != null) {
                tileInstance.reload();

                room.getEntities().broadcastMessage(new UpdateStackMapMessageComposer(tileInstance));
            }
        }

        room.getEntities().broadcastMessage(new SendFloorItemMessageComposer(floorItem));


        floorItem.onPlaced();
        floorItem.saveData();
    }

    private void indexItem(RoomItemFloor floorItem) {
        if (!this.itemClassIndex.containsKey(floorItem.getClass())) {
            itemClassIndex.put(floorItem.getClass(), new HashSet<>());
        }

        if (!this.itemInteractionIndex.containsKey(floorItem.getDefinition().getInteraction())) {
            this.itemInteractionIndex.put(floorItem.getDefinition().getInteraction(), new HashSet<>());
        }

        this.itemClassIndex.get(floorItem.getClass()).add(floorItem.getId());
        this.itemInteractionIndex.get(floorItem.getDefinition().getInteraction()).add(floorItem.getId());
    }

    public RoomItemFloor getRandomBanzaiTeleporter(long currentTeleport) {
        List<RoomItemFloor> items = new ArrayList<RoomItemFloor>() {{
            addAll(floorItems.values().stream().filter(x -> x != null && x instanceof BanzaiTeleportFloorItem).collect(Collectors.toList()).stream().filter(floorItem -> floorItem.getId() != currentTeleport).collect(Collectors.toList()));
        }};

        if (items.size() < 1) {
            return null;
        }

        Collections.shuffle(items);

        return items.stream().parallel().findFirst().get();
    }

    public RoomItemFloor getRandomBreedingBox(BreedingType type) {
        List<RoomItemFloor> items = floorItems.values().stream().filter(x -> x != null && x instanceof PetBreedingBoxFloorItem && ((PetBreedingBoxFloorItem)x).getType() == type && ((PetBreedingBoxFloorItem)x).getEntities().size() < 2).collect(Collectors.toList());

        if (items.size() < 1) return null;

        Collections.shuffle(items);

        return items.stream().parallel().findFirst().get();
    }

    public Map<Integer, List<Integer>> getWaterTiles() {
        Map<Integer, List<Integer>> tiles = Maps.newHashMap();
        for (RoomItemFloor floorItem : this.getByClass(WaterFloorItem.class)) {
            for (int i = 0; i < floorItem.getDefinition().getLength(); i++) {
                for (int j = 0; j < floorItem.getDefinition().getWidth(); j++) {
                    if (!tiles.containsKey(floorItem.getPosition().getX() + i)) {
                        tiles.put(floorItem.getPosition().getX() + i, Lists.newArrayList());
                    }

                    tiles.get(floorItem.getPosition().getX() + i).add(floorItem.getPosition().getY() + j);
                }
            }
        }

        return tiles;
    }

    public SoundMachineFloorItem getSoundMachine() {
        if (this.soundMachineId != 0) {
            return ((SoundMachineFloorItem) this.getFloorItem(this.soundMachineId));
        }

        return null;
    }
}
