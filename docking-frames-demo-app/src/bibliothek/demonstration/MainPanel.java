package bibliothek.demonstration;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.ActionPopupSuppressor;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;

/**
 * A panel that can show a list of {@link Demonstration}s. The panel is
 * divided in a {@link JList} showing all <code>Demonstration</code>s, and
 * a detailed view of the current selection.
 * @author Benjamin Sigg
 *
 */
public class MainPanel extends SplitDockStation {
    /** the list of all {@link Demonstration}s */
	private JList list = new JList();
	/** the layout of {@link #panel} */
	private CardLayout layout = new CardLayout();
	/** contains the detailed views for each {@link Demonstration} */
	private JPanel panel;
	
	/**
	 * Creates a new panel.
	 * @param core the center of the application, used to startup some
	 * {@link Demonstration}
	 * @param demos list of <code>Demonstration</code>s which should
	 * be available to the user
	 */
	public MainPanel( Core core, List<Demonstration> demos ){
		DockController.disableCoreWarning();
		DockController controller = new DockController();
		controller.setRestrictedEnvironment( true );
		controller.addAcceptance( new DockAcceptance(){
			public boolean accept( DockStation parent, Dockable child ){
				return true;
			}

			public boolean accept( DockStation parent, Dockable child, Dockable next ){
				return false;
			}			
		});
		controller.setTheme( new FlatTheme() );
		controller.setPopupSuppressor( ActionPopupSuppressor.SUPPRESS_ALWAYS );
		
		controller.add( this );
		setContinousDisplay( true );
		
		DefaultListModel model = new DefaultListModel();
		panel = new JPanel( layout );
		int index = 0;
		
		for( Demonstration demo : demos ){
			model.addElement( demo );
			
			DemoPanel demoPanel = new DemoPanel( core, demo );
			panel.add( demoPanel, String.valueOf( index ) );
			
			index++;
		}
		
		DockableSplitDockTree tree = new DockableSplitDockTree();
		tree.root( tree.horizontal( 
				new DefaultDockable( new JScrollPane( list ), "Demonstrations" ),
				new DefaultDockable( panel, "Selection" ),
				0.25 ));
		dropTree( tree );
		
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
	
	/**
	 * A {@link ListCellRenderer} used in {@link MainPanel#list} to show
	 * a {@link Demonstration}.
	 * @author Benjamin Sigg
	 */
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
