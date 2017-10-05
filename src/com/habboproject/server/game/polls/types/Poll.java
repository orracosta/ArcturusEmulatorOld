package com.habboproject.server.game.polls.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Poll {
    private final int pollId;
    private final int roomId;

    private final String pollTitle;
    private final String thanksMessage;
    private final String badgeReward;

    private final Map<Integer, PollQuestion> pollQuestions;

    private final Set<Integer> playersAnswered;

    public Poll(int pollId, int roomId, String pollTitle, String thanksMessage, String badgeReward) {
        this.pollId = pollId;
        this.roomId = roomId;
        this.pollTitle = pollTitle;
        this.thanksMessage = thanksMessage;
        this.badgeReward = badgeReward;

        this.pollQuestions = new ConcurrentHashMap<>();
        this.playersAnswered = new HashSet<>();
    }

    public void addQuestion(int questionId, PollQuestion pollQuestion) {
        this.pollQuestions.put(questionId, pollQuestion);
    }

    public int getPollId() {
        return pollId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public Map<Integer, PollQuestion> getPollQuestions() {
        return pollQuestions;
    }

    public String getThanksMessage() {
        return thanksMessage;
    }

    public Set<Integer> getPlayersAnswered() {
        return playersAnswered;
    }

    public String getBadgeReward() {
        return badgeReward;
    }
}
