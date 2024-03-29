package application;

import application.Application.App;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

class ChatStage extends Stage {

	private TextArea textArea;
	private TextField messageField;
	private BorderPane pageLayout;

	private String destinationUsername;

	public ChatStage(String username) {
		super();
		destinationUsername = username;

		this.setTitle(destinationUsername);

		this.setOnCloseRequest((event) -> {
			if (App.isConnected())
				App.disconnect();
		});

		textArea = new TextArea();

		textArea.setEditable(false);
		textArea.setMaxHeight(getMaxHeight());
		textArea.setPadding(new Insets(10));
		textArea.setFocusTraversable(false);

		messageField = new TextField();
		messageField.setPromptText("Type message here");

		messageField.setOnKeyPressed(e -> {
			if (App.isChatOpen(username)) {
				if (e.getCode() == KeyCode.ENTER) {
					String message = messageField.getText();
					messageField.clear();
					if(!message.isEmpty()) {
						// Print on my Chat
						if (textArea.getText().isEmpty()) {
							textArea.appendText("You: " + message);
						} else {
							textArea.appendText("\nYou: " + message);
						}
						// Invia messaggio all'utente finale
						App.SendMessageTo(destinationUsername, message);
					}
					
				} else {
					// NOT IMPLEMENTED "Sta scrivendo"
				}

			} else {
				textArea.appendText(">> Chat not opened correctly");
			}
		});

		// Evento chiusura della finestra
		this.setOnCloseRequest((event) -> {
			// Rimuovi questa chat dalla lista delle chat aperte
			Main.openChats.remove(destinationUsername);
			// Rimuovi chat da "backend"
			App.closeChat(destinationUsername);
		});

		pageLayout = new BorderPane();
		pageLayout.setPadding(new Insets(10));
		pageLayout.setCenter(textArea);
		BorderPane.setMargin(textArea, new Insets(0, 0, 10, 0));
		pageLayout.setBottom(messageField);
		

		this.setScene(new Scene(pageLayout, 400, 400));
		this.show();
	}

	public void putMessage(String incomingMessage) {
		// Print on my Chat
		if (textArea.getText().isEmpty()) {
			textArea.appendText(destinationUsername + ": " + incomingMessage);
		} else {
			textArea.appendText("\n" + destinationUsername + ": " + incomingMessage);
		}
	}
}
