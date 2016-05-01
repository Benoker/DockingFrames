package bibliothek.help.javadoc.inspection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;

import bibliothek.help.model.Entry;
import bibliothek.help.view.text.HelpDocument;

/**
 * A panel showing all contents of an {@link Entry}. That means
 * type, id, title, details and content of the <code>Entry</code>.
 * @author Benjamin Sigg
 *
 */
public class InspectionPanel extends JTextPane{
    /** the object whose content is shown on this panel */
    private Entry entry;
    
    /**
     * Shows the content of <code>entry</code>, deletes any information
     * that was shown before.
     * @param entry the element whose contents will be shown
     */
    public void inspect( Entry entry ){
        if( this.entry != entry ){
            this.entry = entry;
        
            HelpDocument doc = new HelpDocument();
            Set<String> modes = Collections.emptySet();
            
            if( entry == null ){
                doc.appendText( "< nothing >", modes );
            }
            else{
            	Set<String> bold = new HashSet<String>();
            	bold.add( "b" );
            	
            	doc.appendText( "Type: ", bold );
            	doc.appendText( entry.getType() + "\n", modes );
            	
            	doc.appendText( "Id: ", bold );
            	doc.appendText( entry.getId() + "\n", modes );
            	
            	doc.appendText( "Title: ", bold );
            	doc.appendText( entry.getTitle() + "\n", modes );
            	
            	doc.appendText( "Details:", bold );
            	for( String detail : entry.getDetails() )
            		doc.appendText( detail + " ", modes );
            	doc.appendText( "\n", modes );
            	
            	doc.appendText( "Content:\n ", bold );
            	doc.appendText( entry.getContent() + "\n", modes );
            }
            
            setDocument( doc );
        }
    }
}
