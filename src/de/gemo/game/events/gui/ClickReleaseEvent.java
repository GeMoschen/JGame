package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.AbstractEntity;

public class ClickReleaseEvent extends ActionEvent {

    private static final long serialVersionUID = 3527738960075035897L;

    public ClickReleaseEvent(AbstractEntity source) {
        super(source, EventEnums.CLICK_RELEASE.ordinal(), "CLICK_RELEASE");
    }
}
