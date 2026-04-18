package fr.uge.graphic;

import java.util.Objects;

import fr.uge.backpackhero.Item;

/**
 * Represents an item available for purchase in the shop. Tracks the item, its
 * position and dimensions on screen, and whether it has been sold.
 */
public class ShopItem {
	private Item item;
	private int x, y;
	private int width, height;
	private boolean sold;

	/**
	 * Constructs a ShopItem with the specified item and display bounds.
	 * 
	 * @param item   the item being offered for sale
	 * @param x      the x-coordinate of the item's display position
	 * @param y      the y-coordinate of the item's display position
	 * @param width  the width of the item's display area
	 * @param height the height of the item's display area
	 */
	ShopItem(Item item, int x, int y, int width, int height) {
		this.item = item;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sold = false;
	}

	/**
	 * Checks if the given coordinates are within this shop item's bounds.
	 * 
	 * @param mx the x-coordinate to check
	 * @param my the y-coordinate to check
	 * @return true if the coordinates are inside the item's display area, false
	 *         otherwise
	 */
	public boolean contains(int mx, int my) {
		return mx >= x && mx < x + width && my >= y && my < y + height;
	}

	/**
	 * Returns the item being offered for sale.
	 * 
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * Returns the x-coordinate of the item's display position.
	 * 
	 * @return the x-coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Returns the y-coordinate of the item's display position.
	 * 
	 * @return the y-coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Returns the width of the item's display area.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the item's display area.
	 * 
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns whether this item has been sold.
	 * 
	 * @return true if the item has been sold, false otherwise
	 */
	public boolean isSold() {
		return sold;
	}

	/**
	 * Sets the item being offered for sale.
	 * 
	 * @param item the new item
	 * @throws NullPointerException if item is null
	 */
	public void setItem(Item item) {
		Objects.requireNonNull(item);
		this.item = item;
	}

	/**
	 * Sets the x-coordinate of the item's display position.
	 * 
	 * @param x the new x-coordinate
	 * @throws IllegalArgumentException if x is negative
	 */
	public void setX(int x) {
		if (x < 0) {
			throw new IllegalArgumentException();
		}
		this.x = x;
	}

	/**
	 * Sets the y-coordinate of the item's display position.
	 * 
	 * @param y the new y-coordinate
	 * @throws IllegalArgumentException if y is negative
	 */
	public void setY(int y) {
		if (y < 0) {
			throw new IllegalArgumentException();
		}
		this.y = y;
	}

	/**
	 * Sets the width of the item's display area.
	 * 
	 * @param width the new width
	 * @throws IllegalArgumentException if width is negative
	 */
	public void setWidth(int width) {
		if (width < 0) {
			throw new IllegalArgumentException();
		}
		this.width = width;
	}

	/**
	 * Sets the height of the item's display area.
	 * 
	 * @param height the new height
	 * @throws IllegalArgumentException if height is negative
	 */
	public void setHeight(int height) {
		if (height < 0) {
			throw new IllegalArgumentException();
		}
		this.height = height;
	}

	/**
	 * Marks this item as sold or not sold.
	 * 
	 * @param sold true to mark as sold, false otherwise
	 */
	public void setSold(boolean sold) {
		this.sold = sold;
	}
}