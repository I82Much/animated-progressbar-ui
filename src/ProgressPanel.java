
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ProgressPanel extends JPanel implements ChangeListener{

	private static final long serialVersionUID = 1L;
	private JProgressBar bar = new JProgressBar();	
	private static GridLayout gl = null;
	private static int cols = 3;
	private IllusionProgressBarUI ipbi;
	private Color[] col = {Color.BLUE,Color.CYAN,Color.GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.YELLOW};

	
	public  ProgressPanel(int width,int height){
		this(width,height,JProgressBar.HORIZONTAL);
	}
	
	public  ProgressPanel(int width,int height, int orientation){		
		add(getBar(width,height,orientation));
        add(getReverseButtonPanel(orientation));
        add(getSlider(width,orientation));
	}

	
	public JPanel getSlider(int maxdiv2,int orientation){
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(new EmptyBorder(new Insets(0, 10, 0, 10)));
        JSlider slider = new JSlider(0, maxdiv2*2);
        slider.setOrientation(orientation);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        p.add(slider,BorderLayout.CENTER); 		
		return p;
	}
	
	
		
	
	public JPanel getBar(int width, int height, int orientation){
		JPanel p = new JPanel();
		bar = new JProgressBar();
		bar.setOrientation(orientation);
		if (orientation == JProgressBar.VERTICAL)
			bar.setPreferredSize(new Dimension(height, width));	
		else bar.setPreferredSize(new Dimension(width, height));
		bar.setStringPainted(true);
		//bar.setBorder(BorderFactory.createEtchedBorder());
		//bar.setForeground(Color.red);
		bar.setUI(ipbi = new IllusionProgressBarUI(bar));
		ipbi.setRoundCorner(IllusionProgressBarCoord.RoundOVAL);
		ipbi.setDarkLightColors(col);
		ipbi.setBorder(1);
		ipbi.setShadowPainted(true);
		//ipbi.setHighQuality(true);
		setLayout(getGridLayout());
        p.add(bar);
        
        //bar.setBackground(Color.BLACK);
		return p;
	}
	
	public JPanel getReverseButtonPanel(int orientation){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, orientation));
        JRadioButton leftToRight = new JRadioButton(new AbstractAction("Left to right") {
            public void actionPerformed(ActionEvent e) {ipbi.setLTRDirection();}});
        JRadioButton rightToLeft = new JRadioButton(new AbstractAction("Right to left") {
            public void actionPerformed(ActionEvent e) {ipbi.setRTLDirection();}});
        
        ButtonGroup mutuallyExclusiveButtons = new ButtonGroup();
        mutuallyExclusiveButtons.add(leftToRight);
        mutuallyExclusiveButtons.add(rightToLeft);
        rightToLeft.setSelected(true);
        p.add(leftToRight,BorderLayout.EAST);        
        p.add(rightToLeft,BorderLayout.EAST);
        return p;
	}

		
	
	public void setValue(int value){
		bar.setValue(value);
	}
	
	public static GridLayout getGridLayout(){
		if (gl != null) return gl;
		gl = new GridLayout(1,cols);
		return gl;
	}


	@Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) 
        	ipbi.setFrames((int)source.getValue());
    }

	public void setDarkAndLightColor(Color dark, Color light) {
		ipbi.setLightAndDarkColor(light, dark);
	}
	

}	
