package de.gemo.game.physics.entity;

public interface GrabberHolder {


	Grabber getGrabber(GrabDirection direction);

	void moveGrabber(float x, float y, GrabDirection direction);

}
