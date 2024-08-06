import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			// TODO: YOUR CODE HERE

			send(server.getSketch().toString());

			//for every shape in the sketch --> send a message to the client to add that shape

			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String line;
			while ((line = in.readLine()) != null) {
				//coming from buffered reader

				System.out.println("received >" + line);
				readWorld(line);

			}



			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * collects messages from each individual thread and processes the messages
	 * cannot be through the handlemessage class because handle message class
	 */

	public synchronized void readWorld(String input) throws IOException {
		if (input == null){ //message is invalid length
			System.err.println("Invalid command");
			return;
		}

		String[] tokens = input.split("\\s"); //message is invalid length
		if (tokens.length < 2){ //
			System.err.println("Invalid command");
			return;
		}

		// handle messages; essentially same functionality but with diff objects
		HandleMessage master = new HandleMessage(server);
		master.handleMessage(input);

	}




}
