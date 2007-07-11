package bibliothek.chess.model;

public enum Player {
	WHITE, BLACK;
	
	public Player opponent(){
	    if( this == WHITE )
	        return BLACK;
	    else
	        return WHITE;
	}
}
