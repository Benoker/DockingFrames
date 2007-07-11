package bibliothek.chess;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import bibliothek.chess.model.Board;
import bibliothek.chess.util.Utils;
import bibliothek.chess.view.*;
import bibliothek.demonstration.Monitor;
import bibliothek.demonstration.util.ComponentCollector;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.themes.BasicTheme;

public class Core implements ComponentCollector{
    private JFrame frame;
    private PawnReplaceDialog pawn;
    
    private ChessBoard chessBoard;
    private StateLabel stateLabel;
    
    private ChessDockController controller;
    
    private Monitor monitor;
    
    public Core( Monitor monitor ){
        this.monitor = monitor;
    }
    
    public Collection<Component> listComponents() {
        List<Component> list = new ArrayList<Component>();
        list.add( frame );
        list.add( pawn );
        return list;
    }
    
    public void startup(){
        frame = new JFrame( "Chess - Demonstration of DockingFrames" );
        frame.setIconImage( Utils.APPLICATION );
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                shutdown();
            }
        });
        
        controller = new ChessDockController();
        changeTheme( false );
        controller.setSingleParentRemove( true );
        GlassedPane content = new GlassedPane();
        controller.getFocusObserver().addGlassPane( content );
        frame.setContentPane( content );
        
        pawn = new PawnReplaceDialog( frame );
        chessBoard = new ChessBoard( pawn );
        stateLabel = new StateLabel();
        
        Board board = new Board();
        chessBoard.setBoard( board );
        stateLabel.setBoard( board );
        
        JButton newGame = new JButton( "New game" );
        newGame.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                Board board = new Board();
                chessBoard.setBoard( board );
                stateLabel.setBoard( board );
            }
        });
        final JToggleButton theme = new JToggleButton( "Show DockTitles" );
        theme.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                changeTheme( theme.isSelected() );
            }
        });
        
        controller.add( chessBoard );
        JPanel panel = new JPanel( new GridLayout( 1, 2 ));
        panel.add( theme );
        panel.add( newGame );
        
        content.getContentPane().setLayout( new GridBagLayout() );
        content.getContentPane().add( chessBoard, new GridBagConstraints( 0, 0, 2, 1, 100.0, 100.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        content.getContentPane().add( stateLabel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        content.getContentPane().add( panel, new GridBagConstraints( 1, 1, 1, 1, 1.0, 1.0, 
                GridBagConstraints.LAST_LINE_END, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        
        frame.setLocation( 20, 20 );
        frame.pack();
        frame.setVisible( true );
        
        if( monitor != null ){
            monitor.publish( this );
            monitor.running();
        }
    }
    
    private void changeTheme( boolean show ){
        if( show ){
            controller.setTheme( new BasicTheme() );
        }
        else{
            controller.setTheme( new HidingTheme() );
        }
    }
    
    private void shutdown(){
        frame.setVisible( false );
        if( monitor == null )
            System.exit( 0 );
        else
            monitor.shutdown();
    }
}
