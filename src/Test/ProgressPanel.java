package Test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Illusion.IllusionProgressBarCoord;
import Illusion.IllusionProgressBarShadow;
import Illusion.IllusionProgressBarUI;


public class ProgressPanel extends JPanel implements ChangeListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private JProgressBar bar = new JProgressBar();	
	private IllusionProgressBarUI ipbi;
	private Color[] col = {Color.BLUE,Color.CYAN,Color.GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.YELLOW};
	private int lateralWidth = 100;
	private int lateralHeight = 50;
	

	
	public  ProgressPanel(int width,int height){
		this(width,height,JProgressBar.HORIZONTAL);
	}
	
	public  ProgressPanel(int width,int height, int orientation){		
		super();
		if (height > lateralHeight) lateralHeight = height; 
		setLayout(new BorderLayout());
		if (orientation == JProgressBar.HORIZONTAL)
			setMaximumSize(new Dimension(1500,lateralHeight));
		else setMaximumSize(new Dimension(lateralWidth,1500));
		add(getBar(width,height,orientation),BorderLayout.CENTER);
		add(getDirectionsButtonPanel(orientation),(orientation == JProgressBar.HORIZONTAL) ? BorderLayout.WEST : BorderLayout.NORTH);		        
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2,1));
		p.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
       	p.add(new LabeledSlider(width,2,width,"Frame",lateralWidth,lateralHeight/2,this),BorderLayout.NORTH);
       	p.add(new LabeledSlider(10,1,1,"Pass",lateralWidth,lateralHeight/2,this) ,BorderLayout.SOUTH);
       	//p.add(getSlider(width),(orientation == JProgressBar.HORIZONTAL) ? BorderLayout.EAST : BorderLayout.SOUTH);
       	//p.add(getSlider(width),(orientation == JProgressBar.HORIZONTAL) ? BorderLayout.EAST : BorderLayout.SOUTH);
       	add(p,(orientation == JProgressBar.HORIZONTAL) ? BorderLayout.EAST : BorderLayout.SOUTH);
	}


	
	public JPanel getBar(int width, int height, int orientation){
		JPanel p = new JPanel();
		bar = new JProgressBar();
		bar.setOrientation(orientation);
		if (orientation == JProgressBar.VERTICAL)
			bar.setPreferredSize(new Dimension(height, width));	
		else bar.setPreferredSize(new Dimension(width, height));
		bar.setStringPainted(true);
		bar.setBorder(BorderFactory.createEtchedBorder());
		bar.setUI(ipbi = new IllusionProgressBarUI(bar));
		ipbi.setRoundCorner(IllusionProgressBarCoord.RoundOVAL);
		ipbi.setDarkLightColors(col);
		ipbi.setBorder(1);
		ipbi.setShadowPainted(true);
		//ipbi.setHighQuality(true);
		if (orientation == JProgressBar.HORIZONTAL)
			p.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=2;

		p.add(bar,c);
		return p;
	}
	
	public Border getTitleBorder(String title){
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(loweredetched, title);
		titleBorder.setTitleJustification(TitledBorder.CENTER);
		return titleBorder;
	}

	
	public JPanel getRadioPanel(String title,int orientation, boolean rightSelected){
		JPanel p = new JPanel(new BorderLayout());
		//p.setBorder(getTitleBorder(title));
		String s1 = ">";
		String s2 = "<";
		if (orientation == JProgressBar.VERTICAL){
			s1 = "˄";
			s2 = "˅";
		}

        JRadioButton ltr = new JRadioButton(s1);
        ltr.setActionCommand(title + ">");
        ltr.addActionListener(this);      
        JRadioButton rtl = new JRadioButton(s2);
        rtl.setActionCommand(title + "<");
        rtl.addActionListener(this);
        p.add(ltr,BorderLayout.NORTH);
        p.add(rtl,BorderLayout.SOUTH);

        ButtonGroup mutuallyExclusiveButtons = new ButtonGroup();        
        mutuallyExclusiveButtons.add(ltr);
        mutuallyExclusiveButtons.add(rtl);
        if (rightSelected) rtl.setSelected(true);
        else ltr.setSelected(true);
        return p;
	}
	
	public JPanel getDirectionsButtonPanel(int orientation){
		JPanel p = new JPanel(new BorderLayout(0,0));
		p.setPreferredSize(new Dimension(100,lateralHeight+20));
		p.setBorder(getTitleBorder("Effect / Bar"));
        p.add(getRadioPanel("Effect",orientation,true),BorderLayout.WEST);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));      
        JSeparator separate = new JSeparator(JSeparator.VERTICAL);
        separate.setPreferredSize(new Dimension(1,lateralHeight));
        s.add(separate);
        p.add(s,BorderLayout.CENTER);
        p.add(getRadioPanel("Bar",orientation,false),BorderLayout.EAST);
        return p;
	}

		
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Effect" + ">")) 
			ipbi.setIllusionDirection(IllusionProgressBarUI.LEFT_TO_RIGHT);
		else if (e.getActionCommand().equals("Effect" + "<")) 
			ipbi.setIllusionDirection(IllusionProgressBarUI.RIGHT_TO_LEFT);
		else if (e.getActionCommand().equals("Bar" + ">")) 
			ipbi.setBarDirection(IllusionProgressBarUI.LEFT_TO_RIGHT);
		else if (e.getActionCommand().equals("Bar" + "<")) 
			ipbi.setBarDirection(IllusionProgressBarUI.RIGHT_TO_LEFT);

	}
	
	public void setValue(int value){
		bar.setValue(value);
	}
	
	

	@Override
    public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (source.getName().equals("Frame")) 
			ipbi.setFrames((int)source.getValue());
		else ipbi.setPassIncrPercent((int)source.getValue());
    }

	
	public void setDarkAndLightColor(Color dark, Color light) {
		ipbi.setLightAndDarkColor(light, dark);
	}

	
	public void setDarkAndLightColor(Color[] dark, Color[] light) {
		ipbi.setLightAndDarkColors(light, dark);
	}

    public Color[] getDarkColors(){
    	return ipbi.getDarkColors();
    }

    public Color[] getLightColors(){
    	return ipbi.getLightColors();
    }
	
	public IllusionProgressBarShadow getShadowFactory(){
		return ipbi.getShadowFactory();
	}
	
	public Color[] getDefaultColor(){
		return col;
	}


	
}	
