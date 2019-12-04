package application;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

class ContactListElement {
	
	private TilePane ContactElement;
	
	private Background backgroundColor = new Background(
			new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));
	
	private Label aContactName;
	private Label aContactUsername;
	private Label aContactStatus;
	private Label aContactNewMessages;
	
	
	public ContactListElement(String aContactName, String aContactUsername, String aContactStatus, String aContactNewMessages) {
		this.aContactName = new Label(aContactName);
		this.aContactUsername = new Label(aContactUsername);
		this.aContactStatus = new Label(aContactStatus);
		this.aContactNewMessages = new Label(aContactNewMessages);
		
		ContactElement = new TilePane();
		 
		// contat list element to return
		ContactElement.setBackground(backgroundColor);
		ContactElement.setPadding(new Insets(10, 10, 10, 10));
	    ContactElement.setVgap(5);
	    ContactElement.setHgap(5);
	    ContactElement.setPrefColumns(4);
	    ContactElement.setMaxWidth(Region.USE_PREF_SIZE);
	    
	    // contact name
	    this.aContactName.setMaxWidth(Double.MAX_VALUE);
	    this.ContactElement.getChildren().add(this.aContactName);
		
		// contact username
	    this.ContactElement.getChildren().add(this.aContactUsername);
		
		// contact status
	    this.ContactElement.getChildren().add(this.aContactStatus);
		
		// contact new messages
	    this.ContactElement.getChildren().add(this.aContactNewMessages);
	}


	public TilePane getContactElement() {
		return ContactElement;
	}
}
