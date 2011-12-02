package bibliothek.notes.view.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteListener;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.model.NoteModelListener;
import bibliothek.notes.util.ResourceSet;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.actions.ListDeleteAction;
import bibliothek.notes.view.actions.ListNewAction;

/**
 * A {@link Dockable} showing a list of all {@link Note}s that are currently
 * known to the application. The user can double-click on one of the {@link Note}s
 * in order to show the corresponding {@link NoteView}.
 * @author Benjamin Sigg
 *
 */
public class ListView extends DefaultDockable implements NoteModelListener, NoteListener, ListSelectionListener{
    /** the list of Notes */
	private JList list;
	/** the model of {@link #list} */
	private DefaultListModel listModel;
	
	/** the manager used to show the views of the {@link Note}s */
	private NoteViewManager manager;
	
	/** The action used to delete the selected {@link Note} */
	private ListDeleteAction deleteAction;
	
	/**
	 * Creates a new list.
	 * @param manager used to show the {@link Note}s
	 * @param model the set of {@link Note}s known to this application
	 */
	public ListView( NoteViewManager manager, NoteModel model ){
		this.manager = manager;

		deleteAction = new ListDeleteAction( this, model );
		deleteAction.setEnabled( false );
		
		listModel = new DefaultListModel();
		list = new JList( listModel );
		list.setCellRenderer( new NoteRenderer() );
		list.addMouseListener( new Listener() );
		list.addListSelectionListener( this );
		
		setLayout( new BorderLayout() );
		add( new JScrollPane( list ), BorderLayout.CENTER );
		
		setTitleText( "Notes" );
		setTitleIcon( ResourceSet.APPLICATION_ICONS.get( "list" ) );
		
		model.addNoteModelListener( this );
		
		DefaultDockActionSource source = new DefaultDockActionSource( 
				new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ));
		source.add( new ListNewAction( manager, model ) );
		source.add( deleteAction );
		setActionOffers( source );
	}
	
	public void valueChanged( ListSelectionEvent e ){
		deleteAction.setEnabled( getSelected() != null );
	}
	
	public void noteAdded( NoteModel model, Note note ){
		listModel.addElement( note );
		note.addListener( this );
	}
	
	public void noteRemoved( NoteModel model, Note note ){
		listModel.removeElement( note );
		note.removeListener( this );
	}
	
	public void titleChanged( Note note ){
		int index = listModel.indexOf( note );
		if( index >= 0 )
			listModel.set( index, note );
	}
	
	public void iconChanged( Note note ){
		int index = listModel.indexOf( note );
		if( index >= 0 )
			listModel.set( index, note );
	}
	
	public void colorChanged( Note note ){
		// ignore
	}
	
	public void textChanged( Note note ){
		// ignore
	}
	
	/**
	 * Gets the currently selected {@link Note}.
	 * @return the selected <code>Note</code> or <code>null</code>
	 */
	public Note getSelected(){
		return (Note)list.getSelectedValue();
	}
	
	/**
	 * A listener to the list of a {@link ListView}, opens the {@link Dockable}
	 * of a {@link Note} when the user double-clicks onto the <code>Note</code>.
	 * @author Benjamin Sigg
	 */
	private class Listener extends MouseAdapter{
		@Override
		public void mouseClicked( MouseEvent e ){
			if( e.getClickCount() == 2 ){
				int index = list.locationToIndex( e.getPoint() );
				if( index >= 0 ){
					Note note = (Note)listModel.get( index );
					manager.show( note );
				}
			}
		}
	}
	
	/**
	 * A cell-renderer, displaying the title and icon of a {@link Note}.
	 * @author Benjamin Sigg
	 */
	private class NoteRenderer extends DefaultListCellRenderer{
		@Override
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ){
			super.getListCellRendererComponent( list, "", index, isSelected, cellHasFocus );
			
			Note note = (Note)value;
			setIcon( note.getIcon() );
			
			String text = note.getTitle();
			if( text == null || text.trim().length() == 0 )
				text = " - ";
			
			setText( text );
			return this;
		}
	}
}
