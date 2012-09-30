package bibliothek.gui.dock.toolbar;

import javax.swing.JButton;

import junit.framework.Assert;

import org.junit.Test;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.xml.XElement;

public class PersistenceTest {
	@Test
	public void storeAndLoad(){
		DockFrontend frontend = new DockFrontend();
		
		ToolbarContainerDockStation container = new ToolbarContainerDockStation( Orientation.HORIZONTAL );
		frontend.addRoot( "container", container );
		
		ToolbarItemDockable item = new ToolbarItemDockable( new JButton( "something" ) );
		frontend.addDockable( "item", item );
		container.drop( item );
		
		XElement element = new XElement( "root" );
		frontend.writeXML( element );
		frontend.readXML( element );
		
		Assert.assertTrue( DockUtilities.isAncestor( container, item ) );
	}
}
