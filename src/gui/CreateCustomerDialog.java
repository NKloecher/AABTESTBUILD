package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Customer;
import service.Service;

public class CreateCustomerDialog extends Stage {

	public CreateCustomerDialog(Window owner) {
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		initOwner(owner);
		setTitle("Tilføj Kunde");
		GridPane pane = new GridPane();
		initContent(pane);

		Scene scene = new Scene(pane);
		setScene(scene);

	}

	private final TextField txfName = new TextField();
	private final TextField txfPhone = new TextField();
	private final TextField txfEmail = new TextField();
	private final TextField txfAddress = new TextField();
	private final Button btnCancel = new Button("Fortryd");
	private final Button btnOk = new Button("Opret Kunde");
	private final Controller controller = new Controller();
	private final Service service = Service.getInstance();
	private final Label lblError = new Label();

	private void initContent(GridPane pane) {
		pane.setVgap(10);
		pane.setHgap(10);
		pane.setAlignment(Pos.TOP_CENTER);

		HBox hbox = new HBox();
		txfName.setPromptText("Navn");
		txfPhone.setPromptText("Telefon");
		txfEmail.setPromptText("Email");
		txfAddress.setPromptText("Adresse");
		hbox.getChildren().addAll(txfName, txfPhone, txfEmail, txfAddress);
		pane.add(hbox, 0, 0, 2, 1);

		HBox btnBox = new HBox();
		btnBox.getChildren().addAll(btnCancel, btnOk);
		btnBox.setAlignment(Pos.BASELINE_RIGHT);
		btnBox.setSpacing(15);
		pane.add(btnBox, 1, 1);

		btnCancel.setOnAction(e -> CreateCustomerDialog.this.close());
		btnOk.setOnAction(e -> controller.createCustomerAction());
		btnOk.setDefaultButton(true);

		pane.add(lblError, 0, 1);

	}

	public Customer getNewCustomer() {
		return controller.c;
	}

	private class Controller {
		Customer c = null;
		final CustomerEvaluator ce = new CustomerEvaluator();

		public void createCustomerAction() {
			String name = txfName.getText().trim();
			String phone = txfPhone.getText().trim();
			String email = txfEmail.getText().trim();
			String address = txfAddress.getText().trim();
			if (ce.isValid(name, phone, email)
					&& (!phone.isEmpty() || !email.isEmpty())) {
				c = service.createCustomer(name, address, phone, email);
				close();
			} else if (!ce.nameIsValid(name)) {
				lblError.setText("Navn skal skrives");
			} else if (!ce.phoneIsValid(phone)) {
				lblError.setText("Ugyldigt telefonnummer");
			} else if (!ce.emailIsValid(email)) {
				lblError.setText("Email er ugyldig");
			} else if (phone.isEmpty() && email.isEmpty()) {
				lblError.setText("Skal have mindst én kontaktoplysning");
			}

		}
	}
}
