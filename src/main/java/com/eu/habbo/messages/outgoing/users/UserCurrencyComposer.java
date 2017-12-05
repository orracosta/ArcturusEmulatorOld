package com.eu.habbo.messages.outgoing.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

public class UserCurrencyComposer extends MessageComposer
{

    private final Habbo habbo;

    public UserCurrencyComposer(Habbo habbo)
    {
        this.habbo = habbo;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.UserCurrencyComposer);

        String[] pointsTypes = Emulator.getConfig().getValue("seasonal.types").split(";");
        this.response.appendInt(pointsTypes.length);
        for(String s : pointsTypes)
        {
            int type = 0;
            try
            {
                type = Integer.valueOf(s);
            }
            catch (Exception e){
                Emulator.getLogging().logErrorLine(e);
                return null;
            }

            this.response.appendInt(type);
            this.response.appendInt(this.habbo.getHabboInfo().getCurrencyAmount(type));
        }

        //Size
//        this.response.appendInt(11);
//
//        this.response.appendInt(0);
//        this.response.appendInt(this.habbo.getHabboInfo().getPixels());
//
//        this.response.appendInt(1);
//        this.response.appendInt(this.habbo.getHabboInfo().get);
//
//        this.response.appendInt(2);
//        this.response.appendInt(0);
//
//        this.response.appendInt(3);
//        this.response.appendInt(0);
//
//        this.response.appendInt(4);
//        this.response.appendInt(0);
//
//        this.response.appendInt(5);
//        this.response.appendInt(0);
//
//
//        this.response.appendInt(101);
//        this.response.appendInt(0);
//
//        this.response.appendInt(102);
//        this.response.appendInt(0);
//
//        this.response.appendInt(103);
//        this.response.appendInt(0);
//
//        this.response.appendInt(104);
//        this.response.appendInt(0);
//
//        this.response.appendInt(105);
//        this.response.appendInt(this.habbo.getHabboInfo().getPoints());

        return this.response;
    }
}
