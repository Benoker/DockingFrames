package bibliothek.chess;

import javax.swing.JFrame;

import bibliothek.chess.model.Pawn;
import bibliothek.chess.model.Player;
import bibliothek.chess.view.ChessBoard;
import bibliothek.chess.view.ChessFigure;
import bibliothek.gui.DockFrontend;

public class Test {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		DockFrontend frontend = new DockFrontend();
		ChessBoard board = new ChessBoard();
		frontend.addRoot( board, "board" );
		
		board.put( 1, 0, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 1, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 2, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 3, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 4, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 5, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 6, new ChessFigure( new Pawn( Player.WHITE )) );
		board.put( 1, 7, new ChessFigure( new Pawn( Player.WHITE )) );
		
		
		
		frame.add( board );
		frame.setLocation( 20, 20 );
		frame.pack();
		frame.setVisible( true );
	}
}
