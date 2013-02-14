package de.gemo.engine.core.debug;

import de.gemo.engine.manager.GUIManager;

public abstract class AbstractDebugMonitor {
    private int FPS = 0;
    private int delta = 0;
    private boolean showHitboxes = false;
    private boolean showGraphics = true;
    private boolean useVSync = true;
    private boolean showExtended = true;
    private GUIManager activeGUIManager;

    public final int getFPS() {
        return FPS;
    }

    public final void setFPS(int fPS) {
        FPS = fPS;
    }

    public final boolean isShowHitboxes() {
        return showHitboxes;
    }

    public final void setShowHitboxes(boolean showHitboxes) {
        this.showHitboxes = showHitboxes;
    }

    public final boolean isUseVSync() {
        return useVSync;
    }

    public final void setUseVSync(boolean useVSync) {
        this.useVSync = useVSync;
    }

    public final boolean isShowExtended() {
        return showExtended;
    }

    public final void setShowExtended(boolean showExtended) {
        this.showExtended = showExtended;
    }

    public boolean isShowGraphics() {
        return showGraphics;
    }

    public void setShowGraphics(boolean showGraphics) {
        this.showGraphics = showGraphics;
    }

    public final int getDelta() {
        return delta;
    }

    public final void setDelta(int delta) {
        this.delta = delta;
    }

    public GUIManager getActiveGUIManager() {
        return activeGUIManager;
    }

    public void setActiveGUIManager(GUIManager activeGUIManager) {
        this.activeGUIManager = activeGUIManager;
    }

    public abstract void render();
}
