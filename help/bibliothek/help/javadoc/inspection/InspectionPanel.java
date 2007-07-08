package bibliothek.help.javadoc.inspection;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;

import bibliothek.help.gui.text.HelpDocument;
import bibliothek.help.gui.text.HelpLinker;
import bibliothek.help.model.Entry;

public class InspectionPanel extends JTextPane{
    private InspectionTree tree;
    private Entry entry;
    
    public InspectionPanel( InspectionTree tree ){
        this.tree = tree;
        HelpLinker.connect( this );
        addHyperlinkListener( new HyperlinkListener(){
            public void hyperlinkUpdate( HyperlinkEvent e ) {
                if( e.getEventType() == EventType.ACTIVATED ){
                    InspectionPanel.this.tree.select( 
                            (String)e.getSourceElement().getAttributes().getAttribute( HelpLinker.LINK ));
                }
            }
        });
    }
    
    public void inspect( Entry entry ){
        if( this.entry != entry ){
            this.entry = entry;
        
            HelpDocument doc = new HelpDocument();
            Set<String> modes = new HashSet<String>();
            modes.add( "i" );
            
            if( entry == null ){
                doc.appendText( "< nothing >", modes );
            }
            else{
                doc.appendText( "Type: " + entry.getType() + "\n" + 
                        "Id: " + entry.getId() + "\n\n", modes );
                entry.toDocument( doc );
            }
            
            setDocument( doc );
        }
    }
}
