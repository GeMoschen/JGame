package de.gemo.game.polygon.entities;

import de.gemo.game.polygon.navigation.Path;
import de.gemo.gameengine.collision.Hitbox;
import de.gemo.gameengine.units.Vector3f;

import java.util.List;

public class Enemy {

    private final Hitbox _hitbox;
    private final Vector3f _position;
    private final Vector3f _velocity;
    private Path _path;
    private int _currentNode = -1;
    private float _angle;

    private final ViewField _viewField;

    public Enemy(final float x, final float y, final float angle) {
        _position = new Vector3f(x, y, 0);
        _velocity = new Vector3f();
        _hitbox = new Hitbox(_position);
        _hitbox.addPoint(-6, 0);
        _hitbox.addPoint(0, -6);
        _hitbox.addPoint(+6, 0);
        _hitbox.setAngle(angle);
        _angle = angle;
        _viewField = new ViewField(_position);
    }

    public Vector3f getPosition() {
        return _position;
    }

    public Enemy setPath(Path path) {
        _path = path;
        _currentNode = path == null ? -1 : 0;
        return this;
    }


    private Vector3f pathFollowing() {
        if (_path != null && _currentNode != -1) {
            Vector3f target = _path.getNode(_currentNode);
            if (Math.abs(_position.distanceTo(target)) < 5) {
                _currentNode++;
                if (_currentNode >= _path.getLength()) {
                    _currentNode = -1;
                    _path = null;
                    target = null;
                } else {
                    target = _path.getNode(_currentNode);
                }
            }
            return target;
        }
        return null;
    }

    public void updatePosition() {
        final Vector3f targetVector = pathFollowing();
        if (targetVector != null) {
            final Vector3f normalized = Vector3f.normalize(Vector3f.sub(targetVector, _position));
            _velocity.set(normalized.getX(), normalized.getY(), 0);
            _hitbox.setAngle(targetVector.getAngle(_position));
            _viewField.setAngle(_hitbox.getAngle());
            _hitbox.move(_velocity.getX(), _velocity.getY());
            _viewField.move(_velocity.getX(), _velocity.getY());
        }
    }

    public void render() {
        _viewField.render();
        _hitbox.render();
    }

    public void updateCollisions(List<Hitbox> viewObstacles) {
        _viewField.updateCollisions(viewObstacles);
    }
}
