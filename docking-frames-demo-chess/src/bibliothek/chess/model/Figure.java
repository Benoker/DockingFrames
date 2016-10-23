package bibliothek.chess.model;

import javax.swing.Icon;

import bibliothek.chess.util.Utils;
import bibliothek.util.container.Single;

/**
 * A figure is a part of a chess-board. Every figure has a {@link Behavior} that
 * describes how it can move and attack. Each figure has also an {@link Icon}
 * and belongs to a {@link Player}.
 * @author Benjamin Sigg
 */
public final class Figure {
	/**
	 * Describes what kind of figure an instance of {@link Figure} is.
	 * @author Benjamin Sigg
	 */
    public static enum Type{
    	/** most important figure of all */
        KING( "King", "k", new King() ), 
        /** most dangerous figure */
        QUEEN( "Queen", "q", new Queen() ),
        /** can move only diagonal */
        BISHOP( "Bishop", "b", new Bishop() ),
        /** can jump over other figures */
        KNIGHT( "Knight", "n", new Knight() ),
        /** can move only horizontal and vertical*/
        ROCK( "Rock", "r", new Rock() ),
        /** very slow figure */
        PAWN( "Pawn", "p", new Pawn() );
       
        /** The human readable name of this type */
        private String name;
        /** A small version of {@link #name} */
        private String sign;
        /** Describes how figures of this type behave */
        private Behavior behavior;
        
        private Type( String name, String sign, Behavior behavior ){
            this.name = name;
            this.sign = sign;
            this.behavior = behavior;
        }
        
        /**
         * Gets the name of this type.
         * @return A string that can be read by humans.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the sign of this type.
         * @return A unique, small name
         */
        public String getSign() {
            return sign;
        }
        
        /**
         * Gets the behavior of figures of this type.
         * @return The unique behavior
         */
        public Behavior getBehavior() {
            return behavior;
        }
    }
    
    /** An {@link Icon} of size 16x16 */
	private Icon smallIcon;
	/** An {@link Icon} of size 48x48 */
	private Icon bigIcon;
	/** The board on which this figure stands */
	private Board board;
	/** The player to which this figure belongs */
	private Player player;
	/** The row in which this figure stands */
	private int row;
	/** The column in which this figure stands */
	private int column;
	/**
	 * The type of this figure, describing how this figure looks like, and how
	 * it can be used by a player.
	 */
	private Type type;
	/** Behavior describing how this figure can be moved by the player. */
	private Behavior behavior;
	
	/** whether this figure has already been moved or not */
	private boolean moved = false;
	/** whether this figure has been moved exactly once */
	private boolean justMoved = false;
	
	/**
	 * Creates a new figure.
	 * @param board the board on which the figure will stand
	 * @param player the owner of the figure
	 * @param type what kind of figure
	 * @param row the initial position (row-coordinate)
	 * @param column the initial position (column-coordinate)
	 */
	public Figure( Board board, Player player, Type type, int row, int column ){
	    this( board, player, type, row, column, true );
	}
	   
	
	/**
	 * Creates a new figure.
	 * @param board the board on which the figure will stand
	 * @param player the owner of the figure
	 * @param type what kind of figure
	 * @param row the initial position (row-coordinate)
	 * @param column the initial position (column-coordinate)
	 * @param icons whether the figure should create icons or not. Not creating
	 * icons is faster, however the figure can't be displayed without icons.
	 */
	private Figure( Board board, Player player, Type type, int row, int column, boolean icons ){
		this.board = board;
		this.player = player;
		this.type = type;
		
		if( icons ){
		    this.smallIcon = Utils.getChessIcon( type.getSign(), player, 16 );
		    this.bigIcon = Utils.getChessIcon( type.getSign(), player, 48 );
		}
		
		this.row = row;
		this.column = column;
		
		this.behavior = type.getBehavior();
	}

	/**
	 * Gets the name of this figure.
	 * @return the name, a string readable by a human
	 * @see Type#name()
	 */
	public String getName(){
		return type.getName();
	}
	
	/**
	 * Tells what kind of figure this figure is.
	 * @return the type
	 */
	public Type getType() {
        return type;
    }
	
	/**
	 * Gets the board on which this figure stands.
	 * @return the board
	 */
	public Board getBoard() {
        return board;
    }
	
	/**
	 * Gets the owner of this figure.
	 * @return the player which can move this figure
	 */
	public Player getPlayer(){
		return player;
	}
	
	/**
	 * Gets a small icon for this figure.
	 * @return an icon of size 16x16
	 */
	public Icon getSmallIcon(){
		return smallIcon;
	}
	
	/**
	 * Gets a big icon for this figure.
	 * @return an icon of size 48x48
	 */
	public Icon getBigIcon(){
		return bigIcon;
	}
	
	/**
	 * Gets the row in which this figure stands currently
	 * @return the row
	 */
	public int getRow() {
        return row;
    }
	
	/**
	 * Gets the column in which this figure stands currently.
	 * @return the column
	 */
	public int getColumn() {
        return column;
    }
	
	/**
	 * Tells whether this figure has been moved at least once or not.
	 * @return <code>true</code> if the figure has been moved
	 */
	public boolean isMoved() {
        return moved;
    }
	
	/**
	 * Tells whether this figure has been moved exactly once or not 
	 * @return <code>true</code> if this figure has been moved exactly once
	 * and only in the last stroke.
	 */
	public boolean isJustMoved() {
        return justMoved;
    }
	
	/**
	 * Cleans the just moved flag and sets it to <code>false</code>.
	 */
	public void cleanJustMoved(){
	    justMoved = false;
	}
	
	/**
	 * Changes the location of this figure. This might have influence on other
	 * figures on the {@link #getBoard() board}, according to the rules of
	 * chess. The behavior is unspecified for coordinates which are not
	 * reachable by the rules of the game.
	 * @param row the new row
	 * @param column the new column
	 */
	public void setLocation( int row, int column ){
	    behavior.moving( board, this, row, column );
	    justMoved = !moved;
	    moved = true;
	    this.row = row;
	    this.column = column;
	}
	
	/**
	 * Creates a copy of this figure using the new board
	 * @param board the new board
	 * @return the new figure
	 */
	public Figure copy( Board board ){
	    Figure figure = new Figure( board, player, type, row, column, false );
	    figure.moved = moved;
	    figure.justMoved = justMoved;
	    return figure;
	}
	
	/**
	 * Tells whether this figure can move to at least one other cell or not.
	 * @return <code>true</code> if the figure can be moved.
	 */
	public boolean moveable(){
	    final Single<Boolean> result = new Single<Boolean>( false );
	    reachable( new Board.CellVisitor(){
	        public boolean visit( int r, int c, Figure figure ) {
	            result.setA( true );
	            return false;
	        } 
	    });
	    return result.getA();
	}
	
	/**
	 * Visits all cells which can be reached within the next legal move.
	 * @param visitor the visitor
	 */
	public void reachable( Board.CellVisitor visitor ){
	    behavior.reachable( board, this, visitor );
	}
	
	/**
	 * Visits all cells which can be attacked within the next legal move.
	 * @param visitor the visitor
	 */
	public void attackable( Board.CellVisitor visitor ){
	    behavior.attackable( board, this, visitor );
	}
	
	@Override
	public String toString() {
	    return player + " " + getName();
	}
}
