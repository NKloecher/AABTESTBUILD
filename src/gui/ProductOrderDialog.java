package gui;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import exceptions.DiscountParseException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Order;
import model.ProductOrder;

public class ProductOrderDialog extends Stage {
	private final Order order;

	public ProductOrderDialog(Order order) {
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);

		setTitle(order.toString());
		GridPane pane = new GridPane();
		this.order = order;
		initContent(pane);

		Scene scene = new Scene(pane);
		setScene(scene);

	}

	private void initContent(GridPane pane) {
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setPadding(new Insets(10));
		pane.setAlignment(Pos.TOP_CENTER);

		Label produktLabel = new Label("Produkt");
		produktLabel.setStyle("-fx-font-weight: bolder");
		pane.add(produktLabel, 0, 0);
		Label antalLabel = new Label("Antal");
		antalLabel.setStyle("-fx-font-weight: bolder");
		pane.add(antalLabel, 1, 0);
		Label prisLabel = new Label("Pris");
		prisLabel.setStyle("-fx-font-weight: bolder");
		pane.add(prisLabel, 2, 0);
		Label rabatLabel = new Label("Rabat");
		rabatLabel.setStyle("-fx-font-weight: bolder");
		pane.add(rabatLabel, 3, 0);

		for (int i = 0; i < order.getAllProducts().size(); i++) {
			ProductOrder po = order.getAllProducts().get(i);

			pane.add(new Label(po.getProduct().toString()), 0, i + 1);
			pane.add(new Label("" + po.getAmount()), 1, i + 1);
			pane.add(new Label(String.format(Locale.GERMAN, "%.2f", po.price())), 2,
					i + 1);
			String rabat = "";

			try {
				rabat = "" + (po.getAmount() * po.getOriginalPrice() - po.price());
			} catch (DiscountParseException e) {
			}

			pane.add(new Label(rabat), 3, i + 1);
		}

		Label betalingerLabel = new Label("Betalinger");
		betalingerLabel.setStyle("-fx-font-weight: bolder");
		pane.add(betalingerLabel, 0, order.getAllProducts().size() + 1);
		Label antalLabel1 = new Label("Antal");
		antalLabel1.setStyle("-fx-font-weight: bolder");
		pane.add(antalLabel1, 2, order.getAllProducts().size() + 1);
		Label typeLabel = new Label("Type");
		typeLabel.setStyle("-fx-font-weight: bolder");
		pane.add(typeLabel, 3, order.getAllProducts().size() + 1);

		for (int i = 0; i < order.getPayments().size(); i++) {
			pane.add(
					new Label(order.getPayments().get(i).getDate()
							.format(DateTimeFormatter.ofLocalizedDateTime(
									FormatStyle.MEDIUM, FormatStyle.SHORT))),
					0, i + order.getAllProducts().size() + 2);
			pane.add(new Label(Double.toString(order.getPayments().get(i).getAmount())),
					2, i + order.getAllProducts().size() + 2);
			pane.add(new Label(order.getPayments().get(i).getPaymentType().toString()), 3,
					i + order.getAllProducts().size() + 2);
		}

	}

}
