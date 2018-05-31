package sonc.battle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import sonc.quad.PointQuadtree;
import sonc.shared.Movie;
import sonc.shared.SoncException;
import sonc.utils.SafeExecutor;
import sonc.utils.SoncMath;

/**
 * A rectangular area where battles take place. It contains a collection of
 * moving objects, some of which are ships (other are munitions). It provides
 * methods for updating the state of moving objects within it. Those that fell
 * of the boundaries are automatically discarded, including ships.
 * 
 * The state of this class includes a PointQuadtree for managing moving objects
 * and efficiently detecting collisions.
 * 
 * The are a number of static properties that parameterize worlds. Their getters
 * are public put the setter cannot be visible to concrete ships (those
 * submitted by players).
 * 
 * The main method provided by this class is battle(List) that receives a list
 * of Ship and returns a Movie.
 * 
 * @author Rodrigo Marques
 */
public class World {

	private static int	  rounds			= 400;
	private static double margin			= 100;
	private static double width				= 1000;
	private static double height			= 1000;
	private static double collisionDistance	= 10;

	private int currentRound = 0;

	private Set<Ship> ships;

	private PointQuadtree<MovingObject> movingObjects;

	public World() {
		currentRound = 0;
		ships = new HashSet<Ship>();
		movingObjects = new PointQuadtree<>(0, height, width, 0);
	}

	/**
	 * Number of rounds in a battle
	 * 
	 * @return round in a battle
	 */
	public static int getRounds() {
		return rounds;
	}

	/**
	 * Set number of rounds in a battle. This method should be executed before
	 * battles and cannot be visible to concrete ships
	 * 
	 * @param rounds in a battle
	 */
	static void setRounds(int rounds) {
		World.rounds = rounds;
	}

	/**
	 * Margin from border used for placing ships within the world
	 * 
	 * @return margin to border when placing ships
	 */
	public static double getMargin() {
		return margin;
	}

	/**
	 * Set the margin from border used for placing ships within the world. This
	 * method should be executed before battles and cannot be visible to
	 * concrete ships.
	 * 
	 * @param margin the margin to set
	 */
	static void setMargin(double margin) {
		World.margin = margin;
	}

	/**
	 * The width of the world
	 * 
	 * @return width of world
	 */
	public static double getWidth() {
		return width;
	}

	/**
	 * Set the width of the world. This method should be executed before battles
	 * and cannot be visible to concrete ships.
	 * 
	 * @param width of world
	 */
	static void setWidth(double width) {
		World.width = width;
	}

	/**
	 * The height of the world
	 * 
	 * @return the height
	 */
	public static double getHeight() {
		return height;
	}

	/**
	 * Set the height of the world This method should be executed before battles
	 * and cannot be visible to concrete ships.
	 * 
	 * @param height of world
	 */
	static void setHeight(double height) {
		World.height = height;
	}

	/**
	 * Minimum distance between object to be considered as a collision
	 * 
	 * @return distance in pixels
	 */
	public static double getCollisionDistance() {
		return collisionDistance;
	}

	/**
	 * Set minimum distance between object to be considered as a collision
	 * 
	 * @param collisionDistance in pixels
	 */
	static void setCollisionDistance(double collisionDistance) {
		World.collisionDistance = collisionDistance;
	}

