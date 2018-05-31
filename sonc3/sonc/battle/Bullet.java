package sonc.battle;

/**
 * A simple munition that moves in a straight line.
 * 
 * @author Rodrigo Marques
 */
public class Bullet extends Munition {

	private static int	  damage	   = 10;
	private static int	  size		   = 5;
	private static int	  fireDelay	   = 5;
	private static double initialSpeed = 20;

	private String color = "#00FF00";

	/**
	 * Create a bullet with a certain heading. The initial position with be that
	 * of the ship that fires the missile.
	 * 
	 * @param heading of the bullet
	 */
	public Bullet(double heading) {
		super(1, heading, initialSpeed);
	}

	/**
	 * Set the damage inflicted by a bullet in the status of ship it hits. This
	 * method should be invoked before any battle.
	 * 
	 * @param damage inflicted by bullets
	 */
	static void setDamage(int damage) {
		Bullet.damage = damage;
	}

	/**
	 * Get the damage inflicted by a bullet in the status of ship it hits.
	 * 
	 * @return damage inflicted by a bullet
	 */
	static int getDamage() {
		return damage;
	}

	/**
	 * Set the initial speed of a bullet. This method should be invoked before
	 * any battle.
	 * 
	 * @param speed of bullets
	 */
	static void setInitialSpeed(double speed) {
		Bullet.initialSpeed = speed;
	}

	/**
	 * Get the initial speed of a bullet.
	 * 
	 * @param speed of a bullet
	 */
	static double getInitialSpeed() {
		return initialSpeed;
	}

	/**
	 * Set the delay for firing this kind of munition. This method should be
	 * invoked before any battle.
	 * 
	 * @param fireDelay the fireDelay to set
	 */
	static void setFireDelay(int fireDelay) {
		Bullet.fireDelay = fireDelay;
	}

	/**
	 * Get the delay for firing this kind of munition.
	 * 
	 * @return the fireDelay
	 */
	static int getFireDelay() {
		return fireDelay;
	}

	/**
	 * @param size the size to set
	 */
	static void setSize(int size) {
		Bullet.size = size;
	}

	@Override
	int fireDelay() {
		return getFireDelay();
	}

	@Override
	double getMaxSpeed() {
		return initialSpeed;
	}

	@Override
	int getImpactDamage() {
		return getDamage();
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String getColor() {
		return color;
	}

	@Override
	double getMaxSpeedChange() {
		return 0;
	}

	@Override
	double getMaxRotation() {
		return 0;
	}

}
