package bibliothek.help.view.text;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;

public abstract class Linker {
    private JTextPane pane;
    private Element current;
    
    public Linker( JTextPane pane ){
        this.pane = pane;
        
        Handler handler = new Handler();
        pane.addMouseListener( handler );
        pane.addMouseMotionListener( handler );
        pane.addHyperlinkListener( handler );
    }
    
    protected Element elementAt( Point point ){
        if( !pane.getVisibleRect().contains( point ) )
            return null;
        
        Document document = pane.getDocument();
        int offset = pane.viewToModel( point );
        Element element = getElement( offset, document );
        if( elementContainsLocation( pane, element, point ))
            return element;
        else
            return null;
    }
    
    protected Element getElement( int offset, Document document ){
        Element element = document.getDefaultRootElement();
        
        while( !element.isLeaf() ){
            int index = element.getElementIndex( offset );
            element = element.getElement( index );
        }
        
        return element;
    }
    
    protected boolean elementContainsLocation( JEditorPane editor, Element element, Point location ){
        try {
            TextUI ui = editor.getUI();
            Shape begin = ui.modelToView( editor, element.getStartOffset(), 
                    Position.Bias.Forward );
            if ( begin == null) {
                return false;
            }
            Rectangle bounds = (begin instanceof Rectangle) ? 
                        (Rectangle)begin : begin.getBounds();
            
            Shape end = ui.modelToView( editor, element.getEndOffset(),
                                          Position.Bias.Backward);
            if (end != null) {
                Rectangle endBounds = (end instanceof Rectangle) ? 
                        (Rectangle)end : end.getBounds();
                bounds.add( endBounds );
            }
            
            return bounds.contains( location.x, location.y );
            
        } catch (BadLocationException ble) {
            return false;
        }
    }
    
    protected abstract boolean isLink( Element element );
    
    /**
     * The currently selected element.
     * @return the element or <code>null</code>
     */
    public Element getCurrent() {
        return current;
    }
    
    /**
     * Sets the currently selected element.
     * @param current the element
     */
    public void setCurrent( Element current ) {
        if( this.current != current ){
            if( this.current != null ){
                pane.fireHyperlinkUpdate( new HyperlinkEvent( pane, EventType.EXITED, null, null, this.current ) );
            }
            
            this.current = current;
            
            if( this.current != null ){
                pane.fireHyperlinkUpdate( new HyperlinkEvent( pane, EventType.ENTERED, null, null, this.current ));
            }
        }
    }
    
    private class Handler extends MouseInputAdapter implements HyperlinkListener{
        @Override
        public void mouseReleased( MouseEvent e ) {
            if( SwingUtilities.isLeftMouseButton( e )){
                Element next = elementAt( e.getPoint() );
                if( next != null && next == current )
                    pane.fireHyperlinkUpdate( new HyperlinkEvent( pane, EventType.ACTIVATED, null, null, next ));
                else if( next != null && isLink( next ))
                    setCurrent( next );
                else
                    setCurrent( null );
            }
        }
        
        @Override
        public void mousePressed( MouseEvent e ) {
            if( SwingUtilities.isLeftMouseButton( e )){
                Element next = elementAt( e.getPoint() );
                if( next != null && isLink( next ))
                    setCurrent( next );
                else
                    setCurrent( null );
            }
        }
        
        @Override
        public void mouseMoved( MouseEvent e ) {
            Element next = elementAt( e.getPoint() );
            if( next != null && isLink( next ))
                setCurrent( next );
            else
                setCurrent( null );
        }
        
        public void hyperlinkUpdate( HyperlinkEvent e ) {
            if( e.getEventType() == EventType.ENTERED )
                pane.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
            else
                pane.setCursor( null );
        }
    }
}
