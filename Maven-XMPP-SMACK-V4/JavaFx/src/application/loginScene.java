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
	
	LoginScene() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text sceneTitle = new Text("Login");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		Label userName = new Label("User Name: ");
		grid.add(userName, 0, 1);

		TextField userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		Label passwordLabel = new Label("Password: ");
		grid.add(passwordLabel, 0, 2);

		PasswordField passwordField = new PasswordField();
		grid.add(passwordField, 1, 2);

		Button btn = new Button("Sign in");
		HBox hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		final Text actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		userTextField.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {

				String username = userTextField.getText();
				String password = passwordField.getText();

				if (!username.isEmpty() && !password.isEmpty()) {

					// Connect to OpenFire Server
					if (App.connect()) {
						if (App.login(username, password)) {
							Main.window.setScene(Main.hompageScene);
							
						} else {
							actiontarget.setFill(Color.FIREBRICK);
							actiontarget.setText("Username or Password is wrong");
						}

					} else {
						actiontarget.setFill(Color.FIREBRICK);
						actiontarget.setText("Connection to server failed");
					}

				} else {
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Username or Password is empty");
				}
			}
		});

		passwordField.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {

				String username = userTextField.getText();
				String password = passwordField.getText();

				if (!username.isEmpty() && !password.isEmpty()) {

					// Connect to OpenFire Server
					if (App.connect()) {
						if (App.login(username, password)) {
							Main.window.setScene(Main.hompageScene);
							
						} else {
							actiontarget.setFill(Color.FIREBRICK);
							actiontarget.setText("Username or Password is wrong");
						}

					} else {
						actiontarget.setFill(Color.FIREBRICK);
						actiontarget.setText("Connection to server failed");
					}

				} else {
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Username or Password is empty");
				}
			}
		});

		btn.setOnAction((e) -> {
			String username = userTextField.getText();
			String password = passwordField.getText();

			if (!username.isEmpty() && !password.isEmpty()) {

				// Connect to OpenFire Server
				if (App.connect()) {
					if (App.login(username, password)) {
						Main.window.setScene(Main.hompageScene);
						
					} else {
						actiontarget.setFill(Color.FIREBRICK);
						actiontarget.setText("Username or Password is wrong");
					}

				} else {
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Connection to server failed");
				}

			} else {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Username or Password is empty");
			}
		});

		setLoginScene(new Scene(grid, 400, 400));
	}

	public Scene getLoginScene() {
		return loginScene;
	}

	public void setLoginScene(Scene loginScene) {
		this.loginScene = loginScene;
	}
}
