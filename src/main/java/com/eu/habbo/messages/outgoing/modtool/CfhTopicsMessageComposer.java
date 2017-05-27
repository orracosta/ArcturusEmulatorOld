package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.CfhCategory;
import com.eu.habbo.habbohotel.modtool.CfhTopic;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.procedure.TObjectProcedure;

public class CfhTopicsMessageComposer extends MessageComposer
{
    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.CfhTopicsMessageComposer);

        this.response.appendInt32(Emulator.getGameEnvironment().getModToolManager().getCfhCategories().valueCollection().size());

        Emulator.getGameEnvironment().getModToolManager().getCfhCategories().forEachValue(new TObjectProcedure<CfhCategory>()
        {
            @Override
            public boolean execute(CfhCategory category)
            {
                response.appendString(category.getName());
                response.appendInt32(category.getTopics().valueCollection().size());
                category.getTopics().forEachValue(new TObjectProcedure<CfhTopic>()
                {
                    @Override
                    public boolean execute(CfhTopic topic)
                    {
                        response.appendString(topic.name);
                        response.appendInt32(topic.id);
                        response.appendString(topic.action.toString());
                        return true;
                    }
                });
                return true;
            }
        });

        return this.response;
    }
}