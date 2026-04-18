package fr.uge.backpackhero;

/**
 * Represents the four possible orientations for items in the backpack. Items
 * can be rotated in 90-degree increments.
 */
public enum Orientation {
	/**
	 * No rotation (0 degrees).
	 */
	ORIENTATION_0,

	/**
	 * Rotated 90 degrees clockwise.
	 */
	ORIENTATION_90,

	/**
	 * Rotated 180 degrees.
	 */
	ORIENTATION_180,

	/**
	 * Rotated 270 degrees clockwise (or 90 degrees counter-clockwise).
	 */
	ORIENTATION_270;

	/**
	 * Returns the next orientation after a 90-degree clockwise rotation.
	 * 
	 * @return the next orientation (ORIENTATION_0 -> ORIENTATION_90 ->
	 *         ORIENTATION_180 -> ORIENTATION_270 -> ORIENTATION_0)
	 */
	public Orientation rotate() {
		return switch (this) {
		case ORIENTATION_0 -> ORIENTATION_90;
		case ORIENTATION_90 -> ORIENTATION_180;
		case ORIENTATION_180 -> ORIENTATION_270;
		case ORIENTATION_270 -> ORIENTATION_0;
		};
	}

	/**
	 * Returns the number of 90-degree rotations from ORIENTATION_0.
	 * 
	 * @return 0 for ORIENTATION_0, 1 for ORIENTATION_90, 2 for ORIENTATION_180, 3
	 *         for ORIENTATION_270
	 */
	public int nbOfRotation() {
		return switch (this) {
		case ORIENTATION_0 -> 0;
		case ORIENTATION_90 -> 1;
		case ORIENTATION_180 -> 2;
		case ORIENTATION_270 -> 3;
		};
	}
}