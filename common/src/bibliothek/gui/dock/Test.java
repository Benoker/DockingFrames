package bibliothek.gui.dock;

import javax.swing.JFrame;

import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FSingleDockable;

public class Test {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Frame" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        FControl control = new FControl( frame );
        frame.setContentPane( control.getCenter() );
        
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
