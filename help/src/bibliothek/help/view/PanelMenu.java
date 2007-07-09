package bibliothek.help.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockFrontendListener;

public class PanelMenu extends JMenu{
	private DockFrontend frontend;
	
	public PanelMenu( DockFrontend frontend ){
		this.frontend = frontend;
		setText( "Panels" );
		
		for( Dockable dockable : frontend.listDockables() )
			add( new Item( dockable ));
	}
	
	private class Item extends JCheckBoxMenuItem implements ActionListener, DockFrontendListener{
		private boolean onChange = false;
		private Dockable dockable;
		
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

		public void showed( DockFrontend frontend, Dockable dockable ){
			if( dockable == this.dockable ){
				onChange = true;
				setSelected( true );
				onChange = false;
			}
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
	}
}
