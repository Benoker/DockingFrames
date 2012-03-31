package bibliothek.gui.dock.station.split;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.span.Span;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link SplitNode} that also stores some {@link Span}s.
 * @author Benjamin Sigg
 */
public abstract class SpanSplitNode extends VisibleSplitNode {
	/** the spans at the four sides of this root */
    private Span[] spans;
    
    /**
     * Creates a new node.
     * @param access access ot the {@link SplitDockStation}
     * @param id a unique identifier for this node
     */
    protected SpanSplitNode( SplitDockAccess access, long id ){
		super( access, id );
		createSpans();
	}

    /**
     * Creates or re-creates the {@link Span}s used by this {@link Leaf}. This method should not be called
     * by clients.
     */
    @FrameworkOnly
    public void createSpans(){
    	spans = getAccess().getSpanStrategy().createSpans( this );
    }
    
    /**
     * Gets the {@link Span}s that are currently used by this {@link Leaf}. This method should not be called
     * by clients.
     * @return the spans, can be <code>null</code>
     */
    @FrameworkOnly
    public Span[] getSpans(){
    	return spans;
    }
    
    /**
     * Called if a {@link Span} of this node changed its size
     */
    @FrameworkOnly
    public abstract void onSpanResize();
}
