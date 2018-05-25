package sonc.battle;

import sonc.quad.HasPoint;
import sonc.utils.SoncMath;

/**
 * Common class to all munitions in the game, including bullets and guided
 * missiles.
 * 
 * @author Rodrigo Marques
 */
abstract class Munition extends MovingObject implements HasPoint {

	Ship origin;

	public Munition(int status, double heading, double speed) {
		super(status, heading, speed);
	}

	/**
	 * Initial movement from its origin, to avoid being considered as hitting it
	 * (how far from the origin it spawns)
	 */
	void escape() {
		// does size matter? they never tell us
		double dist = World.getCollisionDistance() + getSize() / 2;
		setX(getX() + SoncMath.horizontalDistance(dist, getHeading()));
		setY(getY() + SoncMath.verticalDistance(dist, getHeading()));
	}

	void setOrigin(Ship origin) {
		this.origin = origin;
		this.setX(origin.getX());
		this.setY(origin.getY());
	}

	@Override
	Ship getOrigin() {
		return origin;
	}

	@Override
	double getMaxSpeedChange() {
		return 0;
	}

	@Override
	double getMaxRotation() {
		return 0;
	}

	/**
	 * Number of rounds a ship must wait to fire this munition since it fired
	 * the last time
	 * 
	 * @return delay in number of rounds
	 */
	abstract int fireDelay();

}
