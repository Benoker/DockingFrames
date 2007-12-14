package bibliothek.gui.dock;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FSingleDockable;
import bibliothek.gui.dock.facile.menu.FSingleDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.FThemeMenuPiece;

public class Test {
    public static void main( String[] args ) {
        final JFrame frame = new JFrame( "Frame" );
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        
        JMenu menu = new JMenu( "Test" );
        JMenuBar bar = new JMenuBar();
        bar.add( menu );
        frame.setJMenuBar( bar );
        
        final FControl control = new FControl( frame );
        frame.setContentPane( control.getCenter() );
        
        FSingleDockableListMenuPiece dockableList = new FSingleDockableListMenuPiece( menu, control );
        FThemeMenuPiece themeList = new FThemeMenuPiece( dockableList, control );
        
        FSingleDockable a = new FSingleDockable( "a" );
        FSingleDockable b = new FSingleDockable( "b" );
        FSingleDockable c = new FSingleDockable( "c" );
        
        control.add( a );
        control.add( b );
        control.add( c );

        a.setMinimizable( true );
        a.setMaximizable( true );
        a.setCloseable( true );
        a.setExternalizable( true );
        
        b.setMinimizable( true );
        
        c.setMaximizable( true );
        
        a.setCloseable( false );
        b.setCloseable( true );
        c.setCloseable( true );
        
        a.setTitleText( "alpha" );
        b.setTitleText( "beta" );
        c.setTitleText( "gamma" );
        
        a.setVisible( true );
        b.setVisible( true );
        c.setVisible( true );
        
        try {
            control.getResources().readFile( new File( "config.data" ) );
        } catch( IOException e ) {
            e.printStackTrace();
        }
        
        frame.addWindowListener( new WindowAdapter(){
            @Override
            public void windowClosing( WindowEvent e ) {
                frame.setVisible( false );
                try {
                    control.getResources().writeFile( new File( "config.data" ) );
                } 
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                finally{
                    System.exit( 0 );
                }
            }
        });
        
        frame.setBounds( 20, 20, 400, 300 );
        frame.setVisible( true );
    }
}
