package com.habboproject.server.api.game.furniture.types;

public interface FurnitureDefinition {

   boolean isAdFurni();

   boolean isRoomDecor();

   boolean isTeleporter();

   boolean isSong();

   int getId();

   String getPublicName();

   String getItemName();

   String getType();

   int getWidth();

   double getHeight();

   int getSpriteId();

   int getLength();

   String getInteraction();

   int getInteractionCycleCount();

   int getEffectId();

   String[] getVendingIds();

   int getOfferId();

   boolean canStack();

   boolean canSit();

   boolean canWalk();

   boolean canTrade();

   boolean canRecycle();

   boolean canMarket();

   boolean canGift();

   boolean canInventoryStack();

   Double[] getVariableHeights();

   boolean requiresRights();

   int getSongId();
}
