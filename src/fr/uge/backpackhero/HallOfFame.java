package fr.uge.backpackhero;

import module java.base;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages a hall of fame leaderboard that maintains the top scores for the
 * game. Handles adding new scores with automatic ranking, persistent storage of
 * scores to a file, maintaining only the top 3 scores, and timestamping each
 * score entry.
 */
public class HallOfFame {

	private static final Path SAVE_PATH = Path.of("halloffame.txt");
	private static final int MAX_ENTRIES = 3;
	private final List<ScoreEntry> topScores;

	/**
	 * Constructs a new HallOfFame instance and loads existing scores from the save
	 * file. If the save file doesn't exist, starts with an empty leaderboard.
	 * 
	 * @throws IOException if an I/O error occurs while reading the save file
	 */
	public HallOfFame() throws IOException {
		this.topScores = new ArrayList<>();
		loadFromFile();
	}

	/**
	 * Private constructor used to create a HallOfFame with a predefined list of
	 * scores.
	 * 
	 * @param topScores the list of score entries to initialize with
	 * @throws NullPointerException if topScores is null
	 */
	private HallOfFame(List<ScoreEntry> topScores) {
		this.topScores = Objects.requireNonNull(topScores, "topScores cannot be null");
	}

	/**
	 * Creates an empty HallOfFame instance with no scores. Useful for testing or
	 * initialization without file I/O.
	 * 
	 * @return a new empty HallOfFame instance
	 */
	public static HallOfFame empty() {
		return new HallOfFame(new ArrayList<>());
	}

	/**
	 * Adds a new score to the hall of fame. The score is automatically sorted and
	 * the list is trimmed to keep only the top entries.
	 * 
	 * @param playerName the name of the player who achieved this score
	 * @param score      the score value achieved
	 * @return true if the score made it into the top entries, false otherwise
	 * @throws NullPointerException     if playerName is null
	 * @throws IllegalArgumentException if score is negative
	 * @throws IOException              if an I/O error occurs while saving to file
	 */
	public boolean addScore(String playerName, int score) throws IOException {
		Objects.requireNonNull(playerName, "playerName cannot be null");
		if (score < 0) {
			throw new IllegalArgumentException("score < 0");
		}

		String date = getCurrentDate();
		ScoreEntry entry = new ScoreEntry(playerName, score, date);

		topScores.add(entry);
		Collections.sort(topScores);

		boolean isTopScore = topScores.indexOf(entry) < MAX_ENTRIES;

		if (topScores.size() > MAX_ENTRIES) {
			topScores.subList(MAX_ENTRIES, topScores.size()).clear();
		}

		saveToFile();
		return isTopScore;
	}

	/**
	 * Returns a copy of the current top scores list.
	 * 
	 * @return a new list containing all top score entries
	 */
	public List<ScoreEntry> getTopScores() {
		return new ArrayList<>(topScores);
	}

	/**
	 * Gets the current date and time formatted as "dd/MM/yyyy HH:mm".
	 * 
	 * @return the formatted current date and time
	 */
	private String getCurrentDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
		return LocalDateTime.now().format(formatter);
	}

	/**
	 * Saves all current top scores to the save file. Each line contains one entry
	 * in the format: playerName|score|date
	 * 
	 * @throws IOException if an I/O error occurs while writing to file
	 */
	private void saveToFile() throws IOException {
		List<String> lines = new ArrayList<>();
		for (ScoreEntry entry : topScores) {
			lines.add(entry.playerName() + "|" + entry.score() + "|" + entry.date());
		}
		Files.write(SAVE_PATH, lines);
	}

	/**
	 * Loads scores from the save file if it exists. Invalid entries (malformed
	 * lines or negative scores) are silently ignored.
	 * 
	 * @throws IOException if an I/O error occurs while reading the file
	 */
	private void loadFromFile() throws IOException {
		if (!Files.exists(SAVE_PATH)) {
			return;
		}

		for (String line : Files.readAllLines(SAVE_PATH)) {
			String[] parts = line.split("\\|");
			if (parts.length != 3) {
				continue;
			}

			try {
				String name = parts[0];
				int score = Integer.parseInt(parts[1]);
				String date = parts[2];

				if (score >= 0) {
					topScores.add(new ScoreEntry(name, score, date));
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}
}