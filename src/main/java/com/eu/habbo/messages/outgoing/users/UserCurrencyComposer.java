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
        this.response.appendInt32(pointsTypes.length);
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

            this.response.appendInt32(type);
            this.response.appendInt32(this.habbo.getHabboInfo().getCurrencyAmount(type));
        }

        //Size
//        this.response.appendInt32(11);
//
//        this.response.appendInt32(0);
//        this.response.appendInt32(this.habbo.getHabboInfo().getPixels());
//
//        this.response.appendInt32(1);
//        this.response.appendInt32(this.habbo.getHabboInfo().get);
//
//        this.response.appendInt32(2);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(3);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(4);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(5);
//        this.response.appendInt32(0);
//
//
//        this.response.appendInt32(101);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(102);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(103);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(104);
//        this.response.appendInt32(0);
//
//        this.response.appendInt32(105);
//        this.response.appendInt32(this.habbo.getHabboInfo().getPoints());

        return this.response;
    }
}
