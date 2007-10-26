package bibliothek.extension.gui.dock.theme.eclipse.rex.tab;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockTitleEvent;

public class RexDockTitleEvent extends DockTitleEvent {
    private boolean focused;
    private int index;
    private boolean paintIconWhenInactive;
    
    public RexDockTitleEvent( DockStation station, Dockable dockable,
            boolean selected, boolean focused, boolean paintIconWhenInactive, int index ){
        
        super( station, dockable, selected );
        this.focused = focused;
        setPreferred( selected && !focused );
        this.index = index;
        this.paintIconWhenInactive = paintIconWhenInactive;
    }
    
    public boolean isSelected(){
        return isActive();
    }
    
    public boolean isFocused() {
        return focused;
    }
    
    public boolean isPaintIconWhenInactive() {
        return paintIconWhenInactive;
    }
    
    public int getIndex() {
        return index;
    }
}
