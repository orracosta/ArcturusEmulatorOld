package com.eu.habbo.messages.incoming.navigator;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.navigation.*;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomCategory;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.navigator.NewNavigatorSearchResultsComposer;

import java.util.*;

public class RequestNewNavigatorRoomsEvent extends MessageHandler
{
    @Override
    public void handle() throws Exception
    {
//        String searchCode = this.packet.readString();
//        String text = this.packet.readString();
//
//        List<Room> rooms = new ArrayList<Room>();
//        String key = "popular";
//
//        if(searchCode.equalsIgnoreCase("official_view"))
//        {
//            rooms = Emulator.getGameEnvironment().getRoomManager().getPublicRooms();
//            key = "official-root";
//        }
//        else if(searchCode.equalsIgnoreCase("hotel_view"))
//        {
//            rooms = Emulator.getGameEnvironment().getRoomManager().getActiveRooms();
//            key = "popular";
//        }
//        else if(searchCode.equalsIgnoreCase("roomads_view"))
//        {
//            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsPromoted();
//            key = "top_promotions";
//        }
//        else if(searchCode.equalsIgnoreCase("myworld_view"))
//        {
//            rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo());
//            key = "my";
//        }
//        else
//        {
//        }
//
//        if(text.contains(":"))
//        {
//            String filterType = text.split(":")[0];
//            String filterData = "";
//            int index = 0;
//            for(String s : text.split(":"))
//            {
//                if(index == 0)
//                {
//                    ++index;
//                    continue;
//                }
//
//                filterData+= s;
//            }
//
//            if(filterType.equalsIgnoreCase("owner"))
//            {
//                if(searchCode.equalsIgnoreCase("hotel_view") || searchCode.equalsIgnoreCase("myworld_view"))
//                {
//                    rooms = Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(filterData);
//                }
//
//                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByOwner(rooms, filterData);
//            }
//            else if(filterType.equalsIgnoreCase("tag"))
//            {
//                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByTag(rooms, filterData);
//            }
//            else if(filterType.equalsIgnoreCase("group"))
//            {
//                rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByGroup(rooms, filterData);
//            }
//            else
//            {
//            }
//        }
//        else
//        {
//            rooms = Emulator.getGameEnvironment().getRoomManager().filterRoomsByNameAndDescription(rooms, text);
//        }
//
//        Collections.sort(rooms);
//        List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
//        resultLists.add(new SearchResultList(key, text, SearchAction.MORE, ListMode.LIST, false, rooms));
//        this.client.sendResponse(new NewNavigatorSearchResultsComposer(searchCode, text, resultLists));

//        String view = this.packet.readString();
//
//        if (view.isEmpty())
//        {
//            view = "official_view";
//        }
//
//        String query = this.packet.readString();
//        String part = query;
//
//        NavigatorFilter filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get(view);
//
//        if (filter != null)
//        {
//            List<Room> rooms = filter.getRooms();
//
//            Method field = null;
//
//            if (query.contains(":"))
//            {
//                String[] parts = query.split(":");
//
//                String filterField = "";
//                if (parts.length > 1)
//                {
//                    filterField = parts[0];
//                    part = parts[1];
//                }
//                else
//                {
//                    filterField = parts[0].replace(":", "");
//                    part = "";
//                }
//
//                field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField).getKey();
//            }
//
//            if (field != null)
//            {
//                rooms = filter.filterBy(field, part, rooms);
//            }
//            else
//            {
//                List<Room> newRooms = new ArrayList<Room>();
//
//                for (Map.Entry<Method, NavigatorFilterComparator> set : Emulator.getGameEnvironment().getNavigatorManager().filterSettings.values())
//                {
//                    for (Room room : filter.filterBy(set.getKey(), part, rooms))
//                    {
//                        if (!newRooms.contains(room))
//                        {
//                            newRooms.add(room);
//                        }
//                    }
//                }
//
//                rooms = newRooms;
//            }
//
//            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, filter.getResult(rooms, part)));
//        }

        String view = this.packet.readString();
        String query = this.packet.readString();

        NavigatorFilter filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get(view);
        RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption(view);

        String filterField = "anything";
        String part = query;
        NavigatorFilterField field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
        if (filter != null)
        {
            if (query.contains(":"))
            {
                String[] parts = query.split(":");

                if (parts.length > 1)
                {
                    filterField = parts[0];
                    part = parts[1];
                } else
                {
                    filterField = parts[0].replace(":", "");
                    if (!Emulator.getGameEnvironment().getNavigatorManager().filterSettings.containsKey(filterField))
                    {
                        filterField = "anything";
                    }
                }
            }

            if (Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField) != null)
            {
                field = Emulator.getGameEnvironment().getNavigatorManager().filterSettings.get(filterField);
            }
        }

