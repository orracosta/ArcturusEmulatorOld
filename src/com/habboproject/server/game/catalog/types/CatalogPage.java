package com.habboproject.server.game.catalog.types;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.rooms.bundles.RoomBundleManager;
import com.habboproject.server.game.rooms.bundles.types.RoomBundle;
import com.habboproject.server.game.rooms.bundles.types.RoomBundleItem;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CatalogPage {
    private static final Type listType = new TypeToken<List<String>>() {}.getType();

    private int id;
    private CatalogPageType type;
    private String caption;
    private int icon;
    private int minRank;
    private String template;
    private int parentId;
    private String linkName;

    private boolean enabled;

    private List<String> images;
    private List<String> texts;

    private Map<Integer, CatalogItem> items;
    private String extraData;

    public CatalogPage(ResultSet data, Map<Integer, CatalogItem> items) throws SQLException {

        this.id = data.getInt("id");
        this.caption = data.getString("caption");
        this.icon = data.getInt("icon_image");
        this.minRank = data.getInt("min_rank");
        this.template = data.getString("page_layout");
        this.parentId = data.getInt("parent_id");
        this.linkName = "";
        this.type = CatalogPageType.valueOf(data.getString("type"));
        this.extraData = data.getString("extra_data");

        if (data.getString("page_images") == null || data.getString("page_images").isEmpty()) {
            this.images = new ArrayList<>();
        } else {
            this.images = JsonFactory.getInstance().fromJson(data.getString("page_images"), listType);
        }

        if (data.getString("page_texts") == null || data.getString("page_texts").isEmpty()) {
            this.texts = new ArrayList<>();
        } else {
            this.texts = JsonFactory.getInstance().fromJson(data.getString("page_texts"), listType);
        }

        this.enabled = data.getString("enabled").equals("1");

        if (this.type == CatalogPageType.BUNDLE) {
            String bundleAlias = this.extraData;

            RoomBundle roomBundle = RoomBundleManager.getInstance().getBundle(bundleAlias);

            if (roomBundle != null) {
                List<CatalogBundledItem> bundledItems = new ArrayList<>();
                Map<Integer, List<RoomBundleItem>> bundleItems = new HashMap<>();

                for (RoomBundleItem bundleItem : roomBundle.getRoomBundleData()) {
                    if (bundleItems.containsKey(bundleItem.getItemId())) {
                        bundleItems.get(bundleItem.getItemId()).add(bundleItem);
                    } else {
                        bundleItems.put(bundleItem.getItemId(), Lists.newArrayList(bundleItem));
                    }
                }

                for (Map.Entry<Integer, List<RoomBundleItem>> bundledItem : bundleItems.entrySet()) {
                    bundledItems.add(new CatalogBundledItem("0", bundledItem.getValue().size(), bundledItem.getKey()));
                }

                final CatalogItem catalogItem = new CatalogItem(roomBundle.getId(), "-1", bundledItems, "single_bundle",
                        roomBundle.getCostCredits(), roomBundle.getCostSeasonal(), roomBundle.getCostVip(), 1, false, 0, 0, false, "", "", this.id);

                this.items = new HashMap<>();
                this.items.put(catalogItem.getId(), catalogItem);
            } else {
                this.items = new HashMap<>();
            }
        } else {
            this.items = items;
        }
    }

    public int getOfferSize() {
        int size = 0;

        for (CatalogItem item : this.items.values()) {
            if(item.getItemId().equals("-1")) continue;

            if (ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()) != null) {
                if (ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()).getOfferId() != -1 && ItemManager.getInstance().getDefinition(item.getItems().get(0).getItemId()).getOfferId() != 0) {
                    size++;
                }
            }
        }

        return size;
    }

    public int getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public int getIcon() {
        return icon;
    }

    public int getMinRank() {
        return minRank;
    }

    public String getTemplate() {
        return template;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<Integer, CatalogItem> getItems() {
        return items;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getTexts() {
        return texts;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getExtraData() {
        return extraData;
    }

    public CatalogPageType getType() {
        return type;
    }
}
