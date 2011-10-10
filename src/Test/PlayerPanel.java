package Test;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PlayerPanel extends JPanel implements Runnable, ActionListener{
 

		private static final long serialVersionUID = 1L;
		private ArrayList<ProgressPanel> list;	
		private static Thread t = null;
		private static int value = 0;
		private int buttonHigh = 30;

		
		
		public PlayerPanel(ArrayList<ProgressPanel> list){
			this.list = list;
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, -4));
			add(new ImageButton("images/play.png","Play", false,buttonHigh,this));
			add(new ImageButton("images/pause.png","Pause", false,buttonHigh,this));
			add(new ImageButton("images/stop.png","Stop", false,buttonHigh,this));
			add(new ImageButton("images/previous.png","Previous", false,buttonHigh,this));
			add(new ImageButton("images/next.png","Next", false,buttonHigh,this));
		}

		 
		public void setArrayListener(ArrayList<ProgressPanel> list){
			this.list = list;
		}
		

		@Override
		public void actionPerformed(ActionEvent e) {		
			switch (e.getActionCommand()) {
			case "Play":  	play(); 						break;
			case "Stop":  	stop(); 						break;	
			case "Pause": 	pause();						break;			
			case "Next":  	incrVal(); 						break;
			case "Previous":decrVal(); 						break;
			default:										break;
			}
		}


		
		
		private static void pause(){
			if (t != null) 
				t = null;
		}
		
		private void play(){
			if (t != null) return;
			t = new Thread(this);
			t.start();
		}
			
		
		private synchronized boolean stop(){
			pause();
			return setVal(0);
		}
			
		private synchronized boolean setVal(int val){
			if ((val < 0) || (val>100)) return false;
			value = val;
			for (int i = 0; i < list.size(); i++) 
				list.get(i).setValue(value);
			return true;
		}
		
		private synchronized boolean incrVal(){
			if (value == 100) return false;
			value++;
			for (int i = 0; i < list.size(); i++) 
				list.get(i).setValue(value);
			return true;
		}
		
		
		private synchronized boolean decrVal(){
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
		
		
}	