package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class HoverEndEvent extends ActionEvent {

    private static final long serialVersionUID = 6119539091779422887L;

    public HoverEndEvent(Entity source) {
        super(source, EventEnums.HOVER_END.ordinal(), "HOVER_END");
    }
}
