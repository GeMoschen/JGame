package de.gemo.gameengine.gui;

import java.util.Random;

import org.newdawn.slick.Color;

import de.gemo.gameengine.renderer.IRenderable;
import de.gemo.gameengine.renderer.Renderer;
import de.gemo.gameengine.textures.Animation;
import de.gemo.gameengine.textures.MultiTexture;
import de.gemo.gameengine.textures.SingleTexture;
import de.gemo.gameengine.units.Vector;

import static org.lwjgl.opengl.GL11.*;

public class Graphic2D implements IRenderable {

    protected static Random random = new Random();

    // random color for debugrendering
    protected final Color randomColor;

    // position
    protected final Vector position = new Vector();

    // visibility
    protected float angle = 0, alpha = 1f;
    protected boolean visible = true;

    // PositionAnchor
    protected PositionAnchor positionAnchor = PositionAnchor.CENTER;
    protected float alignmentOffsetX = 0, alignmentOffsetY = 0;

    // width, height and scale
    protected float width = 0, height = 0;
    protected float scaleX = 1f, scaleY = 1f;

    // Animation
    protected Animation animation = null;

    public Graphic2D(float x, float y, float z, float angle, float alpha) {
        this.randomColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        this.setPosition(x, y, z);
        this.setAngle(angle);
        this.setAlpha(alpha);
    }

    public Graphic2D(float x, float y, float z) {
        this(x, y, z, 0f, 1f);
    }

    public Graphic2D(Vector position) {
        this(position.getX(), position.getY(), position.getZ(), 0f, 1f);
    }

    public Graphic2D(Vector position, float angle, float alpha) {
        this(position.getX(), position.getY(), position.getZ(), angle, alpha);
    }

    // ////////////////////////////////////////
    //
    // POSITION-ANCHOR
    //
    // ////////////////////////////////////////

    /**
     * Get the {@link PositionAnchor}.
     * 
     * @return the positionAnchor
     */
    public final PositionAnchor getPositionAnchor() {
        return positionAnchor;
    }

    /**
     * Set the {@link PositionAnchor}
     * 
     * @param positionAnchor
     */
    public final void setPositionAnchor(PositionAnchor positionAnchor) {
        this.positionAnchor = positionAnchor;
        if (this.animation != null) {
            SingleTexture texture = this.animation.getCurrentTexture();
            switch (this.positionAnchor) {
            case LEFT_TOP: {
                this.alignmentOffsetX = +(texture.getHalfWidth());
                this.alignmentOffsetY = +(texture.getHalfHeight());
                break;
            }
            case CENTER_TOP: {
                this.alignmentOffsetX = 0;
                this.alignmentOffsetY = +(texture.getHalfHeight());
                break;
            }
            case RIGHT_TOP: {
                this.alignmentOffsetX = -(texture.getHalfWidth());
                this.alignmentOffsetY = +(texture.getHalfHeight());
                break;
            }
            case LEFT_CENTER: {
                this.alignmentOffsetX = +(texture.getHalfWidth());
                this.alignmentOffsetY = 0;
                break;
            }
            case RIGHT_CENTER: {
                this.alignmentOffsetX = -(texture.getHalfWidth());
                this.alignmentOffsetY = 0;
                break;
            }
            case LEFT_BOTTOM: {
                this.alignmentOffsetX = +(texture.getHalfWidth());
                this.alignmentOffsetY = -(texture.getHalfHeight());
                break;
            }
            case CENTER_BOTTOM: {
                this.alignmentOffsetX = 0;
                this.alignmentOffsetY = -(texture.getHalfHeight());
                break;
            }
            case RIGHT_BOTTOM: {
                this.alignmentOffsetX = -(texture.getHalfWidth());
                this.alignmentOffsetY = -(texture.getHalfHeight());
                break;
            }
            default: {
                this.alignmentOffsetX = 0;
                this.alignmentOffsetY = 0;
                break;
            }
            }
        } else {
            this.alignmentOffsetX = 0;
            this.alignmentOffsetY = 0;
        }

        this.alignmentOffsetX *= this.scaleX;
        this.alignmentOffsetY *= this.scaleY;
    }

    // ////////////////////////////////////////
    //
    // VISIBILITY
    //
    // ////////////////////////////////////////

    /**
     * Get the current alpha-value
     * 
     * @return the alpha-value
     */
    public final float getAlpha() {
        return alpha;
    }

    /**
     * Set the alpha-value (between 0 and 1).
     * 
     * @param alpha
     */
    public final void setAlpha(float alpha) {
        // must be 0 <= alpha <= 1
        alpha = Math.min(Math.max(alpha, 0f), 1f);
        this.alpha = alpha;
        this.visible = (this.alpha > 0f);
    }

