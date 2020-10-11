package draw.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

/**
 * Element that can appear in drawing.
 */
public abstract class Element {

	/** Bounding box for element. */
	protected Rectangle bbox;
	
	/** Is selected. */
	protected boolean selected;
	
	/** Computed anchors on demand. */
	protected Rectangle[] anchors = null;
	
	/** Empty rectangle. */
	public static final Rectangle EmptyRectangle = new Rectangle(0,0,0,0);
		
	/**
	 * Base constructor to establish bounding box for element.
	 * 
	 * Every sub-class must implement this constructor.
	 * @param rect
	 */
	protected Element(Rectangle rect) {
		this.bbox = new Rectangle(rect);
	}

	/**
	 * Base constructor where bounding box definition is delayed.
	 */
	protected Element() {
		this.bbox = EmptyRectangle;
	}
	
	/** Default anchor size in pixels. */
	public final static int anchorSize = 4;
	
	/** 
	 * Access anchors for element.
	 * 
	 * note: A potential risk since these rectangles could be modified by caller. Please avoid doing so!
	 * @return  anchors for this element. Anchors are returned in order SouthWest, SouthEast, NorthWest, NorthEast
	 */
	public Rectangle[] getAnchors() {
		if (anchors == null) {
			anchors = new Rectangle[4];
			
			int[] xs = new int[] { -anchorSize, bbox.width - anchorSize, -anchorSize, bbox.width - anchorSize};
			int[] ys = new int[] { bbox.height - anchorSize, bbox.height - anchorSize, -anchorSize, -anchorSize };
			
			for (int i = 0; i < xs.length; i++) {
				anchors[i] = new Rectangle(bbox.x + xs[i], bbox.y + ys[i], anchorSize*2, anchorSize*2);
			}
		}
		
		return anchors;
	}
	
	/** Reset anchors; useful as a catch-all mechanism to force recalculation when changes occur. */
	public void resetAnchors() { 
		anchors = null;
	}
	
	/** 
	 * Returns bounding box of the given element.
	 * 
	 * The upper left corner of this bounding box rectangle is the 'anchor' for the element, and all drawing
	 * is relative to it.
	 * 
	 * A copy of the current bounding box is returned for protection, so it isn't inadvertently modified by 
	 * code external to this class.
	 * 
	 * @return   bounding box for element.
	 */
	public Rectangle getBoundingBox() {
		return new Rectangle(bbox);
	}
	
	/**
	 * Update the bounding box.
	 * 
	 * Resets anchors to force re-computation when needed.
	 * @param r   new bounding rectangle for the element.
	 */
	public void setBoundingBox(Rectangle r) {
		bbox = new Rectangle(r);
		anchors = null; 
	}
	
	/** 
	 * Query selected status of element.
	 * 
	 * @return  whether element is selected.
	 */
	public boolean isSelected() { return selected; }
	
	/** 
	 * Set whether element is selected. 
	 * @param flag  determines whether element should be selected.
	 * @return     this element, for programmer convenience
	 */
	public Element setSelected(boolean flag) { 
		selected = flag; 
		return this; 
	}
	
	// Fundamental operations
	
	/**
	 * Make an exact copy, delegated to subclasses.
	 * @return   copy of the current element.
	 */
	public abstract Element clone();
	
	/**
	 * Draw self into the given graphics context. 
	 * 
	 * @param g   Graphics object into which to draw
	 */
	public abstract void drawElement(Graphics g);
	
	/**
	 * Draw self into the given graphics context using the drawElement method. This
	 * will store the relevant graphics properties to free up the individual draw elements
	 * from saving them.
	 * 
	 * @param g   Graphics object into which to draw
	 */
	public void draw(Graphics g) {
		// save
		Color oldColor = g.getColor();
		Stroke oldStroke = ((Graphics2D)g).getStroke();
		
		drawElement(g);
		
		// restore
		g.setColor(oldColor);
		((Graphics2D)g).setStroke(oldStroke);
	}
	
	/**
	 * Does this element contain the given point.
	 * @param p   point to query
	 * @return    true if element contains the point
	 */
	public abstract boolean contains (Point p);
}
