package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;
import java.io.IOException;
import java.util.Scanner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

public class App 
{
    public static void main( String[] args ) throws SmackException, IOException, XMPPException, InterruptedException
    {
    	// Start Scanner
    	Scanner reader = new Scanner(System.in);
    	
    	// Create the configuration for this new connection
    	XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
    	configBuilder.setUsernameAndPassword("indi", "indi123");
    	configBuilder.setSecurityMode(SecurityMode.disabled);
    	configBuilder.setHost("localhost");
    	configBuilder.setXmppDomain("@desktop-qi7gbpd.lan"); //"desktop-qi7gbpd.lan"
   	

    	AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());
    	// Connect to the server
    	connection.connect();
    	// Log into the server
    	connection.login();

    	System.out.println( "Connected!" );
    	
		System.out.println("Premere un tasto per continuare...");
		reader.nextLine();
		
    	// Disconnect from the server
    	connection.disconnect();
    	
    	System.out.println( "Disconnected!" );
        
    }
}
