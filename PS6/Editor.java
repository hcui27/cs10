import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

import javax.swing.*;

/**
 * Client-server graphical editor
 * @author helen cui
 *
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged


	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// TODO: YOUR CODE HERE
		//for [shape : sketch] --> draw previous sketches
		for (Integer id : sketch.getShapeList()){
			sketch.getShape(id).draw(g);
		}
		if (curr != null){
			curr.draw(g);
		}
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) { // initialize the x and y you are starting your drawing from
		// TODO: YOUR CODE HERE
		//start a new object
		if (mode == Editor.Mode.DRAW){
			//depending on what shape it is
			//if shape == set initial x, y or more
			drawFrom = p;
			moveFrom = p;
			if (Objects.equals(shapeType, "ellipse")){
				curr = new Ellipse(p.x, p.y, color);
			}
			else if (Objects.equals(shapeType, "freehand")){
				curr = new Polyline(p.x, p.y, color);
			}
			else if (Objects.equals(shapeType, "rectangle")){
				curr = new Rectangle(p.x, p.y, color);
			}
			else if (Objects.equals(shapeType, "segment")){
				curr = new Segment(p.x, p.y, color);
			}

		}

		//request to move

		if (mode == Editor.Mode.MOVE){
			movingId = sketch.getID(p.x, p.y);
			if (movingId != -1){
				moveFrom = p;
				curr = sketch.getShape(movingId);
				handleDrag(p);
			}}

			//request to recolor * how do you know
		else if (mode == Editor.Mode.RECOLOR){
				movingId = sketch.getID(p.x, p.y);
				if (movingId != -1){comm.recolor(movingId, color);}
			}

			//request to delete
		else if (mode == Editor.Mode.DELETE){
				movingId = sketch.getID(p.x, p.y);
				if (sketch != null && movingId != -1){
				comm.delete(movingId);}
			}
		repaint();
	}



	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {  //
		// TODO: YOUR CODE HERE

		//update the other corner --> how to do so?;
		if (mode == Editor.Mode.DRAW){
			if (curr != null){
				if (Objects.equals(shapeType, "ellipse")){
					((Ellipse) curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				}
				else if (Objects.equals(shapeType, "freehand")){ //add a point for each
					( (Polyline) curr).addCoord(p.x, p.y); //double check if this makes sense w framework
				}
				else if (Objects.equals(shapeType, "rectangle")){
					((Rectangle) curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
				}
				else if (Objects.equals(shapeType, "segment")){
					((Segment) curr).setEnd(p.x, p.y);
				}}
		}

		//request to drag
		else if (mode == Editor.Mode.MOVE){
			if (curr != null && moveFrom != null && curr.contains(p.x, p.y)) {
				//send dx and dy in my request
				int dx = p.x - moveFrom.x;
				int dy = p.y - moveFrom.y;
				comm.move(movingId, dx, dy);
				moveFrom = p;
				repaint();
			}

		}
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() { //send a message to add  --> sets what its going to be
		// TODO: YOUR CODE HERE
		//sends message
		//add new object request
		if (mode == Editor.Mode.DRAW){
			comm.add(curr);
		}

		// "release" when moving
		curr = null;
		repaint();

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
