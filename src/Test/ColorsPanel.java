package Test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

public class ColorsPanel extends JPanel implements ActionListener{

	private ArrayList<ProgressPanel> list;
	private JPanel scroller = new JPanel();
	private JScrollPane scrollPane;
	
	public ColorsPanel(ArrayList<ProgressPanel> list){
		this.list = list;		
		setPreferredSize(new Dimension(200,100));
		setLayout(new BorderLayout());
		add(getScrollPane(),BorderLayout.CENTER);
		
	}


	
	private JComponent getScrollPane(){			
		scroller.setBackground(Color.WHITE);
		scroller.setLayout(new FlowLayout(FlowLayout.LEADING, 2, 4));
		scrollPane = new JScrollPane(scroller);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		Color[] middle = list.get(0).getDefaultColor();
		for (int i = 0; i < middle.length; i++) {
			scroller.add(new ColorPanel(middle[i],this));	
		}
		changeColors(false);
		scroller.add(new ImageButton("images/add.png","ADD",false,35,this));
		return scrollPane;
	}		


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ADD")){
			scroller.add(new ColorPanel(this),scroller.getComponentCount()-1);
			scrollPane.repaint();
			scrollPane.validate();
			return;
		}
		if (e.getActionCommand().equals("left"))
			changeComponentPosition(-1,((Component)e.getSource()));
		else if (e.getActionCommand().equals("right"))
			changeComponentPosition(1,((Component)e.getSource()));
		else if (e.getActionCommand().equals("delete")){
			scroller.remove((ColorPanel)e.getSource());
			scrollPane.repaint();
			scrollPane.validate();
		}
		//else if (e.getActionCommand().equals("ColorChanged"))
		changeColors(true);	
	}
	

	
	public void changeComponentPosition(int relative, Component comp){
		Component[] set = scroller.getComponents();
		if (comp == set[0] && relative == -1) return;
		if (comp == set[set.length-2] && relative == +1) return;
		int i = 0;
		while ((i < set.length) && (set[i] != comp)) i++;
		if (i == set.length) return;
		Component temp = set[i + relative];
		set[i + relative] = comp;
		set[i] = temp;
		int start = i;
		if (relative < 0) start--;
		int j = start;
		while (j < set.length) {
			scroller.remove(set[j]);
			j++;
		}
		while (start < set.length) {
			scroller.add(set[start]);
			start++;
		}				
		scrollPane.repaint();
		scrollPane.validate();				
	}
	
	
	public void changeColors(boolean update){
		ArrayList<Color> light =  new ArrayList<>();
		ArrayList<Color> dark =  new ArrayList<>();
		ArrayList<Color> together =  new ArrayList<>();
		
		for (int i = 0; i < scroller.getComponentCount(); i++) 
			if (scroller.getComponent(i) instanceof ColorPanel){
				ColorPanel temp = (ColorPanel) scroller.getComponent(i);
				Color l = temp.getLight();
				Color d = temp.getDark();
				Color t = temp.getBetween();
				if (l != null && d != null) {
					light.add(l);
					dark.add(d);
					if (t == null) together = null;
					if (together != null)						
						together.add(t);
				}
			}
		
		if (update)
			for (int i = 0; i < list.size(); i++) 
				list.get(i).setDarkAndLightColor(dark.toArray(new Color[1]), light.toArray(new Color[1]));
		
		setCode(dark, light, together);	
	}
	
	
	public void setCode(ArrayList<Color> dark, ArrayList<Color> light, ArrayList<Color> together){
		CodeTextArea code = CodeTextArea.getCodeTextArea("Self-generated Code");
		code.clear();
		String type = "large";
		if (together != null) { 
			code.appendUnChecked(colorArrayToString("col", together),type);
			code.appendUnChecked("\n     ...\n", type);
			code.appendUnChecked("ipbi.setDarkLightColors(col);",type);
		}
		else {
			code.appendUnChecked(colorArrayToString("dark", dark),type);
			code.appendUnChecked(colorArrayToString("ligh", light),type);
			code.appendUnChecked("\n     ...\n",type);
			code.appendUnChecked("setDarkAndLightColor(dark,light);",type);
		}
		
	}
	
	public String colorArrayToString(String name, ArrayList<Color> l){
		String s = "private Color[] " + name + " = {";
		
		//Color.BLUE,Color.CYAN,Color.GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.YELLOW};		
		for (int i = 0; i < l.size(); i++) {
			s += "new Color(" + l.get(i).getRed()  + "," + l.get(i).getGreen()  + "," + l.get(i).getBlue() + ")";
			if (i < l.size()) s+= ", ";
		}				
		return s + "};";
	}
	
		
	
	
	
