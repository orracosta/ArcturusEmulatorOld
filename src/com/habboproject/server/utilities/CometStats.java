package com.habboproject.server.utilities;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.game.rooms.RoomManager;
import com.habboproject.server.network.NetworkManager;


public class CometStats {
    private int players;
    private int rooms;
    private String uptime;

    private int processId;
    private long allocatedMemory;
    private long usedMemory;
    private String operatingSystem;
    private int cpuCores;

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public long getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(long allocatedMemory) {
        this.allocatedMemory = allocatedMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public static CometStats get() {
        CometStats statsInstance = new CometStats();

        statsInstance.setPlayers(NetworkManager.getInstance().getSessions().getUsersOnlineCount());
        statsInstance.setRooms(RoomManager.getInstance().getRoomInstances().size());
        statsInstance.setUptime(TimeSpan.millisecondsToDate(System.currentTimeMillis() - Comet.start));

        statsInstance.setProcessId(CometRuntime.processId);
        statsInstance.setAllocatedMemory((Runtime.getRuntime().totalMemory() / 1024) / 1024);
        statsInstance.setUsedMemory(statsInstance.getAllocatedMemory() - (Runtime.getRuntime().freeMemory() / 1024) / 1024);
        statsInstance.setOperatingSystem(CometRuntime.operatingSystem + " (" + CometRuntime.operatingSystemArchitecture + ")");
        statsInstance.setCpuCores(Runtime.getRuntime().availableProcessors());

        return statsInstance;
    }
}
