import org.lwjgl.util.vector.Vector2f;

public class LightBasic {
    public Vector2f location;
    public float red;
    public float green;
    public float blue;
    public float intensity = 1;

    public LightBasic(Vector2f location, float red, float green, float blue) {
        this.location = location;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
