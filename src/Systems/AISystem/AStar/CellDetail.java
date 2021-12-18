package Systems.AISystem.AStar;

public class CellDetail implements Comparable<Object> {
    private double f;
    private int tileX, tileY;

    public CellDetail(double f, int tileX, int tileY) {
        this.f = f;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof CellDetail) {
            CellDetail other = (CellDetail) o;
            if (this.f == other.f) {
                if (this.tileX == other.tileX) {
                    return Integer.compare(this.tileY, other.tileY);
                } else {
                    return Integer.compare(this.tileX, other.tileX);
                }
            } else {
                return Double.compare(this.f, other.f);
            }
        }
        return 1;
    }

    public double getF() {
        return f;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }
}
