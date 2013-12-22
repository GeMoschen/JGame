package de.gemo.game.physics.gui.statics;

import java.io.File;
import java.io.FileInputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.gemo.gameengine.manager.TextureManager;

import static org.lwjgl.opengl.GL11.*;

public class GUITextures {
    public static Texture GUI01 = null;

    public static void load() {
        GUITextures.GUI01 = loadTexture("resources/gui01.jpg");
    }

    private static Texture loadTexture(String path) {
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("ERROR: Texture '" + path + "' does not exist!");
        }
        try {
            return TextureLoader.getTexture(TextureManager.getExtension(file).toUpperCase(), new FileInputStream(file), false, GL_NEAREST);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
