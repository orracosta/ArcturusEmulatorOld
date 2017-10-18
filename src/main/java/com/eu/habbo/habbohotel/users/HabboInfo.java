package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.permissions.Rank;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.rooms.Room;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HabboInfo implements Runnable
{
    private String username;
    private String motto;
    private String look;
    private HabboGender gender;
    private String mail;
    private String sso;
    private String ipRegister;
    private String ipLogin;

    private int id;
    private int accountCreated;
    private int achievementScore;
    private Rank rank;

    private int credits;
    private int lastOnline;
    private int lastLogin;

    private int homeRoom;

    private boolean online;
    private int loadingRoom;
    private Room currentRoom;
    private int roomQueueId;

    private HorsePet riding;

    private Class<? extends Game> currentGame;
    private TIntIntHashMap currencies;
    private GamePlayer gamePlayer;

    private int photoRoomId;
    private int photoTimestamp;
    private String photoURL;
    private String photoJSON;
    private int webPublishTimestamp;
    private String machineID;

    public HabboInfo(ResultSet set)
    {
        try
        {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.motto = set.getString("motto");
            this.look = set.getString("look");
            this.gender = HabboGender.valueOf(set.getString("gender"));
            this.mail = set.getString("mail");
            this.sso = set.getString("auth_ticket");
            this.ipRegister = set.getString("ip_register");
            this.ipLogin = set.getString("ip_current");
            this.rank = Emulator.getGameEnvironment().getPermissionsManager().getRank(set.getInt("rank"));

            if (this.rank == null)
            {
                Emulator.getLogging().logErrorLine("No existing rank found with id " + set.getInt("rank") + ". Make sure an entry in the permissions table exists.");
            }

            this.accountCreated = set.getInt("account_created");
            this.credits = set.getInt("credits");
            this.homeRoom = set.getInt("home_room");
            this.lastOnline = set.getInt("last_online");
            this.machineID = set.getString("machine_id");
            this.lastLogin = Emulator.getIntUnixTimestamp();
            this.online = false;
            this.currentRoom = null;
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }

        this.loadCurrencies();
    }

    private void loadCurrencies()
    {
        this.currencies = new TIntIntHashMap();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM users_currency WHERE user_id = ?"))
        {
            statement.setInt(1, this.id);
            try (ResultSet set = statement.executeQuery())
            {
                while (set.next())
                {
                    this.currencies.put(set.getInt("type"), set.getInt("amount"));
                }
            }
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    private void saveCurrencies()
    {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO users_currency (user_id, type, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = ?"))
        {
            this.currencies.forEachEntry(new TIntIntProcedure()
            {
                @Override
                public boolean execute(int a, int b)
                {
                    try
                    {
                        statement.setInt(1, getId());
                        statement.setInt(2, a);
                        statement.setInt(3, b);
                        statement.setInt(4, b);
                        statement.addBatch();
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }
                    return true;
                }
            });
            statement.executeBatch();
        }
        catch (SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public int getCurrencyAmount(int type)
    {
        return this.currencies.get(type);
    }

    public TIntIntHashMap getCurrencies()
    {
        return this.currencies;
    }

    public void addCurrencyAmount(int type, int amount)
    {
        this.currencies.adjustOrPutValue(type, amount, amount);
        this.run();
    }

    public void setCurrencyAmount(int type, int amount)
    {
        this.currencies.put(type, amount);
        this.run();
    }

    public int getId()
    {
        return this.id;
    }

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getMotto()
    {
        return this.motto;
    }

    public void setMotto(String motto)
    {
        this.motto = motto;
    }

    public Rank getRank()
    {
        return this.rank;
    }

    public void setRank(Rank rank)
    {
        this.rank = rank;
    }

    public String getLook()
    {
        return this.look;
    }

    public void setLook(String look) { this.look = look; }

    public HabboGender getGender()
    {
        return this.gender;
    }

    public void setGender(HabboGender gender) {
        this.gender = gender;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getIpRegister() {
        return ipRegister;
    }

    public void setIpRegister(String ipRegister) {
        this.ipRegister = ipRegister;
    }

    public String getIpLogin()
    {
        return this.ipLogin;
    }

    public void setIpLogin(String ipLogin)
    {
        this.ipLogin = ipLogin;
    }

    public int getAccountCreated()
    {
        return this.accountCreated;
    }

    public void setAccountCreated(int accountCreated) {
        this.accountCreated = accountCreated;
    }

    public boolean canBuy(CatalogItem item)
    {
        return this.credits >= item.getCredits() && this.getCurrencies().get(item.getPointsType()) >= item.getPoints();
    }

    public int getCredits()
    {
        return credits;
    }

    public void setCredits(int credits)
    {
        this.credits = credits;
        this.run();
    }

    public void addCredits(int credits)
    {
        this.credits += credits;
        this.run();
    }

    public int getPixels()
    {
        return this.getCurrencyAmount(0);
    }

    public void setPixels(int pixels)
    {
        this.setCurrencyAmount(0, pixels);
        this.run();
    }

    public void addPixels(int pixels)
    {
        this.addCurrencyAmount(0, pixels);
        this.run();
    }

    public int getLastOnline()
    {
        return this.lastOnline;
    }

    public void setLastOnline(int lastOnline)
    {
        this.lastOnline = lastOnline;
    }

    public int getHomeRoom()
    {
        return this.homeRoom;
    }

    public void setHomeRoom(int homeRoom)
    {
        this.homeRoom = homeRoom;
    }

    public boolean isOnline()
    {
        return this.online;
    }

    public void setOnline(boolean value)
    {
        this.online = value;
    }

    public int getLoadingRoom()
    {
        return this.loadingRoom;
    }

    public void setLoadingRoom(int loadingRoom)
    {
        this.loadingRoom = loadingRoom;
    }

    public Room getCurrentRoom()
    {
        return this.currentRoom;
    }

    public void setCurrentRoom(Room room)
    {
        this.currentRoom = room;
    }

    public int getRoomQueueId()
    {
        return this.roomQueueId;
    }

    public void setRoomQueueId(int roomQueueId)
    {
        this.roomQueueId = roomQueueId;
    }

    public HorsePet getRiding()
    {
        return riding;
    }

    public void setRiding(HorsePet riding)
    {
        this.riding = riding;
    }

    public Class<? extends Game> getCurrentGame()
    {
        return this.currentGame;
    }

    public void setCurrentGame(Class<? extends Game> currentGame)
    {
        this.currentGame = currentGame;
    }

    public boolean isInGame()
    {
        return this.currentGame != null;
    }

    public synchronized GamePlayer getGamePlayer()
    {
        return this.gamePlayer;
    }

    public synchronized void setGamePlayer(GamePlayer gamePlayer)
    {
        this.gamePlayer = gamePlayer;
    }

    public int getPhotoRoomId()
    {
        return this.photoRoomId;
    }

    public void setPhotoRoomId(int roomId)
    {
        this.photoRoomId = roomId;
    }

    public int getPhotoTimestamp()
    {
        return this.photoTimestamp;
    }

    public void setPhotoTimestamp(int photoTimestamp)
    {
        this.photoTimestamp = photoTimestamp;
    }

    public String getPhotoURL()
    {
        return this.photoURL;
    }

    public void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }

    public String getPhotoJSON()
    {
        return this.photoJSON;
    }

    public void setPhotoJSON(String photoJSON)
    {
        this.photoJSON = photoJSON;
    }

    public int getWebPublishTimestamp()
    {
        return this.webPublishTimestamp;
    }

    public void setWebPublishTimestamp(int webPublishTimestamp)
    {
        this.webPublishTimestamp = webPublishTimestamp;
    }

    public String getMachineID()
    {
        return this.machineID;
    }

    public void setMachineID(String machineID)
    {
        this.machineID = machineID;
    }

    @Override
    public void run()
    {
        this.saveCurrencies();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE users SET motto = ?, online = ?, look = ?, gender = ?, credits = ?, last_login = ?, last_online = ?, home_room = ?, ip_current = ?, rank = ?, machine_id = ?, username = ? WHERE id = ?"))
        {
            statement.setString(1, this.motto);
            statement.setString(2, this.online ? "1" : "0");
            statement.setString(3, this.look);
            statement.setString(4, this.gender.name());
            statement.setInt(5, this.credits);
            statement.setInt(7, this.lastOnline);
            statement.setInt(6, Emulator.getIntUnixTimestamp());
            statement.setInt(8, this.homeRoom);
            statement.setString(9, this.ipLogin);
            statement.setInt(10, this.rank.getId());
            statement.setString(11, this.machineID);
            statement.setString(12, this.username);
            statement.setInt(13, this.id);
            statement.executeUpdate();
        }
        catch(SQLException e)
        {
            Emulator.getLogging().logSQLException(e);
        }
    }

    public int getBonusRarePoints()
    {
        return this.getCurrencyAmount(Emulator.getConfig().getInt("hotelview.promotional.points.type"));
    }
}
