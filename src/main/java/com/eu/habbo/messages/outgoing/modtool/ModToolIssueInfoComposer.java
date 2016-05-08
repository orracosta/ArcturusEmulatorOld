package com.eu.habbo.messages.outgoing.modtool;

import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;

/**
 * Created on 4-11-2014 14:49.
 */
public class ModToolIssueInfoComposer extends MessageComposer
{
    private final ModToolIssue issue;
    public ModToolIssueInfoComposer(ModToolIssue issue)
    {
        this.issue = issue;
    }

    @Override
    public ServerMessage compose()
    {
        this.response.init(Outgoing.ModToolIssueInfoComposer);
        this.issue.serialize(this.response);
        return this.response;
    }
}
