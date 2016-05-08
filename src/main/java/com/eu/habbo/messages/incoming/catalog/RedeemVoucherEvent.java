package com.eu.habbo.messages.incoming.catalog;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.catalog.RedeemVoucherErrorComposer;

/**
 * Created on 30-8-2014 13:40.
 */
public class RedeemVoucherEvent extends MessageHandler {

    @Override
    public void handle() throws Exception {
        String voucherCode = this.packet.readString();

        if(voucherCode.contains(" "))
        {
            this.client.sendResponse(new RedeemVoucherErrorComposer(RedeemVoucherErrorComposer.TECHNICAL_ERROR));
            return;
        }

        Emulator.getGameEnvironment().getCatalogManager().redeemVoucher(this.client, voucherCode);
    }
}