package com.habboproject.server.game.rooms.types.misc;

public enum ChatEmotion {
    NONE(0),
    SMILE(1),
    ANGRY(2),
    SHOCKED(3),
    SAD(4),
    LAUGH(6);

    private int emotionId;

    ChatEmotion(int emotionId) {
        this.emotionId = emotionId;
    }

    public int getEmotionId() {
        return emotionId;
    }
}
