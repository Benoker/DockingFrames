package bibliothek.layouts.testing;

import java.awt.GridLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.dock.common.DefaultMultipleCDockable;

public class MultipleTestDockable extends DefaultMultipleCDockable{
    private JTextArea content;
    
    public MultipleTestDockable( MultipleTestFactory factory ){
        super( factory );
        setTitleText( "Multiple" );
        setCloseable( true );
        setExternalizable( false );
        
        content = new JTextArea();
        
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( content ));
    }
    
    public String getContent(){
        return content.getText();
    }
    
    public void setContent( String content ){
        if( content == null )
            content = "";
        this.content.setText( content );
    }
}
