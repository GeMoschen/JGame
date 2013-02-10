package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class FocusGainedEvent extends ActionEvent {

    private static final long serialVersionUID = -6251152313347751217L;

    public FocusGainedEvent(Entity source) {
        super(source, EventEnums.FOCUS_GAINED.ordinal(), "FOCUS_GAINED");
    }
}
