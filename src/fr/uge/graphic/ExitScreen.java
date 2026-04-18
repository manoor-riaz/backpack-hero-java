package fr.uge.graphic;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * Screen displayed when the game ends. Shows a message with a background image.
 */
public class ExitScreen implements Screen {
	private final String message;
	private Image background;

	/**
	 * Creates a new exit screen with a message and background.
	 *
	 * @param message        : the message to display
	 * @param backgroundPath : the path to the background image
	 */
	public ExitScreen(String message, String backgroundPath) {
		this.message = Objects.requireNonNull(message, "message cannot be null");
		this.background = loadBackground(backgroundPath);
	}

	/**
	 * Creates a new exit screen with a message and a pre-loaded background image.
	 *
	 * @param message    : the message to display
	 * @param background : the background image (can be null)
	 */
	public ExitScreen(String message, BufferedImage background) {
		this.message = Objects.requireNonNull(message, "message cannot be null");
		this.background = background;
	}

	/**
	 * Loads a background image from the given path using java.nio.file.Path.
	 * Conforme au cours : on n'utilise pas java.io.File
	 *
	 * @param backgroundPath : the path to the background image
	 * @return the loaded image, or null if loading fails
	 */
	private Image loadBackground(String backgroundPath) {
		if (backgroundPath == null || backgroundPath.isEmpty()) {
			return null;
		}

		Path path = Path.of(backgroundPath);

		try (var inputStream = Files.newInputStream(path)) {
			return ImageIO.read(inputStream);
		} catch (IOException e) {
			try {
				String fileName = path.getFileName().toString();
				String folder = path.getParent() != null ? path.getParent().toString() : "image";
				return new ImageLoader(folder, fileName).getImage();
			} catch (RuntimeException ex) {
				return null;
			}
		}
	}

	/**
	 * Draws the exit screen with the message centered on the background.
	 *
	 * @param graphics : the graphics context
	 * @param width    : the screen width
	 * @param height   : the screen height
	 */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics, "graphics cannot be null");
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}

		if (background != null) {
			graphics.drawImage(background, 0, 0, width, height, null);
		} else {
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, width, height);
		}

		graphics.setColor(Color.WHITE);
		graphics.setFont(new Font("Arial", Font.BOLD, 48));
		int textWidth = graphics.getFontMetrics().stringWidth(message);
		int x = (width - textWidth) / 2;
		int y = height / 2;
		graphics.drawString(message, x, y);
	}

	/**
	 * Handles clicks on this screen (no interaction needed).
	 *
	 * @param x       : the x coordinate of the click
	 * @param y       : the y coordinate of the click
	 * @param manager : the screen manager
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");
	}

}