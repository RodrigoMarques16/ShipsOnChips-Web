package sonc.battle;

import java.util.Set;

/**
 * The class common to all ships. It is meant to be specialized by concrete
 * ships, those submitted by players. Concrete ships are expected to be in the
 * default package (no package declaration) and provide an implementation of the
 * move() method. This method is invoked once per round by the game engine (the
 * World) and will be able to execute actions such as:
 * 
 * <ul>
 * <li>rotate(double) - the ship by given angle in radians</li>
 * <li>changeSpeed(double) - the change the speed of ship and sail around the
 * world</li>
 * <li>fire(Munition) - to fire a munition against other ships</li>
 * </ul>
 * 
 * These methods create instances of the corresponding implementation of the
 * ShipCommand interface and store them, so that latter on the execute() method
 * may be invoked to execute them. With this approach, if the methods in the
 * list above are called more than once, only the last is effectively executed.
 * 
 * Other methods can be override, such as:
 * <ul>
 * <li>init() - to initialize the ship</li>
 * <li>getName() - to assign a name to the ship</li>
 * <li>getColor() - to assign an HTML color to the ship</li>
 * </ul>
 * 
 * This can be instantiated as it provides default implementations to all
 * methods that can be provided by concrete ships. The default definitions are
 * always empty instruction blocks.
 * 
 * @author Rodrigo Marques
 *
 */
public class Ship extends MovingObject {

	private static int	  damage			 = 1000;
	private static int	  maxStatus			 = 1000;
	private static int	  size				 = 10;
	private static double maxShipRotation	 = 5;
	private static double maxSpeed			 = 10;
	private static double maxShipSpeedChange = 1;

	private int	  lastFireRound;
	private int	  points;
	private World world;

	private ShipCommand command;

	public Ship() {
		super(maxStatus, 0, 0);
		lastFireRound = Integer.MIN_VALUE;
		points = 0;
	}

	/**
	 * Set the amount of damage produced by ships. Should be invoked before
	 * instancing any ships. This method cannot be invoked by concrete ships.
	 * 
	 * @param damage produced by ships when they collide
	 */
	static void setDamage(int damage) {
		Ship.damage = damage;
	}

	/**
	 * The damaged produced by ship when it collides with another ship
	 * 
	 * @return damage produced by ships when they collide
	 */
	static int getDamage() {
		return Ship.damage;
	}

	/**
	 * Maximum rotation per turn of any ship.
	 * 
	 * @return maximum rotation
	 */
	static double getMaxShipRotation() {
		return Ship.maxShipRotation;
	}

	/**
	 * Set the maximum rotation per turn of a ship. Should be used before
	 * instancing ships. This method cannot be invoked by concrete ships.
	 * 
	 * @param maxShipRotation
	 */
	static void setMaxShipRotation(double maxShipRotation) {
		Ship.maxShipRotation = maxShipRotation;
	}

	/**
	 * Get the maximum speed ship change per turn. This method cannot be invoked
	 * by concrete ships.
	 * 
	 * @return maximum speed in absolute value
	 */
	static double getMaxShipSpeedChange() {
		return Ship.maxShipSpeedChange;
	}

	/**
	 * Set the maximum speed ship change per turn. Should be used before
	 * instancing ships. This method cannot be invoked by concrete ships.
	 * 
	 * @param maxShipSpeedChange per turn
	 */
	static void setMaxShipSpeedChange(double maxShipSpeedChange) {
		Ship.maxShipSpeedChange = maxShipSpeedChange;
	}

	/**
	 * The initial status of a ship
	 * 
	 * @return status of ship on start
	 */
	static int getMaxStatus() {
		return maxStatus;
	}

	/**
	 * Set the initial status of any ship. It affects ships created afterwards.
	 * This method cannot be invoked by concrete ships.
	 * 
	 * @param maxStatus of a ship when it starts
	 */
	static void setMaxStatus(int maxStatus) {
		Ship.maxStatus = maxStatus;
	}

	/**
	 * The world where this ship sails. This method is available to concrete
	 * ships
	 * 
	 * @return world instance
	 */
	protected World getWorld() {
		return world;
	}

	/**
	 * Set this ship in a world This method is not available to concrete ships
	 * 
	 * @param world where ship will sail
	 */
	void setWorld(World world) {
		this.world = world;
	}

