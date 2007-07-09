package bibliothek.demonstration;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.action.ActionPopupSuppressor;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.security.SecureDockController;
import bibliothek.gui.dock.station.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockTree;

public class MainPanel extends GlassedPane {
	private JList list = new JList();
	private CardLayout layout = new CardLayout();
	private JPanel panel;
	
	public MainPanel( Core core, List<Demonstration> demos ){
		SecureDockController controller = new SecureDockController();
		controller.getFocusObserver().addGlassPane( this );
		controller.addAcceptance( new DockAcceptance(){
			public boolean accept( DockStation parent, Dockable child ){
				return true;
			}

			public boolean accept( DockStation parent, Dockable child, Dockable next ){
				return false;
			}			
		});
		controller.setSingleParentRemove( true );
		controller.setTheme( new FlatTheme() );
		controller.setPopupSuppressor( ActionPopupSuppressor.SUPPRESS_ALWAYS );
		
		SplitDockStation station = new SplitDockStation();
		setContentPane( station );
		
		controller.add( station );
		station.setContinousDisplay( true );
		
		DefaultListModel model = new DefaultListModel();
		panel = new JPanel( layout );
		int index = 0;
		
		for( Demonstration demo : demos ){
			model.addElement( demo );
			
			DemoPanel demoPanel = new DemoPanel( core, demo );
			panel.add( demoPanel, String.valueOf( index ) );
			
			index++;
		}
		
		/*add( new JScrollPane( list ), new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, 
				new Insets( 2, 2, 2, 2 ), 0, 0 ));
		add( panel, new GridBagConstraints( 1, 0, 1, 1, 100.0, 1.0, 
				GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets( 2, 2, 2, 2 ), 0, 0 ));*/
		
		SplitDockTree tree = new SplitDockTree();
		tree.root( tree.horizontal( 
				new DefaultDockable( new JScrollPane( list ), "Demonstrations" ),
				new DefaultDockable( panel, "Selection" ),
				0.25 ));
		station.dropTree( tree );
		
		list.addListSelectionListener( new ListSelectionListener(){
			public void valueChanged( ListSelectionEvent e ){
				layout.show( panel, String.valueOf( list.getSelectedIndex()));
			}
		});
		
		list.setModel( model );
		list.setCellRenderer( new Renderer() );
		
		if( model.getSize() > 0 )
			list.setSelectedIndex( 0 );
	}
	
	private class Renderer extends DefaultListCellRenderer{
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ){
			Demonstration demo = (Demonstration)value;
			super.getListCellRendererComponent( list, demo.getName(), index, isSelected, cellHasFocus );
			setIcon( demo.getIcon() );
			return this;
		}
	}
}
