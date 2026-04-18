package fr.uge.backpackhero;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a room in the dungeon map. Each room has a type, position, and can
 * contain enemies, grids, or treasures. Rooms can be visited and some can be
 * converted to corridors.
 */
public class Room {
	private final TypeRoom type;
	private final Position position;
	private boolean visited;
	private Grid grid;
	private Treasure treasure;
	private final ArrayList<EnemyBase> enemies;
	private boolean convertedToCorridor;

	/**
	 * Creates a new room with the specified type and position. The room starts as
	 * unvisited and not converted to a corridor.
	 * 
	 * @param type     the type of the room
	 * @param position the position of the room in the dungeon
	 * @throws NullPointerException if type or position is null
	 */
	public Room(TypeRoom type, Position position) {
		this.type = Objects.requireNonNull(type);
		this.position = Objects.requireNonNull(position);
		this.visited = false;
		this.grid = null;
		this.enemies = new ArrayList<>();
		this.convertedToCorridor = false;
	}

	/**
	 * Marks this room as converted to a corridor. The room keeps its original type
	 * but will be displayed as a corridor. EXIT rooms and actual CORRIDOR rooms
	 * cannot be converted.
	 */
	public void convertToCorridor() {
		if (type != TypeRoom.EXIT && type != TypeRoom.CORRIDOR) {
			this.convertedToCorridor = true;
		}
	}

	/**
	 * Returns the effective type of the room (converted or original).
	 * 
	 * @return CORRIDOR if the room has been converted, otherwise the original type
	 */
	public TypeRoom getEffectiveType() {
		return convertedToCorridor ? TypeRoom.CORRIDOR : type;
	}

	/**
	 * Returns the original type of the room.
	 * 
	 * @return the room's original type
	 */
	public TypeRoom type() {
		return type;
	}

	/**
	 * Checks if the room has been converted to a corridor.
	 * 
	 * @return true if the room has been converted, false otherwise
	 */
	public boolean isConvertedToCorridor() {
		return convertedToCorridor;
	}

	/**
	 * Returns the position of the room in the dungeon.
	 * 
	 * @return the room's position
	 */
	public Position position() {
		return position;
	}

	/**
	 * Checks if the room has been visited by the hero.
	 * 
	 * @return true if the room has been visited, false otherwise
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * Marks the room as visited.
	 */
	public void markVisited() {
		this.visited = true;
	}

	/**
	 * Adds an enemy to the room.
	 * 
	 * @param enemy the enemy to add
	 * @throws NullPointerException if enemy is null
	 */
	public void addEnemy(EnemyBase enemy) {
		Objects.requireNonNull(enemy);
		enemies.add(enemy);
	}

	/**
	 * Returns an immutable copy of the list of enemies in the room.
	 * 
	 * @return a list of enemies
	 */
	public List<EnemyBase> getEnemies() {
		return List.copyOf(enemies);
	}

	/**
	 * Checks if the room has a grid.
	 * 
	 * @return true if the room has a grid, false otherwise
	 */
	public boolean hasGrid() {
		return (grid != null);
	}

	/**
	 * Returns the grid of the room.
	 * 
	 * @return the room's grid
	 * @throws IllegalStateException if the room has no grid
	 */
	public Grid grid() {
		if (grid == null) {
			throw new IllegalStateException("This room has no grid");
		}
		return grid;
	}

	/**
	 * Checks if the room's grid is unlocked.
	 * 
	 * @return true if the grid exists and is unlocked, false otherwise
	 */
	public boolean isGridUnlocked() {
		return (grid != null && grid.isUnlocked());
	}

	/**
	 * Sets whether the room has a grid. EXIT rooms never have grids.
	 * 
	 * @param hasGrid true to create a new grid, false to remove it
	 */
	public void setHasGrid(boolean hasGrid) {
		if (type == TypeRoom.EXIT) {
			this.grid = null;
			return;
		}

		if (hasGrid) {
			grid = new Grid();
		} else {
			grid = null;
		}
	}

	/**
	 * Attempts to unlock the room's grid using a key from the hero. EXIT rooms are
	 * always considered unlocked.
	 * 
	 * @param hero the hero attempting to unlock the grid
	 * @return true if the grid is unlocked (or doesn't exist), false if the hero
	 *         has no key
	 * @throws NullPointerException if hero is null
	 */
	public boolean unlockGrid(Hero hero) {
		Objects.requireNonNull(hero);

		if (type == TypeRoom.EXIT) {
			return true;
		}

		if (grid == null) {
			return true;
		}
		return grid.unlock(hero);
	}

	/**
	 * Checks if the room has a treasure.
	 * 
	 * @return true if the room is a TREASURE room and has a treasure set, false
	 *         otherwise
	 */
	public boolean hasTreasure() {
		return type == TypeRoom.TREASURE && treasure != null;
	}

	/**
	 * Returns the treasure in the room.
	 * 
	 * @return the room's treasure
	 * @throws IllegalStateException if the room is not a TREASURE room or if the
	 *                               treasure is not initialized
	 */
	public Treasure treasure() {
		if (type != TypeRoom.TREASURE) {
			throw new IllegalStateException("This room is not a treasure room");
		}
		if (treasure == null) {
			throw new IllegalStateException("Treasure not initialized");
		}
		return treasure;
	}

	/**
	 * Sets the treasure for the room. Can only be called on TREASURE rooms and only
	 * once.
	 * 
	 * @param treasure the treasure to set
	 * @throws NullPointerException  if treasure is null
	 * @throws IllegalStateException if the room is not a TREASURE room or if a
	 *                               treasure is already set
	 */
	public void setTreasure(Treasure treasure) {
		Objects.requireNonNull(treasure);
		if (type != TypeRoom.TREASURE) {
			throw new IllegalStateException("Cannot set treasure in a non-treasure room");
		}
		if (this.treasure != null) {
			throw new IllegalStateException("Treasure already set");
		}
		this.treasure = treasure;
	}
}