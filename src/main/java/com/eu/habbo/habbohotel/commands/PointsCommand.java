package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertKeys;
import gnu.trove.map.hash.THashMap;

public class PointsCommand extends Command {
    public PointsCommand() {
        super("cmd_points", Emulator.getTexts().getValue("commands.keys.cmd_points").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);

        if (habbo != null) {
            int type = Emulator.getConfig().getInt("seasonal.primary.type");

            String tipo = params[2];
            String alertstr = "";

            int amount = 0;

            switch (tipo) {
                case "promo":
                    amount = Integer.valueOf(Emulator.getConfig().getInt("cmd.points.amount.promo"));
                    alertstr = "promoção";
                    break;
                case "evento":
                    amount = Integer.valueOf(Emulator.getConfig().getInt("cmd.points.amount.event"));
                    alertstr = "evento";
                    break;
                default:
                    break;
            }

            habbo.givePoints(type, amount);

            THashMap<String, String> keys = new THashMap<String, String>();
            keys.put("display", "BUBBLE");
            keys.put("image", "${image.library.url}notifications/diamonds.png");
            keys.put("message", Emulator.getTexts().getValue("commands.generic.cmd_points.received").replace("%type%", alertstr));

            habbo.getClient().sendResponse(new BubbleAlertComposer(BubbleAlertKeys.RECEIVED_BADGE.key, keys));
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.succes.cmd_points.send").replace("%amount%", amount + "").replace("%user%", habbo.getHabboInfo().getUsername()).replace("%type%", alertstr), RoomChatMessageBubbles.ALERT);

            THashMap<String, String> keysWin = new THashMap<String, String>();
            keysWin.put("display", "BUBBLE");
            keysWin.put("image", "https://www.mania.gg/api/head/" + habbo.getHabboInfo().getUsername() + ".png");
            keysWin.put("message", Emulator.getTexts().getValue("commands.generic.cmd_points.winner").replace("%user%", habbo.getHabboInfo().getUsername()).replace("%type%", alertstr));

            Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BubbleAlertComposer(BubbleAlertKeys.RECEIVED_BADGE.key, keysWin));

            if (tipo.equals("promo")) {
                habbo.getClient().updatePoints(tipo);
            } else {
                habbo.getClient().updatePoints(tipo);
            }
        } else {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("commands.error.cmd_points.user_not_found").replace("%user%", params[1]), RoomChatMessageBubbles.ALERT);

        }

        return true;
    }
}
