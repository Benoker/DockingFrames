package bibliothek.chess.view;

import javax.swing.JLabel;

import bibliothek.chess.model.Board;
import bibliothek.chess.model.ChessListener;
import bibliothek.chess.model.Figure;
import bibliothek.chess.model.Player;

public class StateLabel extends JLabel implements ChessListener{
    private Board board;
    
    public void setBoard( Board board ) {
        if( this.board != null )
            this.board.removeListener( this );
        
        this.board = board;
        board.addListener( this );
        update();
    }
    
    public void update(){
        Board.State state = board.state();
        Player player = board.getPlayer();
        
        switch( state ){
            case NOTHING:
                setText( player + ": your turn" );
                break;
            case STALLED:
                setText( player + ": no moves possible, game finished" );
                break;
            case CHECK:
                setText( player + ": your turn, your king is in danger" );
                break;
            case CHECKMATE:
                setText( player.opponent() + ": You have won" );
                break;
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
