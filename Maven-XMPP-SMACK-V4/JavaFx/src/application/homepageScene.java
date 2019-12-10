package application;

import java.util.ArrayList;

import application.Application.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;

class HomepageScene {
	private Scene homepageScene = null;

	HomepageScene() {
		Background backgroundColor = new Background(
				new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));

		// Homepage Layout
		BorderPane homepageLayout = new BorderPane();
		homepageLayout.setBackground(backgroundColor);

		// Homepage Header HBox
		HBox header = new HBox();
		header.setPadding(new Insets(10));
		homepageLayout.setTop(header);

		// Homepage Footer HBox
		HBox footer = new HBox();
		footer.setPadding(new Insets(10));
		footer.setBackground(backgroundColor);
		homepageLayout.setBottom(footer);

		// Homepage Main Content VBox
		VBox mainContent = new VBox();
		mainContent.setPadding(new Insets(10));
		mainContent.setSpacing(5);
		mainContent.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		homepageLayout.setCenter(mainContent);

		// Header Nodes
		Label exampleLabel = new Label("This is an example Header Label");
		exampleLabel.setTextFill(Color.web("#0076a3"));
		header.getChildren().add(exampleLabel);

		// Footer Nodes
		// Footer Logout Button
		Button btnLogout = new Button("Logout");
		btnLogout.setOnAction(e -> {
			App.disconnect();
			
			// Chiudi tutte le chat aperte
			Main.openChats.forEach((chatUsername, chatStage) -> {
				chatStage.close();
			});
			
			Main.window.setScene(Main.loginScene);
		});

		footer.setAlignment(Pos.BOTTOM_RIGHT);
		footer.getChildren().add(btnLogout);

		ArrayList<TilePane> friendListTilePanes = new ArrayList<TilePane>();
		
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
				
				friendListTilePanes.add(new ContactListElement(name, username, presence, " "));
			});
			
			mainContent.getChildren().addAll(friendListTilePanes);
		}

		// Create Scene with BorderPane homepageLayout
		setHomepageScene(new Scene(homepageLayout, 400, 400));
	}

	public Scene getHomepageScene() {
		return homepageScene;
	}

	public void setHomepageScene(Scene homepageScene) {
		this.homepageScene = homepageScene;
	}
}
