package com.eu.habbo.messages.outgoing.helper;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 28-12-2014 23:17.
 */
public class TalentTrackComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.TalentTrackComposer);
        response.appendString("citizenship");
        response.appendInt32(5);
        //{
            response.appendInt32(0);
            response.appendInt32(1);
            response.appendInt32(1);
            //{
            response.appendInt32(125);
            response.appendInt32(1);
            response.appendString("ACH_SafetyQuizGraduate1");
            response.appendInt32(2);
            response.appendInt32(1);
            response.appendInt32(1);

            response.appendInt32(0);
            //{
            //}

            response.appendInt32(1);
            //{
            response.appendString("A1 KUMIANKKA");
            response.appendInt32(0);
            //}
        //}
        //{
            response.appendInt32(1);
            response.appendInt32(1);
            response.appendInt32(4);
            //{
            response.appendInt32(6);
            response.appendInt32(1);
            response.appendString("ACH_AvatarLooks1");
            response.appendInt32(0);
            response.appendInt32(1);
            response.appendInt32(1);
            //}
            //{
            response.appendInt32(18);
            response.appendInt32(0);
            response.appendString("ACH_RespectGiven1");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(2);
            //}
            //{
            response.appendInt32(19);
            response.appendInt32(1);
            response.appendString("ACH_AllTimeHotelPresence1");
            response.appendInt32(0);
            response.appendInt32(30);
            response.appendInt32(30);
            //}
            //{
            response.appendInt32(8);
            response.appendInt32(1);
            response.appendString("ACH_RoomEntry1");
            response.appendInt32(1);
            response.appendInt32(1);
            response.appendInt32(5);
            //}
            response.appendInt32(0);
            //{
            //}

            response.appendInt32(1);
            //{
            response.appendString("A1 KUMIANKKA");
            response.appendInt32(0);
            //}
        //}
        //{
            response.appendInt32(2);
            response.appendInt32(0);
            response.appendInt32(4);
            //{
            response.appendInt32(1);
            response.appendInt32(1);
            response.appendString("ACH_GuideAdvertisementReader1");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(1);
            //}
            //{
            response.appendInt32(11);
            response.appendInt32(1);
            response.appendString("ACH_RegistrationDuration1");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(1);
            //}
            //{
            response.appendInt32(18);
            response.appendInt32(2);
            response.appendString("ACH_AllTimeHotelPresence2");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(60);
            //}
            //{
            response.appendInt32(8);
            response.appendInt32(2);
            response.appendString("ACH_RoomEntry2");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(20);
            //}
            //{
            response.appendInt32(0);
            //{
            //}
            response.appendInt32(1);
            //{
            response.appendString("A1 KUMIANKKA");
            response.appendInt32(0);
            //}
        //}
            response.appendInt32(3);
            response.appendInt32(0);
            response.appendInt32(4);
            //}
            //{
            response.appendInt32(11);
            response.appendInt32(2);
            response.appendString("ACH_RegistrationDuration2");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(3);
            //}
            //{
            response.appendInt32(18);
            response.appendInt32(1);
            response.appendString("ACH_HabboWayGraduate1");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(1);
            //}
            //{
            response.appendInt32(18);
            response.appendInt32(3);
            response.appendString("ACH_AllTimeHotelPresence3");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(120);
            //}
            //{
            response.appendInt32(50621);
            response.appendInt32(1);
            response.appendString("ACH_FriendListSize1");
            response.appendInt32(0);
            response.appendInt32(0);
            response.appendInt32(2);
            //}

            response.appendInt32(1);
            //{
            response.appendString("TRADE");
            //}

            response.appendInt32(1);
            //{
            response.appendString("A1 KUMIANKKA");
            response.appendInt32(0);
            //}
        //}
        //{
            response.appendInt32(4);
            response.appendInt32(0);
            response.appendInt32(0);
            //{
            //}
            response.appendInt32(1);
            //{
            response.appendString("CITIZIN");
            //}
            response.appendInt32(2);
            //{
            response.appendString("A1 KUMIANKKA");
            response.appendInt32(0);
            //}
            //{
            response.appendString("HABBO_CLUB_CITIZENSHIP_VIP_REWARD");
            response.appendInt32(7);
            //}
        //}
        return this.response;
    }
}
