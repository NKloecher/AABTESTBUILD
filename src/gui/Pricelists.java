package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import gui.table.ButtonColumn;
import gui.table.Column;
import gui.table.LabelColumn;
import gui.table.PrimitiveColumn;
import gui.table.Table;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.Permission;
import model.Pricelist;
import model.Product;
import service.Service;

public class Pricelists extends GridPane {

	private final Controller controller = new Controller();
	private final Service service = Service.getInstance();
	private final Pricelist selectedPricelist = service.getSelectedPricelist();
	private final TextField tfNewPrice = new TextField();
	private final ProductList productList;
	private final Label lError = new Label();
	private final Table<PricelistElement> table = new Table<>(
			(error, isValid) -> lError.setText(error));
	private final MainApp.Controller mainController;

	public Pricelists(MainApp.Controller mainAppController) {
		this.mainController = mainAppController;
		setHgap(10);
		setVgap(10);
		setAlignment(Pos.TOP_CENTER);

		LabelColumn<PricelistElement> lblC = new LabelColumn<>("Produkt",
				t -> t.getProduct().getName() + ", " + t.getProduct().getCategory());
		lblC.setPrefWidth(99999.0);

		Column<PricelistElement> pleC = new PrimitiveColumn<>("Pris",
				PrimitiveColumn.Type.Double, PricelistElement::getPrice,
				controller::updatePrice, (pe, v) -> {
					if (Pattern.matches("^\\d+(\\.\\d+)?", v)) {
						return null;
					}
					return "Pris skal være et positivt tal";
				});
		pleC.setMinWidth(60.0);

		ButtonColumn<PricelistElement> btnC = new ButtonColumn<>("Delete",
				controller::deleteProduct);
		btnC.setMinWidth(100.0);

		table.addColumn(lblC);
		table.addColumn(pleC);
		table.addColumn(btnC);
		table.getPane().setMaxWidth(700);

		List<PricelistElement> tests = new ArrayList<>();

		for (Product p : selectedPricelist.getProducts()) {
			tests.add(new PricelistElement(p, selectedPricelist.getPrice(p)));
		}

		table.setItems(tests);
		table.getPane().setPrefSize(999, 999);

		ScrollPane sp = new ScrollPane();
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setStyle("-fx-background-color:transparent;");
		add(sp, 0, 0);
		sp.setContent(table.getPane());
		sp.setPrefSize(999, 999);

		GridPane gp2 = new GridPane();
		add(gp2, 1, 0);

		Label newProductLabel = new Label("Tilføj nyt produkt:");
		GridPane.setMargin(newProductLabel, new Insets(10, 0, 10, 0));
		newProductLabel.setStyle("-fx-font-size: 16px");
		gp2.add(newProductLabel, 0, 0, 2, 1);

		productList = new ProductList(service.getProducts());
		productList.maxWidth(300);
		GridPane.setMargin(productList, new Insets(0, 30, 0, 0));
		gp2.add(productList, 1, 1, 2, 1);

		tfNewPrice.setPromptText("Pris på valgte produkter");
		TextField txfCategory = new TextField();
		txfCategory.setPromptText("Kategori");

		Button addProduct = new Button("Tilføj produkt");
		addProduct.setOnAction(e -> controller.addProducts());

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 3, 2, 1);

		Button btnCreatePriceList = new Button("Lav ny prisliste");
		btnCreatePriceList.setOnAction(e -> controller.createPriceList());

		Button btnRemovePriceList = new Button("Fjern denne prisliste");
		btnRemovePriceList.setOnAction(e -> controller.removePriceList());
		if (service.getActiveUser().getPermission() != Permission.ADMIN) {
			btnRemovePriceList.setDisable(true);
		}
		HBox hbox = new HBox();
		hbox.getChildren().addAll(tfNewPrice, addProduct);
		hbox.setSpacing(10);
		hbox.setMinWidth(400);
		hbox.setMaxWidth(400);
		add(hbox, 1, 1);

		HBox hbox2 = new HBox();
		hbox2.getChildren().addAll(btnCreatePriceList, btnRemovePriceList);
		hbox2.setSpacing(10);
		hbox2.setMinWidth(400);
		hbox2.setMaxWidth(400);
		add(hbox2, 0, 1);

	}

	class Controller {

		public void removePriceList() {
			String contentText = "Tryk OK for at fjerne denne liste, bemærk at den ikke kan genoprettes";
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Fjernelse af prisliste");
			alert.setHeaderText(
					"Du er i gang med at fjerne " + service.getSelectedPricelist());
			alert.setContentText(contentText);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				if (service.getPricelists().size() == 1) {
					Alert error = new Alert(AlertType.ERROR);
					contentText = "Denne liste kan ikke fjernes da det er den sidste i databasen";
					error.setTitle("Fejl");
					error.setContentText(contentText);
					error.showAndWait();
				} else {
					service.removePricelist(service.getSelectedPricelist());
					mainController.comboBoxFix();
				}
			} else {
				alert.close();
			}
		}

		public void createPriceList() {
			TextInputDialog id = new TextInputDialog();
			id.setTitle("Ny Prisliste");
			id.setHeaderText("Skriv navnet på den nye prisliste\n"
					+ "For at tilføje produkter vælg da prislisten i den øverste menu, og tilføj produkter");

			Optional<String> result = id.showAndWait();
			if (result.isPresent()) {
				String name = result.get();
				service.createPricelist(name);
			}
		}

		public void addProducts() {
			double price;

			try {
				price = Double.parseDouble(tfNewPrice.getText());
			} catch (NumberFormatException ex) {
				lError.setText("Prisen skal være et tal");
				return;
			}

			for (Product product : productList.getSelectedProducts()) {
				if (selectedPricelist.getProducts().contains(product)) {
					lError.setText("Et af produkterne findes allerede i prislisten");
					return;
				} else {
					service.setProductToPricelist(product, selectedPricelist, price);
					table.addItem(new PricelistElement(product, price));
				}
			}

			lError.setText("");
		}

		public void deleteProduct(PricelistElement t) {
			table.removeItem(t);
			service.removeProductFromPricelist(t.product, selectedPricelist);
		}

		public void updatePrice(PricelistElement t, Double price) {
			service.setProductToPricelist(t.getProduct(), service.getSelectedPricelist(),
					price);
		}

	}

	class PricelistElement {
		public final Product product;
		public final double price;

		public PricelistElement(Product product, double price) {
			this.product = product;
			this.price = price;
		}

		public Product getProduct() {
			return product;
		}

		public double getPrice() {
			return price;
		}
	}
}
