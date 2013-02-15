package de.gemo.engine.exceptions;

public class NotEnoughTexturesException extends RuntimeException {

    private static final long serialVersionUID = -4791733447466390744L;

    public NotEnoughTexturesException(String message) {
        super(message);
    }

    public NotEnoughTexturesException() {
        this("Not enough animations!");
    }

    public NotEnoughTexturesException(int current, int needed) {
        this("Not enough animations! Should be at least " + needed + ", but is " + current + ".");
    }
}