	/**
	 * Get the last round when this ship fired a munition. This method is
	 * available to concrete ships.
	 * 
	 * @return last round when ship fired or Integer.MIN_VALUE if it never fired
	 */
	protected int getLastFireRound() {
		return lastFireRound;
	}
	
	/**
	 * Check if this ship can fire the given munition. The difference between
	 * the current round and the last fired round must be greater than the fire
	 * delay for this munition.
	 * 
	 * @param munition to be fired
	 * @return true if munition can be fired or false otherwise
	 */
	protected boolean canFire(Munition munition) {
		if ((world.getCurrentRound() - lastFireRound) > munition.fireDelay()
				|| lastFireRound == Integer.MIN_VALUE)
			return true;
		return false;
	}

	/**
	 * Set the last round when this ship fired a munition. This method is not
	 * available to concrete ships
	 * 
	 * @param lastFireRound round when this ship fired for the last time
	 */
	void setLastFireRound(int lastFireRound) {
		this.lastFireRound = lastFireRound;
	}

	/**
	 * The latest command set by the concrete ship
	 * 
	 * @return command set by concrete ship
	 */
	ShipCommand getCommand() {
		return command;
	}

	/**
	 * Set a command resulting from a method invoked by the move() method
	 * executed by a concrete ship. This method should only be invoked from a
	 * World instance and cannot be invoked by concrete ships.
	 * 
	 * @param command to be executed
	 */
	void setCommand(ShipCommand command) {
		this.command = command;
	}

	/**
	 * Get current points from this ship
	 * 
	 * @return points as integer
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Reset points of this ship to zero
	 */
	void resetPoints() {
		points = 0;
	}

	/**
	 * Add given points to this ship
	 * 
	 * @param points
	 */
	void addPoints(int points) {
		this.points += points;
	}

	@Override
	double getMaxSpeed() {
		return Ship.maxSpeed;
	}

	@Override
	double getMaxSpeedChange() {
		return Ship.maxShipSpeedChange;
	}

	@Override
	double getMaxRotation() {
		return Ship.maxShipRotation;
	}

	/**
	 * Execute the latest command defined by the concrete ship. This method
	 * should only be invoked from a World instance and cannot be invoked by
	 * concrete ships.
	 */
	void execute() {
		if (command != null) {
			command.execute();
		}
	}

	/**
	 * Change the speed of this ship by given delta. This command will be
	 * effective only if it is the last executed in a round
	 * 
	 * @param delta variation of speed
	 */
	protected final void changeSpeed(double delta) {
		command = new ChangeSpeedCommand(this, delta);
	}

	/**
	 * Rotate the ship by given angle. This command will be effective only if it
	 * is the last executed in a round. This command cannot be override by
	 * concrete ships.
	 * 
	 * @param delta the rotation angle
	 */
	protected final void rotate(double delta) {
		command = new RotateCommand(this, delta);
	}

	/**
	 * Fire a munition given as parameter start in current position (e.g. {code
	 * fire(new Bullet(headingTo(enemy)))}. This command will be effective only
	 * if it is the last executed in a round. This command cannot be override by
	 * concrete ships.
	 * 
	 * @param munition to be fired from ship
	 */
	protected final void fire(Munition munition) {
		command = new FireCommand(world, this, munition);
	}

	/**
	 * A set of all other ships in the world, except this one
	 * 
	 * @return a set of ships
	 */
	protected final Set<Ship> getOtherShips() {
		Set<Ship> ships = world.getShips();
		ships.remove(this);
		return ships;
	}

	/**
	 * Initialize you ship. This method is called when the ship starts sailing.
	 * Use this method
	 */
	protected void init() {
	}

	/**
	 * Move your ship. Redefine this method to implement how this ship must be
	 * moved.
	 */
	@Override
	protected void move() {
	}

	@Override
	final int getImpactDamage() {
		return Ship.damage;
	}

	@Override
	final Ship getOrigin() {
		return null;
	}

	@Override
	public final int getSize() {
		return Ship.size;
	}

	@Override
	public String getColor() {
		return "#FF0000";
	}

	/**
	 * The name of this ship. This information is used on the score board.
	 * 
	 * @return name of this ship as String
	 */
	public String getName() {
		return "DefaultName";
	}

}
