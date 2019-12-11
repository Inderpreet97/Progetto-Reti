package application;

import java.util.ArrayList;

import application.Application.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

class HomepageScene {
	private Scene homepageScene = null;
	private VBox mainContent = null;
	private ArrayList<TilePane> friendListTilePanes;
	

	HomepageScene() {
		
		Background backgroundColor = new Background(
				new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));

		// Homepage Layout
		BorderPane homepageLayout = new BorderPane();
		homepageLayout.setBackground(backgroundColor);

		// Homepage Header HBox
		HBox header = new HBox();
		header.setPadding(new Insets(10));
		header.setSpacing(5);
		homepageLayout.setTop(header);

		// Homepage Footer HBox
		HBox footer = new HBox();
		footer.setPadding(new Insets(10));
		footer.setBackground(backgroundColor);
		homepageLayout.setBottom(footer);

		// Homepage Main Content VBox
		mainContent = new VBox();
		mainContent.setPadding(new Insets(10));
		mainContent.setSpacing(5);
		mainContent.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		homepageLayout.setCenter(mainContent);

		// Header Nodes
		TextField searchText = new TextField();
		searchText.setPromptText("Search user");
		searchText.setPrefWidth(300);
		searchText.setFocusTraversable(false);
		header.getChildren().add(searchText);
		
		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);
		header.getChildren().add(btnSearch);

		// Footer Nodes
		// Footer Logout Button
		Button btnLogout = new Button("Logout");
		btnLogout.setOnAction(e -> {
		
			App.disconnect();
			
			// Chiudi tutte le chat aperte
			Main.openChats.forEach((chatUsername, chatStage) -> {
				chatStage.close();
			});
			
			Main.loginSceneClass = new LoginScene();
			Main.window.setScene(Main.loginSceneClass.getLoginScene());
		});

		footer.setAlignment(Pos.BOTTOM_RIGHT);
		footer.getChildren().add(btnLogout);

		friendListTilePanes = new ArrayList<TilePane>();
		
		// Wait until roster get loaded
		while (!App.roster.isLoaded()) {
			
		}
		
		// Main Content Nodes
		if (App.logged) {
			
			App.getFriendList().forEach((entry) -> {
				String name = entry.getName();
				String presence = App.roster.getPresence(entry.getJid()).getStatus();
				String username = "";
				
				if(presence == null) {
					presence = "Offline";
				}
				
				try {
					username = entry.getJid().getLocalpartOrThrow().toString();
				} catch (Exception e) {
					
				}
				
				friendListTilePanes.add(new ContactListElement(name, username, presence));
			});
			
			mainContent.getChildren().addAll(friendListTilePanes);
		}

		// Create Scene with BorderPane homepageLayout
		homepageScene = new Scene(homepageLayout, 400, 400);
	}
	
	public ArrayList<TilePane> getFriendListTilePanes() {
		return friendListTilePanes;
	}
	
	public Scene getHomepageScene() {
		return homepageScene;
	}
}
