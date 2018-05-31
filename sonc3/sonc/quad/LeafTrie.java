package sonc.quad;

import java.util.*;

/**
 * A Trie that has no descendants. This class corresponds to the Leaf in the
 * <b>Composite</b> design pattern.
 * 
 * @author Afonso Brandão
 */
public class LeafTrie<T extends HasPoint> extends Trie<T> {

	private Set<T> points;

	LeafTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
		super(topLeftX, topLeftY, bottomRightX, bottomRightY);
		points = new HashSet<T>();
	}

	@Override
	Trie<T> insertReplace(T point) {
		points.clear();
		return insert(point);
	}

	@Override
	Trie<T> insert(T point) {
		if (points.size() < getCapacity()) {
			points.add(point);
			return this;
		}

		NodeTrie<T> node = new NodeTrie<T>(topLeftX, topLeftY, bottomRightX, bottomRightY);
		for (T p : points) {
			node.insert(p);
		}
		node.insert(point);

		return node;
	}

	@Override
	void collectAll(Set<T> nodes) {
		if (nodes == null)
			nodes = new HashSet<T>();
		for (T p : points) {
			nodes.add(p);
		}
	}

	@Override
	void collectNear(double x, double y, double radius, Set<T> nodes) {
		for (T p : points) {
			if (getDistance(x, y, p.getX(), p.getY()) <= radius) {
				nodes.add(p);
			}
		}
	}

	@Override
	T find(T point) {
		for (T p : points) {
			if (p.getX() == point.getX() && p.getY() == point.getY())
				return point;
		}
		return null;
	}

	@Override
	void delete(T point) {
		points.remove(point);
	}
}