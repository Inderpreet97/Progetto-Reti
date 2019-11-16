package application;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
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

public class Application {

	static class App{

		// Config Server - Connection
		private static XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		private static String XMPPServerAddress = "localhost";
		private static String XMPPDomain = "@messenger.unipr.it";
		private static int XMPPServerPort = 5222;
		private static AbstractXMPPConnection connection;

		// LoggedUser variables
		public static String loggedUsername;
		public static boolean logged = false;
		public static Collection<RosterEntry> friendList;
		
		// Variables
		private static boolean onChat = false;
		private static String onChatUsername;
		private static HashMap<String, Stack<Message>> incomingMessages = new HashMap<String, Stack<Message>>();
		public static Roster roster;
		private static Scanner reader;

		public static void connect() throws SmackException, IOException, XMPPException, InterruptedException {
			// Create the configuration for this new connection
			// XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
			try {
				configBuilder.setSecurityMode(SecurityMode.disabled);
				configBuilder.setPort(XMPPServerPort);
				configBuilder.setHost(XMPPServerAddress);
				configBuilder.setXmppDomain(XMPPDomain);
				connection = new XMPPTCPConnection(configBuilder.build());
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			try {
				connection.connect();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		public static void disconnect() {
			try {
				connection.disconnect();
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		public static void login(String username, String password) throws XMPPException, SmackException, IOException, InterruptedException {
			try {
				
				// Log into the server
				connection.login(username, password);
				loggedUsername = username;
				
				// Tutto questo pezzo di codice lo mettiamo in una funzione? Può tornare utile?
				roster = Roster.getInstanceFor(connection);
				roster.setSubscriptionMode(SubscriptionMode.accept_all);
				Presence presence = new Presence(Presence.Type.available);
				presence.setStatus("Online");
				connection.sendStanza(presence);
				App.logged = true;
				updateFriendList();
				
			} catch (XMPPException ex) {
				
				System.out.println("\nERRORE DURANTE IL LOGIN.");
				System.out.println("XMPPException: " + ex.getMessage());
				System.out.println("Potrebbero essere stati inseriti username e/o password sbagliati.");
				System.out.println("Premere un tasto per continuare...");
				connection.disconnect();
				connection.connect();
			
				
			} catch (Exception ex) {
				System.out.println("\nERRORE: " + ex.getMessage());
			}
		
			
		}

		public static void singIn(String username, String password) {
			try {	
				AccountManager manager = AccountManager.getInstance(connection);
				Localpart user = Localpart.from(username);

				manager.sensitiveOperationOverInsecureConnection(true); 	// It lets create a new account
				manager.createAccount(user, password);						// Create the account
				manager.sensitiveOperationOverInsecureConnection(false); 	// It does not allow to create a new account

				System.out.println("Account registrato correttamente");
			} catch (XMPPException ex) {
				System.out.println("ERRORE DURANTE LA REGISTRAZIONE DI UN ACCOUNT.");
				System.out.println(ex.getMessage());
				System.out.println("L'username inserito potrebbe essere già registrato.");
				System.out.println("Premere un tasto per continuare...");
				reader.nextLine();
			} catch (Exception ex) {
				System.out.println("\nERRORE DURANTE LA REGISTRAZIONE.");
				System.out.println(ex.getMessage());
			}


		}

		public static void IncomingMessageListener() {
			// Creating a listener for incoming messages
			ChatManager chatManager = ChatManager.getInstanceFor(connection);

			chatManager.addIncomingListener(new IncomingChatMessageListener() {
				public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
					String senderUsername = from.getLocalpart().toString();
					if (onChat && onChatUsername.equals(senderUsername)) {
						System.out.println(senderUsername + ": " + message.getBody());
					} else {
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

								// Adds friend to friend list. The second parameter is the nickname, in this case it is the same of username
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
				}
				else { // Data is null
					return false;
				}

				return false;
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
				return false;
			}
		}

		public static boolean updateFriendList(){
			// Updating the friend list
			try {
				friendList = roster.getEntries();
				return true;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
			return false;
		}
		
		public static Collection<RosterEntry> getFriendList() {
			Collection<RosterEntry> entries = roster.getEntries();
			friendList = entries; 
			// Return the collection of entries
			return friendList;
		}
		
		public static boolean checkIfUserInFriendList(String friendUsername) {
			// This function check if a friend with that friendUsername is already in the friendList
			for (RosterEntry friend : friendList) {
				if (friend.getName().equals(friendUsername)) {
					return true;
				}
			}
			return false;
		}

	}
}
