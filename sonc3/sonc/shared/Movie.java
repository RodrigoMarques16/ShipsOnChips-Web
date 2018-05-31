package sonc.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data for an animation of the game that can be presented to the player. It is
 * composed of a sequence frames, each with a sequence of oblong objects and
 * another of player's scores. Oblong objects of different sizes and colours
 * represent ships and munitions.
 * 
 * @author Rodrigo Marques
 */
public class Movie implements IsSerializable  {

	/**
	 * A frame in a movie with a list of oblong objects and scores of players
	 */
	public static class Frame implements IsSerializable {

		private List<Oblong> oblongs = new ArrayList<>();
		private List<Score>	 scores	 = new ArrayList<>();

		public Frame() {
		}
		
		public List<Oblong> getOblongs() {
			return oblongs;
		}

		public List<Score> getScores() {
			return scores;
		}

	}

	/**
	 * An oblong (like a cigar) shaped object defined by it's coordinates, heading
	 * and size.
	 */
	public static class Oblong implements IsSerializable {
		private String color;
		private float  heading;
		private int	   size;
		private int	   x;
		private int	   y;
		
		public Oblong() {
		}
		
		public Oblong(int x, int y, float heading, int size, String color) {
			super();
			this.x = x;
			this.y = y;
			this.heading = heading;
			this.size = size;
			this.color = color;
		}

		public String getColor() {
			return color;
		}

		public float getHeading() {
			return heading;
		}

		public int getSize() {
			return size;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

	}

	/**
	 * The score of a game agent
	 */
	public static class Score implements IsSerializable {
		private String name;
		private int	   points;
		private int	   status;

		public Score() {
		}
	 	
		public Score(String name, int points, int status) {
			super();
			this.name = name;
			this.points = points;
			this.status = status;
		}

		public String getName() {
			return name;
		}

		public int getPoints() {
			return points;
		}

		public int getStatus() {
			return status;
		}

	}

	private List<Frame>	frames = new ArrayList<>();
	private Frame		frame  = null;

	public Movie() {
	}

	/**
	 * Create a new frame. Subsequent calls to addRect() or addScore() will add
	 * these elements to this frame
	 */
	public void newFrame() {
		frames.add(frame = new Frame());
	}

	/**
	 * Add a object to current frame
	 * 
	 * @param x coordinate of object
	 * @param y coordinate of object
	 * @param heading of object (angle in radians)
	 * @param size of oblong object
	 * @param color String with its name (e.g. "red") or HTML/CSS format (e.g.
	 *            "#FF0000")
	 * @throws SoncException
	 * @throws IllegalStateException if no frame was created before executing
	 *             this method
	 */
	public void addOblong(int x, int y, float heading, int size, String color)
			throws IllegalStateException {
		if (frame == null)
			throw new IllegalStateException("Cannot add object to null frame");
		else
			frame.oblongs.add(new Oblong(x, y, heading, size, color));
	}

	/**
	 * Add a score to current frame
	 * 
	 * @param name of player
	 * @param points of player
	 * @param status of player
	 * @throws IllegalStateException if no frame was created before executing
	 *             this method
	 */
	public void addScore(String name, int points, int status) {
		if (frame == null)
			throw new IllegalStateException("Cannot add a score before creating a frame");
		else
			frame.scores.add(new Score(name, points, status));
	}

	/**
	 * Get the current list of frames
	 * 
	 * @return list of frames
	 */
	public List<Frame> getFrames() {
		return frames;
	}

}
