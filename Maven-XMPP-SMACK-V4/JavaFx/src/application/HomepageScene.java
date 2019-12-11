package application;

import java.util.ArrayList;
import java.util.Optional;

import application.Application.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

class HomepageScene {
	private Scene homepageScene = null;
	private VBox mainContent = null;
	private ArrayList<TilePane> friendListTilePanes;
	private TextField searchText;

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
		searchText = new TextField();
		searchText.setPromptText("Search USERNAME");
		searchText.setPrefWidth(300);
		searchText.setFocusTraversable(false);
		searchText.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				searchUsername(searchText.getText());
		});

		header.getChildren().add(searchText);

		Button btnSearch = new Button("Search");
		btnSearch.setPrefWidth(100);
		btnSearch.setOnAction(e -> searchUsername(searchText.getText()));
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

		updateFriendListView();

		// Create Scene with BorderPane homepageLayout
		homepageScene = new Scene(homepageLayout, 400, 400);
	}

	private void searchUsername(String searchedUsername) {

		if (searchedUsername != null && !searchedUsername.isEmpty()) {
			if (App.checkIfUserInFriendList(searchedUsername)) {
				// Add Tag new messages on ContactListElement
				friendListTilePanes.forEach(tilePane -> {
					if (((ContactListElement) tilePane).getUsername().equals(searchedUsername)) {
						((ContactListElement) tilePane).openChat();
					}
				});

			} else {

				if (App.searchFriendServer(searchedUsername)) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Search Username");
					alert.setHeaderText("User not in your friend list");
					alert.setContentText("Add to your friend list?");

					Optional<ButtonType> result = alert.showAndWait();

					if (result.get() == ButtonType.OK) {
						if (App.addFriend(searchedUsername)) {
							updateFriendListView();

							alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Search Username");
							alert.setHeaderText(null);
							alert.setContentText("New User added to your friend list");

							alert.showAndWait();

						} else {

							alert = new Alert(AlertType.ERROR);
							alert.setTitle("Search Username");
							alert.setHeaderText(null);
							alert.setContentText("User not added in your friend list");

							alert.showAndWait();
						}

					} else {
						searchText.clear();
					}

				} else {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Search Username");
					alert.setHeaderText(null);
					alert.setContentText("Username not found in your friend list and on server");

					alert.showAndWait();
				}

			}

		}
	}

	public ArrayList<TilePane> getFriendListTilePanes() {
		return friendListTilePanes;
	}

	public Scene getHomepageScene() {
		return homepageScene;
	}

	public void updateFriendListView() {
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

				if (presence == null) {
					presence = "Offline";
				}

				try {
					username = entry.getJid().getLocalpartOrThrow().toString();
				} catch (Exception e) {

				}

				friendListTilePanes.add(new ContactListElement(name, username, presence));
			});
			
			mainContent.getChildren().clear();
			mainContent.getChildren().addAll(friendListTilePanes);
		}
	}
}
