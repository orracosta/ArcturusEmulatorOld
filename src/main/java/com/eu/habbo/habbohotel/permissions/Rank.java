package com.eu.habbo.habbohotel.permissions;

import gnu.trove.map.hash.THashMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Rank
{
    private final int id;
    private final int level;
    private String name;
    private final THashMap<String, Permission> permissions;
    private final THashMap<String, String> variables;
    private int roomEffect;
    private boolean logCommands;
    private String prefix;
    private String prefixColor;
    private boolean hasPrefix;

    public Rank(ResultSet set) throws SQLException
    {
        this.permissions = new THashMap<>();
        this.variables = new THashMap<>();
        this.id = set.getInt("id");
        this.level = set.getInt("level");

        this.load(set);
    }

    public void load(ResultSet set) throws SQLException
    {
        ResultSetMetaData meta = set.getMetaData();
        this.name = set.getString("rank_name");
        this.roomEffect = set.getInt("room_effect");
        this.logCommands = set.getString("log_commands").equals("1");
        this.prefix = set.getString("prefix");
        this.prefixColor = set.getString("prefix_color");
        this.hasPrefix = !this.prefix.isEmpty();
        for (int i = 1; i < meta.getColumnCount() + 1; i++)
        {
            String columnName = meta.getColumnName(i);
            if (columnName.startsWith("cmd_") || columnName.startsWith("acc_"))
            {
                if (set.getString(i).equals("1") || set.getString(i).equals("2"))
                {
                    this.permissions.put(meta.getColumnName(i), new Permission(columnName, PermissionSetting.fromString(set.getString(i))));
                }
            }
            else
            {
                variables.put(meta.getColumnName(i), set.getString(i));
            }
        }
    }

    public boolean hasPermission(String key, boolean isRoomOwner)
    {
        if (this.permissions.containsKey(key))
        {
            Permission permission = this.permissions.get(key);

            return permission.setting == PermissionSetting.ALLOWED || permission.setting == PermissionSetting.ROOM_OWNER && isRoomOwner;

        }

        return false;
    }

    public int getId()
    {
        return this.id;
    }

    public int getLevel()
    {
        return this.level;
    }

    public String getName()
    {
        return this.name;
    }

    public THashMap<String, Permission> getPermissions()
    {
        return this.permissions;
    }

    public THashMap<String, String> getVariables()
    {
        return this.variables;
    }

    public int getRoomEffect()
    {
        return this.roomEffect;
    }

    public boolean isLogCommands()
    {
        return this.logCommands;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public String getPrefixColor()
    {
        return this.prefixColor;
    }

    public boolean hasPrefix()
    {
        return this.hasPrefix;
    }
}
