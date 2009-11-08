package bibliothek.chess.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.util.xml.XElement;

/**
 * A {@link DockableProperty} that stores the field in which a {@link ChessFigure}
 * stands on a {@link ChessBoard}.
 * @author Benjamin Sigg
 */
public class ChessBoardProperty implements DockableProperty {
	/**
	 * A factory creating new instances of {@link ChessBoardProperty}.
	 */
	public static final DockablePropertyFactory FACTORY = new DockablePropertyFactory(){
		public String getID(){
			return "chess board";
		}
		
		public DockableProperty createProperty(){
			return new ChessBoardProperty( -1, -1 );
		}
	};
	
	/** properties used if a whole path has to be described */
	private DockableProperty successor;
	
	/** the row in which the figure stands */
	private int row;
	/** the column in which the figure stands */
	private int column;
	
	/**
	 * Creates a new property.
	 * @param row the row in which the figure stands
	 * @param column the column in which the figure stands
	 */
	public ChessBoardProperty( int row, int column ){
		this.row = row;
		this.column = column;
	}
	
	public DockableProperty copy() {
	    ChessBoardProperty copy = new ChessBoardProperty( row, column );
	    if( successor != null )
	        copy.successor = successor.copy();
	    return copy;
	}
	
	/**
	 * Gets the row in which the field lies that contains the figure.
	 * @return the row
	 */
	public int getRow(){
		return row;
	}
	
	/**
	 * Gets the column in which the field lies that contains the figure.
	 * @return the column
	 */
	public int getColumn(){
		return column;
	}
	
	public String getFactoryID(){
		return FACTORY.getID();
	}

	public DockableProperty getSuccessor(){
		return successor;
	}

	public void store( DataOutputStream out ) throws IOException{
        out.writeInt( row );
        out.writeInt( column );
    }
	
	public void load( DataInputStream in ) throws IOException{
		row = in.readInt();
		column = in.readInt();
	}
	
	public void store( XElement element ) {
	    element.addElement( "row" ).setInt( row );
	    element.addElement( "column" ).setInt( column );
	}
	
	public void load( XElement element ) {
	    row = element.getElement( "row" ).getInt();
	    column = element.getElement( "column" ).getInt();
	}
	
	public void setSuccessor( DockableProperty properties ){
		successor = properties;
	}

	public boolean equalsNoSuccessor( DockableProperty property ){
		// never used in this application, ignore
		return false;
	}
}
