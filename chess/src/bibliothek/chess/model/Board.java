package bibliothek.chess.model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static enum State{
        CHECK, CHECKMATE, STALLED, NOTHING
    }
    
    private Figure[][] figures = new Figure[8][8];
    private boolean[][] attacked = new boolean[8][8];
    private Player player = Player.WHITE;
    
    private List<ChessListener> listeners = new ArrayList<ChessListener>();
    
    private Figure white;
    private Figure black;
    
    public Board(){
        for( int i = 0; i < 8; i++ ){
            put( new Figure( this, Player.WHITE, Figure.Type.PAWN, 1, i ));
            put( new Figure( this, Player.BLACK, Figure.Type.PAWN, 6, i ));
        }
        
        put( new Figure( this, Player.WHITE, Figure.Type.ROCK, 0, 0 ));
        put( new Figure( this, Player.WHITE, Figure.Type.KNIGHT, 0, 1 ));
        put( new Figure( this, Player.WHITE, Figure.Type.BISHOP, 0, 2 ));
        put( new Figure( this, Player.WHITE, Figure.Type.QUEEN, 0, 3 ));
        put( white = new Figure( this, Player.WHITE, Figure.Type.KING, 0, 4 ));
        put( new Figure( this, Player.WHITE, Figure.Type.BISHOP, 0, 5 ));
        put( new Figure( this, Player.WHITE, Figure.Type.KNIGHT, 0, 6 ));
        put( new Figure( this, Player.WHITE, Figure.Type.ROCK, 0, 7 ));
        
        put( new Figure( this, Player.BLACK, Figure.Type.ROCK, 7, 0 ));
        put( new Figure( this, Player.BLACK, Figure.Type.KNIGHT, 7, 1 ));
        put( new Figure( this, Player.BLACK, Figure.Type.BISHOP, 7, 2 ));
        put( new Figure( this, Player.BLACK, Figure.Type.QUEEN, 7, 3 ));
        put( black = new Figure( this, Player.BLACK, Figure.Type.KING, 7, 4 ));
        put( new Figure( this, Player.BLACK, Figure.Type.BISHOP, 7, 5 ));
        put( new Figure( this, Player.BLACK, Figure.Type.KNIGHT, 7, 6 ));
        put( new Figure( this, Player.BLACK, Figure.Type.ROCK, 7, 7 ));
    }
    
    private Board( Board board ){
        for( int r = 0; r < 8; r++ ){
            for( int c = 0; c < 8; c++ ){
                if( board.figures[r][c] != null ){
                    figures[r][c] = board.figures[r][c].copy( this );
                    if( figures[r][c].getType() == Figure.Type.KING ){
                        if( figures[r][c].getPlayer() == Player.WHITE )
                            white = figures[r][c];
                        else
                            black = figures[r][c];
                    }
                }
                attacked[r][c] = board.attacked[r][c];
                player = board.player;
            }
        }
    }
    
    public Board copy(){
        return new Board( this );
    }
    
    public void addListener( ChessListener listener ){
        listeners.add( listener );
    }
    
    public void removeListener( ChessListener listener ){
        listeners.remove( listener );
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Figure pawnReplacement(){
        for( int c = 0; c < 8; c++ ){
            if( isType( 0, c, Figure.Type.PAWN ))
                return getFigure( 0, c );
            
            if( isType( 7, c, Figure.Type.PAWN ))
                return getFigure( 7, c );
        }
        
        return null;
    }
    
    public void switchPlayer(){
        player = player.opponent();
        builtAttackMatrix();
        
        for( ChessListener listener : listeners )
            listener.playerSwitched( player );
    }
    
    public void builtAttackMatrix(){
        for( int r = 0; r < 8; r++ )
            for( int c = 0; c < 8; c++ )
                attacked[r][c] = false;
        
        for( int r = 0; r < 8; r++ ){
            for( int c = 0; c < 8; c++ ){
                if( figures[r][c] != null ){
                    if( figures[r][c].getPlayer() == player.opponent() ){
                        figures[r][c].attackable( new CellVisitor(){
                            public boolean visit( int r, int c, Figure figure ) {
                                attacked[r][c] = true;
                                return true;
                            }
                        });
                    }
                }
            }
        }
    }
    
    public State state(){
        // check whether at least one move is possible
        boolean moveable = false;
        for( int r = 0; !moveable && r < 8; r++ ){
            for( int c = 0; !moveable && c < 8; c++ ){
                if( figures[r][c] != null ){
                    if( figures[r][c].getPlayer() == player ){
                        moveable = figures[r][c].moveable();
                    }
                }
            }
        }
        
        if( isKingAttacked() ){
            if( moveable )
                return State.CHECK;
            else
                return State.CHECKMATE;
        }
        else{
            if( moveable )
                return State.NOTHING;
            else
                return State.STALLED;
        }
    }
    
    public void put( Figure figure ){
        figures[figure.getRow()][figure.getColumn()] = figure;
    }
    
    public Figure getFigure( int r, int c ){
        return figures[r][c];
    }
    
    public Figure getKing( Player player ){
        if( player == Player.WHITE )
            return white;
        else
            return black;
    }
    
    /**
     * Tells whether the cell r/c is currently under attack by the
     * players opponent.
     * @param r the row
     * @param c the column
     * @return <code>true</code> if the cell is under attack
     */
    public boolean isAttacked( int r, int c ){
        return attacked[r][c];
    }
    
    public boolean isKingAttacked(){
        Figure king = getKing( player );
        return isAttacked( king.getRow(), king.getColumn() );
    }
    
    public void kill( int r, int c ){
        Figure figure = figures[r][c];
        figures[r][c] = null;
        
        for( ChessListener listener : listeners )
            listener.killed( r, c, figure );
    }
    
    public void move( int sr, int sc, int dr, int dc ){
        Figure figure = figures[sr][sc];
        figure.setLocation( dr, dc );
        
        figures[sr][sc] = null;
        if( figures[dr][dc] != null )
            kill( dr, dc );
        figures[dr][dc] = figure;
        
        for( ChessListener listener : listeners )
            listener.moved( sr, sc, dr, dc, figure );
    }
    
    public boolean isPlayer( int r, int c, Player player ){
        Figure figure = getFigure( r, c );
        if( figure == null )
            return false;
        return figure.getPlayer() == player;
    }
    
    public boolean isType( int r, int c, Figure.Type type ){
        Figure figure = getFigure( r, c );
        if( figure == null )
            return false;
        else
            return figure.getType() == type;
    }
    
    public boolean isEmpty( int r, int c ){
        return getFigure( r, c ) == null;
    }
    
    public boolean isValid( int r, int c ){
        return r >= 0 && c >= 0 && r < 8 && c < 8;
    }
    
    /**
     * Visits field r/c.
     * @param r the row
     * @param c the column
     * @param visitor the visitor
     * @return <code>true</code> if other locations should be visited,
     * <code>false</code> if the algorithm should stop
     */
    public boolean visit( int r, int c, CellVisitor visitor ){
        return visitor.visit( r, c, figures[r][c] );
    }
    
    public static interface CellVisitor{
        public boolean visit( int r, int c, Figure figure );
    }
}
