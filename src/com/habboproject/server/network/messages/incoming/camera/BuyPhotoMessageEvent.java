package com.habboproject.server.network.messages.incoming.camera;

import com.google.common.collect.Sets;
import com.habboproject.server.boot.Comet;
import com.habboproject.server.boot.CometServer;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.achievements.types.AchievementType;
import com.habboproject.server.game.players.components.types.inventory.InventoryItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.camera.PhotoPurchaseMessageComposer;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.notification.AlertMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.purse.UpdateActivityPointsMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.camera.CameraDao;
import com.habboproject.server.storage.queries.items.ItemDao;
import com.habboproject.server.utilities.FilterUtil;

/**
 * Created by brend on 03/03/2017.
 */
public class BuyPhotoMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        if (client.getPlayer() == null || client.getPlayer().getEntity() == null || client.getPlayer().getEntity().getRoom() == null)
            return;

        int lastPhotoId = 0;
        if ((lastPhotoId = client.getPlayer().getEntity().getLastPhoto()) == 0)
            return;

        int photoCostCredits = 0;
        if ((photoCostCredits = Comet.getServer().getConfig().getInt("comet.camera.price.credits")) > 0) {
            if (!CometSettings.playerInfiniteBalance  && photoCostCredits > client.getPlayer().getData().getCredits()) {
                client.send(new AlertMessageComposer(Locale.get("catalog.error.notenough")));
                return;
            }
        }

        int photoCostDuckets = 0;
        if ((photoCostDuckets = Comet.getServer().getConfig().getInt("comet.camera.price.duckets")) > 0) {
            if (!CometSettings.playerInfiniteBalance  && photoCostDuckets > client.getPlayer().getData().getActivityPoints()) {
                client.send(new AlertMessageComposer(Locale.get("catalog.error.notenough")));
                return;
            }
        }

        if (photoCostCredits > 0) {
            client.getPlayer().getData().decreaseCredits(photoCostCredits);
            client.getPlayer().sendBalance();
        }

        if (photoCostDuckets > 0) {
            client.getPlayer().getData().decreaseActivityPoints(photoCostDuckets);
            client.send(new UpdateActivityPointsMessageComposer(client.getPlayer().getData().getActivityPoints(), photoCostDuckets));
        }

        String fileName = CameraDao.buyPhoto(lastPhotoId);
        if (fileName == null)
            return;

        CameraDao.updateState(lastPhotoId);

        String itemExtraData = "{\"t\":" + System.currentTimeMillis() + ",\"u\":\"" + lastPhotoId + "\",\"n\":\"" + FilterUtil.escapeJSONString(client.getPlayer().getData().getUsername()) + "\",\"m\":\"\",\"s\":" + client.getPlayer().getId() + ",\"w\":\"" + FilterUtil.escapeJSONString(CometSettings.cameraPhotoUrl.replace("{0}", fileName)) + "\"}";

        long itemId = ItemDao.createItem(client.getPlayer().getId(), CometSettings.cameraPhotoFurnitureId, itemExtraData);

        InventoryItem playerItem = new InventoryItem(itemId, CometSettings.cameraPhotoFurnitureId, 0, itemExtraData);

        client.getPlayer().getInventory().addItem(playerItem);

        client.send(new UpdateInventoryMessageComposer());
        client.send(new UnseenItemsMessageComposer(Sets.newHashSet(playerItem)));

        client.send(new PhotoPurchaseMessageComposer());

        client.getPlayer().getAchievements().progressAchievement(AchievementType.CAMERA_PHOTO, 1);
    }
}
