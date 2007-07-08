package bibliothek.help.view;

import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.control.Undoable;
import bibliothek.help.model.Entry;
import bibliothek.help.view.text.HelpDocument;
import bibliothek.help.view.text.HelpLinker;

public class SelectingView extends DefaultDockable implements HyperlinkListener, Linking, Undoable{
    private JTextPane pane;
    private Set<String> types = new HashSet<String>();
    private LinkManager manager;
    private Entry entry;
    
    public SelectingView( LinkManager manager, String title, Icon icon, String...types ){
        this.manager = manager;
        manager.add( this );
        manager.getUR().register( this );
        setTitleText( title );
        setTitleIcon( icon );
        
        for( String type : types )
            this.types.add( type );
        
        this.types.add( "empty" );
        
        pane = new JTextPane();
        pane.setEditable( false );
        HelpLinker.connect( pane );
        
        setLayout( new GridLayout( 1, 1 ) );
        add( new JScrollPane( pane ) );
        
        pane.addHyperlinkListener( this );
    }
    
    public Entry getCurrent(){
    	return entry;
    }
    
    public void setCurrent( Entry entry ){
    	if( this.entry != entry ){
    		this.entry = entry;
    		HelpDocument help = entry.toDocument( null );
        	pane.setDocument( help );
    	}
    }
    
    public void selected( List<Entry> list ){
        for( Entry entry : list ){
            if( types.contains( entry.getType() )){
                setCurrent( entry );
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
