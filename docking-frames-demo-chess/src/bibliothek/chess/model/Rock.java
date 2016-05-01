package bibliothek.chess.model;

import bibliothek.chess.model.Board.CellVisitor;

/**
 * A {@link Behavior} describing what a rock can do on a chess-board.
 * @author Benjamin Sigg
 */
public class Rock extends Behavior{
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
        
        for( int i = 0; i < 4; i++ ){
            int r = figure.getRow();
            int c = figure.getColumn();
            
            int dr = 0;
            int dc = 0;
            
            switch( i ){
                case 0: dc =  1; break;
                case 1: dc = -1; break;
                case 2: dr =  1; break;
                case 3: dr = -1; break;
            }
            
            r += dr;
            c += dc;
            
            while( board.isValid( r, c ) ){
                if( board.isEmpty( r, c )){
                    if( !board.visit( r, c, visitor ) )
                        return;
                }
                else if( board.isPlayer( r, c, figure.getPlayer().opponent() )){
                    if( !board.visit( r, c, visitor ))
                        return;
                    break;
                }
                else if( ignoreAttack ){
                    if( !board.visit( r, c, visitor ))
                        return;
                    break;
                }
                else
                    break;
                
                r += dr;
                c += dc;
            }
        }
    }
}
