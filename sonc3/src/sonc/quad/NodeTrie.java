package sonc.quad;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Trie with 4 sub tries with equal dimensions covering all its area. This class
 * corresponds to the <i>Composite</i> in the <i>Composite</i> design pattern.
 * 
 * @author Afonso Brandão
 */
class NodeTrie<T extends HasPoint> extends Trie<T> {

	Map<Quadrant, Trie<T>> descendants;

	NodeTrie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
		super(topLeftX, topLeftY, bottomRightX, bottomRightY);
		descendants = new HashMap<Quadrant, Trie<T>>();

		/*
		 * a----b----c
		 * | NW | NE |	a = (0,y)
		 * |    |    |	c = (x,y)
		 * d----e----f
		 * | SW | SE |  g = (0,0)
		 * |    |    |  i = (x,0)
		 * g----h----i  
		 */

		double middleX = (bottomRightX + topLeftX) / 2;
		double middleY = (topLeftY + bottomRightY) / 2;

		descendants.put(Quadrant.NW, new LeafTrie<T>(topLeftX, topLeftY, middleX, middleY));
		descendants.put(Quadrant.NE, new LeafTrie<T>(middleX, topLeftY, bottomRightX, middleY));
		descendants.put(Quadrant.SW, new LeafTrie<T>(topLeftX, middleY, middleX, bottomRightY));
		descendants.put(Quadrant.SE, new LeafTrie<T>(middleX, middleY, bottomRightX, bottomRightY));

	}

	@Override
	T find(T point) {
		return descendants.get(quadrantOf(point)).find(point);
	}

	Quadrant quadrantOf(T point) {

		double x = point.getX();
		double y = point.getY();

		if (x < topLeftX || x > bottomRightX || y > topLeftY || y < bottomRightY) {
			throw new PointOutOfBoundException();
		}

		double middleX = (topLeftX + bottomRightX) / 2;
		double middleY = (topLeftY + bottomRightY) / 2;

		if (x >= middleX) {
			if (y >= middleY) {
				return Quadrant.NE;
			} else if (y < middleY)
				return Quadrant.SE;
		} else if (x < middleX) {
			if (y >= middleY) {
				return Quadrant.NW;
			} else if (y < middleY)
				return Quadrant.SW;
		}

		throw new PointOutOfBoundException();
	}

	@Override
	Trie<T> insert(T point) {
		Quadrant quad = quadrantOf(point);
		Trie<T> tmp = descendants.get(quad).insert(point);
		descendants.put(quad, tmp);
		return this;
	}

	@Override
	Trie<T> insertReplace(T point) {
		Quadrant quad = quadrantOf(point);
		Trie<T> tmp = descendants.get(quad);
		descendants.put(quad, tmp.insertReplace(point));
		return this;
	}

	void delete(T point) {
		descendants.get(quadrantOf(point)).delete(point);
	}

	@Override
	void collectNear(double x, double y, double radius, Set<T> nodes) {
		if (nodes == null)
			nodes = new HashSet<T>();
		descendants.get(Quadrant.NW).collectNear(x, y, radius, nodes);
		descendants.get(Quadrant.NE).collectNear(x, y, radius, nodes);
		descendants.get(Quadrant.SW).collectNear(x, y, radius, nodes);
		descendants.get(Quadrant.SE).collectNear(x, y, radius, nodes);
	}

	@Override
	void collectAll(Set<T> nodes) {
		if (nodes == null)
			nodes = new HashSet<T>();
		descendants.get(Quadrant.NW).collectAll(nodes);
		descendants.get(Quadrant.NE).collectAll(nodes);
		descendants.get(Quadrant.SW).collectAll(nodes);
		descendants.get(Quadrant.SE).collectAll(nodes);
	}

	@Override
	public String toString() {
		return null;
	}

}