package de.gemo.game.physics.entity;

import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.newdawn.slick.Color;

import de.gemo.game.physics.Physics2D;

import static org.lwjgl.opengl.GL11.*;

public class Hook extends EntityCollidable {

    private float halfWidth, halfHeight;
    private boolean left = false;
    private Vec2 hookPos = null;

    private float hookSpeed = 1.2f;
    private Body hookAnchor = null;
    private DistanceJointDef joint = null;

    private Player player;

    public Hook(Player player, float x, float y, float angle, boolean left) {
        this.player = player;
        halfWidth = 10 / 2f;
        halfHeight = 10 / 2f;

        // box
        BodyDef def = new BodyDef();
        def.type = BodyType.DYNAMIC;
        def.bullet = true;
        def.position.set(x / Physics2D.pxPerM, y / Physics2D.pxPerM);
        def.angle = 0;

        Body body = Physics2D.world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.m_radius = halfWidth / Physics2D.pxPerM;
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 1;
        body.createFixture(fDef);

        this.init(body, x, y);

        this.setAngle(angle);
        this.left = left;

        if (this.left) {
            this.setLinearVelocity(-hookSpeed * 16, -hookSpeed * 16);
        } else {
            this.setLinearVelocity(+hookSpeed * 16, -hookSpeed * 16);
        }
    }

    public void createJoint() {
        if (this.hookPos != null && this.joint == null && this.hookAnchor != null) {
            this.joint = new DistanceJointDef();
            this.joint.initialize(this.hookAnchor, this.player.getBody(), hookPos, this.player.getWorldCenter());
            Physics2D.world.createJoint(this.joint);
        }
    }

    public boolean hasJoint() {
        return this.joint != null;
    }

    public void update(int delta) {
        // hook is catched
        if (this.hookPos != null) {
            this.setPosition(this.hookPos);
            this.setLinearVelocity(0, 0);

            // create the anchor
            if (this.hookAnchor == null) {
                BodyDef bodyDef = new BodyDef();
                bodyDef.position.set(hookPos);
                bodyDef.type = BodyType.STATIC;
                hookAnchor = Physics2D.world.createBody(bodyDef);
            }

            // auto-remove of the hook
            double dx = this.getX() - this.player.getX();
            double dy = -(this.getY() - this.player.getY());
            double inRads = Math.atan2(dy, dx);
            if (inRads < 0)
                inRads = Math.abs(inRads);
            else
                inRads = 2 * Math.PI - inRads;
            float angle = (float) Math.abs(Math.toDegrees(inRads));

            if (angle > 40 && angle < 140) {
                this.player.removeHook();
            }
            return;
        }
    }

    public void setHookPos(Vec2 hookPos) {
        this.hookPos = hookPos;
        this.setPosition(this.hookPos);
    }

    @Override
    public void debugRender() {
        glLineWidth(1);
        Vec2 pos = this.getPosition();

        glPushMatrix();
        {
            // translate to center
            glTranslatef(pos.x * Physics2D.pxPerM, pos.y * Physics2D.pxPerM, 9);
            Color.yellow.bind();

            // render center
            glBegin(GL_POLYGON);
            glVertex3f(-halfWidth, -halfHeight, 0f);
            glVertex3f(+halfWidth, -halfHeight, 0f);
            glVertex3f(+halfWidth, +halfHeight, 0f);
            glVertex3f(-halfWidth, +halfHeight, 0f);
            glEnd();
        }
        glPopMatrix();
    }

    @Override
    public void render() {
        this.debugRender();
    }

    public void destroyBody() {
        super.destroyBody();
        if (this.hookAnchor != null) {
            Physics2D.world.destroyBody(this.hookAnchor);
        }
    }

    @Override
    public boolean beginCollision(EntityCollidable entity, Contact contact) {
        if (entity instanceof Ground) {
            WorldManifold worldManifold = new WorldManifold();
            contact.getWorldManifold(worldManifold);

            Ground groundBox = (Ground) entity;
            Vec2 hitPoint = worldManifold.points[0];
            int yPos = (int) (contact.getManifold().localPoint.y * 10 * Physics2D.pxPerM);
            int yPos2 = (int) (groundBox.getHalfHeight() * 10);
            boolean isDown = groundBox.isDown() && yPos == yPos2;

            if (isDown && !this.player.isHookCatched()) {
                this.player.setHookCatched(true);
                this.setHookPos(hitPoint);
                this.player.setHookDistance(MathUtils.distanceSquared(this.hookPos, this.player.getPosition()));
                return true;
            }
            if (!isDown) {
                this.player.removeHook();
                return false;
            }
        }
        this.player.removeHook();
        return false;
    }

    public boolean removePartial() {
        if (this.hookAnchor != null && this.joint != null) {
            Physics2D.world.destroyBody(this.hookAnchor);
            this.player.setHookCatched(false);
            this.hookAnchor = null;
            this.joint = null;
            this.player.setHookDistance(MathUtils.distanceSquared(this.hookPos, this.player.getPosition()));
            return true;
        }
        return false;
    }
}
