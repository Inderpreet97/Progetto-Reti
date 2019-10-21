import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;

public class Server {
	
	//Ports
	private int sendPort = 4001;
	private int receivePortServer = 4002;
	private int receivePortClient = 4003;
	
	//UdpSockets
	DatagramSocket udpReceive;
	DatagramSocket udpSender;
	DatagramPacket packet;
	String currentdDestinationAddress = "";
	
	//Buffer receiver
	byte[] buffer = new byte[1000];
	
	//Table
	HashMap <String, String> table = new HashMap <String, String>();
	
	public void sendMessage(DatagramPacket packet, String destinationAddress) throws IOException {

		System.out.println("VOGLIO INVIARE");
		
		udpSender = new DatagramSocket();
		
		System.out.println("From: " +packet.getSocketAddress());
		packet.setAddress(Inet4Address.getByName(destinationAddress));
		packet.setPort(receivePortClient);
		
		//Sending packet
		udpSender.send(packet);
		udpSender.close();
		
		receiveMessage();
		
	}
	
	public void receiveMessage() throws IOException {
		
		udpReceive = new DatagramSocket(receivePortServer);
		
		packet = new DatagramPacket(buffer, buffer.length);
		
		System.out.println("------------- UDP SERVER -------------");
		
		while(true) {
			
			System.out.println("SONO IN RICEZIONE");
			
			//Receiver
			udpReceive.receive(packet);
			
			//Getting informations from the packet received
			String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
			String sourceAddress = packet.getAddress().getHostAddress()+":"+packet.getPort();
			
			
			if (message.startsWith("Client hello")) {
				//Printing message received
				System.out.println("Client hello -> : From "+sourceAddress+": "+message);
				boolean copy = false;
				for (int i=0; i<message.length(); i++) {
					//Get destination address
					if (copy) currentdDestinationAddress += message.charAt(i);
					if ( message.charAt(i) == ':') copy = true;	
				}
				
				System.out.println("Destination address: " + currentdDestinationAddress);
				table.put(sourceAddress, currentdDestinationAddress);
				
				
			}else {
				//Printing message received
				
				if (table.containsKey(sourceAddress)) {
					//destinationAddress = table.get(sourceAddress);
					System.out.println("Devo inviare a "+ table.get(sourceAddress));
					udpReceive.close();
					break;
					
				}else {
					System.out.println("ERRORE");
				}
				
			}
			
			
		}
		
		sendMessage(packet, currentdDestinationAddress);
		
	}
	
	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.receiveMessage();
		//server.sendMessage("192.168.1.83");

	}

}
