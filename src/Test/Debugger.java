package Test;



/*
 * Add this template to eclipse->window->preference->java->editor->template
 * Debugger.debug("${cursor}",1,"${primary_type_name}.${enclosing_method} " + (new Exception().getStackTrace()[0].getLineNumber()) +": ");
 */
public class Debugger {

	public static boolean[] active = {false,true,true,true,true,true};


	public static void debug(String msg, int activeNum,String text){
		if (active[activeNum]) 
			System.out.println(activeNum + " " + text + msg);			
	}
	
	
}
