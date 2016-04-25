package bibliothek.chess.model;

import bibliothek.chess.model.Board.CellVisitor;

/**
 * A {@link Behavior} describing what a king can do on a chess-board.
 * @author Benjamin Sigg
 */
public class King extends Behavior{
    @Override
    public void moving( Board board, Figure figure, int r, int c ) {
        if( Math.abs( figure.getColumn()-c ) > 1 ){
            // Rochade
            if( c == 2 )
                board.move( r, 0, r, 3 );
            else
                board.move( r, 7, r, 5 );
        }
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
                case 0: r++; break;
                case 1: r++; c++; break;
                case 2: r++; c--; break;
                case 3: c++; break;
                case 4: c--; break;
                case 5: r--; break;
                case 6: r--; c++; break;
                case 7: r--; c--; break;
            }
            
            if( board.isValid( r, c ) ){
                if( ignoreAttack ){
                    if( !board.visit( r, c, visitor ))
                        return;
                }
                else if( board.isEmpty( r, c ) || board.isPlayer( r, c, figure.getPlayer().opponent() )){
                    if( !board.isAttacked( r, c ))
                        if( !board.visit( r, c, visitor ))
                            return;
                }
            }
        }
        
        if( !figure.isMoved() ){
            // Rochade
            if( board.isKingAttacked() )
                return;
            
            int r = figure.getRow();
            int c = figure.getColumn();
            
            if( board.isEmpty( r, c+1 ) && !board.isAttacked( r, c+1 ) && 
                    board.isEmpty( r, c+2 ) && !board.isAttacked( r, c+2 ) ){
                Figure rock = board.getFigure( r, c+3 );
                if( rock != null && rock.getType() == Figure.Type.ROCK ){
                    if( rock.getPlayer() == figure.getPlayer() && !rock.isMoved())
                        if( !board.visit( r, c+2, visitor ))
                            return;
                }
            }
            
            if(  board.isEmpty( r, c-1 ) && !board.isAttacked( r, c-1 ) && 
                    board.isEmpty( r, c-2 ) && !board.isAttacked( r, c-2 ) && 
                    board.isEmpty( r, c-3 ) ){
                Figure rock = board.getFigure( r, c-4 );
                if( rock != null && rock.getType() == Figure.Type.ROCK ){
                    if( rock.getPlayer() == figure.getPlayer() && !rock.isMoved())
                        if( !board.visit( r, c-2, visitor ))
                            return;
                }
            }
        }
    }
}
