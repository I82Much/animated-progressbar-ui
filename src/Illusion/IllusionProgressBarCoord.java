package Illusion;
import java.awt.BasicStroke;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JProgressBar;


public class IllusionProgressBarCoord implements ComponentListener{


	public int top = -1;
	public int bottom = -1;
	public int left = -1;
	public int right = -1;
	public int topBorder = -1;
	public int bottomBorder = -1;
	public int leftBorder = -1;
	public int rightBorder = -1;
	public int width = -1;
	public int height = -1;
	public int widthBorder = -1;
	public int heightBorder = -1;
	private ClippingShape cs;
    private int borderThickness = 0;
    private int borderOverlap = 1;
    public double roundHeight = 0;
    public double roundWidth =  0;
    public static double[] RoundNOROUND = {0,0};
    public static double[] RoundCIRCLE = {1,1};
    public static double[] RoundOVAL = {4/3.0,1};
	
	private JProgressBar bar;
		
	public IllusionProgressBarCoord(JProgressBar bar){
		this(bar,0);
	}
	
	public IllusionProgressBarCoord(JProgressBar bar, int border){
		this.bar = bar;
		//bar.setBorder(new EmptyBorder(new Insets(10,10,10,10)));   
		bar.addComponentListener(this);
		setBorder(border);
	}


	public void init(){
		Insets in = bar.getInsets();
		topBorder = in.top;
		bottomBorder = in.bottom;
		leftBorder = in.left;
		rightBorder = in.right;
		
		top = in.top + borderThickness;
		bottom = in.bottom + borderThickness;
		left = in.left + borderThickness;
		right = in.right + borderThickness;
		
		width = bar.getWidth() - right - left;
		height = bar.getHeight() - bottom - top;

		
		cs = new ClippingShape(left,top,width,height,borderThickness,borderOverlap,roundWidth,roundHeight,bar.getOrientation());
		
		widthBorder = bar.getWidth() - rightBorder - leftBorder - borderThickness;
		heightBorder = bar.getHeight() - bottomBorder - topBorder  - borderThickness;
	}

	public Insets getInsets(){
		return new Insets(top, left, bottom, right);
	}
	
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void componentResized(ComponentEvent e) {
		init();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		init();
	}

	
	public int getOrientation(){
		return bar.getOrientation();
	}

	public boolean isDisplayable() {
		return bar.isDisplayable();
	}

	public boolean isStringPainted() {
		return bar.isStringPainted();
	}	
	
	public int horizontalization(int x) {
		return (bar.getOrientation() == JProgressBar.HORIZONTAL) ? x : 0;
	}	

	public int verticalization(int x) {
		return (bar.getOrientation() == JProgressBar.VERTICAL) ? x : 0;
	}	


	public int vertOrHoriz(int vert,int horiz) {
		return (bar.getOrientation() == JProgressBar.VERTICAL) ? vert : horiz;
	}	
	
	public Shape getClipShape(int width){
		return cs.getClipShape(width);
	}
	
	
	public BasicStroke getBorderStroke(){
		return new BasicStroke(borderThickness + borderOverlap);
	}
	
	public void setBorder(int border){
		this.borderThickness = border;
		if (border > 7) borderOverlap = 4;
		else if (border > 5) borderOverlap = 4;
		else if (border > 3) borderOverlap = 3;
		if (border > 0) bar.setBorderPainted(false);
		if (width>0) init();
	}
	
	
    public void setRoundCorner(double[] moltiplier){
    	roundHeight = moltiplier[0];
    	roundWidth =  moltiplier[1];
    }
	
    public static double[] getRoundComponent(double height,double width){
    	return new double[] {height, width};
    }
    
	public Shape getBorderShape(){
		return cs.getBorderShape();
	}

	public boolean isBorderPainted() {
		return (borderThickness > 0);
	}

	public Shape getClipShadow(){
		return cs.getClipShadow();
	}
	
	public Shape getClipShadow(double height){
		return cs.getClipShadow(height);
	}
	

	
}
