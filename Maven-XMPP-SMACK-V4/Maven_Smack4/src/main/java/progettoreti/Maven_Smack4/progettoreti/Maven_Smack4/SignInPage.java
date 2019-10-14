package progettoreti.Maven_Smack4.progettoreti.Maven_Smack4;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jxmpp.stringprep.XmppStringprepException;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SignInPage {

	private JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;

	/**
	 * Launch the application.
	 */
	public void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignInPage window = new SignInPage();
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
	public SignInPage() throws XmppStringprepException {
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
		
		JLabel lblChooseAUsername = new JLabel("Choose a username");
		lblChooseAUsername.setBounds(62, 41, 133, 27);
		frame.getContentPane().add(lblChooseAUsername);
		
		JLabel lblSelectAPassword = new JLabel("Select a password");
		lblSelectAPassword.setBounds(62, 80, 139, 15);
		frame.getContentPane().add(lblSelectAPassword);
		
		JLabel lblConfirmPassword = new JLabel("Confirm password");
		lblConfirmPassword.setBounds(62, 107, 133, 17);
		frame.getContentPane().add(lblConfirmPassword);
		
		usernameField = new JTextField();
		usernameField.setBounds(237, 45, 124, 19);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(237, 77, 124, 19);
		frame.getContentPane().add(passwordField);
		
		confirmPasswordField = new JPasswordField();
		confirmPasswordField.setBounds(237, 106, 124, 19);
		frame.getContentPane().add(confirmPasswordField);
		
		Application application = new Application();
		
		JButton signInButton = new JButton("Sign in");
		signInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String username = usernameField.getText();
				String password = new String(passwordField.getPassword());
				try {
					application.singIn(username, password);
					JOptionPane.showMessageDialog(null, "Signed UP!");
					// LoginPage.main();
				} catch (XmppStringprepException | NoResponseException | XMPPErrorException | NotConnectedException
						| InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		signInButton.setBounds(154, 169, 114, 25);
		frame.getContentPane().add(signInButton);
		
	}
}
