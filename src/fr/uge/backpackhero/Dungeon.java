package fr.uge.backpackhero;

import module java.base;

/**
 * Represents a dungeon with multiple floors. The hero explores rooms floor by
 * floor, progressing through the dungeon.
 */
public class Dungeon {

	private final Floor[] floors;
	private int currentFloorIndex;
	private Position heroPosition;

	/**
	 * Creates a new dungeon with the specified number of floors.
	 * 
	 * @param nbFloors the number of floors in the dungeon
	 * @throws IllegalArgumentException if nbFloors is not positive
	 */
	public Dungeon(int nbFloors) {
		if (nbFloors <= 0) {
			throw new IllegalArgumentException("The number of floors must be positive");
		}

		this.floors = new Floor[nbFloors];
		this.currentFloorIndex = 0;
	}

	/**
	 * Initializes all floors in the dungeon with generated rooms. Each floor
	 * increases in size as the dungeon progresses.
	 * 
	 * @param random the random number generator for floor generation
	 * @throws NullPointerException if random is null
	 */
	public void initDungeon(Random random) {
		Objects.requireNonNull(random);

		for (int i = 0; i < floors.length; i++) {
			int floorNumber = i + 1;
			int height = floorHeightOf(floorNumber);
			int width = floorWidthOf(floorNumber);
			floors[i] = FloorGenerators.generate(i + 1, height, width, random);
		}

		this.heroPosition = currentFloor().startPos();
	}

	/**
	 * Calculates the height of a floor based on its number. Height increases with
	 * floor number, capped at 10.
	 * 
	 * @param floorNumber the floor number
	 * @return the height of the floor
	 */
	private static int floorHeightOf(int floorNumber) {
		int beginning = 5;
		int moreHeight = (floorNumber - 1) / 2;
		return Math.min(beginning + moreHeight, 10);
	}

	/**
	 * Calculates the width of a floor based on its number. Width increases with
	 * floor number, capped at 21.
	 * 
	 * @param floorNumber the floor number
	 * @return the width of the floor
	 */
	private static int floorWidthOf(int floorNumber) {
		int beginning = 11;
		int moreWidth = 2 * ((floorNumber - 1) / 2);
		return Math.min(beginning + moreWidth, 21);
	}

	/**
	 * Returns the current floor the hero is on.
	 * 
	 * @return the current floor
	 */
	public Floor currentFloor() {
		return floors[currentFloorIndex];
	}

	/**
	 * Returns the room where the hero is currently located.
	 * 
	 * @return the current room
	 */
	public Room curentRoom() {
		return currentFloor().getRoom(heroPosition);
	}

	/**
	 * Advances to the next floor. Resets hero position to the start of the new
	 * floor.
	 * 
	 * @return true if there is a next floor, false if already on last floor
	 */
	public boolean nextFloor() {
		if (currentFloorIndex >= floors.length - 1) {
			return false;
		}

		currentFloorIndex++;
		heroPosition = currentFloor().startPos();
		return true;
	}

}
