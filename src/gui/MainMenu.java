package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Permission;
import service.Service;

public class MainMenu extends GridPane {
	private Handler<Pane> selectHandler;
	private final Stage owner;
	private final Controller controller = new Controller();

	public MainMenu(Stage owner, gui.MainApp.Controller mainAppController) {
		this.owner = owner;

		setHgap(10);
		setVgap(10);
		setAlignment(Pos.TOP_CENTER);

		Button sale = getBigButton("Lav salg");
		sale.setOnAction(e -> controller.selectSale());
		add(sale, 0, 0);

		Button statistics = getBigButton("Statistik");
		statistics.setOnAction(e -> controller.select(new Statistics()));
		add(statistics, 0, 1);

		Button rentals = getBigButton("Udlejninger");
		rentals.setOnAction(e -> controller.selectRentals());
		add(rentals, 0, 2);

		Button tours = getBigButton("Rundvisninger");
		tours.setOnAction(e -> controller.select(new Tours(owner)));
		add(tours, 0, 3);

		Button products = getBigButton("Produkter");
		products.setOnAction(e -> controller.select(new Products()));
		add(products, 0, 4);

		Button pricelists = getBigButton("Prislister");
		add(pricelists, 0, 5);
		pricelists.setOnAction(e -> controller.select(new Pricelists(mainAppController)));

		Button users = getBigButton("Brugere");
		users.setOnAction(e -> controller.select(new Users()));
		add(users, 0, 6);

		Button customers = getBigButton("Kunder");
		add(customers, 0, 7);
		customers.setOnAction(e -> controller.select(new Customers(owner)));

		Service service = Service.getInstance();
		if (service.getActiveUser().getPermission() != Permission.ADMIN) {
			products.setDisable(true);
			pricelists.setDisable(true);
			users.setDisable(true);
			customers.setDisable(true);
		}
	}

	public void setOnSelect(Handler<Pane> handler) {
		selectHandler = handler;
	}

	private Button getBigButton(String text) {
		Button b = new Button(text);

		b.setStyle("-fx-font-size: 24px");
		b.setPrefWidth(300);
		b.setPrefHeight(170);

		return b;
	}

	private class Controller {
		public void selectRentals() {
			select(new Rentals(owner, x -> selectRentals()));
		}

		public void selectSale() {
			select(new Sale(owner, x -> selectSale()));
		}

		public void select(GridPane pane) {
			if (selectHandler == null) {
				return;
			}

			selectHandler.exec(pane);
		}
	}
}
