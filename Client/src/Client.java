import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	private static String userName;
	static JFrame frame;
	static String Title = "Java Chat -Client-";
	static int width = 600;
	static int height = 400;
	static JTextField inputField;
	static JTextArea viewField;
	static JButton submit;
	static JScrollPane scrollPane;

	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;

	static String socketInput;

	private void requestInformation() { // Requests information from client user
		socketInput = JOptionPane.showInputDialog("Enter the IP of the server you are connecting to: ");
		userName = JOptionPane.showInputDialog("Enter your name: ");
	} // End requestInformation

	private void handShake() { // Establish connection with Server
		try {
			viewField.append("Connecting..." + "\n");
			socket = new Socket(socketInput, 7777);
			viewField.append("Connection successful..." + "\n");
			viewField.append("-------------------------\n");
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(userName);
			Input serverIn = new Input(in);
			Thread thread = new Thread(serverIn);
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createFrame() { // Create the GUI
		frame = new JFrame(Title + userName + "-");
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

		inputField.addActionListener(new ActionListener() { // Add actionListener to inputField (Enter)
					public void actionPerformed(ActionEvent e) {
						try {
							out.writeUTF(inputField.getText());
							viewField.append(">" + userName + ": " + inputField.getText() + "\n");
							inputField.setText("");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});

		submit.addActionListener(new ActionListener() { // Add actionListener to Submit (Click)
			public void actionPerformed(ActionEvent e) {
				try {
					out.writeUTF(inputField.getText());
					viewField.append(">" + userName + ": " + inputField.getText() + "\n");
					inputField.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public Client() { // Client Constructor
		requestInformation();
		createFrame();
		handShake();
	}

	public static void main(String[] args) {
		new Client();

	}
}

class Input implements Runnable { // Input class
	DataInputStream in; // InputStream (From server, created in Client)

	public Input(DataInputStream in) { // Constructor
		this.in = in; // Set class "in" to argument "in"
	} // End constructor

	public void run() { // Run method
		while (true) { // While loop
			try {
				Client.viewField.append(in.readUTF() + "\n"); // Add the message received from Server to the viewField
			} catch (IOException e) {
				System.out.println("Disconnected From Server");
				System.exit(0);
			}
		}
	}
}