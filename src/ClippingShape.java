import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.SwingConstants;

public class ClippingShape {

	private Area shape;
	private Area borderShape;
	private int x;
	private int y;
	private int totalWidth;
	private int height;
	private double roundHeight;
	private double roundWidth;
	private int orientation = SwingConstants.HORIZONTAL;
	
	public ClippingShape(int x, int y,int width,int height,int border,int overlap,double roundWidth,double roundHeight, int orientation) {		
		this.x = x;
		this.y = y;
		this.height = height;
		this.totalWidth = width;	
		this.roundWidth = roundWidth;
		this.roundHeight = roundHeight;
		this.orientation = orientation;
		shape = getBarSchape(x,y,height,width);
		borderShape = initBorderShape(border,overlap);
	}

	
	
	
	public Area initBorderShape(int border,int overlap){
		Area max = getBarSchape(x,y,height,totalWidth,border);
		Area min = getBarSchape(x,y,height,totalWidth,-overlap);		
		max.subtract(min);
		return max;
	}	

	
	
	
	
	public Area getBarSchape(int x,int y,int height,int width,int border){
		 return getBarSchape(x-border,y-border,height+border*2,width+border*2);
	}
	
	
	public Area getBarSchape(int x,int y,int height,int width){
		RoundRectangle2D.Double circle1;
		Rectangle2D.Double rectangle;
		RoundRectangle2D.Double circle2;
		
		if (orientation == SwingConstants.VERTICAL){
			circle1 = new RoundRectangle2D.Double(x,y,width,width,width*roundWidth,width*roundHeight);
			rectangle = new Rectangle2D.Double(x,y + width/2,width,height-width);
			circle2 = new RoundRectangle2D.Double(x,y + height - width,width,width,width*roundWidth,width*roundHeight);		
		} else {
			circle1 = new RoundRectangle2D.Double(x,y,height,height,height*roundWidth,height*roundHeight);
			rectangle = new Rectangle2D.Double(x + height/2,y,width-height,height);
			circle2 = new RoundRectangle2D.Double(x + width - height ,y,height,height,height*roundWidth,height*roundHeight);			
		}
		
		Area c1 = new Area(circle1);
		Area c2 = new Area(circle2);
		Area r = new Area(rectangle);
		Area s = new Area(c1);
		s.add(r);
		s.add(c2);
		return s;
	}

	
		
	public Shape getClipShape(int ammount){
		Rectangle2D.Double rec = new Rectangle2D.Double(x + horizontalization(ammount),y  + verticalization(ammount),totalWidth,height);
		Area r = new Area(rec);
		Area clip = new Area();
		clip.add(shape);
		clip.subtract(r);
		return clip;
	}

	public Shape getClipShadow(){
		return getClipShadow(height/2);
	}
	
	
	public Shape getClipShadow(double ammount){
		Rectangle2D.Double rec = new Rectangle2D.Double(x + horizontalization(ammount),y  + verticalization(ammount),totalWidth,height);		
		/*Rectangle2D.Double rec = new Rectangle2D.Double(x,y+height,totalWidth,height);*/
		Area r = new Area(rec);
		Area clip = new Area();
		clip.add(shape);
		clip.subtract(r);
		return clip;
	}
	
	
	public Shape getBorderShape(){
		return borderShape;
	}
	

	public int horizontalization(int x) {
		return (orientation == SwingConstants.HORIZONTAL) ? x : 0;
	}	

	public int verticalization(int x) {
		return (orientation == SwingConstants.VERTICAL) ? x : 0;
	}	

	public double horizontalization(double x) {
		return (orientation == SwingConstants.HORIZONTAL) ? x : 0;
	}	

	public double verticalization(double x) {
		return (orientation == SwingConstants.VERTICAL) ? x : 0;
	}	
	
}
