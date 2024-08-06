import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author helen cui
 * holds the list of shapes
 */
public class  Sketch {
    public TreeMap<Integer, Shape> idShape;
    public Shape curr;

    public Sketch(){
        idShape = new TreeMap<>();
    }

    // returns the shape
    public synchronized Shape getShape(int ID){
        return idShape.get(ID);}

//returns the shape id depending on where you click in frame
    synchronized Integer getID(int x, int y){
        for (Integer key: idShape.keySet()){
            if (idShape.get(key).contains(x,y)) {
                return key;}
        }
        return -1;
    }

    //removes the shape given id
    public synchronized void remove(int key){
            idShape.remove(key);
    }

    //provides the size of the map; mainly used to generate ids
    public synchronized Integer size(){return idShape.size();}

    //adds shape to the map
    public synchronized void addShape(int ID, Shape shape){idShape.put(ID, shape);}

    public synchronized Set<Integer> getShapeList(){
        return idShape.descendingKeySet();}//navigating tree should be old to new -->
    //all of the shapes and their current drawn location, and what they are

    /**
     * used in order to understand what is occuring within the sketch
     * @return
     */
    @Override
    public String toString(){
        String s = "sketch {";
        for (Integer id : idShape.descendingKeySet()){
            s += id.toString() + " "; //id of the shape
            s += idShape.get(id).toString(); //get string representation of the shape
            s += " -1, "; //separates each shape
        }
        s += " }";
        return s;
    }
}
