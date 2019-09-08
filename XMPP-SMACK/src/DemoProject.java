import java.util.Scanner;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

public class DemoProject {

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
					
					AccountManager manager = con.getAccountManager();
					manager.createAccount("indismack", "provasmack");
					
					con.login("indismack", "provasmack");
					
					Chat chat = con.getChatManager().createChat("indi@desktop-qi7gbpd.lan", new MessageListener () {

						@Override
						public void processMessage(Chat chat, Message msg) {
							System.out.println(chat.getParticipant() + " said: " + msg.getBody());
							
						}
						
					});
					
					System.out.println("Connected");
					
					Scanner reader = new Scanner(System.in);
					while(con.isConnected()) {
						chat.sendMessage(reader.nextLine());
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
