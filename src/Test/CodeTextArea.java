package Test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CodeTextArea extends JPanel {

	
	private static String[] shadow = {"   ...",			
								"   ipbi.setShadowPainted(true);",
								"   ...",
								"   IllusionProgressBarShadow ipbs = ipbi.getShadowFactory();",
								"   ...",
								"   Set methods in IllusionProgressBarShadow",
							 	"   public void setShadowDark(Color shadowDark)",
								"   public void setShadowLight(Color shadowLight)",
								"   public void setShadowStartGapFromMiddlePercent(double percent)",  		
								"   public void setShadowEndFromBottomPercent(double percent)"};
	
	
	private static String[] shadowGet = {"Getter methods",
									" public Color getShadowDark()",
									"   public Color getShadowLight()",
									"   public double getShadowStartGapFromMiddlePercent()",
									"   public double getShadowEndFromBottomPercent()"};

	
	private static String[] code = {
			"   ...",	
			"   bar.setUI(ipbi = new IllusionProgressBarUI(bar));",
			"   ipbi.setRoundCorner(IllusionProgressBarCoord.RoundOVAL);",
			"   ipbi.setDarkLightColors(col);",
			"   ipbi.setBorder(1);",
			"   ipbi.setShadowPainted(true);",
			"   ipbi.setHighQuality(true);"};
	
	private StyledDocument doc;
	private JTextPane jtp;

	
	private static ArrayList<CodeTextArea> codes = null;

	public CodeTextArea(String path,String name){
		this(name,false);
		setTextFile(path);
	}
	
	public CodeTextArea(String path,String name,boolean linewrap){
		this(name,linewrap);
		setTextFile(path);
	}
	
	public CodeTextArea(String name){
		this(name,false);
	}
	
	public CodeTextArea(String name,boolean linewrap){
		super();
		setName(name);	
		if (codes == null) codes = new ArrayList<>();
		codes.add(this);
		jtp = new JTextPane();
		jtp.setEditable(false);
		

		doc = jtp.getStyledDocument();		
        addStylesToDocument(doc);

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(100,100));

        if (linewrap) {
        	JScrollPane scrollPane = new JScrollPane(jtp);
        	add(scrollPane,BorderLayout.CENTER);
    		}
        else {
        	JPanel lineUnWrapper = new JPanel(new BorderLayout());
        	lineUnWrapper.add(jtp,BorderLayout.CENTER);
        	JScrollPane scrollPane = new JScrollPane(lineUnWrapper);
        	add(scrollPane,BorderLayout.CENTER);
    		}

	}
	
	
	public void appendBold(int b) throws BadLocationException{
		if (b >= code.length) throw new IndexOutOfBoundsException("Position not valid " + b);
		for (int i = 0; i < b; i++)
			append(code[i],"regular");
		append(code[b],"largeBold");
		for (int j = b+1; j < code.length; j++) 
			append(code[j], "regular");		
	}
	
	
	public void append(String s,String style) throws BadLocationException{
		doc.insertString(doc.getLength(),s + "\n",doc.getStyle(style));		
	}
	
	
	public void clear()  {
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void appendUnChecked(String s,String style) {
		try {	
			doc.insertString(doc.getLength(),s + "\n",doc.getStyle(style));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}		
	}

	
	 protected static void addStylesToDocument(StyledDocument doc) {
	        //Initialize some styles.
	        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

	        Style regular = doc.addStyle("regular", def);
	        StyleConstants.setFontFamily(def, "SansSerif");

	        Style s = doc.addStyle("italic", regular);
	        StyleConstants.setItalic(s, true);

	        Style bold = doc.addStyle("bold", regular);
	        StyleConstants.setBold(bold, true);

	        s = doc.addStyle("small", regular);
	        StyleConstants.setFontSize(s, 14);

	        s = doc.addStyle("large", regular);
	        StyleConstants.setFontSize(s, 16);
	        
	        s = doc.addStyle("largeBold", bold);
	        StyleConstants.setFontSize(s, 16);
        

	        s = doc.addStyle("error", regular);
	        StyleConstants.setForeground(s, Color.red);	        
	        
	 }
	

	 public void setTextFile(String path){
		 try{
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			if (url == null){
				Path p = Paths.get(path);
				url = p.toUri().toURL();
				}
			jtp.setPage(url);
		} catch (IOException e) { 
			appendUnChecked(e.toString(),"error ");}
	 }
	 
	 
	 public static CodeTextArea getCodeTextArea(String name){
		 if (codes == null) return null;
		 int i = 0;
		 while ((i < codes.size()) && (!codes.get(i).getName().equals(name))) i++;
		 if (i < codes.size()) return codes.get(i);
		 return null;
	 }

	
}

   	