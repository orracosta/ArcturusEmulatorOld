package com.habboproject.server.network.messages.outgoing.catalog;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.catalog.CatalogManager;
import com.habboproject.server.game.catalog.types.CatalogPage;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class CatalogIndexMessageComposer extends MessageComposer {

    private final int playerRank;

    public CatalogIndexMessageComposer(int playerRank) {
        this.playerRank = playerRank;
    }

    @Override
    public short getId() {
        return Composers.CatalogIndexMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        List<CatalogPage> pages = CatalogManager.getInstance().getPagesForRank(this.playerRank);
        List<CatalogPage> pagesTwo = CatalogManager.getInstance().getPagesForRank(this.playerRank);
        List<CatalogPage> subPages = CatalogManager.getInstance().getPagesForRank(this.playerRank);

        Collections.sort(subPages, new Comparator<CatalogPage>(){
            @Override
            public int compare(CatalogPage o1, CatalogPage o2) {
                return o1.getCaption().compareTo(o2.getCaption());
            }
        });

        msg.writeBoolean(true);
        msg.writeInt(0);
        msg.writeInt(-1);
        msg.writeString("root");
        msg.writeString("");
        msg.writeInt(0);

        msg.writeInt(this.count(-1, pages));
        for (CatalogPage page : pages.stream().filter(x -> x.getParentId() == -1).collect(Collectors.toList())) {
            if (page.getParentId() != -1)
                continue;

            msg.writeBoolean(true);
            msg.writeInt(page.getIcon());
            msg.writeInt(page.isEnabled() ? page.getId() : -1);
            msg.writeString(!page.getLinkName().isEmpty() ? page.getLinkName() : page.getCaption().toLowerCase().replace(" ", "_"));
            msg.writeString(page.getCaption());
            msg.writeInt(0);

            msg.writeInt(this.count(page.getId(), pages));
            for (CatalogPage child : pagesTwo.stream().filter(x -> x.getParentId() == page.getId()).collect(Collectors.toList())) {
                if (child.getParentId() != page.getId())
                    continue;

                msg.writeBoolean(true);
                msg.writeInt(child.getIcon());
                msg.writeInt(child.isEnabled() ? child.getId() : -1);
                msg.writeString(child.getLinkName().equals("undefined") ? child.getCaption().toLowerCase().replaceAll("[^A-Za-z0-9]", "").replace(" ", "_") : child.getLinkName());
                msg.writeString(child.getCaption());
                msg.writeInt(0);

                msg.writeInt(this.count(child.getId(), pagesTwo));
                for (CatalogPage childTwo : subPages.stream().filter(x -> x.getParentId() == child.getId()).collect(Collectors.toList())) {
                    if (childTwo.getParentId() != child.getId())
                        continue;

                    msg.writeBoolean(true);
                    msg.writeInt(childTwo.getIcon());
                    msg.writeInt(childTwo.isEnabled() ? childTwo.getId() : -1);
                    msg.writeString(childTwo.getLinkName().equals("undefined") ? childTwo.getCaption().toLowerCase().replaceAll("[^A-Za-z0-9]", "").replace(" ", "_") : childTwo.getLinkName());
                    msg.writeString(childTwo.getCaption());
                    msg.writeInt(0);

                    msg.writeInt(0);
                }
            }
        }

        msg.writeBoolean(true);
        msg.writeString("NORMAL");
    }

    private int count(int index, List<CatalogPage> pages) {
        int i = 0;

        for (CatalogPage page : pages) {
            if (page.getParentId() == index) {
                i++;
            }
        }

        return i;
    }
}
