package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.CatalogFrontPage;
import com.habboproject.server.game.catalog.types.CatalogItem;
import com.habboproject.server.game.catalog.types.CatalogPage;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;


public class CatalogPageMessageComposer extends MessageComposer {

    private final CatalogPage catalogPage;

    public CatalogPageMessageComposer(final CatalogPage catalogPage) {
        this.catalogPage = catalogPage;
    }

    @Override
    public short getId() {
        return Composers.CatalogPageMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(this.catalogPage.getId());
        msg.writeString("NORMAL");
        msg.writeString(this.catalogPage.getTemplate());

        msg.writeInt(this.catalogPage.getImages().size());
        for (String image : this.catalogPage.getImages()) {
            msg.writeString(image);
        }

        msg.writeInt(this.catalogPage.getTexts().size());
        for (String text : this.catalogPage.getTexts()) {
            msg.writeString(text);
        }

        if (!this.catalogPage.getTemplate().equals("frontpage4") && !this.catalogPage.getTemplate().equals("club_buy")) {
            msg.writeInt(this.catalogPage.getItems().size());
            for (CatalogItem item : this.catalogPage.getItems().values()) {
                item.compose(msg);
            }
        } else {
            msg.writeInt(0);
        }

        msg.writeInt(0);
        msg.writeBoolean(false);

        if (this.catalogPage.getTemplate().equals("frontpage4")) {
            msg.writeInt(CatalogManager.getInstance().getNews().size());
            for (CatalogFrontPage frontPage : CatalogManager.getInstance().getNews().values()) {
                msg.writeInt(frontPage.getId());
                msg.writeString(frontPage.getCaption());
                msg.writeString(frontPage.getImage());
                msg.writeInt(0);
                msg.writeString(frontPage.getPageLink());
                msg.writeInt(frontPage.getPageId());
            }
        }
    }
}
