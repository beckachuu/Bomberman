package Systems.AISystem.AStar;

import javafx.util.Pair;

public class Cell {
    // Row and Column index of its parent
    public final Pair<Integer, Integer> parent;
    // f = g + h
    public double f, g, h;

    public Cell(Pair<Integer, Integer> parent) {
        this.parent = parent;
    }

    public Cell(double f, double g, double h) {
        this.f = f;
        this.g = g;
        this.h = h;
        this.parent = new Pair<Integer, Integer>(0, 0);
    }

    public Cell(double f, double g, double h, int x, int y) {
        this.f = f;
        this.g = g;
        this.h = h;
        this.parent = new Pair<Integer, Integer>(x, y);
    }
}
