package fr.uge.graphic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * A clickable button for the user interface. Has a position, size, and optional
 * image.
 */
public class Button {
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final BufferedImage image;

	/**
	 * Creates a new button.
	 *
	 * @param x      the x position of the top-left corner
	 * @param y      the y position of the top-left corner
	 * @param width  the width of the button
	 * @param height the height of the button
	 * @param image  the image to display (can be null)
	 * @throws IllegalArgumentException if width or height is negative
	 */
	public Button(int x, int y, int width, int height, BufferedImage image) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Width and height must be non-negative");
		}
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.image = image;
	}

	/**
	 * Checks if a point is inside this button.
	 *
	 * @param pointX the x position to check
	 * @param pointY the y position to check
	 * @return true if the point is inside the button
	 */
	public boolean contains(int pointX, int pointY) {
		return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height;
	}

	/**
	 * Draws the button on the screen.
	 *
	 * @param graphics the graphics context to draw on
	 * @throws NullPointerException if graphics is null
	 */
	public void draw(Graphics2D graphics) {
		Objects.requireNonNull(graphics);
		if (image != null) {
			graphics.drawImage(image, x, y, width, height, null);
		}
	}

	/**
	 * Gets the x position of the button.
	 *
	 * @return the x position
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y position of the button.
	 *
	 * @return the y position
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the width of the button.
	 *
	 * @return the width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the button.
	 *
	 * @return the height in pixels
	 */
	public int getHeight() {
		return height;
	}
}