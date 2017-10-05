package com.habboproject.server.game.navigator.types.search;

import com.habboproject.server.api.game.rooms.settings.RoomAccessType;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.navigator.NavigatorManager;
import com.habboproject.server.game.navigator.types.Category;
import com.habboproject.server.game.navigator.types.publics.PublicRoom;
import com.habboproject.server.game.players.components.types.messenger.MessengerFriend;
import com.habboproject.server.game.players.types.Player;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.game.rooms.types.RoomData;
import com.habboproject.server.game.rooms.types.RoomPromotion;
import com.habboproject.server.network.messages.outgoing.navigator.NavigatorSearchResultSetMessageComposer;
import com.habboproject.server.threads.CometThread;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NavigatorSearchService implements CometThread {
    private static NavigatorSearchService searchServiceInstance;

    private Executor searchExecutor = Executors.newFixedThreadPool(2);

    public NavigatorSearchService() {
//        ThreadManager.getInstance().executePeriodic(this, 0, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        // TODO: Cache navigator search results.
    }

    public void submitRequest(Player player, String category, String data) {
        this.searchExecutor.execute(() -> {
            if(data.isEmpty()) {
                // send categories.
                List<Category> categoryList = Lists.newArrayList();

                for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                    if(navigatorCategory.getCategory().equals(category)) {
                        if(navigatorCategory.isVisible())
                            categoryList.add(navigatorCategory);
                    }
                }

                if(categoryList.size() == 0) {
                    for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if(navigatorCategory.getCategoryType().toString().toLowerCase().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                if(categoryList.size() == 0) {
                    for(Category navigatorCategory : NavigatorManager.getInstance().getCategories().values()) {
                        if(navigatorCategory.getCategoryId().equals(category) && navigatorCategory.isVisible()) {
                            categoryList.add(navigatorCategory);
                        }
                    }
                }

                player.getSession().send(new NavigatorSearchResultSetMessageComposer(category, data, categoryList, player));
            } else {
                player.getSession().send(new NavigatorSearchResultSetMessageComposer("hotel_view", data, null, player));
            }
        });
    }

    public List<RoomData> search(Category category, Player player, boolean expanded) {
        List<RoomData> rooms = Lists.newCopyOnWriteArrayList();

        switch (category.getCategoryType()) {
            case MY_ROOMS:
                if(player.getRooms() == null) {
                    break;
                }

                for (Integer roomId : new LinkedList<>(player.getRooms())) {
                    if (RoomManager.getInstance().getRoomData(roomId) == null) continue;

                    rooms.add(RoomManager.getInstance().getRoomData(roomId));
                }
                break;

            case POPULAR:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(-1), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case CATEGORY:
                rooms.addAll(order(RoomManager.getInstance().getRoomsByCategory(category.getId()), expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                break;

            case TOP_PROMOTIONS:
                List<RoomData> promotedRooms = Lists.newArrayList();

                for (RoomPromotion roomPromotion : RoomManager.getInstance().getRoomPromotions().values()) {
                    if (roomPromotion != null) {
                        RoomData roomData = RoomManager.getInstance().getRoomData(roomPromotion.getRoomId());

                        if (roomData != null) {
                            promotedRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(promotedRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                promotedRooms.clear();
                break;

            case PUBLIC:
                for (PublicRoom publicRoom : NavigatorManager.getInstance().getPublicRooms().values()) {
                    RoomData roomData = RoomManager.getInstance().getRoomData(publicRoom.getRoomId());

                    if (roomData != null) {
                        rooms.add(roomData);
                    }
                }
                break;

            case STAFF_PICKS:
                List<RoomData> staffPicks = Lists.newArrayList();

                for (int roomId : NavigatorManager.getInstance().getStaffPicks()) {
                    RoomData roomData = RoomManager.getInstance().getRoomData(roomId);

                    if (roomData != null) {
                        staffPicks.add(roomData);
                    }
                }

                rooms.addAll(order(staffPicks, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                staffPicks.clear();
                break;

            case MY_GROUPS:
                List<RoomData> groupHomeRooms = Lists.newArrayList();

                for(int groupId : player.getGroups()) {
                    GroupData groupData = GroupManager.getInstance().getData(groupId);

                    if(groupData != null) {
                        RoomData roomData = RoomManager.getInstance().getRoomData(groupData.getRoomId());

                        if(roomData != null) {
                            groupHomeRooms.add(roomData);
                        }
                    }
                }

                rooms.addAll(order(groupHomeRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                groupHomeRooms.clear();
                break;

            case MY_FRIENDS_ROOMS:
                List<RoomData> friendsRooms = Lists.newArrayList();

                for(MessengerFriend messengerFriend : player.getMessenger().getFriends().values()) {
                    if(messengerFriend.isInRoom()) {
                        PlayerEntity playerEntity = messengerFriend.getSession().getPlayer().getEntity();

                        if(playerEntity != null) {
                            if(!friendsRooms.contains(playerEntity.getRoom().getData())) {
                                friendsRooms.add(playerEntity.getRoom().getData());
                            }
                        }
                    }
                }

                rooms.addAll(order(friendsRooms, expanded ? category.getRoomCountExpanded() : category.getRoomCount()));
                friendsRooms.clear();
                break;
        }

        List<RoomData> resultRooms = new ArrayList<RoomData>() {{
            addAll(rooms);
        }};

        try {
            for (RoomData data : resultRooms) {
                Room room = RoomManager.getInstance().get(data.getId());
                if (room == null || (!room.getRights().hasRights(player.getId()) && data.getAccess() == RoomAccessType.INVISIBLE)) {
                    rooms.remove(data);
                }
            }
        } catch (Exception e) {

        }

        return rooms;
    }

    public static List<RoomData> order(List<RoomData> rooms, int limit) {
        try {
            Collections.sort(rooms, (room1, room2) -> {
                boolean is1Active = RoomManager.getInstance().isActive(room1.getId());
                boolean is2Active = RoomManager.getInstance().isActive(room2.getId());

                return ((!is2Active ? 0 : RoomManager.getInstance().get(room2.getId()).getEntities().playerCount()) -
                        (!is1Active ? 0 : RoomManager.getInstance().get(room1.getId()).getEntities().playerCount()));
            });
        } catch (Exception ignored) {

        }

        List<RoomData> returnRooms = new LinkedList<>();

        for (RoomData roomData : rooms) {
            if (returnRooms.size() >= limit) {
                break;
            }

            returnRooms.add(roomData);
        }

        return returnRooms;
    }

    public static NavigatorSearchService getInstance() {
        if (searchServiceInstance == null) {
            searchServiceInstance = new NavigatorSearchService();
        }

        return searchServiceInstance;
    }
}
