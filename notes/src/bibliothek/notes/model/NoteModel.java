package bibliothek.notes.model;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bibliothek.notes.util.ResourceSet;

public class NoteModel implements Iterable<Note>{
	private Map<String, Note> notes = new HashMap<String, Note>();
	private List<NoteModelListener> listeners = new ArrayList<NoteModelListener>();
	
	private long nextId = 0;
	
	public Iterator<Note> iterator(){
		return notes.values().iterator();
	}
	
	public void addNoteModelListener( NoteModelListener listener ){
		listeners.add( listener );
	}
	
	public void removeNoteModelListener( NoteModelListener listener ){
		listeners.remove( listener );
	}
	
	protected NoteModelListener[] listListeners(){
		return listeners.toArray( new NoteModelListener[ listeners.size() ] );
	}
	
	public Note addNote(){
		Note note = new Note( String.valueOf( nextId++ ) );
		notes.put( note.getId(), note );
		
		for( NoteModelListener listener : listeners )
			listener.noteAdded( this, note );
		
		return note;
	}
	
	public void removeNote( Note note ){
		notes.remove( note.getId() );
		for( NoteModelListener listener : listeners )
			listener.noteRemoved( this, note );
	}
	
	public Note getNote( String id ){
		return notes.get( id );
	}
	
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
}
