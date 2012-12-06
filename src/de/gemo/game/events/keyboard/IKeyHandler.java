package de.gemo.game.events.keyboard;

public interface IKeyHandler {

    public void onKeyHold(KeyEvent event);

    public void onKeyPressed(KeyEvent event);

    public void onKeyReleased(KeyEvent event);

}
