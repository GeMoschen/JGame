package de.gemo.game.interfaces;

import de.gemo.game.events.keyboard.KeyEvent;

public interface IKeyController {

    public void onKeyHold(KeyEvent event);

    public void onKeyPressed(KeyEvent event);

    public void onKeyReleased(KeyEvent event);

}
