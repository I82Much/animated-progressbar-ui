package Illusion;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


public class IllusionProgressBarShadow {

	public static final int DEFAULTShadowLightAlpha = 255;
    public static final int DEFAULTShadowDarkAlpha = 0;
    
   	private Color shadowDark = new Color(0,0,0,255);
   	private Color shadowLight = new Color(0,0,0,0);
   	private double shadowStartGapFromMiddlePercent = 0;
   	private double shadowEndFromBottomPercent = 0.02;
   	private IllusionProgressBarCoord bar = null;

   	
   	public IllusionProgressBarShadow(IllusionProgressBarCoord bar){
   		this.bar = bar;
   	}

   	
    protected BufferedImage createShadowImage(boolean highQuality) {
        BufferedImage image = new BufferedImage(bar.width, bar.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        if (highQuality)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        

       	GradientPaint darkToLightReflect = 
       			new GradientPaint(
       					new Point2D.Double(0,bar.height/2+(bar.height/2)*shadowStartGapFromMiddlePercent), shadowLight,
       					new Point2D.Double(0,bar.height - (bar.height/2)*shadowEndFromBottomPercent), shadowDark);
       	g2.setPaint(darkToLightReflect);
       	g2.fillRect(0,bar.height/2 , bar.width, bar.height);
    	return image;
    }   


	public Color getShadowDark() {
		return shadowDark;
	}


	public void setShadowDark(Color shadowDark) {
			this.shadowDark = shadowDark;				
	}


	public Color getShadowLight() {
		return shadowLight;
	}


	public void setShadowLight(Color shadowLight) {
		this.shadowLight = shadowLight;
	}


	public double getShadowStartGapFromMiddlePercent() {
		return shadowStartGapFromMiddlePercent;
	}


	public void setShadowStartGapFromMiddlePercent(double percent) {
		if (percent<0 || percent>1) throw new NumberFormatException("Value must be from 0 to 1");
		this.shadowStartGapFromMiddlePercent = percent;
	}


	public double getShadowEndFromBottomPercent() {
		return shadowEndFromBottomPercent;
	}


	public void setShadowEndFromBottomPercent(double percent) {
		if (percent<0 || percent>1) throw new NumberFormatException("Value must be from 0 to 1");
		this.shadowEndFromBottomPercent = percent;
	}
    

}
