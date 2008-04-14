package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockTitleEvent;
import bibliothek.gui.dock.title.DockTitle;

/**
 * These events are fired by a {@link DockTitleTab} to its
 * {@link DockTitle} to indicate that some properties, normally not
 * used by a <code>DockTitle</code>, have changed.
 * @author Benjamin Sigg
 *
 */
public class EclipseDockTitleEvent extends DockTitleEvent {
    /** whether the tab is focused */
    private boolean focused;
    /** the location of the tab */
    private int index;
    /** whether icons should be painted even when a tab is not selected */
    private boolean paintIconWhenInactive;
    
    /**
     * Creates a new event
     * @param station the station on which the tab lies, might be <code>null</code>
     * @param dockable the element for which the tab is shown
     * @param selected whether the tab is selected
     * @param focused whether the tab is focused
     * @param paintIconWhenInactive whether to paint icons when the tab is
     * not selected
     * @param index the location of the tab
     */
    public EclipseDockTitleEvent( DockStation station, Dockable dockable,
            boolean selected, boolean focused, boolean paintIconWhenInactive, int index ){
        
        super( station, dockable, focused );
        this.focused = focused;
        setPreferred( selected && !focused );
        this.index = index;
        this.paintIconWhenInactive = paintIconWhenInactive;
    }
    
    /**
     * Tells whether the tab which fired this event is selected.
     * @return the selection state
     */
    public boolean isSelected(){
        return isActive();
    }
    
    /**
     * Tells whether the tab which fired this event is focused.
     * @return whether the content of the tab receives input events
     */
    public boolean isFocused() {
        return focused;
    }
    
    /**
     * Tells whether icons should be painted when the tab is not selected.
     * @return <code>true</code> if icons should always be painted
     */
    public boolean isPaintIconWhenInactive() {
        return paintIconWhenInactive;
    }
    
    /**
     * Gets the location of the tab which fired this event.
     * @return the location in a row of tabs
     */
    public int getIndex() {
        return index;
    }
}
