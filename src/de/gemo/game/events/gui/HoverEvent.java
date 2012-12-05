package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.AbstractEntity;

public class HoverEvent extends ActionEvent {

    private static final long serialVersionUID = -5543558256305369842L;

    public HoverEvent(AbstractEntity source) {
        super(source, EventEnums.HOVER.ordinal(), "HOVER");
    }
}
