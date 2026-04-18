package fr.uge.backpackhero;

import module java.base;

/**
 * Represents the shape of an item as a list of relative cell positions. The
 * shape can be rotated in 90-degree increments. All positions are relative to
 * the top-left corner of the item's bounding box.
 */

/**
 * Represents the shape of an item
 * 
 * @param cells the list of positions forming this shape
 */
public record Shape(List<Position> cells) {

	/**
	 * Compact constructor that validates the cells list. Creates an immutable copy
	 * of the provided list.
	 * 
	 * @throws NullPointerException     if cells is null
	 * @throws IllegalArgumentException if cells is empty
	 */
	public Shape {
		Objects.requireNonNull(cells);
		if (cells.isEmpty()) {
			throw new IllegalArgumentException("Shape cannot be empty");
		}
		cells = List.copyOf(cells);
	}

	/**
	 * Returns a rotated version of the item according to the given orientation. The
	 * rotation is applied by performing the appropriate number of 90-degree
	 * clockwise rotations.
	 * 
	 * @param orientation the orientation to rotate to
	 * @return a list of positions representing the rotated shape
	 * @throws NullPointerException if orientation is null
	 */
	public List<Position> turnItem(Orientation orientation) {
		Objects.requireNonNull(orientation);

		var res = cells;
		for (int i = 0; i < orientation.nbOfRotation(); i++) {
			res = rotate90(res);
		}

		return res;
	}

	/**
	 * Rotates a list of positions by 90 degrees clockwise. The rotation formula is:
	 * (row, col) -> (col, height - 1 - row)
	 * 
	 * @param lst the list of positions to rotate
	 * @return a new list of positions rotated 90 degrees clockwise
	 * @throws NullPointerException     if lst is null
	 * @throws IllegalArgumentException if lst is empty
	 */
	private static List<Position> rotate90(List<Position> lst) {
		Objects.requireNonNull(lst);
		if (lst.isEmpty()) {
			throw new IllegalArgumentException("Cannot rotate empty list");
		}

		var maxR = lst.get(0).row();
		var maxC = lst.get(0).col();
		for (var pos : lst) {
			if (pos.row() > maxR) {
				maxR = pos.row();
			}
			if (pos.col() > maxC) {
				maxC = pos.col();
			}
		}
		var height = maxR + 1;
		var res = new ArrayList<Position>();
		for (var pos : lst) {
			var newR = pos.col();
			var newC = height - 1 - pos.row();
			res.add(new Position(newR, newC));
		}

		return res;
	}

	/**
	 * Calculates the bounding box dimension of a list of cells. The dimension is
	 * the smallest rectangle that contains all cells.
	 * 
	 * @param cells the list of cell positions
	 * @return the dimension (height and width) of the bounding box
	 * @throws NullPointerException     if cells is null
	 * @throws IllegalArgumentException if cells is empty
	 */
	public Dimension dimensionOfItem(List<Position> cells) {
		Objects.requireNonNull(cells);

		if (cells.isEmpty()) {
			throw new IllegalArgumentException("Cannot calculate dimension of empty cells");
		}

		var maxR = cells.get(0).row();
		var maxC = cells.get(0).col();
		for (var pos : cells) {
			if (pos.row() > maxR) {
				maxR = pos.row();
			}
			if (pos.col() > maxC) {
				maxC = pos.col();
			}
		}

		return new Dimension(maxR + 1, maxC + 1);
	}

	/**
	 * Creates a rectangular shape with the given dimensions. The rectangle is
	 * filled with all positions from (0,0) to (height-1, width-1).
	 * 
	 * @param dimension the dimension of the rectangle
	 * @return a new Shape representing a filled rectangle
	 * @throws NullPointerException if dimension is null
	 */
	public static Shape rectangle(Dimension dimension) {
		Objects.requireNonNull(dimension);

		var res = new ArrayList<Position>();
		for (int row = 0; row < dimension.height(); row++) {
			for (int col = 0; col < dimension.width(); col++) {
				res.add(new Position(row, col));
			}
		}

		return new Shape(res);
	}

}