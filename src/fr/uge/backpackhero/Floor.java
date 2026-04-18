package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a floor in the dungeon. A floor contains a grid of rooms that the
 * hero can explore.
 * 
 * @param floorNumber the number of this floor
 * @param height      the height of the floor grid
 * @param width       the width of the floor grid
 * @param rooms       the grid of rooms
 * @param startPos    the starting position for the hero
 */
public record Floor(int floorNumber, int height, int width, Room[][] rooms, Position startPos) {

	/**
	 * Creates a new floor with validation.
	 * 
	 * @throws NullPointerException     if rooms or startPos is null
	 * @throws IllegalArgumentException if dimensions are invalid or rooms don't
	 *                                  match dimensions
	 */
	public Floor {
		Objects.requireNonNull(rooms);
		Objects.requireNonNull(startPos);

		if (floorNumber <= 0) {
			throw new IllegalArgumentException("floorNumber have to be positive");
		}
		if (height <= 0) {
			throw new IllegalArgumentException("height have to be sup than 0");
		}
		if (width <= 0) {
			throw new IllegalArgumentException("width have to be sup than 0");
		}
		if (rooms.length != height) {
			throw new IllegalArgumentException("Difference between rooms and height");
		}

		validateRoomDimensions(rooms, width);
		validateStartPosition(startPos, height, width);
	}

	/**
	 * Validates that all room rows match the expected width.
	 * 
	 * @param rooms the rooms array
	 * @param width the expected width
	 * @throws IllegalArgumentException if dimensions don't match
	 */
	private static void validateRoomDimensions(Room[][] rooms, int width) {
		for (var room : rooms) {
			if (room == null || room.length != width) {
				throw new IllegalArgumentException("Difference between rooms and width");
			}
		}
	}

	/**
	 * Validates that the start position is within bounds.
	 * 
	 * @param startPos the starting position
	 * @param height   the floor height
	 * @param width    the floor width
	 * @throws IllegalArgumentException if startPos is out of bounds
	 */
	private static void validateStartPosition(Position startPos, int height, int width) {
		if (startPos.row() < 0 || startPos.row() >= height || startPos.col() < 0 || startPos.col() >= width) {
			throw new IllegalArgumentException("startPos is out of bounds");
		}
	}

	/**
	 * Returns the room at the specified coordinates.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return the room, or null if out of bounds
	 */
	public Room getRoom(int row, int col) {
		if (row < 0 || row >= height || col < 0 || col >= width) {
			return null;
		}
		return rooms[row][col];
	}

	/**
	 * Returns the room at the specified position.
	 * 
	 * @param pos the position
	 * @return the room, or null if out of bounds
	 * @throws NullPointerException if pos is null
	 */
	public Room getRoom(Position pos) {
		Objects.requireNonNull(pos);
		return getRoom(pos.row(), pos.col());
	}

	/**
	 * Checks if a room is accessible from the hero's position. A room is accessible
	 * if it's adjacent to the hero or already visited.
	 * 
	 * @param room    the room to check
	 * @param heroPos the hero's current position
	 * @return true if the room is accessible
	 */
	public boolean isRoomAccessible(Room room, Position heroPos) {
		if (room == null || heroPos == null) {
			return false;
		}

		Position roomPos = room.position();

		if (room.isVisited()) {
			return true;
		}

		int d = Math.abs(heroPos.row() - roomPos.row()) + Math.abs(heroPos.col() - roomPos.col());

		return d == 1;
	}
}
