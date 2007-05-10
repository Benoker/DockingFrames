package bibliothek.gui.dock.util;

import bibliothek.gui.DockTheme;

/**
 * Often resources are divided into three kinds with different
 * priorities. <code>Priority</code> gives a name to these
 * kinds.
 */
public enum Priority{
    /** highest priority */
    CLIENT, 
    /** for resources set by a {@link DockTheme} */
    THEME,
    /** lowest priority, the default-values set by the stations */
    DEFAULT 
}