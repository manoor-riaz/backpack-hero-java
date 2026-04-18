package fr.uge.backpackhero;

import module java.base;

/**
 * Represents the hero's backpack that stores items. The backpack has a grid
 * system where items can be placed. The grid can be expanded by unlocking
 * additional cells.
 */
public class BackPack {
	private static final int MAX_H = 5;
	private static final int MAX_W = 7;
	private static final int INIT_H = 3;
	private static final int INIT_W = 3;

	private final Item[][] backpack;
	private final ArrayList<PlacedItem> items;
	private final HashSet<Position> backpackCells;
	private int nbCells;

	/**
	 * Creates a new backpack with initial size. Initial size is 3x3, maximum size
	 * is 7x5.
	 */
	public BackPack() {
		this.backpack = new Item[MAX_H][MAX_W];
		this.items = new ArrayList<>();
		this.backpackCells = new HashSet<>();

		for (int row = 1; row <= INIT_H; row++) {
			for (int col = 2; col <= (INIT_W + 1); col++) {
				backpackCells.add(new Position(row, col));
			}
		}
	}

	/**
	 * Checks if a position is within the backpack bounds.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return true if the position is within bounds
	 */
	private boolean inBackpack(int row, int col) {
		return row >= 0 && row < MAX_H && col >= 0 && col < MAX_W;
	}

	/**
	 * Checks if a cell is unlocked and available for use.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return true if the cell is unlocked
	 */
	public boolean isCellUnlocked(int row, int col) {
		if (!inBackpack(row, col)) {
			return false;
		}
		return backpackCells.contains(new Position(row, col));
	}

	/**
	 * Checks if a cell can be unlocked. A cell can be unlocked if it's adjacent to
	 * an already unlocked cell.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return true if the cell can be unlocked
	 */
	public boolean canUnlockCell(int row, int col) {
		if (!inBackpack(row, col)) {
			return false;
		}
		if (isCellUnlocked(row, col)) {
			return false;
		}

		return hasUnlockedNeighbor(row, col);
	}

