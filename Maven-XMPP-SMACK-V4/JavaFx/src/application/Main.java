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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.*;


public class Main extends Application {
	
	Stage window;
    Scene scene1, scene2;
    
	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			window.setTitle("Unipr Messenger");
			

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
				
				// TODO call App.Login() which return True if logged in correctl
				window.setScene(scene2);
			});
			
	        scene1 = new Scene(grid, 400, 400);


	        //Button 2
	        Button button2 = new Button("This sucks, go back to scene 1");
	        button2.setOnAction(e -> window.setScene(scene1));

	        //Layout 2
	        StackPane layout2 = new StackPane();
	        layout2.getChildren().add(button2);
	        scene2 = new Scene(layout2, 400, 400);

	        //Display scene 1 at first
	        window.setScene(scene1);
	        window.show();
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
