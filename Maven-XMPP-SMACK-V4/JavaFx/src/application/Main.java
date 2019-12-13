package application;
import java.util.HashMap;

import application.Application.App;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	static LoginScene loginSceneClass;
	static HomepageScene homepageSceneClass;
	static RegistrationScene registrationSceneClass;

	static Stage window;

	/**
	 * 	Collection contiene le Chat Aperte, con un riferimento all'username
	 *  con il quale stiamo messaggiando e un riferimento alla "finestra"
	 *  di chat
	 */
	static HashMap<String, Stage> openChats = new HashMap<String, Stage>();

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			window.setTitle("Unipr Messenger");

			// Evento chiusura della finestra
			window.setOnCloseRequest((event) -> {
				if(App.isConnected())
				App.disconnect();

				// Chiudi tutte le chat aperte
				openChats.forEach((chatUsername, chatStage) -> {
					chatStage.close();
				});
			});

			loginSceneClass =  new LoginScene();

			// Display scene 1 at first
			window.setScene(loginSceneClass.getLoginScene());
			window.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		/**
		 * IL MAIN NON VA TOCCATO
		 *
		 * Il metodo launch � dichiarato nella super class Application launch(args)
		 * corrisponde a scrivere launch(this, args) Quindi conosce la classe che ha
		 * chiamato il metodo. Sar� proprio il metodo launch a chiamare il metodo start
		 * della classe che ha chiamato launch.
		 **/

		launch(args);
	}
}
