package bibliothek.chess.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import bibliothek.chess.model.Board;
import bibliothek.chess.model.ChessListener;
import bibliothek.chess.model.Figure;
import bibliothek.chess.model.Player;

/**
 * A label displaying information about the game: who has to make the next move
 * and whether someone has won.
 * @author Benjamin Sigg
 *
 */
public class StateLabel extends JLabel implements ChessListener{
	/** the board for which the information are displayed */
    private Board board;
    
    /**
     * Sets the board for which this label shows information.
     * @param board the new board
     */
    public void setBoard( Board board ) {
        if( this.board != null )
            this.board.removeListener( this );
      
        this.board = board;
        board.addListener( this );
        setOpaque( true );
        update();
    }

    @Override
    public void updateUI(){
    	setFont( null );
    	super.updateUI();
    	setFont( getFont().deriveFont( getFont().getSize2D()*2 ) );
    }
    
    /**
     * Ensures that text and color of this label matches the state of the
     * game.
     */
    public void update(){
        Board.State state = board.state();
        Player player = board.getPlayer();
        
        switch( state ){
            case NOTHING:
                setText( "  " + player + ": your turn" );
                putColor( player );
                break;
            case STALLED:
            	setText( "  " + player + ": no moves possible, game finished" );
                putColor( player );
                break;
            case CHECK:
            	setText( "  " + player + ": your turn, your king is in danger" );
                putColor( player );
                break;
            case CHECKMATE:
            	setText( "  " + player.opponent() + ": You have won" );
                putColor( player.opponent() );
                break;
        }
    }
    
    /**
     * Changes the color of this label.
     * @param player the player whose color should be used
     */
    private void putColor( Player player ){
    	if( player == Player.WHITE ){
    		setBackground( Color.WHITE );
    		setForeground( Color.BLACK );
    		setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
    	}
    	else{
    		setBackground( Color.BLACK );
    		setForeground( Color.WHITE );
    		setBorder( BorderFactory.createLineBorder( Color.WHITE, 2 ) );
    	}
    }
    
    public void killed( int r, int c, Figure figure ) {
        // ignore
    }
    public void moved( int sr, int sc, int dr, int dc, Figure figure ) {
        // ignore
    }
    public void playerSwitched( Player player ) {
        update();
    }
}
