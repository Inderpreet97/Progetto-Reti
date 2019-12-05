package application;

import application.Application.App;
import javafx.scene.Scene;
import javafx.stage.Stage;

class ChatStage extends Stage {
	Scene loginScene;
	
	public ChatStage(String username){
		super();
		
		this.setTitle(username);

		this.setOnCloseRequest((event) -> {
			if(App.getConnection() != null)
			App.disconnect();
		});
		
		loginScene = new LoginScene().getLoginScene();

		// Display scene 1 at first
		this.setScene(loginScene);
		this.show();
	}
}
