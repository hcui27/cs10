import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


/**
 * PS-2 provided code
 * A point quadtree: stores an element at a 2D position, with children at the subdivided quadrants
 * E extends Point2D to ensure whatever the PointQuadTree holds, it implements getX and getY
 * 
 * @author Tim Pierson, Dartmouth CS10, Winter 2024, based on prior term code
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	public E getPoint() { return point; }
	public int getX1() { return x1; }
	public int getY1() { return y1; }
	public int getX2() { return x2; }
	public int getY2() { return y2; }

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 * @return child for quadrant
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		/**
		 * To insert point p, which is at (x',y'), start at the root
		 *   If (x',y') is in quadrant 1
		 *     If child 1 exists, then recursively insert p in child 1
		 *     Else set child 1 to a new tree holding just p and with all children set to null
		 *   And similarly with the other quadrants / children
		 */

		// depending on which quadrant its in the bounds of x1,y1, x2, y2 will change

		//quad1: x1 = x, y1 = y1; x2 = x2, y2 = y
		if (inQuad1(p2)) {
			if (hasChild(1)) {
				c1.insert(p2);
				} else {
				c1 = new PointQuadtree(p2, (int) point.getX(), y1, x2, (int) point.getY());

			}}


			//quad2: x1 = x1, y1 = y1; x2 = x, y2 = y
		if (inQuad2(p2)) {
				if (hasChild(2)) {
					c2.insert(p2);
					} else
						c2 = new PointQuadtree(p2, x1, y1, (int) point.getX(), (int) point.getY());
		}


		//quad3: x1 = x1, y1 = y; x2 = x, y2 = y2
		if (inQuad3(p2)) {
			if (hasChild(3)) {
				c3.insert(p2);
				}
			else c3 = new PointQuadtree(p2, x1, (int) point.getY(), (int) point.getX(), y2);

		}

		//quad4: x1 = x, y1 = y; x2 = x2, y2 = y2
		if (inQuad4(p2)) {
			if (hasChild(4)) {
				c4.insert(p2);
				} else
				c4 = new PointQuadtree(p2, (int) point.getX(), (int) point.getY(), x2, y2);
		}
	}

	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE

		// recursively goes thru each quadrant and adds to the size --> temporary list and add
		int num = 1;
		if (hasChild(1)) num += c1.size();
		if (hasChild(2)) num += c2.size();
		if (hasChild(3)) num += c3.size();
		if (hasChild(4)) num += c4.size();
		return num;

	}

	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 * @return List with all points in the quadtree
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		List<E> points = new ArrayList();
		helperallPoints(points);
		return points;
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		/**
		 * To find all points within the circle (cx,cy,cr), stored in a tree covering rectangle (x1,y1)-(x2,y2)
		 *   If the circle intersects a quadrant's rectangle
		 *     If the tree's point is in the circle, then the point is a "hit"
		 *     For each non-null child quadrant
		 *       Recurse with that child
		 */

			List<E> result = new ArrayList();
			result = helperfindincircle(cx,cy,cr,result);
//			System.out.println("size" + result.size()); used to test DotTreeGUI
			return result;}


	// TODO: YOUR CODE HERE for any helper methods

	/**
	 * findinCircle(cx, cy, cr)
	 * initialize a list
	 * create a helperfindinCircle
 	 */
	public List<E> helperfindincircle(double cx, double cy, double cr, List<E> list) {
		//checks to see if circle intersects, then if it is a hit
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
			list.add(point);} // it is a hit


		//recurses with children
		if (hasChild(1)) getChild(1).helperfindincircle(cx, cy, cr, list);
		if (hasChild(2)) getChild(2).helperfindincircle(cx, cy, cr, list);
		if (hasChild(3)) getChild(3).helperfindincircle(cx, cy, cr, list);
		if (hasChild(4)) getChild(4).helperfindincircle(cx, cy, cr, list); }

	return list;}

	public void helperallPoints(List<E> list){
		list.add(point);
		if (c1 != null) c1.helperallPoints(list);
		if (c2 != null) c2.helperallPoints(list);
		if (c3 != null) c3.helperallPoints(list);
		if (c4 != null) c4.helperallPoints(list);
	}


	// neatening up code = just checks if the point is actually belonging to which quadrant
	public boolean inQuad1(E p2){
		// x <p2.X < x2 ; y1 < p2.Y < y
		if ((point.getX() <= p2.getX()) && (p2.getX() < getX2()) && (getY1() <= p2.getY()) && (p2.getY() < point.getY())) {
			return true;
		}
		else {return false;}

	}
	public boolean inQuad2(E p2){
		// x1 <p2.X < x ; y1 < p2.Y < y
		if ((getX1() <= p2.getX()) && (p2.getX() < point.getX()) && (getY1() <= p2.getY()) && (p2.getY() < point.getY())) {
			return true;
		}
		else {return false;}
	}

	public boolean inQuad3(E p2){
		// x1 <p2.X < x ; y < p2.Y < y2
		if ((getX1() <= p2.getX()) && (p2.getX() < point.getX()) && (point.getY() <= p2.getY()) && (p2.getY() < getY2())) {
			return true;
		}
		else {return false;}
	}

	public boolean inQuad4(E p2){
		// x <p2.X < x2 ; y < p2.Y < y2
		if ((point.getX() <= p2.getX()) && (p2.getX() < getX2()) && (point.getY() <= p2.getY()) && (p2.getY() < getY2())) {
			return true;
		}
		else {return false;}
	}

	public static void main(String[] args) {
		//testing for insert,
		Point2D point = new Point2D.Double(0,0);
		PointQuadtree<Point2D> tree = new PointQuadtree<>(point, 0,0, 400,400);
		System.out.println(tree.size());
		tree.insert(point);
		tree.insert(point);
		System.out.println(tree.size());
		tree.insert(point);
		System.out.println(tree.size());
	}
}
