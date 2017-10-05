package com.habboproject.server.game.rooms.types.misc;

import com.habboproject.server.game.rooms.RoomManager;

import java.util.HashMap;
import java.util.Map;


public class ChatEmotionsManager {
    private Map<String, ChatEmotion> emotions;

    public ChatEmotionsManager() {
        emotions = new HashMap<String, ChatEmotion>() {{
            put(":)", ChatEmotion.SMILE);
            put(";)", ChatEmotion.SMILE);
            put(":]", ChatEmotion.SMILE);
            put(";]", ChatEmotion.SMILE);
            put("=)", ChatEmotion.SMILE);
            put("=]", ChatEmotion.SMILE);
            put(":-)", ChatEmotion.SMILE);

            put(">:(", ChatEmotion.ANGRY);
            put(">:[", ChatEmotion.ANGRY);
            put(">;[", ChatEmotion.ANGRY);
            put(">;(", ChatEmotion.ANGRY);
            put(">=(", ChatEmotion.ANGRY);

            put(":o", ChatEmotion.SHOCKED);
            put(";o", ChatEmotion.SHOCKED);
            put(">;o", ChatEmotion.SHOCKED);
            put(">:o", ChatEmotion.SHOCKED);
            put(">=o", ChatEmotion.SHOCKED);
            put("=o", ChatEmotion.SHOCKED);

            put(";'(", ChatEmotion.SAD);
            put(";[", ChatEmotion.SAD);
            put(":[", ChatEmotion.SAD);
            put(";(", ChatEmotion.SAD);
            put("=(", ChatEmotion.SAD);
            put("='(", ChatEmotion.SAD);
            put(":(", ChatEmotion.SAD);
            put(":-(", ChatEmotion.SAD);

            put(";D", ChatEmotion.LAUGH);
            put(":D", ChatEmotion.LAUGH);
            put(":L", ChatEmotion.LAUGH);

            //hehe ;-)
            put("trylix", ChatEmotion.SMILE);
        }};

        RoomManager.log.info("Loaded " + this.emotions.size() + " chat emotions");
    }

    public ChatEmotion getEmotion(String message) {
        for (Map.Entry<String, ChatEmotion> emotion : emotions.entrySet()) {
            if (message.toLowerCase().contains(emotion.getKey().toLowerCase())) {
                return emotion.getValue();
            }
        }
        return ChatEmotion.NONE;
    }
}
