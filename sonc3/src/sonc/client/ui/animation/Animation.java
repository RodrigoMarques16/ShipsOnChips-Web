package sonc.client.ui.animation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.cellview.client.CellList;

import sonc.shared.Movie;
import sonc.shared.Movie.Frame;
import sonc.shared.Movie.Oblong;
import sonc.shared.Movie.Score;

/**
 * Class that animates a given movie
 */
public class Animation {

	private Context2d		 context;
	private Movie			 movie;
	private int				 canvasWidth  = 1000;
	private int				 canvasHeight = 1000;
	private CellList<String> scoreCellList;

	/*
	 * We use this this to pass the current
	 * frame of animation to the callback
	 */
	private int frameIndex;

	public Animation(Canvas canvas, CellList<String> scoreCellList_, Movie movie_) {
		context = canvas.getContext2d();
		movie = movie_;
		scoreCellList = scoreCellList_;
	}

	/**
	 * Play a movie through AnimationScheduler
	 */
	public void play() {
		frameIndex = 0;

		List<Frame> frames = movie.getFrames();

		AnimationScheduler.AnimationCallback callback = new AnimationCallback() {
			@Override
			public void execute(double time) {

				Frame frame = frames.get(frameIndex);

				List<Score> scores = frame.getScores();
				List<Oblong> objects = frame.getOblongs();
				ArrayList<String> scores_ = new ArrayList<String>();
				
				context.clearRect(0, 0, canvasHeight, canvasWidth);
				
				context.setFillStyle("#0099CC");
				context.fillRect(0, 0, canvasHeight, canvasWidth);
				
				for (Score s : scores) {
					scores_.add(s.toString());
				}

				for (Oblong o : objects) {
					Polygon poly = new Polygon(o);
					poly.setScale(2);
					context.setFillStyle(o.getColor());
					poly.draw(context);
				}
				scoreCellList.setRowCount(scores_.size(), true);
				scoreCellList.setRowData(0, scores_);
				frameIndex = frameIndex + 1;

				if (frameIndex < frames.size() + 1)
					AnimationScheduler.get().requestAnimationFrame(this);
			}
		};

		AnimationScheduler.get().requestAnimationFrame(callback);
		frameIndex = 0;
	}

}
