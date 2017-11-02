package gui.table;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;

public class PasswordColumn<A> extends Column<A> {
	private final String name;
	private final Setter<A, String> setter;

	public PasswordColumn(String name, Setter<A, String> setter) {
		super(name);
		this.setter = setter;
		this.name = name;
	}

	private void onPressPasswordField(PasswordField pf, Button button) {
		if (pf.getText().length() == 0) {
			button.setText("Annuller");
		} else {
			button.setText("Gem kode");
		}
	}

	private void onPressButton(PasswordField pf, Button button, HBox h, A owner) {
		if (h.getChildren().contains(pf)) {
			button.setText(this.name);
			h.getChildren().remove(pf);
			if (!pf.getText().isEmpty()) {
				this.setter.set(owner, pf.getText());
			}
			pf.setText("");
			button.setText("Sæt kode");
		} else {
			h.getChildren().add(0, pf);
			pf.setMaxWidth(300);
			button.setText("Annuller");
		}
	}

	@Override
	public Node getNode(A owner) {
		HBox h = new HBox();

		Button button = new Button("Sæt kode");
		PasswordField pf = new PasswordField();

		button.setText(name);
		button.setMinWidth(90);
		button.setOnAction(e -> onPressButton(pf, button, h, owner));

		pf.setPromptText("Indtast ny kode");
		pf.textProperty().addListener(e -> onPressPasswordField(pf, button));

		h.getChildren().add(button);

		return h;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
