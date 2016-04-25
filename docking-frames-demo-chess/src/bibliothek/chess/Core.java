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
import bibliothek.gui.DockController;
import bibliothek.gui.dock.security.GlassedPane;
import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;

/**
 * The center of the application. Responsible for creating the graphical
 * user interface and connecting the GUI with the model.
 * @author Benjamin Sigg
 */
public class Core implements ComponentCollector{
	/** the frame on which the board is displayed */
    private JFrame frame;
    /** a dialog used when a pawn has to be replaced by a stronger figure */
    private PawnReplaceDialog pawn;
    
    /** the board shown on {@link #frame} */
    private ChessBoard chessBoard;
    /** a label displaying information about the state of the game */
    private StateLabel stateLabel;
    
    /** a controller used to manage the DockingFrames */
    private ChessDockController controller;
    /** the theme used by {@link #controller} to display the chess figures */
    private HidingTheme theme = new HidingTheme();
    
    /** used to distribute information about the state of this application */
    private Monitor monitor;
    
    /**
     * Creates a new Core.
     * @param monitor used to distribute information about the state of this
     * application, might be <code>null</code>
     */
    public Core( Monitor monitor ){
        this.monitor = monitor;
    }
    
    public Collection<Component> listComponents() {
        List<Component> list = new ArrayList<Component>();
        list.add( frame );
        list.add( pawn );
        return list;
    }
   
    /**
     * Creates and shows the graphical user interface
     */
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
        
        DockController.disableCoreWarning();
        controller = new ChessDockController();
        controller.setRootWindow( frame );
        theme.setShowTitles( false );
        controller.setTheme( theme );
        GlassedPane content = new GlassedPane();
        content.setController( controller );
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
    
    /**
     * Creates an item which allows to start a new game.
     * @return the item
     */
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
    
    /**
     * Creates an item which allows to put the titles of the 
     * {@link bibliothek.chess.view.ChessFigure ChessFigures} on and off.
     * @return the item
     */
    private JMenuItem createThemeItem(){
        final JCheckBoxMenuItem theme = new JCheckBoxMenuItem( "Show DockTitles" );
        theme.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                changeTheme( theme.isSelected() );
            }
        });
        
        return theme;
    }
    
    /**
     * Creates an item allowing to change the color of the black fields.
     * @return the item
     */
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
    
    /**
     * Creates an item allowing to change the color of the white fields.
     * @return the item
     */
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
    
    /**
     * Changes the {@link bibliothek.gui.DockTheme}.
     * @param show whether titles for {@link bibliothek.chess.view.ChessFigure}
     * should be shown or not.
     */
    private void changeTheme( boolean show ){
    	theme.setShowTitles( show );
    }
    
    /**
     * Stops this application.
     */
    private void shutdown(){
        frame.dispose();
        frame.getContentPane().removeAll();
        if( monitor == null )
            System.exit( 0 );
        else
            monitor.shutdown();
    }
}
