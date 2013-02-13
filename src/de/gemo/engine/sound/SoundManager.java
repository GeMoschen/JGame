package de.gemo.engine.sound;

import java.util.concurrent.ConcurrentLinkedQueue;

import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import de.cuina.fireandfuel.CodecJLayerMP3;

public class SoundManager {

    private static int ID = 0;

    protected ConcurrentLinkedQueue<String> soundsPlaying = new ConcurrentLinkedQueue<String>();

    private boolean loaded = false;
    protected SoundSystem soundSystem = null;
    private float volume = 1f;

    public SoundManager() {
        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            soundSystem = new SoundSystem();
            loaded = true;
        } catch (SoundSystemException e) {
            e.printStackTrace();
            loaded = false;
        }

    }

    public void setVolume(float volume) {
        if (!loaded) {
            return;
        }
        this.volume = volume;
        soundSystem.setMasterVolume(volume);
    }

    public float getVolume() {
        return volume;
    }

    public void playSound(int x, int y, int z) {
        if (!loaded) {
            return;
        }

        // File file = new File("test.mp3");
        // try {
        // String sourceName = ID + "_" + "test.mp3";
        // ID++;
        // // Sound sound = new Sound(sourceName, file.toURI().toURL());
        // // soundSystem.newSource(true, sound.getName(), sound.getUrl(), sourceName, false, x, y, z, 2, 16);
        // // soundSystem.play(sourceName);
        // // soundsPlaying.add(sourceName);
        // } catch (MalformedURLException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    public void stopAll() {
        if (!loaded) {
            return;
        }

        try {
            for (String name : soundsPlaying) {
                if (soundSystem.playing(name)) {
                    soundSystem.stop(name);
                }
            }
            soundSystem.cleanup();
            SoundSystemConfig.removeLibrary(LibraryLWJGLOpenAL.class);
        } catch (SoundSystemException e) {
            e.printStackTrace();
        }
    }
}
