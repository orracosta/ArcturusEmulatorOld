package com.habboproject.server.game.rooms.objects.items.types.wall;

import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemWall;
import com.habboproject.server.game.rooms.objects.items.data.MoodlightData;
import com.habboproject.server.game.rooms.objects.items.data.MoodlightPresetData;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.storage.queries.items.MoodlightDao;


public class MoodlightWallItem extends RoomItemWall {
    private MoodlightData moodlightData = null;

    public MoodlightWallItem(long id, int itemId, Room room, int owner, String position, String data) {
        super(id, itemId, room, owner, position, data);
    }

    @Override
    public boolean onInteract(RoomEntity entity, int requestData, boolean isWiredTrigger) {
        return true;
    }

    @Override
    public void onLoad() {
        this.onPlaced();
    }

    @Override
    public void onPlaced() {
        if (this.getRoom().getItems().setMoodlight(this.getId())) {
            this.moodlightData = MoodlightDao.getMoodlightData(this.getId());
        }
    }

    @Override
    public void onUnload() {
        this.onPickup();
    }

    @Override
    public void onPickup() {
        if (this.getRoom().getItems().isMoodlightMatches(this)) {
            this.getRoom().getItems().removeMoodlight();
        }
    }

    public static boolean isValidColour(String colour) {
        switch (colour) {
            case "#000000":
            case "#0053F7":
            case "#EA4532":
            case "#82F349":
            case "#74F5F5":
            case "#E759DE":
            case "#F2F851":
                return true;

            default:
                return false;
        }
    }

    public String generateExtraData() {
        MoodlightPresetData preset = (this.getMoodlightData().getPresets().size() >= this.getMoodlightData().getActivePreset())
                ? this.getMoodlightData().getPresets().get(this.getMoodlightData().getActivePreset() - 1)
                : new MoodlightPresetData(true, "#000000", 255);

        StringBuilder sb = new StringBuilder();
        if (this.getMoodlightData().isEnabled()) {
            sb.append(2);
        } else {
            sb.append(1);
        }

        sb.append(",");
        sb.append(this.getMoodlightData().getActivePreset());
        sb.append(",");

        if (preset.backgroundOnly) {
            sb.append(2);
        } else {
            sb.append(1);
        }

        sb.append(",");
        sb.append(preset.colour);
        sb.append(",");
        sb.append(preset.intensity);
        return sb.toString();
    }

    public MoodlightData getMoodlightData() {
        return this.moodlightData;
    }
}
