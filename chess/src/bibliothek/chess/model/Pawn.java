package bibliothek.chess.model;

import bibliothek.chess.model.Board.CellVisitor;

/**
 * A {@link Behavior} describing what a pawn can do on a chess-board.
 * @author Benjamin Sigg
 */
public class Pawn extends Behavior{
    
    @Override
    public void moving( Board board, Figure figure, int r, int c ) {
	    if( figure.getColumn() != c ){
	        // kill
	        if( board.isEmpty( r, c )){
	            // en passant
	            board.kill( figure.getRow(), c );
	        }
	    }
	}
	
	
	@Override
	public void attackable( Board board, Figure figure, CellVisitor visitor ) {
        int plus;
        
        if( figure.getPlayer() == Player.WHITE )
            plus = 1;
        else
            plus = -1;

        int r = figure.getRow();
        int c = figure.getColumn();
        
        r += plus;
        
        if( board.isValid( r, c+1 ))
            if( !board.visit( r, c+1, visitor ))
                return;
        
        if( board.isValid( r, c-1 ))
            if( !board.visit( r, c-1, visitor ))
                return;
	}
	
	@Override
    public void reachable( Board board, Figure figure, CellVisitor visitor ){
        visitor = createAttackVisitor( board, figure, visitor );
	    
	    int plus;
	    
	    if( figure.getPlayer() == Player.WHITE )
	        plus = 1;
	    else
	        plus = -1;

	    int r = figure.getRow();
	    int c = figure.getColumn();
	    
	    r += plus;
	    
	    // move normal
	    if( board.isValid( r, c ) && board.isEmpty( r, c )){
	        if( !board.visit( r, c, visitor ))
	            return;
	        if( !figure.isMoved() ){
	            r += plus;
	            if( board.isValid( r, c ) && board.isEmpty( r, c ))
	                if( !board.visit( r, c, visitor ))
	                    return;
	        }
	    }
	    
	    // catch
	    r = figure.getRow()+plus;
	    if( board.isValid( r, c+1 ) && board.isPlayer( r, c+1, figure.getPlayer().opponent() ))
	        if( !board.visit( r, c+1, visitor ))
	            return;
	    if( board.isValid( r, c-1 ) && board.isPlayer( r, c-1, figure.getPlayer().opponent() ))
            if( !board.visit( r, c-1, visitor ))
                return;
	    
	    // en passant
	    int enpassent;
	    
	    if( figure.getPlayer() == Player.WHITE )
	        enpassent = 4;
	    else
	        enpassent = 3;
	    
	    if( enpassent == figure.getRow() ){
    	    for( int i = 0; i < 2; i++ ){
    	        c = figure.getColumn();
    	        r = figure.getRow();
    	        if( i == 0 )
    	            c++;
    	        else
    	            c--;
    	        
    	        if( board.isValid( r, c )){
    	            Figure pawn = board.getFigure( r, c );
    	            if( pawn != null && pawn.getType() == Figure.Type.PAWN ){
    	                if( pawn.isJustMoved() && pawn.getPlayer().opponent() == figure.getPlayer() )
    	                    if( !board.visit( r+plus, c, visitor ))
    	                        return;
    	            }
    	        }
    	    }
	    }
	}
}
