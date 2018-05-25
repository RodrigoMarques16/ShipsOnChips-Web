package sonc.battle;

/**
 * This class integrates the concrete part of the Command design pattern. It
 * defines the firing of a munition executed by the fire() method, that is
 * delayed until the Ship.move() command is completed. This ensures that a
 * single command is executed per turn. The execute command in this class is
 * responsible to enforce a delay between consecutive firing from the same ship.
 * The delay between consecutive firing from the same ship must be superior the
 * munition Munition.fireDelay()
 * 
 * @author Rodrigo Marques
 */
class FireCommand implements ShipCommand {

	private World	 world;
	private Ship	 ship;
	private Munition munition;

	/**
	 * Create a FireCommand from given data
	 * 
	 * @param world where munition will created
	 * @param ship that fires munition
	 * @param munition to be fired (may be of different types)
	 */
	FireCommand(World world, Ship ship, Munition munition) {
		this.world = world;
		this.ship = ship;
		this.munition = munition;
	}

	@Override
	public void execute() {
		if (ship.canFire(munition)) {
			munition.setOrigin(ship);
			munition.escape();
			ship.setLastFireRound(world.getCurrentRound());
			world.addMovingObject(munition);
		}
	}

}
