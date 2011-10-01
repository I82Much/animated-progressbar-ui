import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class IllusionProgressBarUI extends BasicProgressBarUI implements HierarchyListener{

	private int frame = 200;
    private int pass = 0;
    private int totalPass = frame*2;
	
	public static final int LEFT_TO_RIGHT = 0;
	public static final int RIGHT_TO_LEFT = 1;

	
    private int direction = RIGHT_TO_LEFT;
    public static final Color DARKDEFAULT = new Color(48,143,252,100);
    public static final Color LIGHTDEFAULT = new Color(145,217,255,100);
    private Color[] dark = { DARKDEFAULT };
    private Color[] light = { LIGHTDEFAULT };
    private Color actualDark;
    private Color actualLight;
    private Color borderColor = Color.BLACK;
    private int colorIndex = 0;
    private boolean highQuality = false;
     
    private BufferedImage barImage = null;
    private BufferedImage shadowImage = null;
    private IllusionProgressBarCoord bar;
    private IllusionProgressBarShadow shadow = null;
   	private boolean shadowPainted = false;
   	
   	
    
    public IllusionProgressBarUI(JProgressBar progressBar) {
    	super();    	
    	super.progressBar = progressBar;
    	//progressBar.set
    	bar = new IllusionProgressBarCoord(progressBar);
    	//TODO in listner
    	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) 
    		frame = progressBar.getPreferredSize().width;
    	else frame = progressBar.getPreferredSize().height;    	
    	progressBar.addHierarchyListener(this);    	    	
    }

    
    
    public static Color getMediumColor(Color c1, Color c2, int totalPass,int incr){
    	double p = incr * (1.0/totalPass);
    	double p1 = 1-p;
    	double p2 = p;
    	int r = (int) (c1.getRed() * p1 + c2.getRed() * p2);
    	int g = (int) (c1.getGreen() * p1 + c2.getGreen() * p2);
    	int b = (int) (c1.getBlue() * p1 + c2.getBlue() * p2);
    	int a = (int) (c1.getAlpha() * p1 + c2.getAlpha() * p2);
    	return new Color(r,g,b,a);
    }

    
    protected BufferedImage createRippleImage(Color[] darkColors, Color[] lightColors) {
    	if (darkColors.length != lightColors.length) return barImage;

    	Color darkColor = darkColors[colorIndex];
    	Color lightColor = lightColors[colorIndex];
    	
    	if (darkColors.length > 1){
    		int next = (colorIndex + 1)%lightColors.length; 
    		darkColor = getMediumColor(darkColors[colorIndex], darkColors[next],totalPass,pass);
    		lightColor = getMediumColor(lightColors[colorIndex], lightColors[next],totalPass,pass);
    		pass++;    	
    		if (pass > totalPass) {
    			pass = 0;
    			colorIndex = (colorIndex + 1)%(lightColors.length);
    		}
    	}
    	
    	if (darkColor.equals(actualDark) && lightColor.equals(actualLight)) 
    		return barImage;
    	else {
    		actualDark = darkColor;
    		actualLight = lightColor;
    	}
    	    	

        BufferedImage image = new BufferedImage(bar.width, bar.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        if (highQuality)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint darkToLightReflect;



        if (bar.getOrientation() == JProgressBar.HORIZONTAL)
        	darkToLightReflect = new GradientPaint(new Point2D.Double(0,0), actualDark, new Point2D.Double(bar.width/2, 0), actualLight,true);
        else darkToLightReflect = new GradientPaint(new Point2D.Double(0,0), actualDark, new Point2D.Double(0, bar.height/2), actualLight,true);       
        g2.setPaint(darkToLightReflect);
        g2.fillRect(0, 0, bar.width, bar.height);
  
    	return image;
    }  
    
 
    
    

    public void setRTLDirection() {
        this.direction = RIGHT_TO_LEFT;
    }

    public void setLTRDirection() {
        this.direction = LEFT_TO_RIGHT;
    }    
    
    @Override
    protected void incrementAnimationIndex() {
    	int newValue = getAnimationIndex() + 1;
        if (newValue < frame)
            setAnimationIndex(newValue);
        else setAnimationIndex(0);
    }


    
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof  Graphics2D)) 
            return;
        if (bar.width <= 0 || bar.height <= 0) {
            return;
        }        
       
        barImage = createRippleImage(dark,light);
        if (isShadowPainted()) shadowImage = shadow.createShadowImage(highQuality);
        
        Graphics2D g2 = (Graphics2D) g;
        if (highQuality)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
         

        if (bar.getOrientation() == JProgressBar.HORIZONTAL)
    		 paintDeterminateUni(g, bar.width,barImage.getWidth(),bar.height, bar.roundHeight,bar.roundWidth);
    	else paintDeterminateUni(g, bar.height,barImage.getHeight(),bar.width,bar.roundWidth,bar.roundHeight);
        
        if (bar.isBorderPainted()){
        	g2.setColor(borderColor);
        	g2.setClip(bar.getBorderShape());
        	g2.fill(bar.getBorderShape());
        }
        
        
        
    }    

    
    
   
    protected void paintDeterminateUni(Graphics g, int barlong, int imgLong, int barShort,double roundShort, double roundLong) {    
    	//if (barImage == null) barImage = createRippleImage(dark,light);
        if (bar.width <= 0 || bar.height <= 0) return;
        int amountFull = getAmountFull(bar.getInsets(), bar.width, bar.height);
        

    	int offset = 0;
        if (direction == RIGHT_TO_LEFT) 
            offset = (int) (map(getAnimationIndex(), 0, frame, imgLong, 0));
        else offset = (int) (map(getAnimationIndex(), 0,  frame, 0, imgLong));

        int numRepetitions = barlong / imgLong;
        numRepetitions += 2;
    
        Graphics2D g2 = (Graphics2D) g;
        
        
        g2.setClip(bar.getClipShape(amountFull));
        

       	for (int i = 0; i < numRepetitions; i++) {
       		int dimOffset = (i - 1) * barlong + offset;       		
       		g2.drawImage(barImage, bar.horizontalization(dimOffset) + bar.left,bar.verticalization(dimOffset) + bar.top, null);       		
       	}
       	
       	if (shadowPainted) g2.drawImage(shadowImage, bar.left,bar.top, null);
       	 
       	
       	g2.setClip(bar.left,bar.top,bar.width,bar.height);
        if (bar.isStringPainted()) 
            paintString(g, bar.left, bar.top, bar.width, bar.height,amountFull, bar.getInsets());
    	
       	
       	
    }
    
    
    


    
    
    protected Color getSelectionBackground() { 
    	return Color.BLACK; 
    }
    
    protected Color getSelectionForeground() { 
    	return Color.BLACK;     
    }
    

    public static double map(double value, double low1, double high1, double low2, double high2) {
        double diff = value - low1;
        double proportion = diff / (high1 - low1);
        return lerp(low2, high2, proportion);
    }

    public static double lerp(double value1, double value2, double amt) {
        return ((value2 - value1) * amt) + value1;
    }

	public void setFrames(int value) {
		frame = value;
	}
    
    public void hierarchyChanged(HierarchyEvent he) {
        if ((he.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED) != 0)
                if (bar.isDisplayable()) startAnimationTimer();
                else stopAnimationTimer();
    }

    public void setLightAndDarkColor(Color light,Color dark){
    	this.light = new Color[]{light};
    	this.dark = new Color[]{dark};
    	colorIndex = 0;
    }
        

    public void setLightAndDarkColors(Color[] lights,Color[] darks){
    	if (lights.length != darks.length) throw new ArrayIndexOutOfBoundsException("Lights.lenght != Darks.length " + lights.length + "!=" + darks.length);    	
    	light = lights;
    	dark = darks;
    	colorIndex = 0;
    }

    public void setDarkLightColors(Color[] c){    
    	Color[] dark = new Color[c.length];
    	Color[] light = new Color[c.length];
    	for (int i = 0; i < c.length; i++) {
    		dark[i] = c[i].darker();
    		light[i] = c[i].brighter();
		}
    	this.dark = dark;
    	this.light = light;
    }    
    

    public void setRoundCorner(double[] moltiplier){
    	bar.setRoundCorner(moltiplier);
    }
    
    public void setBorder(int border){
    	bar.setBorder(border);
    }
    
    public void setBorderColor(Color c){
    	borderColor = c;
    }    
    
    public void setHighQuality(boolean b){
    	highQuality = true;
    }
    

    public IllusionProgressBarShadow getShadowFactory(){
    	return shadow;
    }


    public void setShadowPainted(boolean active){
    	this.shadowPainted = active;
    	if (shadowPainted) shadow = new IllusionProgressBarShadow(bar);  
    }
	
    public boolean isShadowPainted(){
    	return shadowPainted;
    }
    
    
}
