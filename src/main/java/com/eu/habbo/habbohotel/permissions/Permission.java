package com.eu.habbo.habbohotel.permissions;

public class Permission
{
    public final String key;
    public final PermissionSetting setting;

    public Permission(String key, PermissionSetting setting)
    {
        this.key = key;
        this.setting = setting;
    }
}
