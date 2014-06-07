import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform4f;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class Main {

    public final int width = 800;
    public final int height = 600;

    public ArrayList<LightCone> lights = new ArrayList<LightCone>();
    public ArrayList<Block> blocks = new ArrayList<Block>();

    private Shader coneShader, ambientShader;

    private void render() {
        this.updateLights();

        glClear(GL_COLOR_BUFFER_BIT);

        // blocks
        glColor3f(1f, 1f, 1f);
        for (Block block : blocks) {
            block.render();
        }

        // AMBIENT
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        ambientShader.bind();
        glUniform4f(glGetUniformLocation(ambientShader.getID(), "ambientColor"), 0.3f, 0.3f, 0.7f, 0.5f);

        glColor4f(0.3f, 0.3f, 0.7f, 0.5f);
        glBegin(GL_QUADS);
        {
            glVertex2f(0, 0);
            glVertex2f(800, 0);
            glVertex2f(800, 600);
            glVertex2f(0, 600);
        }
        glEnd();
        ambientShader.unbind();
        glDisable(GL_BLEND);

        for (LightCone light : lights) {
            light.render(this.blocks, this.coneShader, this.ambientShader, 800, 600);
        }

        Display.update();
        Display.sync(60);
    }

    private void updateLights() {
        // lights.get(0).location.x = Mouse.getX();
        // lights.get(0).location.y = height - Mouse.getY();
        lights.get(0).intensity = 1f;
        lights.get(0).red = 30f;
        lights.get(0).green = 0f;
        lights.get(0).blue = 0f;
        if (Mouse.isButtonDown(0)) {
            lights.get(0).target = new Vector2f(Mouse.getX(), height - Mouse.getY());
        }

        lights.get(0).setAngle(getAngle(lights.get(0).getLocation(), new Vector2f(Mouse.getX(), height - Mouse.getY())));
        lights.get(0).seek(this.lights);
        int i = 0;
        for (LightCone cone : this.lights) {
            if (i != 0) {
                cone.update();
                // cone.seek(this.lights);
            }
            i = 1;
        }
    }

    public float getAngle(Vector2f target, Vector2f pos) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - pos.y, target.x - pos.x));

        if (angle < 0) {
            angle += 360;
        }

        return angle - 90;
    }

    private void setUpObjects() {
        int lightCount = 5;
        int blockCount = 10 + (int) (Math.random() * 1);
        // int blockCount = 3;

        for (int i = 1; i <= lightCount; i++) {
            Vector2f location = new Vector2f((float) Math.random() * width, (float) Math.random() * height);
            lights.add(new LightCone(location, (float) Math.random() * 10, (float) Math.random() * 10, (float) Math.random() * 10));
        }

        for (int i = 1; i <= blockCount; i++) {
            int width = 50;
            int height = 50;
            int x = (int) (Math.random() * (this.width - width));
            int y = (int) (Math.random() * (this.height - height));
            blocks.add(new Block(x, y, width, height));
        }
    }

    private void initialize() {
        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.setTitle("2D Lighting");
            Display.create(new PixelFormat(0, 16, 1));
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        System.out.println("OS name: " + System.getProperty("os.name"));
        System.out.println("OS version: " + System.getProperty("os.version"));
        System.out.println("LWJGL version: " + org.lwjgl.Sys.getVersion());
        System.out.println("OpenGL version: " + glGetString(GL_VERSION));

        coneShader = new Shader();
        coneShader.loadPixelShader("viewcone.frag");

        ambientShader = new Shader();
        // ambientShader.loadPixelShader("ambientLight.frag");

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        glEnable(GL_STENCIL_TEST);
        glClearColor(0f, 0f, 0f, 0f);
    }

    private void cleanup() {
        this.coneShader.cleanup();
        this.ambientShader.cleanup();
        Display.destroy();
    }

    public static void main(String[] args) {
        Main main = new Main();

        main.setUpObjects();
        main.initialize();

        while (!Display.isCloseRequested()) {
            main.render();
        }

        main.cleanup();
    }
}
