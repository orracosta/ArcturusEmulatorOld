package com.eu.habbo.habbohotel.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogItem;
import com.eu.habbo.habbohotel.games.Game;
import com.eu.habbo.habbohotel.games.GamePlayer;
import com.eu.habbo.habbohotel.pets.HorsePet;
import com.eu.habbo.habbohotel.rooms.Room;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class HabboInfo implements Runnable
{
    private String username;
    private String realName;
    private String motto;
    private String look;
    private HabboGender gender;
    private String mail;
    private String sso;
    private String ipRegister;

    private int id;
    private int accountCreated;
    private int achievementScore;
    private int rank;

    private int credits;
    private int lastOnline;

    private int homeRoom;

    private boolean online;
    private int loadingRoom;
    private Room currentRoom;
    private int roomQueueId;

    private HorsePet riding;

    private Class<? extends Game> currentGame;
    private TIntIntHashMap currencies;
    private GamePlayer gamePlayer;

    public HabboInfo(ResultSet set)
    {
        try
        {
            this.id = set.getInt("id");
            this.username = set.getString("username");
            this.realName = set.getString("real_name");
            this.motto = set.getString("motto");
            this.look = set.getString("look");
            this.gender = HabboGender.valueOf(set.getString("gender"));
            this.mail = set.getString("mail");
            this.sso = set.getString("auth_ticket");
            this.ipRegister = set.getString("ip_register");
            this.rank = set.getInt("rank");
            this.accountCreated = set.getInt("account_created");
            this.credits = set.getInt("credits");
            this.homeRoom = set.getInt("home_room");
            this.lastOnline = Emulator.getIntUnixTimestamp();
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

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("SELECT * FROM users_currency WHERE user_id = ?");
            statement.setInt(1, this.id);
            ResultSet set = statement.executeQuery();

            while(set.next())
            {
                this.currencies.put(set.getInt("type"), set.getInt("amount"));
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

    private void saveCurrencies()
    {
        try
        {
            final PreparedStatement statement = Emulator.getDatabase().prepare("INSERT INTO users_currency (user_id, type, amount) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = ? ");

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
                        statement.execute();
                    }
                    catch (SQLException e)
                    {
                        Emulator.getLogging().logSQLException(e);
                    }

                    return true;
                }
            });

            statement.close();
            statement.getConnection().close();
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
    }

    public void setCurrencyAmount(int type, int amount)
    {
        this.currencies.put(type, amount);
    }

    public int getId()
    {
        return this.id;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getRealName()
    {
        return this.realName;
    }

    public String getMotto()
    {
        return this.motto;
    }

    public void setMotto(String motto)
    {
        this.motto = motto;
    }

    public int getRank()
    {
        return this.rank;
    }

    public void setRank(int rank) {
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

    public int getAccountCreated()
    {
        return this.accountCreated;
    }

    public void setAccountCreated(int accountCreated) {
        this.accountCreated = accountCreated;
    }

    public boolean canBuy(CatalogItem item)
    {
        if(this.credits < item.getCredits())
            return false;

        if(this.getCurrencies().get(item.getPointsType()) < item.getPoints())
            return false;

        return true;
    }

    public int getCredits()
    {
        return credits;
    }

    public void setCredits(int credits)
    {
        this.credits = credits;
    }

    public void addCredits(int credits)
    {
        this.credits += credits;
    }

    public int getPixels()
    {
        return this.getCurrencyAmount(0);
    }

    public void setPixels(int pixels)
    {
        this.setCurrencyAmount(0, pixels);
    }

    public void addPixels(int pixels)
    {
        this.addCurrencyAmount(0, pixels);
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

    @Override
    public void run()
    {
        this.saveCurrencies();

        try
        {
            PreparedStatement statement = Emulator.getDatabase().prepare("UPDATE users SET motto = ?, online = ?, look = ?, gender = ?, credits = ?, last_login = ?, last_online = ?, home_room = ? WHERE id = ?");
            statement.setString(1, this.motto);
            statement.setString(2, this.online ? "1" : "0");
            statement.setString(3, this.look);
            statement.setString(4, this.gender.name());
            statement.setInt(5, this.credits);
            statement.setInt(7, Emulator.getIntUnixTimestamp());
            statement.setInt(6, this.lastOnline);
            statement.setInt(8, this.homeRoom);
            statement.setInt(9, this.id);
            statement.execute();
            statement.close();
            statement.getConnection().close();
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
