package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

/** 
 * 
 * @author indim
 * 
 * Creare classe Chat con un attributo bool onChat.
 * Il messageListener se onChat = true allora stampa il messaggio sulla console.
 * Se onChat = false aggiunge il messaggio in uno stack che verrà stampato quando l'utente entra in chat
 * 
 * Dopo il login mostrare un menu
 * 		- Aggiungi amico
 * 		- visualizza lista amici e stato amici e nuovi messaggi(si/no)
 * 		- inzia chat con un amico
 * 		- modifica profilo
 * 		- logout
 */
public class App {
	private static String XMPPServerAddress = "localhost";
	private static String XMPPDomain = "@desktop-qi7gbpd.lan";
	private static int XMPPServerPort = 5222;
	private static boolean onChat = false;
	private static String onChatUsername;
	private static HashMap<String,Stack<Message>> incomingMessages = new HashMap<String,Stack<Message>>();
 
	public static void main(String[] args) throws SmackException, IOException, XMPPException, InterruptedException {
		
		// Create the configuration for this new connection
		XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
		configBuilder.setSecurityMode(SecurityMode.disabled);
		configBuilder.setPort(XMPPServerPort);
		configBuilder.setHost(XMPPServerAddress);
		configBuilder.setXmppDomain(XMPPDomain);
		AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());

		// Start Scanner
		Scanner reader = new Scanner(System.in);
		
		try {

			int userChoice = 0;
			boolean userLogged = false;
			String username;
			String password;

			do {
				// Connect to the server
				connection.connect();

				do {
					try {
						System.out.println("=========> UNIPR MESSENGER <=========");
						System.out.print("1) Login\n2) Registra account\nScelta: ");
						userChoice = reader.nextInt();
						reader.nextLine(); // Perchè nextInt() non legge "\n" e quindi viene letto da questo nextLine()
					} catch (Exception ex) {
						reader.nextLine();
						userChoice = 0;
					}
				} while (userChoice < 1 || userChoice > 2);

				if (userChoice == 1) {
					try {
						System.out.println("\n=========> LOGIN <=========");

						System.out.print("Username: ");
						username = reader.nextLine();

						System.out.print("Password: ");
						password = reader.nextLine();

						if (username.isEmpty() || password.isEmpty()) {
							throw new Exception("Username e/o Password non stati inseriti.");
						}

						// Log into the server
						connection.login(username, password);

						userLogged = true;

						System.out.println("Login effettuato correttamente.");
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (XMPPException ex) {
						System.out.println("\nERRORE DURANTE IL LOGIN.");
						System.out.println(ex.getMessage());
						System.out.println("Potrebbero essere stati inseriti username e/o password sbagliati.");
						connection.disconnect();
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (Exception ex) {
						System.out.println("\nERRORE DURANTE IL LOGIN.");
						System.out.println(ex.getMessage());
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();
					}

				} else {
					try {
						System.out.println("\n=========> REGISTRAZIONE ACCOUNT <=========");

						System.out.print("Username: ");
						username = reader.nextLine();

						System.out.print("Password: ");
						password = reader.nextLine();

						if (username.isEmpty() || password.isEmpty()) {
							throw new Exception("Username e/o Password non stati inseriti.");
						}

						AccountManager manager = AccountManager.getInstance(connection);

						Localpart user = Localpart.from(username);
						manager.createAccount(user, password);

						System.out.println("Account registrato correttamente.");
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (XMPPException ex) {
						System.out.println("ERRORE DURANTE LA REGISTRAZIONE DI UN ACCOUNT.");
						System.out.println(ex.getMessage());
						System.out.println("L'username inserito potrebbe essere già registrato.");
						connection.disconnect();
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (Exception ex) {
						System.out.println("\nERRORE DURANTE IL LOGIN.");
						System.out.println(ex.getMessage());
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();
					}
				}
			} while (!userLogged);

			// Assume we've created an XMPPConnection name "connection"._
			ChatManager chatManager = ChatManager.getInstanceFor(connection);

			chatManager.addIncomingListener(new IncomingChatMessageListener() {
				public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
					String senderUsername = from.getLocalpart().toString();
					if(onChat && onChatUsername.equals(senderUsername)) {
						System.out.println("New message from " + senderUsername + ": " + message.getBody());
					} else {
						if(incomingMessages.containsKey(senderUsername)) {
							incomingMessages.get(senderUsername).add(message);
						} else {
							incomingMessages.put(senderUsername, new Stack<Message>());
							incomingMessages.get(senderUsername).add(message);
						}
					}
				}
			});

			boolean logout = false;
			do {
				do {
					try {
						System.out.println("=========> MAIN MENU <=========");
						System.out.println("1) Aggiungi Utente\n2) Utenti Attivi\n3) Chat con utente");
						System.out.print("4) Modifica Profilo\n5) Logout\nScelta: ");
						userChoice = reader.nextInt();
						reader.nextLine(); // Perchè nextInt() non legge "\n" e quindi viene letto da questo nextLine()
					} catch (Exception ex) {
						reader.nextLine();
						userChoice = 0;
					}
				} while (userChoice < 1 || userChoice > 5);
				
				switch(userChoice) {
				case 1:
					break;
				case 2:
					break;
				case 3:
					System.out.print("Username Destinatario: ");
					String destUsername = reader.nextLine();
					EntityBareJid jid = JidCreate.entityBareFrom(destUsername + XMPPDomain);
					
					Chat chat = chatManager.chatWith(jid);
					chat.send("Hey, sono online!");
					
					System.out.println("Ora puoi mandare i messaggi a" + destUsername + ".\nInvia 'bye' per terminare la chat");
					onChat = true;
					onChatUsername = destUsername;
					String msg;
					do {
						msg = reader.nextLine();
						chat.send(msg);
					} while (!msg.contains("bye"));
					
					onChat = false;
					break;
				case 4:
					break;
				case 5:
					logout = true;
					break;
					
				default:
					System.out.println("Errore! Scelta non valida.");
					break;
				}
				
			} while (!logout);
			
			

			System.out.println("Premere un tasto per terminare la chat...");
			reader.nextLine();
			// Disconnect from the server
			connection.disconnect();
			System.out.println("Disconnected!");
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		} finally {
			reader.close();
		}
	}
}
