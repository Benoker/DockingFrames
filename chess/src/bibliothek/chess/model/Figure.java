package bibliothek.chess.model;

import javax.swing.Icon;

import bibliothek.chess.util.Utils;
import bibliothek.util.container.Single;

public final class Figure {
    public static enum Type{
        KING( "King", "k", new King() ), 
        QUEEN( "Queen", "q", new Queen() ), 
        BISHOP( "Bishop", "b", new Bishop() ),
        KNIGHT( "Knight", "n", new Knight() ),
        ROCK( "Rock", "r", new Rock() ),
        PAWN( "Pawn", "p", new Pawn() );
        
        private String name;
        private String sign;
        private Behavior behavior;
        
        private Type( String name, String sign, Behavior behavior ){
            this.name = name;
            this.sign = sign;
            this.behavior = behavior;
        }
        
        public String getName() {
            return name;
        }
        
        public String getSign() {
            return sign;
        }
        
        public Behavior getBehavior() {
            return behavior;
        }
    }
    
	private Icon smallIcon;
	private Icon bigIcon;
	private Board board;
	private Player player;
	private int row;
	private int column;
	private Type type;
	private Behavior behavior;
	
	private boolean moved = false;
	private boolean justMoved = false;
	
	public Figure( Board board, Player player, Type type, int row, int column ){
	    this( board, player, type, row, column, true );
	}
	   
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

	public String getName(){
		return type.getName();
	}
	
	public Type getType() {
        return type;
    }
	
	public Board getBoard() {
        return board;
    }
	
	public Player getPlayer(){
		return player;
	}
	
	public Icon getSmallIcon(){
		return smallIcon;
	}
	
	public Icon getBigIcon(){
		return bigIcon;
	}
	
	public int getRow() {
        return row;
    }
	
	public int getColumn() {
        return column;
    }
	
	public boolean isMoved() {
        return moved;
    }
	
	public boolean isJustMoved() {
        return justMoved;
    }
	
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
	
	public void reachable( Board.CellVisitor visitor ){
	    behavior.reachable( board, this, visitor );
	}
	
	public void attackable( Board.CellVisitor visitor ){
	    behavior.attackable( board, this, visitor );
	}
	
	@Override
	public String toString() {
	    return player + " " + getName();
	}
}
