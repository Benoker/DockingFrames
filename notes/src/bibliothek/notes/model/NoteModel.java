package bibliothek.notes.model;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bibliothek.notes.util.ResourceSet;
import bibliothek.util.xml.XElement;

/**
 * A model managing a set of {@link Note}s. The model contains methods
 * to create new notes, or store and load them.
 * @author Benjamin Sigg
 *
 */
public class NoteModel implements Iterable<Note>{
	/** the set of notes */
	private Map<String, Note> notes = new LinkedHashMap<String, Note>();
	/** observer of this model */
	private List<NoteModelListener> listeners = new ArrayList<NoteModelListener>();
	
	/** the next unique id for new notes */
	private long nextId = 0;
	
	public Iterator<Note> iterator(){
		return notes.values().iterator();
	}
	
	/**
	 * Adds an observer to this model, the observer will be notified whenever
	 * a note is created or deleted.
	 * @param listener the new listener
	 */
	public void addNoteModelListener( NoteModelListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes an observer from this model.
	 * @param listener the observer to delete
	 */
	public void removeNoteModelListener( NoteModelListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets an immutable list of observers of this model.
	 * @return the list of {@link NoteModelListener}s
	 */
	protected NoteModelListener[] listListeners(){
		return listeners.toArray( new NoteModelListener[ listeners.size() ] );
	}
	
	/**
	 * Creates a new {@link Note} and stores it in this model.
	 * @return the new Note
	 */
	public Note addNote(){
		Note note = new Note( String.valueOf( nextId++ ) );
		notes.put( note.getId(), note );
		
		for( NoteModelListener listener : listeners )
			listener.noteAdded( this, note );
		
		return note;
	}
	
	/**
	 * Deletes <code>note</code> from this model.
	 * @param note the note to delete
	 */
	public void removeNote( Note note ){
		notes.remove( note.getId() );
		for( NoteModelListener listener : listeners )
			listener.noteRemoved( this, note );
	}
	
	/**
	 * Searches a node using the unique id that every note owns.
	 * @param id a unique id of a {@link Note}
	 * @return the <code>Note</code> or <code>null</code>
	 */
	public Note getNote( String id ){
		return notes.get( id );
	}
	
	/**
     * Stores the current set of Notes.
     * @param out the stream to write into
     * @throws IOException if the method can't write into <code>out</code>
     * @see #read(DataInputStream)
     */
    public void write( DataOutputStream out ) throws IOException{
        out.writeLong( nextId );
        out.writeInt( notes.size() );
        for( Note note : this ){
            out.writeUTF( note.getId() );
            out.writeUTF( note.getTitle() );
            out.writeUTF( note.getText() );
            out.writeInt( note.getColor().getRGB() );
            out.writeInt( ResourceSet.NOTE_ICONS.indexOf( note.getIcon() ));
        }
    }
	
	/**
	 * Reads a set of notes that were early written using {@link #write(DataOutputStream)}.
	 * @param in the stream to read from
	 * @throws IOException if the stream can't be read
	 */
	public void read( DataInputStream in ) throws IOException{
		List<Note> notes = new ArrayList<Note>( this.notes.values() );
		for( Note note : notes )
			removeNote( note );
		
		nextId = in.readLong();
		int count = in.readInt();
		
		for( int i = 0; i < count; i++ ){
			Note note = new Note( in.readUTF() );
			note.setTitle( in.readUTF() );
			note.setText( in.readUTF() );
			note.setColor( new Color( in.readInt() ) );
			int index = in.readInt();
			if( index >= 0 && index < ResourceSet.NOTE_ICONS.size() )
				note.setIcon( ResourceSet.NOTE_ICONS.get( index ) );
			
			this.notes.put( note.getId(), note );
			
			for( NoteModelListener listener : listeners )
				listener.noteAdded( this, note );
			
		}
	}
	
	/**
     * Stores the current set of Notes.
     * @param element the xml element to write into
     */
    public void writeXML( XElement element ){
        element.addElement( "next" ).setLong( nextId );
        XElement xnotes = element.addElement( "notes" );
        
        for( Note note : this ){
            XElement xnote = xnotes.addElement( "note" );
            xnote.addString( "id", note.getId() );
            xnote.addElement( "title" ).setString( note.getTitle() );
            xnote.addElement( "text" ).setString( note.getText() );
            xnote.addElement( "color" ).setInt( note.getColor().getRGB() );
            if( note.getIcon() != null ){
                xnote.addElement( "icon" ).setInt( ResourceSet.NOTE_ICONS.indexOf( note.getIcon() ) );
            }
        }
    }
    
    /**
     * Reads a set of Notes that were early written using {@link #writeXML(XElement)}.
     * @param element the xml element to read from
     */
    public void readXML( XElement element ) {
        List<Note> notes = new ArrayList<Note>( this.notes.values() );
        for( Note note : notes )
            removeNote( note );
        
        nextId = element.getElement( "next" ).getLong();
        
        for( XElement xnote : element.getElement( "notes" ).getElements( "note" )){
            Note note = new Note( xnote.getString( "id" ));
            note.setTitle( xnote.getElement( "title" ).getString() );
            note.setText( xnote.getElement( "text" ).getString() );
            note.setColor( new Color( xnote.getElement( "color" ).getInt() ));
            XElement xicon = xnote.getElement( "icon" );
            if( xicon != null ){
                note.setIcon( ResourceSet.NOTE_ICONS.get( xicon.getInt() ));
            }
            
            this.notes.put( note.getId(), note );
            
            for( NoteModelListener listener : listeners )
                listener.noteAdded( this, note );
        }
    }
}
