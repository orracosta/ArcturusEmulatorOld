package com.eu.habbo.habbohotel.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.modtool.ModToolChatLog;
import com.eu.habbo.habbohotel.modtool.ModToolIssue;
import com.eu.habbo.habbohotel.modtool.ModToolTicketType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.guardians.*;
import com.eu.habbo.messages.outgoing.guides.BullyReportClosedComposer;
import com.eu.habbo.threading.runnables.GuardianNotAccepted;
import com.eu.habbo.threading.runnables.GuardianVotingFinish;
import gnu.trove.map.hash.THashMap;

import java.util.*;

public class GuardianTicket
{
    private ArrayList<ModToolChatLog> chatLogs;
    private  final THashMap<Habbo, GuardianVote> votes = new THashMap<Habbo, GuardianVote>();
    private GuardianVoteType verdict;
    private int timeLeft = 120;
    private int resendCount = 0;
    private int checkSum = 0;
    private final Habbo reporter;
    private final Habbo reported;
    private final Date date;

    private int guardianCount = 0;

    public GuardianTicket(Habbo reporter, Habbo reported, ArrayList<ModToolChatLog> chatLogs)
    {
        this.chatLogs = chatLogs;
        Collections.sort(chatLogs);
        Emulator.getThreading().run(new GuardianVotingFinish(this), 120000);

        this.reported = reported;
        this.reporter = reporter;
        this.date = new Date();
    }

    /**
     * Requests an Guardian to vote on this ticket.
     * @param guardian The Guardian to ask.
     */
    public void requestToVote(Habbo guardian)
    {
        guardian.getClient().sendResponse(new GuardianNewReportReceivedComposer(this));

        this.votes.put(guardian, new GuardianVote(guardianCount, guardian));

        Emulator.getThreading().run(new GuardianNotAccepted(this, guardian), Emulator.getConfig().getInt("guardians.accept.timer") * 1000);
    }

    /**
     * Adds an guardian to this ticket.
     * @param guardian The guardian to add.
     */
    public void addGuardian(Habbo guardian)
    {
        GuardianVote vote = this.votes.get(guardian);

        if(vote != null && vote.type == GuardianVoteType.SEARCHING)
        {
            guardian.getClient().sendResponse(new GuardianVotingRequestedComposer(this));
            vote.type = GuardianVoteType.WAITING;
            this.updateVotes();
        }
    }

    /**
     * Removes an Guardian from this ticket.
     * @param guardian The Guardian to remove.
     */
    public void removeGuardian(Habbo guardian)
    {
        GuardianVote vote = this.getVoteForGuardian(guardian);

        if(vote == null)
            return;

        if(vote.type == GuardianVoteType.SEARCHING || vote.type == GuardianVoteType.WAITING)
        {
            this.getVoteForGuardian(guardian).type = GuardianVoteType.NOT_VOTED;
        }

        this.getVoteForGuardian(guardian).ignore = true;

        guardian.getClient().sendResponse(new GuardianVotingTimeEnded());

        this.updateVotes();
    }

