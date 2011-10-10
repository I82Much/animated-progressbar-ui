package Test;
import java.util.ArrayList;

import javax.swing.JTabbedPane;


public class IllusionProgressBarTestEffect extends JTabbedPane {
	
	
	private ArrayList<ProgressPanel> list;

	
	public IllusionProgressBarTestEffect( ArrayList<ProgressPanel> list){
		super();
		this.list = list;
		addTab("Shadow", new ShadowPanel(list));
		addTab("Colors", new ColorsPanel(list));
	}

	
}
