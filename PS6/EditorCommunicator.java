import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	} //helper for my messages


	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// TODO: YOUR CODE HERE]
			//call your handlemessage method == create object
			String line;
			while ((line = in.readLine()) != null){
				System.out.println("message >" + line);
				HandleMessage parse = new HandleMessage(editor);
				parse.handleMessage(line);
				editor.repaint();
			}

			//pass the sketch; pass the line that it's going to handle
			//repaint()

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	

	// Send editor requests to the server
	// TODO: YOUR CODE HERE


	// add a stop (-1) to know when  you stop reading
	// diff methods : add, delete, recolor,
	//--> use send method in order to

	public void add(Shape curr){
		send("add "+ curr.toString() + " -1");}

	public void move(int movingID, int dx, int dy){
		send("move "+ movingID + " "+ dx + " "+ dy + " -1");}

	public void recolor(int movingID, Color color){
		send("recolor "+ movingID + " "+ color.getRGB()+ " -1");
	}

	public void delete(int movingID){
		send("delete "+ movingID + " -1");
	}
}
