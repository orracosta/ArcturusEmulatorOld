package com.eu.habbo.habbohotel.rooms;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionGuildGate;
import com.eu.habbo.habbohotel.items.interactions.InteractionMultiHeight;
import com.eu.habbo.habbohotel.items.interactions.InteractionTeleport;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.users.DanceType;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserStatusComposer;
import com.eu.habbo.plugin.Event;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitSetGoalEvent;
import com.eu.habbo.plugin.events.users.UserIdleEvent;
import com.eu.habbo.plugin.events.users.UserTakeStepEvent;
import com.eu.habbo.threading.runnables.RoomUnitKick;
import com.eu.habbo.util.pathfinding.Rotation;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;

import java.util.Deque;
import java.util.LinkedList;

public class RoomUnit
{
    private int id;
    private RoomTile startLocation;
    private RoomTile previousLocation;
    private RoomTile currentLocation;
    private RoomTile goalLocation;

    private double z;

    private int tilesWalked;

    private boolean inRoom;
    private boolean canWalk;
    private boolean fastWalk = false;
    public boolean animateWalk = false;
    public boolean cmdTeleport = false;
    public boolean cmdSit = false;
    public boolean cmdLay = false;
    public boolean sitUpdate = false;
    public boolean isTeleporting = false;
    public boolean isKicked = false;

    private final THashMap<String, String> status;
    private final THashMap<String, Object> cacheable;
    private RoomUserRotation bodyRotation = RoomUserRotation.NORTH;
    private RoomUserRotation headRotation = RoomUserRotation.NORTH;
    private DanceType danceType;
    private RoomUnitType roomUnitType;
    private Deque<RoomTile> path = new LinkedList<>();
    private int handItem;
    private long handItemTimestamp;
    private int walkTimeOut;
    private int effectId;

    private int idleTimer;
    private Room room;
    private RoomRightLevels rightsLevel = RoomRightLevels.NONE;

    public RoomUnit()
    {
        this.id = 0;
        this.inRoom = false;
        this.canWalk = true;
        this.status = new THashMap<String, String>();
        this.cacheable = new THashMap<String, Object>();
        this.roomUnitType = RoomUnitType.UNKNOWN;
        this.bodyRotation = RoomUserRotation.NORTH;
        this.bodyRotation = RoomUserRotation.NORTH;
        this.danceType = DanceType.NONE;
        this.handItem = 0;
        this.handItemTimestamp = 0;
        this.walkTimeOut = Emulator.getIntUnixTimestamp();
        this.effectId = 0;
        this.isKicked = false;
    }

    public void clearWalking()
    {
        this.goalLocation = null;
        this.startLocation = this.currentLocation;
        this.inRoom = false;

        synchronized (this.status)
        {
            this.status.clear();
        }

        this.cacheable.clear();
    }

    public void stopWalking()
    {
        synchronized (this.status)
        {
            this.status.remove("mv");
            this.setGoalLocation(this.currentLocation);
        }
    }

