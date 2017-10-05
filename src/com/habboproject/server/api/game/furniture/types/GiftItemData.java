package com.habboproject.server.api.game.furniture.types;

public interface GiftItemData {
    int getPageId();

    int getItemId();

    String getReceiver();

    String getMessage();

    int getSpriteId();

    int getWrappingPaper();

    int getDecorationType();

    boolean isShowUsername();

    int getSenderId();

    String getExtraData();

    void setExtraData(String extraData);
}