    /**
     * Set the vote for an Guardian.
     * @param guardian The Guardian to set the vote for.
     * @param vote The GuardianVoteType to set the vote to.
     */
    public void vote(Habbo guardian, GuardianVoteType vote)
    {
        this.votes.get(guardian).type = vote;

        this.updateVotes();

        AchievementManager.progressAchievement(guardian, Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideChatReviewer"));

        this.finish();
    }

    /**
     * Updates the votes to all other voting guardians.
     */
    public void updateVotes()
    {
        synchronized (this.votes)
        {
            for(Map.Entry<Habbo, GuardianVote> set : this.votes.entrySet())
            {
                if(set.getValue().type == GuardianVoteType.WAITING || set.getValue().type == GuardianVoteType.NOT_VOTED || set.getValue().ignore || set.getValue().type == GuardianVoteType.SEARCHING)
                    continue;

                set.getKey().getClient().sendResponse(new GuardianVotingVotesComposer(this, set.getKey()));
            }
        }
    }

    /**
     * Tries to finish the voting on a ticket and searches for more guardians if needed.
     */
    public void finish()
    {
        int votedCount = this.getVotedCount();
        if(votedCount < Emulator.getConfig().getInt("guardians.minimum.votes"))
        {
            if(this.votes.size() >= Emulator.getConfig().getInt("guardians.maximum.guardians.total") || this.resendCount == Emulator.getConfig().getInt("guardians.maximum.resends"))
            {
                this.verdict = GuardianVoteType.FORWARDED;

                Emulator.getGameEnvironment().getGuideManager().closeTicket(this);

                ModToolIssue issue = new ModToolIssue(this.reporter.getHabboInfo().getId(),
                        this.reporter.getHabboInfo().getUsername(),
                        this.reported.getHabboInfo().getId(),
                        this.reported.getHabboInfo().getUsername(),
                        0,
                        "",
                        ModToolTicketType.GUARDIAN);

                Emulator.getGameEnvironment().getModToolManager().addTicket(issue);
                Emulator.getGameEnvironment().getModToolManager().updateTicketToMods(issue);

                this.reporter.getClient().sendResponse(new BullyReportClosedComposer(BullyReportClosedComposer.CLOSED));
            }
            else
            {
                this.timeLeft = 30;
                Emulator.getThreading().run(new GuardianVotingFinish(this), 10000);
                this.resendCount++;

                Emulator.getGameEnvironment().getGuideManager().findGuardians(this);
            }
        }
        else
        {
            this.verdict = calculateVerdict();

            for(Map.Entry<Habbo, GuardianVote> set : this.votes.entrySet())
            {
                if(set.getValue().type == GuardianVoteType.ACCEPTABLY ||
                        set.getValue().type == GuardianVoteType.BADLY ||
                        set.getValue().type == GuardianVoteType.AWFULLY)
                {
                    set.getKey().getClient().sendResponse(new GuardianVotingResultComposer(this, set.getValue()));
                }
            }

            Emulator.getGameEnvironment().getGuideManager().closeTicket(this);

            if(this.verdict == GuardianVoteType.ACCEPTABLY)
                this.reporter.getClient().sendResponse(new BullyReportClosedComposer(BullyReportClosedComposer.MISUSE));
            else
                this.reporter.getClient().sendResponse(new BullyReportClosedComposer(BullyReportClosedComposer.CLOSED));
        }
    }

    /**
     * @return True if an verdict has been set.
     */
    public boolean isFinished()
    {
        return !(this.verdict == null);
    }

    /**
     * @return Calculates the verdict of this ticket.
     */
    public GuardianVoteType calculateVerdict()
    {
        int countAcceptably = 0;
        int countBadly = 0;
        int countAwfully = 0;
        int total = 0;

        synchronized (this.votes)
        {
            for(Map.Entry<Habbo, GuardianVote> set : this.votes.entrySet())
            {
                GuardianVote vote = set.getValue();

                if(vote.type == GuardianVoteType.ACCEPTABLY)
                {
                    countAcceptably++;
                }
                else if(vote.type == GuardianVoteType.BADLY)
                {
                    countBadly++;
                }
                else if(vote.type == GuardianVoteType.AWFULLY)
                {
                    countAwfully++;
                }
            }
        }

        total += countAcceptably;
        total += countBadly;
        total += countAwfully;

//        if(total / countAcceptably * 100.0 >= 35)
//        {
//            if(total / countBadly >= )
//        }

        return GuardianVoteType.BADLY;
    }

    public GuardianVote getVoteForGuardian(Habbo guardian)
    {
        return this.votes.get(guardian);
    }

    public THashMap<Habbo, GuardianVote> getVotes()
    {
        return this.votes;
    }

    public int getTimeLeft()
    {
        return this.timeLeft;
    }

    public GuardianVoteType getVerdict()
    {
        return this.verdict;
    }

    public ArrayList<ModToolChatLog> getChatLogs()
    {
        return this.chatLogs;
    }

    public int getResendCount()
    {
        return this.resendCount;
    }

    public int getCheckSum()
    {
        return this.checkSum;
    }

    public Habbo getReporter()
    {
        return this.reporter;
    }

    public Habbo getReported()
    {
        return this.reported;
    }

    public Date getDate()
    {
        return this.date;
    }

    public int getGuardianCount()
    {
        return this.guardianCount;
    }

    /**
     * Sorts all votes.
     * @param guardian The guardian to remove from this vote list.
     * @return The sorted votes list.
     */
    public ArrayList<GuardianVote> getSortedVotes(Habbo guardian)
    {
        synchronized (this.votes)
        {
            ArrayList<GuardianVote> votes = new ArrayList<GuardianVote>(this.votes.values());
            Collections.sort(votes);

            GuardianVote v = null;
            for(GuardianVote vote : votes)
            {
                if(vote.guardian == guardian)
                {
                    v = vote;
                    break;
                }
            }
            votes.remove(v);

            return votes;
        }
    }

    /**
     * @return The amount of votes (Acceptably, Badly or Awfully) thath as been cast.
     */
    public int getVotedCount()
    {
        int count = 0;
        synchronized (this.votes)
        {
            for(Map.Entry<Habbo, GuardianVote> set : this.votes.entrySet())
            {
                if(set.getValue().type == GuardianVoteType.ACCEPTABLY ||
                        set.getValue().type == GuardianVoteType.BADLY ||
                        set.getValue().type == GuardianVoteType.AWFULLY)
                    count++;
            }
        }

        return count;
    }
}
