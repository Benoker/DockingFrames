package bibliothek.notes;

import java.awt.Image;

import javax.swing.Icon;

import bibliothek.demonstration.Demonstration;
import bibliothek.demonstration.Monitor;
import bibliothek.notes.util.ResourceSet;

public class Webstart implements Demonstration{
	public static void main( String[] args ){
		Core core = new Core( true, null );
		core.startup();
	}

	public String getHTML(){
		return "<html><h1>Notes</h1><br>A client</html>";
	}

	public Icon getIcon(){
		return ResourceSet.APPLICATION_ICONS.get( "application" );
	}

	public Image getImage(){
		// TODO Auto-generated method stub
		return null;
	}

	public String getName(){
		return "Notes";
	}

	public void show( Monitor monitor ){
		monitor.startup();
		Core core = new Core( true, monitor );
		core.startup();
	}
}
