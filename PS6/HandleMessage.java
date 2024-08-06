import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class HandleMessage {
    private Editor editor;
    private Shape addNew;
    private SketchServer server;

    //initialize
    public HandleMessage(Editor editor){ //initializes a handlemessage for editor functionality
        this.editor = editor;
        server = null;
        addNew = null;
    }

    public HandleMessage(SketchServer server){ //initializes a handlemessage for server functionality
        this.server = server;
        editor = null;
        addNew = null;
    }

    /**
     * calls helper functions
     * @throws IOException
     */

    public synchronized void handleMessage(String input) throws IOException {
        String line = input;
        String[] tokens = line.split("\\s");
        String action = tokens[0];

        if (action.equals("add")){
            //add shape to mastersketch
            handleAdd(tokens);

        }

        if (action.equals("move")){
            //call moveby action on the ID
            handleMove(tokens, line);

        }

        if (action.equals("recolor")){
            //color
            handleRecolor(tokens, line);

        }

        if (action.equals("delete")){
            //remove from shapelist right
            handleDelete(tokens, line);

        }

        if (action.equals("sketch")){
            handleSketch(line);
        }

    }

    /**
     * depending on what what shape it is; make the new shape that shape
     * add to the shape list
     * @param tokens
     */
    public synchronized void handleAdd(String[] tokens){
        for (String s: tokens){
            System.out.println(s);
        }
        if (tokens.length < 6) {return;} //there are no shapes in substring

        String shape = tokens[1];
        Color newColor = new Color(Integer.parseInt(tokens[tokens.length - 2]));


        if (shape.equals("ellipse")){
            addNew = new Ellipse(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), //x1, y1
                    Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), newColor); //x2,y2
        }

        if (shape.equals("rectangle")){
            addNew = new Rectangle(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), //x1, y1
                    Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), newColor); //x2, y2, color
        }

        if (shape.equals("freehand")){
            int x = 2; //index of x in tokens
            int y = 3; //index of y in tokens

            addNew = new Polyline(Integer.parseInt(tokens[x]), Integer.parseInt(tokens[y]), newColor); //x1, y1;x2, y2, color
            //might be odd rn tbh
            for (int i = 2; i < tokens.length - 3; i+= 2){
                x = i;
                y = i+1;
                ((Polyline) addNew).addCoord(Integer.parseInt(tokens[x]), Integer.parseInt(tokens[y]));
            }
        }

        if (shape.equals("segment")){
            addNew = new Segment(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), //x1, y1
                    Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5]), newColor); //x2, y2, color
        }

        if(addNew != null){
            System.out.println("addNew");
            if (server == null){ // EDITOR not using for server functionality
                int id = editor.getSketch().size(); //moving id (master)
                editor.getSketch().addShape(id, addNew);
                editor.repaint();
            }

            else if (editor == null){ // EDITOR not using for server functionality
                int id = server.getSketch().size(); //moving id (master)
                server.getSketch().addShape(id, addNew);
                server.broadcast("add " + addNew.toString() + " -1"); //broadcasts addNew message
            }

        }

    }
    /**
     * handles moving for both editor and sketchserver
     * @param tokens
     * @param line
     */


    public synchronized void handleMove(String[] tokens, String line){
        if (!(tokens.length < 5)){ //tokens is less than 5
            if (server == null){ //editor mode
                editor.getSketch().getShape(Integer.parseInt(tokens[1])).
                        moveBy(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3])); //dx,dy
        }
            if (editor == null){ //server mode
                server.getSketch().getShape(Integer.parseInt(tokens[1])).
                        moveBy(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3])); //dx,dy
                server.broadcast(line); //tells every editor to do the action
            }
            }
    }

    /**
     * handles recoloring for both editor and sketchserver
     * @param tokens
     * @param line
     */
    public synchronized void handleRecolor(String[] tokens, String line){
        Color newColor = new Color(Integer.parseInt(tokens[2])); //newcolor
        if (server == null){ //editor mode
            editor.getSketch().getShape(Integer.parseInt(tokens[1])).setColor(newColor);
        }
        if (editor == null){ //server mode
            server.getSketch().getShape(Integer.parseInt(tokens[1])).setColor(newColor);
            server.broadcast(line);
        }

    }
    /**
     * handles deleting for both editor and sketchserver
     * @param tokens
     * @param line
     */

    public synchronized void handleDelete(String[] tokens, String line){
        if (server == null){editor.getSketch().remove(Integer.parseInt(tokens[1]));}
        if (editor == null){
            server.getSketch().remove(Integer.parseInt(tokens[1]));
            server.broadcast(line);
        }
    }

    /**
     * handles the message coming out of the sketch
     * @param line
     */
    public synchronized void handleSketch(String line){
        String shapeList = line.substring(line.indexOf("{")+1, line.indexOf("}")); // get the list of shapes from the message
        String[] shapes = shapeList.split(","); // split the list of shapes by comma and space
        for (String word : shapes){ // loop through the list of shapes
            String substring = word.substring(1);// get the substring of the line
            String[] shape = substring.split("\\s"); //split into substrings
            handleAdd(shape); // add the shape to the editor

    }

}}
