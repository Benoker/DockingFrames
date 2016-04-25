package bibliothek.chess.model;

import bibliothek.chess.model.Board.CellVisitor;

/**
 * A {@link Behavior} describing what a knight can do on a chess-board.
 * @author Benjamin Sigg
 */
public class Knight extends Behavior{
    @Override
    public void moving( Board board, Figure figure, int r, int c ) {
        // ignore
    }
    
    @Override
    public void attackable( Board board, Figure figure, CellVisitor visitor ) {
        reachable( board, figure, visitor, true );
    }
    
    @Override
    public void reachable( Board board, Figure figure, CellVisitor visitor ) {
        reachable( board, figure, visitor, false );
    }
    
    private void reachable( Board board, Figure figure, CellVisitor visitor, boolean ignoreAttack ){
        if( !ignoreAttack )
            visitor = createAttackVisitor( board, figure, visitor );
        
        for( int i = 0; i < 8; i++ ){
            int r = figure.getRow();
            int c = figure.getColumn();
            
            switch( i ){
                case 0: r += 1; c += 2; break;
                case 1: r += 1; c -= 2; break;
                case 2: r -= 1; c += 2; break;
                case 3: r -= 1; c -= 2; break;
                case 4: r += 2; c += 1; break;
                case 5: r += 2; c -= 1; break;
                case 6: r -= 2; c += 1; break;
                case 7: r -= 2; c -= 1; break;
            }
            
            if( board.isValid( r, c ) && (ignoreAttack || !board.isPlayer( r, c, figure.getPlayer() )))
                if( !board.visit( r, c, visitor ))
                    return;
        }
    }
}