    public boolean cycle(Room room)
    {
        try
        {
            /**
             * !this.status.containsKey("mv") &&
             */
            if (!this.isWalking() && !isKicked)
            {
                if (this.status.remove("mv") == null)
                {
                    return true;
                }
            }

            if (this.status.containsKey("mv"))
            {
                this.status.remove("mv");
            }
            if (this.status.containsKey("lay"))
            {
                this.status.remove("lay");
            }
            if (this.status.containsKey("sit"))
            {
                this.status.remove("sit");
            }

            if (this.path == null || this.path.isEmpty())
                return true;

            if (this.fastWalk && this.path.size() >= 3)
            {
                this.path.poll();
                this.path.poll();
            }

            RoomTile next = this.path.poll();

            if (this.path.isEmpty())
            {
                this.sitUpdate = true;

                if (room.hasHabbosAt(next.x, next.y))
                {
                    return false;
                }
            }

            Deque<RoomTile> peekPath = room.getLayout().findPath(this.currentLocation, this.path.peek());
            if (peekPath.size() >= 3)
            {
                peekPath.pop(); //Start
                peekPath.removeLast(); //End

                if (peekPath.peek() != next)
                {
                    next = peekPath.poll();
                    for (int i = 0; i < peekPath.size(); i++)
                    {
                        this.path.addFirst(peekPath.removeLast());
                    }
                }
            }

            if (next == null)
                return true;

            Habbo habbo = room.getHabbo(this);

            if (this.status.containsKey("ded"))
            {
                this.status.remove("ded");
            }

            if (habbo != null)
            {
                if (this.isIdle())
                {
                    UserIdleEvent event = new UserIdleEvent(habbo, UserIdleEvent.IdleReason.WALKED, false);
                    Emulator.getPluginManager().fireEvent(event);

                    if (!event.isCancelled())
                    {
                        if (!event.idle)
                        {
                            room.unIdle(habbo);
                            this.idleTimer = 0;
                        }
                    }
                }

                if (Emulator.getPluginManager().isRegistered(UserTakeStepEvent.class, false))
                {
                    Event e = new UserTakeStepEvent(habbo, room.getLayout().getTile(this.getX(), this.getY()), next);
                    Emulator.getPluginManager().fireEvent(e);

                    if (e.isCancelled())
                        return true;
                }
            }

            HabboItem item = room.getTopItemAt(next.x, next.y);


            //if(!(this.path.size() == 0 && canSitNextTile))
            {
                if (!room.tileWalkable(next.x, next.y) && !(item instanceof InteractionTeleport))
                {
                    this.room = room;
                    this.findPath();

                    if (!this.path.isEmpty())
                    {
                        next = this.path.pop();
                        item = room.getTopItemAt(next.x, next.y);
                    }
                    else
                    {
                        this.status.remove("mv");
                        return false;
                    }

                }
            }

            boolean canSitNextTile = room.canSitAt(next.x, next.y);

            if (canSitNextTile)
            {
                HabboItem lowestChair = room.getLowestChair(next);

                if (lowestChair != null)
                    item = lowestChair;
            }

            double zHeight = 0.0D;

            if (habbo != null)
            {
                if (habbo.getHabboInfo().getRiding() != null)
                {
                    zHeight += 1.0D;
                }
            }

            HabboItem habboItem = room.getTopItemAt(this.getX(), this.getY());
            if (habboItem != null)
            {
                if (habboItem != item || !RoomLayout.pointInSquare(habboItem.getX(), habboItem.getY(), habboItem.getX() + habboItem.getBaseItem().getWidth() - 1, habboItem.getY() + habboItem.getBaseItem().getLength() - 1, next.x, next.y))
                    habboItem.onWalkOff(this, room, null);
            }

            this.tilesWalked++;

            RoomUserRotation oldRotation = this.getBodyRotation();
            this.setRotation(RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), next.x, next.y)]);
            if (item != null)
            {
                if (item != habboItem || !RoomLayout.pointInSquare(item.getX(), item.getY(), item.getX() + item.getBaseItem().getWidth() - 1, item.getY() + item.getBaseItem().getLength() - 1, this.getX(), this.getY()))
                {
                    if (item.canWalkOn(this, room, null))
                    {
                        item.onWalkOn(this, room, null);
                    }
                    else if (item instanceof InteractionGuildGate)
                    {
                        this.setRotation(oldRotation);
                        this.tilesWalked--;
                        this.setGoalLocation(this.currentLocation);
                        this.status.remove("mv");
                        room.sendComposer(new RoomUserStatusComposer(this).compose());
                        return false;
                    }
                }
                else
                {
                    item.onWalk(this, room, null);
                }

                zHeight += item.getZ();

                if (!item.getBaseItem().allowSit() && !item.getBaseItem().allowLay())
                {
                    zHeight += item.getBaseItem().getHeight();

                    if (item instanceof InteractionMultiHeight)
                    {
                        if (item.getExtradata().length() == 0)
                        {
                            item.setExtradata("0");
                        }
                        zHeight += Item.getCurrentHeight(item);
                    }
                    else if (item instanceof InteractionFreezeBlock)
                    {
                        zHeight -= item.getBaseItem().getHeight();
                    }
                }
            }
            else
            {
                zHeight += room.getLayout().getHeightAtSquare(next.x, next.y);
            }

            this.previousLocation = this.currentLocation;
            this.status.put("mv", next.x + "," + next.y + "," + zHeight);
            if (habbo != null)
            {
                if (habbo.getHabboInfo().getRiding() != null)
                {
                    RoomUnit ridingUnit = habbo.getHabboInfo().getRiding().getRoomUnit();

                    if (ridingUnit != null)
                    {
                        ridingUnit.getStatus().put("mv", next.x + "," + next.y + "," + (zHeight - 1.0));
                    }
                }
            }
            //room.sendComposer(new RoomUserStatusComposer(this).compose());

            this.setZ(zHeight);
            this.setCurrentLocation(room.getLayout().getTile((short) next.x, (short) next.y));
            this.resetIdleTimer();

            if (habbo != null)
            {
                if (next.x == room.getLayout().getDoorX() && next.y == room.getLayout().getDoorY() && (!room.isPublicRoom()) || (room.isPublicRoom() && Emulator.getConfig().getBoolean("hotel.room.public.doortile.kick")))
                {
                    Emulator.getThreading().run(new RoomUnitKick(habbo, room, false), 500);
                }
            }

            return false;

        }
        catch (Exception e)
        {
            Emulator.getLogging().logErrorLine(e);
            return false;
        }
    }
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public RoomTile getCurrentLocation()
    {
        return this.currentLocation;
    }

    public short getX()
    {
        return this.currentLocation.x;
    }

    public short getY()
    {
        return this.currentLocation.y;
    }

    public double getZ()
    {
        return this.z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public boolean isInRoom() {
        return inRoom;
    }

    public synchronized void setInRoom(boolean inRoom) {
        this.inRoom = inRoom;
    }

    public RoomUnitType getRoomUnitType() {
        return roomUnitType;
    }

    public synchronized void setRoomUnitType(RoomUnitType roomUnitType) {
        this.roomUnitType = roomUnitType;
    }

    public void setRotation(RoomUserRotation rotation)
    {
        this.bodyRotation = rotation;
        this.headRotation = rotation;
    }

    public RoomUserRotation getBodyRotation() {
        return bodyRotation;
    }

    public void setBodyRotation(RoomUserRotation bodyRotation) {
        this.bodyRotation = bodyRotation;
    }

    public RoomUserRotation getHeadRotation() {
        return headRotation;
    }

    public void setHeadRotation(RoomUserRotation headRotation)
    {
        this.headRotation = headRotation;
    }

    public DanceType getDanceType() {
        return danceType;
    }

    public synchronized void setDanceType(DanceType danceType) {
        this.danceType = danceType;
    }

    public void setCanWalk(boolean value)
    {
        this.canWalk = value;
    }

    public boolean canWalk()
    {
        return this.canWalk;
    }

    public void setFastWalk(boolean fastWalk)
    {
        this.fastWalk = fastWalk;
    }

    public boolean isFastWalk()
    {
        return this.fastWalk;
    }

    public RoomTile getStartLocation()
    {
        return this.startLocation;
    }

    public int tilesWalked()
    {
        return this.tilesWalked;
    }

    public RoomTile getGoal()
    {
        return this.goalLocation;
    }

    public void setGoalLocation(RoomTile goalLocation)
    {
        this.setGoalLocation(goalLocation, false);
    }

    public void setGoalLocation(RoomTile goalLocation, boolean noReset)
    {
        if (Emulator.getPluginManager().isRegistered(RoomUnitSetGoalEvent.class, false))
        {
            Event event = new RoomUnitSetGoalEvent(this.room, this, goalLocation);
            Emulator.getPluginManager().fireEvent(event);

            if (event.isCancelled())
                return;
        }

        this.startLocation = this.currentLocation;

        if (goalLocation != null && !noReset)
        {
            this.goalLocation = goalLocation;
            this.tilesWalked = 0;
            this.findPath();
            this.cmdSit = false;
        }
    }

    public void setLocation(RoomTile location)
    {
        if (location != null)
        {
            this.startLocation    = location;
            this.previousLocation = location;
            this.currentLocation  = location;
            this.goalLocation     = location;
        }
    }

    public void setCurrentLocation(RoomTile location)
    {
        if (location != null)
        {
            this.currentLocation = location;
        }
    }

    public RoomTile getPreviousLocation()
    {
        return this.previousLocation;
    }

    public void setPreviousLocation(RoomTile previousLocation)
    {
        this.previousLocation = previousLocation;
    }

    public void setPathFinderRoom(Room room)
    {
        this.room = room;
    }

    public void findPath()
    {
        if (this.room != null && this.room.getLayout() != null && this.goalLocation != null && (this.goalLocation.isWalkable() || this.room.canSitOrLayAt(this.goalLocation.x, this.goalLocation.y)))
        {
            this.path = this.room.getLayout().findPath(this.currentLocation, this.goalLocation);
        }
    }

    public boolean isAtGoal()
    {
        return this.currentLocation.equals(this.goalLocation);
    }

    public boolean isWalking()
    {
        return !isAtGoal() && this.canWalk;
    }

    public synchronized THashMap<String, String> getStatus()
    {
        return this.status;
    }

    public synchronized boolean hasStatus(String key)
    {
        return this.status.containsKey(key);
    }

    public TMap<String, Object> getCacheable()
    {
        return this.cacheable;
    }

    public int getHandItem()
    {
        return this.handItem;
    }

    public void setHandItem(int handItem)
    {
        this.handItem = handItem;
        this.handItemTimestamp = System.currentTimeMillis();
    }

    public long getHandItemTimestamp()
    {
        return this.handItemTimestamp;
    }

    public int getEffectId()
    {
        return this.effectId;
    }

    public void setEffectId(int effectId)
    {
        this.effectId = effectId;
    }

    public int getWalkTimeOut()
    {
        return this.walkTimeOut;
    }

    public void setWalkTimeOut(int walkTimeOut)
    {
        this.walkTimeOut = walkTimeOut;
    }

    public void increaseIdleTimer()
    {
        this.idleTimer++;
    }

    public boolean isIdle()
    {
        return this.idleTimer > Room.IDLE_CYCLES; //Amount of room cycles / 2 = seconds.
    }

    public int getIdleTimer()
    {
        return this.idleTimer;
    }

    public void resetIdleTimer()
    {
        this.idleTimer = 0;
    }

    public void setIdle()
    {
        this.idleTimer = Room.IDLE_CYCLES + 1;
    }

    public void lookAtPoint(RoomTile location)
    {
        if(Emulator.getPluginManager().isRegistered(RoomUnitLookAtPointEvent.class, false))
        {
            Event lookAtPointEvent = new RoomUnitLookAtPointEvent(this.room, this, location);
            Emulator.getPluginManager().fireEvent(lookAtPointEvent);

            if(lookAtPointEvent.isCancelled())
                return;
        }

        if (this.status.containsKey("lay"))
        {
            return;
        }

        if (!this.status.containsKey("sit"))
        {
            this.bodyRotation = (RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), location.x, location.y)]);
        }

        RoomUserRotation rotation = (RoomUserRotation.values()[Rotation.Calculate(this.getX(), this.getY(), location.x, location.y)]);

        if (Math.abs(rotation.getValue() - this.bodyRotation.getValue()) <= 1)
        {
            this.headRotation = rotation;
        }
    }

    public Deque<RoomTile> getPath()
    {
        return path;
    }

    public RoomRightLevels getRightsLevel()
    {
        return rightsLevel;
    }

    public void setRightsLevel(RoomRightLevels rightsLevel)
    {
        this.rightsLevel = rightsLevel;
    }
}
