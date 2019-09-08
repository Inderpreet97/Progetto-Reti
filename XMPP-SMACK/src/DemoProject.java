import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class DemoProject {
	
	public static void clearScreen() {  
	    System.out.print("\033[H\033[2J");  
	    System.out.flush();  
	   }

	public static void main(String[] args) {
		
		new Thread() {
			
			public void run() {
				
				try {
					ConnectionConfiguration config = new ConnectionConfiguration("127.0.0.1", 5222);
					config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
					config.setDebuggerEnabled(false);
					config.setSendPresence(true);
					
					XMPPConnection con = new XMPPConnection(config);
					con.connect();
					
					
					System.out.println("=========> LOGIN <=========");
					
					System.out.print("Username: ");
					Scanner reader = new Scanner(System.in);
					String username = reader.nextLine();
					
					System.out.print("Password: ");
					String password = reader.nextLine();
										
					AccountManager manager = con.getAccountManager();
					manager.createAccount(username, password);
					
					con.login(username, password);
					
					clearScreen();
					
					System.out.println("=========> LOGIN <=========");
					
					System.out.print("Username destinatario:  ");
					String destUsername = reader.nextLine() + "@desktop-qi7gbpd.lan" ;  // username + dominio XMPP
					
					
					Chat chat = con.getChatManager().createChat(destUsername, new MessageListener () {

						@Override
						public void processMessage(Chat chat, Message msg) {
							System.out.println(chat.getParticipant() + " said: " + msg.getBody());
							
						}
						
					});
					
					System.out.println("Connected");
					
					
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
		}.start();
	}

}
