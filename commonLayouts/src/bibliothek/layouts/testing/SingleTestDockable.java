package bibliothek.layouts.testing;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

import bibliothek.gui.dock.common.DefaultSingleCDockable;

public class SingleTestDockable extends DefaultSingleCDockable{
    public SingleTestDockable( String id, boolean fromBackupFactory ){
        super( id );
        setTitleText( "Single" );
        
        JLabel pane = new JLabel();
        pane.setText( "<html>This is a SingleCDockable.<br>" +
        		"It would show some vital information...<br>" +
        		" ...if this were are real application." +
        		"<ul>" +
        		    "<li>unique id: " + id + "</li>" +
        		    "<li>is a backup: " + fromBackupFactory + "</li>" +
        		"</ul> </html>" );
        
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( pane ) );
        
        setExternalizable( false );
        setCloseable( false );
    }
}
