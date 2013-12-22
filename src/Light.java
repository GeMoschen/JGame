import de.gemo.gameengine.units.Vector;

public class Light {
	public Vector location;
	public float red;
	public float green;
	public float blue;
	public float brightness;

	public Light(Vector location, float red, float green, float blue, float brightness) {
		this.location = location;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.brightness = brightness;
	}
}
