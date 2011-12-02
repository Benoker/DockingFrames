package bibliothek.chess.model;

/**
 * This enumeration marks the two players that play the game.
 * @author Benjamin Sigg
 *
 */
public enum Player {
	WHITE, BLACK;
	
	/**
	 * Gets the opponent player of <code>this</code>.
	 * @return the opponent
	 */
	public Player opponent(){
	    if( this == WHITE )
	        return BLACK;
	    else
	        return WHITE;
	}
}
