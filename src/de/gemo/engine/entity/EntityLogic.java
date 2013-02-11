package de.gemo.engine.entity;

public class EntityLogic {

    private static int currentID = 0;

    public static int getNextFreeID() {
        return currentID++;
    }

    protected final int entityID;

    public EntityLogic() {
        this.entityID = EntityLogic.getNextFreeID();
    }

    public int getEntityID() {
        return entityID;
    }
}
