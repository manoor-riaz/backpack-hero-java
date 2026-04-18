package fr.uge.graphic;

/**
 * Holds screen dimension information.
 */
public class ScreenInfo {
	private final int width;
	private final int height;

	/**
	 * Constructs a new ScreenInfo.
	 *
	 * @param width  the width of the screen
	 * @param height the height of the screen
	 */
	public ScreenInfo(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns the width of the screen
	 * 
	 * @return the width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the screen
	 * 
	 * @return the height in pixels
	 */
	public int getHeight() {
		return height;
	}

}