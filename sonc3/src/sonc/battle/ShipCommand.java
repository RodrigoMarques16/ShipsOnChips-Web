package sonc.battle;

/**
 * This is the abstract part of the Command design pattern. It defines a command
 * executed by the ship and is used to delays command execution until the
 * Ship.move() command is completed. Ship commands are used to ensure that ships
 * execute at most one command per turn.
 */

public interface ShipCommand {

	/**
	 * Method that executes the command. Arguments for command execution can be
	 * passed to the instance using the constructor
	 */
	void execute();
}
