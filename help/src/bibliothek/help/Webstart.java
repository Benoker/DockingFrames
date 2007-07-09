package bibliothek.help;

import java.awt.Image;

import javax.swing.Icon;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;

public class Webstart implements Demonstration{
	public static void main( String[] args ){
		new Core( true, null ).startup();
	}
	
	public String getHTML(){
		return "<html><h1>Help</h1>BlaBla</html>";
	}

	public Icon getIcon(){
		return null;
	}

	public Image getImage(){
		return null;
	}

	public String getName(){
		return "Help";
	}

	public void show( Monitor monitor ){
		monitor.startup();
		Core core = new Core( true, monitor );
		core.startup();
	}
}
