package Test;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton implements ActionListener{


		private static final long serialVersionUID = 1L;
		
		//private ArrayList<ProgressPanel> list;	
		private int imageDim = 50;
		private String type;
		private Component alternativeSource = null;
		private ActionListener act = null;
		
		
		public ImageButton(String imagePath,String command){
			this(imagePath,command,false,50);
		}
		
		public ImageButton(String imagePath,String command, boolean border,int side){
			this(imagePath,command,border,side,null);
		}
		
		public ImageButton(String imagePath,String command, boolean border,int side,ActionListener lis){			
			super();
			imageDim = side;
			setBorderPainted(border);
		    setContentAreaFilled(false);
		    setBorderPainted(false);
		    ImageIcon buttonIcon = getImageIcon(imagePath,imageDim,imageDim);
			if (buttonIcon != null) setIcon(buttonIcon);
			else setText(command);
			setActionCommand(command);
			if (lis == null) addActionListener(this);
			else addActionListener(lis);
		}

	public static ImageIcon getImageIcon(String path, int width,int height) {
		BufferedImage scaled = null;
		try{			
			URL url = Class.class.getResource("/" + path);
			/* Alternative URL url = loader.getClass().getClassLoader().getResource(path);*/
			if (url == null){
				Path p = Paths.get(path);
				url = p.toUri().toURL();		
			}

			BufferedImage image = ImageIO.read(url);
			scaled = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaled.createGraphics();
			AffineTransform at = AffineTransform.getScaleInstance((double)width/image.getWidth(),(double)height/image.getHeight());
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawRenderedImage(image,at);										
		} catch (IOException e1) { return null;}
		return new ImageIcon(scaled);
	}

	public void addSourcedActionListener(ActionListener act,Component source){
		alternativeSource = source;
		this.act = act;
	}

	public void setActionListener(ActionListener act){
		this.act = act;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (alternativeSource != null) e.setSource(alternativeSource);
		if (act != null) act.actionPerformed(e);
	}

	
}
