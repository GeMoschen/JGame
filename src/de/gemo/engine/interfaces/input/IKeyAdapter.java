package de.gemo.engine.interfaces.input;

import de.gemo.engine.events.keyboard.KeyEvent;

public interface IKeyAdapter {

    public boolean handleKeyHold(KeyEvent event);

    public boolean handleKeyPressed(KeyEvent event);

    public boolean handleKeyReleased(KeyEvent event);

}
