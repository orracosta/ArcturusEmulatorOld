package com.habboproject.server.game.quests.types;

public enum QuestType {

    FURNI_MOVE(0, "MOVE_ITEM"),
    FURNI_ROTATE(1, "ROTATE_ITEM"),
    FURNI_PLACE(2, "PLACE_ITEM"),
    FURNI_PICK(3, "PICKUP_ITEM"),
    FURNI_SWITCH(4, "SWITCH_ITEM_STATE"),
    FURNI_STACK(5, "STACK_ITEM"),
    FURNI_DECORATION_FLOOR(6, "PLACE_FLOOR"),
    FURNI_DECORATION_WALL(7, "PLACE_WALLPAPER"),
    SOCIAL_VISIT(8, "ENTER_OTHERS_ROOM"),
    SOCIAL_CHAT(9, "CHAT_WITH_SOMEONE"),
    SOCIAL_FRIEND(10, "REQUEST_FRIEND"),
    SOCIAL_RESPECT(11, "GIVE_RESPECT"),
    SOCIAL_DANCE(12, "DANCE"),
    SOCIAL_WAVE(13, "WAVE"),
    PROFILE_CHANGE_LOOK(14, "CHANGE_FIGURE"),
    PROFILE_CHANGE_MOTTO(15, "CHANGE_MOTTO"),
    PROFILE_BADGE(16, "WEAR_BADGE"),
    EXPLORE_FIND_ITEM(17, "FIND_STUFF");

    private int questType;
    private String action;

    QuestType(int type, String action) {
        this.questType = type;
        this.action = action;
    }

    public int getQuestType() {
        return this.questType;
    }

    public String getAction() {
        return this.action;
    }

    public static QuestType getById(int id) {
        for (QuestType type : values()) {
            if (type.getQuestType() == id) {
                return type;
            }
        }

        return QuestType.EXPLORE_FIND_ITEM; // default
    }
}
