package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jxmpp.stringprep.XmppStringprepException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI {

	private JFrame frame;
	private JTextField usernameField;
	private JTextField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws XmppStringprepException 
	 */
	public GUI() throws XmppStringprepException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws XmppStringprepException 
	 */
	private void initialize() throws XmppStringprepException {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Login button
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					App.connection.connect();
					try {
						App.loginGui(usernameField.getText(), passwordField.getText());
					} catch(Exception ex) {
						App.connection.disconnect();
						System.out.println("Errore in loginGui()");
					}
				} catch (Exception ex) {
					System.out.println("Errore in app.connection() " + ex.getMessage());
					System.out.println("Premere un tasto per continuare...");
				} finally {
					App.connection.disconnect();
				}
			}
		});
		loginButton.setBounds(165, 157, 114, 25);
		frame.getContentPane().add(loginButton);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(66, 55, 107, 33);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(66, 100, 123, 15);
		frame.getContentPane().add(lblPassword);
		
		usernameField = new JTextField();
		usernameField.setBounds(155, 55, 124, 19);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JTextField();
		passwordField.setBounds(155, 98, 124, 19);
		frame.getContentPane().add(passwordField);
		passwordField.setColumns(10);
	}
}
