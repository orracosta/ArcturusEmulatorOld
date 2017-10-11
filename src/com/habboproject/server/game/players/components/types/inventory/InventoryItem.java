package com.habboproject.server.game.players.components.types.inventory;

import com.habboproject.server.api.game.furniture.types.GiftItemData;
import com.habboproject.server.api.game.furniture.types.LimitedEditionItem;
import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.types.gifts.GiftData;
import com.habboproject.server.game.groups.GroupManager;
import com.habboproject.server.game.groups.types.GroupData;
import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.utilities.JsonFactory;
import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;


public class InventoryItem implements PlayerItem {
    private long id;
    private int baseId;
    private int groupId;
    private String extraData;
    private GiftData giftData;

    private LimitedEditionItem limitedEditionItem;

    public InventoryItem(ResultSet data) throws SQLException {
        this.id = data.getLong("id");
        this.baseId = data.getInt("base_item");
        this.groupId = data.getInt("group_id");
        this.extraData = data.getString("extra_data");

        try {
            if (this.getDefinition().getInteraction().equals("gift")) {
                this.giftData = JsonFactory.getInstance().fromJson(this.extraData.split("GIFT::##")[1], GiftData.class);
            }
        } catch (Exception e) {
            this.giftData = null;
        }

        if (data.getInt("limited_id") != 0) {
            this.limitedEditionItem = new LimitedEditionItemData(this.id, data.getInt("limited_id"), data.getInt("limited_total"));
        }
    }

    public InventoryItem(long id, int baseId, int groupId, String extraData, GiftItemData giftData, LimitedEditionItem limitEditionItem) {
        this.init(id, baseId, groupId, extraData, (GiftData) giftData);

        this.limitedEditionItem = limitEditionItem;
    }

    public InventoryItem(long id, int baseId, int groupId, String extraData) {
        this.init(id, baseId, groupId, extraData, null);
    }

    private void init(long id, int baseId, int groupId, String extraData, GiftData giftData) {
        this.id = id;
        this.baseId = baseId;
        this.groupId = groupId;
        this.extraData = extraData;
        this.giftData = giftData;
    }

