package com.eu.habbo.habbohotel.items.interactions.games.football;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.games.InteractionGameGate;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserDataComposer;
import com.eu.habbo.messages.outgoing.users.UpdateUserLookComposer;
import com.eu.habbo.plugin.events.users.UserSavedLookEvent;
import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class InteractionFootballGate extends HabboItem
{
    private String figureM = "";
    private String figureF = "";
    private final THashMap<Habbo, String> oldLooks;
    
    public InteractionFootballGate(ResultSet set, Item baseItem) throws SQLException
    {
        super(set, baseItem);
        
        String[] bits = set.getString("extra_data").split(";");
        this.figureM = bits.length > 0 ? bits[0] : "";
        this.figureF = bits.length > 1 ? bits[1] : "";
        this.oldLooks = new THashMap<Habbo, String>();
    }

    public InteractionFootballGate(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells)
    {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        
        String[] bits = extradata.split(";");
        this.figureM = bits.length > 0 ? bits[0] : "";
        this.figureF = bits.length > 1 ? bits[1] : "";
        this.oldLooks = new THashMap<Habbo, String>();
    }
    
    public void setFigureM(String look) {
        figureM = look;
        
        this.setExtradata(this.figureM + ";" + this.figureF);
        this.needsUpdate(true);
        Emulator.getThreading().run(this);
    }
    
    public void setFigureF(String look) {
        figureF = look;
        
        this.setExtradata(this.figureM + ";" + this.figureF);
        this.needsUpdate(true);
        Emulator.getThreading().run(this);
    }
    
    
    @Override
    public void serializeExtradata(ServerMessage serverMessage)
    {
        serverMessage.appendInt32((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.figureM + "," + this.figureF);
        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects)
    {
        return true;
    }

    @Override
    public boolean isWalkable()
    {
        return true;
    }

    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception
    {
        super.onWalkOn(roomUnit, room, objects);
    }

    @Override
    public void onWalkOn(RoomUnit roomUnit, Room room, Object[] objects)  throws Exception
    {
        Habbo habbo = room.getHabbo(roomUnit);
        if(habbo != null) {
            
            if(this.oldLooks.containsKey(habbo)) {
                String oldlook = this.oldLooks.get(habbo);
                
                UserSavedLookEvent lookEvent = new UserSavedLookEvent(habbo, habbo.getHabboInfo().getGender(), oldlook);
                Emulator.getPluginManager().fireEvent(lookEvent);
                if(!lookEvent.isCancelled())
                {
                    habbo.getHabboInfo().setLook(lookEvent.newLook);
                    Emulator.getThreading().run(habbo.getHabboInfo());
                    habbo.getClient().sendResponse(new UpdateUserLookComposer(habbo));
                    room.sendComposer(new RoomUserDataComposer(habbo).compose());
                }
                
                this.oldLooks.remove(habbo);
            }
            else {
                THashMap<String, String> habboBits = this.getFigureBits(habbo.getHabboInfo().getLook());
                THashMap<String, String> gateBits = this.getFigureBits(habbo.getHabboInfo().getGender() == HabboGender.F ? this.figureF : this.figureM);

                String[] allowedHabboBits = new String[] { "hd", "hr", "ha", "he", "ea", "fa" };
                String[] allowedGateBits = new String[] { "ch", "ca", "cc", "cp", "lg", "wa", "sh" };

                String finalLook = "";

                for (Map.Entry<String, String> keys : habboBits.entrySet()) {
                    if(ArrayUtils.contains(allowedHabboBits, keys.getKey())) {
                        finalLook = finalLook + keys.getKey() + "-" + keys.getValue() + ".";
                    }
                }

                for (Map.Entry<String, String> keys : gateBits.entrySet()) {
                    if(ArrayUtils.contains(allowedGateBits, keys.getKey())) {
                        finalLook = finalLook + keys.getKey() + "-" + keys.getValue() + ".";
                    }
                }
                
                if(finalLook.endsWith(".")) {
                    finalLook = finalLook.substring(0, finalLook.length() - 1);
                }
                
                UserSavedLookEvent lookEvent = new UserSavedLookEvent(habbo, habbo.getHabboInfo().getGender(), finalLook);
                Emulator.getPluginManager().fireEvent(lookEvent);
                if(!lookEvent.isCancelled())
                {
                    this.oldLooks.put(habbo, habbo.getHabboInfo().getLook());
                    habbo.getHabboInfo().setLook(lookEvent.newLook);
                    Emulator.getThreading().run(habbo.getHabboInfo());
                    habbo.getClient().sendResponse(new UpdateUserLookComposer(habbo));
                    room.sendComposer(new RoomUserDataComposer(habbo).compose());
                }
            }
        }
        
        super.onWalkOn(roomUnit, room, objects);
    }
    
    private THashMap<String, String> getFigureBits(String looks) {
        THashMap<String, String> bits = new THashMap<String, String>();
        String[] sets = looks.split("\\.");
        
        for(String set : sets)
        {
            String[] setBits = set.split("-", 2);
            bits.put(setBits[0], setBits.length > 1 ? setBits[1] : "");
        }
        
        return bits;
    }
}
