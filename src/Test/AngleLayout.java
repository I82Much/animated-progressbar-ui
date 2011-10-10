package Test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;


public class AngleLayout extends BorderLayout{

	
	public static final String NORTHWEST = "NW";
	public static final String NORTHEAST = "NE";
	public static final String SOUTHWEST = "SW";
	public static final String SOUTHEAST = "SE";
	public Component nw = null;
	public Component ne = null;
	public Component sw = null;
	public Component se = null;
			

	
	
    public void addLayoutComponent(Component comp, Object constraints){
    	if (!(constraints instanceof String)) super.addLayoutComponent(comp, constraints);
    	else {
    		String name = (String)constraints;
    		switch (name) {
    		case 	NORTHWEST:	nw = comp;							break;
    		case 	NORTHEAST:	ne = comp;							break;
    		case 	SOUTHWEST:	sw = comp;							break;
    		case 	SOUTHEAST:	se = comp;							break;
    		default:	super.addLayoutComponent(comp, constraints);break;
    		}
    	}

    }


    
    

    
    public Insets getCornerInsets(Component c){
    	Insets in = new Insets(0,0,0,0);
    	if (c == null) return in;
    	if (c == se || c == ne) in.right = c.getPreferredSize().width/2;
    	if (c == sw || c == nw) in.left = c.getPreferredSize().width/2;
    	if (c == se || c == sw) in.bottom = c.getPreferredSize().height/2;
    	if (c == ne || c == nw) in.top = c.getPreferredSize().height/2;
    	return in;
    	
    }
    
    public Insets getMaxCompund(Insets[] in){
    	Insets res = new Insets(0,0,0,0);
    	for (int i = 0; i < in.length; i++) {
			res.top = Math.max(res.top, in[i].top);
			res.left = Math.max(res.left, in[i].left);
			res.bottom = Math.max(res.bottom, in[i].bottom);
			res.right = Math.max(res.right, in[i].right);
		}
    	return res;
    }

    
    public Insets minus(Insets max,Insets min){
    	return new Insets(max.top-min.top,max.left-min.left,max.bottom-min.bottom,max.right-min.right);
    }

    
    public void layoutContainer(Container target) {
    	synchronized (target.getTreeLock()) {
    	if (target instanceof JComponent){    		
    		Insets in = getMaxCompund(new Insets[]{
    									getCornerInsets(se),
    									getCornerInsets(ne),
    									getCornerInsets(nw),
    									getCornerInsets(sw),
    									target.getInsets()});
    	
    		in = minus(in,target.getInsets());
    		JComponent jcomp = (JComponent) target;
    		jcomp.setBorder(new CompoundBorder(jcomp.getBorder(), new EmptyBorder(in)));
    	}
    	super.layoutContainer(target);
    	
    	Rectangle in = target.getBounds();
       
    	setRelativeBound(nw,0		,0			,0	,0);   	
    	setRelativeBound(ne,in.width,0			,-1	,0);  
    	setRelativeBound(sw,0		,in.height	,0	,-1);
    	setRelativeBound(se,in.width,in.height	,-1	,-1);
    	setRelativeBound(nw,0,0,0,0);  
        }
    }
    
    public void setRelativeBound(Component comp, int x,int y, int xMol,int yMol){
    	if (comp == null) return;
    	comp.setBounds(x + xMol*comp.getPreferredSize().width,
    				   y + yMol*comp.getPreferredSize().height,
    				   comp.getPreferredSize().width,
    				   comp.getPreferredSize().height);    	
    }
    
}
