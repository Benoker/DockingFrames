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

/**
 * A {@link Linker} observes a {@link JTextPane} and ensures that the 
 * <code>JTextPane</code> fires an {@link HyperlinkEvent} whenever
 * the user interacts with an element that {@link #isLink(Element) is a link}.<br>
 * This <code>Linker</code> also changes the appearance of the mouse cursor to
 * indicate which parts of the text are links.
 * @author Benjamin Sigg
 */
public abstract class Linker {
    /** the observed textpane */
    private JTextPane pane;
    /** the element that is currently under the mouse */
    private Element current;
    
    /**
     * Creates a new linker.
     * @param pane the textpane that will be observed.
     */
    public Linker( JTextPane pane ){
        this.pane = pane;
        
        Handler handler = new Handler();
        pane.addMouseListener( handler );
        pane.addMouseMotionListener( handler );
        pane.addHyperlinkListener( handler );
    }
    
    /**
     * Searches an element that contains <code>point</code>.
     * @param point a location on the textpane
     * @return the element or <code>null</code> if there is nothing
     * at <code>point</code>.
     */
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
    
    /**
     * Gets the leaf which contains <code>offset</code>.
     * @param offset an number of characters
     * @param document the document in which to search
     * @return the leaf containing the <code>offset</code>'th character
     */
    protected Element getElement( int offset, Document document ){
        Element element = document.getDefaultRootElement();
        
        while( !element.isLeaf() ){
            int index = element.getElementIndex( offset );
            element = element.getElement( index );
        }
        
        return element;
    }
    
    /**
     * Checks whether the point <code>location</code> lies inside
     * <code>element</code> when <code>element</code> is displayed by
     * <code>editor</code>.
     * @param editor an editor that shows some document
     * @param element an element of the document shown by <code>editor</code>
     * @param location a point measured relatively to <code>editor</code>
     * @return <code>true</code> if <code>location</code> is contained by <code>element</code>
     */
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
            
            Rectangle end = ui.modelToView( editor, element.getEndOffset(),
                                          Position.Bias.Backward);
            if (end != null) {
                bounds.add( end );
            }
            
            return bounds.contains( location.x, location.y );
            
        } catch (BadLocationException ble) {
            return false;
        }
    }
    
    /**
     * Tells whether <code>element</code> is a link or not. A link
     * will have an effect to the mouse: the mouse cursor is changed
     * to the "hand"-cursor. It's also possible that the user
     * clicks onto a link, then a {@link HyperlinkEvent} will be fired
     * by the {@link JTextPane} that was given to this <code>Linker</code>
     * through the constructor.
     * @param element a visible element like a text or an image
     * @return <code>true</code> if clicking onto the element will have an effect
     */
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
    
    /**
     * A listener added to a {@link JTextPane}, this listener changes the
     * mouse cursor and fires {@link HyperlinkEvent}s when entering, clicking
     * or exiting a {@link Linker#isLink(Element) link}.
     * @author Benjamin Sigg
     */
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
