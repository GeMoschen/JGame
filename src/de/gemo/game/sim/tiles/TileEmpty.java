package de.gemo.game.sim.tiles;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class TileEmpty extends AbstractTile {

    public TileEmpty() {
        super(1, "Empty", new Color(255, 255, 255), false);
    }

    @Override
    public void onChange(int x, int y, AbstractTile oldTile, AbstractTile newTile) {
    }

    @Override
    public void render(int x, int y) {
        glPushMatrix();
        {
            glTranslatef(x * AbstractTile.TILE_SIZE + x, y * AbstractTile.TILE_SIZE + y, 0);
            glLineWidth(1);
            glDisable(GL_LIGHTING);
            glEnable(GL_BLEND);
            glColor4f(1f, 1f, 1f, 0.06f);
            glBegin(GL_LINE_LOOP);
            {
                glVertex2i(0, 0);
                glVertex2i(TILE_SIZE, 0);
                glVertex2i(TILE_SIZE, TILE_SIZE);
                glVertex2i(0, TILE_SIZE);
            }
            glEnd();
        }
        glPopMatrix();
    }
}
