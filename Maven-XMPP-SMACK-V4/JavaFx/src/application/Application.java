package application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat2.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.Roster.SubscriptionMode;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.SubscribeListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import javafx.application.Platform;

public class Application {

	static class App {

		// Config Server - Connection
		private static XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		private static String XMPPServerAddress = "localhost";
		private static String XMPPDomain = "@messenger.unipr.it";
		private static int XMPPServerPort = 5222;
		private static AbstractXMPPConnection connection;
		
		// Variables
		private static ChatManager chatManager;
		private static HashMap<String, Chat> openChats = new HashMap<String, Chat>();
		private static HashMap<String, Stack<Message>> incomingMessages = new HashMap<String, Stack<Message>>();
		public static Roster roster;
		
		// LoggedUser variables
		public static String loggedUsername;
		public static boolean logged = false;
		private static Collection<RosterEntry> friendList;

		
		/**
		 * Create a connection with Server (TCP + XML Stream)
		 * 
		 * @return	true if connection created successfully, false otherwise
		 */
		public static Boolean connect() {
			configBuilder = XMPPTCPConnectionConfiguration.builder();

			try {
				configBuilder.setSecurityMode(SecurityMode.disabled);
				configBuilder.setPort(XMPPServerPort);
				configBuilder.setHost(XMPPServerAddress);
				configBuilder.setXmppDomain(XMPPDomain);
				configBuilder.setSendPresence(false); // Permette di riceve i messaggi ricevuti mentre l'utente che si
				// sta per loggare era offline
				//configBuilder.enableDefaultDebugger();
				connection = new XMPPTCPConnection(configBuilder.build());
				connection.connect();
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		/**
		 * Destroy the connection with Server (TCP + XML Stream)
		 */
		public static void disconnect() {
			try {
				loggedUsername = "";
				logged = false;
				roster = null;
				connection.disconnect();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		/**
		 * Attempt login with the given credentials, 
		 * If loggin is successful retrieve offline messages it there are any, 
		 * set roster, start incomingMessagesListener, start incomingPresenceListener
		 * start incomingSubscriptionListener and update FriendList
		 * 
		 * @param username
		 * @param password
		 * @return true logged in correctly, false otherwise
		 */
		public static Boolean login(String username, String password) {
			try {

				// Log into the server
				connection.login(username, password);

				OfflineMessageListener();

				loggedUsername = username;
				logged = true;

				// Tutto questo pezzo di codice lo mettiamo in una funzione? Può tornare utile?
				roster = Roster.getInstanceFor(connection);

				roster.setSubscriptionMode(SubscriptionMode.manual);

				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus("Online");
				connection.sendStanza(presence);

				incomingMessageListener();
				incomingPresenceListener();
				incomingSubscriptionListener();

				updateFriendList();

				return true;
			} catch (XMPPException ex) {
				// ex.printStackTrace();
				connection.disconnect();
				return false;

			} catch (Exception ex) {
				// ex.printStackTrace();
				return false;
			}

		}

		/**
		 * Register a new user in the server with the given credentials
		 * 
		 * @param username
		 * @param password
		 * @return true is registration is successful, false otherwise
		 */
		public static boolean registerUser(String username, String password) {
			try {
				AccountManager manager = AccountManager.getInstance(connection);
				Localpart user = Localpart.from(username);

				manager.sensitiveOperationOverInsecureConnection(true); // It lets create a new account
				manager.createAccount(user, password); // Create the account
				manager.sensitiveOperationOverInsecureConnection(false); // It does not allow to create a new account

				return true;

			} catch (XMPPException ex) {
				ex.printStackTrace();
				return false;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

		/**
		 * Start a chat a with the given username
		 * 
		 * @param username
		 * @return true if chat created successfully, false otherwise
		 */
		public static boolean CreateChat(String username) {

			// If the chat is not already is open
			if (!openChats.containsKey(username)) {
				try {
					EntityBareJid jid = JidCreate.entityBareFrom(username + XMPPDomain);

					Chat chat = chatManager.chatWith(jid);
					
					// add this chat to Open Chats HashMap
					openChats.put(username, chat);

					// print messages sent while the user was online but not in this chat
					if (incomingMessages.containsKey(username)) {
						incomingMessages.get(username).forEach(message -> {
							((ChatStage) Main.openChats.get(username)).putMessage(message.getBody());
						});
						incomingMessages.remove(username);
					}
					
					return true;
					
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
			return true;
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
		 * Send the given message to the given user
		 * 
		 * @param username
		 * @param message
		 * @return true if message sent correctly, false otherwise
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

		/**
		 * Check if this user while it was offline has received messages from the given user
		 * 
		 * @param senderUsername
		 * @return true it there are messages to read, false otherwise
		 */
		public static Boolean hasNewMessagesWhileOffline(String senderUsername) {
			if (incomingMessages.containsKey(senderUsername)) {
				return true;
			}
			return false;
		}
		
		/**
		 * If there are messages stored on server that the User didn't received because it 
		 * was offline, thery retrieved from serve and added to the incomingMessages HashMap
		 * ready to be print when the user will open the chat with the user who sent the messages
		 */
		private static void OfflineMessageListener() {
			OfflineMessageManager mOfflineMessageManager = new OfflineMessageManager(connection);

			try {
				// Get the number of messages on server
				int size = mOfflineMessageManager.getMessageCount();

				if (size > 0) {
					
					// Load all messages from the storage
					List<Message> messages = mOfflineMessageManager.getMessages();
					messages.forEach(message -> {
						String senderUsername = message.getFrom().getLocalpartOrNull().toString();

						if (incomingMessages.containsKey(senderUsername)) {
							incomingMessages.get(senderUsername).add(message);
						} else {
							incomingMessages.put(senderUsername, new Stack<Message>());
							incomingMessages.get(senderUsername).add(message);
						}
					});
					
					// delete the messages from server
					mOfflineMessageManager.deleteMessages();
				}

			} catch (NoResponseException | XMPPErrorException | NotConnectedException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Creats a listener for incoming Subscirption Presences, Accept them, add the sender of the subscription 
		 * to the current user friend list, and subscribe current user to the presence of the sender 
		 */
		private static void incomingSubscriptionListener() {
			roster.addSubscribeListener(new SubscribeListener() {

				@Override
				public SubscribeAnswer processSubscribe(Jid from, Presence subscribeRequest) {
					try {
						roster.createEntry(from.asBareJid(), from.getLocalpartOrNull().toString(), null);
						roster.sendSubscriptionRequest(from.asBareJid());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return SubscribeAnswer.Approve;
				}
			});
		}

		/**
		 * Creats a listener for incoming Messages, if the current user has the chat open with the sender
		 * of the message the listener just print it on the chat. But if the chat is not open the listener
		 * add the messages to an incoming messages HashMap ready to be printed when the user will open the
		 * chat. The listener also set a flag to notify that there are some new messages from a specific user.
		 */
		private static void incomingMessageListener() {
			// Creating a listener for incoming messages
			chatManager = ChatManager.getInstanceFor(connection);

			chatManager.addIncomingListener(new IncomingChatMessageListener() {
				public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
					String senderUsername = from.getLocalpart().toString();
					if (isChatOpen(senderUsername)) {
						// Print the message on the open chat
						((ChatStage) Main.openChats.get(senderUsername)).putMessage(message.getBody());
					} else {

						// Add Tag new messages on ContactListElement
						Main.homepageSceneClass.getFriendListTilePanes().forEach(tilePane -> {
							if (((ContactListElement) tilePane).getUsername().equals(senderUsername)) {

								/*
								 * MODIFICA UI DA THREAD NON JAVA FX
								 * 
								 * Essendo Application.App un'applicazione NON JAVA FX non dovrebbe modificare
								 * gli elementi di JavaFx quindi, usiamo il metodo runLater per aggiungere la
								 * modifica della UI in un Thread JavaFX (l'ultima affermazione potrebbe non
								 * essere corretta)
								 * 
								 * Se chiamassi setNewMessagesNotification() fuori dalla lambda il programma in
								 * runtime lancia l'eccezione:
								 * 
								 * java.lang.IllegalStateException: Not on FX application thread; currentThread
								 * = Smack Listener Processor (0)
								 * 
								 */
								Platform.runLater(() -> {
									((ContactListElement) tilePane).setNewMessagesNotification();
								});
							}
						});
						
						/* Add the message to an HashMap ready to be printed when the use will open the chat
						 * with the sender of the message
						 */
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

		/**
		 * Creats different listeners for different presence packets
		 */
		private static void incomingPresenceListener() {
			roster.addPresenceEventListener(new PresenceEventListener() {
				
				/**
				 * Listener for Presences of Available type
				 * If the presence is not self sent, the method setOnline of ContactListElement is called
				 * for the sender of the presence. setOnline() changes the printed status of the sender to "Online" 
				 */
				@Override
				public void presenceAvailable(FullJid address, Presence availablePresence) {
					String senderUsername = address.getLocalpartOrNull().toString();
					if (!senderUsername.equals(loggedUsername)) {
						try {
							Main.homepageSceneClass.getFriendListTilePanes().forEach(tilePane -> {
								if (((ContactListElement) tilePane).getUsername().equals(senderUsername)) {

									Platform.runLater(() -> {
										((ContactListElement) tilePane).setOnline();
									});
								}
							});
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}

				/**
				 * Listener for Presences of Unavailable type
				 * If the presence is not self sent, the method setOffline of ContactListElement is called
				 * for the sender of the presence. setOffline() changes the printed status of the sender to "Offline"
				 */
				@Override
				public void presenceUnavailable(FullJid address, Presence presence) {

					String senderUsername = address.getLocalpartOrNull().toString();
					Main.homepageSceneClass.getFriendListTilePanes().forEach(tilePane -> {
						if (((ContactListElement) tilePane).getUsername().equals(senderUsername)) {

							Platform.runLater(() -> {
								((ContactListElement) tilePane).setOffline();
							});
						}
					});
				}

				// Unset Listener
				@Override
				public void presenceError(Jid address, Presence errorPresence) {
				}
				
				/**
				 * Listener for Presences of type Subscriebed
				 * If a user subscribed to the presence of the current user, the current user update
				 * its Friend List View to show the new user, wwhich has been added automatically to
				 * the friend list of the current user.
				 * 
				 */
				@Override
				public void presenceSubscribed(BareJid address, Presence subscribedPresence) {

					App.updateFriendList();

					Platform.runLater(() -> {
						Main.homepageSceneClass.updateFriendListView();
					});

				}
				
				// Unset Listener
				@Override
				public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
				}
			});
		}

		/**
		 * Search a user on the server
		 * 
		 * @param friendUsername
		 * @return true if the username exists on the server, false otherwise
		 */
		public static boolean searchFriendServer(String friendUsername) {
			try {
				// JabberID of the user to search
				EntityBareJid friendJid = JidCreate.entityBareFrom(friendUsername + XMPPDomain);
				
				// The search service has a domain jabber id -> search.messenger.unipr.it
				DomainBareJid searchService = JidCreate.domainBareFrom("search." + friendJid.asDomainBareJid());
				
				UserSearchManager search = new UserSearchManager(connection);
				
				// Retrieve from server the search form and the instructions to use it
				Form searchForm = search.getSearchForm(searchService);
				Form answerForm = searchForm.createAnswerForm();
				
				answerForm.setAnswer("Username", true); // dove cercare la parola contenuta nel campo search
				answerForm.setAnswer("search", friendUsername); // la parola da cercara
				
				// Execute the "query" and the saves the results in data
				ReportedData data = search.getSearchResults(answerForm, searchService);

				if (data != null) {
					List<Row> rows = data.getRows();
					Iterator<Row> it = rows.iterator();

					if (!it.hasNext()) {
						/* If data has only one row, so there isn't a next row, it means that the data is 
						 * incomplete
						 */
						return false;

					} else {
						// Explore the returned data
						while (it.hasNext()) {
							Row row = it.next();
							List<CharSequence> values = row.getValues("username");
							
							// The search should not return the user who made the search
							if (values.contains(loggedUsername)) {
								return false;
							}
							
							// User found
							if (values.contains(friendUsername)) {
								return true;
							}
						}
					}
				}
				return false;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		/**
		 * Search a user on the server and if it exists add it to the current user roster ("friend list")
		 * and subscribe to his presence
		 * 
		 * @param friendUsername
		 * @return true user added correctly, false otherwise
		 */
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
						return false;
					} else {
						while (it.hasNext()) {
							Row row = it.next();
							List<CharSequence> values = row.getValues("username");

							if (values.contains(loggedUsername)) {
								return false;
							}

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
					}
				}
				
				return false;
				
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		/**
		 * Updated the friend list
		 * 
		 * @return true if updated correctly, false otherwise
		 */
		public static boolean updateFriendList() {
			try {
				roster = Roster.getInstanceFor(connection);
				
				// Wait until the roster is fully loaded
				while (!roster.isLoaded()) {

				}
				friendList = roster.getEntries();
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		/**
		 * Getter for friendlist
		 * 
		 * @return Collection<RosterEntry>
		 */
		public static Collection<RosterEntry> getFriendList() {
			updateFriendList();
			// Return the collection of entries
			return friendList;
		}

		/**
		 * Gets the presence of the given contact and set it to its presence
		 */
		public static void updatePresence(ContactListElement contactToAdd) {
			
			EntityBareJid jid;
			
			try {
				
				jid = JidCreate.entityBareFrom(contactToAdd.getUsername() + XMPPDomain);
				
				if (App.roster.getPresence(jid).getType() == Type.available) {
					contactToAdd.setOnline();
				}
			} catch (XmppStringprepException e) {
				contactToAdd.setOffline();
			}
			
			
		}

		/**
		 * Check if the given username is present in the friendlist of the logged user
		 * 
		 * @param friendUsername
		 * @return true if present in list, false otherwise
		 */
		public static boolean checkIfUserInFriendList(String friendUsername) {
			// This function check if a friend with that friendUsername is already in the
			// friendList

			for (RosterEntry friend : friendList) {
				if (friend.getJid().getLocalpartOrNull().toString().equals(friendUsername)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check if the connection to Openfire Server is set
		 * 
		 * @return true if connected, false otherwise
		 */
		public static Boolean isConnected() {
			
			if(connection != null) {
				return true;
			}
			
			return false;
		}

	}
}
