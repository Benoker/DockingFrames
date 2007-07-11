package bibliothek.chess.model;

public abstract class Behavior {
    
    public abstract void moving( Board board, Figure figure, int r, int c );
    
    /**
     * Visits all cells which are reachable within one step from this
     * figure.
     * @param visitor the visitor to call
     */
    public abstract void reachable( Board board, Figure figure, Board.CellVisitor visitor );
    
    public abstract void attackable( Board board, Figure figure, Board.CellVisitor visitor );
    
    protected Board.CellVisitor createAttackVisitor( final Board board, final Figure figure, final Board.CellVisitor original ){
        return new Board.CellVisitor(){
            public boolean visit( int r, int c, Figure f ) {
                Board copy = board.copy();
                copy.move( figure.getRow(), figure.getColumn(), r, c );
                copy.builtAttackMatrix();
                if( !copy.isKingAttacked() )
                    return original.visit( r, c, figure );
                else
                    return true;
            }
        };
    }
}
