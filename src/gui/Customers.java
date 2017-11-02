package gui;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Customer;
import model.Order;
import storage.Storage;

public class Customers extends GridPane {

	private final Controller controller = new Controller();
	private final Storage storage = Storage.getInstance();
	private final ListView<Customer> lvCustomers = new ListView<>();
	private final ListView<Order> lvOrders = new ListView<>();
	private final Stage owner;

	public Customers(Stage owner) {
		this.owner = owner;

		setVgap(10);
		setHgap(10);
		setAlignment(Pos.TOP_CENTER);

		Label lblCustomers = new Label("Kunder");
		add(lblCustomers, 0, 0);
		Label lblOrders = new Label("Ordrer");
		add(lblOrders, 1, 0);

		ScrollPane sp = new ScrollPane();
		sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setContent(lvCustomers);

		lvCustomers.setPrefSize(200, 300);
		lvCustomers.getItems().addAll(storage.getCustomers());
		lvCustomers.getItems().sort(null);
		ChangeListener<Customer> listener = (ov, oldString, newString) -> controller
				.loadOrdersAction();
		lvCustomers.getSelectionModel().selectedItemProperty().addListener(listener);

		add(lvCustomers, 0, 1);

		lvOrders.setPrefSize(200, 200);

		add(lvOrders, 1, 1);

		Button btnViewOrder = new Button("Vis Ordre");
		add(btnViewOrder, 1, 2);
		btnViewOrder.setOnAction(e -> controller.loadProductOrdersAction());

		HBox hbox = new HBox();
		hbox.setSpacing(10);
		Button btnViewCustomer = new Button("Vis Kundeinformation");
		Button btnAddCustomer = new Button("Tilføj Kunde");
		hbox.getChildren().addAll(btnAddCustomer, btnViewCustomer);
		add(hbox, 0, 2);
		btnAddCustomer.setOnAction(e -> controller.createCustomerDialogAction());
		btnViewCustomer.setOnAction(e -> controller.viewCustomerAction());

	}

	private class Controller extends Window {
		private ProductOrderDialog createProductOrderDialog;

		public void loadOrdersAction() {
			List<Order> orders = new ArrayList<>();
			for (Order o : storage.getOrders()) {
				if (o.getCustomer() != null && o.getCustomer()
						.equals(lvCustomers.getSelectionModel().getSelectedItem())) {
					orders.add(o);
				}
			}
			lvOrders.getItems().clear();
			lvOrders.getItems().addAll(orders);
		}

		public void viewCustomerAction() {
			if (lvCustomers.getSelectionModel().getSelectedItem() != null) {
				Customer c = lvCustomers.getSelectionModel().getSelectedItem();
				ViewCustomerDialog vc = new ViewCustomerDialog(c);
				vc.showAndWait();

				lvCustomers.getItems().clear();
				lvCustomers.getItems().setAll(storage.getCustomers());
				lvCustomers.getItems().sort(null);
			}
		}

		public void loadProductOrdersAction() {
			if (lvOrders.getSelectionModel().getSelectedItem() != null) {
				Order order = lvOrders.getSelectionModel().getSelectedItem();
				if (createProductOrderDialog == null) {
					createProductOrderDialog = new ProductOrderDialog(order);
					Stage stage = (Stage) lvOrders.getScene().getWindow();
					createProductOrderDialog.initOwner(stage);
				}
				createProductOrderDialog.showAndWait();
			}
		}

		public void createCustomerDialogAction() {
			CreateCustomerDialog cd = new CreateCustomerDialog(owner);
			cd.showAndWait();
			updateCustomers(cd.getNewCustomer());
		}

		public void updateCustomers(Customer c) {
			if (c != null) {
				lvCustomers.getItems().add(c);
				lvCustomers.getItems().sort(null);
			}
		}

	}
}
