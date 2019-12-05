package application;
import java.util.HashMap;

import application.Application.App;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

	static Stage window;
	static Scene loginScene, homepageScene;
	
	static HashMap<String, Stage> openChats = new HashMap<String, Stage>();

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			window.setTitle("Unipr Messenger");

			window.setOnCloseRequest((event) -> {
				if(App.getConnection() != null)
				App.disconnect();
				
				// Chiudi tutte le chat aperte
				openChats.forEach((chatUsername, chatStage) -> {
					chatStage.close();
				});
			});
			
			loginScene = new LoginScene().getLoginScene();
			homepageScene = new HomepageScene().getHomepageScene();

			// Display scene 1 at first
			window.setScene(loginScene);
			window.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
