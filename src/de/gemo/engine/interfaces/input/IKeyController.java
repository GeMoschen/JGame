package de.gemo.engine.interfaces.input;

import de.gemo.engine.events.keyboard.KeyEvent;

public interface IKeyController {

    public void onKeyHold(KeyEvent event);

    public void onKeyPressed(KeyEvent event);

    public void onKeyReleased(KeyEvent event);

}
