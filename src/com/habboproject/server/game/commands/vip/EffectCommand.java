package com.habboproject.server.game.commands.vip;

import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.effects.EffectManager;
import com.habboproject.server.game.effects.types.EffectItem;
import com.habboproject.server.game.rooms.objects.entities.effects.PlayerEffect;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.types.components.games.GameTeam;
import com.habboproject.server.network.sessions.Session;


public class EffectCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        if (params.length == 0) {
            return;
        }

        try {
            int effectId = Integer.parseInt(params[0]);

            EffectItem effectItem = null;
            if (EffectManager.getInstance().getEffects().containsKey(effectId)) {
                effectItem = EffectManager.getInstance().getEffects().get(effectId);
            }

            if (effectItem != null) {
                if (effectItem.isFurniEffect() && !client.getPlayer().getEffectComponent().hasEffect(effectId)) {
                    EffectCommand.sendNotif("Voc\u00ea n\u00e3o tem permiss\u00e3o para usar este efeito!", client);
                    return;
                }
                if (effectItem.isBuyableEffect() && !client.getPlayer().getEffectComponent().hasEffect(effectId)) {
                    EffectCommand.sendNotif("Voc\u00ea n\u00e3o tem permiss\u00e3o para usar este efeito!", client);
                    return;
                }
                if (effectItem.getMinRank() > client.getPlayer().getPermissions().getRank().getId()) {
                    EffectCommand.sendNotif("Voc\u00ea n\u00e3o tem permiss\u00e3o para usar este efeito!", client);
                    return;
                }
            }

            PlayerEntity entity;
            if ((entity = client.getPlayer().getEntity()).getCurrentEffect() != null) {
                if (entity.getGameTeam() != null && entity.getGameTeam() != GameTeam.NONE) {
                    return;
                }
                if (entity.getCurrentEffect().isItemEffect()) {
                    return;
                }
            }

            entity.applyEffect(new PlayerEffect(effectId, 0));

        } catch (Exception e) {
            sendNotif(Locale.get("command.enable.invalidid"), client);
        }
    }

    @Override
    public String getPermission() {
        return "enable_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.enable.description");
    }

    @Override
    public boolean canDisable() {
        return true;
    }
}
