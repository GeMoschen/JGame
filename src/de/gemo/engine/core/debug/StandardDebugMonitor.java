package de.gemo.engine.core.debug;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.UnicodeFont;

import de.gemo.engine.core.FontManager;
import de.gemo.game.controller.MyGUIController;

public class StandardDebugMonitor extends AbstractDebugMonitor {

    @Override
    public void render() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);

        UnicodeFont font = FontManager.getStandardFont();
        int fontX = 30;
        int fontY = 60;
        font.drawString(fontX, fontY + 10, "FPS: " + this.getFPS() + (this.isUseVSync() ? " (vsync)" : ""), Color.red);
        if (this.isShowExtended()) {
            font.drawString(fontX, fontY + 25, "Delta: " + this.getDelta(), Color.red);

            font.drawString(fontX, fontY + 40, "1/2: Scale active button", Color.red);
            font.drawString(fontX, fontY + 70, "W/S: change alpha of active button", Color.magenta);
            font.drawString(fontX, fontY + 55, "LEFT/RIGHT: rotate active button", Color.magenta);
            font.drawString(fontX, fontY + 85, "UP/DOWN: move active button", Color.magenta);

            font.drawString(fontX, fontY + 105, "F1: toggle vysnc", Color.orange);
            font.drawString(fontX, fontY + 120, "F2: toggle debuginfo", Color.orange);
            font.drawString(fontX, fontY + 135, "F11: toggle graphics", Color.orange);
            font.drawString(fontX, fontY + 150, "F12: toggle hitboxes", Color.orange);

            String text = "NONE";
            if (this.getActiveGUIController() != null) {
                text = this.getActiveGUIController().getName();

                MyGUIController controller = (MyGUIController) this.getActiveGUIController();

                if (controller.getHoveredElement() != null) {
                    font.drawString(fontX, fontY + 180, "Hovered: " + controller.getHoveredElement().getEntityID(), Color.yellow);
                }
                if (controller.getFocusedElement() != null) {
                    font.drawString(fontX, fontY + 195, "Focused: " + controller.getFocusedElement().getEntityID(), Color.yellow);
                }
                if (controller.hotkeysActive) {
                    font.drawString(fontX, fontY + 210, "Hotkeys active", Color.green);
                }
            }
            font.drawString(fontX, fontY + 165, "Active UI: " + text, Color.red);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

}
