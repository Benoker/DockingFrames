package bibliothek.gui.dock;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FSingleDockable;
import bibliothek.gui.dock.facile.menu.FSingleDockableListMenuPiece;

public class Test {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        JMenu menu = new JMenu( "Test" );
        JMenuBar bar = new JMenuBar();
        bar.add( menu );
        frame.setJMenuBar( bar );
        
        FControl control = new FControl( frame );
        frame.setContentPane( control.getCenter() );
        
        FSingleDockableListMenuPiece dockableList = new FSingleDockableListMenuPiece( menu, control );
        dockableList.setBottomSeparator( true );
        dockableList.setTopSeparator( false );
        
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
        
        frame.setBounds( 20, 20, 400, 300 );
        frame.setVisible( true );
    }
}
