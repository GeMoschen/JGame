package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class FocusLostEvent extends ActionEvent {

    private static final long serialVersionUID = 1577017736282443863L;

    public FocusLostEvent(Entity source) {
        super(source, EventEnums.FOCUS_LOST.ordinal(), "FOCUS_LOST");
    }
}