        if (field == null || query.isEmpty())
        {
            if (filter == null)
                return;

            List<SearchResultList> resultLists = filter.getResult(this.client.getHabbo());
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
            return;
        }

        if (filter == null && category != null)
        {
            filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get("hotel_view");
        }

        if (filter == null)
            return;

        try
        {
            List<SearchResultList> resultLists = new ArrayList<>();

            resultLists.addAll(filter.getResult(this.client.getHabbo(), field, part, category != null ? category.getId() : -1));

            if (resultLists == null)
            {
                resultLists = new ArrayList<>();
            }

//            if (query.isEmpty())
//            {
//                resultLists.add(new SearchResultList(1, view, view, SearchAction.BACK, ListMode.LIST, DisplayMode.VISIBLE, Emulator.getGameEnvironment().getRoomManager().getPopularRooms(10), true, false));
//            }
            filter.filter(field.field, part, resultLists);

            Collections.sort(resultLists);
            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
        }
//        }
//        else
//        {
//            RoomCategory category = Emulator.getGameEnvironment().getRoomManager().getCategoryBySafeCaption(view);
//
//            List<SearchResultList> resultLists = new ArrayList<SearchResultList>();
//            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
//
//            if (category != null)
//            {
//                resultLists.add(new SearchResultList(1, view, query, SearchAction.BACK, ListMode.LIST, DisplayMode.VISIBLE, Emulator.getGameEnvironment().getRoomManager()., true, false));
//            }
//
//            filter = Emulator.getGameEnvironment().getNavigatorManager().filters.get("hotel_view");
//
//            filter(resultLists, filter, query);
//
//            this.client.sendResponse(new NewNavigatorSearchResultsComposer(view, query, resultLists));
//        }
    }

    private void filter(List<SearchResultList> resultLists, NavigatorFilter filter, String part)
    {
        List<SearchResultList> toRemove = new ArrayList<SearchResultList>();
        Map<Integer, HashMap<Integer, Room>> filteredRooms = new HashMap<Integer, HashMap<Integer, Room>>();

        for (NavigatorFilterField field : Emulator.getGameEnvironment().getNavigatorManager().filterSettings.values())
        {
            for (SearchResultList result : resultLists)
            {
                if (result.filter)
                {
                    List<Room> rooms = new ArrayList<Room>();
                    rooms.addAll(result.rooms.subList(0, result.rooms.size()));
                    filter.filterRooms(field.field, part, rooms);

                    if (!filteredRooms.containsKey(result.order))
                    {
                        filteredRooms.put(result.order, new HashMap<Integer, Room>());
                    }

                    for (Room room : rooms)
                    {
                        filteredRooms.get(result.order).put(room.getId(), room);
                    }
                }
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, Room>> set : filteredRooms.entrySet())
        {
            for (SearchResultList resultList : resultLists)
            {
                if (resultList.filter)
                {
                    resultList.rooms.clear();
                    resultList.rooms.addAll(set.getValue().values());

                    if (resultList.rooms.isEmpty())
                    {
                        toRemove.add(resultList);
                    }
                }
            }
        }

        resultLists.removeAll(toRemove);
    }
}
