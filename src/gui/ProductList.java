package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;
import model.Product;
import service.Service;
import storage.Storage;

public class ProductList extends BorderPane {
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final TextField tfSearch = new TextField();
	private final ComboBox<String> cbCategories = new ComboBox<>();
	private final List<Product> selectedProducts = new ArrayList<>();
	private final List<Product> allProducts;
	private GridPane pane = new GridPane();
	private Handler<Product> selectHandler;
	private Handler<Product> deselectHandler;

	public ProductList(List<Product> products) {
		allProducts = products;

		setMinWidth(650);
		setMaxWidth(650);

		final HBox hbQuery = new HBox();
		hbQuery.setStyle("-fx-padding: 0 0 16px 0;");

		tfSearch.setOnKeyTyped(e -> controller.findProducts());
		tfSearch.setPrefWidth(540);
		hbQuery.getChildren().add(tfSearch);

		Storage storage = Storage.getInstance();
		final List<String> categories = storage.getCategories();
		categories.add(0, "All");
		cbCategories.getItems().setAll(categories);
		cbCategories.valueProperty().addListener(e -> controller.findProducts());
		cbCategories.getSelectionModel().select(0);
		cbCategories.setPrefWidth(100);
		hbQuery.getChildren().add(cbCategories);

		controller.showProducts(products);

		setTop(hbQuery);
	}

	public void setSelectHandler(Handler<Product> handler) {
		selectHandler = handler;
	}

	public void setDeselectHandler(Handler<Product> handler) {
		deselectHandler = handler;
	}

	public List<Product> getSelectedProducts() {
		return new ArrayList<>(selectedProducts);
	}

	public void select(Product p) {
		controller.select(p);
	}

	public void deselect(Product p) {
		controller.deselect(p);
	}

	class Controller {
		public void findProducts() {
			final String selectedCategory = cbCategories.getSelectionModel()
					.getSelectedItem();
			final String query = tfSearch.getText();
			final List<Product> matchingProducts = service.getMatchingProducts(query,
					selectedCategory, allProducts);

			showProducts(matchingProducts);
		}

		public void showProducts(List<Product> products) {
			pane = new GridPane();

			final int productSize = 150;

			pane.setHgap(10);
			pane.setVgap(10);
			pane.setAlignment(Pos.TOP_CENTER);

			products.sort(null);

			for (int i = 0; i < products.size(); i++) {
				final Product p = products.get(i);
				Node node;

				if (p.getImage() != null) {
					File file = new File("product_images/" + p.getImage());
					Image image = new Image(file.toURI().toString(), productSize - 20,
							productSize - 20, true, true);
					ImageView imageView = new ImageView(image);
					BorderPane bp = new BorderPane(imageView);

					bp.setPadding(new Insets(10));

					node = bp;
				} else {
					Label label = new Label(p.getName());

					label.setMinWidth(productSize);
					label.setMaxWidth(productSize);
					label.setMinHeight(productSize);
					label.setMaxHeight(productSize);
					label.setWrapText(true);
					label.setTextAlignment(TextAlignment.CENTER);

					node = label;
				}

				node.setUserData(p);
				node.setStyle(
						"-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 1px;");
				GridPane.setHalignment(node, HPos.CENTER);

				node.setOnMouseClicked(e -> {
					if (selectedProducts.contains(p)) {
						deselect(p);
					} else {
						select(p);
					}
				});

				pane.add(node, i % 4, i / 4);
			}
			ScrollPane sp = new ScrollPane();
			sp.setHbarPolicy(ScrollBarPolicy.NEVER);
			sp.setVbarPolicy(ScrollBarPolicy.ALWAYS);
			sp.setContent(pane);
			GridPane.setMargin(pane, new Insets(20));

			setCenter(sp);
		}

		public Node findNode(Product p) {
			for (Node node : pane.getChildren()) {
				if (node.getUserData().equals(p)) {
					return node;
				}
			}

			throw new RuntimeException("the product was not found in the list");
		}

		public void select(Product p) {
			Node node = findNode(p);

			selectedProducts.add(p);

			if (selectHandler != null) {
				selectHandler.exec(p);
			}

			node.setStyle(
					"-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 2px;");
		}

		public void deselect(Product p) {
			Node node = findNode(p);

			selectedProducts.remove(p);

			if (deselectHandler != null) {
				deselectHandler.exec(p);
			}

			node.setStyle(
					"-fx-border-color: black; -fx-border-style: solid; -fx-border-width: 1px;");
		}
	}
}