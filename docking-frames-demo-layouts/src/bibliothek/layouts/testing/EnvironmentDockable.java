package bibliothek.layouts.testing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JRootPane;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MissingCDockableStrategy;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * A {@link CDockable} which internally contains a whole {@link CControl} with
 * all the wiring.
 * @author Benjamin Sigg
 */
public class EnvironmentDockable extends DefaultSingleCDockable {
    private CControl control;
    private JRootPane rootPane;
    
    public EnvironmentDockable(){
        super( "environment" );
        setTitleText( "Testing Environment" );
        
        setCloseable( false );
        setMinimizable( false );
        
        rootPane = new JRootPane();
        setLayout( new GridLayout( 1, 1 ) );
        add( rootPane );
        
        control = new CControl( true );
        control.setMissingStrategy( MissingCDockableStrategy.STORE );
        
        rootPane.getContentPane().setLayout( new BorderLayout() );
        rootPane.getContentPane().add( control.getContentArea(), BorderLayout.CENTER );
    }
    
    @Override
    public void setControlAccess( CControlAccess control ) {
        super.setControlAccess( control );
        if( control != null ){
            this.control.setRootWindow( control.getOwner().getRootWindow() );
        }
    }
    
    public CControl getEnvironmentControl(){
        return control;
    }
}
