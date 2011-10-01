import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class IllusionProgressBarTest extends JPanel implements Runnable,ActionListener{


	private static final long serialVersionUID = 1L;
	private ArrayList<ProgressPanel> list;	
	private static Thread t = null;
	private static int value = 0;
	private static int imageDim = 50;
	private JButton dark;
	private JButton light;
	private JColorChooser lightChooser = null;
	private JColorChooser darkChooser = null;
	private Dialog darkDialog = null;
	private Dialog lightDialog = null;
	private boolean all = true;
	private boolean vertical = true;
	private boolean horizontal = true;
	private int height = 40;
	
	public IllusionProgressBarTest(){
		list = new ArrayList<>();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));	
		add(getRunnerBarPanel());		
		if (horizontal) add(getHorizontalPanel());
		if (vertical) add(getVerticalPanel());
    }
	
	
	private JPanel getHorizontalPanel(){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		if (all){
			p.add(getProgressBarPanel(100,height));
			p.add(getProgressBarPanel(200,height));
			p.add(getProgressBarPanel(300,height));
		}
		p.add(getProgressBarPanel(600,height));
		
		return p;
	}
	
	private JPanel getVerticalPanel(){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.LINE_AXIS));
		if (all){
			p.add(getProgressBarPanel(100,height,JProgressBar.VERTICAL));
			p.add(getProgressBarPanel(200,height,JProgressBar.VERTICAL));
			p.add(getProgressBarPanel(300,height,JProgressBar.VERTICAL));
		}
		p.add(getProgressBarPanel(400,height,JProgressBar.VERTICAL));
		return p;
	}

	

	private Component getProgressBarPanel(int width,int height, int orientation) {
		ProgressPanel pp = new ProgressPanel(width, height,orientation);
		list.add(pp);
		return pp;
	}


	public JPanel getProgressBarPanel(int width,int height){
		ProgressPanel pp = new ProgressPanel(width, height);
		list.add(pp);
		return pp;
	}
	
	
	
	
	public ImageIcon getImageIcon(Path path, int width,int height) {
		BufferedImage scaled = null;
		try {
			BufferedImage image = ImageIO.read(path.toUri().toURL());
			scaled = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaled.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance((double)width/image.getWidth(),(double)height/image.getHeight());
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawRenderedImage(image,at);										
		} catch (IOException e1) {return null;}
		return new ImageIcon(scaled);
	}


	
	public JButton getIconButton(String command, String path) {		
		JButton b = new JButton();
	    b.setContentAreaFilled(false);
        b.setBorderPainted(false);
		ImageIcon buttonIcon = getImageIcon(Paths.get(path),imageDim,imageDim);
		if (buttonIcon != null) b.setIcon(buttonIcon);
		else b.setText(command);
		b.setActionCommand(command);
		b.addActionListener(this);
		return b;
	}
	
	public JPanel getRunnerBarPanel(){    
		JPanel p = new JPanel();
		p.add(getIconButton("Play", "images/NN - Play.png"));
		p.add(getIconButton("Pause", "images/NN - Pause.png"));
		p.add(getIconButton("Stop", "images/NN - Stop.png"));
		p.add(getIconButton("Previous", "images/NN - Previous.png"));
		p.add(getIconButton("Next", "images/NN - Next.png"));
		darkChooser = new JColorChooser(IllusionProgressBarUI.DARKDEFAULT);
		lightChooser = new JColorChooser(IllusionProgressBarUI.LIGHTDEFAULT);
		darkDialog = JColorChooser.createDialog(this,"Select Dark Color",true, darkChooser, this, null);		
		lightDialog = JColorChooser.createDialog(this,"Select Light Color",true, lightChooser, this, null);		
		p.add(dark = getColorButton(IllusionProgressBarUI.DARKDEFAULT,"Dark"));
		p.add(light = getColorButton(IllusionProgressBarUI.LIGHTDEFAULT,"Light"));
		return p;
	}

	public JButton getColorButton(Color c,String command){		
		JButton b = new JButton();
		b.setActionCommand(command);
		b.setBackground(c);
		b.addActionListener(this);
		b.setIcon(getColorImageIcon(imageDim,imageDim,c));
		b.setContentAreaFilled(false);
		b.setBorderPainted(false);
		return b;
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
		switch (e.getActionCommand()) {
		case "Play":  	play(); 						break;
		case "Stop":  	stop(); 						break;	
		case "Pause": 	pause();						break;			
		case "Next":  	incrVal(); 						break;
		case "Previous":decrVal(); 						break;
		case "Dark":	darkDialog.setVisible(true);	break;
		case "Light":	lightDialog.setVisible(true);	break;
		case "OK":		setColors(e);					break;
		default:										break;
		}
	}


	private void setColors(ActionEvent e){
		if (darkDialog.isAncestorOf((Component) e.getSource())) setColor(dark,darkChooser);
		else setColor(light, lightChooser);
	}
		
	private void setColor(JButton b, JColorChooser selected){
		Color newColor = selected.getColor();
        b.setBackground(newColor);
        b.setIcon(getColorImageIcon(imageDim,imageDim,newColor));
		for (int i = 0; i < list.size(); i++)
			list.get(i).setDarkAndLightColor(dark.getBackground(),light.getBackground());

	}

	
	
	public static void pause(){
		if (t != null) 
			t = null;
	}
	
	public void play(){
		if (t != null) return;
		t = new Thread(this);
		t.start();
	}
		
	
	public synchronized boolean stop(){
		pause();
		return setVal(0);
	}
		
	public synchronized boolean setVal(int val){
		if ((val < 0) || (val>100)) return false;
		value = val;
		for (int i = 0; i < list.size(); i++) 
			list.get(i).setValue(value);
		return true;
	}
	
	public synchronized boolean incrVal(){
		if (value == 100) return false;
		value++;
		for (int i = 0; i < list.size(); i++) 
			list.get(i).setValue(value);
		return true;
	}
	
	
	public synchronized boolean decrVal(){
		if (value == 0) return false;
		value--;
		for (int i = 0; i < list.size(); i++) 
			list.get(i).setValue(value);
		return true;		
	}

	
	@Override
	public void run() {
		while (incrVal() && (t != null))
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new IllusionProgressBarTest());
		frame.pack();
		frame.setVisible(true);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
	}


	

}
