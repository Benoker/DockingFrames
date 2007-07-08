package bibliothek.help.gui.text;

import javax.swing.JTextPane;
import javax.swing.text.Element;

public class HelpLinker extends Linker{
    public static final Object LINK = new Object();
    
    public static void connect( JTextPane pane ){
        new HelpLinker( pane );
    }
    
    protected HelpLinker( JTextPane pane ) {
        super( pane );
    }
    
    @Override
    protected boolean isLink( Element element ) {
        return element.getAttributes().getAttribute( LINK ) != null;
    }
}
