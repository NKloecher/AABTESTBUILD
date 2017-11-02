package gui.table;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class Table<A> {
	private final GridPane pane = new GridPane();
	private List<A> items = new ArrayList<>();
	private final List<Column<A>> columns = new ArrayList<>();
	private final ValidationHandler validationHanlder;

	public Table(ValidationHandler validationHanlder) {
		this.validationHanlder = validationHanlder;

		pane.setHgap(10);
		pane.setVgap(10);
	}

	/**
	 * removes and sets all items in the table
	 */
	public void setItems(List<A> items) {
		assert items != null;

		pane.getChildren().removeAll();
		this.items = new ArrayList<>();

		for (A item : items) {
			addItem(item);
		}
	}

	/**
	 * adds an item to the table
	 * 
	 * @param item
	 *            cannot already be in the table
	 */
	public void addItem(A item) {
		assert item != null;
		assert !items.contains(item);

		this.items.add(item);

		int row = this.items.size();

		for (int col = 0; col < columns.size(); col++) {
			Column<A> column = columns.get(col);
			Node node = column.getNode(item);

			if (node != null) {
				pane.add(node, col, row);
			}
		}
	}

	/**
	 * removes an item from the table
	 * 
	 * @param item
	 *            must be it the table
	 */
	public void removeItem(A item) {
		assert item != null;
		assert items.contains(item);

		this.items.remove(item);

		pane.getChildren().remove(columns.size() - 1, pane.getChildren().size());

		setItems(new ArrayList<>(this.items));
	}

	/**
	 * returns the pain containing the table contents
	 */
	public GridPane getPane() {
		return pane;
	}

	/**
	 * adds a column to the table
	 * 
	 * @param column
	 *            cannot be null
	 */
	public void addColumn(Column<A> column) {
		assert column != null;
		assert !columns.contains(column);

		columns.add(column);

		if (validationHanlder != null) {
			column.validationHandler = (error, isValid) -> validationHanlder
					.onValidate(error, isValid());
		}

		Label columnName = new Label(column.getName());

		ColumnConstraints cc = new ColumnConstraints();
		if (column.getMinWidth() != null) {
			cc.setMinWidth(column.getMinWidth());
		}
		if (column.getPrefWidth() != null) {
			cc.setPrefWidth(column.getPrefWidth());
		}
		if (column.getMaxWidth() != null) {
			cc.setMaxWidth(column.getMaxWidth());
		}
		pane.getColumnConstraints().add(cc);
		pane.add(columnName, columns.size() - 1, 0);
	}

	public boolean isValid() {
		for (Column<A> c : columns) {
			if (!c.isValid()) {
				return false;
			}
		}

		return true;
	}

}
