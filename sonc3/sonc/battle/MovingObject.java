package sonc.battle;

import sonc.quad.HasPoint;
import sonc.utils.SoncMath;

/**
 * Common class to all moving objects in the game, including ships and the
 * munitions they throw at each other
 * 
 * @author Rodrigo Marques
 */
public abstract class MovingObject implements HasPoint {

	private int	   status;
	private double heading;
	private double speed;
	private double x;
	private double y;

	/**
	 * Initialize a moving object with given status, heading and speed.
	 * 
	 * @param status of this moving object at start
	 * @param heading of this moving object at start
	 * @param speed of this moving object at start
	 */
	public MovingObject(int status, double heading, double speed) {
		super();
		this.status = status;
		this.heading = normalizeAngle(heading);
		this.speed = SoncMath.clamp(speed, -getMaxSpeed(), getMaxSpeed());
	}

	public double getX() {
		return x;
	}

	void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = normalizeAngle(heading);
	}

	public double getSpeed() {
		return speed;
	}

	/**
	 * Normalize angles in range [0,2*PI[ in radians. The value is added, or
	 * subtracted 2xPI, respectively while it is less than 0, or greater or
	 * equal than 2xPI. The method is available to concrete ships
	 * 
	 * @param angle to normalize
	 * @return normalized angle in range [0,2*PI[
	 */
	protected double normalizeAngle(double angle) {
		/*
		 * while (angle < 0)
		 * angle += 2 * Math.PI;
		 * while (angle >= 2 * Math.PI)
		 * angle -= 2 * Math.PI;
		 * return angle;
		 */
		return angle - SoncMath.TWO_PI * Math.floor(angle / SoncMath.TWO_PI);
	}

	/**
	 * Distance from this moving object to another given as parameter
	 * 
	 * @param other moving object
	 * @return distance to the other
	 */
	protected double distanceTo(MovingObject other) {
		return Math.hypot(x - other.getX(), y - other.getY());
	}

	/**
	 * Angle from this moving object to another given as parameter. Angles are
	 * in radians in the range [0,2*PI[
	 * 
	 * 0 is right, PI/2 is down, PI is left and 3/2*PI is up
	 * 
	 * @param other moving object
	 * @return angle to other object or NaN if some coordinates are not defined
	 */
	protected double headingTo(MovingObject other) {
		return normalizeAngle(Math.atan2(other.getY() - y, other.getX() - x));
	}

	/**
	 * Update the position - (x,y) coordinates - of this moving object taking in
	 * consideration the current speed and heading. This method cannot be
	 * invoked by a concrete ship.
	 */
	final void updatePosition() {
		setX(x + SoncMath.horizontalDistance(speed, heading));
		setY(y + SoncMath.verticalDistance(speed, heading));
	}

	/**
	 * Change heading of this moving object by given variation. Positive
	 * variation correspond to clockwise rotations and negative variations to
	 * clockwise counterclockwise rotations. If the absolute value of variation
	 * exceeds the predefined maximum rotation than it is limited to that value
	 * (with the corresponding signal). This method cannot be invoked by a
	 * concrete ship.
	 * 
	 * @param delta angle in radians
	 */
	final void doRotate(double delta) {
		double maxRotation = getMaxRotation();

		delta = SoncMath.clamp(delta, -maxRotation, maxRotation);

		setHeading(getHeading() + delta);
	}

	/**
	 * Change speed of this moving object. Positive values increase the speed
	 * and negative values decrease it. If either the absolute value of
	 * variation, or the absolute value of the changed speed, exceeds their
	 * respective predefined maximums (getMaxSpeedChange() and getMaxSpeed()
	 * then they are limited to that value (with the corresponding signal). This
	 * method cannot be invoked by a concrete ship.
	 * 
	 * @param delta angle variation (in radians)
	 */
	final void doChangeSpeed(double delta) {
		double maxSpeed = getMaxSpeed();
		double maxSpeedChange = getMaxSpeedChange();

		delta = SoncMath.clamp(delta, -maxSpeedChange, maxSpeedChange);

		speed = SoncMath.clamp(speed + delta, -maxSpeed, maxSpeed);
	}

	/**
	 * Override this method to define the movement of this object. Concrete
	 * ships will need to do it to implement their strategies.
	 */
	void move() {
	}

	/**
	 * Change status to reflect damage inflicted by given moving object
	 * 
	 * @param moving
	 */
	void hitBy(MovingObject moving) {
		status -= moving.getImpactDamage();
	}

	/**
	 * Check if this moving object was destroyed
	 * 
	 * @return true is this object is destroyed, false otherwise
	 */
	public boolean isDestroyed() {
		if (getStatus() <= 0)
			return true;
		return false;
	}

	/**
	 * Current status of this moving object. When status reaches 0 the object is
	 * destroyed.
	 * 
	 * @return status of this moving object
	 */
	public int getStatus() {
		return status;
	}

	void setStatus(int status) {
		this.status = status;
	}

	/**
	 * The maximum speed of this moving object in absolute value. Ships may have
	 * negative speed when sailing backwards but the absolute value of the speed
	 * cannot exceed this value.
	 * 
	 * @return maximum speed variation
	 */
	abstract double getMaxSpeed();

	/**
	 * The maximum speed variation in absolute value, per turn, of this moving
	 * object
	 * 
	 * @return maximum speed variation
	 */
	abstract double getMaxSpeedChange();

	/**
	 * The maximum rotation per turn of this moving object
	 * 
	 * @return maximum rotation
	 */
	abstract double getMaxRotation();

	/**
	 * Damage inflicted by this moving object when it hits another
	 * 
	 * @return amount of status removed from another moving object on collision
	 */
	abstract int getImpactDamage();

	/**
	 * The ship where this moving object originated, that must be credited for
	 * the damage inflicted by this moving object
	 * 
	 * @return ship from which this moving object started (or null if a ship)
	 */
	abstract Ship getOrigin();

	/**
	 * Size of this moving object when displayed. Moving objects are represented
	 * as elongated objects (imagine a cigar) with this size
	 * 
	 * @return size of this moving object
	 */
	public abstract int getSize();

	/**
	 * Color of this moving objects. Different ships may have their own color
	 * but most other have colors depending on their type. Colors may be set as
	 * an HTML/CSS color (e.g. "#0000FF") or a name for some basic colors (e.g.
	 * "yellow" or "red").
	 * 
	 * @return color as a HTML/CSS string or basic color
	 */
	public abstract String getColor();

}
