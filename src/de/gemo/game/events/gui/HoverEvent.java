package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class HoverEvent extends ActionEvent {

    private static final long serialVersionUID = -5543558256305369842L;

    public HoverEvent(Entity source) {
        super(source, EventEnums.HOVER.ordinal(), "HOVER");
    }
}
