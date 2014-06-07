import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.util.vector.Vector2f;

public class Block {
    public int x, y, width, height;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Vector2f[] getVertices() {
        return new Vector2f[] { new Vector2f(x, y), new Vector2f(x, y + height), new Vector2f(x + width, y + height), new Vector2f(x + width, y) };

        // return new Vector2f[] { new Vector2f(x, y), new Vector2f(x+ width,
        // y), new Vector2f(x + width, y + height), new Vector2f(x, y + height)
        // };
    }

    public void render() {
        glBegin(GL_QUADS);
        {
            for (Vector2f vertex : this.getVertices()) {
                glVertex2f(vertex.getX(), vertex.getY());
            }
        }
        glEnd();
    }
}
