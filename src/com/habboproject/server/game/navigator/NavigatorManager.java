package com.habboproject.server.game.navigator;

import com.habboproject.server.game.navigator.types.Category;
import com.habboproject.server.game.navigator.types.categories.NavigatorCategoryType;
import com.habboproject.server.game.navigator.types.publics.PublicRoom;
import com.habboproject.server.storage.queries.navigator.NavigatorDao;
import com.habboproject.server.utilities.Initializable;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class NavigatorManager implements Initializable {
    private static NavigatorManager navigatorManagerInstance;

    private Map<Integer, Category> categories;
    private List<Category> userCategories;

    private Map<Integer, PublicRoom> publicRooms;
    private Set<Integer> staffPicks;

    Logger log = Logger.getLogger(NavigatorManager.class.getName());

    public NavigatorManager() {
    }

    @Override
    public void initialize() {
        this.loadCategories();
        this.loadPublicRooms();
        this.loadStaffPicks();

        log.info("NavigatorManager initialized");
    }

    public static NavigatorManager getInstance() {
        if (navigatorManagerInstance == null)
            navigatorManagerInstance = new NavigatorManager();

        return navigatorManagerInstance;
    }

    public void loadPublicRooms() {
        try {
            if (this.publicRooms != null && this.publicRooms.size() != 0) {
                this.publicRooms.clear();
            }

            this.publicRooms = NavigatorDao.getPublicRooms();

        } catch (Exception e) {
            log.error("Error while loading public rooms", e);
        }

        log.info("Loaded " + this.publicRooms.size() + " featured rooms");
    }

    public void loadStaffPicks() {
        try {
            if (this.staffPicks != null && this.staffPicks.size() != 0) {
                this.staffPicks.clear();
            }

            this.staffPicks = NavigatorDao.getStaffPicks();

        } catch (Exception e) {
            log.error("Error while loading staff picked rooms", e);
        }

        log.info("Loaded " + this.publicRooms.size() + " staff picks");
    }

    public void loadCategories() {
        try {
            if (this.categories != null && this.categories.size() != 0) {
                this.categories.clear();
            }

            if(this.userCategories == null) {
                this.userCategories = Lists.newArrayList();
            } else {
                this.userCategories.clear();
            }

            this.categories = NavigatorDao.getCategories();

            for(Category category : this.categories.values()) {
                if(category.getCategoryType() == NavigatorCategoryType.CATEGORY) {
                    this.userCategories.add(category);
                }
            }
        } catch (Exception e) {
            log.error("Error while loading navigator categories", e);
        }

        log.info("Loaded " + (this.getCategories() == null ? 0 : this.getCategories().size()) + " room categories");
    }

    public Category getCategory(int id) {
        for (Category c : this.getCategories().values()) {
            if (c.getId() == id) {
                return c;
            }
        }

        return null;
    }

    public boolean isStaffPicked(int roomId) {
        return this.staffPicks.contains(roomId);
    }

    public PublicRoom getPublicRoom(int roomId) {
        return this.publicRooms.get(roomId);
    }

    public Map<Integer, Category> getCategories() {
        return this.categories;
    }

    public Map<Integer, PublicRoom> getPublicRooms() {
        return this.publicRooms;
    }

    public Set<Integer> getStaffPicks() {
        return staffPicks;
    }

    public List<Category> getUserCategories() {
        return userCategories;
    }
}
