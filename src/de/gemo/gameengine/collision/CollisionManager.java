package de.gemo.gameengine.collision;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionManager {

	private HashMap<String, ICollisionable> registered = new HashMap<String, ICollisionable>();
	private HashMap<String, ICollisionable> collided = new HashMap<String, ICollisionable>();

	public boolean register(ICollisionable collisionable) {
		return (this.registered.put(collisionable.toString(), collisionable) == null);
	}

	public boolean unregister(ICollisionable collisionable) {
		return (this.registered.remove(collisionable.toString()) != null);
	}

	public void checkCollisions() {
		this.collided.clear();

		HashMap<ICollisionable, List<ICollisionable>> list = new HashMap<ICollisionable, List<ICollisionable>>();

		for (Map.Entry<String, ICollisionable> entry : this.registered.entrySet()) {
			// broadphase-collision
			List<ICollisionable> collisionList = this.broadphaseCollision(entry.getValue());

			// narrowphase-collision
			this.narrowphaseCollision(collisionList, entry.getValue());
			if (collisionList.size() > 0) {
				list.put(entry.getValue(), collisionList);
			}
		}

		// handle collisions
		for (Map.Entry<ICollisionable, List<ICollisionable>> entry : list.entrySet()) {
			for (ICollisionable collider : entry.getValue()) {
				entry.getKey().handleCollision(collider);
			}
			entry.getValue().clear();
		}

		// cleanup
		list.clear();
		list = null;
	}

	private List<ICollisionable> broadphaseCollision(ICollisionable collisionable) {
		List<ICollisionable> list = new ArrayList<ICollisionable>();
		for (Map.Entry<String, ICollisionable> entry : this.registered.entrySet()) {
			if (entry.getValue() == collisionable) {
				continue;
			}

			// ignore dead objects
			if (entry.getValue().isDead()) {
				continue;
			}

			// TODO: implement AABB and other things to handle broadphase
			// for now, we will simply add all objects...

			// if (entry.getValue().broadphaseColliding(collisionable)) {
			list.add(entry.getValue());
			// }
		}
		return list;
	}

	private List<ICollisionable> narrowphaseCollision(List<ICollisionable> list, ICollisionable collisionable) {
		ICollisionable other;
		for (int i = list.size() - 1; i >= 0; i--) {
			other = list.get(i);
			if (!collisionable.narrowphaseColliding(other)) {
				list.remove(i);
			}
		}
		return list;
	}
}
