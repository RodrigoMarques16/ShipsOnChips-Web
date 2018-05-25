package sonc.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Creates an animation to view the evolution of the world. The animation is a
 * sequence of frames. A new frame is created with the {@link #newFrame()}
 * method. Subsequent calls to the methods
 * {@link #addOblong(int,int,float,int,String)} or
 * {@link #addScore(String,int,int)} will respectively add oblongs and scores to
 * the current frame.
 * 
 * @author Jos&eacute; Paulo Leal {@code zp@dcc.fc.up.pt}
 */
public class WorldViewer extends Frame {

	private static final int NUMBER_OF_BUFFERS = 2;
	private static final int EXIT_OK = 0;
	private static final String TITLE = "World Viewer";
	private static final Font FONT1 = new Font("Helvetica", Font.BOLD, 14);
	private static final Font FONT2 = new Font("Helvetica", Font.ITALIC, 12);
	private static final long serialVersionUID = 1L;
	private static final int SIZE = 1000;
	private static final int RATIO = 4;
	private static final Color BACK = new Color(0x00AABB);
	private static final double DEFAUL_SCALE = 1.0;
	private static final int DEFAULT_FRAMES_PER_SECOND = 10;
	private static final int DEFAULT_STOP_FREQUENCY = 0;

	private static double scale = DEFAUL_SCALE;
	private static int stopFrequency = DEFAULT_STOP_FREQUENCY;
	private static int framesPerSecond = DEFAULT_FRAMES_PER_SECOND;

	/**
	 * The scale of the viewer where 1 (100%) corresponds 1000 pixels
	 * 
	 * @return current scale
	 */
	public static double getScale() {
		return scale;
	}

	/**
	 * Sets the scale of the viewer where 1 (100%) corresponds to 1000 pixels.
	 * This method should be used before instancing the {@code WoldViewer}
	 * 
	 * @param scale
	 *            to use
	 */
	public static void setScale(double scale) {
		WorldViewer.scale = scale;
	}

	/**
	 * Current frames per second (FPS) used in the animation
	 * 
	 * @return fps (frames per second)
	 */
	public static int getFramesPerSecond() {
		return framesPerSecond;
	}

	/**
	 * Set frames per second (FPS) to use in the animation. This method should
	 * be invoked for configuring the viewer, before instancing it.
	 * 
	 * @param framesPerSecond
	 *            used in the simulation
	 */
	public static void setFramesPerSecond(int framesPerSecond) {
		WorldViewer.framesPerSecond = framesPerSecond;
	}

	/**
	 * Get the frequency (in frames) in which movie stops. Zero frequency is
	 * continuous (non-stop) movie (default). This method should be used before
	 * instancing the {@code WoldViewer}
	 * 
	 * @return current stop frequency
	 */
	public static int getStopFrequency() {
		return stopFrequency;
	}

	/**
	 * Get the frequency (in frames) in which movie stops. Zero frequency is
	 * continuous (non-stop) movie (default).
	 * 
	 * @param stopFrequency
	 *            the new frame stop frequency
	 */
	public static void setStopFrequency(int stopFrequency) {
		WorldViewer.stopFrequency = stopFrequency;
	}

	/**
	 * A frame containing a list of oblong objects and a list of scores
	 */
	static class Frame {
		private List<Oblong> oblongs = new ArrayList<>();
		private List<Score> scores = new ArrayList<>();

		/**
		 * Show the frame content using given graphics
		 * 
		 * @param graphics
		 *            where frame is painted
		 */
		void paint(Graphics graphics) {
			for (Oblong oblong : oblongs)
				oblong.paint(graphics);

			Score.prepare(graphics, scores);

			for (Score score : scores)
				score.paint(graphics);
		}
	}

	/**
	 * An oblong (like a cigar) shaped object
	 */
	static class Oblong {
		private int x;
		private int y;
		private float heading;
		private int size;
		private String color;

		public Oblong(int x, int y, float heading, int size, String color) {
			super();
			this.x = x;
			this.y = y;
			this.heading = heading;
			this.size = size;
			this.color = color;
		}

		/**
		 * Paint this oblong object
		 * 
		 * @param graphics
		 *            where shape is painted
		 */
		void paint(Graphics graphics) {
			final Polygon poly = new Polygon();
			final double cos = Math.cos(heading);
			final double sin = Math.sin(heading);
			final double perp = heading + Math.PI / 2;
			final double cos2 = Math.cos(perp);
			final double sin2 = Math.sin(perp);

			poly.addPoint(
					(int) (scale * (x + RATIO * size * cos + size * cos2)),
					(int) (scale * (y + RATIO * size * sin + size * sin2)));
			poly.addPoint((int) (scale * (x + RATIO * (size + 1) * cos)),
					(int) (scale * (y + RATIO * (size + 1) * sin)));
			poly.addPoint(
					(int) (scale * (x + RATIO * size * cos - size * cos2)),
					(int) (scale * (y + RATIO * size * sin - size * sin2)));
			poly.addPoint(
					(int) (scale * (x - RATIO * size * cos - size * cos2)),
					(int) (scale * (y - RATIO * size * sin - size * sin2)));
			poly.addPoint(
					(int) (scale * (x - RATIO * size * cos + size * cos2)),
					(int) (scale * (y - RATIO * size * sin + size * sin2)));

			graphics.setColor(getColor(color));
			graphics.fillPolygon(poly);
		}

	}

	/**
	 * The score of a game agent
	 */
	static class Score {
		private String name;
		private int points;
		private int status;

		public Score(String name, int points, int status) {
			super();
			this.name = name;
			this.points = points;
			this.status = status;
		}

		private static FontMetrics fontMetrics;
		private static int top;
		private static int nameWidth;
		private static int pointsWidth;
		private static int statusWidth;

		/**
		 * Perform preparatory computations to layout scores
		 * 
		 * @param graphics
		 *            where scores will be painted
		 * @param scores
		 *            list of scores
		 */
		private static void prepare(Graphics graphics, List<Score> scores) {
			top = 100;
			fontMetrics = graphics.getFontMetrics(FONT1);

			graphics.setFont(FONT1);
			graphics.setColor(Color.WHITE);

			nameWidth = 0;
			pointsWidth = 0;
			statusWidth = 0;

			int width;
			for (Score score : scores) {
				width = fontMetrics.stringWidth(score.name + " ");
				if (width > nameWidth)
					nameWidth = width;

				width = fontMetrics.stringWidth(score.points + " ");
				if (width > pointsWidth)
					pointsWidth = width;

				width = fontMetrics.stringWidth(score.status + " ");
				if (width > statusWidth)
					statusWidth = width;
			}
		}

		/**
		 * Paint score using given graphics
		 *
		 * @param graphics
		 *            where score is painted
		 */
		void paint(Graphics graphics) {
			int left = 10;
			graphics.drawString(name, left, top);
			left += nameWidth;
			graphics.drawString(Integer.toString(points), left, top);
			left += pointsWidth;
			graphics.drawString(Integer.toString(status), left, top);
			top += fontMetrics.getHeight();
		}
	}

	/**
	 * Create a new frame. Subsequent calls to <code>addOblong()</code> or
	 * <code>addScore()</code> will add these elements to this frame
	 */
	public void newFrame() {
		if (currentFrame == 0 && frames.size() > 0)
			repaint();

		frames.add(frame = new Frame());
	}

	/**
	 * Add a Oblong to current frame
	 * 
	 * @param x
	 *            coordinate of object
	 * @param y
	 *            coordinate of object
	 * @param heading
	 *            of object (angle in radians)
	 * @param size
	 *            of oblong shape
	 * @param color
	 *            String with its name (e.g. "red") or HTML/CSS format (e.g.
	 *            "#FF0000")
	 * @throws IllegalStateException
	 *             if no frame was created before
	 */
	public void addOblong(int x, int y, float heading, int size, String color) {
		if (frame == null)
			throw new IllegalStateException(
					"Cannot add a rect before creating a frame");
		else
			frame.oblongs.add(new Oblong(x, y, heading, size, color));
	}

	/**
	 * Add a score to current frame
	 * 
	 * @param name
	 *            of player
	 * @param points
	 *            of player
	 * @param status
	 *            of player
	 * @throws IllegalStateException
	 *             if no frame was created
	 */
	public void addScore(String name, int points, int status) {
		if (frame == null)
			throw new IllegalStateException(
					"Cannot add a score before creating a frame");
		else
			frame.scores.add(new Score(name, points, status));
	}

	private List<Frame> frames = new ArrayList<>();
	private Frame frame = null;
	private Timer timer = new Timer();
	private BufferStrategy strategy;
	private int currentFrame = 0;
	private int size = SIZE;

	/**
	 * Create a default world viewer, a square of 1000 pixel
	 */
	public WorldViewer() {
		super(TITLE);
		size = (int) (SIZE * scale);

		setSize(size, size);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				System.exit(EXIT_OK);
			}
		});

		// double buffering to avoid flickering
		createBufferStrategy(NUMBER_OF_BUFFERS);
		strategy = getBufferStrategy();
	}

	/**
	 * Wait until all frames are displayed
	 */
	public void waitForExit() {
		while (frames.size() == 0 || currentFrame + 1 < frames.size()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Display current frame. Schedule for next frame, if available Use double
	 * buffering to avoid flickering
	 */
	public void paint(Graphics gc) {

		if (currentFrame >= frames.size()) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					repaint();
				}
			}, 1000);
		} else {
			final Frame frame = frames.get(currentFrame);
			final boolean lastFrame = currentFrame + 1 >= frames.size();
			final boolean stopFrame = stopFrequency != 0
					&& (currentFrame % stopFrequency) == 0;
			final long start = System.currentTimeMillis();

			do {
				do {
					Graphics graphics = strategy.getDrawGraphics();

					graphics.setColor(BACK);
					graphics.fillRect(0, 0, size, size);

					frame.paint(graphics);

					if (stopFrame)
						pressToContinue(graphics);

					if (lastFrame)
						closeViewer(graphics);

					graphics.dispose();
				} while (strategy.contentsRestored());
				strategy.show();
			} while (strategy.contentsLost());

			if (!stopFrame && !lastFrame) {
				currentFrame++;
				final long delay = (1000 / framesPerSecond)
						- (System.currentTimeMillis() - start);

				if (delay > 0) {
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							repaint();
						}
					}, delay);
				} else {
					repaint(); // already late;
				}
			}
		}
	}

	/**
	 * Close viewer by closing window
	 * 
	 * @param graphics
	 */
	private void closeViewer(Graphics graphics) {
		drawMessageCentered(graphics, "Close window to exit", FONT1);
	}

	/**
	 * Close viewer by closing window
	 * 
	 * @param graphics
	 *            where message is painted
	 */
	private void pressToContinue(Graphics graphics) {
		final WorldViewer viewer = this;
		drawMessageCentered(graphics, "Press mouse to continue", FONT2);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				repaint();
				viewer.addMouseListener(null);
			}
		});
	}

	private void drawMessageCentered(Graphics graphics, String message,
			Font font) {
		final FontMetrics fontMetrics = graphics.getFontMetrics(font);
		final int messageWidth = fontMetrics.stringWidth(message);
		final int fontHeight = fontMetrics.getHeight();

		graphics.drawString(message, (size - messageWidth) / 2,
				size - fontHeight);
	}

	/*---------------------------------------------------------------------------*\
	 *                        Static methods for handling colors                 *
	\*---------------------------------------------------------------------------*/

	private static Map<String, Color> colors = new HashMap<>();

	/**
	 * Return a Color instance given its name or RGB (HTML/CSS format). Color
	 * instance are reused for the same name;
	 * 
	 * @param name
	 *            of color (e.g. "red") or HTML/CSS format (e.g. "#FF0000")
	 * @return color instance for given string
	 */
	static Color getColor(String name) {
		Color color = colors.get(name);

		if (color == null)
			colors.put(name, color = makeColor(name));

		return color;
	}

	/**
	 * Return a Color instance given its RGB(HTML/CSS format) or name.
	 * 
	 * @param name
	 *            of color (e.g. "red") or HTML/CSS format (e.g. "#FF0000")
	 * @return color instance for given string
	 */
	static Color makeColor(String name) {
		Color color = Color.BLACK;

		if (name.startsWith("#"))
			color = new Color(Integer.parseInt(name.substring(1), 16));
		else
			try {
				color = (Color) Color.class.getField(name).get(null);
			} catch (IllegalArgumentException | IllegalAccessException
					| NoSuchFieldException | SecurityException cause) {
				throw new IllegalArgumentException("Unknown color " + name);
			}

		return color;
	}

	/*---------------------------------------------------------------------------*\
	 *                        									                 *
	\*---------------------------------------------------------------------------*/

	public static void main(String[] args) {
		WorldViewer viewer = new WorldViewer();
		int len = 300;
		int size = 5;
		String color = "#FF0000";
		int points = 0;
		int status = 1000;

		setScale(0.7);
		setStopFrequency(0);
		
		for (float alpha = 0; alpha < Math.PI / 8; alpha += Math.PI / 500) {
			int x = (int) (SIZE / 2 + len * Math.cos(alpha));
			int y = (int) (SIZE / 2 + len * Math.sin(alpha));
			float beta = (float) (alpha + Math.PI / 2);

			viewer.newFrame();
			viewer.addOblong(x, y, beta, size, color);

			viewer.addScore("zp", points++, status--);
			viewer.addScore("other", points, status);
		}
	}
}
