package application;

import application.Application.App;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;


public class Main extends Application {

	static Stage window;
	static Scene loginScene, hompageScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			window.setTitle("Unipr Messenger");
			
			window.setOnCloseRequest((event) -> {
					App.disconnect();
			});
			
			createLoginScene();
			createhompageScene();
			
			// Display scene 1 at first
			window.setScene(loginScene);
			window.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createLoginScene() {
		loginScene = new LoginScene().getLoginScene();
	}

	public void createhompageScene() {
		// Button 2
		Button button2 = new Button("This sucks, go back to scene 1");
		button2.setOnAction(e -> window.setScene(loginScene));

		// Layout 2
		StackPane layout2 = new StackPane();
		layout2.getChildren().add(button2);
		hompageScene = new Scene(layout2, 400, 400);
	}

	public static void main(String[] args) {
		/**
		 * IL MAIN NON VA TOCCATO
		 * 
		 * Il metodo launch è dichiarato nella super class Application launch(args)
		 * corrisponde a scrivere launch(this, args) Quindi conosce la classe che ha
		 * chiamato il metodo. Sarà proprio il metodo launch a chiamare il metodo start
		 * della classe che ha chiamato launch.
		 **/

		launch(args);
	}
}
