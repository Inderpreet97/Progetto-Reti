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

class RegistrationScene {
	private Scene loginScene = null;
	private Text sceneTitle;
	
	private GridPane grid;
	
	private Label userName;
	private TextField userTextField;
	
	private Label passwordLabelChoose;
	private Label passwordLabelConfirm;
	private PasswordField passwordFieldChoose;
	private PasswordField passwordFieldConfirm;
	
	private Button btn;
	private HBox hbBtn;
	private final Text actiontarget;
	
	RegistrationScene(){
		grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		sceneTitle = new Text("Sign Up");
		sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(sceneTitle, 0, 0, 2, 1);

		// Username input
		userName = new Label("User Name: ");
		grid.add(userName, 0, 1);
		userTextField = new TextField();
		grid.add(userTextField, 1, 1);

		// Password choose
		passwordLabelChoose = new Label("Choose a password: ");
		grid.add(passwordLabelChoose, 0, 2);
		passwordFieldChoose = new PasswordField();
		grid.add(passwordFieldChoose, 1, 2);
		
		// Password confirm
		passwordLabelConfirm = new Label("Confirm password: ");
		grid.add(passwordLabelConfirm, 0, 3);
		passwordFieldConfirm = new PasswordField();
		grid.add(passwordFieldConfirm, 1, 3);

		// Sign Up Button
		btn = new Button("Sign UP");
		hbBtn = new HBox(10);
		hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
		hbBtn.getChildren().add(btn);
		grid.add(hbBtn, 1, 4);

		actiontarget = new Text();
		grid.add(actiontarget, 1, 6);

		userTextField.setOnKeyReleased((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				attemptRegistration();
			}
		});

		
		
		passwordFieldChoose.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				attemptRegistration();
			}
		});
		
		passwordFieldConfirm.setOnKeyPressed((event) -> {
			if (event.getCode() == KeyCode.ENTER) {
				attemptRegistration();
			}
		});

		btn.setOnAction((e) -> attemptRegistration());

		setLoginScene(new Scene(grid, 400, 400));
	}
	
	public Scene getLoginScene() {
		return loginScene;
	}

	public void setLoginScene(Scene loginScene) {
		this.loginScene = loginScene;
	}

	private void attemptRegistration() {
		String username = userTextField.getText();
		String passwordChoose = passwordFieldChoose.getText();
		String passwordConfirm = passwordFieldConfirm.getText();
		
		if (!username.isEmpty() && !passwordChoose.isEmpty() && !passwordConfirm.isEmpty()) {

			// Check if passwordChoose and passwordConfirm are equals
			if (passwordConfirm.equals(passwordChoose)) {
				
				// Connect to OpenFire Server
				if (App.connect()) {
					if (App.login(username, passwordChoose)) {
						
						passwordFieldChoose.clear();
						passwordFieldConfirm.clear();
						Main.loginScene = new LoginScene().getLoginScene();
						Main.window.setScene(Main.loginScene);

					} else {
						passwordFieldChoose.clear();
						passwordFieldConfirm.clear();
						actiontarget.setFill(Color.FIREBRICK);
						actiontarget.setText("Username or Password is wrong");
					}

				} else {
					passwordFieldChoose.clear();
					passwordFieldConfirm.clear();
					actiontarget.setFill(Color.FIREBRICK);
					actiontarget.setText("Connection to server failed");
				}
			} else {
				passwordFieldChoose.clear();
				passwordFieldConfirm.clear();
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Passwords are NOT equals");
			}
			
		} else {
			passwordFieldChoose.clear();
			passwordFieldConfirm.clear();
			actiontarget.setFill(Color.FIREBRICK);
			actiontarget.setText("Username or Password is empty");
		}
	}
	
	
}
