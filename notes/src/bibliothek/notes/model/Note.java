package bibliothek.notes.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;


public class Note {
	private String text;
	private String title;
	private Icon icon;
	private Color color;
	
	private String id;
	
	private List<NoteListener> listeners = new ArrayList<NoteListener>();
	
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
	
	public void addListener( NoteListener listener ){
		listeners.add( listener );
	}
	
	public void removeListener( NoteListener listener ){
		listeners.remove( listener );
	}
	
	protected NoteListener[] listListeners(){
		return listeners.toArray( new NoteListener[ listeners.size() ]); 
	}
	
	public String getId(){
		return id;
	}
	
	public String getText(){
		return text;
	}
	
	public void setText( String text ){
		this.text = text;
		for( NoteListener listener : listListeners() )
			listener.textChanged( this );
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle( String title ){
		this.title = title;
		for( NoteListener listener : listListeners() )
			listener.titleChanged( this );
	}
	
	public Icon getIcon(){
		return icon;
	}
	
	public void setIcon( Icon icon ){
		this.icon = icon;
		for( NoteListener listener : listListeners() )
			listener.iconChanged( this );
	}
	
	public Color getColor(){
		return color;
	}
	
	public void setColor( Color color ){
		this.color = color;
		for( NoteListener listener : listListeners() )
			listener.colorChanged( this );
	}
}
