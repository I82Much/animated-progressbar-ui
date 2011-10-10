package Test;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;

public class ColorButton extends JButton implements ActionListener {


	private static final long serialVersionUID = 1L;
	
	//private ArrayList<ProgressPanel> list;	
	private int imageDim = 50;
	private JColorChooser chooser = null;	
	private Dialog dialog = null;
	private ActionListener act;
	private String type;
	private Color realColor;
	
	
	public ColorButton(Color ini, String type, ActionListener act){
		this(ini,type,act,false);
	}
	
	
	public ColorButton(Color ini, String type, ActionListener act,boolean border){
		this(ini,type,act,false,50);
	}
	
	public ColorButton(Color ini, String type, ActionListener act,boolean border,int side){		
		super();
		imageDim = side;
		this.type = type;
		this.act = act;
		realColor = ini;
		if (ini == null) chooser = new JColorChooser(Color.WHITE);
		else chooser = new JColorChooser(ini);
		dialog = JColorChooser.createDialog(this,"Select" + type + "Color",true, chooser, this, null);
		setActionCommand(type);
		setBackground(ini);
		addActionListener(this);
		setContentAreaFilled(false);
		setBorderPainted(border);
	}

	public ImageIcon getColorImageIcon(int width,int height,Color c) {
		BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(c);
		g.fillRect(0, 0, width, height);							
		return new ImageIcon(image);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this) dialog.setVisible(true);
		else if (e.getActionCommand() == "OK") setColor();				
	}
		
	private void setColor(){
		Color newColor = chooser.getColor();
        setBackground(newColor);
        act.actionPerformed(new ActionEvent(this, 0, type));
	}

	public void setBackground(Color color){
		realColor = color;
		if (color != null) {
			super.setBackground(color);
			if (imageDim <1) return;
			setIcon(getColorImageIcon(imageDim,imageDim,color));
			setToolTipText(color.toString());
			}
		else {
			super.setBackground(Color.WHITE);
			if (imageDim <1) return;
			setIcon(ImageButton.getImageIcon("images/empty.png", imageDim, imageDim));
			setToolTipText("No Color");
		}
	}
	
	public Color getBackground(){
		return realColor;
	}

}
