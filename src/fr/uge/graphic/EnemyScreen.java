package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import fr.uge.backpackhero.*;

/**
 * Screen for combat encounters. Displays the hero's backpack, stats, and
 * enemies.
 */
public class EnemyScreen implements Screen {
	private final Combat combat;
	private final GameData gameData;
	private final BufferedImage heroImage;
	private final BufferedImage background;
	private final BufferedImage buttonImage;
	private final List<Button> buttons = new ArrayList<>();

	private final BackpackView backpackView;

	private EnemyBase selectedTarget = null;
	private final ArrayList<Rectangle> enemyHitboxes = new ArrayList<>();

	private final Queue<Curses> pendingCurses = new LinkedList<>();
	private int myScreenIndex = -1;
	private boolean waitingForCurseScreen = false;
	private ScreenManager cachedManager = null;

	/**
	 * Constructs an EnemyScreen for combat encounters.
	 *
	 * @param gameData    the game data containing all game state
	 * @param combat      the combat instance managing the fight
	 * @param heroImage   the image representing the hero
	 * @param background  the background image for the screen
	 * @param buttonImage the image used for buttons
	 * @throws NullPointerException if any parameter is null
	 */
	public EnemyScreen(GameData gameData, Combat combat, BufferedImage heroImage, BufferedImage background,
			BufferedImage buttonImage) {
		this.gameData = Objects.requireNonNull(gameData);
		this.combat = Objects.requireNonNull(combat);
		this.heroImage = Objects.requireNonNull(heroImage);
		this.background = background;
		this.buttonImage = Objects.requireNonNull(buttonImage);

		this.backpackView = new BackpackView(combat.hero().backpack(), 3, 5, 70);
		ItemImageManager.configureBackpackView(backpackView);
		backpackView.setShowLockedCells(false);
	}

	/**
	 * Adds action buttons to the screen.
	 *
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 */
	public void addActionButtons(int screenWidth, int screenHeight) {
		if (screenWidth <= 0 || screenHeight <= 0) {
			throw new IllegalArgumentException("screenWidth and screenHeight must be > 0");
		}

		int btnWidth = Math.max(120, screenWidth / 8);
		int btnHeight = Math.max(40, screenHeight / 15);
		int centerX = screenWidth / 2;
		int endTurnX = centerX - btnWidth / 2;
		int endTurnY = screenHeight - Math.max(60, screenHeight / 10);
		buttons.add(new Button(endTurnX, endTurnY, btnWidth, btnHeight, buttonImage));
	}

	/**
	 * Draws the entire combat screen.
	 *
	 * @param graphics the graphics context to draw on
	 * @param width    the width of the screen
	 * @param height   the height of the screen
	 * @throws NullPointerException if graphics is null
	 */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		Objects.requireNonNull(graphics);

		if (waitingForCurseScreen && cachedManager != null) {
			waitingForCurseScreen = false;
			if (!pendingCurses.isEmpty()) {
				processNextCurse(cachedManager);
			} else {
				checkCombatEnd(cachedManager);
			}
		}

		Hero hero = combat.hero();
		hero.refreshManaFromBackpack();

		drawBackground(graphics, width, height);

		int leftMargin = Math.max(30, width / 25);
		int topMargin = Math.max(30, height / 20);

		backpackView.setPosition(leftMargin, topMargin);
		backpackView.draw(graphics, gameData);

