package com.eu.habbo.util.pathfinding;

/**
 * Created on 14-9-2014 13:51.
 */
public class Node extends AbstractNode {

    public Node(int xPosition, int yPosition)
    {
        super(xPosition, yPosition);
    }

    @Override
    public void sethCosts(AbstractNode endNode)
    {
        sethCosts((absolute(getX() - endNode.getX()) + absolute(getY() - endNode.getY())) * 10);
    }

    private int absolute(int a)
    {
        return a > 0 ? a : -a;
    }

    public boolean equals(Node node)
    {
        return (getX() == node.getX()) && (getY() == node.getY());
    }
}
