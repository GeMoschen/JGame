package de.gemo.gameengine.units.quadtree;

public interface Func<T> {
    public void call(QuadTree<T> quadTree, Node<T> node);
}
