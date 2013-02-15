package de.gemo.engine.gui;

import java.util.HashMap;

public class RadioGroup {
    private HashMap<Integer, GUIRadioButton> radioList = new HashMap<Integer, GUIRadioButton>();
    private GUIRadioButton activeRadioButton = null;

    public void unsetActiveElement() {
        if (this.activeRadioButton != null) {
            this.activeRadioButton.setChecked(false);
        }
        this.activeRadioButton = null;
    }

    public void setActiveElement(GUIRadioButton element) {
        if (this.activeRadioButton != element) {
            this.unsetActiveElement();
        }
        this.activeRadioButton = element;
    }

    public GUIRadioButton getActiveElement() {
        return this.activeRadioButton;
    }

    public void addElement(GUIRadioButton element) {
        if (!this.radioList.containsKey(element.getEntityID())) {
            this.radioList.put(element.getEntityID(), element);
        }
    }

    public void removeElement(GUIRadioButton element) {
        if (this.getActiveElement() == element) {
            this.unsetActiveElement();
        }
        this.radioList.remove(element.getEntityID());
    }
}