package bibliothek.chess.model;

/**
 * A ChessListener is added to a {@link Board} using {@link Board#addListener(ChessListener)}
 * and gets informed whenever a state of the board changes.
 * @author Benjamin Sigg
 */
public interface ChessListener {
	/**
	 * Called when a figure gets killed.
	 * @param r the row in which the figure stood
	 * @param c the column in which the figure stood
	 * @param figure the figure that has just been removed
	 */
    public void killed( int r, int c, Figure figure );
    
    /**
     * Called when a figure changes its location.
     * @param sr the row in which the figure stood before the move
     * @param sc the column in which the figure stood before the move
     * @param dr the current row
     * @param dc the current column
     * @param figure the figure which has changed its location
     */
    public void moved( int sr, int sc, int dr, int dc, Figure figure );
    
    /**
     * Called when a player finished his turn.
     * @param player the current player
     */
    public void playerSwitched( Player player );
}
