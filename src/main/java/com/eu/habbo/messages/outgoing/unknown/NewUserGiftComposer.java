package com.eu.habbo.messages.outgoing.unknown;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

import java.util.List;
import java.util.Map;

public class NewUserGiftComposer extends MessageComposer
{
    private final List<NewUserExperienceGiftOptions> options;

    public NewUserGiftComposer(List<NewUserExperienceGiftOptions> options)
    {
        this.options = options;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.NewUserGiftComposer);
        this.response.appendInt(this.options.size());
        for (NewUserExperienceGiftOptions option : this.options)
        {
            option.serialize(this.response);
        }
        return this.response;
    }

    public static class NewUserExperienceGiftOptions implements ISerialize
    {
        private final int unknownInt1;
        private final int unknownInt2;
        private final List<NewUserExperienceGift> gifts;

        public NewUserExperienceGiftOptions(int unknownInt1, int unknownInt2, List<NewUserExperienceGift> gifts)
        {
            this.unknownInt1 = unknownInt1;
            this.unknownInt2 = unknownInt2;
            this.gifts = gifts;
        }

        @Override
        public void serialize(ServerMessage message)
        {
            message.appendInt(this.unknownInt1);
            message.appendInt(this.unknownInt2);
            message.appendInt(this.gifts.size());
            for (NewUserExperienceGift gift : this.gifts)
            {
                gift.serialize(message);
            }
        }

        public static class NewUserExperienceGift implements ISerialize
        {
            private final String unknownString;
            private final Map<String, String> unknownMap;

            public NewUserExperienceGift(String unknownString, Map<String, String> unknownMap)
            {
                this.unknownString = unknownString;
                this.unknownMap = unknownMap;
            }

            @Override
            public void serialize(ServerMessage message)
            {
                message.appendString(this.unknownString);
                message.appendInt(this.unknownMap.size());
                for (Map.Entry<String, String> entry : this.unknownMap.entrySet())
                {
                    message.appendString(entry.getKey());
                    message.appendString(entry.getValue());
                }
            }
        }
    }
}