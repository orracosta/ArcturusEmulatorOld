package com.habboproject.server.game.navigator.types;

import com.habboproject.server.api.game.rooms.RoomCategory;
import com.habboproject.server.game.navigator.types.categories.NavigatorCategoryType;
import com.habboproject.server.game.navigator.types.categories.NavigatorSearchAllowance;
import com.habboproject.server.game.navigator.types.categories.NavigatorViewMode;

public class Category implements RoomCategory {
    private final int id;
    private final String category;
    private final String categoryId;
    private final String publicName;
    private final boolean canDoActions;
    private final int colour;
    private final int requiredRank;
    private final NavigatorViewMode viewMode;
    private final NavigatorCategoryType categoryType;
    private final NavigatorSearchAllowance searchAllowance;
    private final int orderId;
    private final boolean visible;
    private final int roomCount;
    private final int roomCountExpanded;

    public Category(int id, String category, String categoryId, String publicName, boolean canDoActions, int colour, int requiredRank, NavigatorViewMode viewMode, String categoryType, String searchAllowance, int orderId, boolean visible, int roomCount, int roomCountExpanded) {
        this.id = id;
        this.category = category;
        this.categoryId = categoryId;
        this.publicName = publicName;
        this.canDoActions = canDoActions;
        this.colour = colour;
        this.requiredRank = requiredRank;
        this.viewMode = viewMode;
        this.categoryType = NavigatorCategoryType.valueOf(categoryType.toUpperCase());
        this.searchAllowance = NavigatorSearchAllowance.valueOf(searchAllowance.toUpperCase());
        this.orderId = orderId;
        this.visible = visible;
        this.roomCount = roomCount;
        this.roomCountExpanded = roomCountExpanded;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getPublicName() {
        return publicName;
    }

    public boolean canDoActions() {
        return canDoActions;
    }

    public int getColour() {
        return colour;
    }

    public int getRequiredRank() {
        return requiredRank;
    }

    public NavigatorViewMode getViewMode() {
        return viewMode;
    }

    public NavigatorCategoryType getCategoryType() {
        return categoryType;
    }

    public NavigatorSearchAllowance getSearchAllowance() {
        return searchAllowance;
    }

    public int getOrderId() {
        return orderId;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public int getRoomCountExpanded() {
        return roomCountExpanded;
    }
}
