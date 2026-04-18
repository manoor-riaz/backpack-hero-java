package fr.uge.backpackhero;

import module java.base;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating dungeon floors. Generates connected rooms with
 * various types including enemies, merchants, and treasures.
 */
public class FloorGenerators {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private FloorGenerators() {
	}

	/**
	 * Generates a new floor with the specified dimensions.
	 * 
	 * @param floorNumber the floor number
	 * @param height      the floor height
	 * @param width       the floor width
	 * @param random      the random number generator
	 * @return a new floor
	 * @throws NullPointerException     if random is null
	 * @throws IllegalArgumentException if height or width is not positive
	 */
	public static Floor generate(int floorNumber, int height, int width, Random random) {
		Objects.requireNonNull(random);
		if (height <= 0 || width <= 0) {
			throw new IllegalArgumentException("height and width have to be sup than 0");
		}

		var rooms = new Room[height][width];

		var startPos = new Position(random.nextInt(height), random.nextInt(width));
		var connectedRooms = connectedRooms(startPos, height, width, random);
		initRooms(rooms, height, width);
		Collections.shuffle(connectedRooms, random);
		initSpecificRooms(rooms, connectedRooms, random);
		addGrids(rooms, connectedRooms, startPos, random);

		return new Floor(floorNumber, height, width, rooms, startPos);
	}

	/**
	 * Generates a list of connected room positions starting from a given position.
	 * 
	 * @param start  the starting position
	 * @param height the floor height
	 * @param width  the floor width
	 * @param random the random number generator
	 * @return list of connected room positions
	 */
	private static List<Position> connectedRooms(Position start, int height, int width, Random random) {
		var totalRooms = 10 + random.nextInt(21);

		var roomsTmp = new boolean[height][width];
		var roomAdded = new ArrayList<Position>();
		var roomPossible = new HashSet<Position>();

		roomsTmp[start.row()][start.col()] = true;
		roomAdded.add(start);

		addRoomsPossible(start, roomsTmp, roomPossible, height, width);
		moreRooms(totalRooms, roomsTmp, roomAdded, roomPossible, height, width, random);

		return roomAdded;
	}

	/**
	 * Adds possible room positions adjacent to the given position.
	 * 
	 * @param p            the current position
	 * @param roomsTmp     the grid tracking added rooms
	 * @param roomPossible the set of possible positions
	 * @param height       the floor height
	 * @param width        the floor width
	 */
	private static void addRoomsPossible(Position p, boolean[][] roomsTmp, Set<Position> roomPossible, int height,
			int width) {
		int[][] neighbors = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		for (int[] neighbor : neighbors) {
			var newRow = p.row() + neighbor[0];
			var newCol = p.col() + neighbor[1];

			if (positionValide(newRow, newCol, height, width) && (!roomsTmp[newRow][newCol])) {
				roomPossible.add(new Position(newRow, newCol));
			}
		}
	}

	/**
	 * Checks if a position is valid within the floor bounds.
	 * 
	 * @param row    the row position
	 * @param col    the column position
	 * @param height the floor height
	 * @param width  the floor width
	 * @return true if the position is valid
	 */
	private static boolean positionValide(int row, int col, int height, int width) {
		return ((row >= 0 && row < height) && (col >= 0 && col < width));
	}

	/**
	 * Adds more rooms until the target is reached.
	 * 
	 * @param totalRoom    the target number of rooms
	 * @param roomsTmp     the grid tracking added rooms
	 * @param roomAdded    the list of added room positions
	 * @param roomPossible the set of possible positions
	 * @param height       the floor height
	 * @param width        the floor width
	 * @param random       the random number generator
	 */
	private static void moreRooms(int totalRoom, boolean[][] roomsTmp, List<Position> roomAdded,
			Set<Position> roomPossible, int height, int width, Random random) {
		while ((roomAdded.size() < totalRoom) && !roomPossible.isEmpty()) {
			Position roomChoosed = chooseRandom(roomPossible, random);
			roomPossible.remove(roomChoosed);

			if (!roomsTmp[roomChoosed.row()][roomChoosed.col()]) {
				roomsTmp[roomChoosed.row()][roomChoosed.col()] = true;
				roomAdded.add(roomChoosed);
				addRoomsPossible(roomChoosed, roomsTmp, roomPossible, height, width);
			}
		}
	}

