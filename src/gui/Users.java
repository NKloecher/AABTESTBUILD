package gui;

import java.util.ArrayList;
import java.util.List;

import gui.table.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import storage.Storage;
import service.Service;

import model.User;
import model.Permission;

public class Users extends GridPane {
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final Label lError = new Label();
	private final Table<User> table = new Table<>(
			(error, isValid) -> lError.setText(error));
	private final TextField tfName = new TextField();
	private final TextField tfUsername = new TextField();
	private final TextField tfPassword = new TextField();
	private final ComboBox<Permission> cbPermission = new ComboBox<>();

	public Users() {
		setHgap(10);
		setVgap(10);
		setAlignment(Pos.TOP_CENTER);

		Column<User> nameColumn = new PrimitiveColumn<>("Navn",
				PrimitiveColumn.Type.String, User::getName, controller::updateName,
				(u, n) -> controller.validateName(n));
		nameColumn.setMinWidth(150.0);
		nameColumn.setMaxWidth(150.0);
		table.addColumn(nameColumn);

		Column<User> usernameColumn = new PrimitiveColumn<>("Brugernavn",
				PrimitiveColumn.Type.String, User::getUsername,
				controller::updateUsername,
				(u, bn) -> controller.validateUsername(bn, u));
		usernameColumn.setMinWidth(150.0);
		usernameColumn.setMaxWidth(150.0);
		table.addColumn(usernameColumn);

		Column<User> permissionColumn = new ListColumn<>("Permission",
				User::getPermission, service::updateUserPermission, Permission.values());
		permissionColumn.setMinWidth(100.0);
		permissionColumn.setMaxWidth(100.0);
		table.addColumn(permissionColumn);

		Column<User> passwordColumn = new PasswordColumn<>("Sæt kode",
				controller::setPassword);
		passwordColumn.setMinWidth(90.0);
		table.addColumn(passwordColumn);

		Column<User> deleteColumn = new ButtonColumn<>("Delete", controller::deleteUser);
		deleteColumn.setMinWidth(80.0);
		deleteColumn.setMaxWidth(80.0);
		table.addColumn(deleteColumn);

		List<User> notDeletedUsers = new ArrayList<>();

		Storage storage = Storage.getInstance();
		for (User u : storage.getUsers()) {
			if (!u.isDeleted())
				notDeletedUsers.add(u);
		}

		table.setItems(notDeletedUsers);

		ScrollPane sp = new ScrollPane();
		sp.setHbarPolicy(ScrollBarPolicy.NEVER);
		sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		sp.setStyle("-fx-background-color:transparent;");
		sp.setContent(table.getPane());
		add(sp, 0, 0);

		HBox hbAdd = new HBox();

		tfName.setPromptText("Navn");
		hbAdd.getChildren().add(tfName);

		tfUsername.setPromptText("Brugernavn");
		hbAdd.getChildren().add(tfUsername);

		tfPassword.setPromptText("Kodeord");
		hbAdd.getChildren().add(tfPassword);

		cbPermission.getItems().setAll(Permission.values());
		hbAdd.getChildren().add(cbPermission);

		Button bAdd = new Button("Tilføj");
		bAdd.setOnAction(e -> controller.addUser());
		hbAdd.getChildren().add(bAdd);

		add(hbAdd, 0, 1);

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 2);
	}

	private class Controller {
		public String validateName(String name) {
			if (name.isEmpty()) {
				return "Navn er påkrævet";
			}

			return null;
		}

		public String validateUsername(String username, User user) {
			if (username.isEmpty()) {
				return "Brugernavn er påkrævet";
			}
			if (!service.usernameIsUnique(username, user)) {
				return "Brugernavnet er allerede brugt";
			}

			return null;
		}

		public void updateName(User user, String name) {
			lError.setText("");
			service.updateUserName(user, name);
		}

		public void updateUsername(User user, String username) {
			lError.setText("");
			service.updateUserUsername(user, username);
		}

		public void deleteUser(User user) {
			if (service.getActiveUser().equals(user)) {
				lError.setText("Du kan ikke slette den bruger du er logget ind som");
				return;
			}

			lError.setText("");
			table.removeItem(user);
			service.deleteUser(user);
		}

		public void setPassword(User user, String password) {
			service.updateUserPassword(user, password);
		}

		public void addUser() {
			String name = tfName.getText();
			String username = tfUsername.getText();
			String password = tfPassword.getText();
			Permission permission = cbPermission.getSelectionModel().getSelectedItem();

			if (permission == null) {
				lError.setText("Du skal vælge en permission");
				return;
			}

			String nameError = validateName(name);
			if (nameError != null) {
				lError.setText(nameError);
				return;
			}

			String usernameError = validateUsername(username, null);
			if (usernameError != null) {
				lError.setText(usernameError);
				return;
			}

			if (password.isEmpty()) {
				lError.setText("Kodeord er påkrævet");
				return;
			}

			if (password.length() < 8) {
				lError.setText("Koden skal være mindst 8 karakterer langt");
				return;
			}

			tfName.setText("");
			tfUsername.setText("");
			tfPassword.setText("");
			cbPermission.getSelectionModel().select(null);

			lError.setText("");
			User user = service.createUser(name, username, password, permission);
			table.addItem(user);
		}
	}
}