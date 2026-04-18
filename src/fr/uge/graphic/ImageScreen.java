package fr.uge.graphic;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Screen that displays a background image. Click anywhere to continue to the
 * next screen.
 */
public class ImageScreen implements Screen {
	private final BufferedImage backgroundImage;

	/**
	 * Creates a new image screen with the given background
	 * 
	 * @param backgroundImage the background image to display
	 */
	public ImageScreen(BufferedImage backgroundImage) {
		this.backgroundImage = Objects.requireNonNull(backgroundImage);
	}

	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics, "graphics cannot be null");
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}

		graphics.drawImage(backgroundImage, 0, 0, width, height, null);
	}

	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");
		manager.goToScreen(1);
	}

	@Override
	public void handleMouseMove(int x, int y) {

	}

	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);
	}

	@Override
	public void handleRotate(int x, int y) {

	}

	@Override
	public void handleRightClick(int x, int y) {

	}
}