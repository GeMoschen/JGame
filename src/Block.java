import de.gemo.gameengine.units.Vector;

public class Block {
    public int x, y, width, height;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Vector[] getVertices() {
        return new Vector[]{new Vector(x, y), new Vector(x, y + height), new Vector(x + width, y + height), new Vector(x + width, y)};
    }
}
