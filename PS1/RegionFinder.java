import net.datastructures.ArrayStack;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.lang.Math;
import java.util.List;

/**
 * @author Helen Cui
 * date: 4/3
 * purpose: PS-1
 *
 * Code provided for PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Winter 2024
 *
 * @author Tim Pierson, Dartmouth CS10, Winter 2024, based on prior terms RegionFinder
 */
public class RegionFinder {
	private static final int maxColorDiff = 25;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 100; 				// how many points in a region to be worth considering
	private static final int radius = 1;

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 *
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		//saving visited pixels
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();

		//loop over all pixels
		for (int y = 0; y < image.getHeight(); y++){
			for (int x = 0; x < image.getWidth(); x++) {
				Color checkColor = new Color(image.getRGB(x,y));

				//checkpoint
				if(visited.getRGB(x,y)>0 || !colorMatch(targetColor,checkColor)){
					continue;}

				ArrayList<Point> region = new ArrayList<Point>(); //initialize region list
				ArrayList<Point> toVisit = new ArrayList<Point>(); //initialize to visit list
				Point current = new Point(x,y); // pixel that is going to start the toVisit loop
				toVisit.add(current);


						while (!toVisit.isEmpty()) {
							Point point = toVisit.get(toVisit.size()-1);
							toVisit.remove(toVisit.size()-1);

							if (visited.getRGB(point.x, point.y) == 0){
								region.add(point); // add the point itself to the region
								visited.setRGB(point.x, point.y, 1);

								/**
								 * taken from BlurredImage (provided program); essentially going over neighbors thru nested loops,
								 * careful not to go over the image bounds
								 */

							for (int ny = Math.max(0, point.y - radius); ny < Math.min(image.getHeight(), point.y + 1 + radius); ny++) {
								for (int nx = Math.max(0, point.x - radius); nx < Math.min(image.getWidth(), point.x + 1 + radius); nx++) {
									Point neighbor = new Point(nx, ny);
									Color neighborcolor = new Color(image.getRGB(nx,ny));

									if (colorMatch(targetColor, neighborcolor) && (visited.getRGB(neighbor.x, neighbor.y) == 0)) {
										region.add(neighbor);
										toVisit.add(neighbor); // add to toVisit
										visited.setRGB(point.x, point.y, 1);
								}}}}}


						// check if region should b added to array of arraylists
							if (region.size() >= minRegion){
								regions.add(region);

					}}}}



	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */

	protected static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		//finds the absolute values of the diff btwn each color
		int diffRed = Math.abs(c1.getRed() - c2.getRed());
		int diffBlue = Math.abs(c1.getBlue() - c2.getBlue());
		int diffGreen = Math.abs(c1.getGreen() - c2.getGreen());

		// returns boolean about if the color matches or not
		return ((diffRed <= maxColorDiff) && (diffBlue <= maxColorDiff) && (diffGreen <= maxColorDiff));
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		if(regions.isEmpty()) {return null;}

		ArrayList<Point> large = regions.get(0); // set initial

		//findmax algorithm
		for (ArrayList<Point> region : regions) {
			if (large.size() > region.size()) {
				large = region;
			}
		}
		return large;

	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE

		for (ArrayList<Point> region : regions) {
			int randomRed = (int) (Math.random() * 255);
			int randomBlue = (int) (Math.random()*255);
			int randomGreen = (int) (Math.random() * 255);
			Color randomColor = new Color (randomRed, randomGreen, randomBlue);

			for (Point pixel : region) {
				recoloredImage.setRGB(pixel.x, pixel.y, randomColor.getRGB());
			}}}}