    /**
     * Check if this Graphic2D is visible
     * 
     * @return <b>true</b>, if the graphic is set visible, otherwise
     *         <b>false</b>
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Set the visibility of this Graphic2D. <br />
     * <b>Note: </b>This will <b>NOT</b> alter the alpha-value.
     * 
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // ////////////////////////////////////////
    //
    // ANGLE
    //
    // ////////////////////////////////////////

    /**
     * Get the current angle (in degrees)
     * 
     * @return the current angle in degrees
     */
    public final float getAngle() {
        return angle;
    }

    /**
     * Rotate this Graphic2D
     * 
     * @param angle
     *            in degrees
     */
    public final void rotate(float angle) {
        this.setAngle(this.getAngle() + angle);
    }

    /**
     * Set the angle of this Graphic2D
     * 
     * @param angle
     *            in degrees
     */
    public final void setAngle(float angle) {
        // correct angles >= 360
        while (angle >= 360f) {
            angle -= 360f;
        }

        // corrent angles < 0
        while (angle < 0f) {
            angle += 360f;
        }
        this.angle = angle;
    }

    // ////////////////////////////////////////
    //
    // POSITIONING
    //
    // ////////////////////////////////////////

    /**
     * Get the current position. <br />
     * <b>Note:</b> This position is always the center of the graphic. The
     * offsets given by the PositionAnchor are handled seperatly.
     * 
     * @return the position as a {@link Vector}
     */
    public final Vector getPosition() {
        return this.position;
    }

    /**
     * Set the position of this Graphic2D
     * 
     * @param x
     * @param y
     */
    public final void setPosition(float x, float y) {
        this.setPosition(x, y, this.position.getZ());
    }

    /**
     * Set the position of this Graphic2D
     * 
     * @param x
     * @param y
     * @param z
     */
    public final void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    /**
     * Move this Graphic2D
     * 
     * @param x
     * @param y
     */
    public final void move(float x, float y) {
        this.move(x, y, 0f);
    }

    /**
     * Move this Graphic2D
     * 
     * @param x
     * @param y
     * @param z
     */
    public final void move(float x, float y, float z) {
        this.position.set(this.position.getX() + x, this.position.getY() + y, this.position.getZ() + z);
    }

    // ////////////////////////////////////////
    //
    // DIMENSIONS
    //
    // ////////////////////////////////////////

    /**
     * Get the current width of this Graphic2D.
     * 
     * @return the width
     */
    public final float getWidth() {
        return width;
    }

    /**
     * Get the current height of this Graphic2D.
     * 
     * @return the height
     */
    public final float getHeight() {
        return height;
    }

    // ////////////////////////////////////////
    //
    // ANIMATIONS
    //
    // ////////////////////////////////////////

    /**
     * Play the {@link Animation}
     * 
     * @param delta
     * @return <b>true</b> if we have set an animation, otherwise <b>false</b>
     */
    public final boolean playAnimation(float delta) {
        if (this.animation == null) {
            return false;
        }
        this.animation.step(delta);
        return true;
    }

    /**
     * Set the {@link Animation}, based on a {@link SingleTexture}
     * 
     * @param texture
     * @return <b>true</b> if the texture was set, otherwise <b>false</b>
     */
    public final boolean setTexture(SingleTexture texture) {
        if (texture == null) {
            this.animation = null;
            this.width = 0f;
            this.height = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            return false;
        }
        return this.setAnimation(texture.toMultiTexture().toAnimation());
    }

    /**
     * Set the {@link Animation}, based on a {@link MultiTexture}
     * 
     * @param texture
     * @return <b>true</b> if the texture was set, otherwise <b>false</b>
     */
    public final boolean setTexture(MultiTexture texture) {
        if (texture == null) {
            this.animation = null;
            this.width = 0f;
            this.height = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            return false;
        }
        return this.setAnimation(texture.toAnimation());
    }

    /**
     * Set the {@link Animation}
     * 
     * @param texture
     * @return <b>true</b> if the animation was set, otherwise <b>false</b>
     */
    public final boolean setAnimation(Animation animation) {
        if (animation == null) {
            this.animation = null;
            this.width = 0f;
            this.height = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            return false;
        }
        this.animation = animation.clone();
        this.width = this.animation.getWidth();
        this.height = this.animation.getHeight();
        this.scaleX = 1f;
        this.scaleY = 1f;
        this.setPositionAnchor(this.getPositionAnchor());
        return true;
    }

