package fr.uge.graphic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * Loads an image from the classpath.
 */
public class ImageLoader {
	private final BufferedImage image;

	/**
	 * Creates a new image loader.
	 *
	 * @param folder   : the folder where images are stored
	 * @param fileName : the image file name
	 * @throws RuntimeException : if the image cannot be loaded
	 */
	public ImageLoader(String folder, String fileName) {
		Objects.requireNonNull(folder);
		Objects.requireNonNull(fileName);

		String cleanFolder = folder.replace("\\", "/");
		if (cleanFolder.startsWith("src/")) {
			cleanFolder = cleanFolder.substring(4);
		}

		String resourcePath = "/" + cleanFolder + "/" + fileName;

		try (InputStream is = ImageLoader.class.getResourceAsStream(resourcePath)) {
			if (is == null) {
				throw new RuntimeException("Cannot load image (resource not found): " + resourcePath);
			}
			this.image = ImageIO.read(is);
			if (this.image == null) {
				throw new RuntimeException("Cannot decode image: " + resourcePath);
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot load image: " + resourcePath, e);
		}
	}

	/**
	 * Returns the loaded image.
	 *
	 * @return the BufferedImage
	 */
	public BufferedImage getImage() {
		return image;
	}
}
