package gui.table;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class LabelColumn<A> extends Column<A> {
	private final Getter<A, String> getter;
	private final List<Label> cells = new ArrayList<>();

	public LabelColumn(String name, Getter<A, String> getter) {
		super(name);

		this.getter = getter;
	}

	public void updateCell(A item) {
		for (int i = cells.size() - 1; i >= 0; i--)
			if (!cells.get(i).isVisible())
				cells.remove(i); // !isVisible() == removed from table

		for (Label l : cells) {
			if (l.getUserData().equals(item)) {
				setText(l, item);
				return;
			}
		}
	}

	@Override
	public Node getNode(A item) {
		final Label label = new Label();
		label.setUserData(item);
		setText(label, item);
		cells.add(label);
		return label;
	}

	private void setText(Label label, A item) {
		final String value = getter.get(item);

		if (value != null) {
			label.setText(value);
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
