package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.awt.EventQueue;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jivesoftware.smack.roster.RosterEntry;

import progettoreti.Maven_Smack4.progettoreti.Maven_Smack4.Application.App;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class HomePage {

	private JFrame frame;
	private JTextField addFriendField;
	private JPanel header;
	private JPanel main;
	private JPanel footer;
	/* TODO private ArrayList<JPanel> friendListPanels = new ArrayList<JPanel>(); */

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomePage window = new HomePage();
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
	public HomePage() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 432, 0 };
		gridBagLayout.rowHeights = new int[] { 45, 161, 35, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		// Header Panel contente la barra di ricerca
		header = new JPanel();
		GridBagConstraints gbc_header = new GridBagConstraints();
		gbc_header.insets = new Insets(5, 5, 5, 5);
		gbc_header.gridx = 0;
		gbc_header.gridy = 0;
		frame.getContentPane().add(header, gbc_header);
		GridBagLayout gbl_header = new GridBagLayout();
		gbl_header.columnWidths = new int[] { 333, 106, 0 };
		gbl_header.rowHeights = new int[] { 0 };
		gbl_header.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_header.rowWeights = new double[] { 0.0 };
		header.setLayout(gbl_header);

		// TextField -> Barra di ricerca utenti
		addFriendField = new JTextField();
		addFriendField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		addFriendField.setText("Type the friend to add");
		addFriendField.setToolTipText("Type the friend to add");
		GridBagConstraints gbc_addFriendField = new GridBagConstraints();
		gbc_addFriendField.fill = GridBagConstraints.BOTH;
		gbc_addFriendField.insets = new Insets(0, 0, 0, 5);
		gbc_addFriendField.gridx = 0;
		gbc_addFriendField.gridy = 0;
		header.add(addFriendField, gbc_addFriendField);
		addFriendField.setColumns(10);

		// Button -> Add Friend
		JButton addFriendButton = new JButton("Add Friend");
		GridBagConstraints gbc_addFriendButton = new GridBagConstraints();
		gbc_addFriendButton.fill = GridBagConstraints.BOTH;
		gbc_addFriendButton.gridx = 1;
		gbc_addFriendButton.gridy = 0;
		header.add(addFriendButton, gbc_addFriendButton);

		// Main Panel Contente la lista Amici
		main = new JPanel();
		main.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		main.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_main = new GridBagConstraints();
		gbc_main.fill = GridBagConstraints.BOTH;
		gbc_main.insets = new Insets(5, 5, 5, 5);
		gbc_main.gridx = 0;
		gbc_main.gridy = 1;
		frame.getContentPane().add(main, gbc_main);
		
		// TODO Working on JPanel and JLabels
		// TODO Sistemare bori friendPanel, Aggiungere spazio tra un FriendPanel e l'altro
		App.updateFriendList();
		Collection<RosterEntry> friendEntries = App.friendList;

		if (!friendEntries.isEmpty()) {
			for (RosterEntry entry : friendEntries) {
				
				// Creo un JPanel per ogni amico
				JPanel friendPanel = new JPanel();
				friendPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
				friendPanel.setAlignmentY(Component.TOP_ALIGNMENT);
				friendPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
				main.add(friendPanel);
				
				// Inserisco nel JPanel diversi Label per stampare le info sull'amico
				// Friend Name
				JLabel nomeLabel = new JLabel(entry.getName());
				nomeLabel.setAlignmentX(new Float(0.0));
				friendPanel.add(nomeLabel);
				
				// Username Name
				JLabel usernameLabel = new JLabel(entry.getJid().getLocalpartOrNull().toString());
				usernameLabel.setAlignmentX(new Float(0.5));
				friendPanel.add(usernameLabel);
				
				// Stato Name
				String stato = App.roster.getPresence(entry.getJid()).getType().toString();
				JLabel statoLabel = new JLabel(stato);
				statoLabel.setAlignmentX(new Float(1));
				friendPanel.add(statoLabel);
				
				friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.X_AXIS));
				
			}
		} else {
			System.out.println("Lista amici vuota!");
		}

		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));		

		// Footer Panel contente Button Logout
		footer = new JPanel();
		FlowLayout fl_footer = (FlowLayout) footer.getLayout();
		fl_footer.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_footer = new GridBagConstraints();
		gbc_footer.anchor = GridBagConstraints.EAST;
		gbc_footer.gridx = 0;
		gbc_footer.gridy = 2;
		frame.getContentPane().add(footer, gbc_footer);

		// Button -> Logout
		JButton logOutButton = new JButton("Log out");
		footer.add(logOutButton);

		// Logout button listener
		logOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					App.disconnect();
					System.out.println("Disconnected");
					System.exit(0);
					// Return to login page
				} catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});

		// Add Friend Button Listener
		addFriendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Your name is " + App.loggedUsername);
				try {

					// NON AGGIUNGE L'AMICO

					String friendUsername = addFriendField.getText();

					// Check if the friendFieldText is not empty
					if (!friendUsername.equals("")) {

						// Check if the friend to add is not already in the user friend list
						if (!App.checkIfUserInFriendList(friendUsername) || App.friendList.isEmpty()) {

							if (App.addFriend(addFriendField.getText())) {
								JOptionPane.showMessageDialog(null, "User has been added to friend list");
							} else {
								JOptionPane.showMessageDialog(null, "User not found");
							}

						} else {
							JOptionPane.showMessageDialog(null, "User is already in list");
						}

					} else {
						// Empty text in friend field
						JOptionPane.showMessageDialog(null, "Please enter a correct the name");
					}
				} catch (Exception ex) {
					System.out.println("ERRORE" + ex.getMessage());
				}
			}
		});

		// Listener bottone X per chiusura finestre
		frame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit", "Close window",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					App.disconnect();
					System.exit(0);
				}
			}

		});

		/* Getting the friend list
		int dim = -1;
		try {

			// !!!! PROBABILMENTE LA LISTA ENTRIES RIMANE VUOTA E LANCIA L'ECCEZIONE

			Collection<RosterEntry> entries = App.friendList;

			if (!entries.isEmpty()) {
				for (RosterEntry entry : entries) {
					System.out.print("Nome: " + entry.getName() + "\t");
					System.out.print("Username: " + entry.getJid().getLocalpartOrNull() + "\t");
					System.out.println("Stato: " + App.roster.getPresence(entry.getJid()).getType());
				}
			} else {
				System.out.println("Lista amici vuota!");
			}

			
			  if (!entries.isEmpty()) { JLabel lblWelcomeToChat = new
			  JLabel("List friends: " + Integer.toString(entries.size()));
			  lblWelcomeToChat.setBounds(140, 108, 180, 43);
			  frame.getContentPane().add(lblWelcomeToChat); } else { JLabel
			  lblWelcomeToChat = new JLabel("List friends: " +
			  Integer.toString(entries.size())); lblWelcomeToChat.setBounds(140, 108, 180,
			  43); frame.getContentPane().add(lblWelcomeToChat); }
			 
		} catch (Exception ex) {
			System.out.println("Errore: " + ex.getMessage());
			System.out.println(Integer.toString(dim));
		}
		
		*/

	}
}
