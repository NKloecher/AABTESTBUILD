package gui.table;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class ListColumn<A, B> extends Column<A> {
	private B[] values;
	private Getter<A, B> getter;
	private Setter<A, B> setter;

	/**
	 * @param getter
	 *            the value returned gets selected in the list
	 * @param setter
	 *            gets called when a new value is selected in the list
	 * @param values
	 *            the values to display in the list
	 */
	public ListColumn(String name, Getter<A, B> getter, Setter<A, B> setter, B[] values) {
		super(name);

		assert getter != null;
		assert setter != null;
		assert values != null;

		this.getter = getter;
		this.setter = setter;
		this.values = values;
	}

	@Override
	public Node getNode(A owner) {
		ComboBox<B> cb = new ComboBox<>();
		B value = getter.get(owner);

		cb.getItems().setAll(values);
		cb.setValue(value);
		cb.valueProperty().addListener(
				e -> setter.set(owner, cb.getSelectionModel().getSelectedItem()));

		return cb;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
