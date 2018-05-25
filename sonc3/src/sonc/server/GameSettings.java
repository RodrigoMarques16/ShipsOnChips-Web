package sonc.server;

import java.io.File;

public class GameSettings {

	// Serialization file
	public static final File PLAYERS_FILE = new File("sonc3/players.ser");

	// Window scale
	public static final double SCALE = 0.8;

	// World settings
	public static final double SIDE				  = 1000;
	public static final double MARGIN			  = 100;
	public static final double COLLISION_DISTANCE = 10;

	// Ship settings
	public static final int	   SHIP_HEALTH			 = 1000;
	public static final int	   SHIP_DAMAGE			 = 1000;
	public static final int	   SHIP_SIZE			 = 10;
	public static final double SHIP_MAX_ROTATION	 = 5;
	public static final double SHIP_MAX_SPEED		 = 10;
	public static final double SHIP_MAX_SPEED_CHANGE = 1;

	// Bullet settings
	public static final int	   BULLET_DAMAGE = 10;
	public static final int	   BULLET_SIZE	 = 5;
	public static final int	   BULLET_DELAY	 = 5;
	public static final int	   BULLET_HEALTH = 1;
	public static final double BULLET_SPEED	 = 20;

	// Missile settings
	public static final int	   MISSILE_DAMAGE		= 600;
	public static final int	   MISSILE_SIZE			= 2;
	public static final int	   MISSILE_DELAY		= 60;
	public static final int	   MISSILE_HEALTH		= 5;
	public static final double MISSILE_SPEED		= 5;
	public static final double MISSILE_MAX_ROTATION	= Math.PI / 64;

}
