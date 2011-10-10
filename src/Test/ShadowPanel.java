package Test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ShadowPanel extends JPanel implements ChangeListener , ActionListener{


	private static final long serialVersionUID = 1L;
	private int height = 150;
	private ArrayList<ProgressPanel> list;
	
	private JLabel darkAlphaLabel;	
	private JSlider darkAlphaSlider;
	private JLabel lightAlphaLabel;
	private JSlider lightAlphaSlider;
	private JButton lightColor;
	private JButton darkColor;
	private JLabel  middleLabel;
	private JSlider middleSlider;
	private JLabel  bottomLabel;
	private JSlider bottomSlider;
	private JPanel gradient;
	private GridBagConstraints c;
	private JPanel grid;

	public ShadowPanel(ArrayList<ProgressPanel> list){
		this.list = list;

		setLayout(new BorderLayout());
		/*
		CodeTextArea cta = new CodeTextArea();
		add(cta,BorderLayout.LINE_END);
		*/
		grid = new JPanel(new GridBagLayout());
		add(grid,BorderLayout.CENTER);
		c = new GridBagConstraints();

		
		
		
		darkAlphaLabel = new JLabel("255");
		darkAlphaSlider =  new JSlider(0,255,255);
		darkAlphaSlider.addChangeListener(this);
		darkAlphaSlider.setPreferredSize(new Dimension(80,50));
		lightAlphaLabel = new JLabel("255");
		lightAlphaSlider =  new JSlider(0,255,0);
		lightAlphaSlider.addChangeListener(this);		
		lightAlphaSlider.setPreferredSize(new Dimension(80,50));
		lightColor = new ColorButton(new Color(0,0,0),"Light",this);
		darkColor = new ColorButton(new Color(0,0,0),"Dark",this);
		middleLabel =  new JLabel("0"); 
		middleSlider =  new JSlider(0,100,0);
		middleSlider.setOrientation(JSlider.VERTICAL);
		middleSlider.addChangeListener(this);
		Hashtable<Integer,JLabel> labelTable = new Hashtable<>();
		labelTable.put( new Integer( 0 ), new JLabel("0.00") );
		labelTable.put( new Integer( 50 ), new JLabel("0.50") );
		labelTable.put( new Integer( 100 ), new JLabel("1.00") );
		middleSlider.setLabelTable( labelTable );
		middleSlider.setInverted(true);
		middleSlider.setPreferredSize(new Dimension(50,height/4*3));
		bottomLabel = new JLabel("0,02");
		bottomSlider = new JSlider(0,100,2);
		bottomSlider.setLabelTable( labelTable );		
		bottomSlider.addChangeListener(this);
		bottomSlider.setOrientation(JSlider.VERTICAL);
		bottomSlider.setPreferredSize(new Dimension(50,height/4*3));
		//c.fill = GridBagConstraints.HORIZONTAL;
	
		

		addGrid(0,0,8,gradient = getGradientPanel());
		addGrid(1,0,8,getSeparator(2,140,JSeparator.VERTICAL));
		addGrid(2,0,1,middleLabel);		
		addGrid(3,0,8,getSeparator(2,140,JSeparator.VERTICAL));
		addGrid(4,0,1,bottomLabel);
		addGrid(5,0,8,getSeparator(2,140,JSeparator.VERTICAL));
		addGrid(2,1,7,middleSlider);
		addGrid(4,1,7,bottomSlider);
		

		addGrid(6,0,2,darkColor);
		addGrid(7,0,2,darkAlphaLabel);
		addGrid(8,0,2,darkAlphaSlider);
		addGrid(9,0,1,new JLabel("Dark Shadow color Button, Alpha channel label, Alpha channel slider"));	
		addGrid(9,1,1,new JLabel("-ui.getShadowFactory().setShadowDark(new Color(r,g,b,A));"));

		
		
		addGrid(6,2,2,lightColor);
		addGrid(7,2,2,lightAlphaLabel);
		addGrid(8,2,2,lightAlphaSlider);		
		addGrid(9,2,1,new JLabel("Light Shadow color Button, Alpha channel label, Alpha channel slider"));
		addGrid(9,3,1,new JLabel("-ui.getShadowFactory().setShadowLight(new Color(r,g,b,A));"));	
		
		//addGrid(9,4,GridBagConstraints.RELATIVE,new JPanel());

		c.gridwidth=4;
		addGrid(6,4,1,getSeparator(600,10,JSeparator.HORIZONTAL));
		addGrid(6,5,1,new JLabel("Vertical slider indicate shadow percentual from bottom and middle range 0-1 Bottom < Middle"));
		addGrid(6,6,1,new JLabel("    ui.getShadowFactory().setShadowEndFromBottomPercent(double range 0-1);"));
		addGrid(6,7,1,new JLabel("    ui.getShadowFactory().setShadowStartGapFromMiddlePercent(double range 0-1);"));
		c.gridwidth=1;
		
		
		/*
		try {
			cta.appendBold(3);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}
	

	private void addGrid(int x,int y, int h, JComponent comp){
		c.gridx = x;
		c.gridy = y;
		c.gridheight = h;
		grid.add(comp,c);
	}


	
	private JSeparator getSeparator(int w,int h,int orientation){
		JSeparator sep = new JSeparator(orientation);
		sep.setPreferredSize(new Dimension(w,h));
		return sep;
	}
	
	
	@Override
	public void stateChanged(ChangeEvent e) {
		Object s = e.getSource();
		if (s == darkAlphaSlider) changeColor(darkAlphaSlider,darkAlphaLabel,darkColor);
		else if (s == lightAlphaSlider) changeColor(lightAlphaSlider,lightAlphaLabel,lightColor);
		if (s == middleSlider) setShadowHeightMiddle();
		if (s == bottomSlider) setShadowHeightBottom(); 
		gradient.repaint();
	}
	
	public void setShadowHeightMiddle(){
		if ((100 - middleSlider.getValue()) < bottomSlider.getValue()) middleSlider.setValue(100 - bottomSlider.getValue() -1);
		middleLabel.setText(""+(middleSlider.getValue()/100.0));
		for (int i = 0; i < list.size(); i++) {
			list.get(i).getShadowFactory().setShadowStartGapFromMiddlePercent(middleSlider.getValue()/100.0);
		}					
	}
	
	public void setShadowHeightBottom(){
		if ((100 - middleSlider.getValue()) < bottomSlider.getValue()) bottomSlider.setValue(100 - middleSlider.getValue() - 1);
		bottomLabel.setText(""+(bottomSlider.getValue()/100.0));
		for (int i = 0; i < list.size(); i++) {
			list.get(i).getShadowFactory().setShadowEndFromBottomPercent(bottomSlider.getValue()/100.0);
		}					
	}
	
	
	public void changeColor(JSlider s, JLabel l, JButton b){
		l.setText(""+ s.getValue());
		Color c = b.getBackground();
		c = new Color(c.getRed(),c.getGreen(),c.getBlue(),s.getValue());
		for (int i = 0; i < list.size(); i++) {
			if (s == darkAlphaSlider) list.get(i).getShadowFactory().setShadowDark(c);
			else list.get(i).getShadowFactory().setShadowLight(c);
		}
		
	}
	
	
	private JPanel getGradientPanel(){
		@SuppressWarnings("serial")
		JPanel p = new JPanel(){
			BufferedImage shadowImage = null;
			int width = 50;
			protected void paintComponent(Graphics g){
				 super.paintComponent(g);
				 Graphics2D g2 = (Graphics2D) g;
				 g2.setColor(Color.WHITE);
				 g2.fillRect(0, 0, width, height);
				 shadowImage = createShadowImage();
				 g2.drawImage(shadowImage, 0,0, null);
			}
			
			protected BufferedImage createShadowImage() {
		        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2 = image.createGraphics();
		        Color d = list.get(0).getShadowFactory().getShadowDark();
		        Color l = list.get(0).getShadowFactory().getShadowLight();
		        double m = list.get(0).getShadowFactory().getShadowStartGapFromMiddlePercent();
		        double b = list.get(0).getShadowFactory().getShadowEndFromBottomPercent();
		       	GradientPaint darkToLightReflect = 
		       			new GradientPaint(new Point2D.Double(0, height/2+  height/2*m), l,
		       							  new Point2D.Double(0, height - height/2*b), d);
		       	g2.setPaint(darkToLightReflect);
		       	g2.fillRect(0,height/2 , width, height);
		    	return image;
		    }   		
		};
		//p.setMinimumSize(new Dimension(50,50));
		p.setPreferredSize(new Dimension(55,height));
		return p;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == darkColor)	changeColor(darkAlphaSlider, darkAlphaLabel, darkColor);
		else if (e.getSource() == darkColor) changeColor(lightAlphaSlider, lightAlphaLabel, lightColor);
	}
	
   	
    
	
	
 	
}