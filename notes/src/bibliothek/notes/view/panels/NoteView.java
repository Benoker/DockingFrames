package bibliothek.notes.view.panels;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.notes.model.Note;
import bibliothek.notes.model.NoteListener;
import bibliothek.notes.model.NoteModel;
import bibliothek.notes.view.NoteViewManager;
import bibliothek.notes.view.actions.ColorAction;
import bibliothek.notes.view.actions.CopyAction;
import bibliothek.notes.view.actions.IconAction;
import bibliothek.notes.view.actions.TitleAction;

public class NoteView extends DefaultDockable implements NoteListener{
	private JTextPane textPane = new JTextPane();
	
	private NoteViewManager manager;
	private NoteModel model;
	private Note note;
	
	private DefaultDockActionSource source = new DefaultDockActionSource(
			new LocationHint( LocationHint.DOCKABLE, LocationHint.LEFT ));
	
	public NoteView( NoteViewManager manager, NoteModel model ){
		this.manager = manager;
		this.model = model;
		
		setFactoryID( NoteViewFactory.FACTORY_ID );
		setActionOffers( source );
		
		setLayout( new BorderLayout() );
		add( new JScrollPane( textPane ) );
		
		textPane.getDocument().addDocumentListener( new DocumentListener(){
			public void changedUpdate( DocumentEvent e ){
				updateText();
			}

			public void insertUpdate( DocumentEvent e ){
				updateText();
			}

			public void removeUpdate( DocumentEvent e ){
				updateText();
			}
		});
	}
	
	public Note getNote(){
		return note;
	}
	
	public void setNote( Note note ){
		if( this.note != null ){
			this.note.removeListener( this );
			source.remove( 0, source.getDockActionCount() );
		}
		
		this.note = null;
		
		if( note != null ){
			note.addListener( this );
			
			setTitleText( note.getTitle() );
			setTitleIcon( note.getIcon() );
			textPane.setText( note.getText() );
			textPane.setBackground( note.getColor() );
			
			source.add( new IconAction( note ) );
			source.add( new TitleAction( note ) );
			source.add( new ColorAction( note ) );
			source.add( new CopyAction( manager, model, note ) );
			
			this.note = note;
		}
	}
	
	public void textChanged( Note note ){
		if( this.note != null ){
			Note old = this.note;
			this.note = null;
			textPane.setText( old.getText() );
			this.note = old;
		}
	}
	
	private void updateText(){
		if( note != null ){
			Note old = note;
			note = null;
			old.setText( textPane.getText() );
			note = old;
		}
	}
	
	public void titleChanged( Note note ){
		setTitleText( this.note.getTitle() );
	}
	
	public void iconChanged( Note note ){
		setTitleIcon( this.note.getIcon() );
	}
	
	public void colorChanged( Note note ){
		textPane.setBackground( this.note.getColor() );
	}
}
