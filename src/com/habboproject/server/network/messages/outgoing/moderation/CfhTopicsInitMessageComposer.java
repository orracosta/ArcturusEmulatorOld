package com.habboproject.server.network.messages.outgoing.moderation;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.network.messages.composers.MessageComposer;
import com.habboproject.server.protocol.headers.Composers;

public class CfhTopicsInitMessageComposer extends MessageComposer {
    @Override
    public short getId() {
        return Composers.CfhTopicsInitMessageComposer;
    }

    @Override
    public void compose(IComposer msg) {
        msg.writeInt(6);
        msg.writeString("sex_and_pii");
        msg.writeInt(8);
        msg.writeString("sexual_webcam_images");
        msg.writeInt(1);
        msg.writeString("mods");
        msg.writeString("sexual_webcam_images_auto");
        msg.writeInt(2);
        msg.writeString("mods");
        msg.writeString("explicit_sexual_talk");
        msg.writeInt(3);
        msg.writeString("mods");
        msg.writeString("cybersex");
        msg.writeInt(4);
        msg.writeString("mods");
        msg.writeString("cybersex_auto");
        msg.writeInt(5);
        msg.writeString("mods");
        msg.writeString("meet_some");
        msg.writeInt(6);
        msg.writeString("mods");
        msg.writeString("meet_irl");
        msg.writeInt(7);
        msg.writeString("mods");
        msg.writeString("email_or_phone");
        msg.writeInt(8);
        msg.writeString("mods");
        msg.writeString("scamming");
        msg.writeInt(3);
        msg.writeString("stealing");
        msg.writeInt(9);
        msg.writeString("mods");
        msg.writeString("scamsites");
        msg.writeInt(10);
        msg.writeString("mods");
        msg.writeString("selling_buying_accounts_or_furni");
        msg.writeInt(11);
        msg.writeString("mods");
        msg.writeString("trolling");
        msg.writeInt(11);
        msg.writeString("hate_speech");
        msg.writeInt(12);
        msg.writeString("mods");
        msg.writeString("violent_roleplay");
        msg.writeInt(13);
        msg.writeString("mods");
        msg.writeString("swearing");
        msg.writeInt(14);
        msg.writeString("auto_reply");
        msg.writeString("drugs");
        msg.writeInt(15);
        msg.writeString("mods");
        msg.writeString("gambling");
        msg.writeInt(16);
        msg.writeString("mods");
        msg.writeString("self_threatening");
        msg.writeInt(17);
        msg.writeString("mods");
        msg.writeString("mild_staff_impersonation");
        msg.writeInt(18);
        msg.writeString("mods");
        msg.writeString("severe_staff_impersonation");
        msg.writeInt(19);
        msg.writeString("mods");
        msg.writeString("habbo_name");
        msg.writeInt(20);
        msg.writeString("mods");
        msg.writeString("minors_access");
        msg.writeInt(21);
        msg.writeString("mods");
        msg.writeString("bullying");
        msg.writeInt(22);
        msg.writeString("guardians");
        msg.writeString("interruption");
        msg.writeInt(2);
        msg.writeString("flooding");
        msg.writeInt(23);
        msg.writeString("mods");
        msg.writeString("doors");
        msg.writeInt(24);
        msg.writeString("mods");
        msg.writeString("room");
        msg.writeInt(1);
        msg.writeString("room_report");
        msg.writeInt(25);
        msg.writeString("mods");
        msg.writeString("help");
        msg.writeInt(2);
        msg.writeString("help_habbo");
        msg.writeInt(26);
        msg.writeString("auto_reply");
        msg.writeString("help_payments");
        msg.writeInt(27);
        msg.writeString("auto_reply");
    }
}
