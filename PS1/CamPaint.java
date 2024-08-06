import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * @author Helen Cui
 * date: 4/5/24
 * purpose: PS-1
 *
 *
 * Code provided for PS-1
 * Webcam-based drawing
 * Dartmouth CS 10, Winter 2024
 *
 * @author Tim Pierson, Dartmouth CS10, Winter 2024 (based on CamPaint from previous terms)
 */
public class CamPaint extends VideoGUI {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * VideoGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void handleImage() {
		// TODO: YOUR CODE HERE
		if (targetColor != null){ // check to make sure targetcolor exists

			//setting the regionfinder up
			finder.setImage(image);
			finder.findRegions(targetColor);
			finder.recolorImage();
			ArrayList<Point> brush = finder.largestRegion();

			for (Point paint : brush){
				painting.setRGB(paint.x, paint.y, paintColor.getRGB());
			}

			//handling the display modes
			if (displayMode == 'w'){setImage1(image);}
			if (displayMode == 'r'){setImage1(finder.getRecoloredImage());}
			if (displayMode == 'p'){setImage1(painting);}

			}
		}


	/**
	 * Overrides the Webcam method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		targetColor = new Color(image.getRGB(x, y));

	}

	/**
	 * Webcam method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		new CamPaint();
	}
}
