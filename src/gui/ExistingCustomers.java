package gui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Customer;
import storage.Storage;

public class ExistingCustomers extends Stage {

	public ExistingCustomers(Window owner) {
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		initOwner(owner);
		setTitle("Tilføj Kunde");
		GridPane pane = new GridPane();
		initContent(pane);

		Scene scene = new Scene(pane);
		setScene(scene);

	}

	private final ListView<Customer> customers = new ListView<>();
	private final Button btnOK = new Button("OK");
	private final Button btnCancel = new Button("Fortryd");
	private final Storage storage = Storage.getInstance();
	private final Controller controller = new Controller();

	private void initContent(GridPane pane) {
		pane.setVgap(10);
		pane.setHgap(10);
		pane.setAlignment(Pos.TOP_CENTER);

		customers.getItems().addAll(storage.getCustomers());
		pane.add(customers, 0, 0, 2, 1);

		pane.add(btnCancel, 0, 1);
		btnCancel.setOnAction(e -> close());
		pane.add(btnOK, 1, 1);
		btnOK.setOnAction(e -> controller.customer());
	}

	public Customer getCustomer() {
		return controller.c;
	}

	private class Controller {
		private Customer c;

		public void customer() {
			try {
				if (customers.getSelectionModel().getSelectedItem() != null) {
					c = customers.getSelectionModel().getSelectedItem();
					close();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
