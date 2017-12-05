package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import gnu.trove.map.hash.THashMap;

import java.util.Map;

public class EventCommand extends Command
{
    public EventCommand()
    {
        super("cmd_event", Emulator.getTexts().getValue("commands.keys.cmd_event").split(";"));
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception
    {
        if(gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
        {
            if (params.length >= 1)
            {
                String message =
                        "<br><b>Opaa! Parece que um novo evento está rolando!</b>" +
                        "<br><br><b>"+ gameClient.getHabbo().getHabboInfo().getUsername() + "</b> está começando um novo evento no quarto "+ gameClient.getHabbo().getHabboInfo().getCurrentRoom().getName() +"!" +
                        "<br><br>Lembrando que você pode ganhar   <b>diamantes</b> e   <b>pontos de evento</b>, para comprar/craftar  <b>Raros</b> ou ficar famoso(a) em nosso   <b>Hall da Fama</b>!" +
                        "<br><br><b>Venha se divertir conosco! :)</b><br><br>";

                THashMap<String, String> codes = new THashMap<String, String>();
                codes.put("ROOMNAME", gameClient.getHabbo().getHabboInfo().getCurrentRoom().getName());
                codes.put("ROOMID", gameClient.getHabbo().getHabboInfo().getCurrentRoom().getId() + "");
                codes.put("USERNAME", gameClient.getHabbo().getHabboInfo().getUsername());
                codes.put("LOOK", gameClient.getHabbo().getHabboInfo().getLook());
                codes.put("TIME", Emulator.getDate().toString());
                codes.put("MESSAGE", message);

                ServerMessage msg = new BubbleAlertComposer("hotel.event", codes).compose();

                for(Map.Entry<Integer, Habbo> set : Emulator.getGameEnvironment().getHabboManager().getOnlineHabbos().entrySet())
                {
                    Habbo habbo = set.getValue();
                    if(habbo.getHabboStats().blockStaffAlerts)
                        continue;

                    habbo.getClient().sendResponse(msg);
                }

                return true;
            }
        }

        return false;
    }
}
