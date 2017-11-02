package gui;

import javax.security.sasl.AuthenticationException;

import javafx.animation.RotateTransition;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import service.Service;

import java.io.File;

public class Login extends GridPane {
	private final Service service = Service.getInstance();
	private final Controller controller = new Controller();
	private final TextField tfUsername = new TextField();
	private final TextField tfPassword = new PasswordField();
	private final Label lError = new Label();
	private final Handler<?> loginHandler;
	private final ImageView img = new ImageView(
			new Image(new File("images/logo.png").toURI().toString()));
	private final RotateTransition rt = new RotateTransition(Duration.millis(1000), img);

	public Login(Handler<?> loginHandler) {
		this.loginHandler = loginHandler;

		setHgap(10);
		setVgap(10);
		setAlignment(Pos.CENTER);

		// automatisk login mens vi udvikler
		tfUsername.setText("test");
		tfPassword.setText("test");

		GridPane.setHalignment(img, HPos.CENTER);
		add(img, 0, 0, 2, 1);

		add(new Label("Brugernavn"), 0, 1);
		add(tfUsername, 1, 1);

		add(new Label("Kodeord"), 0, 2);
		add(tfPassword, 1, 2);

		Button bLogin = new Button("Login");
		bLogin.setOnAction(e -> controller.login());
		GridPane.setHalignment(bLogin, HPos.RIGHT);
		add(bLogin, 1, 2);
		bLogin.setDefaultButton(true);

		lError.setStyle("-fx-text-fill: red");
		add(lError, 0, 3, 2, 1);
	}

	class Controller {

		public void login() {
			String username = tfUsername.getText().trim();
			String password = tfPassword.getText().trim();

			double angle = 1000 / rt.getCurrentTime().toMillis() * 360;
			if (rt.getCurrentTime().equals(Duration.ZERO)) {
				angle = 0;
			}
			rt.setByAngle(360);
			rt.setFromAngle(angle);
			rt.play();

			// Wait for the beautiful animation to end
			rt.setOnFinished(e -> {
				try {
					service.login(username, password);

					if (loginHandler != null) {
						loginHandler.exec(null);
					}
				} catch (AuthenticationException ex) {
					lError.setText("Brugernavn eller kodeord er forkert");
				}
			});
		}
	}
}
