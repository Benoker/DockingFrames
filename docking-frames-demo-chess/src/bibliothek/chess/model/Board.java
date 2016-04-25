package bibliothek.chess.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A board describes all states in which a game of chess can be.
 * @author Benjamin Sigg
 *
 */
public class Board {
	/**
	 * Describes states in which the possible moves for a player might
	 * be restricted.
	 * @author Benjamin Sigg
	 */
    public static enum State{
        /** The state when the king is in danger */
    	CHECK,
    	/** The state when a player has won */
    	CHECKMATE, 
    	/** The state when a player can't move anymore */
    	STALLED,
    	/** The state where nothing special happens */
    	NOTHING
    }
    
    /** The cells which might be covered by one figure */
    private Figure[][] figures = new Figure[8][8];
    /** Tells for every cell whether it is attacked by the opponent or not */
    private boolean[][] attacked = new boolean[8][8];
    /** The player which can do the next move */
    private Player player = Player.WHITE;
    
    /**
     *  A list of {@link ChessListener} which are informed whenever the state of
     * this board changes.
     */
    private List<ChessListener> listeners = new ArrayList<ChessListener>();
    
    /** the white king */
    private Figure white;
    /** the black king */
    private Figure black;
    
    /** whether this board is a copy of the original board or not */
    private boolean copy = false;
   
    /**
     * Creates a new board, puts all figures at their initial position.
     */
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
    
    /**
     * Creates a new board copying as much as possible from <code>board</code>.
     * @param board the original board
     */
    private Board( Board board ){
        copy = true;
        
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
    
    /**
     * Gets a copy of this board. The copy is independent of this board.
     * @return the copy
     */
    public Board copy(){
        return new Board( this );
    }
    
    /**
     * Adds an observer to this board. The observer will be notified whenever
     * a state of this board changes.
     * @param listener the new observer
     */
    public void addListener( ChessListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes an observer from this board.
     * @param listener the observer to remove
     */
    public void removeListener( ChessListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the player which has the turn. 
     * @return the current player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Gets a pawn on the first or the last row.
     * @return a pawn or <code>null</code> if now pawn is on the first or
     * last row.
     */
    public Figure pawnReplacement(){
        for( int c = 0; c < 8; c++ ){
            if( isType( 0, c, Figure.Type.PAWN ))
                return getFigure( 0, c );
            
            if( isType( 7, c, Figure.Type.PAWN ))
                return getFigure( 7, c );
        }
        
        return null;
    }
    
    /**
     * Switches the players. Also rebuilds the attack-matrix.
     */
    public void switchPlayer(){
        player = player.opponent();
        buildAttackMatrix();
        
        for( ChessListener listener : listeners )
            listener.playerSwitched( player );
    }
    
    /**
     * Builds the attack-matrix. The attack-matrix tells for every cell
     * whether it is attacked by a figure of the opponent, or not.
     */
    public void buildAttackMatrix(){
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
    
    /**
     * Tells in which state the game is.
     * @return the state
     */
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
    
    /**
     * Puts the <code>figure</code> at the location delivered by the figure
     * itself.
     * @param figure the figure to add to this board.
     */
    public void put( Figure figure ){
        figures[figure.getRow()][figure.getColumn()] = figure;
    }
    
    /**
     * Gets the figure at the designated location.
     * @param r the row 
     * @param c the column
     * @return the figure or <code>null</code>
     */
    public Figure getFigure( int r, int c ){
        return figures[r][c];
    }
    
    /**
     * Gets the figure which represents the king for a given player.
     * @param player the player, black or white
     * @return the player's king
     */
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
    
    /**
     * Tells whether the king of the current player is attacked or not.
     * @return <code>true</code> if the king is attacked.
     */
    public boolean isKingAttacked(){
        Figure king = getKing( player );
        return isAttacked( king.getRow(), king.getColumn() );
    }
    
    /**
     * Removes a figure from this board.
     * @param r the row
     * @param c the column
     */
    public void kill( int r, int c ){
        Figure figure = figures[r][c];
        figures[r][c] = null;
        
        for( ChessListener listener : listeners )
            listener.killed( r, c, figure );
    }
    
    /**
     * Moves a figure on this board.
     * @param sr the current row
     * @param sc the current column
     * @param dr the row of the destination
     * @param dc the column of the destination
     */
    public void move( int sr, int sc, int dr, int dc ){
        Figure figure = figures[sr][sc];
        figure.setLocation( dr, dc );
        
        figures[sr][sc] = null;
        if( figures[dr][dc] != null )
            kill( dr, dc );
        figures[dr][dc] = figure;
        
        if( !copy ){
            for( int i = 0; i < 8; i++ ){
                for( int j = 0; j < 8; j++ ){
                    if( i != dr || j != dc ){
                        Figure check = figures[i][j];
                        if( check != null )
                            check.cleanJustMoved();
                    }
                }
            }
        }
        
        for( ChessListener listener : listeners )
            listener.moved( sr, sc, dr, dc, figure );
    }
    
    /**
     * Tells whether there is a figure of <code>player</code> at the
     * designated location.
     * @param r the row
     * @param c the column
     * @param player the player which might have a figure at <code>r/c</code>
     * @return <code>true</code> if there is a figure of <code>player</code>
     */
    public boolean isPlayer( int r, int c, Player player ){
        Figure figure = getFigure( r, c );
        if( figure == null )
            return false;
        return figure.getPlayer() == player;
    }
    
    /**
     * Tells whether the is a figure of type <code>type</code> at the
     * designated location.
     * @param r the row
     * @param c the column
     * @param type the type of the figure
     * @return <code>true</code> if a figure was found with the given <code>type</code>
     */
    public boolean isType( int r, int c, Figure.Type type ){
        Figure figure = getFigure( r, c );
        if( figure == null )
            return false;
        else
            return figure.getType() == type;
    }
    
    /**
     * Tells whether the designated cell is empty or not.
     * @param r the row
     * @param c the column
     * @return <code>true</code> if the cell is empty
     */
    public boolean isEmpty( int r, int c ){
        return getFigure( r, c ) == null;
    }
    
    /**
     * Tells whether <code>r/c</code> is a valid location.
     * @param r the row
     * @param c the column
     * @return <code>true</code> if a cell <code>r/c</code> exists.
     */
    public boolean isValid( int r, int c ){
        return r >= 0 && c >= 0 && r < 8 && c < 8;
    }
    
    /**
     * Visits field <code>r/c</code>.
     * @param r the row
     * @param c the column
     * @param visitor the visitor
     * @return <code>true</code> if other locations should be visited,
     * <code>false</code> if the algorithm should stop
     */
    public boolean visit( int r, int c, CellVisitor visitor ){
        return visitor.visit( r, c, figures[r][c] );
    }
    
    /**
     * A visitor can be used to visit a set of cells filtered by some
     * rule.
     * @author Benjamin Sigg
     */
    public static interface CellVisitor{
    	/**
    	 * Called when visiting the cell <code>r/c</code>.
    	 * @param r the row
    	 * @param c the column
    	 * @param figure the figure in this cell
    	 * @return <code>true</code> if other cells should be visited,
    	 * <code>false</code> if the algorithm should stop immediately
    	 */
        public boolean visit( int r, int c, Figure figure );
    }
}
