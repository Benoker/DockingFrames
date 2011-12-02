package bibliothek.help.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockFrontendListener;

/**
 * A menu that creates an entry for every layout that a {@link DockFrontend}
 * provides. There is also an entry to save the current layout.
 * @author Benjamin Sigg
 *
 */
public class LayoutMenu extends JMenu implements DockFrontendListener{
	private DockFrontend frontend;
	private Set<String> settings = new HashSet<String>();
	
	public LayoutMenu( DockFrontend frontend ){
		this.frontend = frontend;
		setText( "Layout" );
		
		JMenuItem store = new JMenuItem( "Save current layout" );
		add( store );
		store.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				LayoutMenu.this.frontend.save( "Layout: " + 
						(1+LayoutMenu.this.frontend.getSettings().size()) );
			}
		});
		
		frontend.addFrontendListener( this );
	}
	
	public void saved( DockFrontend frontend, final String name ){
		if( settings.isEmpty() )
			addSeparator();
		
		if( settings.add( name ) ){
			JMenuItem item = new JMenuItem( name );
			item.addActionListener( new ActionListener(){
				public void actionPerformed( ActionEvent e ){
					LayoutMenu.this.frontend.load( name );
				}
			});
			add( item );
		}
	}

	public void loaded( DockFrontend frontend, String name ){
		// ignore
	}

	public void deleted( DockFrontend frontend, String name ){
		// ignore
	}

	public void hidden( DockFrontend fronend, Dockable dockable ){
		// ignore
	}

	public void shown( DockFrontend frontend, Dockable dockable ){
		// ignore
	}
	
	public void added( DockFrontend frontend, Dockable dockable ) {
	    // ignore
	}
	
	public void hideable( DockFrontend frontend, Dockable dockable, boolean hideable ) {
	    // ignore
	}
	
	public void removed( DockFrontend frontend, Dockable dockable ) {
	    // ignore
	}
	
	public void read( DockFrontend frontend, String name ){
		// ignore
	}
}