class ColorPanel extends JPanel implements ActionListener{
	
	private int bigDim = 56;
	private int lowDim = 15;
	ColorButton between;
	ColorButton light;
	ColorButton dark;
	ImageButton close;
	GridBagConstraints c;
	JPanel p;
	ActionListener act;

	
	public ColorPanel(ActionListener act){
		this(null,null,act);
	}	
	
	public ColorPanel(Color ini,ActionListener act){
		this(ini.darker(),ini.brighter(),act);
		between.setBackground(ini);
		between.repaint();
	}
	
	public ColorPanel(Color darker,Color brighter,ActionListener act){
		super();
		this.act = act;
		p = new JPanel();
		p.setBorder(new EtchedBorder(2));
		light = new ColorButton(brighter, "light", this,true,bigDim);
		light.setMargin(new Insets(0, 0, 0, 0));
		
		dark = new ColorButton(darker, "dark", this,true,bigDim);
		dark.setMargin(new Insets(0, 0, 0, 0));				
		
		//between = new ColorButton(null, "between", this,true,lowDim);		
		between = new ColorButton(null, "between", this,true,lowDim);
		between.setMargin(new Insets(0, 0, 0, 0));
		
		ImageButton left = new ImageButton("images/left.png", "left",false,lowDim);
		left.addSourcedActionListener(act,this);
		left.setMargin(new Insets(0, 0, 0, 0));
		left.setBackground(Color.WHITE);
		ImageButton right = new ImageButton("images/right.png", "right",false,lowDim);
		right.addSourcedActionListener(act,this);
		right.setMargin(new Insets(0, 0, 0, 0));
		right.setBackground(Color.WHITE);
		close = new ImageButton("images/delete.png","delete",false,lowDim);
		close.addSourcedActionListener(act,this);
		close.setMargin(new Insets(0, 0, 0, 0));
		
		p.setLayout(new GridBagLayout());
		c = new GridBagConstraints();
		c.weightx = 3;
		addGrid(0,0,3,3,light);
		addGrid(0,3,3,3,dark);
		
		c.weightx = 1;
		addGrid(0,6,1,1,left);
		addGrid(1,6,1,1,between);
		addGrid(2,6,1,1,right);

		setLayout(new AngleLayout());
		add(close,AngleLayout.SOUTHEAST);

		add(p,AngleLayout.CENTER);
		setBackground(Color.WHITE);

	}
	
	
	private void addGrid(int x,int y, int h, int w, JComponent comp){
		c.gridx = x;
		c.gridy = y;
		c.gridheight = h;
		c.gridwidth = w;		
		p.add(comp,c);
	}

	public void actionPerformed(ActionEvent e) {
//		if (e.getActionCommand().equals("light")) scroller.remove((Component)e.getSource());
//		else if (e.getActionCommand().equals("dark")) scroller.remove((Component)e.getSource());
		if (e.getActionCommand().equals("between")) {
			light.setBackground(between.getBackground().brighter());
			dark.setBackground(between.getBackground().darker());
		}
		else between.setBackground(null);
		act.actionPerformed(new ActionEvent(this, 0, "ColorChanged"));
		
	}
	
	
	public Color getLight(){
		return light.getBackground();
	}
	
	public Color getDark(){
		return dark.getBackground();
	}

	public Color getBetween(){
		return between.getBackground();
	}

	

}





}