    public void compose(IComposer msg) {
        final boolean isGift = this.getGiftData() != null;
        final boolean isGroupItem = this.getDefinition().getInteraction().equals("group_item") || this.getDefinition().getInteraction().equals("group_gate");
        final boolean isLimited = this.getLimitedEditionItem() != null;
        final boolean isWired = this.getDefinition().getInteraction().startsWith("wf_act") || this.getDefinition().getInteraction().startsWith("wf_cnd") || this.getDefinition().getInteraction().startsWith("wf_trg");

        msg.writeInt(ItemManager.getInstance().getItemVirtualId(this.getId()));
        msg.writeString(this.getDefinition().getType().toUpperCase());
        msg.writeInt(ItemManager.getInstance().getItemVirtualId(this.getId()));
        msg.writeInt(isGift ? this.getGiftData().getSpriteId() : this.getDefinition().getSpriteId());

        if (!isGroupItem)
            msg.writeInt(1);

        if (isGroupItem) {
            // Append the group data...
            int groupId = 0;

            msg.writeInt(17);

            if (StringUtils.isNumeric(this.getExtraData())) {
                groupId = Integer.parseInt(this.getExtraData());
            }

            GroupData groupData = groupId == 0 ? null : GroupManager.getInstance().getData(groupId);

            if (groupData == null) {
                msg.writeInt(2);
                msg.writeInt(0);
            } else {
                msg.writeInt(2);
                msg.writeInt(5);
                msg.writeString("0"); //state
                msg.writeString(groupId);
                msg.writeString(groupData.getBadge());

                String colourA = GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()) != null ? GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()).getColour() : "ffffff";
                String colourB = GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()) != null ? GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()).getColour() : "ffffff";

                msg.writeString(colourA);
                msg.writeString(colourB);
            }
        } else if (isLimited && !isGift) {
            msg.writeString("");
            msg.writeBoolean(true);
            msg.writeBoolean(false);
        } else if (this.getDefinition().getInteraction().equals("badge_display") && !isGift) {
            msg.writeInt(2);
        } else {
            msg.writeInt(0);
        }

        if (this.getDefinition().getInteraction().equals("badge_display") && !isGift) {
            msg.writeInt(4);

            String badge;
            String name = "";
            String date = "";

            if(this.getExtraData().contains("~")) {
                String[] data = this.getExtraData().split("~");

                badge = data[0];
                name = data[1];
                date = data[2];
            } else {
                badge = this.getExtraData();
            }

            msg.writeString("0");
            msg.writeString(badge);
            msg.writeString(name); // creator
            msg.writeString(date); // date
        } else if (!isGroupItem) {
            msg.writeString(!isGift && !isWired ? this.getExtraData() : "");
        }

        if (isLimited && !isGift) {
            LimitedEditionItem limitedEditionItem = this.getLimitedEditionItem();

            msg.writeInt(limitedEditionItem.getLimitedRare());
            msg.writeInt(limitedEditionItem.getLimitedRareTotal());
        }

        msg.writeBoolean(this.getDefinition().canRecycle());
        msg.writeBoolean(!isGift && this.getDefinition().canTrade());
        msg.writeBoolean(!isLimited && !isGift && this.getDefinition().canInventoryStack());
        msg.writeBoolean(!isGift && isLimited);

        msg.writeInt(-1);
        msg.writeBoolean(true);//??
        msg.writeInt(-1);
        msg.writeString("");
        msg.writeInt(isGift ? this.getGiftData().getWrappingPaper() * 1000 + this.getGiftData().getDecorationType() : 0);
    }

    public void serializeTrade(IComposer msg) {
        final boolean isGift = this.getGiftData() != null;
        final boolean isGroupItem = this.getDefinition().getInteraction().equals("group_item") || this.getDefinition().getInteraction().equals("group_gate");
        final boolean isLimited = this.getLimitedEditionItem() != null;
        final boolean isWired = this.getDefinition().getInteraction().startsWith("wf_act") || this.getDefinition().getInteraction().startsWith("wf_cnd") || this.getDefinition().getInteraction().startsWith("wf_trg");

        msg.writeInt(ItemManager.getInstance().getItemVirtualId(this.id));
        msg.writeString(this.getDefinition().getType().toLowerCase());
        msg.writeInt(ItemManager.getInstance().getItemVirtualId(this.id));
        msg.writeInt(this.getDefinition().getSpriteId());
        msg.writeInt(0);
        msg.writeBoolean(true);

        if (isGroupItem) {
            // Append the group data...
            int groupId = 0;

//            msg.writeInt(17);

            if (StringUtils.isNumeric(this.getExtraData())) {
                groupId = Integer.parseInt(this.getExtraData());
            }

            GroupData groupData = groupId == 0 ? null : GroupManager.getInstance().getData(groupId);

            if (groupData == null) {
                msg.writeInt(0);
            } else {
                msg.writeInt(2);
                msg.writeInt(5);
                msg.writeString("0"); //state
                msg.writeString(groupId);
                msg.writeString(groupData.getBadge());

                String colourA = GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()) != null ? GroupManager.getInstance().getGroupItems().getSymbolColours().get(groupData.getColourA()).getColour() : "ffffff";
                String colourB = GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()) != null ? GroupManager.getInstance().getGroupItems().getBackgroundColours().get(groupData.getColourB()).getColour() : "ffffff";

                msg.writeString(colourA);
                msg.writeString(colourB);
            }
        } else if (isLimited) {
            msg.writeString("");
            msg.writeBoolean(true);
            msg.writeBoolean(false);
        } else if (this.getDefinition().getInteraction().equals("badge_display") && !isGift) {
            msg.writeInt(2);
        } else {
            msg.writeInt(0);
        }

        if (this.getDefinition().getInteraction().equals("badge_display") && !isGift) {
            msg.writeInt(4);

            msg.writeString("0");
            msg.writeString(this.getExtraData());
            msg.writeString(""); // creator
            msg.writeString(""); // date
        } else if (!isGroupItem) {
            msg.writeString(!isGift && !isWired ? this.getExtraData() : "");
        }

        if (isLimited && !isGift) {
            LimitedEditionItem limitedEditionItem = this.getLimitedEditionItem();

            msg.writeInt(limitedEditionItem.getLimitedRare());
            msg.writeInt(limitedEditionItem.getLimitedRareTotal());
        }

        msg.writeInt(0);
        msg.writeInt(0);
        msg.writeInt(0);

        if (this.getDefinition().getType().equals("s")) {
            msg.writeInt(0);
        }
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public ItemDefinition getDefinition() {
        return ItemManager.getInstance().getDefinition(this.getBaseId());
    }

    @Override
    public int getBaseId() {
        return this.baseId;
    }

    @Override
    public String getExtraData() {
        return this.extraData;
    }

    public GiftData getGiftData() {
        return giftData;
    }

    @Override
    public LimitedEditionItem getLimitedEditionItem() {
        return limitedEditionItem;
    }

    public InventoryItemSnapshot createSnapshot() {
        return new InventoryItemSnapshot(this.id, this.baseId, this.extraData);
    }

    @Override
    public int getVirtualId() {
        return ItemManager.getInstance().getItemVirtualId(this.getId());
    }

    @Override
    public int getGroupId() {
        return groupId;
    }
}
