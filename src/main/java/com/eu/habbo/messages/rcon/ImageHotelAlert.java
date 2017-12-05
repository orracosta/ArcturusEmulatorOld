package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.messages.outgoing.generic.alerts.BubbleAlertComposer;
import com.google.gson.Gson;
import gnu.trove.map.hash.THashMap;

public class ImageHotelAlert extends RCONMessage<ImageHotelAlert.JSON>
{
    public ImageHotelAlert()
    {
        super(ImageHotelAlert.JSON.class);
    }

    @Override
    public void handle(Gson gson, JSON json)
    {
        THashMap<String, String> keys = new THashMap<>();
        keys.put("message", json.message);
        keys.put("linkUrl", json.url);
        keys.put("linkTitle", json.url_messaege);
        keys.put("title", json.title);
        Emulator.getGameServer().getGameClientManager().sendBroadcastResponse(new BubbleAlertComposer(json.bubble_key, keys).compose());
    }

    public class JSON
    {
        public String bubble_key;
        public String message;
        public String url;
        public String url_messaege;
        public String title;
    }
}