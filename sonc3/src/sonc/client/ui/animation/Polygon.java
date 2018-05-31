package sonc.client.ui.animation;

import java.util.ArrayList;
import com.google.gwt.canvas.dom.client.Context2d;

import sonc.shared.Movie.Oblong;

/**
 * Class that represents the polygons to be drawn in the animation
 */
public class Polygon {

	private ArrayList<Point> list;
	private int				 scale = 2;

	public Polygon(Oblong object) {
		int rectWidth = object.getSize() * 4 * scale;
		int rectHeight = object.getSize() * scale;

		float rotation = object.getHeading();

		int x = object.getX();
		int y = object.getY();

		double rectDiag = Math.sqrt((rectWidth / 2) * (rectWidth / 2) + (rectHeight / 2) * (rectHeight / 2));
		double rectAngle = Math.atan2(rectHeight / 2, rectWidth / 2);

		// cx1 cy1
		int cx1 = (int) (x + -rectDiag * Math.cos(-rectAngle + rotation));
		int cy1 = (int) (y + -rectDiag * Math.sin(-rectAngle + rotation));

		// cx2 cy2
		int cx2 = (int) (x + rectDiag * Math.cos(rectAngle + rotation));
		int cy2 = (int) (y + rectDiag * Math.sin(rectAngle + rotation));

		// cx3 cy3
		int cx3 = (int) (x + rectDiag * Math.cos(-rectAngle + rotation));
		int cy3 = (int) (y + rectDiag * Math.sin(-rectAngle + rotation));

		// cx4 cy4
		int cx4 = (int) (x + -rectDiag * Math.cos(rectAngle + rotation));
		int cy4 = (int) (y + -rectDiag * Math.sin(rectAngle + rotation));

		list = new ArrayList<Point>();
		list.add(new Point(cx1, cy1));
		list.add(new Point(cx2, cy2));
		list.add(new Point(cx3, cy3));
		list.add(new Point(cx4, cy4));
		list.add(new Point(cx1, cy1));
	}

	public void setScale(int s) {
		scale = s;
	}

	/**
	 * Draw the polygon
	 * @param context where to draw
	 */
	public void draw(Context2d context) {
		context.beginPath();
		context.moveTo(list.get(0).getX(), list.get(0).getY());
		for (int i = 0 + 1; i < 5; i++) {
			Point point = list.get(i);
			context.lineTo(point.getX(), point.getY());
			//context.moveTo(point.getX(), point.getY());
		}
		context.stroke();
		context.fill();
		//context.closePath();	
	}

}
