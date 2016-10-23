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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.help.control.LinkManager;
import bibliothek.help.control.Linking;
import bibliothek.help.control.Undoable;
import bibliothek.help.model.Entry;
import bibliothek.help.view.text.HelpDocument;
import bibliothek.help.view.text.HelpLinker;

/**
 * A {@link Dockable} that shows the content of one {@link Entry} translated
 * into a {@link HelpDocument}. This view implements {@link Linking} in
 * order to listen to a {@link LinkManager} and update its content automatically.
 * @author Benjamin Sigg
 */
public class SelectingView extends DefaultDockable implements HyperlinkListener, Linking, Undoable{
    /** the component showing the text of this view */
    private JTextPane pane;
    /** the types of <code>Entry</code>s shown by this view */
    private Set<String> types = new HashSet<String>();
    /** the set of all <code>Entry</code>s */
    private LinkManager manager;
    /** the current content */
    private Entry entry;
    
    /**
     * Creates a new view.
     * @param manager The set of all {@link Entry}s, this view will listen to
     * <code>manager</code> in order to update its content automatically. This
     * view will also use <code>manager</code> to change the content of other
     * views when the user clicks onto a link in this view.
     * @param title the title of this view
     * @param icon the icon of this view
     * @param types The types of {@link Entry}s that will be shown in this view.
     * When {@link #selected(List)} is called, then the first <code>Entry</code>
     * whose {@link Entry#getType() type} is contained in <code>types</code> will
     * be shown.
     */
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
