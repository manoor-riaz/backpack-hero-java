package fr.uge.graphic;

import java.awt.Color;
import java.io.IOException;
import java.util.Objects;

import com.github.forax.zen.Application;
import com.github.forax.zen.KeyboardEvent;
import com.github.forax.zen.PointerEvent;

import fr.uge.backpackhero.GameData;
import fr.uge.backpackhero.Floor;
import fr.uge.backpackhero.HallOfFame;

/**
 * Main entry point for the Backpack Hero game application.
 */

public class Main {

	
	/** Default constructor */
	public Main() {}
	
	
	/**
	 * Entry point of the application
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		Objects.requireNonNull(args, "args cannot be null");

		ItemImageManager.loadAllItemImages();

		ImageLoader background = new ImageLoader("src/image", "background.jpg");
		ImageLoader heroImage = new ImageLoader("src/image", "hero.png");
		ImageLoader buttonImage = new ImageLoader("src/image", "button.png");
		ImageLoader healerImage = new ImageLoader("src/image", "healer.png");
		ImageLoader merchantImage = new ImageLoader("src/image", "merchant.png");

		GameData gameData = new GameData("Bosphore");
		Floor floor = gameData.dungeon().currentFloor();

		HallOfFame tmp;
		try {
			tmp = new HallOfFame();
		} catch (IOException e) {
			System.err.println("Erreur lors du chargement du Hall of Fame : " + e.getMessage());
			tmp = HallOfFame.empty();
		}
		HallOfFame hallOfFame = tmp;

		Application.run(Color.WHITE, context -> {
			var screenInfo = context.getScreenInfo();
			var width = screenInfo.width();
			var height = screenInfo.height();

			ScreenManager manager = new ScreenManager();
			manager.setScreenInfo(width, height);
			manager.setHallOfFame(hallOfFame);

			ImageScreen firstScreen = new ImageScreen(background.getImage());
			manager.addScreen(firstScreen);

			StartingGearScreen gearScreen = new StartingGearScreen(gameData, buttonImage.getImage());
			manager.addScreen(gearScreen);

			MapHeroScreen mapScreen = new MapHeroScreen(heroImage.getImage(), gameData, floor, healerImage.getImage(),
					merchantImage.getImage(), buttonImage.getImage());
			manager.addScreen(mapScreen);

			while (true) {
				var event = context.pollEvent();
				while (event != null) {
					switch (event) {
					case PointerEvent pe -> handlePointerEvent(pe, manager);
					case KeyboardEvent ke -> {
						if (handleKeyboardEvent(ke, manager)) {
							System.exit(0);
						}
					}
					default -> {
					}
					}
					event = context.pollEvent();
				}
				context.renderFrame(graphics -> manager.draw(graphics, width, height));
			}
		});
	}

	private static void handlePointerEvent(PointerEvent pe, ScreenManager manager) {
		var loc = pe.location();
		int mouseX = loc.x();
		int mouseY = loc.y();

		manager.setLastMousePosition(mouseX, mouseY);

		switch (pe.action()) {
		case POINTER_DOWN -> {
			boolean isRightClick = pe.modifiers().contains(com.github.forax.zen.EventModifier.META)
					|| pe.modifiers().contains(com.github.forax.zen.EventModifier.CTRL);
			if (isRightClick) {
				manager.handleRightClick(mouseX, mouseY);
			} else {
				manager.handleClick(mouseX, mouseY);
			}
		}
		case POINTER_MOVE -> manager.handleMouseMove(mouseX, mouseY);
		case POINTER_UP -> manager.handleMouseRelease(mouseX, mouseY);
		}
	}

	private static boolean handleKeyboardEvent(KeyboardEvent ke, ScreenManager manager) {
		if (ke.action() != KeyboardEvent.Action.KEY_PRESSED) {
			return false;
		}

		var key = ke.key();
		String keyStr = key.toString();

		if (keyStr.equals("ESCAPE") || keyStr.equals("Escape") || keyStr.equals("ESC") || keyStr.equals("Esc")) {
			return true;
		}

		if (keyStr.toUpperCase().equals("R")) {
			manager.handleRotate();
		}

		if (keyStr.toUpperCase().equals("I")) {
			manager.handleKeyPress('i');
		}

		return false;
	}
}