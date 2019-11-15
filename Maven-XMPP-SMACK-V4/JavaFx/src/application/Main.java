package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.*;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Unipr Messenger");
			
			GridPane grid = new GridPane();
			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(25,25,25,25));
			
			Text sceneTitle = new Text("Login");
			sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			grid.add(sceneTitle, 0, 0, 2, 1);
			
			Label userName = new Label("User Name: ");
			grid.add(userName, 0, 1);
			
			TextField userTextField = new TextField();
			grid.add(userTextField, 1, 1);
			
			Label password = new Label("Password: ");
			grid.add(password, 0, 2);
			
			PasswordField passwordField = new PasswordField();
			grid.add(passwordField, 1, 2);
			
			Button btn = new Button("Sign in");
			HBox hbBtn = new HBox(10);
			hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
			hbBtn.getChildren().add(btn);
			grid.add(hbBtn, 1, 4);
			
			final Text actiontarget = new Text();
			grid.add(actiontarget, 1, 6);
			
			btn.setOnAction((e) -> {
				actiontarget.setFill(Color.FIREBRICK);
				actiontarget.setText("Sign in button pressed");
			});
			
			Scene scene = new Scene(grid,400,400);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		/**
		 * IL MAIN NON VA TOCCATO
		 * 
		 * Il metodo launch è dichiarato nella super class Application
		 * launch(args) corrisponde a scrivere launch(this, args)
		 * Quindi conosce la classe che ha chiamato il metodo.
		 * Sarà proprio il metodo launch a chiamare il metodo start
		 * della classe che ha chiamato launch.
		**/
		
		launch(args);
	}
}
