package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.AbstractEntity;

public class ClickBeginEvent extends ActionEvent {

    private static final long serialVersionUID = -6019261658484598678L;

    public ClickBeginEvent(AbstractEntity source) {
        super(source, EventEnums.CLICK_BEGIN.ordinal(), "CLICK_BEGIN");
    }
}
