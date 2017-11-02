package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.security.sasl.AuthenticationException;

import exceptions.DiscountParseException;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Pricelist;
import service.Service;

public class MainApp extends Application {
	private final Service service = Service.getInstance();
	private final CController controller = new Controller();
	private final BorderPane pane = new BorderPane();
	private final ComboBox<String> cbPricelist = new ComboBox<>();
	private final ImageView img = new ImageView(
			new Image(new File("images/rsz_logo.png").toURI().toString()));
	private final RotateTransition rt = new RotateTransition(Duration.millis(1000), img);

	private Stage owner;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage stage)
			throws DiscountParseException, AuthenticationException {
		this.owner = stage;
		Service.getInstance().initStorage();

		Scene scene = new Scene(pane);

		stage.setMaximized(true);
		stage.setScene(scene);
		stage.show();

		stage.setTitle("Aarhus Bryghus");

		initContent();
	}

	private final Button home = new Button("Hjem");

	private void initContent() {
		owner.setResizable(false);
		HBox hMenu = new HBox(10);

		StackPane stackPane = new StackPane();
		Label lUserName = new Label("Ikke logget ind");
		Label lUser = new Label("Bruger:");
		Label lPricelist = new Label("   Prislisten:");
		Button logout = new Button("Log ud");

		stackPane.maxWidthProperty().bind(owner.widthProperty());

		hMenu.setStyle("-fx-background-color: #135b1f; -fx-padding: 20px;");
		hMenu.setAlignment(Pos.BASELINE_LEFT);
		hMenu.setMaxHeight(50);

		lUserName.setStyle("-fx-text-fill: white; -fx-font-weight: bolder");
		lUser.setStyle("-fx-text-fill: darkgray;");
		lPricelist.setStyle("-fx-text-fill: darkgray;");

		List<String> pricelists = new ArrayList<>();
		for (Pricelist pl : service.getPricelists()) {
			pricelists.add(pl.getName());
		}
		cbPricelist.getItems().setAll(pricelists);
		cbPricelist.setOnAction(e -> controller.selectPricelist());
		cbPricelist.getSelectionModel().select(0);

		Login l = new Login(x -> {
			MainMenu m = new MainMenu(owner, controller);
			home.setOnAction(e -> controller.setScreen(m));
			m.setOnSelect(controller::setScreen);
			controller.setScreen(m);

			lUserName.setText(service.getActiveUser().getUsername());
			this.pane.setTop(stackPane);
		});

		logout.setTranslateX(hMenu.getWidth());
		logout.setOnAction(e -> {
			service.logout();
			controller.setScreen(l);
			this.pane.getChildren().removeIf(n -> (n instanceof StackPane));
		});

		hMenu.getChildren().add(home);
		hMenu.getChildren().add(lPricelist);
		hMenu.getChildren().add(cbPricelist);

		Region r = new Region();
		HBox.setHgrow(r, Priority.ALWAYS);
		hMenu.getChildren().add(r);

		hMenu.getChildren().add(lUser);
		hMenu.getChildren().add(lUserName);
		hMenu.getChildren().add(logout);

		stackPane.getChildren().add(hMenu);

		VBox imgPane = new VBox();
		imgPane.setPickOnBounds(false);
		img.setOnMouseClicked(e -> controller.spinThatShit());
		imgPane.setPadding(new Insets(5, 0, 5, 0));
		imgPane.getChildren().add(img);
		imgPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(imgPane);

		controller.setScreen(l);
	}

	public Controller getController() {
		return controller;
	}

	@Override
	public void stop() {
		try {
			service.saveStorage();
		} catch (IOException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error!");
			alert.setHeaderText("Error saving data");
			alert.setContentText("Could not save the data: " + e);
			alert.showAndWait();
		}
	}

	protected class Controller {

		public void spinThatShit() {
			Random ran = new Random();

			double angle = 1000 / rt.getCurrentTime().toMillis() * 360;
			if (rt.getCurrentTime().equals(Duration.ZERO)) {
				angle = 0;
			}
			rt.setByAngle(360);
			rt.setFromAngle(angle);
			rt.setRate(ran.nextDouble());
			rt.play();
		}

		public void lockPricelist(boolean state) {
			cbPricelist.setDisable(state);
		}

		public void selectPricelist() {
			String pricelistName = cbPricelist.getSelectionModel().getSelectedItem();

			for (Pricelist pl : service.getPricelists()) {
				if (pl.getName().equals(pricelistName)) {
					service.setSelectedPricelist(pl);
				}
			}

			if (MainApp.this.pane.getCenter() instanceof Pricelists) {
				setScreen(new Pricelists(getController()));
			}
		}

		public void comboBoxFix() {
			// Out of bounds Fix for removal of a pricelist
			cbPricelist.getSelectionModel().select(0);
		}

		public void setScreen(Pane pane) {
			if (pane instanceof MainMenu) {
				home.setDisable(true);
			} else {
				home.setDisable(false);
			}
			GridPane oldPane = (GridPane) MainApp.this.pane.getCenter();
			List<String> pricelists = new ArrayList<>();

			for (Pricelist pl : service.getPricelists()) {
				pricelists.add(pl.getName());
			}

			cbPricelist.getItems().setAll(pricelists);

			pane.setPadding(new Insets(20));
			ObservableList<Node> children = MainApp.this.pane.getChildren();
			for (int i = 0; i < children.size(); i++) {
				if (children.get(i) instanceof GridPane) {
					children.remove(i);
				}
			}

			if (pane instanceof MainMenu || pane instanceof Pricelists) {
				lockPricelist(false);
			} else {
				lockPricelist(true);
			}

			// Sliding animation
			if (oldPane != null) {
				oldPane.setMinWidth(oldPane.getWidth());
				pane.setMinWidth(oldPane.getWidth());

				HBox tempPane;

				// Reverse if main menu
				if (pane instanceof MainMenu || pane instanceof Login) {
					tempPane = new HBox(pane, oldPane);
				} else {
					tempPane = new HBox(oldPane, pane);
				}

				TranslateTransition tt = new TranslateTransition(Duration.millis(1000),
						tempPane);

				// Reverse if main menu
				if (pane instanceof MainMenu || pane instanceof Login) {
					tt.setFromX(-oldPane.getWidth());
					tt.setToX(0);
				} else {
					tt.setToX(-oldPane.getWidth());
				}

				MainApp.this.pane.setCenter(tempPane);
				tt.play();
				tt.setOnFinished(e -> MainApp.this.pane.setCenter(pane));
			} else {
				MainApp.this.pane.setCenter(pane);
			}
		}
	}
}