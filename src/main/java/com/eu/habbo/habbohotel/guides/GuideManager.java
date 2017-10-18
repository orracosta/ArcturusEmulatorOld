package com.eu.habbo.habbohotel.guides;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.guides.*;
import com.eu.habbo.threading.runnables.GuardianTicketFindMoreSlaves;
import com.eu.habbo.threading.runnables.GuideFindNewHelper;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GuideManager
{
    private final THashSet<GuideTour> activeTours;
    private final THashSet<GuardianTicket> activeTickets;
    private final THashSet<GuardianTicket> closedTickets;
    private final THashMap<Habbo, Boolean> activeHelpers;
    private final THashMap<Habbo, GuardianTicket> activeGuardians;
    private final THashMap<Integer, Integer> tourRequestTiming;

    public GuideManager()
    {
        this.activeTours = new THashSet<GuideTour>();
        this.activeTickets = new THashSet<GuardianTicket>();
        this.closedTickets = new THashSet<GuardianTicket>();
        this.activeHelpers = new THashMap<Habbo, Boolean>();
        this.activeGuardians = new THashMap<Habbo, GuardianTicket>();
        this.tourRequestTiming = new THashMap<Integer, Integer>();
    }

    public void userLogsOut(Habbo habbo)
    {
        GuideTour tour = this.getGuideTourByHabbo(habbo);

        if(tour != null)
        {
            this.endSession(tour);
        }

        this.activeHelpers.remove(habbo);

        GuardianTicket ticket = this.getTicketForGuardian(habbo);

        if(ticket != null)
        {
            ticket.removeGuardian(habbo);
        }

        this.activeGuardians.remove(habbo);
    }

    /**
     * Guide Shit
     */

    public void setOnGuide(Habbo habbo, boolean onDuty)
    {
        if(onDuty)
        {
            this.activeHelpers.put(habbo, false);
        }
        else
        {
            GuideTour tour = this.getGuideTourByHabbo(habbo);

            if(tour != null)
                return;

            this.activeHelpers.remove(habbo);
        }
    }

    /**
     * Searches for an helper to handle the tour.
     * Automatically schedules the tour request untill an helper is found
     * or the request has been cancelled by the requester.
     *
     * @param tour The tour to find a helper for.
     * @return Wether an helper has been found.
     */
    public boolean findHelper(GuideTour tour)
    {
        synchronized (this.activeHelpers)
        {
            for(Map.Entry<Habbo, Boolean> set : activeHelpers.entrySet())
            {
                if(!set.getValue())
                {
                    if(!tour.hasDeclined(set.getKey().getHabboInfo().getId()))
                    {
                        tour.checkSum++;
                        tour.setHelper(set.getKey());
                        set.getKey().getClient().sendResponse(new GuideSessionAttachedComposer(tour, true));
                        tour.getNoob().getClient().sendResponse(new GuideSessionAttachedComposer(tour, false));
                        Emulator.getThreading().run(new GuideFindNewHelper(tour, set.getKey()), 60000);
                        this.activeTours.add(tour);
                        return true;
                    }
                }
            }
        }
        this.endSession(tour);
        tour.getNoob().getClient().sendResponse(new GuideSessionErrorComposer(GuideSessionErrorComposer.NO_HELPERS_AVAILABLE));

        return false;
    }

    /**
     * Declines an tourrequest for the current helper assigned.
     * Automatically searches for a new helper.
     * @param tour The tour to decline.
     */
    public void declineTour(GuideTour tour)
    {
        Habbo helper = tour.getHelper();
        tour.addDeclinedHelper(tour.getHelper().getHabboInfo().getId());
        tour.setHelper(null);
        helper.getClient().sendResponse(new GuideSessionEndedComposer(GuideSessionEndedComposer.HELP_CASE_CLOSED));
        helper.getClient().sendResponse(new GuideSessionDetachedComposer());
        if(!this.findHelper(tour))
        {
            this.endSession(tour);
            tour.getNoob().getClient().sendResponse(new GuideSessionErrorComposer(GuideSessionErrorComposer.NO_HELPERS_AVAILABLE));
        }
    }

    /**
     * Starts an new tour session.
     * @param tour The tour to start.
     * @param helper The helper to assign.
     */
    public void startSession(GuideTour tour, Habbo helper)
    {
        synchronized (this.activeTours)
        {
            synchronized (this.activeHelpers)
            {
                this.activeHelpers.put(helper, true);

                ServerMessage message = new GuideSessionStartedComposer(tour).compose();
                tour.getNoob().getClient().sendResponse(message);
                tour.getHelper().getClient().sendResponse(message);
                tour.checkSum++;
                this.tourRequestTiming.put(tour.getStartTime(), Emulator.getIntUnixTimestamp());
            }
        }
    }

    /**
     * Ends an tour session.
     * <list>
     *     <ul>
     *         The requester cancels the tour request.
     *     </ul>
     *     <ul>
     *         The requester says no more help needed.
     *     </ul>
     *     <ul>
     *         The requester reported the helper.
     *     </ul>
     * </list>
     * @param tour
     */
    public void endSession(GuideTour tour)
    {
        synchronized (this.activeTours)
        {
            synchronized (this.activeHelpers)
            {
                tour.getNoob().getClient().sendResponse(new GuideSessionEndedComposer(GuideSessionEndedComposer.HELP_CASE_CLOSED));
                tour.end();

                if(tour.getHelper() != null)
                {
                    this.activeHelpers.put(tour.getHelper(), false);
                    tour.getHelper().getClient().sendResponse(new GuideSessionEndedComposer(GuideSessionEndedComposer.HELP_CASE_CLOSED));
                    tour.getHelper().getClient().sendResponse(new GuideSessionDetachedComposer());
                    tour.getHelper().getClient().sendResponse(new GuideToolsComposer(true));
                }
            }
        }
    }

    /**
     * Recommend the guide.
     * @param tour The GuideTour this applies to.
     * @param recommend Recommended or not.
     */
    public void recommend(GuideTour tour, boolean recommend)
    {
        synchronized (this.activeTours)
        {
            tour.setWouldRecommend(recommend ? GuideRecommendStatus.YES : GuideRecommendStatus.NO);
            tour.getNoob().getClient().sendResponse(new GuideSessionDetachedComposer());
            AchievementManager.progressAchievement(tour.getNoob(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("GuideFeedbackGiver"));

            this.activeTours.remove(tour);
        }
    }

    /**
     * Gets the GuideTour for the given helper.
     * @param helper The helper to find the tour for.
     * @return The GuideTour for the helper. NULL when not found.
     */
    public GuideTour getGuideTourByHelper(Habbo helper)
    {
        synchronized (this.activeTours)
        {
            for(GuideTour tour : this.activeTours)
            {
                if(!tour.isEnded() && tour.getHelper() == helper)
                {
                    return tour;
                }
            }
        }

        return null;
    }

    /**
     * Gets the GuideTour for the given requester.
     * @param noob The noob to find the tour for.
     * @return The GuideTour for the noob. NULL when not found.
     */
    public GuideTour getGuideTourByNoob(Habbo noob)
    {
        synchronized (this.activeTours)
        {
            for(GuideTour tour : this.activeTours)
            {
                if(tour.getNoob() == noob)
                {
                    return tour;
                }
            }
        }

        return null;
    }

    /**
     * Searches for any GuideTour linked to the either the noob or the helper.
     * @param habbo The Habbo to look for.
     * @return An given tour.
     */
    public GuideTour getGuideTourByHabbo(Habbo habbo)
    {
        synchronized (this.activeTours)
        {
            for(GuideTour tour : this.activeTours)
            {
                if(tour.getHelper() == habbo || tour.getNoob() == habbo)
                {
                    return tour;
                }
            }
        }

        return null;
    }

    /**
     * @return The amount of helpers that are on duty.
     */
    public int getGuidesCount()
    {
        return this.activeHelpers.size();
    }

    /**
     * @return The amount of guardians that are on duty.
     */
    public int getGuardiansCount()
    {
        return this.activeGuardians.size();
    }

    /**
     * @return true if there are guardians on duty.
     */
    public boolean activeGuardians()
    {
        return this.activeGuardians.size() > 0;
    }

    /**
     * @return The average waiting time before an helper is assinged.
     */
    public int getAverageWaitingTime()
    {
        synchronized (this.tourRequestTiming)
        {
            int total = 0;

            if(this.tourRequestTiming.isEmpty())
                return 5;

            for(Map.Entry<Integer, Integer> set : this.tourRequestTiming.entrySet())
            {
                total += (set.getValue() - set.getKey());
            }

            return total / this.tourRequestTiming.size();
        }
    }

    /**
     * Guardians
     */

    /**
     * Adds a new guardian ticket to the active ticket Queue
     * @param ticket The GuardianTicket to add.
     */
    public void addGuardianTicket(GuardianTicket ticket)
    {
        synchronized (this.activeTickets)
        {
            this.activeTickets.add(ticket);

            this.findGuardians(ticket);
        }
    }

    /**
     * Searches for new guardians to vote on the given ticket.
     * @param ticket The GuardianTicket to find Guardians for.
     */
    public void findGuardians(GuardianTicket ticket)
    {
        synchronized (this.activeGuardians)
        {
            int count = ticket.getVotedCount();

            THashSet<Habbo> selectedGuardians = new THashSet<Habbo>();

            for(Map.Entry<Habbo, GuardianTicket> set : this.activeGuardians.entrySet())
            {
                if(count == 5)
                    break;

                if(set.getKey() == ticket.getReporter() ||
                        set.getKey() == ticket.getReported())
                    continue;

                if(set.getValue() == null)
                {
                    if(ticket.getVoteForGuardian(set.getKey()) == null)
                    {
                        ticket.requestToVote(set.getKey());

                        selectedGuardians.add(set.getKey());
                    }
                }

                count++;
            }

            for(Habbo habbo : selectedGuardians)
            {
                this.activeGuardians.put(habbo, ticket);
            }

            if(count < 5)
            {
                Emulator.getThreading().run(new GuardianTicketFindMoreSlaves(ticket), 3000);
            }
        }
    }

    /**
     * Accept a ticket for an Guardian.
     * @param guardian The Guardian who accepts.
     * @param accepted Accepted.
     */
    public void acceptTicket(Habbo guardian, boolean accepted)
    {
        GuardianTicket ticket = this.getTicketForGuardian(guardian);

        if(ticket != null)
        {
            if(!accepted)
            {
                ticket.removeGuardian(guardian);
                this.findGuardians(ticket);
                this.activeGuardians.put(guardian, null);
            }
            else
            {
                ticket.addGuardian(guardian);
                this.activeGuardians.put(guardian, ticket);
            }
        }
    }

    /**
     * @return The active GuardianTicket for the Guardian.
     */
    public GuardianTicket getTicketForGuardian(Habbo guardian)
    {
        synchronized (this.activeGuardians)
        {
            return this.activeGuardians.get(guardian);
        }
    }

    /**
     * @return The most recent ticket send by the reporter. NULL when not found.
     */
    public GuardianTicket getRecentTicket(Habbo reporter)
    {
        GuardianTicket ticket = null;

        synchronized (this.activeTickets)
        {
            for(GuardianTicket t : this.activeTickets)
            {
                if(t.getReporter() == reporter)
                {
                    return t;
                }
            }
        }

        synchronized (this.closedTickets)
        {
            for(GuardianTicket t : this.closedTickets)
            {
                if(t.getReporter() != reporter)
                    continue;

                if(ticket == null || Emulator.getIntUnixTimestamp() - (t.getDate().getTime() / 1000) < Emulator.getIntUnixTimestamp() - (ticket.getDate().getTime() / 1000))
                {
                    ticket = t;
                }
            }
        }

        return ticket;
    }

    public GuardianTicket getOpenReportedHabboTicket(Habbo reported)
    {
        GuardianTicket ticket = null;

        synchronized (this.activeTickets)
        {
            for(GuardianTicket t : this.activeTickets)
            {
                if(t.getReported() == reported)
                {
                    return t;
                }
            }
        }

        return ticket;
    }

    /**
     * Closes a ticket and moves it to the closed ticket queue.
     * @param ticket The GuardianTicket to close.
     */
    public void closeTicket(GuardianTicket ticket)
    {
        synchronized (this.activeTickets)
        {
            this.activeTickets.remove(ticket);
        }

        synchronized (this.closedTickets)
        {
            this.closedTickets.add(ticket);
        }

        THashSet<Habbo> toUpdate = new THashSet<Habbo>();

        synchronized (this.activeGuardians)
        {
            for (Map.Entry<Habbo, GuardianTicket> set : this.activeGuardians.entrySet())
            {
                if (set.getValue() == ticket)
                {
                    toUpdate.add(set.getKey());
                }
            }

            for (Habbo habbo : toUpdate)
            {
                this.activeGuardians.put(habbo, null);
            }
        }
    }

    /**
     * Sets the give Guardian active for Guardian duty.
     * @param habbo The Guardian to set on duty.
     * @param onDuty On duty or not.
     */
    public void setOnGuardian(Habbo habbo, boolean onDuty)
    {
        if(onDuty)
        {
            this.activeGuardians.put(habbo, null);
        }
        else
        {
            GuardianTicket ticket = this.getTicketForGuardian(habbo);

            if(ticket != null)
            {
                ticket.removeGuardian(habbo);
            }

            this.activeGuardians.remove(habbo);
        }
    }

    /**
     * Cleans up shit.
     */
    public void cleanup()
    {
        synchronized (this.activeTours)
        {
            THashSet<GuideTour> tours = new THashSet<GuideTour>();
            for(GuideTour tour : this.activeTours)
            {
                if(tour.isEnded() && (Emulator.getIntUnixTimestamp() - tour.getEndTime() > 300))
                {
                    tours.add(tour);
                }
            }

            for(GuideTour tour : tours)
            {
                this.activeTours.remove(tour);
            }
        }

        synchronized (this.activeTickets)
        {
            THashSet<GuardianTicket> tickets = new THashSet<GuardianTicket>();

            for(GuardianTicket ticket : this.closedTickets)
            {
                if(Emulator.getIntUnixTimestamp() - (ticket.getDate().getTime() / 1000) > 15 * 60)
                {
                    tickets.add(ticket);
                }
            }

            for(GuardianTicket ticket : tickets)
            {
                this.closedTickets.remove(ticket);
            }
        }
    }
}