		drawHeroSection(graphics, width, height, hero, leftMargin, topMargin);
		drawEnemiesSection(graphics, width, height);
		drawButtons(graphics, height);
		drawInstructions(graphics, height, leftMargin);
	}

	/**
	 * Draws the background of the screen.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawBackground(Graphics2D g, int w, int h) {
		if (background != null) {
			g.drawImage(background, 0, 0, w, h, null);
		} else {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, w, h);
		}
	}

	/**
	 * Draws the hero section including image and stats.
	 *
	 * @param g          the graphics context
	 * @param w          the screen width
	 * @param h          the screen height
	 * @param hero       the hero to display
	 * @param leftMargin the left margin
	 * @param topMargin  the top margin
	 */
	private void drawHeroSection(Graphics2D g, int w, int h, Hero hero, int leftMargin, int topMargin) {
		int heroW = Math.max(120, w / 8);
		int heroH = Math.max(160, h / 4);
		int heroX = leftMargin;
		int heroY = topMargin + backpackView.getHeight() + Math.max(40, h / 15);
		g.drawImage(heroImage, heroX, heroY, heroW, heroH, null);
		drawHeroStats(g, w, h, hero, heroX, heroY, heroW);
	}

	/**
	 * Draws the hero's statistics.
	 *
	 * @param g     the graphics context
	 * @param w     the screen width
	 * @param h     the screen height
	 * @param hero  the hero whose stats to display
	 * @param heroX the x position of the hero image
	 * @param heroY the y position of the hero image
	 * @param heroW the width of the hero image
	 */
	private void drawHeroStats(Graphics2D g, int w, int h, Hero hero, int heroX, int heroY, int heroW) {
		int statsX = heroX + heroW + Math.max(15, w / 50);
		int statsY = heroY + Math.max(25, h / 30);
		int lineH = Math.max(28, h / 22);

		g.setFont(new Font("Arial", Font.BOLD, Math.max(16, h / 35)));
		g.setColor(Color.BLACK);

		g.drawString("HP: " + hero.hp() + " / " + hero.maxhp(), statsX, statsY);
		g.drawString("Shield: " + hero.shieldpoint(), statsX, statsY + lineH);
		g.drawString("EP: " + hero.ep(), statsX, statsY + lineH * 2);
		g.drawString("Mana: " + hero.mp(), statsX, statsY + lineH * 3);
		g.drawString("XP: " + hero.xp() + " / " + hero.xpNeededForNextLevel(), statsX, statsY + lineH * 4);
		g.drawString("Level: " + hero.level(), statsX, statsY + lineH * 5);

		drawBonusStats(g, h, hero, statsX, statsY, lineH);
		drawHeroEffects(g, h, hero, statsX, statsY, lineH);
	}

	/**
	 * Draws the hero's bonus stats from accessories.
	 *
	 * @param g      the graphics context
	 * @param h      the screen height
	 * @param hero   the hero
	 * @param statsX the x position for stats
	 * @param statsY the y position for stats
	 * @param lineH  the line height
	 */
	private void drawBonusStats(Graphics2D g, int h, Hero hero, int statsX, int statsY, int lineH) {
		int dmgBonus = hero.getAccessoryDamageBonus();
		int shieldBonus = hero.getAccessoryShieldBonus();
		if (dmgBonus > 0 || shieldBonus > 0) {
			g.setFont(new Font("Arial", Font.ITALIC, Math.max(14, h / 40)));
			g.setColor(new Color(100, 50, 150));
			int bonusY = statsY + lineH * 6;
			if (dmgBonus > 0)
				g.drawString("Bonus ATK: +" + dmgBonus, statsX, bonusY);
			if (shieldBonus > 0)
				g.drawString("Bonus DEF: +" + shieldBonus, statsX, bonusY + Math.max(20, h / 30));
		}
	}

	/**
	 * Draws the effects currently affecting the hero.
	 *
	 * @param g      the graphics context
	 * @param h      the screen height
	 * @param hero   the hero
	 * @param statsX the x position for stats
	 * @param statsY the y position for stats
	 * @param lineH  the line height
	 */
	private void drawHeroEffects(Graphics2D g, int h, Hero hero, int statsX, int statsY, int lineH) {
		var effects = hero.effects().allEffects();
		if (effects.isEmpty())
			return;

		int effectY = statsY + lineH * 7 + 10;
		g.setFont(new Font("Arial", Font.BOLD, Math.max(12, h / 50)));
		g.setColor(Color.WHITE);
		g.drawString("Effects:", statsX, effectY);

		g.setFont(new Font("Arial", Font.BOLD, Math.max(11, h / 55)));
		int offsetX = 0, maxW = 250, currentY = effectY + 20;

		for (var e : effects) {
			String txt = e.effect().getName() + " x" + e.acc();
			int txtW = g.getFontMetrics().stringWidth(txt) + 12;
			if (offsetX + txtW > maxW) {
				offsetX = 0;
				currentY += 22;
			}
			g.setColor(e.effect().isPositive() ? new Color(30, 120, 30) : new Color(150, 30, 30));
			g.fillRoundRect(statsX + offsetX - 2, currentY - 14, txtW, 18, 5, 5);
			g.setColor(Color.WHITE);
			g.drawString(txt, statsX + offsetX + 4, currentY);
			offsetX += txtW + 4;
		}
	}

	/**
	 * Draws the enemies section.
	 *
	 * @param g the graphics context
	 * @param w the screen width
	 * @param h the screen height
	 */
	private void drawEnemiesSection(Graphics2D g, int w, int h) {
		int startX = w / 2 + Math.max(30, w / 20);
		int startY = Math.max(60, h / 10);
		int spacing = Math.max(180, h / 4);
		int enemyW = Math.max(280, w / 4);
		int enemyH = Math.max(160, h / 5);

		enemyHitboxes.clear();
		List<EnemyBase> enemies = combat.enemies();
		for (int i = 0; i < enemies.size(); i++) {
			drawSingleEnemy(g, h, enemies.get(i), startX, startY + i * spacing, enemyW, enemyH);
		}
	}

	/**
	 * Draws a single enemy card.
	 *
	 * @param g      the graphics context
	 * @param h      the screen height
	 * @param enemy  the enemy to draw
	 * @param x      the x position
	 * @param y      the y position
	 * @param enemyW the card width
	 * @param enemyH the card height
	 */
	private void drawSingleEnemy(Graphics2D g, int h, EnemyBase enemy, int x, int y, int enemyW, int enemyH) {
		enemyHitboxes.add(new Rectangle(x, y, enemyW, enemyH));

		drawEnemyCard(g, enemy, x, y, enemyW, enemyH);
		drawEnemyImage(g, enemy, x, y, enemyW, enemyH);
		drawEnemyInfo(g, h, enemy, x, y, enemyW, enemyH);
	}

	/**
	 * Draws the enemy card background and selection border.
	 *
	 * @param g      the graphics context
	 * @param enemy  the enemy
	 * @param x      the x position
	 * @param y      the y position
	 * @param enemyW the card width
	 * @param enemyH the card height
	 */
	private void drawEnemyCard(Graphics2D g, EnemyBase enemy, int x, int y, int enemyW, int enemyH) {
		g.setColor(new Color(230, 230, 230, 220));
		g.fillRoundRect(x, y, enemyW, enemyH, 12, 12);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x, y, enemyW, enemyH, 12, 12);

		if (enemy == selectedTarget && enemy.isAlive()) {
			g.setColor(Color.YELLOW);
			g.setStroke(new BasicStroke(4));
			g.drawRoundRect(x - 2, y - 2, enemyW + 4, enemyH + 4, 14, 14);
			g.setStroke(new BasicStroke(1));
		}
	}

	/**
	 * Draws the enemy image.
	 *
	 * @param g      the graphics context
	 * @param enemy  the enemy
	 * @param x      the x position
	 * @param y      the y position
	 * @param enemyW the card width
	 * @param enemyH the card height
	 */
	private void drawEnemyImage(Graphics2D g, EnemyBase enemy, int x, int y, int enemyW, int enemyH) {
		int padding = Math.max(8, enemyW / 30);
		int imgSize = Math.min(enemyH - padding * 2, 70);

		BufferedImage enemyImg = getEnemyImage(enemy);
		if (enemyImg != null) {
			g.drawImage(enemyImg, x + padding, y + padding, imgSize, imgSize, null);
		} else {
			g.setColor(new Color(200, 50, 50));
			g.fillRect(x + padding, y + padding, imgSize, imgSize);
		}
	}

	/**
	 * Retrieves the image for a specific enemy.
	 *
	 * @param enemy the enemy
	 * @return the enemy image, or null if not found
	 */
	private BufferedImage getEnemyImage(EnemyBase enemy) {
		String fileName;

		switch (enemy.name().toLowerCase()) {
		case "rat-loup" -> fileName = "rat_loup.png";
		case "petit rat-loup" -> fileName = "petit_rat_loup.png";
		case "sorcier grenouille" -> fileName = "sorcier_grenouille.png";
		case "ombre vivante" -> fileName = "ombre_vivante.png";
		case "reine abeille" -> fileName = "reine_abeille.png";
		default -> fileName = null;
		}

		if (fileName == null)
			return null;

		try {
			return new ImageLoader("image", fileName).getImage();
		} catch (RuntimeException e) {
			System.err.println("Failed to load enemy image: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Draws the enemy information section.
	 *
	 * @param g      the graphics context
	 * @param h      the screen height
	 * @param enemy  the enemy
	 * @param x      the x position
	 * @param y      the y position
	 * @param enemyW the card width
	 * @param enemyH the card height
	 */
	private void drawEnemyInfo(Graphics2D g, int h, EnemyBase enemy, int x, int y, int enemyW, int enemyH) {
		int padding = Math.max(8, enemyW / 30);
		int imgSize = Math.min(enemyH - padding * 2, 70);
		int infoX = x + padding + imgSize + padding;
		int infoY = y + padding;

		int lineH = Math.max(15, enemyH / 7);
		int currentY = infoY + lineH;

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, Math.max(12, h / 55)));
		g.drawString(enemy.name(), infoX, currentY);
		currentY += lineH;

		g.setFont(new Font("Arial", Font.PLAIN, Math.max(11, h / 60)));
		g.drawString("HP: " + enemy.hp() + "/" + enemy.maxhp(), infoX, currentY);
		currentY += lineH;

		if (enemy.shieldpoint() > 0) {
			g.setColor(new Color(50, 100, 180));
			g.drawString("Shield: " + enemy.shieldpoint(), infoX, currentY);
			g.setColor(Color.BLACK);
			currentY += lineH;
		}

		currentY = drawEnemyEffects(g, enemy, infoX, currentY, lineH);
		drawEnemyIntentions(g, enemy, infoX, currentY, lineH);
	}

	/**
	 * Draws the effects affecting an enemy.
	 *
	 * @param g     the graphics context
	 * @param enemy the enemy
	 * @param x     the x position
	 * @param y     the y position
	 * @param lineH the line height
	 * @return the updated y position after drawing effects
	 */
	private int drawEnemyEffects(Graphics2D g, EnemyBase enemy, int x, int y, int lineH) {
		var effects = enemy.effects().allEffects();
		if (effects.isEmpty())
			return y;

		g.setFont(new Font("Arial", Font.BOLD, Math.max(9, lineH / 2)));
		int offsetX = 0, maxW = 140, currentY = y;

		for (var e : effects) {
			String txt = e.effect().getName() + " x" + e.acc();
			int txtW = g.getFontMetrics().stringWidth(txt) + 6;
			if (offsetX + txtW > maxW && offsetX > 0) {
				offsetX = 0;
				currentY += 14;
			}
			g.setColor(e.effect().isPositive() ? new Color(30, 120, 30) : new Color(150, 30, 30));
			g.fillRoundRect(x + offsetX - 1, currentY - 9, txtW, 12, 3, 3);
			g.setColor(Color.WHITE);
			g.drawString(txt, x + offsetX + 2, currentY);
			offsetX += txtW + 2;
		}
		return currentY + lineH;
	}

	/**
	 * Draws the enemy's intentions.
	 *
	 * @param g     the graphics context
	 * @param enemy the enemy
	 * @param x     the x position
	 * @param y     the y position
	 * @param lineH the line height
	 */
	private void drawEnemyIntentions(Graphics2D g, EnemyBase enemy, int x, int y, int lineH) {
		List<ActionEnnemi> intentions = enemy.getIntentions();
		if (intentions == null || intentions.isEmpty()) {
			g.setColor(Color.GRAY);
			g.setFont(new Font("Arial", Font.ITALIC, Math.max(10, lineH / 2)));
			g.drawString("Action: ?", x, y);
			return;
		}

		g.setFont(new Font("Arial", Font.BOLD, Math.max(10, lineH / 2)));
		int currentY = y;
		for (ActionEnnemi intent : intentions) {
			drawSingleIntention(g, intent, x, currentY);
			currentY += lineH - 2;
		}
	}

	/**
	 * Draws a single enemy intention.
	 *
	 * @param g      the graphics context
	 * @param intent the intention to draw
	 * @param x      the x position
	 * @param y      the y position
	 */
	private void drawSingleIntention(Graphics2D g, ActionEnnemi intent, int x, int y) {
		String icon, text;
		Color color;

		switch (intent.type()) {
		case ATTACK -> {
			icon = "ATK";
			text = "Attack: " + intent.valeur();
			color = new Color(180, 50, 50);
		}
		case DEFENSE -> {
			icon = "DEF";
			text = "Defense: " + intent.valeur();
			color = new Color(50, 100, 180);
		}
		case CURSE -> {
			icon = "CRS";
			text = "Curse!";
			color = new Color(150, 0, 150);
		}
		case EFFECT -> {
			icon = "EFF";
			text = (intent.effect() != null) ? intent.effect().getName() + " x" + intent.effectAcc()
					: "Effect x" + intent.valeur();
			color = new Color(200, 100, 50);
		}
		default -> {
			icon = "?";
			text = "Unknown";
			color = Color.GRAY;
		}
		}

		g.setColor(color);
		g.drawString(icon + " " + text, x, y);
	}

	/**
	 * Draws the action buttons.
	 *
	 * @param g the graphics context
	 * @param h the screen height
	 */
	private void drawButtons(Graphics2D g, int h) {
		for (Button btn : buttons) {
			btn.draw(g);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.BOLD, Math.max(16, h / 40)));
			String label = "END TURN";
			int labelW = g.getFontMetrics().stringWidth(label);
			g.drawString(label, btn.getX() + (btn.getWidth() - labelW) / 2,
					btn.getY() + (btn.getHeight() + g.getFontMetrics().getAscent()) / 2 - 2);
		}
	}

	/**
	 * Draws the instruction text.
	 *
	 * @param g          the graphics context
	 * @param h          the screen height
	 * @param leftMargin the left margin
	 */
	private void drawInstructions(Graphics2D g, int h, int leftMargin) {
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.setColor(Color.BLACK);
		g.drawString("Click item: use | Click enemy: target", leftMargin, h - Math.max(15, h / 40));
	}

	/**
	 * Handles mouse click events.
	 *
	 * @param x       the x coordinate of the click
	 * @param y       the y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		if (myScreenIndex < 0)
			myScreenIndex = manager.getCurrentScreenIndex();
		cachedManager = manager;

		if (!buttons.isEmpty() && buttons.get(0).contains(x, y)) {
			endTurn(manager);
			return;
		}

		List<EnemyBase> enemies = combat.enemies();
		for (int i = 0; i < enemyHitboxes.size() && i < enemies.size(); i++) {
			if (enemyHitboxes.get(i).contains(x, y)) {
				EnemyBase e = enemies.get(i);
				if (e.isAlive())
					selectedTarget = (selectedTarget == e) ? null : e;
				return;
			}
		}

		Item clickedItem = backpackView.itemAtPixel(x, y);
		if (clickedItem != null)
			useItem(clickedItem, manager);
	}

	/**
	 * Handles mouse move events.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	@Override
	public void handleMouseMove(int x, int y) {
	}

	/**
	 * Handles mouse release events.
	 *
	 * @param x       the x coordinate
	 * @param y       the y coordinate
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);
	}

	/**
	 * Handles rotation events.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	@Override
	public void handleRotate(int x, int y) {
	}

	/**
	 * Handles right-click events.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	@Override
	public void handleRightClick(int x, int y) {
	}

	/**
	 * Handles key press events.
	 *
	 * @param key     the key pressed
	 * @param x       the x coordinate
	 * @param y       the y coordinate
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleKeyPress(char key, int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager);

		if (key == 'i' || key == 'I') {
			Item selected = backpackView.getSelectedItem(x, y);
			if (selected != null) {
				int currentIndex = manager.getCurrentScreenIndex();
				manager.addScreen(new ItemInfoScreen(selected, buttonImage, currentIndex));
				manager.goToScreen(manager.getScreenCount() - 1);
			}
		}
	}

	/**
	 * Resolves the target for an attack. If no target is selected or the selected
	 * target is dead, selects the first alive enemy.
	 *
	 * @return the selected target, or null if no enemies are alive
	 */
	private EnemyBase resolveTarget() {
		if (selectedTarget != null && selectedTarget.isAlive()) {
			return selectedTarget;
		}
		selectedTarget = combat.getFirstAliveEnemy();
		return selectedTarget;
	}

	/**
	 * Uses an item from the backpack. The actual combat logic is handled by the
	 * Combat class.
	 *
	 * @param item    the item to use
	 * @param manager the screen manager
	 */
	private void useItem(Item item, ScreenManager manager) {
		EnemyBase target = resolveTarget();

		if (combat.tryUseItem(item, target)) {
			if (target != null && !target.isAlive()) {
				onEnemyKilled(target);
			}
			checkCombatEnd(manager);
		}
	}

	/**
	 * Called when an enemy is killed.
	 *
	 * @param target the killed enemy
	 */
	private void onEnemyKilled(EnemyBase target) {
		combat.remove(target);
		if (selectedTarget == target)
			selectedTarget = null;
	}

	/**
	 * Ends the player's turn and processes enemy actions.
	 *
	 * @param manager the screen manager
	 */
	private void endTurn(ScreenManager manager) {
		if (myScreenIndex < 0)
			myScreenIndex = manager.getCurrentScreenIndex();
		cachedManager = manager;

		if (!combat.itsEnd()) {
			combat.endPlayerTurn();
			pendingCurses.addAll(combat.enemiesTurn());
			checkEnemiesKilledByEffects();
		}

		if (!pendingCurses.isEmpty()) {
			processNextCurse(manager);
			return;
		}
		checkCombatEnd(manager);
	}

	/**
	 * Checks if any enemies were killed by effects like poison or burn. The cleanup
	 * is handled by the Combat class.
	 */
	private void checkEnemiesKilledByEffects() {
		List<EnemyBase> dead = new ArrayList<>();
		for (EnemyBase e : combat.enemies()) {
			if (!e.isAlive()) {
				dead.add(e);
				if (selectedTarget == e) {
					selectedTarget = null;
				}
			}
		}

		combat.cleanupDeadEnemies();
	}

	/**
	 * Processes the next pending curse.
	 *
	 * @param manager the screen manager
	 */
	private void processNextCurse(ScreenManager manager) {
		if (pendingCurses.isEmpty()) {
			checkCombatEnd(manager);
			return;
		}
		waitingForCurseScreen = true;
		CurseScreen cs = new CurseScreen(combat.hero(), pendingCurses.poll(), buttonImage, myScreenIndex);
		manager.addScreen(cs);
		manager.goToScreen(manager.getScreenCount() - 1);
	}

	/**
	 * Checks if the combat has ended and transitions to appropriate screen.
	 *
	 * @param manager the screen manager
	 */
	private void checkCombatEnd(ScreenManager manager) {
		Hero hero = combat.hero();

		if (hero.hp() <= 0) {
			manager.addScreen(new GameOverScreen(hero, background, manager.getHallOfFame(), false));
			manager.goToScreen(manager.getScreenCount() - 1);
			return;
		}

		if (combat.enemies().isEmpty() || combat.enemies().stream().noneMatch(EnemyBase::isAlive)) {
			CombatReward reward = gameData.applyCombatVictory(combat);
			manager.addScreen(new RewardScreen(gameData, reward.loot(), buttonImage));
			manager.goToScreen(manager.getScreenCount() - 1);
		}
	}

}