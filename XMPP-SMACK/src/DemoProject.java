import java.util.Iterator;
import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

public class DemoProject {
	private static String XMPPServerAddress = "127.0.0.1";
	private static int XMPPServerPort = 5222;

	public static void main(String[] args) {

		try {
			Scanner reader = new Scanner(System.in);

			ConnectionConfiguration config = new ConnectionConfiguration(XMPPServerAddress, XMPPServerPort);
			config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
			config.setDebuggerEnabled(false);
			config.setSendPresence(true);

			XMPPConnection con = new XMPPConnection(config);

			int userChoice = 0;
			boolean userLogged = false;
			String username;
			String password;

			do {
				con.connect();
				
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


				if(userChoice == 1) {
					try {
						System.out.println("\n=========> LOGIN <=========");

						System.out.print("Username: ");
						username = reader.nextLine();

						System.out.print("Password: ");
						password = reader.nextLine();

						if(username.isEmpty() || password.isEmpty()) {
							throw new Exception("Username e/o Password non stati inseriti.");
						}

						con.login(username, password);

						userLogged = true;

						System.out.println("Login effettuato correttamente.");
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (XMPPException ex) {
						System.out.println("\nERRORE DURANTE IL LOGIN.");
						System.out.println(ex.getMessage());
						System.out.println("Potrebbero essere stati inseriti username e/o password sbagliati.");
						con.disconnect();
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
						
						if(username.isEmpty() || password.isEmpty()) {
							throw new Exception("Username e/o Password non stati inseriti.");
						}
						

						AccountManager manager = con.getAccountManager();
						manager.createAccount(username, password);

						System.out.println("Account registrato correttamente.");
						System.out.println("Premere un tasto per continuare...");
						reader.nextLine();

					} catch (XMPPException ex) {
						System.out.println("ERRORE DURANTE LA REGISTRAZIONE DI UN ACCOUNT.");
						System.out.println(ex.getMessage());
						System.out.println("L'username inserito potrebbe essere già registrato.");
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

			System.out.print("Username destinatario:  ");

			String destUsername = reader.nextLine();  // username + dominio XMPP

			UserSearchManager search = new UserSearchManager(con);
			Form searchForm = search.getSearchForm("search." + con.getServiceName());

			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", destUsername);
			ReportedData data = search.getSearchResults(answerForm, "search." + con.getServiceName());

			if (data.getRows() != null) {
				Iterator<Row> rowIterator = data.getRows();

				while(rowIterator.hasNext()) {
					System.out.println(rowIterator.next());
				}

			}

			System.out.println("NON TROVATO");

			Chat chat = con.getChatManager().createChat(destUsername + "@desktop-qi7gbpd.lan" , new MessageListener () {

				@Override
				public void processMessage(Chat chat, Message msg) {
					System.out.println(chat.getParticipant() + " said: " + msg.getBody());
				}
			});


			while(con.isConnected()) {
				String msg = reader.nextLine();
				chat.sendMessage(msg);
				System.out.println("You said: " + msg);
			}
			reader.close();

		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
