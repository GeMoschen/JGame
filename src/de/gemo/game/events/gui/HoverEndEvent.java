package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.AbstractEntity;

public class HoverEndEvent extends ActionEvent {

    private static final long serialVersionUID = 6119539091779422887L;

    public HoverEndEvent(AbstractEntity source) {
        super(source, EventEnums.HOVER_END.ordinal(), "HOVER_END");
    }
}
