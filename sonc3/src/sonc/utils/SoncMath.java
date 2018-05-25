package sonc.utils;

public class SoncMath {

	public static double TWO_PI = Math.PI * 2;

	public static double clamp(double value, double lower, double upper) {
		return Math.max(lower, Math.min(value, upper));
	}

	public static int clamp(int value, int lower, int upper) {
		return Math.max(lower, Math.min(value, upper));
	}

	public static double horizontalDistance(double distance, double angle) {
		return distance * Math.cos(angle);
	}

	public static double verticalDistance(double distance, double angle) {
		return distance * Math.sin(angle);
	}

}
