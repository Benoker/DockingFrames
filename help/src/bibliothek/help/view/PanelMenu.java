package bibliothek.help.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockFrontendListener;

/**
 * A menu that contains a {@link JCheckBoxMenuItem} for every
 * {@link Dockable} known to the {@link DockFrontend}. The user
 * can show and hide <code>Dockable</code>s by clicking on these
 * items.
 * @author Benjamin Sigg
 *
 */
public class PanelMenu extends JMenu{
    /** the frontend which is used to show or hide <code>Dockable</code>s */
	private DockFrontend frontend;
	
	/**
	 * Creates a new menu that observes <code>frontend</code>. The items
	 * of the menu are read once from <code>frontend</code>, and will not
	 * change when new <code>Dockable</code>s are registered to <code>frontend</code>.
	 * @param frontend the list of <code>Dockable</code>s
	 */
	public PanelMenu( DockFrontend frontend ){
		this.frontend = frontend;
		setText( "Panels" );
		
		for( Dockable dockable : frontend.listDockables() )
			add( new Item( dockable ));
	}
	
	/**
	 * A menu item showing the visibility-state of one {@link Dockable}.
	 * @author Benjamin Sigg
	 */
	private class Item extends JCheckBoxMenuItem implements ActionListener, DockFrontendListener{
		/** whether the state currently is changing or not */
	    private boolean onChange = false;
	    /** the <code>Dockable</code> whose visibility-state is represented by this item */
		private Dockable dockable;

		/**
		 * Creates a new item.
		 * @param dockable the element whose visibility-state is represented by this item.
		 */
		public Item( Dockable dockable ){
			this.dockable = dockable;
			frontend.addFrontendListener( this );
			setIcon( dockable.getTitleIcon() );
			setText( dockable.getTitleText() );
			setSelected( frontend.isShown( dockable ));
			addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e ){
			if( !onChange ){
				onChange = true;
				try{
					if( isSelected() )
						frontend.show( dockable );
					else
						frontend.hide( dockable );
				}
				finally{
					onChange = false;
				}
			}
		}

		public void hidden( DockFrontend fronend, Dockable dockable ){
			if( dockable == this.dockable ){
				onChange = true;
				setSelected( false );
				onChange = false;
			}
		}

		public void shown( DockFrontend frontend, Dockable dockable ){
			if( dockable == this.dockable ){
				onChange = true;
				setSelected( true );
				onChange = false;
			}
		}

		public void added( DockFrontend frontend, Dockable dockable ) {
		    // ignore
		}
		
		public void removed( DockFrontend frontend, Dockable dockable ) {
		    // ignore
		}
		
		public void deleted( DockFrontend frontend, String name ){
			// ignore
		}

		public void loaded( DockFrontend frontend, String name ){
			// ignore
		}

		public void saved( DockFrontend frontend, String name ){
			// ignore
		}
		
		public void hideable( DockFrontend frontend, Dockable dockable, boolean hideable ) {
		    // ignore
		}
		
		public void read( DockFrontend frontend, String name ){
			// ignore
		}
	}
}
