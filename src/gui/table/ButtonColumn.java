package gui.table;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class ButtonColumn<A> extends Column<A> {
	private final ActionHandler<A> actionHandler;
	private Getter<A, Button> getter;

	public ButtonColumn(String name, ActionHandler<A> actionHandler) {
		super(name);

		this.actionHandler = actionHandler;
	}

	public ButtonColumn(String name, Getter<A, Button> getter,
			ActionHandler<A> actionHandler) {
		this(name, actionHandler);

		this.getter = getter;
	}

	@Override
	public Node getNode(A item) {
		final Button b;

		if (getter == null) {
			b = new Button(getName());
		} else {
			b = getter.get(item);
		}

		b.setOnAction(e -> actionHandler.exec(item));

		return b;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
