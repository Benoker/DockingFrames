package bibliothek.help.view.text;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Element;

/**
 * A {@link Linker} that considers every {@link Element} to be a link
 * if its map of attributes contains an entry for the key {@link #LINK}.
 * @author Benjamin Sigg
 *
 */
public class HelpLinker extends Linker{
    /** The key for the map of attributes of an {@link Element} */
    public static final Object LINK = new Object();
    
    /**
     * Gives <code>pane</code> the ability to treat all elements
     * with an entry for {@link #LINK} as links.
     * @param pane the pane which gets a new ability
     */
    public static void connect( JTextPane pane ){
        new HelpLinker( pane );
    }
    
    /**
     * Creates a new {@link HelpLinker}
     * @param pane the pane which will fire {@link HyperlinkEvent}s
     */
    protected HelpLinker( JTextPane pane ) {
        super( pane );
    }
    
    @Override
    protected boolean isLink( Element element ) {
        return element.getAttributes().getAttribute( LINK ) != null;
    }
}
