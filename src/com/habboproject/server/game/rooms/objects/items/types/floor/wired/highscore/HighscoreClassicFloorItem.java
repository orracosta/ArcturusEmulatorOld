package com.habboproject.server.game.rooms.objects.items.types.floor.wired.highscore;

import com.habboproject.server.api.networking.messages.IComposer;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.entities.types.PlayerEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.ClassicScoreboardData;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.ResetItemData;
import com.habboproject.server.game.rooms.objects.items.types.floor.wired.data.scoreboard.entries.HighscoreEntry;
import com.habboproject.server.game.rooms.types.Room;
import com.habboproject.server.storage.queries.items.WiredHighscoreDao;
import com.habboproject.server.utilities.JsonFactory;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.List;

public class HighscoreClassicFloorItem extends RoomItemFloor {
    private boolean state;
    private final ClassicScoreboardData itemData;
    private ResetItemData resetData;

    public HighscoreClassicFloorItem(long id, int itemId, Room room, int owner, int groupId, int x, int y, double z, int rotation, String data) {
        super(id, itemId, room, owner, groupId, x, y, z, rotation, data);

        if (data.startsWith("1{") || data.startsWith("0{")) {
            this.state = data.startsWith("1");
            this.itemData = JsonFactory.getInstance().fromJson(data.substring(1), ClassicScoreboardData.class);

            int clearType = 0;

            switch (this.getDefinition().getItemName().split("[_]")[1]) {
                case "classic*2": {
                    clearType = 1;
                    break;
                }
                case "classic*3": {
                    clearType = 2;
                    break;
                }
                case "classic*4": {
                    clearType = 3;
                    break;
                }
                default:
                    break;
            }

            if (this.itemData.getClearType() != clearType) {
                this.itemData.setClearType(clearType);
            }

            if (this.itemData.getScoreType() != 2) {
                this.itemData.setScoreType(2);
            }

            String resetData = WiredHighscoreDao.getData(id);
            if (!resetData.isEmpty()) {
                this.resetData = JsonFactory.getInstance().fromJson(resetData, ResetItemData.class);
            }
            else {
                this.createResetData();
            }

            if (this.needsReset()) {
                this.updateResetData();
            }
        }
        else {
            int clearType = 0;
            switch (this.getDefinition().getItemName().split("[_]")[1]) {
                case "classic*2": {
                    clearType = 1;
                    break;
                }
                case "classic*3": {
                    clearType = 2;
                    break;
                }
                case "classic*4": {
                    clearType = 3;
                    break;
                }
                default:
                    break;
            }

            this.state = false;

            this.itemData = new ClassicScoreboardData(2, clearType, Lists.newArrayList());

            this.createResetData();
        }
    }

    public void createResetData() {
        DateTime date = new DateTime();
        this.resetData = new ResetItemData(date.getDayOfMonth(), date.getDayOfYear(), date.getMonthOfYear(), date.getDayOfWeek(), date.getWeekOfWeekyear());

        WiredHighscoreDao.save(JsonFactory.getInstance().toJson(this.resetData), this.getId());
    }

    public boolean needsReset() {
        DateTime date = new DateTime();
        if (this.getScoreData().getClearType() == 1 && this.getResetData().getLastDay() != date.getDayOfMonth()) {
            this.getResetData().setLastDay(date.getDayOfMonth());
            return true;
        }

        if (this.getScoreData().getClearType() == 2 && date.getWeekOfWeekyear() != this.getResetData().getLastWeekOfWeekyear()) {
            this.getResetData().setLastWeekOfWeekyear(date.getWeekOfWeekyear());
            return true;
        }

        if (this.getScoreData().getClearType() == 3 && this.getResetData().getLastMonth() != date.getMonthOfYear()) {
            this.getResetData().setLastMonth(date.getMonthOfYear());
            return true;
        }

        return false;
    }

    public void updateResetData() {
        DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime date = new DateTime();

        this.resetData = new ResetItemData(date.getDayOfMonth(), date.getDayOfYear(), date.getMonthOfYear(), date.getDayOfWeek(), date.getWeekOfWeekyear());
        this.getScoreData().removeAll();

        WiredHighscoreDao.update(JsonFactory.getInstance().toJson(this.resetData), this.getId());
    }

    public void reset() {
        this.getScoreData().removeAll();
        WiredHighscoreDao.update(JsonFactory.getInstance().toJson(this.resetData), this.getId());
    }

    public void compose(final IComposer msg, final boolean isNew) {
        msg.writeInt(0);
        msg.writeInt(6);

        msg.writeString((this.state ? "1" : "0"));

        msg.writeInt(this.getScoreData().getScoreType());
        msg.writeInt(this.getScoreData().getClearType());

        msg.writeInt((this.getScoreData().getEntries().size() > 50) ? 50 : this.getScoreData().getEntries().size());

        int x = 0;
        for (HighscoreEntry entry : this.getScoreData().getEntries()) {
            if (++x > 50) {
                break;
            }

            msg.writeInt(entry.getScore());
            msg.writeInt(entry.getUsers().size());
            for (String name : entry.getUsers()) {
                msg.writeString(name);
            }
        }
    }

    public boolean onInteract(final RoomEntity entity, final int requestData, final boolean isWiredTrigger) {
        if (isWiredTrigger) {
            if (!(entity instanceof PlayerEntity)) {
                return false;
            }

            PlayerEntity pEntity = (PlayerEntity)entity;
            if (!pEntity.getRoom().getRights().hasRights(pEntity.getPlayerId()) && !pEntity.getPlayer().getPermissions().getRank().roomFullControl()) {
                return false;
            }
        }

        this.state = !this.state;

        this.sendUpdate();
        this.saveData();

        return true;
    }

    public String getDataObject() {
        return String.valueOf(this.state ? "1" : "0") + JsonFactory.getInstance().toJson(this.itemData);
    }

    public void addEntry(List<String> users, int score) {
        this.itemData.addEntry(users, score);
        this.sendUpdate();
        this.saveData();
    }

    public ClassicScoreboardData getScoreData() {
        return this.itemData;
    }

    public ResetItemData getResetData() {
        return this.resetData;
    }
}
