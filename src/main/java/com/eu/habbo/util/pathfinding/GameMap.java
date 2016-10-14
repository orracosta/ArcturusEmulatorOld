package com.eu.habbo.util.pathfinding;

import com.eu.habbo.habbohotel.rooms.Room;

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

    public GameMap(int width, int height)
    {
        this.nodes = (T[][]) new AbstractNode[width][height];
        this.width = (width - 1);
        this.height = (height - 1);
        initEmptyNodes();
    }

    private void initEmptyNodes()
    {
        for (int i = 0; i <= this.width; i++) {
            for (int j = 0; j <= this.height; j++) {
                this.nodes[i][j] = (T) new Node(i, j);
            }
        }
    }

    public void setWalkable(int x, int y, boolean bool)
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

    public final List<T> getNodes()
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

    public final Queue<T> findPath(int oldX, int oldY, int newX, int newY, Room room)
    {
        if ((oldX == newX) && (oldY == newY)) {
            return new LinkedList();
        }


        List<T> openList = new LinkedList();
        List<T> closedList = new LinkedList();

        if(oldX > this.width  ||
                oldY > this.height ||
                newX > this.width  ||
                newY > this.height
                )
            return new LinkedList();

        openList.add(this.nodes[oldX][oldY]);

        this.done = false;
        while (!this.done)
        {
            T current = lowestFInOpen(openList);
            closedList.add(current);
            openList.remove(current);

            if ((current.getX() == newX) && (current.getY() == newY)) {
                return calcPath(this.nodes[oldX][oldY], current, room);
            }
            List<T> adjacentNodes = getAdjacent(closedList, current, newX, newY, room);
            for (T currentAdj : adjacentNodes)
            {
                if (!room.isLoaded())
                    continue;

                if(!room.getLayout().tileWalkable((short)currentAdj.getX(), (short)currentAdj.getY()) || Math.abs(room.getLayout().getHeightAtSquare(current.getX(), current.getY()) - room.getLayout().getHeightAtSquare(currentAdj.getX(), currentAdj.getY())) > 1)
                    continue;

                if (!openList.contains(currentAdj) || (currentAdj.getX() == newX && currentAdj.getY() == newY && (room.canSitOrLayAt(newX, newY))))
                {
                    currentAdj.setPrevious(current);
                    currentAdj.sethCosts(this.nodes[newX][newY]);
                    currentAdj.setgCosts(current);
                    openList.add(currentAdj);
                } else if (currentAdj.getgCosts() > currentAdj.calculategCosts(current))
                {
                    currentAdj.setPrevious(current);
                    currentAdj.setgCosts(current);
                }
            }
            if (openList.isEmpty()) {
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

    private synchronized T lowestFInOpen(List<T> openList)
    {
        if(openList == null)
            return null;

        T cheapest = openList.get(0);
        for (T anOpenList : openList)
        {
            if (anOpenList.getfCosts() < cheapest.getfCosts())
            {
                cheapest = anOpenList;
            }
        }
        return cheapest;
    }

    private synchronized List<T> getAdjacent(List<T> closedList, T node, int newX, int newY, Room room)
    {
        int x = node.getX();
        int y = node.getY();
        List<T> adj = new LinkedList<T>();
        if (x > 0)
        {
            T temp = getNode(x - 1, y);
            if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (x < this.width)
        {
            T temp = getNode(x + 1, y);
            if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (y > 0)
        {
            T temp = getNode(x, y - 1);
            if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
            {
                temp.setIsDiagonaly(false);
                adj.add(temp);
            }
        }
        if (y < this.height)
        {
            T temp = getNode(x, y + 1);
            if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
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
                if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y > 0))
            {
                T temp = getNode(x - 1, y - 1);
                if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x > 0) && (y < this.height))
            {
                T temp = getNode(x - 1, y + 1);
                if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
                {
                    temp.setIsDiagonaly(true);
                    adj.add(temp);
                }
            }
            if ((x < this.width) && (y > 0))
            {
                T temp = getNode(x + 1, y - 1);
                if (((temp.isWalkable()) && (!closedList.contains(temp))) || (temp.getX() == newX && temp.getY() == newY && room.canSitOrLayAt(newX, newY)))
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