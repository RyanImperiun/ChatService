import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	static JFrame			frame;
	static String			Title		= "Java Chat -Server-";
	static int				width		= 600;
	static int				height		= 400;
	static JTextField		inputField;
	static JTextArea		viewField;
	static JButton			submit;
	JScrollPane				scrollPane;

	static ServerSocket		serverSocket;
	static DataOutputStream	out;
	static DataInputStream	in;
	static Socket			socket;
	static Users[]			maxUsers	= new Users[5];

	static boolean			serverHostHasName;
	static String			serverName;

	private void requestInformation() {
		String nameOption = JOptionPane.showInputDialog("Would you like a name, or remain as 'Server'? (Y/N)");
		if (nameOption.equals("Y")) { // If answers "Y", or Yes
			serverName = JOptionPane.showInputDialog("What is your name?");
			serverHostHasName = true;
		} else {
			serverHostHasName = false;
		}
	} // End requestInformation

	private void handShake() { // Establish connection & create user(s)
		try {
			viewField.append("Starting Server...\n");
			serverSocket = new ServerSocket(7777);
			viewField.append("Server Started...\n");
			viewField.append("-------------------------\n"); // Line to break up server-starting info from actual messages
			while (true) { // While loop
				socket = serverSocket.accept();
				viewField.append("Connection from: " + socket.getInetAddress() + "\n");
				for (int i = 0; i < maxUsers.length; i++) {
					out = new DataOutputStream(socket.getOutputStream());
					in = new DataInputStream(socket.getInputStream());
					if (maxUsers[i] == null) {
						maxUsers[i] = new Users(out, in, maxUsers);
						maxUsers[i].userID = i;
						Thread userThread = new Thread(maxUsers[i]);
						userThread.start();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMessage(String message) throws IOException {
		if (message != "" && message != null) { // Check if there is actually a message
			if (serverHostHasName) {
				viewField.append(">Server[" + serverName + "]: " + message + "\n");
			} else {
				viewField.append(">Server: " + message + "\n");
			}
			for (int i = 0; i < maxUsers.length; i++) {
				if (maxUsers[i] != null) {
					if (serverHostHasName) {
						maxUsers[i].out.writeUTF("Server[" + serverName + "]: " + message);
					} else {
						maxUsers[i].out.writeUTF("Server:" + message);
					}
				}
			}
		}
	}

	public static void reRouteMessage(String message, Users currentUser) throws IOException { // Used to bounce messages from a client to other clients
		if (message != "" && message != null) {
			viewField.append(currentUser.name + ": " + message + "\n");
			for (int i = 0; i < maxUsers.length; i++) {
				if (maxUsers[i] != null && maxUsers[i] != currentUser) // If that user exists and is not the user who sent the message
					maxUsers[i].out.writeUTF(currentUser.name + ": " + message);
			}
		}
	}

	private void createFrame() {
		if (serverHostHasName) {
			frame = new JFrame(Title + serverName + "-");
		} else {
			frame = new JFrame(Title);
		}

		viewField = new JTextArea(20, 50);
		viewField.setEditable(false);
		scrollPane = new JScrollPane(viewField);
		inputField = new JTextField(42);
		submit = new JButton("Submit");
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		frame.add(scrollPane);
		frame.add(inputField);
		frame.add(submit);

		inputField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessage(inputField.getText());
					inputField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sendMessage(inputField.getText());
					inputField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public Server() {
		requestInformation();
		createFrame();
		handShake(); // Boiler stuff to establish connections
	}

	public static void main(String[] args) {
		new Server();
	}
}