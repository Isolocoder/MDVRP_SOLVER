package Common.Node;

import Common.Route;

import java.util.Objects;

public abstract class Node {
    public int id;
    public double x;
    public double y;
    public int duration;

    public Node(int id, double x, double y, int duration) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.duration = duration;
    }

    public abstract boolean isDepot();
}
