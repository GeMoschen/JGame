package de.gemo.gameengine.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.core.GameEngine;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private static List<IFontRenderable> fontList = new ArrayList<IFontRenderable>();
    private static Map<Integer, List<IRenderable>> renderList = new ConcurrentHashMap<Integer, List<IRenderable>>();

    /**
     * Add an {@link IRenderable} to the renderpipeline.
     * 
     * @param renderable
     */
    public static void addRenderable(IRenderable renderable) {
        addRenderable(null, renderable);
    }

    /**
     * Add an {@link IRenderable} to the renderpipeline.
     * 
     * @param texture
     * @param renderable
     */
    public static void addRenderable(Texture texture, IRenderable renderable) {
        int textureID = -1;
        if (texture != null) {
            textureID = texture.getTextureID();
        }

        List<IRenderable> list = renderList.get(textureID);
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<IRenderable>());
            renderList.put(textureID, list);
        }
        list.add(renderable);
    }

    /**
     * Render all elements. <br/>
     * <b>NOTE: </b> This method is <b>automaticly</b> called be the
     * {@link GameEngine} in the Renderloop. It will render all given
     * {@link IRenderable} and also the {@link IFontRenderable} from the
     * renderpipeline.<br />
     * Do <b>NOT</b> call this method by yourself.
     */
    public static void renderAll() {
        // render elements
        for (List<IRenderable> list : renderList.values()) {
            for (IRenderable renderable : list) {
                renderable.render();
            }
        }
        renderList.clear();

        // render fonts
        Renderer.renderFonts();
    }

    /**
     * Render a given {@link Hitbox}
     * 
     * @param hitbox
     */
    public static void renderHitbox(Hitbox hitbox) {
        if (hitbox == null) {
            return;
        }
        hitbox.render();
    }

    /**
     * Bind a {@link Texture} to OpenGL. This will bind the given Texture for
     * later uses.
     * 
     * @param texture
     */
    public static void bindTexture(Texture texture) {
        if (texture != null && (glGetInteger(GL_TEXTURE_BINDING_2D) != texture.getTextureID())) {
            texture.bind();
        }
    }

    /**
     * Unbind the current {@link Texture} from OpenGL.
     */
    public static void unbind() {
        TextureImpl.bindNone();
    }

    /**
     * Add a {@link IFontRenderable} to the renderpipeline
     * 
     * @param element
     */
    public static void addFontRender(IFontRenderable element) {
        fontList.add(element);
    }

    /**
     * Render all {@link IFontRenderable}, currently in the renderpipeline.
     */
    private static void renderFonts() {
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        for (IFontRenderable font : fontList) {
            glPushMatrix();
            {
                font.renderFont();
            }
            glPopMatrix();
        }
        fontList.clear();
        glDisable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
    }
}
