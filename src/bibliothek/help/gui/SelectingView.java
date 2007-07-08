package bibliothek.help.gui;

import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.gui.text.HelpDocument;
import bibliothek.help.gui.text.HelpLinker;
import bibliothek.help.model.Entry;

public class SelectingView extends DefaultDockable implements HyperlinkListener, Linking{
    private JTextPane pane;
    private Set<String> types = new HashSet<String>();
    private LinkManager manager;
    
    public SelectingView( LinkManager manager, String title, String...types ){
        this.manager = manager;
        manager.add( this );
        setTitleText( title );
        
        for( String type : types )
            this.types.add( type );
        
        pane = new JTextPane();
        pane.setEditable( false );
        HelpLinker.connect( pane );
        
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( pane ) );
        
        pane.addHyperlinkListener( this );
    }
    
    public void selected( List<Entry> list ){
        for( Entry entry : list ){
            if( types.contains( entry.getType() )){
                HelpDocument help = entry.toDocument( null );
                pane.setDocument( help );
                break;
            }
        }
    }
    
    public void hyperlinkUpdate( HyperlinkEvent e ) {
        if( e.getEventType() == EventType.ACTIVATED ){
            String link = (String)e.getSourceElement().getAttributes().getAttribute( HelpLinker.LINK );
            manager.select( link );
        }
    }
}
