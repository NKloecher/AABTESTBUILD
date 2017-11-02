package gui;

import java.util.Locale;
import java.util.regex.Pattern;

import exceptions.DiscountParseException;
import gui.table.ButtonColumn;
import gui.table.Column;
import gui.table.LabelColumn;
import gui.table.PrimitiveColumn;
import gui.table.Table;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.DepositProduct;
import model.Order;
import model.PaymentStatus;
import model.ProductOrder;
import service.Service;

public class Sale extends GridPane {
	private final Stage owner;
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final Order order = service.createOrder();
	private final Label lError = new Label();
	private final Button pay = new Button("Betal");
	private final LabelColumn<ProductOrder> priceColumn = new LabelColumn<>("Pris",
			po -> {
				try {
					return String.format(Locale.GERMAN, "%.2f kr.", po.price());
				} catch (Exception e) {
					return e.getMessage();
				}

			});
	private final Label lTotal = new Label();
	private final Label lblCustomer = new Label();
	private final Handler<?> orderPaidHanlder;
	private final Table<ProductOrder> productTable = new Table<>(controller::validate);
	private final TextField discount = new TextField();

	public Sale(Stage owner, Handler<?> orderPaidHanlder) {
		setPadding(new Insets(20));
		setHgap(10);
		setVgap(10);

		this.owner = owner;
		this.orderPaidHanlder = orderPaidHanlder;

		LabelColumn<ProductOrder> nameColumn = new LabelColumn<>("Navn",
				po -> po.getProduct().getName() + ", " + po.getProduct().getCategory());
		nameColumn.setPrefWidth(owner.getWidth() / 2);

		Column<ProductOrder> amountColumn = new PrimitiveColumn<>("Antal",
				PrimitiveColumn.Type.Integer, ProductOrder::getAmount,
				controller::updateAmount, (po, v) -> {
					if (Pattern.matches("^\\d+$", v)) {
						return null;
					}
					return "Antal skal være et positivt tal";
				});
		amountColumn.setMinWidth(50.0);

		Column<ProductOrder> discountColumn = new PrimitiveColumn<>("Rabat",
				PrimitiveColumn.Type.String, ProductOrder::getDiscount,
				controller::updateDiscount);
		discountColumn.setMinWidth(50.0);

		LabelColumn<ProductOrder> depositColumn = new LabelColumn<>("Pant", po -> {
			if (po.getProduct() instanceof DepositProduct) {
				return String.format(Locale.GERMAN, "%.2f kr.",
						((DepositProduct) po.getProduct()).getDeposit());
			} else {
				return "";
			}
		});

		ButtonColumn<ProductOrder> btnGiftProductsColumn = new ButtonColumn<ProductOrder>(
				"Gave", controller::addGifts) {
			@Override
			public Node getNode(ProductOrder po) {
				Node node = super.getNode(po);

				if (po.getProduct().getCategory().equals("sampakninger")) {
					return node;
				} else {
					return null;
				}
			}
		};
		btnGiftProductsColumn.setMinWidth(55.0);

		depositColumn.setMinWidth(80.0);

		priceColumn.setMinWidth(80.0);

		productTable.addColumn(nameColumn);
		productTable.addColumn(amountColumn);
		productTable.addColumn(discountColumn);
		productTable.addColumn(depositColumn);
		productTable.addColumn(priceColumn);
		productTable.addColumn(btnGiftProductsColumn);

		productTable.setItems(order.getAllProducts());

		ProductList pl = new ProductList(service.getSelectedPricelist().getProducts());
		pl.setSelectHandler(p -> {
			ProductOrder po = order.addProduct(p);
			productTable.addItem(po);

			controller.updateRow();
		});

		pl.setDeselectHandler(p -> {
			ProductOrder po = order.removeProduct(p);

			productTable.removeItem(po);

			controller.updateRow();
		});

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 1);

		ScrollPane sp = new ScrollPane();
		sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setMinWidth(650); // 650
		sp.setContent(pl);

		add(pl, 0, 0);
		add(productTable.getPane(), 1, 0, 2, 1);

		discount.setPromptText("Rabat");
		discount.setOnAction(e -> controller.updateOrderDiscount());

		HBox buttons = new HBox();
		Button btnCustomer = new Button("Tilføj Kunde");
		buttons.getChildren().addAll(lTotal, btnCustomer, lblCustomer, discount);
		buttons.setSpacing(20);
		add(buttons, 1, 1);
		btnCustomer.prefWidth(100);
		btnCustomer.setOnAction(e -> controller.addCustomer());

		controller.updateTotal();

		pay.setDefaultButton(true);
		pay.setMinWidth(80);
		pay.setOnAction(e -> controller.showPayDialog());
		add(pay, 3, 1);
	}

	class Controller {

		public void addGifts(ProductOrder o) {
			if (!o.getProduct().getCategory().equals("sampakninger")) {
				lError.setText("Kan kun tilføje gaver til gaveæsker etc.");
			} else {
				GiftBasketDialog gb = new GiftBasketDialog(owner, order);
				gb.showAndWait();

			}
		}

		public void validate(String error, boolean isValid) {
			pay.setDisable(!isValid);

			if (!order.getRentalProductOrders().isEmpty()
					&& order.getCustomer() == null) {
				pay.setDisable(true);
			}

			lError.setText(error);
		}

		public void addCustomer() {
			AddCustomerDialog ad = new AddCustomerDialog(owner, order);
			ad.showAndWait();
			if (order.getCustomer() != null) {
				lblCustomer.setText(order.getCustomer().getName());
			}
			validate("", productTable.isValid());
		}

		public void showPayDialog() {
			boolean gift = false;
			for (ProductOrder po : order.getAllProducts()) {
				if (po.getProduct().getCategory().equals("sampakninger")) {
					gift = true;
				}
			}
			if (gift) {
				for (ProductOrder po : order.getAllProducts()) {
					if (po.getGift()) {
						lError.setText("");
						break;
					} else {
						lError.setText("En gaveæske skal have produkter tilføjet");
					}
				}
			}
			PayDialog pd = new PayDialog(owner, order, order.totalPrice(),
					order.totalDeposit());

			pd.showAndWait();

			PaymentStatus status = order.paymentStatus();

			boolean depositOrPriceIsPaid = status == PaymentStatus.ORDERPAID
					|| status == PaymentStatus.DEPOSITPAID;
			if (depositOrPriceIsPaid) {
				orderPaidHanlder.exec(null);
			}
		}

		public void updateRow() {
			validate("", productTable.isValid());
			updateTotal();
		}

		public void updateTotal() {
			String text = String.format(Locale.GERMAN, "Total %.2f kr.",
					order.totalPrice());

			if (order.totalPayment() != 0) {
				text += String.format(Locale.GERMAN, " Mangler at betale: %.2f kr.",
						order.totalPrice() - order.totalPayment());
			}

			lTotal.setText(text);
		}

		public void updateDiscount(ProductOrder po, String value) {
			try {
				po.setDiscount(value);

				lError.setText("");

				priceColumn.updateCell(po);
				controller.updateTotal();
			} catch (DiscountParseException e) {
				lError.setText("ugyldig rabat på \"" + po.getProduct().getName() + "\"");
			}
		}

		public void updateAmount(ProductOrder po, int amount) {
			service.updateProductOrderAmount(po, amount);
			priceColumn.updateCell(po);
			controller.updateTotal();
		}

		public void updateOrderDiscount() {
			service.updateOrderDiscount(order, discount.getText());
		}
	}

}