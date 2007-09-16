package bibliothek.notes.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 * A note is a combination of some text, a small description, an image
 * and a color used when the note is displayed somewhere.
 * @author Benjamin Sigg
 */
public class Note {
	/** The message of this note */
	private String text;
	/** A small description of this note */
	private String title;
	/** An image for this note */
	private Icon icon;
	/** A color often used to paint this note */
	private Color color;
	
	/** a unique id */
	private String id;
	
	/** the list of listeners which are informed when properties of this note change */
	private List<NoteListener> listeners = new ArrayList<NoteListener>();
	
	/**
	 * Creates a new, empty note.
	 * @param id a unique id
	 */
	public Note( String id ){
		this.id = id;
		
		title = "";
		text = "";
		color = Color.WHITE;
	}
	
	@Override
	public boolean equals( Object obj ){
		if( obj == null )
			return false;
		
		if( obj.getClass().equals( getClass() )){
			return ((Note)obj).id.equals( id );
		}
		
		return false;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	/**
	 * Adds an observer to this note, the <code>listener</code> will be
	 * informed whenever a property of this note changes.
	 * @param listener the listener
	 */
	public void addListener( NoteListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes a listener from this note.
	 * @param listener the listener to remove
	 */
	public void removeListener( NoteListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets an immutable list of all {@link NoteListener}s which are
	 * currently registered at this note.
	 * @return the list of listeners
	 */
	protected NoteListener[] listListeners(){
		return listeners.toArray( new NoteListener[ listeners.size() ]); 
	}
	
	/**
	 * Gets the unique and immutable identifier of this note.
	 * @return the identifier
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Gets the message of this note.
	 * @return the message
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * Sets the message of this note, informs all {@link NoteListener}.
	 * @param text the new message
	 */
	public void setText( String text ){
		this.text = text;
		for( NoteListener listener : listListeners() )
			listener.textChanged( this );
	}
	
	/**
	 * Gets the short description of this note.
	 * @return the short description
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Sets the short description of this note, informs all {@link NoteListener}.
	 * @param title the short description
	 */
	public void setTitle( String title ){
		this.title = title;
		for( NoteListener listener : listListeners() )
			listener.titleChanged( this );
	}
	
	/**
	 * Gets the image of this note.
	 * @return the image
	 */
	public Icon getIcon(){
		return icon;
	}
	
	/**
	 * Sets the image of this note, informs all {@link NoteListener}.
	 * @param icon the new image
	 */
	public void setIcon( Icon icon ){
		this.icon = icon;
		for( NoteListener listener : listListeners() )
			listener.iconChanged( this );
	}
	
	/**
	 * Gets the color of this note.
	 * @return the color
	 */
	public Color getColor(){
		return color;
	}
	
	/**
	 * Sets the color of this note, informs all {@link NoteListener}. The color
	 * is used to paint the message of this note.
	 * @param color the new color
	 */
	public void setColor( Color color ){
		this.color = color;
		for( NoteListener listener : listListeners() )
			listener.colorChanged( this );
	}
}
