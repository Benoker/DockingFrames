package bibliothek.chess.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.dock.DockableProperty;
import bibliothek.gui.dock.DockablePropertyFactory;

public class ChessBoardProperty implements DockableProperty {
	public static final DockablePropertyFactory FACTORY = new DockablePropertyFactory(){
		public String getID(){
			return "chess board";
		}
		
		public DockableProperty createProperty(){
			return new ChessBoardProperty( -1, -1 );
		}
	};
	
	private DockableProperty successor;
	
	private int row;
	private int column;
	
	public ChessBoardProperty( int row, int column ){
		this.row = row;
		this.column = column;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return column;
	}
	
	public String getFactoryID(){
		return FACTORY.getID();
	}

	public DockableProperty getSuccessor(){
		return successor;
	}

	public void load( DataInputStream in ) throws IOException{
		row = in.readInt();
		column = in.readInt();
	}

	public void setSuccessor( DockableProperty properties ){
		successor = properties;
	}

	public void store( DataOutputStream out ) throws IOException{
		out.writeInt( row );
		out.writeInt( column );
	}
}
