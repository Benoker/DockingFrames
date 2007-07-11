package bibliothek.chess;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import bibliothek.chess.model.Board;
import bibliothek.chess.util.Utils;
import bibliothek.chess.view.ChessBoard;
import bibliothek.chess.view.ChessDockController;
import bibliothek.chess.view.HidingTheme;
import bibliothek.chess.view.PawnReplaceDialog;
import bibliothek.chess.view.StateLabel;
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
        frame.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
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
        


        JMenu menu = new JMenu( "Options" );
        menu.add( createNewGameItem() );
        menu.addSeparator();
        menu.add( createThemeItem() );
        menu.add( createDarkColorItem() );
        menu.add( createLightColorItem() );
        JMenuBar menubar = new JMenuBar();
        menubar.add( menu );
        frame.setJMenuBar( menubar );
        
        controller.add( chessBoard );
        
        content.getContentPane().setLayout( new GridBagLayout() );
        content.getContentPane().add( chessBoard, new GridBagConstraints( 0, 0, 2, 1, 100.0, 100.0, 
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        content.getContentPane().add( stateLabel, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1.0, 
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ));
        
        frame.setLocation( 20, 20 );
        frame.pack();
        frame.setVisible( true );
        
        if( monitor != null ){
            monitor.publish( this );
            monitor.running();
        }
    }
    
    private JMenuItem createNewGameItem(){
        JMenuItem newGame = new JMenuItem( "New game" );
        newGame.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                Board board = new Board();
                chessBoard.setBoard( board );
                stateLabel.setBoard( board );
            }
        });
        return newGame;
    }
    
    private JMenuItem createThemeItem(){
        final JCheckBoxMenuItem theme = new JCheckBoxMenuItem( "Show DockTitles" );
        theme.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                changeTheme( theme.isSelected() );
            }
        });
        
        return theme;
    }
    
    private JMenuItem createDarkColorItem(){
    	JMenuItem item = new JMenuItem( "Dark color" );
    	item.addActionListener( new ActionListener(){
    		public void actionPerformed( ActionEvent e ){
    			Color dark = JColorChooser.showDialog( frame, "Dark", chessBoard.getDark() );
    			if( dark != null )
    				chessBoard.setDark( dark );
    		}
    	});
    	return item;
    }
    
    private JMenuItem createLightColorItem(){
    	JMenuItem item = new JMenuItem( "Light color" );
    	item.addActionListener( new ActionListener(){
    		public void actionPerformed( ActionEvent e ){
    			Color light = JColorChooser.showDialog( frame, "Light", chessBoard.getLight() );
    			if( light != null )
    				chessBoard.setLight( light );
    		}
    	});
    	return item;
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
