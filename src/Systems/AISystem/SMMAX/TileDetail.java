package Systems.AISystem.SMMAX;

public class TileDetail implements Comparable<Object> {
    private int tileX, tileY;
    private double heuristic;

    public TileDetail(int tileX, int tileY, double heuristic) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.heuristic = heuristic;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof TileDetail) {
            TileDetail other = (TileDetail) o;
            // return this.detail.getValue().compareTo(otherPair.getValue());
            return this.getHeuristic().compareTo(other.getHeuristic());
        }
        return -1;
    }

    public int getTileX() {
        return tileX;
    }

    public int getTileY() {
        return tileY;
    }

    public Double getHeuristic() {
        return heuristic;
    }
}
