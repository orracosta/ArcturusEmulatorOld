package com.eu.habbo.util.pathfinding;

public class Node extends AbstractNode {

    public Node(int xPosition, int yPosition)
    {
        super((short)xPosition, (short)yPosition);
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
