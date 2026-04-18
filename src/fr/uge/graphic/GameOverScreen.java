package fr.uge.graphic;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import fr.uge.backpackhero.*;

/**
 * Represents the game over screen displayed at the end of a game session. This
 * screen shows the final score, score breakdown, hall of fame, and a quit
 * button. It supports both victory and defeat scenarios with different visual
 * presentations.
 */
public class GameOverScreen implements Screen {
	private final Hero hero;
	private final BufferedImage background;
	private final int finalScore;
	private final HallOfFame hallOfFame;
	private final boolean isVictory;
	private Button quitButton;

	/**
	 * Constructs a new GameOverScreen. Automatically calculates the final score and
	 * registers it in the hall of fame.
	 *
	 * @param hero       the hero whose game session has ended
	 * @param background the background image to display (can be null)
	 * @param hallOfFame the hall of fame to record the score
	 * @param isVictory  true if the game ended in victory, false otherwise
	 * @throws NullPointerException if hero or hallOfFame is null
	 */
	public GameOverScreen(Hero hero, BufferedImage background, HallOfFame hallOfFame, boolean isVictory) {
		this.hero = Objects.requireNonNull(hero, "hero cannot be null");
		this.background = background;
		this.hallOfFame = Objects.requireNonNull(hallOfFame, "hallOfFame cannot be null");
		this.isVictory = isVictory;
		this.finalScore = ScoreCalculator.calculateScore(hero);

		try {
			hallOfFame.addScore(hero.name(), finalScore);
		} catch (IOException e) {
			System.err.println("Impossible d'enregistrer le score : " + e.getMessage());
		}
	}

	/**
	 * Draws the complete game over screen including background, title, score
	 * details, hall of fame, and quit button.
	 *
	 * @param g      the graphics context to draw on
	 * @param width  the width of the drawing area
	 * @param height the height of the drawing area
	 * @throws NullPointerException     if g is null
	 * @throws IllegalArgumentException if width or height is negative
	 */
	@Override
	public void draw(Graphics2D g, int width, int height) {
		Objects.requireNonNull(g, "graphics cannot be null");
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		if (height < 0) {
			throw new IllegalArgumentException("height cannot be negative");
		}

		drawBackground(g, width, height);
		drawTitle(g, width, height);
		drawScoreDetails(g, width, height);
		drawHallOfFame(g, width, height);
		drawQuitButton(g, width, height);
	}

	/**
	 * Draws the background of the game over screen. If a background image is
	 * provided, it is drawn with a semi-transparent overlay. Otherwise, a black
	 * background is drawn.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawBackground(Graphics2D g, int w, int h) {
		if (background != null) {
			g.drawImage(background, 0, 0, w, h, null);
			g.setColor(new Color(0, 0, 0, 180));
			g.fillRect(0, 0, w, h);
			return;
		}
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, w, h);
	}

	/**
	 * Draws the title of the game over screen. The title differs based on whether
	 * the game ended in victory or defeat.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawTitle(Graphics2D g, int w, int h) {
		String title = isVictory ? "VICTORY!" : "GAME OVER";
		Color titleColor = isVictory ? new Color(255, 215, 0) : Color.RED;

		g.setColor(titleColor);
		g.setFont(new Font("Arial", Font.BOLD, Math.max(40, h / 15)));
		int titleW = g.getFontMetrics().stringWidth(title);
		g.drawString(title, (w - titleW) / 2, Math.max(80, h / 8));
	}

	/**
	 * Draws the score details section including the final score and breakdown.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawScoreDetails(Graphics2D g, int w, int h) {
		int startY = Math.max(150, h / 5);

		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, Math.max(24, h / 25)));
		drawCenteredText(g, "FINAL SCORE: " + finalScore, w, startY);

		drawScoreBreakdown(g, w, startY + Math.max(50, h / 15));
	}

	/**
	 * Draws the detailed breakdown of the score.
	 *
	 * @param g      the graphics context
	 * @param w      the width of the screen
	 * @param startY the starting Y coordinate for the breakdown
	 */
	private void drawScoreBreakdown(Graphics2D g, int w, int startY) {
		g.setFont(new Font("Arial", Font.PLAIN, Math.max(16, w / 40)));
		String[] lines = ScoreCalculator.getScoreBreakdown(hero).split("\n");

		int y = startY;
		for (String line : lines) {
			drawCenteredText(g, line, w, y);
			y += Math.max(25, w / 30);
		}
	}

	/**
	 * Draws the hall of fame section including the title and top scores.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawHallOfFame(Graphics2D g, int w, int h) {
		int startY = h / 2 + Math.max(50, h / 15);

		g.setColor(new Color(255, 215, 0));
		g.setFont(new Font("Arial", Font.BOLD, Math.max(22, h / 28)));
		drawCenteredText(g, "=== HALL OF FAME ===", w, startY);

		drawTopScores(g, w, startY + Math.max(40, h / 20));
	}

	/**
	 * Draws the list of top scores. If no scores are recorded, displays a message
	 * indicating this.
	 *
	 * @param g      the graphics context
	 * @param w      the width of the screen
	 * @param startY the starting Y coordinate for the scores list
	 */
	private void drawTopScores(Graphics2D g, int w, int startY) {
		List<ScoreEntry> topScores = hallOfFame.getTopScores();

		if (topScores.isEmpty()) {
			drawEmptyScoresMessage(g, w, startY);
			return;
		}

		drawScoreEntries(g, topScores, w, startY);
	}

