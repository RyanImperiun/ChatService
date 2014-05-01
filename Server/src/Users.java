import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

class Users implements Runnable { // Users class
	DataOutputStream out;
	DataInputStream in;
	Users[] user = new Users[5];
	String name;
	int userID;

	public Users(DataOutputStream out, DataInputStream in, Users[] user) {
		this.out = out;
		this.in = in;
		this.user = user;
	}

	public void run() {
		try {
			name = in.readUTF();
			Server.reRouteMessage("has entered the chat.", this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				Server.reRouteMessage(in.readUTF(), this); // Re-route the message received from the Client to other Clients via Server
				Server.viewField.append(name + ": " + in.readUTF() + "\n"); // Add whatever the message is that was received to the Server-GUI viewfield
			} catch (IOException e) {
				this.out = null;
				this.in = null;
				user[userID] = null;
			}
		}
	}
}