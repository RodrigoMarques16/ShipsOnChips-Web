package sonc.quad;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This class follows the <b>Facade</b> design pattern and presents a presents a
 * single access point to manage <i>quad trees</i>. It provides methods for
 * inserting, deleting and finding elements implementing {@link HasPoint}.
 * This class corresponds to the <b>Client</b> in the <b>Composite</b> design
 * pattern used in this package.
 * 
 * @author Rodrigo Marques
 *
 */
public class PointQuadtree<T extends HasPoint> {

	private Trie<T> root;
	
	public PointQuadtree(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
		this.root = new NodeTrie<>(topLeftX, topLeftY, bottomRightX, bottomRightY);
	}

	/**
	 * Find a recorded point with the same coordinates of given point
	 * 
	 * @param point with requested coordinates
	 * @return recorded point, if found; null otherwise
	 */
	public T find(T point) {
		return root.find(point);
	}

	/**
	 * Insert given point in the QuadTree
	 * 
	 * @param point to be inserted
	 */
	public void insert(T point) {
		root.insert(point);
	}

	/**
	 * Insert point, replacing existing point in the same position
	 * 
	 * @param point point to be inserted
	 */
	public void insertReplace(T point) {
		root.insertReplace(point);
	}

	/**
	 * Returns a set of points at a distance smaller or equal to radius from point
	 * with given coordinates.
	 * 
	 * @param x coordinate of point
	 * @param y coordinate of point
	 * @param radius from given point
	 * @return set of instances of type {@link HasPoint}
	 */
	public Set<T> findNear(double x, double y, double radius) {
		Set<T> set = new HashSet<T>();
		root.collectNear(x, y, radius, set);
		return set;
	}

	/**
	 * Delete given point from QuadTree, if it exists there
	 * 
	 * @param point to be deleted
	 */
	public void delete(T point) {
		root.delete(point);
	}

	/**
	 * A set with all points in the QuadTree
	 * 
	 * @return set of instances of type HasPoint
	 */
	public Set<T> getAll() {
		Set<T> set = new HashSet<T>();
		root.collectAll(set);
		return set;
	}

}
