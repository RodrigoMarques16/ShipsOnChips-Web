package sonc.quad;

import sonc.utils.SoncMath;

/**
 * Abstract class common to all classes implementing the trie structure. Defines
 * methods required by those classes and provides general methods for checking
 * overlaps and computing distances.
 * This class corresponds to the <b>Component</b> in the <b>Composite</b> design
 * pattern.
 * 
 * @author Afonso Brandao
 */
abstract class Trie<T extends HasPoint> extends java.lang.Object {

	static enum Quadrant {
		NE, NW, SE, SW
	}

	static int capacity = 10;

	protected double bottomRightX;
	protected double bottomRightY;
	protected double topLeftX;
	protected double topLeftY;

	protected Trie(double topLeftX, double topLeftY, double bottomRightX, double bottomRightY) {
		this.topLeftX = topLeftX;
		this.topLeftY = topLeftY;
		this.bottomRightX = bottomRightX;
		this.bottomRightY = bottomRightY;
	}

	public static int getCapacity() {
		return capacity;
	}

	public static void setCapacity(int c) {
		capacity = c;
	}

	/**
	 * Find a recorded point with the same coordinates of given point
	 * 
	 * @param point with requested coordinates
	 * @return recorded point, if found; null otherwise
	 */
	abstract T find(T point);

	/**
	 * Insert given point
	 * 
	 * @param point to be inserted
	 * @return changed parent node
	 */
	abstract Trie<T> insert(T point);

	/**
	 * Insert given point, replacing existing points in same location
	 * 
	 * @param point to be inserted
	 * @return changed parent node
	 */
	abstract Trie<T> insertReplace(T point);

	/**
	 * Collect points at a distance smaller or equal to radius from (x,y) and place
	 * them in given list
	 * 
	 * @param x coordinate of point
	 * @param y coordinate of point
	 * @param radius from given point
	 * @param points set for collecting points
	 */
	abstract void collectNear(double x, double y, double radius, java.util.Set<T> points);

	/**
	 * Collect all points in this node and its descendants in given set
	 * 
	 * @param points set of HasPoint for collecting points
	 */
	abstract void collectAll(java.util.Set<T> points);

	/**
	 * Delete given point
	 * 
	 * @param point to delete
	 */
	abstract void delete(T point);

	/**
	 * Check if overlaps with given circle
	 * 
	 * @param x coordinate of circle
	 * @param y coordinate of circle
	 * @param radius of circle
	 * @return true if overlaps and false otherwise
	 */

	boolean overlaps(double x, double y, double radius) {

		double closest_x = SoncMath.clamp(x, topLeftX, bottomRightX);
		double closest_y = SoncMath.clamp(y, bottomRightY, topLeftY);

		if (getDistance(closest_x, x, closest_y, y) <= radius)
			return true;
		else
			return false;

	}

	/**
	 * Euclidean distance between two pair of coordinates of two points
	 * 
	 * @param x1 x coordinate of first point
	 * @param y1 y coordinate of first point
	 * @param x2 x coordinate of second point
	 * @param y2 y coordinate of second point
	 * @return distance between given points
	 */
	static double getDistance(double x1, double y1, double x2, double y2) {
		return Math.hypot(x2 - x1, y2 - y1);
		// return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
	}

	boolean inBoundaries(T point) {
		double x = point.getX();
		double y = point.getY();
		return (x >= topLeftX && x <= bottomRightX && y >= topLeftY && y <= bottomRightY);
	}

	@Override
	public java.lang.String toString() {
		return null;
	}

}