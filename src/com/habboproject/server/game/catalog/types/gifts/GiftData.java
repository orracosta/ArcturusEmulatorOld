package com.habboproject.server.game.catalog.types.gifts;

import com.habboproject.server.api.game.furniture.types.GiftItemData;
import com.habboproject.server.utilities.JsonData;

public class GiftData implements JsonData, GiftItemData {
    /**
     * The page ID of the item
     */
    private int pageId;

    /**
     * The item ID of the item
     */
    private int itemId;

    /**
     * The ID of the player who sent the item
     */
    private int senderId;

    /**
     * The username of the player who will receive the item
     */
    private String receiver;

    /**
     * The message shown in the gift
     */
    private String message;

    /**
     * The ID of the item in furnidata
     */
    private int spriteId;

    /**
     * The wrapping paper
     */
    private int wrappingPaper;

    /**
     * The box decoration type
     */
    private int decorationType;

    /**
     * Do you want to show the username in the gift?
     */
    private boolean showUsername;

    /**
     * The data supplied when purchasing the gift
     */
    private String extraData;

    /**
     * Initialize the gift data
     *
     * @param pageId         The page ID of the item
     * @param itemId         The item ID
     * @param senderId       The ID of the player who sent the item
     * @param receiver       The name of the user will recieve the gift
     * @param message        The message that will appear in the gift
     * @param spriteId       The ID of the item in furnidata
     * @param wrappingPaper
     * @param decorationType
     * @param showUsername
     * @param extraData      The data supplied when purchasing the gift
     */
    public GiftData(int pageId, int itemId, int senderId, String receiver, String message, int spriteId, int wrappingPaper, int decorationType, boolean showUsername, String extraData) {
        this.pageId = pageId;
        this.itemId = itemId;
        this.senderId = senderId;
        this.receiver = receiver;
        this.message = message;
        this.spriteId = spriteId;
        this.wrappingPaper = wrappingPaper;
        this.decorationType = decorationType;
        this.showUsername = showUsername;
        this.extraData = extraData;
    }

    public GiftData() {
        this.pageId = 0;
        this.itemId = 0;
        this.senderId = 0;
        this.receiver = "";
        this.message = "";
        this.spriteId = 0;
        this.wrappingPaper = 0;
        this.decorationType = 0;
        this.showUsername = false;
        this.extraData = "0";
    }

    public int getPageId() {
        return pageId;
    }

    public int getItemId() {
        return itemId;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public int getSpriteId() {
        return spriteId;
    }

    public int getWrappingPaper() {
        return wrappingPaper;
    }

    public int getDecorationType() {
        return decorationType;
    }

    public boolean isShowUsername() {
        return showUsername;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
}
