package application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import javafx.application.Platform;

public class Application {

	static class App {

		// Config Server - Connection
		private static XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		private static String XMPPServerAddress = "localhost";
		private static String XMPPDomain = "@messenger.unipr.it";
		private static int XMPPServerPort = 5222;
		private static AbstractXMPPConnection connection;
		private static ChatManager chatManager;

		// LoggedUser variables
		public static String loggedUsername;
		public static boolean logged = false;
		private static Collection<RosterEntry> friendList;

		private static HashMap<String, Chat> openChats = new HashMap<String, Chat>();
		private static HashMap<String, Stack<Message>> incomingMessages = new HashMap<String, Stack<Message>>();
		public static Roster roster;

		public static Boolean connect() {
			configBuilder = XMPPTCPConnectionConfiguration.builder();

			try {
				configBuilder.setSecurityMode(SecurityMode.disabled);
				configBuilder.setPort(XMPPServerPort);
				configBuilder.setHost(XMPPServerAddress);
				configBuilder.setXmppDomain(XMPPDomain);
				// configBuilder.enableDefaultDebugger();
				connection = new XMPPTCPConnection(configBuilder.build());
				connection.connect();
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		public static void disconnect() {
			try {
				loggedUsername = "";
				logged = false;
				roster = null;
				connection.disconnect();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		public static Boolean login(String username, String password) {
			try {

				// Log into the server
				connection.login(username, password);
				loggedUsername = username;
				App.logged = true;

				// Tutto questo pezzo di codice lo mettiamo in una funzione? Può tornare utile?
				roster = Roster.getInstanceFor(connection);

				roster.setSubscriptionMode(SubscriptionMode.accept_all);
				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus("Online");
				connection.sendStanza(presence);
				IncomingMessageListener();
				updateFriendList();

				return true;
			} catch (XMPPException ex) {
				ex.printStackTrace();
				connection.disconnect();
				return false;

			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

		public static boolean registerUser(String username, String password) {
			try {
				AccountManager manager = AccountManager.getInstance(connection);
				Localpart user = Localpart.from(username);

				manager.sensitiveOperationOverInsecureConnection(true); // It lets create a new account
				manager.createAccount(user, password); // Create the account
				manager.sensitiveOperationOverInsecureConnection(false); // It does not allow to create a new account

				// XXX Debug print
				System.out.println("Account registrato correttamente");
				return true;

			} catch (XMPPException ex) {
				ex.printStackTrace();
				return false;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

		public static boolean CreateChat(String username) {

			// Se la chat non � gi� aperta
			if (!openChats.containsKey(username)) {
				try {
					EntityBareJid jid = JidCreate.entityBareFrom(username + XMPPDomain);

					Chat chat = chatManager.chatWith(jid);

					openChats.put(username, chat);

					// New messages while i was online but not in chat
					if (incomingMessages.containsKey(username)) {	
						incomingMessages.get(username).forEach(message -> {
							((ChatStage) Main.openChats.get(username)).putMessage(message.getBody());
						});
						incomingMessages.remove(username);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}

		/**
		 * Close an open chat with a username if exists
		 * 
		 * @param username
		 * @return true if closed correctly, false otherwise
		 * 
		 */
		public static boolean closeChat(String username) {
			if (isChatOpen(username)) {
				try {
					openChats.remove(username);
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}

		/**
		 * Check if a Chat with a user is open
		 * 
		 * @param username
		 * @return true if chat is open, false otherwise
		 */
		public static boolean isChatOpen(String username) {
			if (openChats.containsKey(username)) {
				return true;
			}
			return false;
		}

		/**
		 * 
		 * @param username
		 * @param message
		 * @return
		 */
		public static boolean SendMessageTo(String username, String message) {
			if (isChatOpen(username)) {
				try {
					Chat chat = openChats.get(username);
					chat.send(message);
					return true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}

		// TODO forse servir� send Stanza I'm Typing
		public static void SendStanzaTyping() {

		}

		public static void IncomingMessageListener() {
			// Creating a listener for incoming messages
			chatManager = ChatManager.getInstanceFor(connection);

			chatManager.addIncomingListener(new IncomingChatMessageListener() {
				public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
					String senderUsername = from.getLocalpart().toString();

					if (isChatOpen(senderUsername)) {
						((ChatStage) Main.openChats.get(senderUsername)).putMessage(message.getBody());
					} else {

						// Add Tag new messages on ContactListElement
						Main.homepageSceneClass.getFriendListTilePanes().forEach(tilePane -> {
							if (((ContactListElement) tilePane).getUsername().equals(senderUsername)) {
								
								/*	MODIFICA UI DA THREAD NON JAVA FX
								 * 
								 *  Essendo Application.App un'applicazione NON JAVA FX non dovrebbe
								 *  modificare gli elementi di JavaFx quindi, usiamo il metodo runLater
								 *  per aggiungere la modifica della UI in un Thread JavaFX (l'ultima
								 *  affermazione potrebbe non essere corretta)
								 *  
								 *  Se chiamassi setNewMessagesNotification() fuori dalla lambda il
								 *  programma in runtime lancia l'eccezione:
								 *  
								 *  java.lang.IllegalStateException: Not on FX application thread; 
								 *  currentThread = Smack Listener Processor (0)
								 *  
								 */
								Platform.runLater(() -> {
									((ContactListElement) tilePane).setNewMessagesNotification();
								});
							}
						});

						if (incomingMessages.containsKey(senderUsername)) {
							incomingMessages.get(senderUsername).add(message);
						} else {
							incomingMessages.put(senderUsername, new Stack<Message>());
							incomingMessages.get(senderUsername).add(message);
						}
					}
				}
			});

		}

		public static boolean addFriend(String friendUsername) {
			// This function add a friend, true -> friend added, false -> user not found

			try {

				EntityBareJid friendJid = JidCreate.entityBareFrom(friendUsername + XMPPDomain);

				DomainBareJid searchService = JidCreate.domainBareFrom("search." + friendJid.asDomainBareJid());

				UserSearchManager search = new UserSearchManager(connection);

				Form searchForm = search.getSearchForm(searchService);
				Form answerForm = searchForm.createAnswerForm();

				answerForm.setAnswer("Username", true);
				answerForm.setAnswer("search", friendUsername);

				ReportedData data = search.getSearchResults(answerForm, searchService);

				if (data != null) {
					List<Row> rows = data.getRows();
					Iterator<Row> it = rows.iterator();

					if (!it.hasNext()) {
						System.out.println("Nessun utente trovato con questo username " + friendUsername);
					} else {
						boolean userFound = false;
						while (it.hasNext()) {
							Row row = it.next();
							List<CharSequence> values = row.getValues("username");

							if (values.contains(friendUsername)) {

								// Adds friend to friend list. The second parameter is the nickname, in this
								// case it is the same of username
								roster.createEntry(friendJid, friendUsername, null);

								// Asking to get the status of new friend
								roster.sendSubscriptionRequest(friendJid);
								updateFriendList();
								return true;
							}
						}

						if (!userFound) {
							return false;
						}
					}
				} else { // Data is null
					return false;
				}

				return false;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		public static boolean updateFriendList() {
			// Updating the friend list
			try {
				friendList = roster.getEntries();
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		public static Collection<RosterEntry> getFriendList() {
			updateFriendList();
			// Return the collection of entries
			return friendList;
		}

		public static boolean checkIfUserInFriendList(String friendUsername) {
			// This function check if a friend with that friendUsername is already in the
			// friendList
			for (RosterEntry friend : friendList) {
				if (friend.getName().equals(friendUsername)) {
					return true;
				}
			}
			return false;
		}

		public static AbstractXMPPConnection getConnection() {
			return connection;
		}

		public static void setConnection(AbstractXMPPConnection connection) {
			App.connection = connection;
		}

	}
}
