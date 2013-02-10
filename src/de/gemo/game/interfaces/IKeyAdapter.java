package de.gemo.game.interfaces;

import de.gemo.game.events.keyboard.KeyEvent;

public interface IKeyAdapter {

    public boolean handleKeyHold(KeyEvent event);

    public boolean handleKeyPressed(KeyEvent event);

    public boolean handleKeyReleased(KeyEvent event);

}
