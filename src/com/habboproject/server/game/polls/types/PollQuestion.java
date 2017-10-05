package com.habboproject.server.game.polls.types;

public abstract class PollQuestion {
    private String question;

    public PollQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}
