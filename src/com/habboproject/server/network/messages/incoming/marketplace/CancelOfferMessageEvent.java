package com.habboproject.server.network.messages.incoming.marketplace;

import com.habboproject.server.api.game.players.data.components.inventory.PlayerItem;
import com.habboproject.server.game.items.rares.LimitedEditionItemData;
import com.habboproject.server.game.marketplace.MarketplaceManager;
import com.habboproject.server.game.marketplace.types.MarketplaceOfferItem;
import com.habboproject.server.network.messages.incoming.Event;
import com.habboproject.server.network.messages.outgoing.catalog.UnseenItemsMessageComposer;
import com.habboproject.server.network.messages.outgoing.marketplace.CancelOfferMessageComposer;
import com.habboproject.server.network.messages.outgoing.user.inventory.UpdateInventoryMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.protocol.messages.MessageEvent;
import com.habboproject.server.storage.queries.items.ItemDao;
import com.habboproject.server.storage.queries.items.LimitedEditionDao;
import com.habboproject.server.storage.queries.marketplace.MarketplaceDao;

/**
 * Created by brend on 31/01/2017.
 */
public class CancelOfferMessageEvent implements Event {
    @Override
    public void handle(Session client, MessageEvent msg) throws Exception {
        int offerId = msg.readInt();

        MarketplaceOfferItem offer = MarketplaceManager.getInstance().getOfferById(offerId);
        if (offer == null || offer.getPlayerId() != client.getPlayer().getId() || MarketplaceDao.getItemIdByOffer(offerId) <= 0) {
            client.send(new CancelOfferMessageComposer(offerId, false));
            return;
        }

        long itemId = ItemDao.createItem(client.getPlayer().getId(), offer.getDefinition().getId(), "");
        PlayerItem playerItem = client.getPlayer().getInventory().add(itemId, offer.getDefinition().getId(), 0, "", null, (offer.getLimitedNumber() > 0 ? new LimitedEditionItemData(itemId, offer.getLimitedStack(), offer.getLimitedNumber()) : null));
        if (offer.getLimitedStack() > 0) {
            LimitedEditionDao.save(new LimitedEditionItemData(itemId, offer.getLimitedStack(), offer.getLimitedNumber()));
        }

        client.send(new UnseenItemsMessageComposer(playerItem));
        client.send(new UpdateInventoryMessageComposer());

        MarketplaceManager.getInstance().endOffer(offerId);

        client.send(new CancelOfferMessageComposer(offerId, true));
    }
}