	/**
	 * Checks if a cell has at least one unlocked neighbor.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return true if at least one neighbor is unlocked
	 */
	private boolean hasUnlockedNeighbor(int row, int col) {
		int[][] neighbors = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
		for (int[] n : neighbors) {
			int newR = row + n[0];
			int newC = col + n[1];
			if (isCellUnlocked(newR, newC)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Unlocks a cell if possible.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return true if the cell was successfully unlocked
	 */
	public boolean unlockCell(int row, int col) {
		if (!canUnlockCell(row, col)) {
			return false;
		}
		if (nbCells <= 0) {
			return false;
		}
		backpackCells.add(new Position(row, col));
		nbCells--;
		return true;
	}

	/**
	 * Adds unlockable cells to the backpack.
	 * 
	 * @param nb the number of cells to add
	 * @throws IllegalArgumentException if nb is negative
	 */
	public void addNbCells(int nb) {
		if (nb < 0) {
			throw new IllegalArgumentException("count must be >= 0");
		}
		nbCells += nb;
	}

	/**
	 * Returns the number of cells available to unlock.
	 * 
	 * @return the number of unlockable cells
	 */
	public int getNbCells() {
		return nbCells;
	}

	/**
	 * Checks if an item can be added at the specified position with the given
	 * orientation.
	 * 
	 * @param item        the item to check
	 * @param pos         the position to place the item
	 * @param orientation the orientation of the item
	 * @return true if the item can be added
	 * @throws NullPointerException if any parameter is null
	 */
	public boolean canAdd(Item item, Position pos, Orientation orientation) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(orientation);

		for (var cell : item.shape().turnItem(orientation)) {
			if (!canPlaceCell(pos, cell)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if a specific cell of an item can be placed at a position.
	 * 
	 * @param basePos    the base position of the item
	 * @param cellOffset the offset of the cell from the base
	 * @return true if the cell can be placed
	 */
	private boolean canPlaceCell(Position basePos, Position cellOffset) {
		var row = basePos.row() + cellOffset.row();
		var col = basePos.col() + cellOffset.col();

		if (!inBackpack(row, col)) {
			return false;
		}
		if (!isCellUnlocked(row, col)) {
			return false;
		}
		if (backpack[row][col] != null) {
			return false;
		}

		return true;
	}

	/**
	 * Places an item in the backpack grid.
	 * 
	 * @param item        the item to place
	 * @param pos         the position to place it
	 * @param orientation the orientation of the item
	 */
	private void placeItem(Item item, Position pos, Orientation orientation) {
		for (var cell : item.shape().turnItem(orientation)) {
			var row = pos.row() + cell.row();
			var col = pos.col() + cell.col();
			backpack[row][col] = item;
		}
	}

	/**
	 * Finds the index of an item in the items list.
	 * 
	 * @param item the item to find
	 * @return the index, or -1 if not found
	 */
	private int indexOf(Item item) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).item() == item) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Adds an item to the backpack at the specified position.
	 * 
	 * @param item        the item to add
	 * @param pos         the position to place the item
	 * @param orientation the orientation of the item
	 * @return true if the item was added, false if there's no space
	 * @throws NullPointerException if any parameter is null
	 */
	public boolean add(Item item, Position pos, Orientation orientation) {
		Objects.requireNonNull(item);
		Objects.requireNonNull(pos);
		Objects.requireNonNull(orientation);

		if (!canAdd(item, pos, orientation)) {
			return false;
		}

		placeItem(item, pos, orientation);
		items.add(new PlacedItem(item, pos, orientation));

		return true;
	}

	/**
	 * Removes an item from the backpack.
	 * 
	 * @param item the item to remove
	 * @return true if the item was removed, false if not found
	 * @throws NullPointerException if item is null
	 */
	public boolean remove(Item item) {
		Objects.requireNonNull(item);

		var iterator = items.iterator();
		while (iterator.hasNext()) {
			var placedItem = iterator.next();
			if (placedItem.item() == item) {
				removeFromBackpack(placedItem.item(), placedItem.pos(), placedItem.orientation());
				iterator.remove();
				return true;
			}
		}

		return false;
	}

	/**
	 * Removes an item from the backpack grid.
	 * 
	 * @param item        the item to remove
	 * @param pos         the position of the item
	 * @param orientation the orientation of the item
	 */
	private void removeFromBackpack(Item item, Position pos, Orientation orientation) {
		for (var cell : item.shape().turnItem(orientation)) {
			var row = pos.row() + cell.row();
			var col = pos.col() + cell.col();
			if (inBackpack(row, col) && backpack[row][col] == item) {
				backpack[row][col] = null;
			}
		}
	}

	/**
	 * Returns the item at the specified position.
	 * 
	 * @param row the row position
	 * @param col the column position
	 * @return the item at this position, or null if empty or out of bounds
	 */
	public Item itemAt(int row, int col) {
		if (!inBackpack(row, col)) {
			return null;
		}
		return backpack[row][col];
	}

	/**
	 * Returns all items in the backpack.
	 * 
	 * @return a list of all items
	 */
	public List<Item> items() {
		return items.stream().map(PlacedItem::item).toList();
	}

	/**
	 * Checks if the backpack contains the specified item.
	 * 
	 * @param item the item to check
	 * @return true if the item is in the backpack
	 * @throws NullPointerException if item is null
	 */
	public boolean contains(Item item) {
		Objects.requireNonNull(item);
		return items.stream().anyMatch(p -> p.item() == item);
	}

	/**
	 * Returns the position of an item in the backpack.
	 * 
	 * @param item the item to find
	 * @return the position of the item, or null if not found
	 * @throws NullPointerException if item is null
	 */
	public Position position(Item item) {
		Objects.requireNonNull(item);

		return items.stream().filter(i -> i.item() == item).map(PlacedItem::pos).findFirst().orElse(null);
	}

	/**
	 * Returns the orientation of an item in the backpack.
	 * 
	 * @param item the item to find
	 * @return the orientation of the item
	 * @throws NullPointerException   if item is null
	 * @throws NoSuchElementException if item is not in the backpack
	 */
	public Orientation orientation(Item item) {
		Objects.requireNonNull(item);

		return items.stream().filter(i -> i.item() == item).map(PlacedItem::orientation).findFirst().orElseThrow();
	}

	/**
	 * Returns all cells occupied by an item.
	 * 
	 * @param item the item to check
	 * @return set of positions occupied by the item
	 * @throws NullPointerException if item is null
	 */
	public Set<Position> itemCells(Item item) {
		Objects.requireNonNull(item);

		var i = indexOf(item);
		if (i < 0) {
			return Set.of();
		}

		return calculateItemCells(items.get(i), item);
	}

	/**
	 * Calculates all cells occupied by a placed item.
	 * 
	 * @param placedItem the placed item information
	 * @param item       the item
	 * @return set of positions occupied by the item
	 */
	private Set<Position> calculateItemCells(PlacedItem placedItem, Item item) {
		var res = new HashSet<Position>();

		for (var cell : item.shape().turnItem(placedItem.orientation())) {
			var row = placedItem.pos().row() + cell.row();
			var col = placedItem.pos().col() + cell.col();
			res.add(new Position(row, col));
		}

		return res;
	}

	/**
	 * Returns all items adjacent to the given item.
	 * 
	 * @param item the item to check neighbors for
	 * @return list of neighboring items
	 * @throws NullPointerException if item is null
	 */
	public List<Item> neighbourItems(Item item) {
		Objects.requireNonNull(item);

		if (!contains(item)) {
			return List.of();
		}

		var itemCells = itemCells(item);
		return findNeighborItems(item, itemCells);
	}

	/**
	 * Finds all items neighboring the given cells.
	 * 
	 * @param item      the original item
	 * @param itemCells the cells occupied by the item
	 * @return list of neighboring items
	 */
	private List<Item> findNeighborItems(Item item, Set<Position> itemCells) {
		var neighbourItems = new HashSet<Item>();
		int[][] neighbours = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		for (var cell : itemCells) {
			addNeighborsForCell(cell, neighbours, item, neighbourItems);
		}

		return new ArrayList<>(neighbourItems);
	}

	/**
	 * Adds neighboring items for a specific cell.
	 * 
	 * @param cell           the cell to check
	 * @param neighbours     the neighbor directions
	 * @param originalItem   the original item to exclude
	 * @param neighbourItems the set to add neighbors to
	 */
	private void addNeighborsForCell(Position cell, int[][] neighbours, Item originalItem, Set<Item> neighbourItems) {
		for (var neighbour : neighbours) {
			int newR = cell.row() + neighbour[0];
			int newC = cell.col() + neighbour[1];

			if (inBackpack(newR, newC)) {
				var neighbourItem = backpack[newR][newC];
				if (neighbourItem != null && neighbourItem != originalItem) {
					neighbourItems.add(neighbourItem);
				}
			}
		}
	}

	/**
	 * Checks if the backpack contains any armor.
	 * 
	 * @return true if there is at least one armor piece
	 */
	public boolean ifHasArmor() {
		return items.stream().anyMatch(i -> i.item().isArmor());
	}

	/**
	 * Returns the gold item in the backpack.
	 * 
	 * @return the gold item, or null if there's no gold
	 */
	public Gold getGold() {
		for (var itemPos : items) {
			if (itemPos.item().isGold()) {
				return switch (itemPos.item()) {
				case Gold res -> res;
				default -> throw new IllegalArgumentException("Unexpected value: " + itemPos.item());
				};
			}
		}
		return null;
	}

	/**
	 * Returns the total amount of gold in the backpack.
	 * 
	 * @return the total gold, or 0 if there's no gold
	 */
	public int getGoldTotal() {
		Gold gold = getGold();
		return gold != null ? gold.purse() : 0;
	}

	/**
	 * Merges gold into the backpack. If gold already exists, adds to it. Otherwise
	 * adds the new gold.
	 * 
	 * @param goldAmount the amount of gold to add
	 */
	public void mergeGold(int goldAmount) {
		if (goldAmount <= 0) {
			return;
		}

		var existingGold = getGold();
		if (existingGold != null) {
			existingGold.add(goldAmount);
		} else {
			freeAdd(new Gold(goldAmount));
		}
	}

	/**
	 * Checks if the backpack contains a key.
	 * 
	 * @return true if there's a key in the backpack
	 */
	public boolean hasKey() {
		return items.stream().anyMatch(i -> i.item().isKey());
	}

	/**
	 * Removes one key from the backpack.
	 * 
	 * @return true if a key was removed, false if no key found
	 */
	public boolean removeKey() {
		for (var itemPos : items) {
			if (itemPos.item().isKey()) {
				remove(itemPos.item());
				return true;
			}
		}
		return false;
	}

	/**
	 * Consumes the specified amount of mana from mana stones.
	 * 
	 * @param amount the amount of mana to consume
	 * @return true if enough mana was available, false otherwise
	 * @throws IllegalArgumentException if amount is not positive
	 */
	public boolean consumeMana(int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("amount must be > 0");
		}

		int totalMana = getTotalMana();
		if (totalMana < amount) {
			return false;
		}

		return consumeManaFromStones(amount);
	}

	/**
	 * Consumes mana from mana stones in the backpack.
	 * 
	 * @param amount the amount of mana to consume
	 * @return true if mana was consumed successfully
	 */
	private boolean consumeManaFromStones(int amount) {
		int remaining = amount;
		var copy = new ArrayList<>(items);

		for (var itemPos : copy) {
			if (remaining <= 0) {
				break;
			}

			Item item = itemPos.item();
			if (item.isManaStone()) {
				remaining = consumeManaStone(item, remaining);
			}
		}

		return true;
	}

	/**
	 * Consumes mana from a single mana stone.
	 * 
	 * @param item      the mana stone item
	 * @param remaining the remaining mana to consume
	 * @return the updated remaining mana
	 */
	private int consumeManaStone(Item item, int remaining) {
		return switch (item) {
		case ManaStone manaStone -> {
			int stoneMana = manaStone.mana();
			remove(manaStone);
			yield remaining - stoneMana;
		}
		default -> remaining;
		};
	}

	/**
	 * Converts an item to a mana stone if applicable.
	 * 
	 * @param item the item to check
	 * @return stream containing the mana stone, or empty stream
	 */
	private static Stream<ManaStone> isManaStone(Item item) {
		return switch (item) {
		case ManaStone m -> Stream.of(m);
		default -> Stream.of();
		};
	}

	/**
	 * Returns the total mana available from all mana stones.
	 * 
	 * @return the total mana
	 */
	public int getTotalMana() {
		return items.stream().map(PlacedItem::item).flatMap(BackPack::isManaStone).mapToInt(ManaStone::mana).sum();
	}

	/**
	 * Returns the total passive shield from armor with no cost.
	 * 
	 * @return the total passive shield points
	 */
	public int passiveShield() {
		int res = 0;
		for (var itemPos : items) {
			res = addArmorShield(itemPos.item(), res);
		}
		return res;
	}

	/**
	 * Adds armor shield to the total if the item is zero-cost armor.
	 * 
	 * @param item          the item to check
	 * @param currentShield the current shield total
	 * @return the updated shield total
	 */
	private int addArmorShield(Item item, int currentShield) {
		return switch (item) {
		case Armor armor -> {
			if (armor.cost() == 0) {
				yield currentShield + armor.stats().shield();
			}
			yield currentShield;
		}
		default -> currentShield;
		};
	}

	/**
	 * Returns the current height of the backpack.
	 * 
	 * @return the height
	 */
	public int height() {
		return MAX_H;
	}

	/**
	 * Returns the current width of the backpack.
	 * 
	 * @return the width
	 */
	public int width() {
		return MAX_W;
	}

	/**
	 * Adds an item at the first free position found.
	 * 
	 * @param item the item to add
	 * @return true if the item was added, false if no space available
	 * @throws NullPointerException if item is null
	 */
	public boolean freeAdd(Item item) {
		Objects.requireNonNull(item);

		for (int row = 0; row < MAX_H; row++) {
			for (int col = 0; col < MAX_W; col++) {
				if (canAdd(item, new Position(row, col), Orientation.ORIENTATION_0)) {
					return add(item, new Position(row, col), Orientation.ORIENTATION_0);
				}
			}
		}
		return false;
	}

	/**
	 * Checks if a curse can be placed at the specified position. A curse can be
	 * placed if all its cells are within unlocked backpack cells. Unlike normal
	 * items, curses CAN overlap with existing items (they will be destroyed).
	 * 
	 * @param curse the curse to check
	 * @param row   the row position
	 * @param col   the column position
	 * @return true if the curse can be placed
	 * @throws NullPointerException if curse is null
	 */
	public boolean canPlaceCurse(Curses curse, int row, int col) {
		Objects.requireNonNull(curse);
		return canPlaceCurse(curse, new Position(row, col));
	}

	/**
	 * Checks if a curse can be placed at the specified position. A curse can be
	 * placed if all its cells are within unlocked backpack cells. Curses CANNOT
	 * overlap with other curses.
	 * 
	 * @param curse the curse to check
	 * @param pos   the position
	 * @return true if the curse can be placed
	 * @throws NullPointerException if any parameter is null
	 */
	public boolean canPlaceCurse(Curses curse, Position pos) {
		Objects.requireNonNull(curse);
		Objects.requireNonNull(pos);

		for (var cell : curse.shape().turnItem(Orientation.ORIENTATION_0)) {
			if (!canPlaceCurseCell(pos, cell)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if a specific cell of a curse can be placed.
	 * 
	 * @param basePos    the base position of the curse
	 * @param cellOffset the cell offset
	 * @return true if the cell can be placed
	 */
	private boolean canPlaceCurseCell(Position basePos, Position cellOffset) {
		var row = basePos.row() + cellOffset.row();
		var col = basePos.col() + cellOffset.col();

		if (!inBackpack(row, col)) {
			return false;
		}

		if (!isCellUnlocked(row, col)) {
			return false;
		}

		Item existingItem = itemAt(row, col);
		if (existingItem != null && existingItem.isCurse()) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the list of items that would be destroyed if a curse is placed at the
	 * given position.
	 * 
	 * @param curse the curse to place
	 * @param pos   the position
	 * @return list of items that would be destroyed (empty if curse can't be
	 *         placed)
	 * @throws NullPointerException if any parameter is null
	 */
	public List<Item> itemsDestroyedByCurse(Curses curse, Position pos) {
		Objects.requireNonNull(curse);
		Objects.requireNonNull(pos);

		if (!canPlaceCurse(curse, pos)) {
			return List.of();
		}

		return collectDestroyedItems(curse, pos);
	}

	/**
	 * Collects all items that would be destroyed by placing a curse.
	 * 
	 * @param curse the curse to place
	 * @param pos   the position
	 * @return list of items to be destroyed
	 */
	private List<Item> collectDestroyedItems(Curses curse, Position pos) {
		var destroyedItems = new HashSet<Item>();
		for (var cell : curse.shape().turnItem(Orientation.ORIENTATION_0)) {
			var row = pos.row() + cell.row();
			var col = pos.col() + cell.col();

			Item item = itemAt(row, col);
			if (item != null) {
				destroyedItems.add(item);
			}
		}
		return new ArrayList<>(destroyedItems);
	}

	/**
	 * Places a curse at the specified position, destroying any items in the way.
	 * 
	 * @param curse the curse to place
	 * @param pos   the position
	 * @return list of destroyed items, or null if curse couldn't be placed
	 * @throws NullPointerException if any parameter is null
	 */
	public List<Item> placeCurse(Curses curse, Position pos) {
		Objects.requireNonNull(curse);
		Objects.requireNonNull(pos);

		if (!canPlaceCurse(curse, pos)) {
			return null;
		}

		var destroyedItems = itemsDestroyedByCurse(curse, pos);
		destroyedItems.forEach(this::remove);

		placeItem(curse, pos, Orientation.ORIENTATION_0);
		items.add(new PlacedItem(curse, pos, Orientation.ORIENTATION_0));

		return destroyedItems;
	}
}