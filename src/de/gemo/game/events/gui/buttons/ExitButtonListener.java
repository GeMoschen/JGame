package de.gemo.game.events.gui.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.gemo.game.events.gui.EventEnums;

public class ExitButtonListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == EventEnums.CLICK_RELEASE.ordinal()) {
            System.exit(0);
        } else {
            System.out.println(e.getActionCommand());
        }
    }

}
