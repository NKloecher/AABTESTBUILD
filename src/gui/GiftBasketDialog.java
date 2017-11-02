package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import gui.table.Column;
import gui.table.LabelColumn;
import gui.table.PrimitiveColumn;
import gui.table.Table;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Order;
import model.Product;
import model.ProductOrder;
import service.Service;

public class GiftBasketDialog extends Stage {
	private final Order order;

	public GiftBasketDialog(Window owner, Order order) {
		this.order = order;
		// setMaximized(true);
		initModality(Modality.APPLICATION_MODAL);
		setResizable(false);
		initOwner(owner);
		setTitle("Tilføj produkter");
		GridPane pane = new GridPane();
		initContent(pane);

		Scene scene = new Scene(pane);
		setScene(scene);
	}

	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final Table<ProductOrder> productTable = new Table<>(null);
	private final List<ProductOrder> newProducts = new ArrayList<>();

	private void initContent(GridPane pane) {
		pane.setVgap(10);
		pane.setHgap(10);
		pane.setAlignment(Pos.TOP_CENTER);

		LabelColumn<ProductOrder> nameColumn = new LabelColumn<>("Navn",
				po -> po.getProduct().getName() + ", " + po.getProduct().getCategory());
		nameColumn.setPrefWidth(getOwner().getWidth() / 2);

		Column<ProductOrder> amountColumn = new PrimitiveColumn<>("Antal",
				PrimitiveColumn.Type.Integer, ProductOrder::getAmount,
				controller::updateAmount, (po, v) -> {
					if (Pattern.matches("^\\d+$", v)) {
						return null;
					}
					return "Antal skal være et positivt tal";
				});
		amountColumn.setMinWidth(20.0);

		productTable.addColumn(nameColumn);
		productTable.addColumn(amountColumn);

		for (ProductOrder po : order.getAllProducts()) {
			if (po.getGift()) {
				newProducts.add(po);
			}
		}
		productTable.setItems(newProducts);

		List<Product> products = new ArrayList<>();
		for (Product p : service.getSelectedPricelist().getProducts()) {
			if (p.getCategory().equals("flaske")) {
				products.add(p);
			}
		}
		ProductList pl = new ProductList(products);

		for (ProductOrder po : newProducts) {
			pl.select(po.getProduct());
		}

		pl.setSelectHandler(p -> {
			ProductOrder po = order.addProduct(p);
			po.setGiftStatus();
			productTable.addItem(po);
		});

		pl.setDeselectHandler(p -> {
			ProductOrder po = order.removeProduct(p);
			po.setGiftStatus();
			productTable.removeItem(po);

		});

		ScrollPane sp = new ScrollPane();
		sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setMinWidth(650); // 650
		sp.setContent(pl);

		pane.add(pl, 0, 0);
		pane.add(productTable.getPane(), 1, 0, 2, 1);

		Button btnOK = new Button("OK");
		btnOK.setOnAction(e -> close());
		pane.add(btnOK, 1, 1);
		btnOK.setDefaultButton(true);

	}

	class Controller {
		public void updateAmount(ProductOrder po, int amount) {
			service.updateProductOrderAmount(po, amount);
		}
	}
}
