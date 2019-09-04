import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UdpClient {
	
	private String serverAddress = "192.168.1.137";
	private String destinationName = "";
	private String name = "windows";
	
	//Ports
	private int sendPort = 4001;
	private int receivePortServer = 4002;
	private int receivePortClient = 4003;
	
	//Buffer receiver
	byte[] buffer = new byte[1000];
	
	//Constructor
	public UdpClient() {
	}
	
	public connectToServer() {
	}
	
	public void Menu() throws IOException{
		
		System.out.println("Comando: ");
		
		String option = new Scanner(System.in).next();
		
		while(!option.equals("close")) {
			option = option.toLowerCase();
			
			switch(option) {
			case "send":	case "sender":		sendMessage();		break;
			case "receive":						receiveMessage();	break;
			}
			
			option = new Scanner(System.in).next();
			
		}
		
	}
	
	public void sendMessage() throws IOException {
		
		System.out.println("------------- UDP SENDER -------------");
		
		// 1) Set recipient and Send a client hello to server with that particular recipient
		// 2) Send message
		// 3) Bye
		
		DatagramSocket udp = new DatagramSocket();
		
		// 1) Client hello
		String message = "Client hello:" + destinationAddress;
		byte[] data = message.getBytes();
		
		DatagramPacket clientHello = new DatagramPacket(data, data.length);
		clientHello.setAddress(Inet4Address.getByName(serverAddress));
		clientHello.setPort(receivePortServer);
		udp.send(clientHello);
						
		
		// 2) Messages
		System.out.println("Type your text: ");
		Scanner text = new Scanner(System.in);
		message = text.nextLine();
		
		while(!message.equals("close")) {
			
			data = message.getBytes();
			
			//Set up packet
			DatagramPacket packet = new DatagramPacket(data, data.length);
			packet.setAddress(Inet4Address.getByName(serverAddress));
			packet.setPort(receivePortServer);
			
			//Sending packet
			udp.send(packet);
			
			//New message -- "close" to finish
			text = new Scanner(System.in);
			message = text.nextLine();
			
		}
		
		//3) Bye
		message = "Bye: ";
		
	}
	
	public void receiveMessage() throws IOException {
		
		DatagramSocket udp = new DatagramSocket(receivePortClient);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		System.out.println("------------- UDP RECEIVER -------------");
		
		while(true) {
			
			//Receiver
			udp.receive(packet);
			
			//Getting informations from the packet received
			String message = new String(packet.getData(), packet.getOffset(), packet.getLength());
			
			String sourceAddress = packet.getAddress().getHostAddress()+":"+packet.getPort();
			
			//Printing message received
			System.out.println("From "+sourceAddress+": "+message);
			
		}
	}

	public static void main(String[] args) throws IOException {
			
			//Creating an instance of UdpClient class
			UdpClient client = new UdpClient();
			
			//Running Client
			client.Menu();
		}
}