	/**
	 * Draws a message indicating that no scores have been recorded yet.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param y the Y coordinate for the message
	 */
	private void drawEmptyScoresMessage(Graphics2D g, int w, int y) {
		g.setColor(Color.GRAY);
		g.setFont(new Font("Arial", Font.ITALIC, Math.max(16, w / 40)));
		drawCenteredText(g, "No scores recorded", w, y);
	}

	/**
	 * Draws all score entries in the hall of fame list. Highlights the current
	 * player's score in green.
	 *
	 * @param g      the graphics context
	 * @param scores the list of score entries to display
	 * @param w      the width of the screen
	 * @param y      the starting Y coordinate for the entries
	 */
	private void drawScoreEntries(Graphics2D g, List<ScoreEntry> scores, int w, int y) {
		g.setFont(new Font("Arial", Font.PLAIN, Math.max(16, w / 40)));

		int currentY = y;
		for (int i = 0; i < scores.size(); i++) {
			ScoreEntry entry = scores.get(i);
			boolean isCurrentScore = isCurrentPlayerScore(entry);

			g.setColor(isCurrentScore ? new Color(50, 255, 50) : Color.WHITE);
			String text = formatScoreEntry(i + 1, entry);
			drawCenteredText(g, text, w, currentY);
			currentY += Math.max(30, w / 25);
		}
	}

	/**
	 * Checks if a score entry belongs to the current player.
	 *
	 * @param entry the score entry to check
	 * @return true if the entry matches the current player's name and final score
	 */
	private boolean isCurrentPlayerScore(ScoreEntry entry) {
		return entry.playerName().equals(hero.name()) && entry.score() == finalScore;
	}

	/**
	 * Formats a score entry for display.
	 *
	 * @param rank  the rank of the score (1-based)
	 * @param entry the score entry to format
	 * @return the formatted string
	 */
	private String formatScoreEntry(int rank, ScoreEntry entry) {
		return String.format("%d. %s", rank, entry.toString());
	}

	/**
	 * Draws the quit button at the bottom of the screen. Creates the button if it
	 * doesn't exist yet.
	 *
	 * @param g the graphics context
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void drawQuitButton(Graphics2D g, int w, int h) {
		if (quitButton == null) {
			createQuitButton(w, h);
		}

		quitButton.draw(g);
		drawButtonLabel(g);
	}

	/**
	 * Creates the quit button with appropriate dimensions based on screen size.
	 *
	 * @param w the width of the screen
	 * @param h the height of the screen
	 */
	private void createQuitButton(int w, int h) {
		int btnW = Math.max(200, w / 5);
		int btnH = Math.max(50, h / 12);
		int btnX = (w - btnW) / 2;
		int btnY = h - Math.max(100, h / 8);
		quitButton = new Button(btnX, btnY, btnW, btnH, null);
	}

	/**
	 * Draws the label on the quit button.
	 *
	 * @param g the graphics context
	 */
	private void drawButtonLabel(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 18));
		String label = "QUIT";

		int textW = g.getFontMetrics().stringWidth(label);
		int textX = quitButton.getX() + (quitButton.getWidth() - textW) / 2;
		int textY = calculateButtonTextY(g);
		g.drawString(label, textX, textY);
	}

	/**
	 * Calculates the Y coordinate for the button label text to center it
	 * vertically.
	 *
	 * @param g the graphics context
	 * @return the Y coordinate for the text
	 */
	private int calculateButtonTextY(Graphics2D g) {
		return quitButton.getY() + (quitButton.getHeight() + g.getFontMetrics().getAscent()) / 2;
	}

	/**
	 * Draws text centered horizontally at the specified Y coordinate.
	 *
	 * @param g    the graphics context
	 * @param text the text to draw
	 * @param w    the width of the screen
	 * @param y    the Y coordinate
	 */
	private void drawCenteredText(Graphics2D g, String text, int w, int y) {
		int textW = g.getFontMetrics().stringWidth(text);
		g.drawString(text, (w - textW) / 2, y);
	}

	/**
	 * Handles mouse click events on the screen. If the quit button is clicked,
	 * exits the application.
	 *
	 * @param x       the X coordinate of the click
	 * @param y       the Y coordinate of the click
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleClick(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");

		if (quitButton != null && quitButton.contains(x, y)) {
			System.exit(0);
		}
	}

	/**
	 * Handles mouse move events. This screen does not respond to mouse movements.
	 *
	 * @param x the X coordinate of the mouse
	 * @param y the Y coordinate of the mouse
	 */
	@Override
	public void handleMouseMove(int x, int y) {
	}

	/**
	 * Handles mouse release events. This screen does not respond to mouse releases.
	 *
	 * @param x       the X coordinate of the release
	 * @param y       the Y coordinate of the release
	 * @param manager the screen manager
	 * @throws NullPointerException if manager is null
	 */
	@Override
	public void handleMouseRelease(int x, int y, ScreenManager manager) {
		Objects.requireNonNull(manager, "manager cannot be null");
	}

	/**
	 * Handles item rotation events. This screen does not support item rotation.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	@Override
	public void handleRotate(int x, int y) {
	}

	/**
	 * Handles right-click events. This screen does not respond to right-clicks.
	 *
	 * @param x the X coordinate of the click
	 * @param y the Y coordinate of the click
	 */
	@Override
	public void handleRightClick(int x, int y) {
	}
}