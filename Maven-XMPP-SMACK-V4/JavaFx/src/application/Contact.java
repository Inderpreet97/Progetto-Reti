package application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public class Contact {
	private final StringProperty nome;
	private final StringProperty username;
	private final StringProperty stato;
	private final BooleanProperty newMessages;
	
	public Contact(StringProperty nome, StringProperty username, StringProperty stato, BooleanProperty newMessages) {
		super();
		this.nome = nome;
		this.username = username;
		this.stato = stato;
		this.newMessages = newMessages;
	}
	
	public StringProperty getNome() {
		return nome;
	}
	public StringProperty getUsername() {
		return username;
	}
	public StringProperty getStato() {
		return stato;
	}
	public BooleanProperty getNewMessages() {
		return newMessages;
	}
	
}
