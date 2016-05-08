package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-3-2015 16:46.
 */
public class ModToolIssueUpdateComposer extends MessageComposer
{
    private final ModToolIssue issue;

    public ModToolIssueUpdateComposer(ModToolIssue issue)
    {
        this.issue = issue;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueUpdateComposer);
        this.issue.serialize(this.response);
        return this.response;
    }
}
