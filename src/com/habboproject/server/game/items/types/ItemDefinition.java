package com.habboproject.server.game.items.types;

import com.habboproject.server.api.game.furniture.types.FurnitureDefinition;

import java.sql.ResultSet;
import java.sql.SQLException;


public class ItemDefinition implements FurnitureDefinition {
    private final int id;
    private final String publicName;
    private final String itemName;
    private final String type;
    private final int width;
    private final int length;
    private final double height;
    private final int spriteId;

    private final boolean canStack;
    private final boolean canSit;
    private final boolean canWalk;
    private final boolean canTrade;
    private final boolean canRecycle;
    private final boolean canMarket;
    private final boolean canGift;
    private final boolean canInventoryStack;

    private final int effectId;
    private final int offerId;
    private final String interaction;
    private final int interactionCycleCount;
    private final String[] vendingIds;
    private final boolean requiresRights;

    private int songId;

    private final Double[] variableHeights;

    public ItemDefinition(ResultSet data) throws SQLException {
        this.id = data.getInt("id");
        this.publicName = data.getString("public_name");
        this.itemName = data.getString("item_name");
        this.type = data.getString("type");
        this.width = data.getInt("width");
        this.length = data.getInt("length");
        final double height = data.getDouble("stack_height");
        this.spriteId = data.getInt("sprite_id");

        this.canStack = data.getString("can_stack").equals("1");
        this.canSit = data.getString("can_sit").equals("1");
        this.canWalk = data.getString("is_walkable").equals("1");
        this.canTrade = data.getString("allow_trade").equals("1");
        this.canInventoryStack = data.getString("allow_inventory_stack").equals("1");

        this.offerId = data.getInt("flat_id");

        this.canRecycle = false;
        this.canMarket = false;
        this.canGift = data.getString("allow_gift").equals("1");

        this.effectId = data.getInt("effect_id");
        this.interaction = data.getString("interaction_type");
        this.interactionCycleCount = data.getInt("interaction_modes_count");
        this.vendingIds = data.getString("vending_ids").isEmpty() ? new String[0] : data.getString("vending_ids").split(",");

        this.requiresRights = data.getString("requires_rights").equals("1");

        this.songId = data.getInt("song_id");

        final String variableHeightData = data.getString("variable_heights");

        if (!variableHeightData.isEmpty() && variableHeightData.contains(",")) {
            String[] variableHeightArray = variableHeightData.split(",");
            this.variableHeights = new Double[variableHeightArray.length];

            for (int i = 0; i < variableHeightArray.length; i++) {
                try {
                    this.variableHeights[i] = Double.parseDouble(variableHeightArray[i]);
                } catch (Exception ignored) {

                }
            }
        } else {
            this.variableHeights = null;
        }

        if (height == 0.0) {
            this.height = 0.001;
        } else {
            this.height = height;
        }
    }

    public boolean isAdFurni() {
        return itemName.equals("ads_mpu_720") || this.itemName.equals("ads_background") || this.itemName.equals("ads_mpu_300") || this.itemName.equals("ads_mpu_160") || this.itemName.equals("backgroundk") || this.interaction.equals("ads_background");
    }

    public boolean isRoomDecor() {
        return itemName.startsWith("wallpaper") || itemName.startsWith("landscape") || itemName.startsWith("a2 ");
    }

    public boolean isTeleporter() {
        return this.getInteraction().equals("teleport") || this.getInteraction().equals("teleport_door") || this.getInteraction().equals("teleport_pad");
    }

    public boolean isSong() {
        return this.songId != 0;
    }

    public int getId() {
        return this.id;
    }

    public String getPublicName() {
        return this.publicName;
    }

    public String getItemName() {
        return this.itemName;
    }

    public String getType() {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public int getSpriteId() {
        return spriteId;
    }

    public int getLength() {
        return length;
    }

    public String getInteraction() {
        return interaction;
    }

    public int getInteractionCycleCount() {
        return this.interactionCycleCount;
    }

    public int getEffectId() {
        return effectId;
    }

    public String[] getVendingIds() {
        return vendingIds;
    }

    public int getOfferId() {
        return offerId;
    }

    public boolean canStack() {
        return canStack;
    }

    public boolean canSit() {
        return canSit;
    }

    public boolean canWalk() {
        return canWalk;
    }

    public boolean canTrade() {
        return canTrade;
    }

    public boolean canRecycle() {
        return canRecycle;
    }

    public boolean canMarket() {
        return canMarket;
    }

    public boolean canGift() {
        return canGift;
    }

    public boolean canInventoryStack() {
        return canInventoryStack;
    }

    public Double[] getVariableHeights() {
        return variableHeights;
    }

    public boolean requiresRights() {
        return requiresRights;
    }

    public int getSongId() {
        return this.songId;
    }
}
