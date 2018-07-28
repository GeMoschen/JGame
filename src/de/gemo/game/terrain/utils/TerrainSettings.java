package de.gemo.game.terrain.utils;

import java.util.*;

public class TerrainSettings {
    private float frequencyX = 0.0025f, frequencyY = 0.005f;
    private float offsetX, offsetY;

    private float lowerCutOff = 0.35f, upperCutOff = 1.6f;

    public TerrainSettings(long seed) {
        setSeed(seed);
    }

    public TerrainSettings(String seed) {
        this(seed.hashCode());
    }

    public TerrainSettings() {
        this((long) (Math.random() * 500000));
    }

    public void setSeed(long seed) {
        Random random = new Random(seed);
        final int seedMagic = 5000;
        this.offsetX = random.nextFloat() * (random.nextFloat() * seedMagic);// / (random.nextFloat() * seedMagic) * (random.nextFloat() * 800 / frequencyX);
        this.offsetY = random.nextFloat() * (random.nextFloat() * seedMagic);//  / (random.nextFloat() * seedMagic)* (random.nextFloat() * 800 / frequencyY);
        System.out.println(offsetX + " / " + offsetY);
    }

    public void setSeed(String seed) {
        this.setSeed(seed.hashCode());
    }

    public float getFrequencyX() {
        return frequencyX;
    }

    public float getFrequencyY() {
        return frequencyY;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getLowerCutOff() {
        return lowerCutOff;
    }

    public float getUpperCutOff() {
        return upperCutOff;
    }

}
