package gui.table;

import javafx.scene.Node;

public abstract class Column<A> {
	private final String name;
	private Double minWidth, prefWidth, maxWidth = null;
	protected ValidationHandler validationHandler;

	public Column(String name) {
		assert name != null;

		this.name = name;
	}

	/**
	 * @param item
	 * @return the node to add to the table column in the row with the item
	 *         passed in
	 */
	public abstract Node getNode(A item);

	public abstract boolean isValid();

	public String getName() {
		return name;
	}

	public Double getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(Double minWidth) {
		this.minWidth = minWidth;
	}

	public Double getPrefWidth() {
		return prefWidth;
	}

	public void setPrefWidth(Double prefWidth) {
		this.prefWidth = prefWidth;
	}

	public Double getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Double maxWidth) {
		this.maxWidth = maxWidth;
	}
}