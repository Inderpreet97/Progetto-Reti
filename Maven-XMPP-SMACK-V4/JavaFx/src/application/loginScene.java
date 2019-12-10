package application;

import application.Application.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

class LoginScene {
	private Scene loginScene = null;
	private Text sceneTitle;
	private Label userName;
	private TextField userTextField;
	private GridPane grid;
	private Label passwordLabel;
	private PasswordField passwordField;
	private Button btn;
	private Button signUpBtn;
	private HBox hbBtn;
	private final Text actiontarget;

	LoginScene() {
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		sceneTitle = new Text("Login");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		userName = new Label("User Name: ");
		grid.add(userName, 0, 1);

		userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		passwordLabel = new Label("Password: ");
		grid.add(passwordLabel, 0, 2);

		passwordField = new PasswordField();
		grid.add(passwordField, 1, 2);

		btn = new Button("Sign in");
		hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);
		
		signUpBtn = new Button("Sign up");
		hbBtn.getChildren().add(signUpBtn);
		grid.add(hbBtn, 1, 5);

		actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		userTextField.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				attemptLogin();
			}
		});

		passwordField.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				attemptLogin();
			}
		});

		btn.setOnAction((e) -> attemptLogin());
		
		signUpBtn.setOnAction((e) -> {
			Main.RegistrationScene = new RegistrationScene().get; 
			Main.window.setScene(Main.RegistrationScene);
		});

		setLoginScene(new Scene(grid, 400, 400));
	}

	public Scene getLoginScene() {
		return loginScene;
	}

	public void setLoginScene(Scene loginScene) {
		this.loginScene = loginScene;
	}

	private void attemptLogin() {
		String username = userTextField.getText();
		String password = passwordField.getText();

		if (!username.isEmpty() && !password.isEmpty()) {

			// Connect to OpenFire Server
			if (App.connect()) {
				if (App.login(username, password)) {
					passwordField.clear();
					Main.homepageScene =  new HomepageScene().getHomepageScene();
					Main.window.setScene(Main.homepageScene);

				} else {
					passwordField.clear();
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Username or Password is wrong");
				}

			} else {
				passwordField.clear();
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Connection to server failed");
			}

		} else {
			passwordField.clear();
			actiontarget.setFill(Color.FIREBRICK);
			actiontarget.setText("Username or Password is empty");
		}
	}

}
