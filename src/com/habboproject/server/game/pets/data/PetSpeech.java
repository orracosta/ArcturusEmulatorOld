package com.habboproject.server.game.pets.data;

import com.habboproject.server.utilities.RandomInteger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetSpeech {
    private Map<PetMessageType, List<String>> messages;

    public PetSpeech() {
        this.messages = new HashMap<>();
    }

    public String getMessageByType(PetMessageType type) {
        final List<String> availableMessages = messages.get(type);

        if(availableMessages.size() == 0) {
            return null;
        }

        int index = RandomInteger.getRandom(0, availableMessages.size() - 1);
        return availableMessages.get(index);
    }

    public Map<PetMessageType, List<String>> getMessages() {
        return messages;
    }
}
