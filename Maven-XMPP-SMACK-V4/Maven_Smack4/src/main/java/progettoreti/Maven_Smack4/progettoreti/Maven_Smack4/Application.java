package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

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
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
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
import org.jxmpp.stringprep.XmppStringprepException;

public class Application {
	
	// Config Server - Connection
	private XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
	private static String XMPPServerAddress = "localhost";
	private static String XMPPDomain = "@messenger.unipr.it";
	private static int XMPPServerPort = 5222;
	private static AbstractXMPPConnection connection;
	
	public static String loggedUsername;
	public static boolean logged = false;

	// Variables
	private static boolean onChat = false;
	private static String onChatUsername;
	private static HashMap<String, Stack<Message>> incomingMessages = new HashMap<String, Stack<Message>>();
	public static Roster roster;
	private static Scanner reader;
	
	// Constructor
	public Application() throws XmppStringprepException {
		// Create the configuration for this new connection
		// XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		configBuilder.setSecurityMode(SecurityMode.disabled);
		configBuilder.setPort(XMPPServerPort);
		configBuilder.setHost(XMPPServerAddress);
		configBuilder.setXmppDomain(XMPPDomain);
		connection = new XMPPTCPConnection(configBuilder.build());
	}
	
	public static void connect() throws SmackException, IOException, XMPPException, InterruptedException {
		connection.connect();
	}
	
	public static void disconnect() {
		connection.disconnect();
	}
	
	public static void login(String username, String password) throws XMPPException, SmackException, IOException, InterruptedException {
		// Log into the server
		connection.login(username, password);
	}
	
	public static void singIn(String username, String password) throws XmppStringprepException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException {
		AccountManager manager = AccountManager.getInstance(connection);
		Localpart user = Localpart.from(username);
		
		manager.sensitiveOperationOverInsecureConnection(true); 	// It lets to create a new account
		manager.createAccount(user, password);						// Create the account
		manager.sensitiveOperationOverInsecureConnection(false); 	// It does not allow to create a new account
		
		System.out.println("Account registrato correttamente");
		
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
	
	public static boolean addFriend(String friendUsername) throws XmppStringprepException, NoResponseException, XMPPErrorException, NotConnectedException, InterruptedException, NotLoggedInException {
		
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
		
	}
	
	public static Collection<RosterEntry> getFriendList() {
		Collection<RosterEntry> entries = roster.getEntries();
		// Return the collection of entries
		return entries;
	}

}
