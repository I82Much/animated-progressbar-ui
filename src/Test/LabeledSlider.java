package Test;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LabeledSlider extends JSlider implements ChangeListener, AncestorListener{

	private static final long serialVersionUID = 1L;
	private JLabel slide;

	
	public  LabeledSlider(int max,int moltiplier, int val,String name,int width,int height, ChangeListener ext){
		super(0, max*moltiplier);        
        setPreferredSize(new Dimension(width, height));
        putClientProperty("JSlider.isFilled", Boolean.TRUE);
        slide = new JLabel("");
        add(slide);
    	slide.setVisible(true); 
    	slide.setForeground(Color.BLACK);
        setMajorTickSpacing(max);
        putClientProperty( "JSlider.isFilled", Boolean.TRUE );
        //setPaintTicks(true);
        //setPaintLabels(true);
        setName(name);
        setValue(val);
        addChangeListener(this);
        addChangeListener(ext);        
        addAncestorListener(this);
	}
	
	
	@Override
    public void stateChanged(ChangeEvent e) {
		setLabel();
	}
	
	public void setLabel(){
		int val = getValue();
		slide.setText(val+"");
		if (getOrientation() == JProgressBar.VERTICAL){
			int lh = getBounds().height - (val * (getBounds().height) /getMaximum());
			slide.setBounds(0, lh, slide.getPreferredSize().width, slide.getPreferredSize().height);
		}
		else{
			double cent = (1.0*getValue()/getMaximum()); 
			int k = 0;
			if (getValue() >9 ) k = (new Integer(getValue())).toString().length();
			double xx = slide.getPreferredSize().width* (1.0 -2*cent + k/3.0*(cent-1));			
			int lh = (int) ((val * (1.0*getBounds().width)/getMaximum()) + xx );
			
			slide.setBounds(lh, getBounds().height/2 - slide.getPreferredSize().height/2 -2, slide.getPreferredSize().width, slide.getPreferredSize().height);	
		}


		
	}


	@Override
	public void ancestorAdded(AncestorEvent event) {
		setLabel();
		removeAncestorListener(this);
	}

	@Override
	public void ancestorMoved(AncestorEvent event) {}

	@Override
	public void ancestorRemoved(AncestorEvent event) {}
	
}	
