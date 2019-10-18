package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.awt.EventQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class ChatPage {

	private JFrame frame;
	private JTextField addFriendField;

	/**
	 * Launch the application.
	 */
	public void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatPage window = new ChatPage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatPage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Logout
		JButton logOutButton = new JButton("Log out");
		logOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Application.disconnect();
					System.out.println("Disconnected");
					// Return to login page
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		logOutButton.setBounds(276, 222, 114, 36);
		frame.getContentPane().add(logOutButton);
		
		// Add Friend
		JButton addFriendButton = new JButton("Add Friend");
		addFriendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Application.addFriend(addFriendField.getText())) {
						JOptionPane.showMessageDialog(null, "User has been added to friend list");
					} else {
						JOptionPane.showMessageDialog(null, "User not found");
					}
				} catch (Exception ex) {
					// System.out.println(ex.getMessage());
					System.out.println("Cazzo non funziona");
				}
			}
		});
		addFriendButton.setBounds(276, 12, 114, 25);
		frame.getContentPane().add(addFriendButton);
		
		addFriendField = new JTextField();
		addFriendField.setText("Type the friend to add");
		addFriendField.setBounds(12, 15, 252, 19);
		frame.getContentPane().add(addFriendField);
		addFriendField.setColumns(10);
		
		
		// Getting the friend list
		int dim = -1;
		try {
			
			// !!!! PROBABILMENTE LA LISTA ENTRIES RIMANE VUOTA E LANCIA L'ECCEZIONE
			
			Collection<RosterEntry> entries = Application.getFriendList();
			if (!entries.isEmpty()) {
				JLabel lblWelcomeToChat = new JLabel("List friends: " + Integer.toString(entries.size()));
				lblWelcomeToChat.setBounds(140, 108, 180, 43);
				frame.getContentPane().add(lblWelcomeToChat);
			} else {
				JLabel lblWelcomeToChat = new JLabel("List friends: " + Integer.toString(entries.size()));
				lblWelcomeToChat.setBounds(140, 108, 180, 43);
				frame.getContentPane().add(lblWelcomeToChat);
			}
		} catch (Exception ex) {
			System.out.println("Errore" + ex.getMessage());
			System.out.println(Integer.toString(dim));
		}
		
		
	}
}
