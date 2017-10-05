package com.habboproject.server.game.marketplace.types;

import com.habboproject.server.game.items.ItemManager;
import com.habboproject.server.game.items.types.ItemDefinition;
import com.habboproject.server.storage.queries.marketplace.MarketplaceDao;
import com.habboproject.server.threads.CometThread;
import com.habboproject.server.threads.ThreadManager;

/**
 * Created by brend on 01/02/2017.
 */
public class MarketplaceOfferItem implements CometThread {
    private final int offerId;
    private final int itemId;
    private final ItemDefinition definition;
    private final int playerId;
    private final int type;
    private final int limitedStack;
    private final int limitedNumber;
    private int state;
    private int price;
    private int finalPrice;
    private int time;

    private boolean needsUpdate;

    public MarketplaceOfferItem(int offerId, int itemId, int playerId, int type, int limitedStack, int limitedNumber, int state, int price, int finalPrice, int time) {
        this.offerId = offerId;
        this.itemId = itemId;
        this.definition = ItemManager.getInstance().getDefinition(itemId);
        this.playerId = playerId;
        this.type = type;
        this.limitedStack = limitedStack;
        this.limitedNumber = limitedNumber;
        this.state = state;
        this.price = price;
        this.finalPrice = finalPrice;
        this.time = time;

        this.needsUpdate = false;
    }

    public void stateUpdate(int state) {
        this.needsUpdate = true;
        this.state = state;

        ThreadManager.getInstance().execute(this);
    }

    public void run() {
        if (needsUpdate) {
            needsUpdate = false;
            MarketplaceDao.updateOffer(this);
        }
    }

    public int getOfferId() {
        return offerId;
    }

    public int getItemId() {
        return itemId;
    }

    public ItemDefinition getDefinition() {
        return definition;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getType() {
        return type;
    }

    public int getLimitedStack() {
        return limitedStack;
    }

    public int getLimitedNumber() {
        return limitedNumber;
    }

    public int getPrice() {
        return price;
    }

    public int getFinalPrice() {
        return finalPrice;
    }

    public int getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public void needsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }
}
