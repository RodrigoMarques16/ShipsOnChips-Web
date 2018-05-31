package sonc.battle;

/**
 * Type of missile that that seeks the target
 * 
 * @author Rodrigo Marques
 */
public class GuidedMissile extends Munition {

	private static int	  damage			 = 600;
	private static int	  size				 = 2;
	private static int	  fireDelay			 = 60;
	private static double initialSpeed		 = 5;
	private static double maxMissileRotation = Math.PI / 64;

	private String		 color = "#0000FF";	// should be dynamic
	private MovingObject target;

	/**
	 * Create a bullet with a certain heading. The initial position with be that
	 * of the ship that fires the missile.
	 * 
	 * @param heading of the bullet
	 */
	public GuidedMissile(double heading, MovingObject target) {
		super(5, heading, initialSpeed);
		this.target = target;
	}

	@Override
	void move() {
		if (target != null && !target.isDestroyed())
			doRotate(headingTo(target) - getHeading());

	}

	/**
	 * Set the damage inflicted by a missile in the status of ship it hits. This
	 * method should be invoked before any battle.
	 * 
	 * @param damage inflicted by bullets
	 */
	static void setDamage(int damage) {
		GuidedMissile.damage = damage;
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
	 * Maximum rotation per turn of any missile.
	 * 
	 * @return maximum rotation
	 */
	static double getMaxMissileRotation() {
		return GuidedMissile.maxMissileRotation;
	}

	/**
	 * Set the maximum rotation per turn of a ship. This method should be used
	 * before instancing ships and cannot be invoked by concrete ships.
	 * 
	 * @param maxMissileRotation of guided missiles
	 */
	static void setMaxMissileRotation(double maxMissileRotation) {
		GuidedMissile.maxMissileRotation = maxMissileRotation;
	}

	/**
	 * Set the initial speed of a bullet. This method should be invoked before
	 * any battle.
	 * 
	 * @param speed of bullets
	 */
	static void setInitialSpeed(double speed) {
		GuidedMissile.initialSpeed = speed;
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
		GuidedMissile.fireDelay = fireDelay;
	}

	/**
	 * Get the delay for firing this kind of munition.
	 * 
	 * @return the fireDelay
	 */
	static int getFireDelay() {
		return GuidedMissile.fireDelay;
	}

	/**
	 * Set the size of this kind of munition. This method should be
	 * invoked before any battle.
	 * 
	 * @param size of this munition
	 */
	static void setSize(int size) {
		GuidedMissile.size = size;
	}

	@Override
	public int getSize() {
		return GuidedMissile.size;
	}

	@Override
	int fireDelay() {
		return GuidedMissile.fireDelay;
	}

	@Override
	double getMaxSpeed() {
		return GuidedMissile.initialSpeed;
	}

	@Override
	int getImpactDamage() {
		return GuidedMissile.damage;
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
		return maxMissileRotation;
	}

}
