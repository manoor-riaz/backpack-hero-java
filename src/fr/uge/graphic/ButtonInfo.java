package fr.uge.graphic;

import java.awt.Color;
import java.util.Objects;

/**
 * A data class holding button display information including label, description,
 * price string, and label color. All fields are immutable and non-null.
 */
public class ButtonInfo {
	private final String label;
	private final String description;
	private final String priceStr;
	private final Color labelColor;

	/**
	 * Constructs a ButtonInfo with the specified display properties.
	 * Package-private constructor for use within the graphic package.
	 * 
	 * @param label       the text label for the button
	 * @param description the description text for the button
	 * @param priceStr    the price string to display
	 * @param labelColor  the color to use for the label
	 * @throws NullPointerException if any parameter is null
	 */
	ButtonInfo(String label, String description, String priceStr, Color labelColor) {
		this.label = Objects.requireNonNull(label);
		this.description = Objects.requireNonNull(description);
		this.priceStr = Objects.requireNonNull(priceStr);
		this.labelColor = Objects.requireNonNull(labelColor);
	}

	/**
	 * Returns the button's text label.
	 * 
	 * @return the label string
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns the button's description text.
	 * 
	 * @return the description string
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the button's price string.
	 * 
	 * @return the price string
	 */
	public String getPriceStr() {
		return priceStr;
	}

	/**
	 * Returns the color to use for the button's label.
	 * 
	 * @return the label color
	 */
	public Color getLabelColor() {
		return labelColor;
	}

}