package sonc.battle;

/**
 * This class integrates the concrete part of the Command design pattern. It
 * defines a rotation executed by the rotate() method, that is delayed until the
 * Ship.move() command is completed. This ensures that a single command is
 * executed per turn.
 */
class RotateCommand implements ShipCommand {

	private Ship   ship;
	private double delta;

	/**
	 * Create a RotateCommand from given data
	 * 
	 * @param ship that is rotated
	 * @param delta rotation angle
	 */
	RotateCommand(Ship ship, double delta) {
		this.ship = ship;
		this.delta = delta;
	}

	@Override
	public void execute() {
		ship.doRotate(delta);
	}

}