	/**
	 * Add a ship to this world. Set it in a random position. Initialize the
	 * ship and reset its points
	 * 
	 * @param ship to be added
	 * @throws SoncException if ship is added out of bounds
	 */
	void addShipAtRandom(Ship ship) {
		double x = ThreadLocalRandom.current().nextDouble(margin, width - margin);
		double y = ThreadLocalRandom.current().nextDouble(margin, height - margin);
		double heading = ThreadLocalRandom.current().nextDouble(0, SoncMath.TWO_PI);
		try {
			addShipAt(ship, x, y, heading);
		} catch (SoncException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a ship to this world. Define position and heading
	 * Initialize the ship and reset its points. This method is useful for testing
	 * 
	 * @param ship to be added
	 * @param x coordinate of initial position
	 * @param y coordinate of initial position
	 * @param heading of ship at the initial position
	 * @throws SoncException if ship is added out of bounds
	 */
	void addShipAt(Ship ship, double x, double y, double heading) throws SoncException {
		ship.setX(x);
		ship.setY(y);

		ship.setHeading(heading);
		ship.resetPoints();
		ship.setWorld(this);

		if (checkCall(ship::init)) {
			movingObjects.insert(ship);
			ships.add(ship);
		} else
			throw new SoncException("Failed to initiate");
	}

	/**
	 * Update the world by moving objects, removing those outside the
	 * boundaries, checking those that were hit by another one, reducing their
	 * status, terminating those that reach zero. This method uses and creates a
	 * new version of the PointQuadtree containing all moving objects in the
	 * world instance.
	 */
	void update() {
		PointQuadtree<MovingObject> tree = new PointQuadtree<>(0, height, width, 0);

		for (MovingObject obj : movingObjects.getAll()) {

			if (!obj.isDestroyed()) {

				if (!(obj instanceof Ship)) // move missiles
					obj.move();

				obj.updatePosition();

				Set<MovingObject> collisions = movingObjects.findNear(obj.getX(), obj.getY(),
						getCollisionDistance() + obj.getSize() * 5); // hack to prevent infinite loop in stalker vs stalker

				for (MovingObject collision : collisions) {
					if (collision != obj && collision.getOrigin() != obj && 
							!collision.isDestroyed() && !obj.isDestroyed()) {

						obj.hitBy(collision);
						collision.hitBy(obj);

						if (collision.getOrigin() != null)
							collision.getOrigin().addPoints(collision.getImpactDamage());
						if (obj.getOrigin() != null)
							obj.getOrigin().addPoints(obj.getImpactDamage());

						if (obj.isDestroyed())
							movingObjects.delete(obj);
						if (collision.isDestroyed())
							movingObjects.delete(collision);
					}
				}

				try {
					if (!obj.isDestroyed())
						tree.insert(obj);
				} catch (Exception e) {
					obj.setStatus(0);
				}
			}
		}
		movingObjects = tree;
	}

	/**
	 * Make a battle with given ships. The battle unfolds for a number of rounds
	 * defined by the rounds property. The init() method of each of these ships
	 * is invoked in the beginning, and the method move() is invoked in each
	 * turn. These two methods are invoked trough the safe executor. If they
	 * raise any exception, including those due to timeout or to attempt to use
	 * system resources, then the ship is removed from the battle.
	 * 
	 * @param ships
	 * @return
	 */
	public Movie battle(List<Ship> ships) {
		Movie movie = new Movie();

		this.movingObjects = new PointQuadtree<>(0, height, width, 0);
		this.ships = new HashSet<>();

		for (Ship ship : ships) {
			addShipAtRandom(ship);
		}

		this.currentRound = 0;

		while (getCurrentRound() < rounds) { //&& getAliveShips() > 1) { this is important but 
			for (Ship ship : this.ships) {
				if (!ship.isDestroyed()) {
					if (checkCall(ship::move))
						ship.execute();
					else
						movingObjects.delete(ship);
				}
			}

			update();
			movie.newFrame();
			for (MovingObject obj : this.getMovingObjects()) {
				movie.addOblong((int) obj.getX(), (int) obj.getY(), (float) obj.getHeading(),
						obj.getSize(), obj.getColor());
			}
			for (Ship ship : this.ships) {
				movie.addScore(ship.getName(), ship.getPoints(), ship.getStatus());
			}

			this.currentRound++;
		}

		return movie;
	}

	/**
	 * The numbers of ships alive in an ongoing battle
	 * 
	 * @return ships alive
	 */
	int getAliveShips() {
		int count = 0;
		for (Ship ship : ships) {
			if (!ship.isDestroyed())
				count++;
		}
		return count;
	}

	/**
	 * Execute a method safely
	 * 
	 * @param runnable method
	 * @return true if executed, false if failed
	 */
	private Boolean checkCall(Runnable runnable) {
		try {
			SafeExecutor.executeSafelly(runnable);
			return true;
		} catch (Exception cause) {
			return false;
		}
	}

	/**
	 * Get the number of round from the initial one (round 0). The current
	 * number of rounds is need keeping delays between consecutive firings (of
	 * munition). This method is available to concrete ships
	 * 
	 * @return currentRound of the battle
	 */
	public int getCurrentRound() {
		return currentRound;
	}

	/**
	 * Set the number of round from the initial one (round 0). The current
	 * number of rounds is needed for keeping delays between consecutive firings
	 * (of munition). This method is not available to concrete ships
	 * 
	 * @param currentRound of the battle
	 */
	void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	Boolean insideBoundaries(MovingObject obj) {
		double x = obj.getX();
		double y = obj.getY();
		return (x >= 0 && x <= width) && (y >= 0 && y <= height);
	}

	/**
	 * Add a moving object to the world
	 * 
	 * @param added object
	 */
	void addMovingObject(MovingObject added) {
		// if (insideBoundaries(added))
		movingObjects.insert(added);
	}

	/**
	 * The set of all moving objects in the world. Mostly for tests
	 * 
	 * @return set of MovingObject instances
	 */
	public Set<MovingObject> getMovingObjects() {
		return movingObjects.getAll();
	}

	/**
	 * @return the ships
	 */
	Set<Ship> getShips() {
		return new HashSet<Ship>(ships);
	}

}