	/**
	 * Chooses a random position from a set.
	 * 
	 * @param set    the set of positions
	 * @param random the random number generator
	 * @return a random position from the set
	 */
	private static Position chooseRandom(Set<Position> set, Random random) {
		var index = random.nextInt(set.size());
		var iterator = set.iterator();
		for (int i = 0; i < index; i++) {
			iterator.next();
		}
		return iterator.next();
	}

	/**
	 * Initializes all rooms to null.
	 * 
	 * @param rooms  the rooms array
	 * @param height the floor height
	 * @param width  the floor width
	 */
	private static void initRooms(Room[][] rooms, int height, int width) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				rooms[row][col] = null;
			}
		}
	}

	/**
	 * Initializes specific room types (enemy, merchant, treasure, etc.).
	 * 
	 * @param rooms     the rooms array
	 * @param positions the list of positions
	 * @param random    the random number generator
	 */
	private static void initSpecificRooms(Room[][] rooms, List<Position> positions, Random random) {
		var types = AllSpecificRooms(random);
		Collections.shuffle(types);

		var i = 0;
		for (i = 0; i < types.size() && i < positions.size(); i++) {
			createSpecificRoom(rooms, types.get(i), positions.get(i), random);
		}

		fillWithCorridors(rooms, positions, i);
	}

	/**
	 * Creates a specific room at the given position.
	 * 
	 * @param rooms  the rooms array
	 * @param type   the room type
	 * @param pos    the position
	 * @param random the random number generator
	 */
	private static void createSpecificRoom(Room[][] rooms, TypeRoom type, Position pos, Random random) {
		var room = new Room(type, pos);
		rooms[pos.row()][pos.col()] = room;

		if (type == TypeRoom.ENEMY) {
			addEnemiesInRoom(room, random);
		}
	}

	/**
	 * Returns the list of all specific room types for a floor.
	 * 
	 * @param random the random number generator
	 * @return list of room types
	 */
	private static List<TypeRoom> AllSpecificRooms(Random random) {
		var types = new ArrayList<TypeRoom>();
		for (int i = 0; i < 3; i++) {
			types.add(TypeRoom.ENEMY);
		}
		types.add(TypeRoom.MERCHANT);
		types.add(TypeRoom.HEALER);
		for (int i = 0; i < 3; i++) {
			types.add(TypeRoom.TREASURE);
		}
		types.add(TypeRoom.EXIT);

		return types;
	}

	/**
	 * Adds enemies to a room based on random generation.
	 * 
	 * @param room   the room to add enemies to
	 * @param random the random number generator
	 */
	private static void addEnemiesInRoom(Room room, Random random) {
		int nbenemies = 1 + random.nextInt(3);

		for (int i = 0; i < nbenemies; i++) {
			int tmp = random.nextInt(5);
			EnemyBase enemy = createEnemy(tmp);
			room.addEnemy(enemy);
		}
	}

	/**
	 * Creates an enemy based on the given type index.
	 * 
	 * @param type the enemy type index (0-4)
	 * @return a new enemy
	 */
	private static EnemyBase createEnemy(int type) {
		return switch (type) {
		case 0 -> createRatWolf();
		case 1 -> createSmallRatWolf();
		case 2 -> createFrogWizard();
		case 3 -> createLivingShadow();
		default -> createQueenBee();
		};
	}

	/**
	 * Creates a Rat-Wolf enemy.
	 * 
	 * @return a Rat-Wolf enemy
	 */
	private static EnemyBase createRatWolf() {
		var effects = List.of(EffectType.POISON, EffectType.WEAK);
		return new EnemyBase("Rat-Loup", 45, 6, 7, 9, 13, 16, List.of(), 0, effects, 30);
	}

	/**
	 * Creates a Small Rat-Wolf enemy.
	 * 
	 * @return a Small Rat-Wolf enemy
	 */
	private static EnemyBase createSmallRatWolf() {
		var effects = List.of(EffectType.POISON);
		return new EnemyBase("Petit Rat-Loup", 32, 6, 7, 9, 14, 14, List.of(), 0, effects, 25);
	}

	/**
	 * Creates a Frog Wizard enemy.
	 * 
	 * @return a Frog Wizard enemy
	 */
	private static EnemyBase createFrogWizard() {
		var curses = List.of(new Curses("Bave", Rarity.COMMON, Shape.rectangle(new Dimension(1, 1))),
				new Curses("Z Maudit", Rarity.RARE,
						new Shape(List.of(new Position(0, 0), new Position(0, 1), new Position(1, 1),
								new Position(1, 2)))),
				new Curses("Lenteur", Rarity.COMMON, Shape.rectangle(new Dimension(1, 1))));
		var effects = List.of(EffectType.POISON);
		return new EnemyBase("Sorcier Grenouille", 45, 8, 0, 0, 0, 0, curses, 100, effects, 4);
	}

	/**
	 * Creates a Living Shadow enemy.
	 * 
	 * @return a Living Shadow enemy
	 */
	private static EnemyBase createLivingShadow() {
		var curses = List.of(new Curses("Grande", Rarity.RARE, Shape.rectangle(new Dimension(2, 2))));
		return new EnemyBase("Ombre Vivante", 50, 0, 0, 0, 0, 0, curses, 100, List.of(), 0);
	}

	/**
	 * Creates a Queen Bee enemy.
	 * 
	 * @return a Queen Bee enemy
	 */
	private static EnemyBase createQueenBee() {
		var effects = List.of(EffectType.POISON);
		return new EnemyBase("Reine Abeille", 74, 20, 15, 15, 0, 0, List.of(), 0, effects, 1);
	}

	/**
	 * Fills remaining positions with corridor rooms.
	 * 
	 * @param rooms     the rooms array
	 * @param positions the list of positions
	 * @param index     the starting index for corridors
	 */
	private static void fillWithCorridors(Room[][] rooms, List<Position> positions, int index) {
		for (int i = index; i < positions.size(); i++) {
			Position pos = positions.get(i);
			rooms[pos.row()][pos.col()] = new Room(TypeRoom.CORRIDOR, pos);
		}
	}

	/**
	 * Adds locked grids to random rooms on the floor.
	 * 
	 * @param rooms          the rooms array
	 * @param connectedRooms the list of connected room positions
	 * @param startPos       the starting position
	 * @param random         the random number generator
	 */
	private static void addGrids(Room[][] rooms, List<Position> connectedRooms, Position startPos, Random random) {
		var allRooms = connectedRooms.stream().filter(p -> (!p.equals(startPos)))
				.collect(Collectors.toCollection(ArrayList::new));
		if (allRooms.isEmpty()) {
			return;
		}

		int tmp = Math.min(3, allRooms.size());
		int nbGrid = 1 + random.nextInt(tmp);

		Collections.shuffle(allRooms, random);

		allRooms.stream().limit(nbGrid).forEach(pos -> addGridToRoom(rooms, pos));
	}

	/**
	 * Adds a grid to a room at the specified position.
	 * 
	 * @param rooms the rooms array
	 * @param pos   the position
	 */
	private static void addGridToRoom(Room[][] rooms, Position pos) {
		var room = rooms[pos.row()][pos.col()];
		if (room != null) {
			room.setHasGrid(true);
		}
	}
}