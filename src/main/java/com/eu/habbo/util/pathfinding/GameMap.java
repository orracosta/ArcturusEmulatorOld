package com.eu.habbo.util.pathfinding;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameMap<T extends AbstractNode>
{
    private static final boolean CANMOVEDIAGONALY = true;
    private final T[][] nodes;
    private final int width;
    private final int height;
    private List<T> openList;
    private List<T> closedList;

    public GameMap(int width, int height)
    {
        this.nodes = (T[][]) new AbstractNode[width][height];
        this.width = (width - 1);
        this.height = (height - 1);
        initEmptyNodes();
    }

    private synchronized void initEmptyNodes()
    {
        for (int i = 0; i <= this.width; i++) {
            for (int j = 0; j <= this.height; j++) {
                this.nodes[i][j] = (T) new Node(i, j);
            }
        }
    }

    public synchronized void setWalkable(int x, int y, boolean bool)
    {
        if(x > this.nodes.length - 1)
            return;

        if(y > this.nodes[x].length - 1)
            return;

        this.nodes[x][y].setWalkable(bool);
    }

    public final T getNode(int x, int y)
    {
        return this.nodes[x][y];
    }

    public synchronized final List<T> getNodes()
    {
        List<T> nodes = new ArrayList<T>();
        for (int x = 0; x < this.nodes.length; x++) {
            for (int y = 0; y < this.nodes[x].length; y++) {
                nodes.add(getNode(x, y));
            }
        }
        return nodes;
    }

    private boolean done = false;

    public synchronized final Queue<T> findPath(int oldX, int oldY, int newX, int newY, Room room)
    {
        if ((oldX == newX) && (oldY == newY)) {
            return new LinkedList();
        }
        this.openList = new LinkedList();
        this.closedList = new LinkedList();

        if(oldX > this.width  ||
           oldY > this.height ||
           newX > this.width  ||
           newY > this.height
                )
            return new LinkedList();

        this.openList.add(this.nodes[oldX][oldY]);

        this.done = false;
        while (!this.done)
        {
            T current = lowestFInOpen();
            this.closedList.add(current);
            this.openList.remove(current);

            if ((current.getX() == newX) && (current.getY() == newY)) {
                return calcPath(this.nodes[oldX][oldY], current, room);
            }
            List<T> adjacentNodes = getAdjacent(current, newX, newY, room);
            for (T currentAdj : adjacentNodes)
            {
                if((Math.abs(room.getLayout().getHeightAtSquare(current.getX(), current.getY()) - room.getLayout().getHeightAtSquare(currentAdj.getX(), currentAdj.getY())) > 1))
                    continue;

                if (!this.openList.contains(currentAdj) || (currentAdj.getX() == newX && currentAdj.getY() == newY && (canWalkOntoFurniture(room, oldX, oldY, newX, newY))))
                {
                    currentAdj.setPrevious(current);
                    currentAdj.sethCosts(this.nodes[newX][newY]);
                    currentAdj.setgCosts(current);
                    this.openList.add(currentAdj);
                } else if (currentAdj.getgCosts() > currentAdj.calculategCosts(current))
                {
                    currentAdj.setPrevious(current);
                    currentAdj.setgCosts(current);
                }
            }
            if (this.openList.isEmpty()) {
                return new LinkedList();
            }
        }
        return null;
    }

    private Queue<T> calcPath(T start, T goal, Room room)
    {
        LinkedList<T> path = new LinkedList();

        T curr = goal;
        boolean done = false;
        while (!done) {
            if (curr != null)
            {
                path.addFirst(curr);
                curr = getNode(curr.getPrevious().getX(), curr.getPrevious().getY());
                //curr.getPrevious();
                if ((curr != null) && (start != null) && (curr.equals(start))) {
                    done = true;
                }
            }
        }
        return path;
    }

    private synchronized T lowestFInOpen()
    {
        if(this.openList == null)
            return null;

        T cheapest = this.openList.get(0);
        for (T anOpenList : this.openList)
        {
            if (anOpenList.getfCosts() < cheapest.getfCosts())
            {
                cheapest = anOpenList;
            }
        }
        return cheapest;
    }

    private boolean canWalkOntoFurniture(Room room, int x, int y, int newX, int newY)
    {
        HabboItem oldTopItem = room.getTopItemAt(x, y);
        HabboItem newTopItem = room.getTopItemAt(newX, newY);

        if (newTopItem != null)
        {
            if (newTopItem.getBaseItem().allowSit() || newTopItem.getBaseItem().allowLay())
            {
                return false;
            }

            if (oldTopItem != null)
            {
                if (Math.abs((oldTopItem.getZ() + Item.getCurrentHeight(oldTopItem)) - (newTopItem.getZ() + Item.getCurrentHeight(newTopItem))) > 1.5)
                {
                    return false;
                }
            }
        }

        return true;
    }

    private synchronized List<T> getAdjacent(T node, int newX, int newY, Room room)
    {
        int x = node.getX();
        int y = node.getY();
        List<T> adj = new LinkedList<T>();
        if (x > 0)
        {
            T temp = getNode(x - 1, y);
            if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (x < this.width)
        {
            T temp = getNode(x + 1, y);
            if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (y > 0)
        {
            T temp = getNode(x, y - 1);
            if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (y < this.height)
        {
            T temp = getNode(x, y + 1);
            if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (CANMOVEDIAGONALY)
        {
            if ((x < this.width) && (y < this.height))
            {
                T temp = getNode(x + 1, y + 1);
                if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y > 0))
            {
                T temp = getNode(x - 1, y - 1);
                if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y < this.height))
            {
                T temp = getNode(x - 1, y + 1);
                if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x < this.width) && (y > 0))
            {
                T temp = getNode(x + 1, y - 1);
                if (((temp.isWalkable()) && (!this.closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && !canWalkOntoFurniture(room, x, y, newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
        }
        return adj;
    }

    public void finalize()
            throws Throwable
    {
        super.finalize();
    }
}
