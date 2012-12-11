package de.gemo.game.events.gui;

import java.awt.event.ActionEvent;

import de.gemo.game.entity.Entity;

public class HoverBeginEvent extends ActionEvent {

    private static final long serialVersionUID = -1057295720643481614L;

    public HoverBeginEvent(Entity source) {
        super(source, EventEnums.HOVER_BEGIN.ordinal(), "HOVER_BEGIN");
    }
}
