package com.habboproject.server.game.achievements.types;

public enum AchievementType {
    AVATAR_LOOKS("ACH_AvatarLooks"),
    MOTTO("ACH_Motto"),
    RESPECT_GIVEN("ACH_RespectGiven"),
    RESPECT_EARNED("ACH_RespectEarned"),
    ROOM_ENTRY("ACH_RoomEntry"),
    REGISTRATION_DURATION("ACH_RegistrationDuration"),
    PET_RESPECT_GIVEN("ACH_PetRespectGiver"),
    PET_LOVER("ACH_PetLover"),
    GIFT_GIVER("ACH_GiftGiver"),
    GIFT_RECEIVER("ACH_GiftReceiver"),
    BB_TILES_LOCKED("ACH_BattleBallTilesLocked"),
    BB_PLAYER("ACH_BattleBallPlayer"),
    BB_WINNER("ACH_BattleBallWinner"),
    ONLINE_TIME("ACH_AllTimeHotelPresence"),
    LOGIN("ACH_Login"),
    FRIENDS_LIST("ACH_FriendListSize"),
    CAMERA_PHOTO("ACH_CameraPhotoCount"),
    FOOTBALL_GOAL("ACH_FootballGoalScoredInRoom");

    private String groupName;

    AchievementType(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public static AchievementType getTypeByName(String name) {
        for (AchievementType type : AchievementType.values()) {
            if (type.groupName.equals(name)) {
                return type;
            }
        }

        return null;
    }
}
