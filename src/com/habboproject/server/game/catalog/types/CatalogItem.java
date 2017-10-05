package com.habboproject.server.game.catalog.types;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CatalogItem {
    /**
     * The ID of the catalog item
     */
    private int id;

    /**
     * The ID of the item definition
     */
    private String itemId;

    /**
     * The name of item which will be displayed in the catalog
     */
    private String displayName;

    /**
     * The coin cost of the item
     */
    private int costCredits;

    /**
     * The duckets cost of the item
     */
    private int costActivityPoints;

    /**
     * The seasonal currency cost of the items (usually diamonds)
     */
    private int costOther;

    /**
     * The amount of items you get if you purchase this
     */
    private int amount;

    /**
     * Is this item only available to VIP members?
     */
    private boolean vip;

    /**
     * The items (if this is a bundle)
     */
    private List<CatalogBundledItem> items;

    /**
     * If this item is limited edition, how many items are available
     */
    private int limitedTotal;

    /**
     * If this item is limited edition, how many items have been sold
     */
    private int limitedSells;

    /**
     * Allow this item to be sold
     */
    private boolean allowOffer;

    /**
     * Badge ID that's bundled with this item (if any)
     */
    private String badgeId;

    /**
     * Item extra-data presets (once purchased, this preset will be applied to the item)
     */
    private String presetData;

    /**
     * The catalog page ID
     */
    private int pageId;

    private boolean canAdd = true;

    /**
 * Initialize the catalog item with data from the database
     *
     * @param data Data from the database
     * @throws SQLException
     */
    public CatalogItem(ResultSet data) throws Exception {
        this(
                data.getInt("id"),
                data.getString("item_ids"),
                data.getString("catalog_name"),
                data.getInt("cost_credits"),
                data.getInt("cost_pixels"),
                data.getInt("cost_snow"),
                data.getInt("amount"),
                data.getString("vip").equals("1"),
                data.getInt("limited_stack"),
                data.getInt("limited_sells"),
                data.getString("offer_active").equals("1"),
                data.getString("badge_id"),
                data.getString("extradata"),
                data.getInt("page_id"));
    }

    public CatalogItem(int id, String itemId, String displayName, int costCredits, int costActivityPoints, int costOther, int amount, boolean vip, int limitedTotal, int limitedSells, boolean allowOffer, String badgeId, String presetData, int pageId) {
        this(id, itemId, null, displayName, costCredits, costActivityPoints, costOther, amount, vip, limitedTotal, limitedSells, allowOffer, badgeId, presetData, pageId);
    }

    public CatalogItem(int id, String itemId, List<CatalogBundledItem> bundledItems, String displayName, int costCredits, int costActivityPoints, int costOther, int amount, boolean vip, int limitedTotal, int limitedSells, boolean allowOffer, String badgeId, String presetData, int pageId) {
        this.id = id;
        this.itemId = itemId;
        this.displayName = displayName;
        this.costCredits = costCredits;
        this.costActivityPoints = costActivityPoints;
        this.costOther = costOther;
        this.amount = amount;
        this.vip = vip;
        this.limitedTotal = limitedTotal;
        this.limitedSells = limitedSells;
        this.allowOffer = allowOffer;
        this.badgeId = badgeId;
        this.presetData = presetData;
        this.pageId = pageId;

        this.items = bundledItems != null ? bundledItems : new ArrayList<>();

        if (items.size() == 0) {
            if (!this.itemId.equals("-1")) {
                if (bundledItems != null) {
                    items = bundledItems;
                } else {

                    if (itemId.contains(",")) {
                        String[] split = itemId.replace("\n", "").split(",");

                        for (String str : split) {
                            if (!str.equals("")) {
                                String[] parts = str.split(":");
                                if (parts.length != 3) continue;

                                try {
                                    final int aItemId = Integer.parseInt(parts[0]);
                                    final int aAmount = Integer.parseInt(parts[1]);
                                    final String aPresetData = parts[2];

                                    this.items.add(new CatalogBundledItem(aPresetData, aAmount, aItemId));
                                } catch (Exception ignored) {
                                    Comet.getServer().getLogger().warn("Invalid item data for catalog item: " + this.id);
                                }
                            }
                        }
                    } else {
                        if (ItemManager.getInstance().getDefinition(Integer.valueOf(this.itemId)) == null) {
                            this.canAdd = false;
                        }

                        this.items.add(new CatalogBundledItem(this.presetData, this.amount, Integer.valueOf(this.itemId)));
                    }
                }
            }

            if (this.getItems().size() == 0) return;

            List<CatalogBundledItem> itemsToRemove = new ArrayList<>();

            for (CatalogBundledItem catalogBundledItem : this.items) {
                final ItemDefinition itemDefinition = ItemManager.getInstance().getDefinition(catalogBundledItem.getItemId());

                if (itemDefinition == null) {
                    itemsToRemove.add(catalogBundledItem);
                }
            }

            for(CatalogBundledItem itemToRemove : itemsToRemove) {
                this.items.remove(itemToRemove);
            }

            itemsToRemove.clear();

            if(this.items.size() == 0) {
                return;
            }

            if (ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId()) == null) return;
            int offerId = ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId()).getOfferId();

            if (!CatalogManager.getCatalogOffers().containsKey(offerId)) {
                CatalogManager.getCatalogOffers().put(offerId, new CatalogOffer(offerId, this.getPageId(), this.getId()));
            }
        }
    }

    public void compose(IComposer msg) {
        ItemDefinition firstItem = this.itemId.equals("-1") ? null : ItemManager.getInstance().getDefinition(this.getItems().get(0).getItemId());

        msg.writeInt(this.getId());
        msg.writeString(this.getDisplayName());
        msg.writeBoolean(false);

        msg.writeInt(this.getCostCredits());

        if (this.getCostOther() > 0) {
            msg.writeInt(this.getCostOther());
            msg.writeInt(105);
        } else if (this.getCostActivityPoints() > 0) {
            msg.writeInt(this.getCostActivityPoints());
            msg.writeInt(0);
        } else {
            msg.writeInt(0);
            msg.writeInt(0);
        }

        msg.writeBoolean(firstItem != null && firstItem.canGift());

        if (!this.hasBadge()) {
            msg.writeInt(this.getItems().size());
        } else {
            msg.writeInt(this.isBadgeOnly() ? 1 : 2);
            msg.writeString("b");
            msg.writeString(this.getBadgeId());
        }

        for (CatalogBundledItem bundledItem : this.getItems()) {
            ItemDefinition def = ItemManager.getInstance().getDefinition(bundledItem.getItemId());
            msg.writeString(def.getType());

            if (def.getType().equals("b")) {
                msg.writeString(def.getItemName());
                continue;
            }

            msg.writeInt(def.getSpriteId());

            if (this.getDisplayName().contains("wallpaper_single") || this.getDisplayName().contains("floor_single") || this.getDisplayName().contains("landscape_single")) {
                msg.writeString(this.getDisplayName().split("_")[2]);
            } else if (def.isSong()) {
                msg.writeString(def.getSongId());
            } else {
                msg.writeString(bundledItem.getPresetData());
            }

            msg.writeInt(bundledItem.getAmount());
            msg.writeBoolean(this.getLimitedTotal() != 0);

            if (this.getLimitedTotal() <= 0) continue;

            msg.writeInt(this.getLimitedTotal());
            msg.writeInt(this.getLimitedTotal() - this.getLimitedSells());
        }

        msg.writeInt(0);
        msg.writeBoolean(this.getLimitedTotal() <= 0 && this.allowOffer());

        msg.writeBoolean(false);
        msg.writeString("");
    }

    public int getId() {
        return this.id;
    }

    public String getItemId() {
        return itemId;
    }

    public List<CatalogBundledItem> getItems() {
        return this.items;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCostCredits() {
        return costCredits;
    }

    public int getCostActivityPoints() {
        return costActivityPoints;
    }

    public int getCostOther() {
        return costOther;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isVip() {
        return vip;
    }

    public int getLimitedTotal() {
        return this.limitedTotal;
    }

    public int getLimitedSells() {
        return this.limitedSells;
    }

    public boolean allowOffer() {
        return this.allowOffer;
    }

    public void increaseLimitedSells(int amount) {
        this.limitedSells += amount;
    }

    public boolean hasBadge() {
        return !(this.badgeId.isEmpty());
    }

    public boolean isBadgeOnly() {
        return this.items.size() == 0 && this.hasBadge();
    }

    public String getBadgeId() {
        return this.badgeId;
    }

    public String getPresetData() {
        return presetData;
    }

    public int getPageId() {
        return pageId;
    }

    public boolean canAdd() {
        return canAdd;
    }
}
