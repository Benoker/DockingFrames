package bibliothek.help.javadoc.inspection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;

import bibliothek.help.model.Entry;
import bibliothek.help.view.text.HelpDocument;

public class InspectionPanel extends JTextPane{
    private Entry entry;
    
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
