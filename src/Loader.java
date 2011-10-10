import javax.swing.JFrame;

import Test.IllusionProgressBarTest;


public class Loader extends JFrame {
 	
	public Loader(){
		super();
		getContentPane().add(new IllusionProgressBarTest());
		pack();
		setVisible(true);			
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Illusion Progress Bar");
		
	}
	
	public static void main(String[] args) {
		new Loader();
	}

}
