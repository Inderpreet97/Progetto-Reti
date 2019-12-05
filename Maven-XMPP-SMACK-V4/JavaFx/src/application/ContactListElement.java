package application;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

class ContactListElement extends TilePane{

	private Background backgroundColor = new Background(
			new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY));

	private Label aContactName;
	private Label aContactUsername;
	private Label aContactStatus;
	private Label aContactNewMessages;

	public ContactListElement(String aContactName, String aContactUsername, String aContactStatus, String aContactNewMessages) {
		super();
		this.aContactName = new Label(aContactName);
		this.aContactUsername = new Label(aContactUsername);
		this.aContactStatus = new Label(aContactStatus);
		this.aContactNewMessages = new Label(aContactNewMessages);

		// contat list element to return
		// TODO Il TilePane deve occupare tutto il VBox che è il parent di TilePane
		// https://docs.oracle.com/javafx/2/layout/size_align.htm
		// https://amyfowlersblog.wordpress.com/2010/05/26/javafx-1-3-growing-shrinking-and-filling/
		// 
		this.setBackground(backgroundColor);
		this.setPadding(new Insets(10, 10, 10, 10));
		this.setVgap(5);
		this.setHgap(5);
		this.setPrefWidth(Region.USE_COMPUTED_SIZE);
		this.setPrefColumns(4);
		this.setMaxWidth(Region.USE_PREF_SIZE);

		// contact name
		this.aContactName.setMaxWidth(Double.MAX_VALUE);
		this.getChildren().add(this.aContactName);

		// contact username
		this.getChildren().add(this.aContactUsername);

		// contact status
		this.getChildren().add(this.aContactStatus);

		// contact new messages
		this.getChildren().add(this.aContactNewMessages);

		// Click on TilePane to Open a Chat
		this.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {

			public void handle(MouseEvent e) {
				/*
				 *  TODO Create a new Window with chat
				 *  but first check the openChats HASHMAP
				 *  
				 *  if there is a already a window with the chat,
				 *  then do nothing, else open chat window
				 */
				if( ! Main.openChats.containsKey(aContactUsername)) {
					Main.openChats.put(aContactUsername, new ChatStage(aContactUsername));
				} else {
					// TODO debug only
					System.out.println("Chat with " + aContactUsername + " already open");
				}
				
			}
		});
	}
}
