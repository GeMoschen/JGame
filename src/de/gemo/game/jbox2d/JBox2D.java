package de.gemo.game.jbox2d;

import java.util.HashMap;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.gemo.engine.core.Engine;
import de.gemo.engine.core.debug.StandardDebugMonitor;
import de.gemo.engine.manager.KeyboardManager;
import de.gemo.game.jbox2d.tests.BodyTest;
import de.gemo.game.jbox2d.tests.RaycastTest;
import de.gemo.game.jbox2d.tests.Test;

import static org.lwjgl.opengl.GL11.*;

public class JBox2D extends Engine {

    public static float SCALE = 1f;

    private World world;
    private int mouseCooldown = 0;

    private PolygonShape groundShape;

    private HashMap<String, Test> testMap = new HashMap<String, Test>();

    public JBox2D() {
        super("JBox2D", 800, 600, false);
    }

    @Override
    protected void createGUI() {
        this.initPhysics();
    }

    @Override
    protected void createManager() {
        this.setDebugMonitor(new StandardDebugMonitor());
    }

    private int tick = 0;

    @Override
    protected void updateGame(int delta) {
        // if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
        long ms = System.currentTimeMillis();
        this.world.step(1f / 30f, 6, 16);
        ms = System.currentTimeMillis() - ms;

        if (this.mouseCooldown > 0) {
            this.mouseCooldown--;
        }

        // MOUSE LEFT
        if (mouseCooldown < 1 && Mouse.isButtonDown(0)) {
            mouseCooldown = 10;
            for (Test test : this.testMap.values()) {
                if (test.handleMousePress(0)) {
                    mouseCooldown = 10;
                    break;
                }
            }
        }

        // MOUSE RIGHT
        if (mouseCooldown < 1 && Mouse.isButtonDown(1)) {
            for (Test test : this.testMap.values()) {
                if (test.handleMousePress(1)) {
                    mouseCooldown = 10;
                    break;
                }
            }
        }

        if (mouseCooldown < 1 && Mouse.isButtonDown(2)) {
            for (Test test : this.testMap.values()) {
                if (test.handleMousePress(2)) {
                    mouseCooldown = 10;
                    break;
                }
            }
        }

        if (mouseCooldown < 1) {
            int key = 0;
            for (Boolean isPressed : KeyboardManager.INSTANCE.pressedKeys.values()) {
                if (isPressed) {
                    for (Test test : this.testMap.values()) {
                        if (test.handleKeyPress(key)) {
                            mouseCooldown = 10;
                            break;
                        }
                    }
                }
                key++;
            }
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            this.resetScene();
            return;
        }

        tick++;
        if (tick % 100 == 0) {
            tick = 0;
            this.cleanUp();
        }

        // update tests
        for (Test test : this.testMap.values()) {
            test.update(delta);
        }
    }

    private void resetScene() {
        this.cleanUp();
        this.setOrtho();
        for (Test test : this.testMap.values()) {
            test.init();
        }
    }

    private void cleanUp() {
        for (Test test : this.testMap.values()) {
            test.cleanUp();
        }
    }

    public void removeBody(Body body) {
        this.world.destroyBody(body);
    }

    private void renderVec2(Vec2 vector) {
        glVertex2f(vector.x, vector.y);
    }

    private void renderObjects() {
        long ms = System.currentTimeMillis();

        glPushMatrix();
        {
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);

            glDisable(GL_LIGHTING);



            int bodyCount = 0;
            for (Test test : this.testMap.values()) {
                bodyCount += test.renderBodies();
                bodyCount += test.render();
            }
            
            // render ground
            glPushMatrix();
            {
                glColor4f(1, 1, 1, 1);
                glBegin(GL_LINES);
                renderVec2(groundShape.getVertex(0));
                renderVec2(groundShape.getVertex(1));
                glEnd();
            }
            glPopMatrix();
        }
        glPopMatrix();

        ms = System.currentTimeMillis() - ms;
        // System.out.println("Rendering (" + (bodies.size() + 1) + ") : " + ms + "ms");
    }

    public void initPhysics() {
        // create world
        this.world = new World(new Vec2(0, 9.8f), true);
        this.world.setWarmStarting(true);

        // create ground
        BodyDef bd = new BodyDef();
        Body ground = world.createBody(bd);
        ground.setType(BodyType.STATIC);

        groundShape = new PolygonShape();
        groundShape.setAsEdge(new Vec2(0, Engine.INSTANCE.VIEW_HEIGHT - 20), new Vec2(Engine.INSTANCE.VIEW_WIDTH, Engine.INSTANCE.VIEW_HEIGHT - 20));

        ground.createFixture(groundShape, 0.0F);
        ground.setUserData(-1);

        this.testMap.put("bodytest", new BodyTest(this));
        this.testMap.put("raycasttest", new RaycastTest(this));
        this.resetScene();

        // add contactlistener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }

    @Override
    protected void renderGame2D() {
        renderObjects();
    }

    public World getWorld() {
        return world;
    }
}
