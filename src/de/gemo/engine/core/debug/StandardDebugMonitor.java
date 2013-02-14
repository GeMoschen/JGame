package de.gemo.engine.core.debug;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.core.Engine;
import de.gemo.engine.core.FontManager;
import de.gemo.game.controller.MyGUIController;

public class StandardDebugMonitor extends AbstractDebugMonitor {

    @Override
    public void render() {
        glPushMatrix();
        {
            glEnable(GL_BLEND);

            UnicodeFont font = FontManager.getStandardFont();
            int fontX = 30;
            int fontY = 60;
            font.drawString(fontX, fontY + 10, "FPS: " + this.getFPS() + (this.isUseVSync() ? " (vsync)" : ""), Color.red);
            if (this.isShowExtended()) {
                font.drawString(fontX, fontY + 25, "Delta: " + this.getDelta(), Color.red);

                font.drawString(fontX, fontY + 40, "1/2: Scale active button", Color.red);
                font.drawString(fontX, fontY + 55, "LEFT/RIGHT: rotate active button", Color.magenta);
                font.drawString(fontX, fontY + 70, "W/S: change alpha of active button", Color.magenta);
                font.drawString(fontX, fontY + 85, "UP/DOWN: move active button", Color.magenta);

                font.drawString(fontX, fontY + 100, "F1: toggle vysnc", Color.orange);
                font.drawString(fontX, fontY + 115, "F2: toggle debuginfo", Color.orange);
                font.drawString(fontX, fontY + 130, "F5 / F6: change volume ( " + Engine.INSTANCE.getSoundManager().getVolume() + " )", Color.orange);
                font.drawString(fontX, fontY + 145, "F11: toggle graphics", Color.orange);
                font.drawString(fontX, fontY + 160, "F12: toggle hitboxes", Color.orange);

                String text = "NONE";
                if (this.getActiveGUIController() != null) {
                    text = this.getActiveGUIController().getName();

                    if (this.getActiveGUIController() instanceof MyGUIController) {
                        MyGUIController controller = (MyGUIController) this.getActiveGUIController();

                        if (controller.getHoveredElement() != null) {
                            font.drawString(fontX, fontY + 195, "Hovered: " + controller.getHoveredElement().getEntityID(), Color.yellow);
                        }
                        if (controller.getFocusedElement() != null) {
                            font.drawString(fontX, fontY + 210, "Focused: " + controller.getFocusedElement().getEntityID(), Color.yellow);
                        }
                        if (controller.hotkeysActive) {
                            font.drawString(fontX, fontY + 225, "Hotkeys active", Color.green);
                        }
                    }
                }
                font.drawString(fontX, fontY + 180, "Active UI: " + text, Color.red);
            }

            glDisable(GL_BLEND);
        }
        glPopMatrix();
    }

}
