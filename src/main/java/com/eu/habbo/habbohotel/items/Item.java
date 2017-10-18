package com.eu.habbo.habbohotel.items;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.interactions.InteractionMultiHeight;
import com.eu.habbo.habbohotel.items.interactions.InteractionVendingMachine;
import com.eu.habbo.habbohotel.users.HabboItem;
import gnu.trove.list.array.TIntArrayList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Item {

    private int id;
    private int spriteId;
    private String name;
    private String fullName;
    private FurnitureType type;
    private short width;
    private short length;
    private double height;
    private boolean allowStack;
    private boolean allowWalk;
    private boolean allowSit;
    private boolean allowLay;
    private boolean allowRecyle;
    private boolean allowTrade;
    private boolean allowMarketplace;
    private boolean allowGift;
    private boolean allowInventoryStack;
    private short stateCount;
    private short effectM;
    private short effectF;
    private TIntArrayList vendingItems;
    private double[] multiHeights;

    private ItemInteraction interactionType;

    public Item(ResultSet set) throws SQLException
    {
        this.load(set);
    }

    public void update(ResultSet set) throws SQLException
    {
        this.load(set);
    }

    private void load(ResultSet set) throws SQLException
    {
        this.id = set.getInt("id");
        this.spriteId = set.getInt("sprite_id");
        this.name = set.getString("item_name");
        this.fullName = set.getString("public_name");
        this.type = FurnitureType.fromString(set.getString("type"));
        this.width = set.getShort("width");
        this.length = set.getShort("length");
        this.height = set.getDouble("stack_height");
        this.allowStack = set.getBoolean("allow_stack");
        this.allowWalk = set.getBoolean("allow_walk");
        this.allowSit = set.getBoolean("allow_sit");
        this.allowLay = set.getBoolean("allow_lay");
        this.allowRecyle = set.getBoolean("allow_recycle");
        this.allowTrade = set.getBoolean("allow_trade");
        this.allowMarketplace = set.getBoolean("allow_marketplace_sell");
        this.allowGift = set.getBoolean("allow_gift");
        this.allowInventoryStack = set.getBoolean("allow_inventory_stack");

        this.interactionType = Emulator.getGameEnvironment().getItemManager().getItemInteraction(set.getString("interaction_type").toLowerCase());

        this.stateCount = set.getShort("interaction_modes_count");
        this.effectM = set.getShort("effect_id_male");
        this.effectF = set.getShort("effect_id_female");

        if(!set.getString("vending_ids").isEmpty())
        {
            this.vendingItems = new TIntArrayList();
            String[] vendingIds = set.getString("vending_ids").replace(";", ",").split(",");
            for (String s : vendingIds)
            {
                this.vendingItems.add(Integer.valueOf(s.replace(" ", "")));
            }
        }

        if(this.interactionType.getType() == InteractionMultiHeight.class)
        {
            if(set.getString("multiheight").contains(";"))
            {
                String[] s = set.getString("multiheight").split(";");
                this.multiHeights = new double[s.length];

                for(int i = 0; i < s.length; i++)
                {
                    this.multiHeights[i] = Double.parseDouble(s[i]);
                }
            }
            else
            {
                this.multiHeights = new double[0];
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public int getSpriteId() {
        return this.spriteId;
    }

    public String getName() {
        return this.name;
    }

    public String getFullName()
    {
        return this.fullName;
    }

    public FurnitureType getType()
    {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public int getLength() {
        return this.length;
    }

    public double getHeight() {
        return this.height;
    }

    public boolean allowStack() {
        return this.allowStack;
    }

    public boolean allowWalk() {
        return this.allowWalk;
    }

    public boolean allowSit() {
        return this.allowSit;
    }

    public boolean allowLay() {
        return this.allowLay;
    }

    public boolean allowRecyle() {
        return this.allowRecyle;
    }

    public boolean allowTrade() {
        return this.allowTrade;
    }

    public boolean allowMarketplace() {
        return this.allowMarketplace;
    }

    public boolean allowGift() {
        return this.allowGift;
    }

    public boolean allowInventoryStack() {
        return this.allowInventoryStack;
    }

    public int getStateCount() {
        return this.stateCount;
    }

    public int getEffectM() {
        return this.effectM;
    }

    public int getEffectF() {
        return this.effectF;
    }

    public ItemInteraction getInteractionType()
    {
        return this.interactionType;
    }

    public TIntArrayList getVendingItems()
    {
        return this.vendingItems;
    }

    public int getRandomVendingItem()
    {
        return this.vendingItems.get(Emulator.getRandom().nextInt(this.vendingItems.size()));
    }

    public double[] getMultiHeights()
    {
        return this.multiHeights;
    }

    public static boolean isPet(Item item)
    {
        return item.getName().toLowerCase().startsWith("a0 pet");
    }

    public static double getCurrentHeight(HabboItem item)
    {
        if(item instanceof InteractionMultiHeight && item.getBaseItem().getMultiHeights().length > 0)
        {
            if (item.getExtradata().isEmpty())
            {
                item.setExtradata("0");
            }

            try
            {
                int index = Integer.valueOf(item.getExtradata()) % (item.getBaseItem().getMultiHeights().length);
                return item.getBaseItem().getMultiHeights()[(item.getExtradata().isEmpty() ? 0 : index)];
            }
            catch (Exception e)
            {

            }
        }

        return item.getBaseItem().getHeight();
    }
}
