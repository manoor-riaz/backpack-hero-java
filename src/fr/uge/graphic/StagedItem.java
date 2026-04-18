package fr.uge.graphic;

import module java.base;

import fr.uge.backpackhero.Item;
import fr.uge.backpackhero.Orientation;

/**
 * Represents an item in the staging area with its screen position and
 * orientation.
 */
public class StagedItem {
	private final Item item;
	private int screenX;
	private int screenY;
	private Orientation orientation;

	/**
	 * Constructs a new staged item.
	 * 
	 * @param item        the item to stage
	 * @param screenX     the x-coordinate on screen
	 * @param screenY     the y-coordinate on screen
	 * @param orientation the orientation of the item
	 * @throws NullPointerException if item or orientation is null
	 */
	public StagedItem(Item item, int screenX, int screenY, Orientation orientation) {
		this.item = Objects.requireNonNull(item);
		this.screenX = screenX;
		this.screenY = screenY;
		this.orientation = Objects.requireNonNull(orientation);
	}

	/**
	 * Checks if the given mouse coordinates are within this staged item's bounds.
	 * 
	 * @param mouseX   the x-coordinate of the mouse
	 * @param mouseY   the y-coordinate of the mouse
	 * @param cellSize the size of a cell in pixels
	 * @return true if the coordinates are within bounds, false otherwise
	 */
	public boolean contains(int mouseX, int mouseY, int cellSize) {
		if (cellSize <= 0) {
			throw new IllegalArgumentException("cellSize must be > 0");
		}
		var cells = item.shape().turnItem(orientation);
		var dim = item.shape().dimensionOfItem(cells);
		int width = dim.width() * cellSize;
		int height = dim.height() * cellSize;
		return mouseX >= screenX && mouseX < screenX + width && mouseY >= screenY && mouseY < screenY + height;
	}

	/**
	 * Returns the staged item
	 * 
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Returns the coordinates of the item
	 * 
	 * @return the coordinates
	 */
	public int getScreenX() {
		return screenX;
	}

	/**
	 * Returns the coordinates of the item
	 * 
	 * @return the coordinates
	 */
	public int getScreenY() {
		return screenY;
	}

	/**
	 * Returns the orientation of the item
	 * 
	 * @return the orientation
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Sets the orientation of the item
	 * 
	 * @param orientation the new orientation
	 */
	public void setOrientation(Orientation orientation) {
		Objects.requireNonNull(orientation);

		this.orientation = orientation;
	}

}