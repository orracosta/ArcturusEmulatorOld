package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogPage;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.CatalogPageComposer;

public class RequestCatalogPageEvent extends MessageHandler
{

    @Override
    public void handle() throws Exception
    {
        int catalogPageId = this.packet.readInt();
        int unknown = this.packet.readInt();
        String mode = this.packet.readString();

        CatalogPage page = Emulator.getGameEnvironment().getCatalogManager().catalogPages.get(catalogPageId);

        if (catalogPageId > 0 && page != null)
        {
            if (page.getRank() <= this.client.getHabbo().getHabboInfo().getRank().getId() && page.isEnabled())
            {
                this.client.sendResponse(new CatalogPageComposer(page, this.client.getHabbo(), mode));
            }
            else
            {
                if(!page.isVisible())
                {
                    Emulator.getGameEnvironment().getModToolManager().quickTicket(this.client.getHabbo(), "Scripter", Emulator.getTexts().getValue("scripter.warning.catalog.page").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%pagename%", page.getCaption()));
                }
            }
        }
    }
}
