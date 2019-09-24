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
    	
    	// Create the configuration for this new connection
    	XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
    	configBuilder.setUsernameAndPassword("beppe", "beppe123");
    	//configBuilder.setResource("SomeResource");
    	configBuilder.setSecurityMode(SecurityMode.disabled);
    	configBuilder.setPort(5222);
    	configBuilder.setHost("160.78.162.44");
    	configBuilder.setXmppDomain("@desktop-qi7gbpd.lan"); //"desktop-qi7gbpd.lan"
    	
    	AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());
    	
    	try {
    		// Start Scanner
        	Scanner reader = new Scanner(System.in);          	   	
       	
        	// Connect to the server
        	connection.connect();
        	// Log into the server
        	connection.login();

        	System.out.println( "Connected!" );
        	
    		System.out.println("Premere un tasto per continuare...");
    		reader.nextLine();
    		
        	
        	
        	System.out.println( "Disconnected!" );
    	}catch(Exception ex) {
    		System.out.println(ex.getMessage());
    	}finally {
    		// Disconnect from the server
        	connection.disconnect();
    	}
        
    }
}
