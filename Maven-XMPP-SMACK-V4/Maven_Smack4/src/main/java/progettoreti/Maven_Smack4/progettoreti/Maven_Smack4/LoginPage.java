package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.stringprep.XmppStringprepException;

import progettoreti.Maven_Smack4.progettoreti.Maven_Smack4.Application.App;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class LoginPage {

	private JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginPage window = new LoginPage();
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
	public LoginPage() throws XmppStringprepException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws XmppStringprepException 
	 */
	private void initialize() throws XmppStringprepException{
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Username
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(98, 71, 105, 28);
		frame.getContentPane().add(lblUsername);
		
		// Password
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(98, 131, 66, 15);
		frame.getContentPane().add(lblPassword);
		
		usernameField = new JTextField();
		usernameField.setBounds(223, 76, 124, 19);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);	
		
		// Login button
		JButton loginButton = new JButton("Login");
		// Application application = new Application(); 
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					
					// Connection and login
					App.connect();
					App.login(usernameField.getText(), new String(passwordField.getPassword()));
					//App.logged = true
					
				} catch (XMPPException | SmackException | IOException | InterruptedException e) {
					// Disconnecting
					App.disconnect();
					System.out.println("Login NOT corrected");
				}
				
				if (App.logged) {
					System.out.println("Login corrected");
					frame.dispose();
					HomePage homePage = new HomePage();
					App.loggedUsername = usernameField.getText();
					homePage.main();
				}else {
					JOptionPane.showMessageDialog(null, "Wrong username or password");
				}
			}
		});
		loginButton.setBounds(233, 188, 114, 25);
		frame.getContentPane().add(loginButton);
		
		// Sign in button
		SignInPage signInPage = new SignInPage();
		JButton signInButton = new JButton("Sign in");
		signInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
				signInPage.run();
			}
		});
		signInButton.setBounds(89, 188, 114, 25);
		frame.getContentPane().add(signInButton);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(223, 129, 124, 19);
		frame.getContentPane().add(passwordField);
	}
}
