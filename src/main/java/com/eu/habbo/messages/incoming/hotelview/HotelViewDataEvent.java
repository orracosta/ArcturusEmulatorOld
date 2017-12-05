package com.eu.habbo.messages.incoming.hotelview;

import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.hotelview.HotelViewDataComposer;

public class HotelViewDataEvent extends MessageHandler {

    @Override
    public void handle()
    {

        /*String[] data = hotelViewData.split(",");

        if(data.length > 1)
        {
            this.client.sendResponse(new HotelViewDataComposer(hotelViewData, data[data.length - 1].replace(";", "")));
            System.out.println("Hotelview Data: " + hotelViewData);
        }
        else
        {
            System.out.println("Hotelview Data error: " + hotelViewData);
            this.client.sendResponse(new HotelViewDataComposer(hotelViewData, ""));
        }*/
        /*String hotelViewData = this.packet.readString();

        String[] types = hotelViewData.split(";");

        for(String s : types)
        {
            if(!s.contains(","))
                continue;

            String[] data = s.split(",");
            this.client.sendResponse(new HotelViewDataComposer(hotelViewData, data[data.length - 1]));
        }*/

        try
        {
            String data = this.packet.readString();
            if (data.contains(";"))
            {
                String[] d = data.split(";");

                for (String s : d)
                {
                    if (s.contains(","))
                    {
                        this.client.sendResponse(new HotelViewDataComposer(s, s.split(",")[s.split(",").length - 1]));
                    } else
                    {
                        this.client.sendResponse(new HotelViewDataComposer(data, s));
                    }

                    break;
                }

                //this.client.sendResponse(new HotelViewDataComposer("2013-05-08 13:0", "gamesmaker"));
            } else
            {
                this.client.sendResponse(new HotelViewDataComposer(data, data.split(",")[data.split(",").length - 1]));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
