package bibliothek.chess.model;

/**
 * A behavior describes how a {@link Figure} reacts on certain events.
 * @author Benjamin Sigg
 *
 */
public abstract class Behavior {
    
	/**
	 * Invoked when <code>figure</code> is moved to row <code>r</code> of
	 * column <code>c</code> on the <code>board</code>. This method might
	 * call methods of <code>board</code> if the event "moving" requests
	 * for more than just moving one figure.
	 * @param board the board on which the figure is
	 * @param figure the moved figure
	 * @param r the new row
	 * @param c the new column
	 */
    public abstract void moving( Board board, Figure figure, int r, int c );
    
    /**
     * Visits all cells which are reachable within one step from this
     * figure.
     * @param visitor the visitor to call
     */
    public abstract void reachable( Board board, Figure figure, Board.CellVisitor visitor );
   
    /**
     * Visits all cells which are attackable by <code>figure</code>.
     * @param board the board on which <code>figure</code> stands
     * @param figure the figure which might attack other figures
     * @param visitor the visitor to call
     */
    public abstract void attackable( Board board, Figure figure, Board.CellVisitor visitor );
    
    /**
     * Creates a new {@link bibliothek.chess.model.Board.CellVisitor} which
     * visits the <code>original</code> visitor only if the king would not be
     * attacked when moving the visited figure. 
     * @param board the board on which the figure stands
     * @param figure the figure which might be moved
     * @param original the original visitor which is only called for legal moves.
     * @return the new visitor
     */
    protected Board.CellVisitor createAttackVisitor( final Board board, final Figure figure, final Board.CellVisitor original ){
        return new Board.CellVisitor(){
            public boolean visit( int r, int c, Figure f ) {
                Board copy = board.copy();
                copy.move( figure.getRow(), figure.getColumn(), r, c );
                copy.buildAttackMatrix();
                if( !copy.isKingAttacked() )
                    return original.visit( r, c, figure );
                else
                    return true;
            }
        };
    }
}
