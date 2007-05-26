package bibliothek.gui.dock.accept;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockAcceptance;

/**
 * A {@link DockAcceptance} which consists of other acceptances, and returns
 * only <code>true</code> if all children of this acceptance return <code>true</code>.
 * @author Benjamin Sigg
 *
 */
public class MultiDockAcceptance implements DockAcceptance {
    private List<DockAcceptance> acceptances = new ArrayList<DockAcceptance>();
    
    /**
     * Adds a {@link DockAcceptance} to the list of acceptances, which must be
     * asked, before an <code>accept</code>-method returns <code>true</code>.
     * @param acceptance the acceptance to ask
     */
    public void add( DockAcceptance acceptance ){
        if( acceptance == null )
            throw new IllegalArgumentException( "Acceptance must not be null" );
        acceptances.add( acceptance );
    }
    
    /**
     * Removes a {@link DockAcceptance} which was earlier {@link #add(DockAcceptance) added}
     * to this <code>MultiDockAcceptance</code>.
     * @param acceptance the acceptance to remove
     */
    public void remove( DockAcceptance acceptance ){
        acceptances.remove( acceptance );
    }
    
    public boolean accept( DockStation parent, Dockable child ){
        for( DockAcceptance acceptance : acceptances ){
            if( !acceptance.accept( parent, child ))
                return false;
        }
        
        return true;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ){
        for( DockAcceptance acceptance : acceptances ){
            if( !acceptance.accept( parent, child, next ))
                return false;
        }
        
        return true;
    }
}
