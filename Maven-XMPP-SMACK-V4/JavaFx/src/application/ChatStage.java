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
	
	public ChatStage(String username){
		super();
		
		this.setTitle(username);

		this.setOnCloseRequest((event) -> {
			if(App.getConnection() != null)
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
            if (e.getCode() == KeyCode.ENTER) {
            	
            	// Print on my Chat
            	if(textArea.getText().isEmpty()) {
            		textArea.appendText("You: " + messageField.getText());
            	} else {
            		textArea.appendText("\nYou: " + messageField.getText());
            	}
            	
                // TODO Invia il messaggio
                
            } else {
            	
            	// TODO invia "Sta scrivendo"
            }
        });

        pageLayout = new BorderPane();
        pageLayout.setPadding(new Insets(10));
        pageLayout.setCenter(textArea);
        BorderPane.setMargin(textArea, new Insets(0,0,10,0));
        pageLayout.setBottom(messageField);

        this.setScene(new Scene(pageLayout, 400, 400));
		this.show();
	}
}
