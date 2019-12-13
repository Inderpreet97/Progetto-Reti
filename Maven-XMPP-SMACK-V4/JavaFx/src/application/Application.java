package application;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat2.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
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

import javafx.application.Platform;

public class Application {

	static class App {
		
		// Config Server - Connection
		private static XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		private static String XMPPServerAddress = "160.78.246.44";
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

		/**
		 * 
		 * @return
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
				// configBuilder.enableDefaultDebugger();
				connection = new XMPPTCPConnection(configBuilder.build());
				connection.connect();
				
				// XXX SLEEP AFTER CONNECT 
				System.out.println("Sleep After Connect 5s");
				TimeUnit.SECONDS.sleep(5);
				
				
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		/**
		 * 
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
		 * 
		 * @param username
		 * @param password
		 * @return
		 */
		public static Boolean login(String username, String password) {
			try {

				// Log into the server
				connection.login(username, password);

				OfflineMessageListener();

				loggedUsername = username;
				logged = true;

				// Tutto questo pezzo di codice lo mettiamo in una funzione? PuÃ² tornare utile?
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
				ex.printStackTrace();
				connection.disconnect();
				return false;

			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}

		}

		/**
		 * 
		 * @param username
		 * @param password
		 * @return
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
		 * 
		 * @param username
		 * @return
		 */
		public static boolean CreateChat(String username) {

			// Se la chat non è già aperta
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

		/**
		 * 
		 */
		private static void OfflineMessageListener() {
			OfflineMessageManager mOfflineMessageManager = new OfflineMessageManager(connection);

			try {
				// Get the message size

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

					mOfflineMessageManager.deleteMessages();
				}

			} catch (NoResponseException | XMPPErrorException | NotConnectedException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 
		 * @param senderUsername
		 * @return
		 */
		public static Boolean hasNewMessagesWhileOffline(String senderUsername) {
			if (incomingMessages.containsKey(senderUsername)) {
				return true;
			}
			return false;
		}

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
		 * 
		 */
		private static void incomingMessageListener() {
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
		 * 
		 */
		private static void incomingPresenceListener() {
			roster.addPresenceEventListener(new PresenceEventListener() {

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

				@Override
				public void presenceError(Jid address, Presence errorPresence) {
				}

				@Override
				public void presenceSubscribed(BareJid address, Presence subscribedPresence) {

					App.updateFriendList();

					Platform.runLater(() -> {
						Main.homepageSceneClass.updateFriendListView();
					});

				}

				@Override
				public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
				}
			});
		}

		/**
		 * 
		 * @param friendUsername
		 * @return
		 */
		public static boolean searchFriendServer(String friendUsername) {
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
						boolean userFound = false;
						while (it.hasNext()) {
							Row row = it.next();
							List<CharSequence> values = row.getValues("username");

							if (values.contains(loggedUsername)) {
								return false;
							}

							if (values.contains(friendUsername)) {
								return true;
							}
						}
						if (!userFound) {
							return false;
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
		 * 
		 * @param friendUsername
		 * @return
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
						boolean userFound = false;
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

		/**
		 * 
		 * @return
		 */
		public static boolean updateFriendList() {
			// Updating the friend list
			try {
				roster = Roster.getInstanceFor(connection);
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
		 * 
		 * @return
		 */
		public static Collection<RosterEntry> getFriendList() {
			updateFriendList();
			// Return the collection of entries
			return friendList;
		}

		/**
		 * 
		 * @param friendUsername
		 * @return
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
		 * 
		 * @return
		 */
		public static AbstractXMPPConnection getConnection() {
			return connection;
		}

		/**
		 * 
		 * @param connection
		 */
		public static void setConnection(AbstractXMPPConnection connection) {
			App.connection = connection;
		}

	}
}
