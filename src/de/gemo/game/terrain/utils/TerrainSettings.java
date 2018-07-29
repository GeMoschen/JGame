package de.gemo.game.terrain.utils;

import java.util.Random;

public class TerrainSettings {
    
    private float _frequencyX = 0.0025f, _frequencyY = 0.005f;
    private float _lowerCutOff = 0.35f, _upperCutOff = 1.6f;
    private float _offsetX, _offsetY;

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
        _offsetX = random.nextFloat() * (random.nextFloat() * seedMagic);// / (random.nextFloat() * seedMagic) * (random.nextFloat() * 800 / frequencyX);
        _offsetY = random.nextFloat() * (random.nextFloat() * seedMagic);//  / (random.nextFloat() * seedMagic)* (random.nextFloat() * 800 / frequencyY);
        System.out.println(_offsetX + " / " + _offsetY);
    }

    public void setSeed(String seed) {
        setSeed(seed.hashCode());
    }

    public float getFrequencyX() {
        return _frequencyX;
    }

    public float getFrequencyY() {
        return _frequencyY;
    }

    public float getOffsetX() {
        return _offsetX;
    }

    public float getOffsetY() {
        return _offsetY;
    }

    public float getLowerCutOff() {
        return _lowerCutOff;
    }

    public float getUpperCutOff() {
        return _upperCutOff;
    }

}
