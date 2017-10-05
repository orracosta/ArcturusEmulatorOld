package com.habboproject.server.game.rooms.objects.entities.pathfinding;

import com.habboproject.server.game.rooms.objects.RoomObject;
import com.habboproject.server.game.rooms.objects.entities.RoomEntity;
import com.habboproject.server.game.rooms.objects.items.RoomItemFloor;
import com.habboproject.server.game.rooms.objects.misc.Position;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class Pathfinder {
    public static final byte DISABLE_DIAGONAL = 0;
    public static final byte ALLOW_DIAGONAL = 1;

    public List<Square> makePath(RoomObject roomFloorObject, Position end) {
        return this.makePath(roomFloorObject, end, ALLOW_DIAGONAL, false);
    }

    public List<Square> makePath(RoomObject roomFloorObject, Position end, byte pathfinderMode, boolean isRetry) {
        List<Square> squares = new CopyOnWriteArrayList<>();

        PathfinderNode nodes = makePathReversed(roomFloorObject, end, pathfinderMode, isRetry);

        if (nodes != null) {
            while (nodes.getNextNode() != null) {
                squares.add(new Square(nodes.getPosition().getX(), nodes.getPosition().getY()));
                nodes = nodes.getNextNode();
            }
        }

        return Lists.reverse(squares);
    }

    private PathfinderNode makePathReversed(RoomObject roomFloorObject, Position end, byte pathfinderMode, boolean isRetry) {
        MinMaxPriorityQueue<PathfinderNode> openList = MinMaxPriorityQueue.maximumSize(256).create();

        PathfinderNode[][] map = new PathfinderNode[roomFloorObject.getRoom().getMapping().getModel().getSizeX()][roomFloorObject.getRoom().getMapping().getModel().getSizeY()];
        PathfinderNode node = null;
        Position tmp;

        int cost;
        int diff;

        PathfinderNode current = new PathfinderNode(roomFloorObject.getPosition());
        current.setCost(0);

        PathfinderNode finish = new PathfinderNode(end);

        map[current.getPosition().getX()][current.getPosition().getY()] = current;
        openList.add(current);

        while (openList.size() > 0) {
            current = openList.pollFirst();
            current.setInClosed(true);

            for (int i = 0; i < (pathfinderMode == ALLOW_DIAGONAL ? diagonalMovePoints.length : movePoints.length); i++) {
                tmp = current.getPosition().add((pathfinderMode == ALLOW_DIAGONAL ? diagonalMovePoints : movePoints)[i]);
                final boolean isFinalMove = (tmp.getX() == end.getX() && tmp.getY() == end.getY());

                if (this.isValidStep(roomFloorObject, new Position(current.getPosition().getX(), current.getPosition().getY(), current.getPosition().getZ()), tmp, isFinalMove, isRetry)) {
                    try {
                        if (map[tmp.getX()][tmp.getY()] == null) {
                            node = new PathfinderNode(tmp);
                            map[tmp.getX()][tmp.getY()] = node;
                        } else {
                            node = map[tmp.getX()][tmp.getY()];
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }

                    if (!node.isInClosed()) {
                        diff = 0;

                        if (current.getPosition().getX() != node.getPosition().getX()) {
                            diff += 1;
                        }

                        if (current.getPosition().getY() != node.getPosition().getY()) {
                            diff += 1;
                        }

                        cost = current.getCost() + diff + node.getPosition().getDistanceSquared(end);

                        if (cost < node.getCost()) {
                            node.setCost(cost);
                            node.setNextNode(current);
                        }

                        if (!node.isInOpen()) {
                            if (node.getPosition().getX() == finish.getPosition().getX() && node.getPosition().getY() == finish.getPosition().getY()) {
                                node.setNextNode(current);
                                return node;
                            }

                            node.setInOpen(true);
                            openList.add(node);
                        }
                    }
                }
            }
        }

        return null;
    }

    public boolean isValidStep(RoomObject roomObject, Position from, Position to, boolean lastStep, boolean isRetry) {
        if (!(roomObject.getRoom().getMapping().isValidStep(roomObject instanceof RoomEntity ? (RoomEntity)roomObject : null, from, to, lastStep, roomObject instanceof RoomItemFloor, isRetry) || roomObject instanceof RoomEntity && ((RoomEntity)roomObject).isOverriden())) {
            return false;
        }

        return true;
    }

    private final Position[] diagonalMovePoints = {
            new Position(-1, -1),
            new Position(0, -1),
            new Position(1, 1),
            new Position(0, 1),
            new Position(1, -1),
            new Position(1, 0),
            new Position(-1, 1),
            new Position(-1, 0)
    };

    private final Position[] movePoints = new Position[]{
            new Position(0, -1),
            new Position(1, 0),
            new Position(0, 1),
            new Position(-1, 0)
    };
}