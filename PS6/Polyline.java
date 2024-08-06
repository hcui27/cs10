import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE
	//keeping a list of points instead
	private ArrayList<Point> points;
	private Color color;

	/**
	 * initial 0-length segment at point that eventually
	 * becomes a polyline
	 * @param x1
	 * @param y1
	 * @param color
	 */

	public Polyline(int x1, int y1, Color color){
		this.color = color;
		points = new ArrayList<>();

		points.add(new Point(x1, y1)); //adds a new point
	}

	/**
	 * updates lines list for draw
	 * @param x2
	 * @param y2
	 */
	public void addCoord(int x2, int y2){
		//adding coordinates to each line
		points.add(new Point(x2,y2));
	}

	/**
	 * goes thru every point and moves by dx/dy
	 * @param dx
	 * @param dy
	 */
	@Override
	public void moveBy(int dx, int dy) {
		//copy over each point
		for (Point point: points){
			point.x += dx;
			point.y += dy;
		}
	}

	@Override
	public Color getColor() {return color;}

	@Override
	public void setColor(Color color) {this.color = color;}
	
	@Override
	public boolean contains(int x, int y) {
		//measuring distance btwn the points and not the objects
		//in segment
		for (int i = 0; i < points.size()-1; i++){
			Point tempPoint1 = points.get(i);
			Point tempPoint2 = points.get(i+1);

			if (Segment.pointToSegmentDistance(x,y, tempPoint1.x, tempPoint1.y, tempPoint2.x, tempPoint2.y) <= 4){
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		//draws each line btwn points
		for (int i = 0; i < points.size()-1; i++){
			Point tempPoint1 = points.get(i);
			Point tempPoint2 = points.get(i+1);

			g.drawLine(tempPoint1.x, tempPoint1.y, tempPoint2.x, tempPoint2.y);
		}
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("freehand");
		for (Point point: points){
			s.append(" ").append(point.x).append(" ").append(point.y);
		}
		s.append(" ").append(color.getRGB());
		return s.toString();
	}
}