    /**
     * Get the {@link Animation}
     * 
     * @return the Animation
     */
    public final Animation getAnimation() {
        return animation;
    }

    // ////////////////////////////////////////
    //
    // RENDERING
    //
    // ////////////////////////////////////////

    @Override
    public final void addToRenderPipeline() {
        // we need an animation
        if (this.animation == null && !this.isVisible()) {
            return;
        }
        Renderer.addRenderable(this.animation.getCurrentTexture().getTexture(), this);
    }

    /**
     * Render this Graphic2D with a given RGB-Value
     * 
     * @param r
     *            , between 0 and 1
     * @param g
     *            , between 0 and 1
     * @param b
     *            , between 0 and 1
     */
    public final void render(float r, float g, float b) {
        // we need an animation
        if (this.animation == null || !this.isVisible()) {
            return;
        }

        // translate and render...
        glPushMatrix();
        {
            glTranslatef(this.position.getX(), this.position.getY(), this.position.getZ());
            glTranslatef(this.alignmentOffsetX, this.alignmentOffsetY, 0f);
            glRotatef(this.getAngle(), 0, 0, 1);
            glScalef(scaleX, scaleY, 1);
            this.animation.render(r, g, b, this.getAlpha());

        }
        glPopMatrix();
    }

    /**
     * Render this Graphic2D with RGB(255, 255, 255). Calls render(r, g, b).
     */
    @Override
    public final void render() {
        this.render(1f, 1f, 1f);
    }

    /**
     * Debugrendering of this Graphic2D. This will render a simple rectangle,
     * based on the {@link Animation}-dimensions.
     */
    @Override
    public final void debugRender() { // we need an animation
        if (this.animation == null || !this.isVisible()) {
            return;
        }

        glLineWidth(2f);
        glDisable(GL_LINE_STIPPLE);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        // translate and render...
        glPushMatrix();
        {
            glTranslatef(this.position.getX(), this.position.getY(), this.position.getZ());
            glTranslatef(this.alignmentOffsetX, this.alignmentOffsetY, 0);
            glRotatef(this.getAngle(), 0, 0, 1);
            glScalef(scaleX, scaleY, 1);

            this.randomColor.bind();
            glBegin(GL_LINE_LOOP);
            glVertex2f(-this.animation.getHalfWidth(), -this.animation.getHalfHeight());
            glVertex2f(+this.animation.getHalfWidth(), -this.animation.getHalfHeight());
            glVertex2f(+this.animation.getHalfWidth(), +this.animation.getHalfHeight());
            glVertex2f(-this.animation.getHalfWidth(), +this.animation.getHalfHeight());
            glEnd();

        }
        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
    }

    // ////////////////////////////////////////
    //
    // SCALING
    //
    // ////////////////////////////////////////

    /**
     * Scale this Graphic2D in X & Y.
     * 
     * @param scaleX
     * @param scaleY
     * @return <b>true</b> if the scale was successfull, otherwise <b>false</b>
     */
    public final boolean scale(float scaleX, float scaleY) {
        if (this.animation == null || scaleX <= 0f || scaleY <= 0f) {
            return false;
        }
        this.scaleX *= scaleX;
        this.scaleY *= scaleY;
        this.width *= scaleX;
        this.height *= scaleY;
        this.setPositionAnchor(this.getPositionAnchor());
        return true;
    }

    /**
     * Scale this Graphic2D in X & Y.
     * 
     * @param scale
     * @return <b>true</b> if the scale was successfull, otherwise <b>false</b>
     */
    public final boolean scale(float scale) {
        return this.scale(scale, scale);
    }

    /**
     * Scale this Graphic2D in X.
     * 
     * @param scaleX
     * @return <b>true</b> if the scale was successfull, otherwise <b>false</b>
     */
    public final boolean scaleX(float scaleX) {
        return this.scale(scaleX, 1f);
    }

    /**
     * Scale this Graphic2D in Y.
     * 
     * @param scaleY
     * @return <b>true</b> if the scale was successfull, otherwise <b>false</b>
     */
    public final boolean scaleY(float scaleY) {
        return this.scale(1f, scaleY);
    }

    /**
     * Set the size of this Graphic2D. <br />
     * <b>NOTE: </b>this will call scale(scaleX, scaleY).
     * 
     * @param width
     * @param height
     * 
     * @return <b>true</b> if the scale was successfull, otherwise <b>false</b>
     */
    public final boolean setSize(float width, float height) {
        if (this.animation == null) {
            return false;
        }
        float scaleX = width / this.width;
        float scaleY = height / this.height;
        return this.scale(scaleX, scaleY);
    }

}
