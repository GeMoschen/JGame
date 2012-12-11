package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class ClickReleaseEvent extends ActionEvent {

    private static final long serialVersionUID = 3527738960075035897L;

    public ClickReleaseEvent(Entity source) {
        super(source, EventEnums.CLICK_RELEASE.ordinal(), "CLICK_RELEASE");
    }
}
