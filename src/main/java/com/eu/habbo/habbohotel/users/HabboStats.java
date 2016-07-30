package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.Achievement;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TIntHashSet;
import gnu.trove.stack.array.TIntArrayStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HabboStats implements Runnable
{
    private final int timeLoggedIn = Emulator.getIntUnixTimestamp();

    private Habbo habbo;

    public int achievementScore;
    public int respectPointsReceived;
    public int respectPointsGiven;
    public int respectPointsToGive;

    public int petRespectPointsToGive;

    public boolean blockFollowing;
    public boolean blockFriendRequests;
    public boolean blockRoomInvites;
    public boolean blockStaffAlerts;
    public boolean allowTrade;
    public boolean preferOldChat;
    public boolean blockCameraFollow;
    public RoomChatMessageBubbles chatColor;

    private int clubExpireTimestamp;

    public int volumeSystem;
    public int volumeFurni;
    public int volumeTrax;

    public int guild;
    public int[] guilds;

    public String[] tags;

    public TIntArrayStack votedRooms;
    public int loginStreak;
    public int rentedItemId;
    public int rentedTimeEnd;
    public int hofPoints;

    private final THashMap<Achievement, Integer> achievementProgress; //TEMP PUBLIC
    private final THashMap<Achievement, Integer> achievementCache;
    private final THashMap<Integer, CatalogItem> recentPurchases;
    public final TIntHashSet ignoredUsers;

    public final HabboNavigatorWindowSettings navigatorWindowSettings;

    private HabboStats(ResultSet set, Habbo habbo) throws SQLException
    {
        this.achievementProgress = new THashMap<Achievement, Integer>();
        this.achievementCache = new THashMap<Achievement, Integer>();
        this.recentPurchases = new THashMap<Integer, CatalogItem>();
        this.ignoredUsers = new TIntHashSet();

        this.habbo = habbo;

        this.achievementScore = set.getInt("achievement_score");
        this.respectPointsReceived = set.getInt("respects_received");
        this.respectPointsGiven = set.getInt("respects_given");
        this.petRespectPointsToGive = set.getInt("daily_pet_respect_points");
        this.respectPointsToGive = set.getInt("daily_respect_points");
        this.blockFollowing = set.getString("block_following").equals("1");
        this.blockFriendRequests = set.getString("block_friendrequests").equals("1");
        this.blockRoomInvites = set.getString("block_roominvites").equals("1");
        this.preferOldChat = set.getString("old_chat").equals("1");
        this.blockCameraFollow = set.getString("block_camera_follow").equals("1");
        this.guild = set.getInt("guild_id");
        this.guilds = new int[100];
        this.tags = set.getString("tags").split(";");
        this.allowTrade = set.getString("can_trade").equals("1");
        this.votedRooms = new TIntArrayStack();
        this.clubExpireTimestamp = set.getInt("club_expire_timestamp");
        this.loginStreak = set.getInt("login_streak");
        this.rentedItemId = set.getInt("rent_space_id");
        this.rentedTimeEnd = set.getInt("rent_space_endtime");
        this.volumeSystem = set.getInt("volume_system");
        this.volumeFurni = set.getInt("volume_furni");
        this.volumeTrax = set.getInt("volume_trax");
        this.chatColor = RoomChatMessageBubbles.getBubble(set.getInt("chat_color"));
        this.hofPoints = set.getInt("hof_points");
        this.blockStaffAlerts = set.getString("block_alerts").equals("1");

        PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM user_window_settings WHERE user_id = ? LIMIT 1");
        statement.setInt(1, this.habbo.getHabboInfo().getId());
        ResultSet nSet = statement.executeQuery();

        if(nSet.next())
        {
            this.navigatorWindowSettings = new HabboNavigatorWindowSettings(nSet);
        }
        else
        {
            PreparedStatement stmt = Emulator.getDatabase().prepare("INSERT INTO user_window_settings (user_id) VALUES (?)");
            stmt.setInt(1, this.habbo.getHabboInfo().getId());
            stmt.execute();
            stmt.close();
            stmt.getConnection().close();
            this.navigatorWindowSettings = new HabboNavigatorWindowSettings();
        }

        nSet.close();
        statement.close();
        statement.getConnection().close();
    }

    @Override
    public void run()
    {
        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users_settings SET achievement_score = ?, respects_received = ?, respects_given = ?, daily_respect_points = ?, block_following = ?, block_friendrequests = ?, online_time = online_time + ?, guild_id = ?, daily_pet_respect_points = ?, club_expire_timestamp = ?, login_streak = ?, rent_space_id = ?, rent_space_endtime = ?, volume_system = ?, volume_furni = ?, volume_trax = ?, block_roominvites = ?, old_chat = ?, block_camera_follow = ?, chat_color = ?, hof_points = ?, block_alerts = ? WHERE user_id = ? LIMIT 1");
            statement.setInt(1, this.achievementScore);
            statement.setInt(2, this.respectPointsReceived);
            statement.setInt(3, this.respectPointsGiven);
            statement.setInt(4, this.respectPointsToGive);
            statement.setString(5, this.blockFollowing ? "1" : "0");
            statement.setString(6, this.blockFriendRequests ? "1" : "0");
            statement.setInt(7, Emulator.getIntUnixTimestamp() - this.timeLoggedIn);
            statement.setInt(8, this.guild);
            statement.setInt(9, this.petRespectPointsToGive);
            statement.setInt(10, this.clubExpireTimestamp);
            statement.setInt(11, this.loginStreak);
            statement.setInt(12, this.rentedItemId);
            statement.setInt(13, this.rentedTimeEnd);
            statement.setInt(14, this.volumeSystem);
            statement.setInt(15, this.volumeFurni);
            statement.setInt(16, this.volumeTrax);
            statement.setString(17, this.blockRoomInvites ? "1" : "0");
            statement.setString(18, this.preferOldChat ? "1" : "0");
            statement.setString(19, this.blockCameraFollow ? "1" : "0");
            statement.setInt(20, this.chatColor.getType());
            statement.setInt(21, this.hofPoints);
            statement.setString(22, this.blockStaffAlerts ? "1" : "0");
            statement.setInt(23, this.habbo.getHabboInfo().getId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE user_window_settings SET x = ?, y = ?, width = ?, height = ?, open_searches = ? WHERE user_id = ? LIMIT 1");
            statement.setInt(1, this.navigatorWindowSettings.x);
            statement.setInt(2, this.navigatorWindowSettings.y);
            statement.setInt(3, this.navigatorWindowSettings.width);
            statement.setInt(4, this.navigatorWindowSettings.height);
            statement.setString(5, this.navigatorWindowSettings.openSearches ? "1" : "0");
            statement.setInt(6, this.habbo.getHabboInfo().getId());
            statement.execute();
            statement.close();
            statement.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public void dispose()
    {
        this.run();
        this.habbo = null;
        this.recentPurchases.clear();
    }


    private static HabboStats createNewStats(Habbo habbo)
    {
        habbo.firstVisit = true;

        try
        {
            PreparedStatement insertQuery = Emulator.getDatabase().prepare("INSERT INTO users_settings (user_id) VALUES (?)");
            insertQuery.setInt(1, habbo.getHabboInfo().getId());
            insertQuery.execute();
            insertQuery.close();
            insertQuery.getConnection().close();

            return load(habbo);
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        return null;
    }

    public static HabboStats load(Habbo habbo)
    {
        HabboStats stats = null;
        try
        {
            PreparedStatement selectQuery = Emulator.getDatabase().prepare("SELECT * FROM users_settings WHERE user_id = ? LIMIT 1");
            selectQuery.setInt(1, habbo.getHabboInfo().getId());
            ResultSet set = selectQuery.executeQuery();
            set.first();
            if(set.getRow() != 0)
            {
                stats = new HabboStats(set, habbo);
            }
            else
            {
                stats = createNewStats(habbo);
            }
            set.close();
            selectQuery.close();
            selectQuery.getConnection().close();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        if(stats != null)
        {
            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT guild_id FROM guilds_members WHERE user_id = ? LIMIT 100");
                statement.setInt(1, habbo.getHabboInfo().getId());
                ResultSet set = statement.executeQuery();

                int i = 0;
                while(set.next())
                {
                    stats.guilds[i] = set.getInt("guild_id");
                    i++;
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT room_id FROM room_votes WHERE user_id = ?");
                statement.setInt(1, habbo.getHabboInfo().getId());
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    stats.votedRooms.push(set.getInt("room_id"));
                }

                set.close();
                statement.close();
                statement.getConnection().close();
            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }

            try
            {
                PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_achievements WHERE user_id = ?");
                statement.setInt(1, habbo.getHabboInfo().getId());
                ResultSet set = statement.executeQuery();

                while(set.next())
                {
                    stats.achievementProgress.put(Emulator.getGameEnvironment().getAchievementManager().achievements.get(set.getString("achievement_name")), set.getInt("progress"));
                }

                set.close();
                statement.close();
                statement.getConnection().close();

            }
            catch (SQLException e)
            {
                Emulator.getLogging().logSQLException(e);
            }
        }

        return stats;
    }

    public void addGuild(int guildId)
    {
        for(int i = 0; i < this.guilds.length; i++)
        {
            if(this.guilds[i] == 0)
            {
                this.guilds[i] = guildId;
                break;
            }
        }
    }

    public void removeGuild(int guildId)
    {
        for(int i = 0; i < this.guilds.length; i++)
        {
            if(this.guilds[i] == guildId)
            {
                this.guilds[i] = 0;
                break;
            }
        }
    }

    public boolean hasGuild(int guildId)
    {
        for(int i : this.guilds)
        {
            if(i == guildId)
                return true;
        }

        return false;
    }

    public int getAchievementScore()
    {
        return this.achievementScore;
    }

    public void addAchievementScore(int achievementScore)
    {
        this.achievementScore += achievementScore;
    }

    public int getAchievementProgress(Achievement achievement)
    {
        if(this.achievementProgress.containsKey(achievement))
            return this.achievementProgress.get(achievement);

        return -1;
    }

    public void setProgress(Achievement achievement, int progress)
    {
        this.achievementProgress.put(achievement, progress);
    }

    public int getRentedTimeEnd()
    {
        return this.rentedTimeEnd;
    }

    public void setRentedTimeEnd(int rentedTimeEnd)
    {
        this.rentedTimeEnd = rentedTimeEnd;
    }

    public int getRentedItemId()
    {
        return this.rentedItemId;
    }

    public void setRentedItemId(int rentedItemId)
    {
        this.rentedItemId = rentedItemId;
    }

    public boolean canRentSpace()
    {
        return this.rentedTimeEnd < Emulator.getIntUnixTimestamp();
    }

    public int getClubExpireTimestamp()
    {
        return this.clubExpireTimestamp;
    }

    public void setClubExpireTimestamp(int clubExpireTimestamp)
    {
        this.clubExpireTimestamp = clubExpireTimestamp;
    }

    public boolean hasActiveClub()
    {
        return this.clubExpireTimestamp > Emulator.getIntUnixTimestamp();
    }

    public THashMap<Achievement, Integer> getAchievementProgress()
    {
        return this.achievementProgress;
    }

    public THashMap<Achievement, Integer> getAchievementCache()
    {
        return this.achievementCache;
    }

    public void addPurchase(CatalogItem item)
    {
        if(!this.recentPurchases.containsKey(item.getId()))
        {
            this.recentPurchases.put(item.getId(), item);
        }
    }

    public THashMap<Integer, CatalogItem> getRecentPurchases()
    {
        return this.recentPurchases;
    }

    public void disposeRecentPurchases()
    {
        this.recentPurchases.clear();
    }

}
